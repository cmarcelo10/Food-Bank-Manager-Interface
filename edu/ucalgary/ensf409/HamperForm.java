package edu.ucalgary.ensf409;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import java.util.*;



import java.awt.*;
import java.awt.event.*;
public class HamperForm extends OrderForm
{
    boolean invalidState = false;
    JFrame frame = new JFrame("Food Bank Orderform");
    JPanel orderForm = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel title = new JLabel();
    JLabel c1Label = new JLabel();
    //JTextField client1 = new JTextField(2);
    JLabel c2Label = new JLabel();
   // JTextField client2 = new JTextField(2);
    JLabel c3Label = new JLabel();
   // JTextField client3 = new JTextField(2);
    JLabel c4Label = new JLabel();
    //JTextField client4 = new JTextField(2);
    SpinnerNumberModel client1S = new SpinnerNumberModel(0,0,5,1);
    SpinnerNumberModel client2S = new SpinnerNumberModel(0,0,5,1);
    SpinnerNumberModel client3S = new SpinnerNumberModel(0,0,5,1);
    SpinnerNumberModel client4S = new SpinnerNumberModel(0,0,5,1);
    JSpinner client1 = new JSpinner(client1S);
    JSpinner client2 = new JSpinner(client2S);
    JSpinner client3 = new JSpinner(client3S);
    JSpinner client4 = new JSpinner(client4S);

    JButton enterOrderButton = new JButton("Enter");
    public HamperForm() throws Exception
    {
        ((DefaultEditor) client1.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client2.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client3.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) client4.getEditor()).getTextField().setEditable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gbc.weightx = 1;
        gbc.weighty = 1;
        title.setText("Orderform");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        gbc.ipady = 10;     
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 4;
        gbc.gridx = 0;      
        gbc.gridy = 0;       
        orderForm.add(title, gbc);

        c1Label.setText("# of Adult Males: ");
        c1Label.setFont(c1Label.getFont().deriveFont(14f));
        gbc.ipady = 10;    
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(100, 30, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 1;         
        orderForm.add(c1Label, gbc);

        gbc.ipady = 10;    
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(100, 5, 5, 30);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 1;         
        orderForm.add(client1, gbc);

        c2Label.setText("# of Adult Females: ");
        c2Label.setFont(c2Label.getFont().deriveFont(14f));
        gbc.ipady = 10;      
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 2;      
        orderForm.add(c2Label, gbc);

        gbc.ipady = 10;       
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 30);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 2;       
        orderForm.add(client2, gbc);

        c3Label.setText("# of Children over 8: ");
        c3Label.setFont(c3Label.getFont().deriveFont(14f));
        gbc.ipady = 10;      
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 3;      
        orderForm.add(c3Label, gbc);

        gbc.ipady = 10;        
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 30);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 3;      
        orderForm.add(client3, gbc);

        c4Label.setText("# of Children under 8: ");
        c4Label.setFont(c4Label.getFont().deriveFont(14f));
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 30, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 4;        
        orderForm.add(c4Label, gbc);

        gbc.ipady = 10;     
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 30);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 4;       
        orderForm.add(client4, gbc);

        gbc.ipady = 10;     
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(100, 5, 30, 30);
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 3;      
        gbc.gridy = 5;       
        enterOrderButton.addActionListener(this);

        orderForm.add(enterOrderButton, gbc);
        frame.add(orderForm);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int c1 = (Integer)client1.getValue();
        int c2 = (Integer)client2.getValue();
        int c3 = (Integer)client3.getValue();
        int c4 = (Integer)client4.getValue();
        Hamper hamper = null;
        try{
            ArrayList<Client> clients = super.createClients(c1, c2, c3, c4);
            hamper = super.createHamper(clients);
            System.out.println(hamper.printSummary());
            frame.dispose();
        }catch(Exception exception){
            try{
                new HamperForm();
            }
            catch(Exception exception2){
                exception2.addSuppressed(exception);
                exception2.printStackTrace();
            }
            System.out.println("Exception!");
        }
        finally{
            frame.dispose();
        }
        if(hamper != null){
            orderedHampers.add(hamper);
        }
    }
}