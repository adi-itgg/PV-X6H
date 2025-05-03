package me.syahdilla.putra.sholeh.repository;

import me.syahdilla.putra.sholeh.Future;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface MySQLRepository {

  static MySQLRepository pool(String jdbcUrl, String username, String password, Consumer<Throwable> errorHandler) {
    return new MySQLRepositoryImpl(jdbcUrl, username, password, errorHandler);
  }

  Future<Stream<Map<String, Object>>> preparedQuery(String sql, Object... args);

  Future<Void> insertItem(String id, String name, String type, Double buyPrice, Double sellPrice);

  Future<Stream<Map<String, Object>>> findAllItems();

  Future<Void> updateItemById(String id, String name, String type, Double buyPrice, Double sellPrice);

  Future<Void> deleteItemById(String id);

  Future<Stream<Map<String, Object>>> findAllCustomers();

  Future<Void> insertCustomer(String id, String name, Character gender, String phone, String address);

  Future<Void> updateCustomerById(String id, String name, Character gender, String phone, String address);

  Future<Void> deleteCustomerById(String id);

  Future<Stream<Map<String, Object>>> findCustomerByKeyword(String keyword);

  Future<Stream<Map<String, Object>>> findAllCashiers();

  Future<Stream<Map<String, Object>>> findChashierByKeyword(String keyword);

  Future<Void> insertCashier(String id, String name, Character gender, String phone, String religion, String address, String password);

  Future<Void> updateCashierById(String id, String name, Character gender, String phone, String religion, String address, String password);

  Future<Void> deleteCashierById(String id);
}
