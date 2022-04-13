package edu.ucalgary.ensf409;
import java.util.*;

/**A main class built for testing and debugging. Not for submission. */
public class Main{
    public static void main(String args[]) throws Exception{
        String url = "jdbc:mysql://localhost:3306/food_inventory";
        String user = "root";
        String password = "password";
        Database database = new Database(url, user, password);
        //FoodItem item = database.searchByValue("calories", 2105);
        //System.out.println(item.getItemInfo());
        Client clientA = database.createClient("Adult Male");
        Client clientB = database.createClient("Adult Female");
        Client clientC = database.createClient("Child under 8");
        Client clientD = database.createClient("Child over 8");
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(clientA);
        clients.add(clientB);
        clients.add(clientC);
        clients.add(clientD);
        clients.add(clientD);
        clients.add(clientC);
        Hamper hamper = database.createHamper(clients);
        ArrayList<FoodItem> foodItems = hamper.getFoodList().toArrayList();
        for(FoodItem foodItem : foodItems){
            
        }
        hamper.printSummary();

    }
}
