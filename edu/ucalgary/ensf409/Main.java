package edu.ucalgary.ensf409;
import java.util.*;
import java.sql.*;
public class Main{


    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        Connection dbConnect = DriverManager.getConnection(url, user, password);
        Statement stmt = dbConnect.createStatement();

       



    }
    
}
