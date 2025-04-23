package me.syahdilla.putra.sholeh.form.item;

import me.syahdilla.putra.sholeh.ActionCommand;
import me.syahdilla.putra.sholeh.Future;
import me.syahdilla.putra.sholeh.form.BaseForm;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Map;

public class ItemForm extends BaseForm {

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

    public ItemForm(MySQLRepository mySQLRepository) {
        super(mySQLRepository);
        initialize();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "Nama", "Jenis", "Harga Beli", "Harga Jual", "Dibuat", "Diubah"};
    }

    @Override
    protected void updateTable() {
        log.debug("Updating table");
        mySQLRepository.findAllItems().onSuccess(stream -> {
            DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
            model.setRowCount(0);
            stream.forEach(item -> {
                model.addRow(onAddRow(item));
            });
        });
    }

    @Override
    protected Object[] onAddRow(Map<String, Object> item) {
        return new Object[]{
                item.get("id"),
                item.get("name"),
                item.get("type"),
                item.get("buy_price"),
                item.get("sell_price"),
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
        return switch (ActionCommand.valueOf(e.getActionCommand())) {
            case Simpan -> mySQLRepository.insertItem(
                    idTxt.getText(),
                    nameTxt.getText(),
                    typeTxt.getText(),
                    Double.valueOf(buyPriceTxt.getText()),
                    Double.valueOf(sellPriceTxt.getText())
            );

            case Ubah -> mySQLRepository.updateItemById(
                    idTxt.getText(),
                    nameTxt.getText(),
                    typeTxt.getText(),
                    Double.valueOf(buyPriceTxt.getText()),
                    Double.valueOf(sellPriceTxt.getText())
            );


            case Hapus -> mySQLRepository.deleteItemById(idTxt.getText());

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
