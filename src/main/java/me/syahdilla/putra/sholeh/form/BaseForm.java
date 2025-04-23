package me.syahdilla.putra.sholeh.form;

import me.syahdilla.putra.sholeh.ActionCommand;
import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseForm extends MouseAdapter implements ActionListener, Consumer<Field> {

  protected final Logger log = LoggerFactory.getLogger(getClass().asSubclass(getClass()));
  protected final MySQLRepository mySQLRepository;

  public BaseForm(MySQLRepository mySQLRepository) {
    this.mySQLRepository = mySQLRepository;
  }

  protected void initialize() {
    final DefaultTableModel model = new DefaultTableModel(getHeaderColumns(), 0);
    getTableItems().setModel(model);

    updateTable();

    getTableItems().addMouseListener(this);

    Arrays.stream(this.getClass().asSubclass(getClass()).getDeclaredFields())
      .filter(f -> f.getType().isAssignableFrom(JButton.class))
      .forEach(this);
  }

  @Override
  public void accept(Field field) {
    if (field.trySetAccessible()) {
      try {
        final JButton btn = (JButton) field.get(this);
        btn.addActionListener(this);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    onClick(e, ActionCommand.valueOf(e.getActionCommand()))
      .onSuccess(result -> updateTable())
      .onFailure(ex -> {
        log.error("Error executing action command {}", e.getActionCommand(), ex);
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      });
  }

  public abstract JPanel getMainPanel();

  protected abstract String[] getHeaderColumns();

  protected abstract void updateTable();

  protected abstract Object[] onAddRow(Map<String, Object> item);

  protected abstract JTable getTableItems();

  protected abstract Future<Void> onClick(ActionEvent e, ActionCommand actionCommand);

}
