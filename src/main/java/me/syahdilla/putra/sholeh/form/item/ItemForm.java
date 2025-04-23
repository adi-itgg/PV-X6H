package me.syahdilla.putra.sholeh.form.item;

import me.syahdilla.putra.sholeh.ActionCommand;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public class ItemForm extends MouseAdapter implements ActionListener, Consumer<Field> {

  private static final Logger log = LoggerFactory.getLogger(ItemForm.class);

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

  private final DefaultTableModel model;
  private final MySQLRepository mySQLRepository;

  public ItemForm(MySQLRepository mySQLRepository) {
    this.mySQLRepository = mySQLRepository;

    final String[] headerColumns = {"Id", "Nama", "Jenis", "Harga Beli", "Harga Jual", "Dibuat", "Diubah"};
    this.model = new DefaultTableModel(headerColumns, 0);
    tableItems.setModel(model);

    updateTable();

    tableItems.addMouseListener(this);

    Arrays.stream(this.getClass().getDeclaredFields())
      .filter(f -> f.getType().isAssignableFrom(JButton.class))
      .forEach(this);
  }

  public JPanel getMainPanel() {
    return mp;
  }

  private void updateTable() {
    log.debug("Updating table");
    mySQLRepository.findAllItems().onSuccess(stream -> {
      model.setRowCount(0);
      stream.forEach(item -> {
        model.addRow(new Object[]{
          item.get("id"),
          item.get("name"),
          item.get("type"),
          item.get("buy_price"),
          item.get("sell_price"),
          item.get("created_at"),
          item.get("updated_at")
        });
      });
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    log.debug("Button clicked: {}", e.getActionCommand());
    switch (ActionCommand.valueOf(e.getActionCommand())) {
      case Simpan -> mySQLRepository.insertItem(
          idTxt.getText(),
          nameTxt.getText(),
          typeTxt.getText(),
          Double.valueOf(buyPriceTxt.getText()),
          Double.valueOf(sellPriceTxt.getText())
        ).onSuccess(result -> updateTable())
        .onFailure(ex -> {
          log.error("Error inserting item", ex);
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });

      case Ubah -> mySQLRepository.updateItemById(
          idTxt.getText(),
          nameTxt.getText(),
          typeTxt.getText(),
          Double.valueOf(buyPriceTxt.getText()),
          Double.valueOf(sellPriceTxt.getText())
        ).onSuccess(result -> updateTable())
        .onFailure(ex -> {
          log.error("Error updating item", ex);
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });


      case Hapus -> mySQLRepository.deleteItemById(idTxt.getText()).onSuccess(result -> updateTable())
        .onFailure(ex -> {
          log.error("Error deleting item", ex);
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });

      case Keluar -> System.exit(0);
    }
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
