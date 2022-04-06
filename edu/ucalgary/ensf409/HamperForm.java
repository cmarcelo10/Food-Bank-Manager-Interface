import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  

public class HamperForm implements ActionListener
{
    public static void main(String[] args)
    {
        new HamperForm();
    }

    JFrame frame = new JFrame("Food Bank Orderform");
    JPanel orderForm = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel title = new JLabel();
    JLabel c1Label = new JLabel();
    JTextField client1 = new JTextField(2);
    JLabel c2Label = new JLabel();
    JTextField client2 = new JTextField(2);
    JLabel c3Label = new JLabel();
    JTextField client3 = new JTextField(2);
    JLabel c4Label = new JLabel();
    JTextField client4 = new JTextField(2);
    JButton enterOrderButton = new JButton("Enter");

    public HamperForm()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gbc.weightx = 1;
        gbc.weighty = 1;

        title.setText("Orderform");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        gbc.ipady = 10;       //reset to default   
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
        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(100, 5, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 1;         
        orderForm.add(c1Label, gbc);

        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(100, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 1;         
        orderForm.add(client1, gbc);

        c2Label.setText("# of Adult Females: ");
        c2Label.setFont(c2Label.getFont().deriveFont(14f));
        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 2;      
        orderForm.add(c2Label, gbc);

        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 2;       
        orderForm.add(client2, gbc);

        c3Label.setText("# of Children over 8: ");
        c3Label.setFont(c3Label.getFont().deriveFont(14f));
        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 3;      
        orderForm.add(c3Label, gbc);

        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 3;      
        orderForm.add(client3, gbc);

        c4Label.setText("# of Children under 8: ");
        c4Label.setFont(c4Label.getFont().deriveFont(14f));
        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;      
        gbc.gridy = 4;        
        orderForm.add(c4Label, gbc);

        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 2;      
        gbc.gridy = 4;       
        orderForm.add(client4, gbc);

        gbc.ipady = 10;       //reset to default   
        gbc.anchor = GridBagConstraints.SOUTH; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(100, 5, 5, 5);
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
        String c1 = client1.getText();  
        String c2 = client2.getText();  
        String c3 = client3.getText();  
        String c4 = client4.getText();
        System.out.println(c1 + ", " + c2 + ", " + c3 + ", " + c4);
        frame.dispose();
        //Send to order function 
    }
}
