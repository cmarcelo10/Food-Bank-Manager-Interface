package edu.ucalgary.ensf409;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
public class HamperForm{
    private int maxClients;
    private JPanel orderForm;
    private JLabel title = new JLabel();
    private JLabel c1Label = new JLabel();
    private JLabel c2Label = new JLabel();
    private JLabel c3Label = new JLabel();
    private JLabel c4Label = new JLabel();
    private SpinnerNumberModel spinnerA;
    private SpinnerNumberModel spinnerB;
    private SpinnerNumberModel spinnerC;
    private SpinnerNumberModel spinnerD;
    public final JSpinner CLIENT_A_SPINNER;
    public final JSpinner CLIENT_B_SPINNER;
    public final JSpinner CLIENT_C_SPINNER;
    public final JSpinner CLIENT_D_SPINNER;
    private final JButton ENTER_ORDER_BUTTON = new JButton("Enter Order");
    private final JButton PRINT_ORDER_BUTTON = new JButton("Print Order Form");
    public HamperForm(JPanel anchor, int maxClients)
    {   Insets clientInsets = new Insets(5, 75, 5, 10);
        Insets firstInset = new Insets(120,50,5,75);
        Insets spinnerInsets = new Insets(5,50,5,75);
        this.maxClients = maxClients;
        this.orderForm = anchor;
        this.spinnerA = new SpinnerNumberModel(0,0,maxClients,1);
        this.spinnerB = new SpinnerNumberModel(0,0,maxClients,1);
        this.spinnerC = new SpinnerNumberModel(0,0,maxClients,1);
        this.spinnerD = new SpinnerNumberModel(0,0,maxClients,1);
        this.CLIENT_A_SPINNER = new JSpinner(spinnerA);
        this.CLIENT_B_SPINNER = new JSpinner(spinnerB);
        this.CLIENT_C_SPINNER = new JSpinner(spinnerC);
        this.CLIENT_D_SPINNER = new JSpinner(spinnerD);
        this.c1Label.setText("# of Adult Males: ");
        this.c2Label.setText("# of Adult Females: ");
        this.c3Label.setText("# of Children over 8: ");
        this.c4Label.setText("# of Children under 8: ");
        this.c4Label.setFont(c4Label.getFont().deriveFont(14f));
        this.c3Label.setFont(c3Label.getFont().deriveFont(14f));
        this.c2Label.setFont(c2Label.getFont().deriveFont(14f));
        this.c1Label.setFont(c1Label.getFont().deriveFont(14f));
        addInputComponent(c1Label, CLIENT_A_SPINNER, new Insets(120, 75, 5,10),
        firstInset, 0, 1, 1, 1);
        addInputComponent(c2Label, CLIENT_B_SPINNER, clientInsets, spinnerInsets,0, 2, 1, 2);
        addInputComponent(c3Label, CLIENT_C_SPINNER, clientInsets, spinnerInsets,0, 3, 1, 3);
        addInputComponent(c4Label, CLIENT_D_SPINNER, clientInsets, spinnerInsets,0, 4, 1, 4);
        changeIfSpinnerIsEditable(CLIENT_A_SPINNER, false);
        changeIfSpinnerIsEditable(CLIENT_B_SPINNER, false);
        changeIfSpinnerIsEditable(CLIENT_C_SPINNER, false);
        changeIfSpinnerIsEditable(CLIENT_D_SPINNER, false);
        addTitleToWindow("Order Form");
        addTextBoxToWindow("Enter a maximum of 10 clients",0,1,1, 4, 0, 5,12f,
        GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,
        new Insets(20,10,10,10));
        addButtonToWindow(anchor,ENTER_ORDER_BUTTON,1,10, 1, 4, 0, 6,
        GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(30, 30, 10, 30));
        addButtonToWindow(anchor,PRINT_ORDER_BUTTON,0,10, 1, 1, 3, 7,
        GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL, new Insets(10, 280, 30, 30));
    }
    public void changeIfSpinnerIsEditable(JSpinner spinner, boolean state){
        ((DefaultEditor)spinner.getEditor()).getTextField().setEditable(state);
    }
    public JButton getEnterOrderButton(){
        return this.ENTER_ORDER_BUTTON;
    }
    public JButton getPrintOrderButton(){
        return this.PRINT_ORDER_BUTTON;
    }
    public int getClientASpinnerValue(){
        return (Integer)(this.CLIENT_A_SPINNER.getValue());
    }
    public int getClientBSpinnerValue(){
        return (Integer)(this.CLIENT_B_SPINNER.getValue());
    }
    public int getClientCSpinnerValue(){
        return (Integer)(this.CLIENT_C_SPINNER.getValue());
    }
    public int getClientDSpinnerValue(){
        return (Integer)(this.CLIENT_D_SPINNER.getValue());
    }
    private void addTextBoxToWindow(String text, int ipadx, int ipady, int h, int w, int xPos, int yPos, 
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
        orderForm.add(label, gbc);
    }
    private void addTitleToWindow(String text)
    { GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        title.setText(text);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(18f));
        gbc.ipady = 10;     
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 4;
        gbc.gridx = 0;      
        gbc.gridy = 0;       
        orderForm.add(title, gbc);
    }
    private GridBagConstraints addLabelToWindow(JLabel label, int ipadx, int ipady, int gridHeight, 
    int gridWidth, int gridx, int gridy, int anchor, int fill, Insets insets)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.gridheight = gridHeight;
        gbc.gridwidth = gridWidth;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        return gbc;
    }
    private GridBagConstraints addSpinnerToWindow(JSpinner spinner, int ipadx, int ipady, int gridHeight, 
    int gridWidth, int gridx, int gridy, int anchor, int fill, Insets insets){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.gridheight = gridHeight;
        gbc.gridwidth = gridWidth;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        return gbc;
    }
    private void addInputComponent(JLabel label, JSpinner spinner, Insets textInset, Insets spinnerInset,
    int textX, int textY, int spinnerX, int spinnerY){
        var gbc2 = addSpinnerToWindow(spinner, 0, 10, 1, 
        3, spinnerX, spinnerY, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
        spinnerInset);
        var gbc1 = addLabelToWindow(label, 0, 10, 1, 
        1, textX, textY, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, textInset);
        orderForm.add(label, gbc1);
        orderForm.add(spinner,gbc2);
;    }
    public void addButtonToWindow(JPanel panel, JButton button, int ipadx, int ipady, int gridHeight, 
    int gridWidth, int gridx, int gridy, int anchor, int fill, Insets insets){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.gridheight = gridHeight;
        gbc.gridwidth = gridWidth;
        gbc.gridx = gridx;      
        gbc.gridy = gridy;
        panel.add(button, gbc);
    }
    public boolean throwErrorDialog(int code){
        if(code == -1){
            JOptionPane.showMessageDialog(null, 
            "Error: Total number of clients must be between 0 and " + maxClients);
            return false;

        }else if(code == 1){
            JOptionPane.showMessageDialog(null, 
            "Clients' needs cannot be fully met");
            return true;
        }
        else{
            JOptionPane.showMessageDialog(null, 
            "Unable to complete the request" + maxClients);
            return false;
        }
    }
}