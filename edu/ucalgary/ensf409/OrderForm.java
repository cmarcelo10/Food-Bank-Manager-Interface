package edu.ucalgary.ensf409;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.util.*;
import java.sql.*;

public class OrderForm implements ActionListener{
    public static void main(String args[]) throws DatabaseException, SQLException{
        new OrderForm();

        //Carter's additions to test their main;
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        Database database = new Database(url, user, password);
        FoodList foodList = database.getAvailableFoodList();
        var theList = foodList.getFoodList();
        database.sortByKey("calories");
        FoodItem item = database.searchByValue("calories", 2105);
        System.out.println(item.getItemInfo());
        Client clientA = database.createClient("Adult Male");
        Client clientB = database.createClient("Adult Female");
        Client clientC = database.createClient("Child under 8");
        Client clientD = database.createClient("Child over 8");
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(clientA);
        clients.add(clientB);
        clients.add(clientC);
        clients.add(clientD);
        FoodList list = database.getLeastWasteful(clients);
        ArrayList<FoodItem> list2 = list.getFoodList();
        Iterator<FoodItem> iterator = list2.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getItemInfo());
        }

    }

    JFrame frame = new JFrame("Food Bank Manager");
    JTabbedPane tabs = new JTabbedPane();
    JPanel orderPage = new JPanel(new GridBagLayout());
    JPanel foodInventory = new JPanel();
    JPanel clientNeeds = new JPanel();
    JPanel orderList = new JPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    JButton hamperFormButton = new JButton("Hamperform");
    JButton databaseButton = new JButton("Inventory");
    JButton clientDataButton = new JButton("Individual Needs");
    JButton newHamperButton = new JButton("New Hamper");
    JButton orderListButton = new JButton("Order List");
    JButton printOrderButton = new JButton("Print Order");
    JButton updateDatabaseButton = new JButton("Update Inventory");
    JButton updateClientButton = new JButton("Update Client Info");



    OrderForm()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gbc.weightx = 0.5;
        gbc.weighty = 1;

        gbc.ipady = 5;      
        gbc.anchor = GridBagConstraints.NORTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        hamperFormButton.addActionListener(this);
        orderPage.add(hamperFormButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        databaseButton.addActionListener(this);
        orderPage.add(databaseButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        clientDataButton.addActionListener(this);
        orderPage.add(clientDataButton, gbc);

        gbc.ipady = 20;
        gbc.insets = new Insets(100, 15, 15, 15);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        newHamperButton.addActionListener(this);
        orderPage.add(newHamperButton, gbc);

        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 2;       
        orderListButton.addActionListener(this);
        orderPage.add(orderListButton, gbc);

        gbc.ipady = 10;
        gbc.gridwidth = 1;
        gbc.gridx = 2;      
        gbc.gridy = 2;       
        printOrderButton.addActionListener(this);
        orderPage.add(printOrderButton, gbc);

        foodInventory.add(BorderLayout.PAGE_END, updateDatabaseButton);

        clientNeeds.add(BorderLayout.PAGE_END, updateClientButton);

        tabs.add(orderPage, "Orderform");
        tabs.add(foodInventory, "Inventory");
        tabs.add(clientNeeds, "Individual Needs");
        tabs.add(orderList, "Order List");

        frame.add(tabs);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void orderPageSetup()
    {
        
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == hamperFormButton)
        {
            tabs.setSelectedIndex(0);
        }
        else if (e.getSource() == databaseButton)
        {
            tabs.setSelectedIndex(1);
        }
        else if (e.getSource() == clientDataButton)
        {
            tabs.setSelectedIndex(2);
        }
        else if (e.getSource() == newHamperButton)
        {
            HamperForm.main(null);
        }
        else if (e.getSource() == orderListButton)
        {
            tabs.setSelectedIndex(3);
        }        
        else if (e.getSource() == printOrderButton)
        {
            //print order
        }
    }
}