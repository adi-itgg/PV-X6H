package me.syahdilla.putra.sholeh.form.cashier;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.form.AbstractBaseMasterForm;
import me.syahdilla.putra.sholeh.model.ActionCommand;
import me.syahdilla.putra.sholeh.repository.CashierRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class CashierMasterForm extends AbstractBaseMasterForm<CashierRepository> {


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


  public CashierMasterForm(CashierRepository cashierRepository) {
    super(cashierRepository);
  }

  @Override
  protected void initialize() {
    super.initialize();

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
  }

  @Override
  protected JPanel mainPanel() {
    return mp;
  }

  @Override
  protected String[] getHeaderColumns() {
    return new String[]{"Id", "Nama", "Jenis Kelamin", "No Hp", "Agama", "Alamat", "Dibuat", "Diubah"};
  }

  @Override
  protected JTable getTable() {
    return tableItems;
  }

  @Override
  protected JTextField getSearchEditText() {
    return searchTxt;
  }

  @Override
  protected Future<Void> onClick(ActionEvent e, ActionCommand actionCommand) {
    final String id = idTxt.getText();
    final String name = nameTxt.getText();
    final String phone = phoneTxt.getText();
    final String religion = religionBox.getSelectedItem() + "";
    final String address = addressTxt.getText();
    final String password = BCrypt.withDefaults().hashToString(10, passwordTxt.getPassword());
    final char gender = lRadioButton.isSelected() ? 'L' : 'P';

    return switch (actionCommand) {
      case Simpan -> repository.save(id, name, gender, phone, religion, address, password);
      case Ubah -> repository.update(id, name, gender, phone, religion, address, password);
      case Hapus -> repository.delete(id);
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
