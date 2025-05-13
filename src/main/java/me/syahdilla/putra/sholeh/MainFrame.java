package me.syahdilla.putra.sholeh;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import me.syahdilla.putra.sholeh.form.BaseForm;
import me.syahdilla.putra.sholeh.form.main.MainForm;
import me.syahdilla.putra.sholeh.model.ConnectionOptions;
import me.syahdilla.putra.sholeh.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

public class MainFrame extends JFrame {

  private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

  public static void main(String[] args) throws Throwable {
    UIManager.setLookAndFeel(new FlatLightLaf());
    SwingUtilities.invokeLater(MainFrame::new);
  }

  public MainFrame() {
    final Properties properties = loadProperties();
    final Pool pool = initializeDatabase(properties);
    runMigrations(pool);
    initializeUI(pool);
  }

  private void runMigrations(Pool pool) {
    log.info("Running migrations");

    try (InputStream is = getClass().getClassLoader().getResourceAsStream("db-migrations/initial_structure.sql");
         BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {

      final StringBuilder sql = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sql.append(line).append("\n");
        if (line.contains(";")) {
          pool.query(sql.toString()).toCompletionStage().toCompletableFuture().join();
          sql.setLength(0);
        }
      }
    } catch (Throwable e) {
      log.error("Error migrations", e);
    }

    log.info("Migrations finished");
  }

  private Properties loadProperties() {
    final String configFile = "config.properties";
    final Properties properties = new Properties();

    try (final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile)) {
      properties.load(stream);
    } catch (IOException e) {
      log.error("Error loading properties", e);
      throw new IllegalStateException("Error loading properties: " + e.getMessage());
    }
    return properties;
  }

  private Pool initializeDatabase(Properties properties) {
    final String jdbcUrl = properties.getProperty("database.jdbc.url");
    final String username = properties.getProperty("database.jdbc.username");
    final String password = properties.getProperty("database.jdbc.password");

    final ConnectionOptions options = new ConnectionOptions.Builder()
      .jdbcUrl(jdbcUrl)
      .username(username)
      .password(password)
      .build();

    return Pool.pool(options, error -> {
      JOptionPane.showMessageDialog(this, error.getMessage(), "Error - Database Connection", JOptionPane.ERROR_MESSAGE);
      JOptionPane.showMessageDialog(this, "Aplikasi tidak akan berjalan semestinya karena koneksi ke database gagal", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    });
  }

  private void initializeUI(Pool pool) {
    // setup UI
    log.info("Starting UI application");
    setTitle("Aplikasi Kasir");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(300, 340);
    setLocationRelativeTo(null);
    setContainer(new MainForm(this, pool));
    setVisible(true);
    log.info("UI application started");
  }

  public void setContainer(BaseForm form) {
    setContainer(form.getMainPanel());
  }

  public void setContainer(JComponent component) {
    setLocationRelativeTo(null);
    FlatAnimatedLafChange.showSnapshot();
    setContentPane(component);
    component.applyComponentOrientation(getComponentOrientation());
    SwingUtilities.updateComponentTreeUI(component);
    FlatAnimatedLafChange.hideSnapshotWithAnimation();
  }

}
