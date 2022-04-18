package edu.ucalgary.ensf409;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
public class OrderForm extends Database implements ActionListener
{
    private JTabbedPane tabs = new JTabbedPane();
    private JPanel foodInventory = new JPanel(new GridBagLayout());
    private JPanel clientNeeds = new JPanel(new GridBagLayout());
    private  JPanel orderList = new JPanel();
    private JTable inventoryTable = new JTable();
    private Object inventoryNames[] = {"ItemID", "Name", "Whole Grains", "Fruit & Veggies", "Protein", "Other", "Calories"};
    private JButton databaseButton = new JButton("Inventory");
    private JButton clientDataButton = new JButton("Individual Needs");
    private JButton orderListButton = new JButton("Order List");
    private JButton updateDatabaseButton = new JButton("Update Inventory");
    private JButton updateClientButton = new JButton("Update Client Info");
    private JButton returnButton1 = new JButton("Return");
    private JButton returnButton2 = new JButton("Return");
    private JPanel orderPage = new JPanel(new GridBagLayout());
    private ArrayList<Hamper> orderedHampers;
    private HamperForm hamperForm;
    JScrollPane inventory = new JScrollPane();
    private JPanel orderUI = new JPanel(new GridBagLayout());
    private JFrame frame = new JFrame("Food Bank Manager");
    private GridBagConstraints gbc = new GridBagConstraints();
    protected final JButton ENTER_ORDER_BUTTON = new JButton("Enter");
    OrderForm(String url, String user, String password) throws Exception{   
        super(url,user,password);
        this.orderedHampers = new ArrayList<>();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(orderUI);
        this.hamperForm = new HamperForm(orderUI,10);
        hamperForm.getEnterOrderButton().addActionListener(this); 
        hamperForm.getPrintOrderButton().addActionListener(this);
        inventorySetup();
        tabs.add(orderUI,"Create Order");
        tabs.add(foodInventory, "Inventory");
        frame.add(tabs);
        frame.setSize(750, 750);
        frame.setResizable(true);
        frame.setVisible(true);
    }
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
        String str = determineIfClientNeedsCanBeMet(clients);

        if(hamper == null || hamper.getFoodList() == null){
            if(this.hamperForm.throwErrorDialog(1)){
                JOptionPane.showMessageDialog(null, str);
            }
        else if(str != null){
            this.hamperForm.throwErrorDialog(1);
            JOptionPane.showMessageDialog(null,str));
        }
        }else{
            JOptionPane.showMessageDialog(null,"Order created successfully","Database message", JOptionPane.OK_OPTION);
            orderedHampers.add(hamper);
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource().equals(this.hamperForm.getEnterOrderButton())){
            int adultMale = 0;
            int adultFemale = 0;
            int childrenOver8 = 0;
            int childrenUnder8 = 0;
            adultMale = this.hamperForm.getClientASpinnerValue();
            adultFemale = this.hamperForm.getClientBSpinnerValue();
            childrenOver8 = this.hamperForm.getClientCSpinnerValue();
            childrenUnder8 = this.hamperForm.getClientDSpinnerValue();
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
            String fname = JOptionPane.showInputDialog(null, "Save as: ",JOptionPane.OK_CANCEL_OPTION);
            Database.writeToFile(text, fname);
            JOptionPane.showMessageDialog(null, "Order form saved");
        }
        else if (e.getSource().equals(returnButton1))
        {
            tabs.setSelectedIndex(0);
        }
        else if (e.getSource().equals(updateDatabaseButton)){
            try{
                super.updateAvailableFood();
            }catch(SQLException exc){
                JOptionPane.showMessageDialog(null, "Failed to connect to database", 
            "Connection Failure",JOptionPane.WARNING_MESSAGE);
            }
            updateInventory();
        }
    }
    private void updateInventory(){
        Object[][] inventoryData = new Object[super.inventory.toArrayList().size()][7];
        try{
            super.updateAvailableFood();
            inventoryData = pullInventoryFromDatabase();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Failed to connect to database", 
            "Connection Failure",JOptionPane.WARNING_MESSAGE);
        }
        foodInventory.remove(inventory);
        inventoryTable.setModel(new DefaultTableModel(inventoryData,inventoryNames));
        inventory = null;
        inventory = new JScrollPane(inventory);
        foodInventory.add(inventory);
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
        Object[][] inventoryData = new Object[super.inventory.toArrayList().size()][7];
        try{
            inventoryData = pullInventoryFromDatabase();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Failed to connect to database", 
            "Connection Failure",JOptionPane.WARNING_MESSAGE);
        }
        try{
           inventoryTable = new JTable(new DefaultTableModel(inventoryData,inventoryNames));
        }catch(Exception e){
            inventoryTable = new JTable();
        }
        JScrollPane inventory = new JScrollPane(inventoryTable);
        inventoryTable.setDefaultEditor(Object.class, null);
        gbc.ipady = 575;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        foodInventory.add(inventory, gbc);
    }
    private Object[][] pullInventoryFromDatabase() throws SQLException{
        super.updateAvailableFood();
        Object[][] available = new Object[super.inventory.toArrayList().size()][7];
        ArrayList<FoodItem> inventoryCopy = super.getAvailableFoodList().toArrayList();
        int i = 0;
        while(i < available.length){
            FoodItem item = inventoryCopy.get(i);
            String name = item.getName();
            Integer a = item.getItemID();
            Integer b = item.getGrainContent();
            Integer c = item.getFruitVeggiesContent();
            Integer d = item.getProteinContent();
            Integer e = item.getOtherContent();
            Integer f = item.getCalories();
            Object[] array = {a,name,b,c,d,e,f};
            available[i] = array;
            i++;
        }
        return available;
    }
}