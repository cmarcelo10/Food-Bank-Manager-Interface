package edu.ucalgary.ensf409;
import java.util.*;
/**
 * A class of the package {@code edu.ucalgary.ensf409} 
 * that holds {@code FoodItem} objects in an {@code ArrayList} data structure.
 * @version 1.0
 * @since 1.0
 * @author Carter Marcelo <ahref>mailto :carter.marcelo@ucalgary.ca</a>
 * @see java.util.ArrayList
 */
public class FoodList{
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
    public void replaceFoodItem(int index, FoodItem foodItem){
        this.foodList.set(index,foodItem);
    }
    public void addFoodItem(FoodItem foodItem){
        foodList.add(foodItem);
    }
}
