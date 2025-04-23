package me.syahdilla.putra.sholeh.repository;

import me.syahdilla.putra.sholeh.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class MySQLRepositoryImpl implements MySQLRepository {

  private static final Logger log = LoggerFactory.getLogger(MySQLRepository.class);

  private final ExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
  private final Connection pool;

  MySQLRepositoryImpl(String jdbcUrl, String username, String password) {
    try {
      this.pool = DriverManager.getConnection(jdbcUrl, username, password);
//      List<Map<String, Object>> result = preparedQuery("SELECT current_timestamp()").toCompletionStage().toCompletableFuture().join().toList();
      preparedQuery("SELECT current_timestamp()")
        .onFailure(e -> log.error("Error connecting to database", e))
        .onSuccess(result -> log.info("Connected to database: {} - {}", jdbcUrl, result.toList().getFirst().values().stream().findFirst().orElseThrow()));
    } catch (Throwable e) {
      log.error("Error connecting to database", e);
      throw new IllegalStateException(e);
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
        if (arg instanceof String s) {
          statement.setString(++i, s);
          continue;
        }
        if (arg instanceof Number number) {
          statement.setBigDecimal(++i, BigDecimal.valueOf(number.doubleValue()));
          continue;
        }
        if (arg instanceof Instant instant) {
          statement.setTimestamp(++i, Timestamp.from(instant));
          continue;
        }
        statement.setObject(++i, arg);
      }

      boolean isSelect = sql.trim().replace("\n", "").toLowerCase().startsWith("select");

      if (isSelect) {
        final ResultSet resultSet = statement.executeQuery();
        return resultToStream(resultSet);
      }
      statement.execute();
      return Stream.empty();
    } catch (Throwable e) {
      log.error("Error executing query", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public Future<Stream<Map<String, Object>>> preparedQuery(String sql, Object... args) {
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

  @Override
  public Future<Void> insertItem(String id, String name, String type, Double buyPrice, Double sellPrice) {
    return preparedQuery("""
        INSERT INTO m_item (id, name, type, buy_price, sell_price)
        VALUES (?, ?, ?, ?, ?)
        """, id, name, type, buyPrice, sellPrice)
      .mapEmpty();
  }

  @Override
  public Future<Stream<Map<String, Object>>> findAllItems() {
    return preparedQuery("SELECT * FROM m_item");
  }

  @Override
  public Future<Void> updateItemById(String id, String name, String type, Double buyPrice, Double sellPrice) {
    return preparedQuery("""
        UPDATE m_item
          SET name = ?, type = ?, buy_price = ?, sell_price = ?,
              updated_at = current_timestamp()
        WHERE id = ?
        """, name, type, buyPrice, sellPrice, id)
      .mapEmpty();
  }

  @Override
  public Future<Void> deleteItemById(String id) {
    return preparedQuery("DELETE FROM m_item WHERE id = ?", id).mapEmpty();
  }


}
