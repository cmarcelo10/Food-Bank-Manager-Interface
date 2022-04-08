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
    public FoodList getFoodList(){
        return this.foodList;
    }
    public void setFoodList (FoodList foodList){
        this.foodList = foodList;
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
}
