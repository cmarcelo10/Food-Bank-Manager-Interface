package edu.ucalgary.ensf409;
import java.util.*;
public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        InventoryManager manager = new InventoryManager(url, user, password);
        FoodList foodList = manager.getAvailableFoodList();
        var theList = foodList.getFoodList();
        Iterator<FoodItem> iterator = theList.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getItemInfo());
        }
    }
    
}
