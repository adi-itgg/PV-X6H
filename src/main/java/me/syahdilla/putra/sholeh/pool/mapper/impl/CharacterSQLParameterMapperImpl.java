package me.syahdilla.putra.sholeh.pool.mapper.impl;

import me.syahdilla.putra.sholeh.pool.mapper.SQLParameterMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CharacterSQLParameterMapperImpl implements SQLParameterMapper<Character> {

  @Override
  public boolean isSupport(Object value) {
    return value instanceof Character;
  }

  @Override
  public void map(PreparedStatement statement, int index, Character value) throws SQLException {
    statement.setString(index, String.valueOf(value));
  }

}
