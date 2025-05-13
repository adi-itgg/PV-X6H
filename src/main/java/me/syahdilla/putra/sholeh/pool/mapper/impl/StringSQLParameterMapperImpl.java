package me.syahdilla.putra.sholeh.pool.mapper.impl;

import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringSQLParameterMapperImpl implements SQLParameterMapper<String> {

  @Override
  public boolean isSupport(Object value) {
    return value instanceof String;
  }

  @Override
  public void map(PreparedStatement statement, int index, String value) throws SQLException {
    statement.setString(index, value);
  }


}
