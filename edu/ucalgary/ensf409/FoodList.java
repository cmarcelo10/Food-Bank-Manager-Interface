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
    private ArrayList<FoodItem> foodItems;
    private int wholeGrainsContent;
    private int fruitVeggieContent;
    private int proteinContent;
    private int otherContent;
    private int calorieContent;

    public FoodList(){
        this.foodItems = new ArrayList<FoodItem>();
    }
    public FoodList(ArrayList<FoodItem> foodItems){
        this.foodItems = foodItems;
        updateAllFields();
    }
    private void updateAllFields(){
        Iterator<FoodItem> iterator = this.foodItems.iterator();
        while(iterator.hasNext()){
            FoodItem temp = iterator.next();
            this.calorieContent += temp.getCalories();
            this.fruitVeggieContent +=temp.getFruitVeggiesContent();
            this.proteinContent += temp.getProteinContent();
            this.wholeGrainsContent +=temp.getGrainContent();
        }
    }
    public ArrayList<FoodItem> getFoodList(){
        return this.foodItems;
    }
    public FoodItem getFoodItem(int index){
        return this.foodItems.get(index);
    }
    public void replaceFoodItem(int index, FoodItem foodItem){
        this.foodItems.set(index,foodItem);
        updateAllFields();
    }
    public void replaceFoodItem(FoodItem original, FoodItem substitute){
        this.foodItems.set(this.foodItems.indexOf(original),substitute);
        updateAllFields();
    }
    public void addFoodItem(FoodItem foodItem){
        foodItems.add(foodItem);
        updateAllFields();
    }
    public int getTotalCalories(){
        return this.calorieContent;
    }
    public int getGrainContent(){
        return this.wholeGrainsContent;
    }
    public int getFruitVeggiesContent(){
        return this.fruitVeggieContent;
    }
    public int getProteinContent(){
        return this.proteinContent;
    }
    public int getOtherContent(){
        return this.otherContent;
    }
    public int getTotalItems(){
        return this.foodItems.size();
    }
}
