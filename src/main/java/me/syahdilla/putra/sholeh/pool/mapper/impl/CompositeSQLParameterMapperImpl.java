package me.syahdilla.putra.sholeh.pool.mapper.impl;

import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class CompositeSQLParameterMapperImpl implements SQLParameterMapper<Object> {

  private final SQLParameterMapper<Object>[] mappers;

  public CompositeSQLParameterMapperImpl(SQLParameterMapper<Object>[] mappers) {
    this.mappers = mappers;
  }

  @Override
  public boolean isSupport(Object value) {
    return Arrays.stream(mappers).anyMatch(mapper -> mapper.isSupport(value));
  }

  @Override
  public void map(PreparedStatement statement, int index, Object value) throws SQLException {
    for (SQLParameterMapper<Object> mapper : mappers) {
      if (mapper.isSupport(value)) {
        mapper.map(statement, index, value);
        return;
      }
    }
  }

}
