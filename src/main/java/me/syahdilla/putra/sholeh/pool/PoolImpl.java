package me.syahdilla.putra.sholeh.pool;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.model.ConnectionOptions;
import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;
import me.syahdilla.putra.sholeh.pool.mapper.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class PoolImpl implements Pool {

  private static final Logger log = LoggerFactory.getLogger(Pool.class);

  private final ExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
  private final ConnectionOptions options;
  private final Consumer<Throwable> errorHandler;
  private Connection pool;

  private final SQLParameterMapper sqlParameterMapper;

  PoolImpl(ConnectionOptions options, Consumer<Throwable> errorHandler) {
    this.options = options;
    this.errorHandler = errorHandler;

    // check connection first
    connect();

    List<SQLParameterMapper> sqlParameterMappers = new ArrayList<>();
    sqlParameterMappers.add(new StringSQLParameterMapperImpl());
    sqlParameterMappers.add(new BigDecimalSQLParameterMapperImpl());
    sqlParameterMappers.add(new InstantSQLParameterMapperImpl());
    sqlParameterMappers.add(new CharacterSQLParameterMapperImpl());

    this.sqlParameterMapper = new CompositeSQLParameterMapperImpl(sqlParameterMappers.toArray(new SQLParameterMapper[0]));
  }

  private void connect() {
    try {
      if (this.pool != null) {
        this.pool.close();
      }
      this.pool = DriverManager.getConnection(options.getJdbcUrl(), options.getUsername(), options.getPassword());
      final boolean isValid = this.pool.isValid(15); // timeout 15 seconds
      if (!isValid) {
        throw new IllegalStateException("Connection is not valid");
      }
    } catch (Throwable e) {
      log.error("Error connecting to database", e);
      errorHandler.accept(e);
      throw new IllegalStateException("Error connecting to database: " + e.getMessage());
    }
  }

  private Stream<Map<String, Object>> resultToStream(ResultSet resultSet) {
    final Iterator<Map<String, Object>> iterator = new Iterator<>() {

      @Override
      public boolean hasNext() {
        try {
          return resultSet.next();
        } catch (SQLException e) {
          return false;
        }
      }

      @Override
      public Map<String, Object> next() {
        final Map<String, Object> map = new LinkedHashMap<>();
        try {
          for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            map.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
          }
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
        return map;
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
  }

  private Stream<Map<String, Object>> sendPreparedQuery(String sql, Object... args) {
    try {
      final PreparedStatement statement = this.pool.prepareStatement(sql);
      int i = 0;
      for (Object arg : args) {
        int currentIndex = ++i;
        if (sqlParameterMapper.isSupport(arg)) {
          sqlParameterMapper.map(statement, currentIndex, arg);
          continue;
        }
        statement.setObject(currentIndex, arg);
      }

      boolean isSelect = sql.trim().replace("\n", "").toLowerCase().startsWith("select");

      if (isSelect) {
        final ResultSet resultSet = statement.executeQuery();
        return resultToStream(resultSet);
      }
      statement.execute();
      return Stream.empty();
    } catch (SQLRecoverableException e) { // connection lost or something like that
      connect(); // so we should reconnect
      return sendPreparedQuery(sql, args); // retry the query
    } catch (Throwable e) {
      log.error("Error executing query", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public Future<Stream<Map<String, Object>>> query(String sql, Object... args) {
    Future<Stream<Map<String, Object>>> future = new Future<>();
    executorService.submit(() -> {
      try {
        Stream<Map<String, Object>> result = sendPreparedQuery(sql, args);
        future.complete(result);
      } catch (Throwable e) {
        Throwable ex = e.getCause();
        log.error("Error executing query", ex);
        future.fail(ex);
      }
    });
    return future;
  }


}
