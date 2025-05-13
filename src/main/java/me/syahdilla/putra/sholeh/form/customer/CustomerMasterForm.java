package me.syahdilla.putra.sholeh.form.customer;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.form.AbstractBaseMasterForm;
import me.syahdilla.putra.sholeh.model.ActionCommand;
import me.syahdilla.putra.sholeh.repository.CustomerRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class CustomerMasterForm extends AbstractBaseMasterForm<CustomerRepository> {

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

  public CustomerMasterForm(CustomerRepository repository) {
    super(repository);
  }

  @Override
  protected void initialize() {
    super.initialize();

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
    return new String[]{"Id", "Nama", "Jenis Kelamin", "No Hp", "Alamat", "Dibuat", "Diubah"};
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
    char gender = lRadioButton.isSelected() ? 'L' : 'P';
    return switch (actionCommand) {
      case Simpan -> repository.save(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), addressTxt.getText());
      case Ubah -> repository.update(idTxt.getText(), nameTxt.getText(), gender, phoneTxt.getText(), addressTxt.getText());
      case Hapus -> repository.delete(idTxt.getText());
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
