package edu.ucalgary.ensf409;
public class FoodItem {
    private final int ITEMID;
    private final String NAME;
    private final int GRAIN_CONTENT;
    private final int PROTEIN_CONTENT;
    private final int FV_CONTENT;
    private final int OTHER_CONTENT;
    private final int CALORIES;
    public FoodItem(int itemid, String name, int grainContent, 
    int proteinContent, int fvContent, int otherContent, int calories){
        this.ITEMID = itemid;
        this.NAME = name;
        this.GRAIN_CONTENT = Math.round((float)(calories*grainContent)/100);
        this.PROTEIN_CONTENT=Math.round((float)(calories*proteinContent)/100);
        this.FV_CONTENT = Math.round((float)(calories*fvContent)/100);
        this.OTHER_CONTENT = Math.round((float)(calories*otherContent)/100);
        this.CALORIES = calories;
    }
    public int getItemID() {
        return ITEMID;
    }
    public int getCalories() {
        return CALORIES;
    }
    public int getFruitsVeggiesContent() {
        return FV_CONTENT;
    }
    public int getGrainContent() {
        return GRAIN_CONTENT;
    }
    public String getName() {
        return NAME;
    }
    public int getOtherContent() {
        return OTHER_CONTENT;
    }
    public int getProteinContent(){
        return PROTEIN_CONTENT;
    }
    public String getItemInfo(){
        String info = "";
        info += "Item name: " + getName() + "\n";
        info += "Item ID: " + getItemID() + "\n";
        info += "Grain Content: " + getGrainContent() + "\n";
        info += "FruitsVeggies Content: " + getFruitsVeggiesContent() + "\n";
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
     * <br>{@code key = 3} returns the value of {@code getFruitsVeggiesContent()}</br>
     * <br>{@code key = 4} returns the value of {@code getProteinContent()}</br>
     * <br>{@code key = 5} returns the value of {@code getOtherContent()}</br>
     * <br>{@code key = 6}returns the value of {@code getCalories()}</br>
     * @return The {@code int} value from the getter specified by the search key.
     */
    public int getNumericAttribute(int key){
        int temp = 0;
        if(key > 6 || key < 1){
            key = key % 6;
            if(key == 0){
                key = 6;
            }
        }
        switch(key){
            case 1:
                temp = this.getItemID();
            case 2:
                temp = this.getGrainContent();
            case 3:
                temp = this.getFruitsVeggiesContent();
            case 4:
                temp = this.getProteinContent();
            case 5:
                temp = this.getOtherContent();
            case 6:
                temp = this.getCalories();
        }
        return temp;
    }
}