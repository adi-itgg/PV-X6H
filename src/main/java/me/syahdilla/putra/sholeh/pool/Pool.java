package me.syahdilla.putra.sholeh.pool;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.model.ConnectionOptions;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Pool {

  static Pool pool(ConnectionOptions options, Consumer<Throwable> errorHandler) {
    return new PoolImpl(options, errorHandler);
  }

  Future<Stream<Map<String, Object>>> query(String sql, Object... args);

}
