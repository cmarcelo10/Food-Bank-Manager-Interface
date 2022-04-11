package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;


public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        Database database = new Database(url, user, password);
        FoodList foodList = database.getAvailableFoodList();
        var theList = foodList.toArrayList();
        //FoodItem item = database.searchByValue("calories", 2105);
        //System.out.println(item.getItemInfo());
        Client clientA = database.createClient("Adult Male");
        Client clientB = database.createClient("Adult Female");
        Client clientC = database.createClient("Child under 8");
        Client clientD = database.createClient("Child over 8");
        Client clientE = database.createClient("Adult Female");
        Client clientF = database.createClient("Child over 8");

        ArrayList<Client> clients = new ArrayList<Client>();
        int calTotal = 0;
        int othTotal = 0;
        int gTotal = 0;
        int fvTotal = 0;
        int pTotal = 0;
        clients.add(clientA);
        clients.add(clientB);
        clients.add(clientC);
        clients.add(clientD);
        Iterator<Client> iter = clients.iterator();
        while(iter.hasNext()){
           var temp = iter.next();
           calTotal+=temp.getCalories();
           othTotal+=temp.getOther();
           gTotal+=temp.getGrains();
           fvTotal+=temp.getFruitVeggies();
           pTotal+=temp.getProtein();
        }
        Hamper hamper = database.createHamper(clients);
        FoodList list = hamper.getFoodList();
        System.out.println(String.format("\n\n%-15s%-15s%-15s%-15s","Field","Expected","Actual","Overflow"));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Calories:", 
        calTotal*7,list.getTotalCalories(),list.getTotalCalories()-calTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Grains:", 
        gTotal*7, list.getGrainContent(), list.getGrainContent()-gTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","FV:", 
        fvTotal*7, list.getFruitVeggiesContent(),list.getFruitVeggiesContent()-fvTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Protein:", 
        pTotal*7, list.getProteinContent(), list.getProteinContent()-pTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d\n","Other:",
        othTotal*7, list.getOtherContent(),list.getOtherContent()-othTotal*7));
    }
}
