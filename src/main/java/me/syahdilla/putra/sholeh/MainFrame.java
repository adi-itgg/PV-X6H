package me.syahdilla.putra.sholeh;

import com.formdev.flatlaf.FlatLightLaf;
import me.syahdilla.putra.sholeh.form.cashier.CashierForm;
import me.syahdilla.putra.sholeh.form.customer.CustomerForm;
import me.syahdilla.putra.sholeh.form.item.ItemForm;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainFrame extends JFrame {

  private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

  public static void main(String[] args) throws Throwable {
    UIManager.setLookAndFeel(new FlatLightLaf());
    SwingUtilities.invokeLater(MainFrame::new);
  }

  public MainFrame() {
    final Properties properties = loadProperties();
    final MySQLRepository mySQLRepository = initializeDatabase(properties);
    initializeUI(mySQLRepository);
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

  private MySQLRepository initializeDatabase(Properties properties) {
    final String jdbcUrl = properties.getProperty("database.jdbc.url");
    final String username = properties.getProperty("database.jdbc.username");
    final String password = properties.getProperty("database.jdbc.password");

    return MySQLRepository.pool(jdbcUrl, username, password);
  }

  private void initializeUI(MySQLRepository mySQLRepository) {
    // setup UI
    log.info("Starting UI application");
    setTitle("Aplikasi Kasir");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 400);
    setLocationRelativeTo(null);
//    setLayout(new MigLayout("al center center"));
//    add(new ItemForm(mySQLRepository).getMainPanel(), BorderLayout.CENTER);
//    add(new CustomerForm(mySQLRepository).getMainPanel(), BorderLayout.CENTER);
    add(new CashierForm(mySQLRepository).getMainPanel(), BorderLayout.CENTER);
    setVisible(true);
    log.info("UI application started");
  }

}
