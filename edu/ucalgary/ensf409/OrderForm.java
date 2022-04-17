package edu.ucalgary.ensf409;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.sql.*;
public class OrderForm extends Database implements ActionListener
{
    protected ArrayList<Hamper> orderedHampers;
    public static void main(String args[]) throws Exception
    {
        new OrderForm();
    }
    JFrame frame = new JFrame("Food Bank Manager");
    JTabbedPane tabs = new JTabbedPane();
    JPanel orderPage = new JPanel(new GridBagLayout());
    JPanel foodInventory = new JPanel(new GridBagLayout());
    JPanel clientNeeds = new JPanel(new GridBagLayout());
    JPanel orderList = new JPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    JButton databaseButton = new JButton("Inventory");
    JButton clientDataButton = new JButton("Individual Needs");
    JButton newHamperButton = new JButton("New Hamper");
    JButton orderListButton = new JButton("Order List");
    JButton printOrderButton = new JButton("Print Order");
    JButton updateDatabaseButton = new JButton("Update Inventory");
    JButton updateClientButton = new JButton("Update Client Info");
    JButton returnButton1 = new JButton("Return");
    JButton returnButton2 = new JButton("Return");

    OrderForm() throws Exception
    
    {   super("jdbc:mysql://localhost:3306/food_inventory","root","password");
        this.orderedHampers = new ArrayList<>();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        orderPageSetup();

        inventorySetup();

        clientNeedsSetup();

        orderListSetup();

        tabs.add(orderPage, "Orderform");
        tabs.add(clientNeeds, "Individual Needs");
        tabs.add(foodInventory, "Inventory");
        tabs.add(orderList, "Order List");

        frame.add(tabs);
        frame.setSize(750, 750);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void orderPageSetup()
    {
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        gbc.ipady = 5;      
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        clientDataButton.addActionListener(this);
        clientDataButton.setPreferredSize(new Dimension(120, 25));
        orderPage.add(clientDataButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        databaseButton.setPreferredSize(new Dimension(120, 25));
        databaseButton.addActionListener(this);
        orderPage.add(databaseButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        orderListButton.setPreferredSize(new Dimension(120, 25));
        orderListButton.addActionListener(this);
        orderPage.add(orderListButton, gbc);

        gbc.ipady = 20;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(100, 15, 15, 15);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        newHamperButton.addActionListener(this);
        orderPage.add(newHamperButton, gbc);

        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(80, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 2;      
        gbc.gridy = 2;       
        printOrderButton.setPreferredSize(new Dimension(120, 32));
        printOrderButton.addActionListener(this);
        orderPage.add(printOrderButton, gbc);
    }

    private void inventorySetup() 
    {
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        gbc.ipady = 5;      
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        returnButton1.addActionListener(this);
        foodInventory.add(returnButton1, gbc);

        JLabel spacer1 = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        foodInventory.add(spacer1, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        updateDatabaseButton.addActionListener(this);
        foodInventory.add(updateDatabaseButton, gbc);

        Object[][] inventoryData = {};
        String inventoryNames[] = {"Item", "Grain", "Fruit & Veg", "Protein", "Dairy", "Quantity"};
        JTable inventoryTable = new JTable(inventoryData, inventoryNames);
        JScrollPane inventory = new JScrollPane(inventoryTable);

        gbc.ipady = 575;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        foodInventory.add(inventory, gbc);
    }

    private void clientNeedsSetup() 
    {
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        gbc.ipady = 5;      
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        returnButton2.addActionListener(this);
        clientNeeds.add(returnButton2, gbc);

        JLabel spacer2 = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        clientNeeds.add(spacer2, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        updateClientButton.addActionListener(this);
        clientNeeds.add(updateClientButton, gbc);

        Object[][] clientData = {};
        String clientNames[] = {"Item", "Grain", "Fruit & Veg", "Protein", "Dairy", "Quantity"};
        JTable clientTable = new JTable(clientData, clientNames);
        JScrollPane client = new JScrollPane(clientTable);
        gbc.ipady = 575;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        clientNeeds.add(client, gbc);
    }
    public void orderListSetup()
    {
        JTabbedPane orders = new JTabbedPane();
        orderPage.add(orders);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == databaseButton)
        {
            tabs.setSelectedIndex(2);
        }
        else if (e.getSource() == clientDataButton)
        {
            tabs.setSelectedIndex(1);
        }
        else if (e.getSource() == newHamperButton)
        {
            try{
                new HamperForm();
            }catch(Exception exception){
                exception.printStackTrace();
                System.exit(-1);
            }
           
        }
        else if (e.getSource() == orderListButton)
        {
            tabs.setSelectedIndex(3);
        }        
        else if (e.getSource() == printOrderButton)
        {
            String text = Database.generateOrderForm(orderedHampers);
            Database.writeToFile(text, "orderform");
        }
        else if (e.getSource() == returnButton1)
        {
            tabs.setSelectedIndex(0);
        }
        else if (e.getSource() == returnButton2)
        {
            tabs.setSelectedIndex(0);
        }
    }
}