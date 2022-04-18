package edu.ucalgary.ensf409;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.sql.*;
public class OrderForm extends Database implements ActionListener
{
    /*
    private JTabbedPane tabs = new JTabbedPane();
    private JPanel foodInventory = new JPanel(new GridBagLayout());
    private JPanel clientNeeds = new JPanel(new GridBagLayout());
    private  JPanel orderList = new JPanel();
    private JButton databaseButton = new JButton("Inventory");
    private JButton clientDataButton = new JButton("Individual Needs");
    private JButton newHamperButton = new JButton("New Hamper");
    private JButton orderListButton = new JButton("Order List");
    private JButton updateDatabaseButton = new JButton("Update Inventory");
    private JButton updateClientButton = new JButton("Update Client Info");
    private JButton returnButton1 = new JButton("Return");
    private JButton returnButton2 = new JButton("Return");
    private JPanel orderPage = new JPanel(new GridBagLayout());
    */
    private ArrayList<Hamper> orderedHampers;
    private HamperForm hamperForm;
    private JPanel orderUI = new JPanel(new GridBagLayout());
    private JFrame frame = new JFrame("Food Bank Manager");
    private GridBagConstraints gbc = new GridBagConstraints();
    protected final JButton ENTER_ORDER_BUTTON = new JButton("Enter");
    OrderForm(String url, String user, String password) throws Exception{   
        super("jdbc:mysql://localhost:3306/food_inventory","root","password");
        this.orderedHampers = new ArrayList<>();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(orderUI);
        this.hamperForm = new HamperForm(orderUI,10);
        hamperForm.getEnterOrderButton().addActionListener(this); 
        frame.setSize(750, 750);
        frame.setResizable(true);
        frame.setVisible(true);
        hamperForm.getPrintOrderButton().addActionListener(this);
        /*
        //orderPageSetup();
        //inventorySetup();
        //clientNeedsSetup();
        //orderListSetup();
        tabs.add(orderPage, "Orderform");
        //tabs.add(clientNeeds, "Individual Needs");
        //tabs.add(foodInventory, "Inventory");
        //tabs.add(orderList, "Order List");
        tabs.add(orderUI,"Create Order");
        frame.add(tabs);
         */
    }
   /* public void orderPageSetup()
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
    }
    private void addPrintOrderButton(JPanel parent){
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.WEST; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(80, 15, 15, 15);
        gbc.gridwidth = 1;
        gbc.gridx = 2;      
        gbc.gridy = 2;       
        printOrderButton.setPreferredSize(new Dimension(50, 32));
        printOrderButton.addActionListener(this);
        printOrderButton.setVisible(true);
        parent.add(printOrderButton, gbc);
    }*/
    public GridBagConstraints createGridBagTextBox(String text, int ipadx, int ipady, int h, int w, int xPos, int yPos, 
    float fontsize, int anchor, int fill, Insets insets){
        JLabel label = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        label.setText(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(fontsize));
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;     
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.gridheight = h;
        gbc.gridwidth = w;
        gbc.gridx = xPos;      
        gbc.gridy = yPos;       
        return gbc;
    }
    public void createHamperFromInput(ArrayList<Client> clients){
        Hamper hamper = super.createHamper(clients, false);
        if(hamper == null){
            if(this.hamperForm.throwErrorDialog(1)){
                try{
                    super.createHamper(clients, true);
                    orderedHampers.add(hamper);
                }
                catch(Exception exception){
                    JOptionPane.showMessageDialog(null, exception.toString(), "Warning: Exception caught",JOptionPane.WARNING_MESSAGE);
                }
            }
        }else{
            JOptionPane.showMessageDialog(null,"Order created successfully","Database message",JOptionPane.OK_OPTION);
            orderedHampers.add(hamper);
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource().equals(this.hamperForm.getEnterOrderButton())){
            int adultMale = this.hamperForm.getClientASpinnerValue();
            int adultFemale = this.hamperForm.getClientBSpinnerValue();
            int childrenOver8 = this.hamperForm.getClientCSpinnerValue();
            int childrenUnder8 = this.hamperForm.getClientDSpinnerValue();
            ArrayList<Client> clientsList = super.createClients(adultMale, adultFemale, childrenOver8, childrenUnder8);
            boolean valid = super.validateClientList(clientsList);
            if(!valid){
                hamperForm.throwErrorDialog(-1);
            }else{
                createHamperFromInput(clientsList);
            }
        }
        else if (e.getSource().equals(this.hamperForm.getPrintOrderButton()))
        {
            String text = Database.generateOrderForm(orderedHampers);
            String fname = JOptionPane.showInputDialog(null, "Save as: ",JOptionPane.OK_OPTION);
            Database.writeToFile(text, fname);
            JOptionPane.showMessageDialog(null, "Order form saved");
        }
        /*
        else if (e.getSource().equals(databaseButton))
        {
            tabs.setSelectedIndex(2);
        }
        else if (e.getSource().equals(clientDataButton))
        {
            tabs.setSelectedIndex(1);
        }
        else if (e.getSource().equals(orderListButton))
        {
            tabs.setSelectedIndex(3);
        }        
       
        else if (e.getSource().equals(returnButton1))
        {
            tabs.setSelectedIndex(0);
        }
        else if (e.getSource().equals(returnButton2))
        {
            tabs.setSelectedIndex(0);
        }*/
    }
    /*private void inventorySetup() 
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
        String inventoryNames[] = {"Item", "Grain", "Fruit & Veg", "Protein", "Other", "Quantity"};
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
    */

    /*private void clientNeedsSetup() 
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
    }*/
}