package me.syahdilla.putra.sholeh.pool.mapper.impl;

import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BigDecimalSQLParameterMapperImpl implements SQLParameterMapper<Number> {

  @Override
  public boolean isSupport(Object value) {
    return value instanceof Number;
  }

  @Override
  public void map(PreparedStatement statement, int index, Number value) throws SQLException {
    statement.setBigDecimal(index, BigDecimal.valueOf(value.doubleValue()));
  }

}
