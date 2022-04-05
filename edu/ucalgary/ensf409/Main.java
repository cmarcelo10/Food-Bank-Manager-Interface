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
        String query = "SELECT * FROM AVAILABLE_FOOD";
        ResultSet results = stmt.executeQuery(query);
        FoodList theList = new FoodList();
        while(results.next()){
            int itemid = results.getInt(1);
            String name = results.getString(2);
            int grainContent = results.getInt(3);
            int fvContent = results.getInt(4);
            int proteinContent = results.getInt(5);
            int otherContent = results.getInt(6);
            int calories = results.getInt(7);
            theList.addFoodItem(new FoodItem(itemid, name, grainContent, proteinContent, fvContent, otherContent, calories));
        }
        ArrayList<FoodItem> foodList = theList.getFoodList();
        Iterator<FoodItem> iterator = foodList.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getItemInfo());
        }
        dbConnect.close();
        

       



    }
    
}
