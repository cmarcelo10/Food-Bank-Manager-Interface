package edu.ucalgary.ensf409;
import java.util.*;
/**
 * @version 1.0
 * @since 1.0
 * @author Carter Marcelo <ahref>mailto:carter.marcelo@ucalgary.ca</a>
 */
public class Hamper{
    private ArrayList<Client> clients;
    private FoodList foodList;
    private int totalCalories;
    private int totalFruitVeggies;
    private int totalGrains;
    private int totalProtein;
    private int totalOther;
    public Hamper(){
        this.clients = null;
        this.foodList = null;
        this.totalCalories = 0;
        this.totalFruitVeggies = 0;
        this.totalGrains = 0;
        this.totalOther = 0;
        this.totalProtein = 0;
    }
    public Hamper(ArrayList<Client> clients,FoodList foodList){
        this.clients = clients;
        this.foodList = foodList;
        this.totalCalories = foodList.getTotalCalories();
        this.totalFruitVeggies = foodList.getFruitVeggiesContent();
        this.totalGrains = foodList.getGrainContent();
        this.totalOther = foodList.getOtherContent();
        this.totalProtein = foodList.getProteinContent();
    }
    public ArrayList<Client> getClients(){
        return this.clients;
    }
    public void setClients(ArrayList<Client> clients){
        this.clients = clients;
    }
    public FoodList toArrayList(){
        return this.foodList;
    }
    public void setFoodList (FoodList foodList){
        this.foodList = foodList;
    }
    public FoodList getFoodList(){
        return this.foodList;
    }
    public int getTotalCalories(){
        return this.totalCalories;
    }
    public int getTotalFruitVeggies(){
        return this.totalFruitVeggies;
    }
    public int getTotalProtein(){
        return this.totalProtein;
    }
    public int getTotalOther(){
        return this.totalOther;
    }
    public int getTotalGrains(){
        return this.totalGrains;
    }
    public void printSummary(){
        ArrayList<Client> clients = this.getClients();
        Iterator<Client> iter = clients.iterator();
        int calTotal = 0;
        int othTotal = 0;
        int gTotal = 0;
        int fvTotal = 0;
        int pTotal = 0;
        while(iter.hasNext()){
           var temp = iter.next();
           calTotal+=temp.getCalories();
           othTotal+=temp.getOther();
           gTotal+=temp.getGrains();
           fvTotal+=temp.getFruitVeggies();
           pTotal+=temp.getProtein();
        }
        FoodList list = this.getFoodList();
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
        System.out.println((double)((double)list.getTotalCalories()/(double)(calTotal*7))*100);
    }
}
