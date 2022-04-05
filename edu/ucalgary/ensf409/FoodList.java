package edu.ucalgary.ensf409;
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
}
