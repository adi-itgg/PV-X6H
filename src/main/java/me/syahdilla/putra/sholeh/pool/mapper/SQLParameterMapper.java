package me.syahdilla.putra.sholeh.pool.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SQLParameterMapper<T> {

  boolean isSupport(Object value);

  void map(PreparedStatement statement, int index, T value) throws SQLException;

}
