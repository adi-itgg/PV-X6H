package me.syahdilla.putra.sholeh.form.main;

import me.syahdilla.putra.sholeh.MainFrame;
import me.syahdilla.putra.sholeh.form.cashier.CashierForm;
import me.syahdilla.putra.sholeh.form.customer.CustomerForm;
import me.syahdilla.putra.sholeh.form.item.ItemForm;
import me.syahdilla.putra.sholeh.repository.MySQLRepository;

import javax.swing.*;

public class MainForm {

  private JPanel mp;
  private JButton bukaButton;
  private JButton bukaButton1;
  private JButton bukaButton2;


  public MainForm(MainFrame mainFrame, MySQLRepository mySQLRepository) {
    bukaButton.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new CashierForm(mySQLRepository).getMainPanel());
    });
    bukaButton1.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new ItemForm(mySQLRepository).getMainPanel());
    });
    bukaButton2.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new CustomerForm(mySQLRepository).getMainPanel());
    });
  }

  public JPanel getMainPanel() {
    return mp;
  }

}
