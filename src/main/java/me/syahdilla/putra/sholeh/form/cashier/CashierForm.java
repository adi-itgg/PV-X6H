package me.syahdilla.putra.sholeh.form.cashier;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.syahdilla.putra.sholeh.ActionCommand;
import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.form.BaseForm;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CashierForm extends BaseForm {

  private static final java.util.Timer timer = new Timer(true);

  private JPanel mp;
  private JTextField idTxt;
  private JTextField nameTxt;
  private JTextField phoneTxt;
  private JTextArea addressTxt;
  private JButton simpanButton;
  private JButton ubahButton;
  private JButton hapusButton;
  private JButton batalButton;
  private JButton keluarButton;
  private JTable tableItems;
  private JTextField searchTxt;
  private JPasswordField passwordTxt;
  private JComboBox<String> religionBox;
  private JRadioButton lRadioButton;
  private JRadioButton pRadioButton;

  private TimerTask typingTask;

  public CashierForm(MySQLRepository mySQLRepository) {
    super(mySQLRepository);
    initialize();

    religionBox.addItem("Islam");
    religionBox.addItem("Kristen");
    religionBox.addItem("Katolik");
    religionBox.addItem("Hindu");
    religionBox.addItem("Budha");
    religionBox.addItem("Konghucu");

    lRadioButton.addChangeListener(e -> {
      if (lRadioButton.isSelected()) {
        pRadioButton.setSelected(false);
      }
    });

    pRadioButton.addChangeListener(e -> {
      if (pRadioButton.isSelected()) {
        lRadioButton.setSelected(false);
      }
    });

    searchTxt.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (typingTask != null) {
          typingTask.cancel();
        }
        typingTask = new TimerTask() {
          @Override
          public void run() {
            mySQLRepository.findChashierByKeyword(searchTxt.getText().toLowerCase()).onSuccess(stream -> {
              DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
              model.setRowCount(0);
              stream.forEach(item -> model.addRow(onAddRow(item)));
            });
          }
        };
        timer.schedule(typingTask, 800L);
      }
    });
  }

  @Override
  public JPanel getMainPanel() {
    return mp;
  }

  @Override
  protected String[] getHeaderColumns() {
    return new String[]{"Id", "Nama", "Jenis Kelamin", "No Hp", "Agama", "Alamat", "Dibuat", "Diubah"};
  }

  @Override
  protected void updateTable() {
    mySQLRepository.findAllCashiers().onSuccess(stream -> {
      DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
      model.setRowCount(0);
      stream.forEach(item -> model.addRow(onAddRow(item)));
    });
  }

  @Override
  protected Object[] onAddRow(Map<String, Object> item) {
    return new Object[]{
      item.get("id"),
      item.get("name"),
      item.get("gender"),
      item.get("phone"),
      item.get("religion"),
      item.get("address"),
      item.get("created_at"),
      item.get("updated_at")
    };
  }

  @Override
  protected JTable getTableItems() {
    return tableItems;
  }

  @Override
  protected Future<Void> onClick(ActionEvent e, ActionCommand actionCommand) {
    char gender = lRadioButton.isSelected() ? 'L' : 'P';
    return switch (actionCommand) {
      case Simpan -> mySQLRepository.insertCashier(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), (String) religionBox.getSelectedItem(), addressTxt.getText(), BCrypt.withDefaults().hashToString(10, passwordTxt.getPassword()));
      case Ubah -> mySQLRepository.updateCashierById(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), (String) religionBox.getSelectedItem(), addressTxt.getText(), BCrypt.withDefaults().hashToString(10, passwordTxt.getPassword()));
      case Hapus -> mySQLRepository.deleteCashierById(idTxt.getText());
      case Keluar -> {
        System.exit(0);
        yield Future.succeedFuture();
      }
      default -> Future.succeedFuture();
    };
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int row = tableItems.rowAtPoint(e.getPoint());
    String id = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 0), "").toString();
    String name = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 1), "").toString();
    String gender = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 2), "").toString();
    String phone = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 3), "").toString();
    String religion = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 4), "").toString();
    String address = Objects.requireNonNullElse(tableItems.getModel().getValueAt(row, 5), "").toString();

    idTxt.setText(id);
    nameTxt.setText(name);
    if (gender.equals("L")) {
      lRadioButton.setSelected(true);
    } else {
      pRadioButton.setSelected(true);
    }
    religionBox.setSelectedItem(religion);
    phoneTxt.setText(phone);
    addressTxt.setText(address);
  }
}
