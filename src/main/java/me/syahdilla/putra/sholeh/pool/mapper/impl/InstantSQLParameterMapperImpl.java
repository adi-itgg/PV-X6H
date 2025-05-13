package me.syahdilla.putra.sholeh.pool.mapper.impl;

import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class InstantSQLParameterMapperImpl implements SQLParameterMapper<Instant> {

  @Override
  public boolean isSupport(Object value) {
    return value instanceof Instant;
  }

  @Override
  public void map(PreparedStatement statement, int index, Instant value) throws SQLException {
    statement.setTimestamp(index, Timestamp.from(value));
  }

}
