package edu.ucalgary.ensf409;
import java.util.*;
public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        Database database = new Database(url, user, password);
        FoodList foodList = database.getAvailableFoodList();
        var theList = foodList.getFoodList();
        database.sortByKey("calories");
        FoodItem item = database.searchByValue("calories", 2105);
        System.out.println(item.getItemInfo());
    }
}
