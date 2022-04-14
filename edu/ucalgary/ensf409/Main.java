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
        FoodList inventory = database.getAvailableFoodList();
        ArrayList<FoodItem> list = inventory.toArrayList();
        Hamper hamper = database.createHamper(clients);
        for(FoodItem item : list){
            int[] arr = database.getAllItemData(item);
            if(arr[2] == 0 && arr[3] == 0 && arr[4] == 0 && arr[5] !=0){
               /*System.out.println(String.format("%s %18d","Other only:",item.getItemID()));
               System.out.println(String.format("Calories: %d",item.getCalories()));
               System.out.println("\n");*/
            }
            else if(arr[2] == 0 && arr[3] ==0 && arr[4] != 0 && arr[5] == 0){
               /* System.out.println(String.format("%s %18d","Protein only:",item.getItemID()));
                System.out.println(String.format("Calories: %d",item.getCalories()));
                System.out.println("\n");*/
            }
            else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] ==0){
                System.out.println(String.format("%s %10d","Fruit Veggies only:",item.getItemID()));
                System.out.println(String.format("Calories: %d",item.getCalories()));
                System.out.println("\n");

            
            }else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){
                /*System.out.println(String.format("%s %11d","Whole Grains Only:", item.getItemID()));
                System.out.println(String.format("Calories: %d",item.getCalories()));
                System.out.println("\n");*/

            }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){
               /* System.out.println(String.format("%s %11d","Whole Grains & Other:", item.getItemID()));
                System.out.println(String.format("Grain Content: %d",item.getGrainContent()));
                System.out.println(String.format("Other Content: %d",item.getOtherContent()));
                System.out.println("\n");*/

            }
          
        }
       // Hamper hamper = database.createHamper(clients);
        hamper.printSummary();
       

    }
}
