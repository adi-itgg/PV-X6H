package me.syahdilla.putra.sholeh.form;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.model.ActionCommand;
import me.syahdilla.putra.sholeh.repository.base.BaseMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public abstract class AbstractBaseMasterForm<R extends BaseMasterRepository> extends MouseAdapter implements ActionListener, Consumer<Field>, BaseForm {

  private static final java.util.Timer timer = new Timer(true);

  protected final Logger log = LoggerFactory.getLogger(getClass().asSubclass(getClass()));
  protected final R repository;

  protected DefaultTableModel model;

  private TimerTask typingTask;
  private boolean isInitialized;

  public AbstractBaseMasterForm(R repository) {
    this.repository = repository;
  }

  protected void initialize() {
    this.model = new DefaultTableModel(getHeaderColumns(), 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    getTable().setModel(model);
    // center align
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    centerRenderer.setVerticalAlignment(SwingConstants.CENTER);
    for (int i = 0; i < getTable().getColumnCount(); i++) {
      getTable().getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    fetchData();

    getTable().addMouseListener(this);

    Arrays.stream(this.getClass().asSubclass(getClass()).getDeclaredFields())
      .filter(f -> f.getType().isAssignableFrom(JButton.class))
      .forEach(this);

    getSearchEditText().addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (typingTask != null) {
          typingTask.cancel();
        }
        typingTask = new TimerTask() {
          @Override
          public void run() {
            fetchData();
          }
        };
        timer.schedule(typingTask, 800L);
      }
    });
  }

  @Override
  public void accept(Field field) {
    if (!field.trySetAccessible()) {
      return;
    }
    try {
      final JButton btn = (JButton) field.get(this);
      btn.addActionListener(this);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    onClick(e, ActionCommand.valueOf(e.getActionCommand()))
      .onSuccess(result -> fetchData())
      .onFailure(ex -> {
        log.error("Error executing action command {}", e.getActionCommand(), ex);
        showErrorDialog("Error executing action command " + e.getActionCommand() + " - " + ex.getMessage());
      });
  }

  public void fetchData() {
    final String keyword = getSearchEditText() == null ? "" : getSearchEditText().getText();
    repository.findAll(keyword).onSuccess(rows -> {
      model.setRowCount(0); // clear table
      rows.forEach(row -> {
        Object[] values = row.values().toArray();
        model.addRow(values);
      });
    }).onFailure(e -> {
      log.error("Failed to fetch data", e);
      showErrorDialog("Failed to fetch data - " + e.getMessage());
    });
  }

  @Override
  public JPanel getMainPanel() {
    if (!isInitialized) {
      isInitialized = true;
      initialize();
    }
    return mainPanel();
  }

  protected abstract JPanel mainPanel();

  protected abstract String[] getHeaderColumns();

  protected abstract JTable getTable();

  protected abstract JTextField getSearchEditText();

  protected abstract Future<Void> onClick(ActionEvent e, ActionCommand actionCommand);

}
