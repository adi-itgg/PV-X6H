package me.syahdilla.putra.sholeh.repository.impl;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.pool.Pool;
import me.syahdilla.putra.sholeh.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ItemRepositoryImpl implements ItemRepository {

  private final Pool pool;

  public ItemRepositoryImpl(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Future<Stream<Map<String, Object>>> findAll(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return pool.query("""
        SELECT * FROM m_item
        """);
    }
    keyword = "%" + keyword.toLowerCase() + "%";
    return pool.query("""
      SELECT * FROM m_item
      WHERE lower(name) LIKE ? OR type LIKE ?
      """, keyword, keyword);
  }

  @Override
  public Future<Void> save(Object... params) {
    return pool.query("""
      INSERT INTO m_item (id, name, type, buy_price, sell_price)
      VALUES (?, ?, ?, ?, ?)
      """, params)
      .mapEmpty();
  }

  @Override
  public Future<Void> update(Object id, Object... params) {
    List<Object> paramsList = new ArrayList<>(Arrays.asList(params));
    paramsList.add(id);
    return pool.query("""
      UPDATE m_item
        SET name = ?, type = ?, buy_price = ?, sell_price = ?,
            updated_at = current_timestamp()
      WHERE id = ?
      """, paramsList.toArray())
      .mapEmpty();
  }

  @Override
  public Future<Void> delete(Object id) {
    return pool.query("DELETE FROM m_item WHERE id = ?", id)
      .mapEmpty();
  }
}
