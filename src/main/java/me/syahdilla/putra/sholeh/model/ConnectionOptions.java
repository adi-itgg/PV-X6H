package me.syahdilla.putra.sholeh.model;

public class ConnectionOptions {

  private final String jdbcUrl;
  private final String username;
  private final String password;

  ConnectionOptions(String jdbcUrl, String username, String password) {
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public static class Builder {

    private String jdbcUrl;
    private String username;
    private String password;

    public Builder jdbcUrl(String jdbcUrl) {
      this.jdbcUrl = jdbcUrl;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public ConnectionOptions build() {
      if (jdbcUrl == null || username == null || password == null) {
        throw new IllegalArgumentException("JDBC URL, username, dan password harus diisi");
      }
      return new ConnectionOptions(jdbcUrl, username, password);
    }
  }

}
