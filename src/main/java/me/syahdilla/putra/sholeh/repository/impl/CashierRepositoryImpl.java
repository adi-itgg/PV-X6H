package me.syahdilla.putra.sholeh.repository.impl;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.pool.Pool;
import me.syahdilla.putra.sholeh.repository.CashierRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CashierRepositoryImpl implements CashierRepository {

  private final Pool pool;

  public CashierRepositoryImpl(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Future<Stream<Map<String, Object>>> findAll(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return pool.query("""
        SELECT id, name, gender, phone,
               religion, address, created_at,
               updated_at
        FROM m_cashier
        """);
    }
    keyword = "%" + keyword.toLowerCase() + "%";
    return pool.query("""
      SELECT id, name, gender, phone,
             religion, address, created_at,
             updated_at
      FROM m_cashier
      WHERE lower(name) LIKE ? OR phone LIKE ?
      """, keyword, keyword);
  }

  @Override
  public Future<Void> save(Object... params) {
    return pool.query("""
        INSERT INTO m_cashier (id, name, gender, phone, religion, address, password)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """, params)
      .mapEmpty();
  }

  @Override
  public Future<Void> update(Object id, Object... params) {
    List<Object> paramsList = new ArrayList<>(Arrays.asList(params));
    paramsList.add(id);
    return pool.query("""
        UPDATE m_cashier
          SET name = ?, gender = ?, phone = ?, religion = ?, address = ?, password = ?,
              updated_at = current_timestamp()
        WHERE id = ?
        """, paramsList.toArray())
      .mapEmpty();
  }

  @Override
  public Future<Void> delete(Object id) {
    return pool.query("DELETE FROM m_cashier WHERE id = ?", id)
      .mapEmpty();
  }

}
