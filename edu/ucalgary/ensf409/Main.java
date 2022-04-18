package edu.ucalgary.ensf409;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.*;


import java.util.*;
/**A main class built for testing and debugging. Not for submission. */
public class Main{
    public static void main(String args[]) throws Exception{
        boolean successful = false;
        while(!successful){
            String user = JOptionPane.showInputDialog
            (null, "Enter a username to access the SQL database:", 
            "Enter username",JOptionPane.QUESTION_MESSAGE);
            String password = JOptionPane.showInputDialog(null, 
            "Enter a password to access the SQL database:", 
            "Enter password",JOptionPane.QUESTION_MESSAGE);
            String url = "jdbc:mysql://localhost:3306/food_inventory";
                try{
                    new OrderForm(url, user, password);
                    successful = true;
                }catch(NullPointerException e){
                    continue;
                }
        }
    }
}
