package me.syahdilla.putra.sholeh.repository.base;

import me.syahdilla.putra.sholeh.Future;

import java.util.Map;
import java.util.stream.Stream;

public interface BaseMasterRepository {

  Future<Stream<Map<String, Object>>> findAll(String keyword);

  Future<Void> save(Object... params);

  Future<Void> update(Object id, Object... params);

  Future<Void> delete(Object id);

}
