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
public class FoodList implements Cloneable{
    private ArrayList<FoodItem> foodItems;
    private int wholeGrainsContent;
    private int fruitVeggieContent;
    private int proteinContent;
    private int otherContent;
    private int calorieContent;

    public static Comparator<FoodItem> compareByGrain = Comparator.comparing(f -> f.getGrainContent());
    public static Comparator<FoodItem> compareByProtein = Comparator.comparing(f -> f.getProteinContent());
    public static Comparator<FoodItem> compareByFruitVeggie = Comparator.comparing(f -> f.getFruitVeggiesContent());
    public static Comparator<FoodItem> compareByOther = Comparator.comparing(f -> f.getOtherContent());
    public static Comparator<FoodItem> compareByCalories = Comparator.comparing(f -> f.getCalories());
    public static Comparator<FoodItem> compareByName = Comparator.comparing(f -> f.getName());

    public FoodList(){
        this.foodItems = new ArrayList<FoodItem>();
    }
    public FoodList(ArrayList<FoodItem> foodItems){
        this.foodItems = foodItems;
        updateAllFields();
    }
    private void updateAllFields(){
        //Multithreadable task
        Iterator<FoodItem> iterator = this.foodItems.iterator();
        this.calorieContent = 0;
        this.fruitVeggieContent = 0;
        this.otherContent = 0;
        this.proteinContent = 0;
        this.wholeGrainsContent = 0;
        
        while(iterator.hasNext()){
            FoodItem temp = iterator.next();
            this.calorieContent += temp.getCalories();
            this.fruitVeggieContent +=temp.getFruitVeggiesContent();
            this.proteinContent += temp.getProteinContent();
            this.wholeGrainsContent +=temp.getGrainContent();
            this.otherContent +=temp.getOtherContent();
        }
    }
    public ArrayList<FoodItem> getFoodList(){
        return this.foodItems;
    }
    public void setFoodList(ArrayList<FoodItem> foodList){
        this.foodItems = foodList;
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
    public FoodList shallowCopy(){
        ArrayList<FoodItem> copy = new ArrayList<FoodItem>();
        for(FoodItem item: foodItems){
            copy.add(item);
        }
        return new FoodList(copy);
    }
}
