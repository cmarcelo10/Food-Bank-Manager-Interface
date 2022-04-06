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
        Client clientA = database.createClient("Adult Male");
        Client clientB = database.createClient("Adult Female");
        Client clientC = database.createClient("Child under 8");
        Client clientD = database.createClient("Child over 8");
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(clientA);
        clients.add(clientB);
        clients.add(clientC);
        clients.add(clientD);
        FoodList list = database.getLeastWasteful(clients);
        ArrayList<FoodItem> list2 = list.getFoodList();
        Iterator<FoodItem> iterator = list2.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next().getItemInfo());
        }
    }
}
