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
        Iterator<FoodItem> iterator = foodItems.iterator();
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
    public ArrayList<FoodItem> toArrayList(){
        return this.foodItems;
    }
    public void setFoodList(ArrayList<FoodItem> foodList){
        this.foodItems = foodList;
        Iterator<FoodItem> iterator = foodItems.iterator();
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
    public FoodItem getFoodItem(int index){
        return this.foodItems.get(index);
    }
    /**
     * Removes a specified foodItem from the list and updates the fields of this object to
     * match the current items.
     * @param item is the item to remove from the list
     * @return {@code true} if the item exists in the list, or equivalently, if the size
     * of the list changed as a result of the operation
     */
    public boolean removeFoodItem(FoodItem item){
        if(foodItems.remove(item)){
            this.calorieContent -= item.getCalories();
            this.wholeGrainsContent -= item.getGrainContent();
            this.fruitVeggieContent -= item.getFruitVeggiesContent();
            this.proteinContent -= item.getProteinContent();
            this.otherContent -= item.getOtherContent();
            return true;
        }else{
            return false;
        }
    }
    public void replaceFoodItem(int index, FoodItem foodItem){
        FoodItem item = foodItems.get(index);
        this.foodItems.set(index,foodItem);
        this.calorieContent += (foodItem.getCalories() - item.getCalories());
        this.wholeGrainsContent += (foodItem.getGrainContent() - item.getGrainContent());
        this.fruitVeggieContent += (foodItem.getFruitVeggiesContent()-item.getFruitVeggiesContent());
        this.proteinContent += (foodItem.getProteinContent() - item.getProteinContent());
        this.otherContent += (foodItem.getOtherContent() - item.getOtherContent());

    }
    public void replaceFoodItem(FoodItem original, FoodItem substitute){
        int index = foodItems.indexOf(original);
        replaceFoodItem(index, substitute);
    }
    public void addFoodItem(FoodItem foodItem){
        foodItems.add(foodItem);
        this.calorieContent += foodItem.getCalories();
        this.fruitVeggieContent +=foodItem.getFruitVeggiesContent();
        this.proteinContent += foodItem.getProteinContent();
        this.wholeGrainsContent +=foodItem.getGrainContent();
        this.otherContent +=foodItem.getOtherContent();

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
