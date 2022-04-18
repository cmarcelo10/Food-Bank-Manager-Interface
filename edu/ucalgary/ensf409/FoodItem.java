package edu.ucalgary.ensf409;
import java.util.*;
public class FoodItem implements Comparator<FoodItem>{
    private final int ITEM_ID;
    private final String NAME;
    private final int GRAIN_CONTENT;
    private final int PROTEIN_CONTENT;
    private final int FRUIT_VEGGIE_CONTENT;
    private final int OTHER_CONTENT;
    private final int CALORIES;
    private final int PERCENT_WHOLE_GRAINS;
    private final int PERCENT_PROTEIN;
    private final int PERCENT_FRUIT_VEGGIES;
    private final int PERCENT_OTHER;

    public static final int ID = 1;
    public static final int WHOLE_GRAINS = 2;
    public static final int FRUIT_VEGGIES = 3;
    public static final int PROTEIN = 4;
    public static final int OTHER = 5;
    public static final int CALORIE_AMOUNT = 6;
    public static final int WHOLE_GRAINS_PERCENT = 7;
    public static final int FRUIT_VEGGIE_PERCENT = 8;
    public static final int PROTEIN_PERCENT = 9;
    public static final int OTHER_PERCENT = 10;
    
    private volatile boolean added;
    @Override public int compare(FoodItem item1, FoodItem item2){
        return Integer.compare(item1.getCalories(), item2.getCalories());
    }
    public FoodItem(int itemid, String name, int grainContent, 
    int proteinContent, int fruitVeggiesContent, int otherContent, int calories){
        this.PERCENT_WHOLE_GRAINS = grainContent;
        this.PERCENT_PROTEIN = proteinContent;
        this.PERCENT_FRUIT_VEGGIES = fruitVeggiesContent;
        this.PERCENT_OTHER = otherContent;
        this.ITEM_ID = itemid;
        this.NAME = name;
        this.GRAIN_CONTENT = Math.round((float)(calories*grainContent)/100);
        this.PROTEIN_CONTENT=Math.round((float)(calories*proteinContent)/100);
        this.FRUIT_VEGGIE_CONTENT = Math.round((float)(calories*fruitVeggiesContent)/100);
        this.OTHER_CONTENT = Math.round((float)(calories*otherContent)/100);
        this.CALORIES = calories;
        this.added = true;
    }
    public int getItemID() {
        return this.ITEM_ID;
    }
    public int getCalories() {
        return this.CALORIES;
    }
    public int getFruitVeggiesContent() {
        return this.FRUIT_VEGGIE_CONTENT;
    }
    public int getGrainContent() {
        return this.GRAIN_CONTENT;
    }
    public String getName() {
        return this.NAME;
    }
    public int getOtherContent() {
        return this.OTHER_CONTENT;
    }
    public int getProteinContent(){
        return this.PROTEIN_CONTENT;
    }
    public int getPercentProtein(){
        return this.PERCENT_PROTEIN;
    }
    public int getPercentFruitVeggies(){
        return this.PERCENT_FRUIT_VEGGIES;
        
    }
    public int getPercentWholeGrains(){
        return this.PERCENT_WHOLE_GRAINS;
    }
    public int getPercentOther(){
        return this.PERCENT_OTHER;
    }
    public String getItemInfo(){
        String info = "";
        info += "Item name: " + getName() + "\n";
        info += "Item ID: " + getItemID() + "\n";
        info += "Grain Content: " + getGrainContent() + "\n";
        info += "FruitVeggies Content: " + getFruitVeggiesContent() + "\n";
        info += "Protein Content: " + getProteinContent() + "\n";
        info += "Other Content: " + getOtherContent() + "\n";
        info += "Calories: " + getCalories() + "\n";
        return info;
    }
    /**
     * A generic getter that will return a specific field value 
     * of a {@code FoodItem} object based on the passed search key
     * @param key is the search key:
     * <br>{@code key = 1} returns the value of {@code getItemID()}</br> 
     * <br>{@code key = 2} returns the value of {@code getGrainContent()}</br>
     * <br>{@code key = 3} returns the value of {@code getFruitVeggiesContent()}</br>
     * <br>{@code key = 4} returns the value of {@code getProteinContent()}</br>
     * <br>{@code key = 5} returns the value of {@code getOtherContent()}</br>
     * <br>{@code key = 6}returns the value of {@code getCalories()}</br>
     * @return The {@code int} value from the getter specified by the search key.
     */
    public int getProperty(int key){
        if(key > 6 || key < 1){
            key = key % 6;
            if(key == 0){
                key = 6;
            }
        }
        switch(key){
            case 1:
                return this.getItemID();
            case 2:
                return this.getGrainContent();
            case 3:
                return this.getFruitVeggiesContent();
            case 4:
               return this.getProteinContent();
            case 5:
                return this.getOtherContent();
            case 6:
                return this.getCalories();
        }
        return 0;
    }

    public int getPercentContent(String key)throws IllegalArgumentException{
        key = key.toLowerCase().trim();
        if(key.equals("grains")){
            return this.PERCENT_WHOLE_GRAINS;
        }
        else if(key.equals("protein")){
            return this.PERCENT_PROTEIN;
        }
        else if(key.equals("fruit veggies")){
            return this.PERCENT_FRUIT_VEGGIES;
        }
        else if(key.equals("other")){
            return this.PERCENT_OTHER;
        }
        else{
            throw new IllegalArgumentException(String.format("Invalid input to function '%s'",key));
        }
    }public int compareTo(FoodItem foodItem){
        return compare(this,foodItem);
    }
    public void setIfAdded(boolean state){
        this.added = state;
    }
    public boolean getIfAdded(){
        return this.added;
    }
}