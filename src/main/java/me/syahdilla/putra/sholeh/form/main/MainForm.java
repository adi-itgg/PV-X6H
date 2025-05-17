package me.syahdilla.putra.sholeh.form.main;

import me.syahdilla.putra.sholeh.MainFrame;
import me.syahdilla.putra.sholeh.form.BaseForm;
import me.syahdilla.putra.sholeh.form.cashier.CashierMasterForm;
import me.syahdilla.putra.sholeh.form.customer.CustomerMasterForm;
import me.syahdilla.putra.sholeh.form.item.ItemMasterForm;
import me.syahdilla.putra.sholeh.pool.Pool;
import me.syahdilla.putra.sholeh.repository.CashierRepository;
import me.syahdilla.putra.sholeh.repository.CustomerRepository;
import me.syahdilla.putra.sholeh.repository.ItemRepository;
import me.syahdilla.putra.sholeh.repository.impl.CashierRepositoryImpl;
import me.syahdilla.putra.sholeh.repository.impl.CustomerRepositoryImpl;
import me.syahdilla.putra.sholeh.repository.impl.ItemRepositoryImpl;

import javax.swing.*;

public class MainForm implements BaseForm {

  private JPanel mp;
  private JButton bukaButton;
  private JButton bukaButton1;
  private JButton bukaButton2;


  private final MainFrame mainFrame;
  private final CashierRepository cashierRepository;
  private final ItemRepository itemRepository;
  private final CustomerRepository customerRepository;

  public MainForm(MainFrame mainFrame, Pool pool) {
    this.mainFrame = mainFrame;
    this.cashierRepository = new CashierRepositoryImpl(pool);
    this.itemRepository = new ItemRepositoryImpl(pool);
    this.customerRepository = new CustomerRepositoryImpl(pool);
  }

  private void initialize() {
    bukaButton.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new CashierMasterForm(cashierRepository).getMainPanel());
    });
    bukaButton1.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new ItemMasterForm(itemRepository).getMainPanel());
    });
    bukaButton2.addActionListener(e -> {
      mainFrame.setSize(800, 500);
      mainFrame.setContainer(new CustomerMasterForm(customerRepository).getMainPanel());
    });
  }

  @Override
  public JPanel getMainPanel() {
    initialize();
    return mp;
  }

}
