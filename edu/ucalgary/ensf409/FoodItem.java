package edu.ucalgary.ensf409;
import java.util.*;
import java.sql.*;

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
        this.GRAIN_CONTENT = grainContent;
        this.PROTEIN_CONTENT=proteinContent;
        this.FV_CONTENT = fvContent;
        this.OTHER_CONTENT = otherContent;
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
    public int getProteinContent() {
        return PROTEIN_CONTENT;
    }
}