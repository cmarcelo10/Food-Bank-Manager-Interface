package edu.ucalgary.ensf409;
import java.util.*;

/**A main class built for testing and debugging. Not for submission. */
public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        new OrderForm(url, user, password);
    }
}
