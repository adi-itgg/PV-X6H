package me.syahdilla.putra.sholeh.repository;

import me.syahdilla.putra.sholeh.Future;

import java.util.Map;
import java.util.stream.Stream;

public interface MySQLRepository {

  static MySQLRepository pool(String jdbcUrl, String username, String password) {
    return new MySQLRepositoryImpl(jdbcUrl, username, password);
  }

  Future<Stream<Map<String, Object>>> preparedQuery(String sql, Object... args);

  Future<Void> insertItem(String id, String name, String type, Double buyPrice, Double sellPrice);

  Future<Stream<Map<String, Object>>> findAllItems();

  Future<Void> updateItemById(String id, String name, String type, Double buyPrice, Double sellPrice);

  Future<Void> deleteItemById(String id);
}
