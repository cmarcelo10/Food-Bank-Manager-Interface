package edu.ucalgary.ensf409;
import java.util.*;
import java.util.concurrent.*;
public class Parser extends Thread implements Callable <FoodItem>{
    private ArrayList<FoodItem> itemList;
    private String searchKey;
    private int sortKey;
    public Parser(ArrayList<FoodItem>itemList,String searchKey, int sortKey){
        this.itemList = itemList;
        this.sortKey = sortKey;
        this.searchKey = searchKey;
    }
    private FoodItem binarySearch(String searchKey, int sortKey) throws IllegalArgumentException{
        ArrayList<FoodItem> foodItems = itemList;
        int leftBound = 0;
        int rightBound = foodItems.size() - 1;
        if(sortKey < foodItems.get(leftBound).getPercentContent(searchKey)){

            //if the key occurs at the start of the array
            return foodItems.get(0);
        }
        if(sortKey > foodItems.get(rightBound).getPercentContent(searchKey)){
            //if the key occurs at the beginning of the array
            return foodItems.get(rightBound);
        }
        while(leftBound <= rightBound){
            int middle = (leftBound + rightBound)/2;
            if(sortKey < foodItems.get(middle).getPercentContent(searchKey)){
                rightBound = middle - 1;
            }
            else if(sortKey > foodItems.get(middle).getPercentContent(searchKey)){
                leftBound = middle+1;
            }
            else{
                return foodItems.get(middle);
            }
        }
        int leftAttribute = foodItems.get(leftBound-1).getPercentContent(searchKey);
        int rightAttribute = foodItems.get(rightBound).getPercentContent(searchKey);
        if((leftAttribute - sortKey) < (sortKey - rightAttribute) && leftAttribute != 0){
            return foodItems.get(leftBound);
        }
        else{
            while(foodItems.get(rightBound).getPercentContent(searchKey) == 0 
            && rightBound < foodItems.size()){
                rightBound ++;
            }
            return foodItems.get(rightBound);
        }
    }
    public int stringToNumericKey(String key) throws IllegalArgumentException{
        int temp = 0;
        switch(key = key.toLowerCase().trim()){
            case "itemid":
                temp = 1;
            case "fruit veggies content":
                temp = 2;
            case "grain content":
                temp = 3;
            case "protein content":
                temp = 4;
            case "other content":
                temp = 5;
            case "calories":
                temp = 6;
        }
        if(temp == 0){
            throw new IllegalArgumentException("Invalid search key argument " + key);
        }
        else return temp;
    }
    @Override
    public FoodItem call() throws IllegalArgumentException{
        try{
            return binarySearch(this.searchKey, this.sortKey);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException();
        }
    }
}
