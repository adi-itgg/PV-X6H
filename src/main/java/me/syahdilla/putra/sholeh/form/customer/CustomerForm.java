package me.syahdilla.putra.sholeh.form.customer;

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
import java.util.Timer;
import java.util.TimerTask;

public class CustomerForm extends BaseForm {

  private static final java.util.Timer timer = new Timer(true);

  private JPanel mp;
  private JTextField idTxt;
  private JTextField nameTxt;
  private JTextField phoneTxt;
  private JButton simpanButton;
  private JButton ubahButton;
  private JButton hapusButton;
  private JButton batalButton;
  private JButton keluarButton;
  private JTable tableItems;
  private JTextArea addressTxt;
  private JTextField searchTxt;
  private JRadioButton lRadioButton;
  private JRadioButton pRadioButton;

  private TimerTask typingTask;

  public CustomerForm(MySQLRepository mySQLRepository) {
    super(mySQLRepository);
    initialize();


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
            mySQLRepository.findCustomerByKeyword(searchTxt.getText().toLowerCase()).onSuccess(stream -> {
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
    return new String[]{"Id", "Nama", "Jenis Kelamin", "No Hp", "Alamat", "Dibuat"};
  }

  @Override
  protected void updateTable() {
    mySQLRepository.findAllCustomers().onSuccess(stream -> {
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
      item.get("address"),
      item.get("created_at")
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
      case Simpan -> mySQLRepository.insertCustomer(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), addressTxt.getText());
      case Ubah -> mySQLRepository.updateCustomerById(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), addressTxt.getText());
      case Hapus -> mySQLRepository.deleteCustomerById(idTxt.getText());
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
    String id = tableItems.getModel().getValueAt(row, 0).toString();
    String name = tableItems.getModel().getValueAt(row, 1).toString();
    String gender = tableItems.getModel().getValueAt(row, 2).toString();
    String phone = tableItems.getModel().getValueAt(row, 3).toString();
    String address = tableItems.getModel().getValueAt(row, 4).toString();

    idTxt.setText(id);
    nameTxt.setText(name);
    if (gender.equals("L")) {
      lRadioButton.setSelected(true);
    } else {
      pRadioButton.setSelected(true);
    }
    phoneTxt.setText(phone);
    addressTxt.setText(address);
  }

}
