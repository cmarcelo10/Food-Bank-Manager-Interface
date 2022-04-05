package edu.ucalgary.ensf409;
import java.util.*;
import java.sql.*;
public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        InventoryManager manager = new InventoryManager(url, user, password);
        ArrayList<FoodItem> foodList = theList.getFoodList();
        Iterator<FoodItem> iterator = foodList.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getItemInfo());
        }
        dbConnect.close();
    }
    
}
