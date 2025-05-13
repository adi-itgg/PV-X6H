package me.syahdilla.putra.sholeh.form.item;

import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.form.AbstractBaseMasterForm;
import me.syahdilla.putra.sholeh.model.ActionCommand;
import me.syahdilla.putra.sholeh.repository.ItemRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class ItemMasterForm extends AbstractBaseMasterForm<ItemRepository> {

  private JPanel mp;
  private JTextField idTxt;
  private JTextField nameTxt;
  private JTextField typeTxt;
  private JTextField buyPriceTxt;
  private JTextField sellPriceTxt;
  private JTable tableItems;
  private JButton simpanButton;
  private JButton ubahButton;
  private JButton hapusButton;
  private JButton batalButton;
  private JButton keluarButton;
  private JTextField searchTxt;

  public ItemMasterForm(ItemRepository repository) {
    super(repository);
  }

  @Override
  protected JPanel mainPanel() {
    return mp;
  }

  @Override
  protected String[] getHeaderColumns() {
    return new String[]{"Id", "Nama", "Jenis", "Harga Beli", "Harga Jual", "Dibuat", "Diubah"};
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
    return switch (ActionCommand.valueOf(e.getActionCommand())) {
      case Simpan -> repository.save(
        idTxt.getText(),
        nameTxt.getText(),
        typeTxt.getText(),
        Double.valueOf(buyPriceTxt.getText()),
        Double.valueOf(sellPriceTxt.getText())
      );

      case Ubah -> repository.update(
        idTxt.getText(),
        nameTxt.getText(),
        typeTxt.getText(),
        Double.valueOf(buyPriceTxt.getText()),
        Double.valueOf(sellPriceTxt.getText())
      );

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
    String type = tableItems.getModel().getValueAt(row, 2).toString();
    String buyPrice = tableItems.getModel().getValueAt(row, 3).toString();
    String sellPrice = tableItems.getModel().getValueAt(row, 4).toString();

    idTxt.setText(id);
    nameTxt.setText(name);
    typeTxt.setText(type);
    buyPriceTxt.setText(buyPrice);
    sellPriceTxt.setText(sellPrice);
  }
}
