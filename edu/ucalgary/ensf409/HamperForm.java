package edu.ucalgary.ensf409;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
public class HamperForm
{
    boolean invalidState = false;
    JFrame frame = new JFrame("Food Bank Orderform");
    JPanel orderForm = new JPanel(new GridBagLayout());
    JLabel title = new JLabel();
    JLabel c1Label = new JLabel();
    //JTextField client1 = new JTextField(2);
    JLabel c2Label = new JLabel();
   // JTextField client2 = new JTextField(2);
    JLabel c3Label = new JLabel();
   // JTextField client3 = new JTextField(2);
    JLabel c4Label = new JLabel();
    //JTextField client4 = new JTextField(2);
    SpinnerNumberModel client1S = new SpinnerNumberModel(0,0,10,1);
    SpinnerNumberModel client2S = new SpinnerNumberModel(0,0,10,1);
    SpinnerNumberModel client3S = new SpinnerNumberModel(0,0,10,1);
    SpinnerNumberModel client4S = new SpinnerNumberModel(0,0,10,1);
    JSpinner client1 = new JSpinner(client1S);
    JSpinner client2 = new JSpinner(client2S);
    JSpinner client3 = new JSpinner(client3S);
    JSpinner client4 = new JSpinner(client4S);

    private final int SOUTH = GridBagConstraints.SOUTH;
    private final int HORIZONTAL = GridBagConstraints.HORIZONTAL;

    JButton enterOrderButton = new JButton("Enter");
    public HamperForm()
    {
        Insets clientInsets = new Insets(5, 30, 5, 15);
        Insets spinnerInsets = new Insets(5,5,5,30);
        c1Label.setText("# of Adult Males: ");
        c2Label.setText("# of Adult Females: ");
        c3Label.setText("# of Children over 8: ");
        c4Label.setText("# of Children under 8: ");
        c4Label.setFont(c4Label.getFont().deriveFont(14f));
        c3Label.setFont(c3Label.getFont().deriveFont(14f));
        c2Label.setFont(c2Label.getFont().deriveFont(14f));
        c1Label.setFont(c1Label.getFont().deriveFont(14f));
        ((DefaultEditor) client1.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client2.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client3.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client4.getEditor()).getTextField().setEditable(false);
        createInputComponent(c1Label, client1, new Insets(100, 30, 5,15),
        new Insets(100,5,5,30), 
        0, 1,2,1);
        createInputComponent(c2Label, client2, clientInsets, spinnerInsets,0, 2,2,2);
        createInputComponent(c3Label, client3, clientInsets, spinnerInsets,0, 3,2,3);
        createInputComponent(c4Label, client4, clientInsets, spinnerInsets,0, 4,2,4);
        addButtonToWindow(enterOrderButton,0,10, 1, 1, 3, 5, 
        SOUTH, HORIZONTAL, new Insets(100, 5, 30, 30));
        addTitleToWindow("Order Form");
        addTextBoxToWindow("Enter a maximum of 10 clients",1, 4, 0, 5,12f);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(orderForm);
        frame.setSize(500, 500);
        frame.setResizable(true);
        frame.setVisible(true);
    }
    private void addTextBoxToWindow(String text, int h, int w, int x,int y, float size){
        JLabel label = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        label.setText(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(title.getFont().deriveFont(size));
        gbc.ipady = 10;     
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridheight = h;
        gbc.gridwidth = w;
        gbc.gridx = x;      
        gbc.gridy = y;       
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
    private void createInputComponent(JLabel label, JSpinner spinner, Insets textInset, Insets spinnerInset,
    int textX, int textY, int spinnerX, int spinnerY){
        var gbc2 = addSpinnerToWindow(client1, 0, 10, 1, 
        2, spinnerX, spinnerY, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, 
        spinnerInset);
        var gbc1 = addLabelToWindow(label, 0, 10, 1, 
        2, textX, textY, SOUTH, HORIZONTAL, textInset);
        orderForm.add(label, gbc1);
        orderForm.add(spinner,gbc2);
;    }
    private void addButtonToWindow(JButton button, int ipadx, int ipady, int gridHeight, 
    int gridWidth, int gridx, int gridy, int anchor, int fill, Insets insets){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = gridx;      
        gbc.gridy = gridy;       
        orderForm.add(button, gbc);

    }
    public int[] getUserInput(){
        int [] array = new int[4];
        enterOrderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                while(e == null){
                    int c1 = (Integer)client1.getValue();
                    int c2 = (Integer)client2.getValue();
                    int c3 = (Integer)client3.getValue();
                    int c4 = (Integer)client4.getValue();
                    if(((c1 + c2 + c3 + c4) > 0 ) && ( 11 > (c1 + c2 + c3 + c4))){
                        enterOrderButton.setVisible(false);
                    }
                    else{
                        enterOrderButton.setVisible(true);
                    }
                }
                array[0] = (Integer)client1.getValue();
                array[1] = (Integer)client2.getValue();
                array[2] = (Integer)client3.getValue();
                array[3] = (Integer)client4.getValue();
            }
        });
        return array;
    }
    public void closeWindow(){
        this.frame.dispose();
    }
}