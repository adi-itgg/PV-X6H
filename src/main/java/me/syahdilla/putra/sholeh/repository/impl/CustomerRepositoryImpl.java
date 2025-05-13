package me.syahdilla.putra.sholeh.repository.impl;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.pool.Pool;
import me.syahdilla.putra.sholeh.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomerRepositoryImpl implements CustomerRepository {

  private final Pool pool;

  public CustomerRepositoryImpl(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Future<Stream<Map<String, Object>>> findAll(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return pool.query("""
        SELECT * FROM m_customer
        """);
    }
    keyword = "%" + keyword.toLowerCase() + "%";
    return pool.query("""
      SELECT * FROM m_customer
      WHERE name LIKE ?
      """, keyword);
  }

  @Override
  public Future<Void> save(Object... params) {
    return pool.query("""
      INSERT INTO m_customer (id, name, gender, phone, address)
      VALUES (?, ?, ?, ?, ?)
      """, params)
      .mapEmpty();
  }

  @Override
  public Future<Void> update(Object id, Object... params) {
    List<Object> paramsList = new ArrayList<>(Arrays.asList(params));
    paramsList.add(id);
    return pool.query("""
      UPDATE m_customer
        SET name = ?, gender = ?, phone = ?, address = ?,
            updated_at = current_timestamp()
      WHERE id = ?
      """, paramsList.toArray())
      .mapEmpty();
  }

  @Override
  public Future<Void> delete(Object id) {
    return pool.query("DELETE FROM m_customer WHERE id = ?", id)
      .mapEmpty();
  }

}
