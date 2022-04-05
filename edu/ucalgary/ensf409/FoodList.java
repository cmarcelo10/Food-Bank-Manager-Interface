package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;
public class FoodList {
    private ArrayList<FoodItem> foodList;
    public FoodList(){
        this.foodList = new ArrayList<FoodItem>();
    }
    public ArrayList<FoodItem> getFoodList(){
        return this.foodList;
    }
    public FoodItem getFoodItem(int index){
        return this.foodList.get(index);
    }
    public void addFoodItem(FoodItem foodItem){
        foodList.add(foodItem);
    }
    /**
     * @param grains is the required grain content of the client(s)
     * @param protein is the required protein content of the client(s)
     * @param fruitsVeggies is the required fruits content of the client (s)
     * @param other is the required other content of the client (s)
     * @param calories is the required caloric needs of the client(s)
     */
    public void findLeastWastefulCombo(int grains, 
    int protein, int fruitsVeggies, int other, int calories){
        //Step one: calculate a set amount of solutions
        //Step two: compare the amount of waste of the solutions
        //Step three: choose the least wasteful of the combinations
    }
    public void sortByKey(String key){

    }
}
