package edu.ucalgary.ensf409;
import java.util.*;
import java.util.concurrent.*;
public class Parser extends Thread implements Callable <FoodItem>{
    private volatile ArrayList<FoodItem> itemList;
    private String searchKey;
    private volatile int sortKey;
    public Parser(ArrayList<FoodItem>itemList,String searchKey, int sortKey){
        this.itemList = itemList;
        this.sortKey = sortKey;
        this.searchKey = searchKey;
    }
   
    /**
     * 
     * @param left the index of the first element to be compared
     * @param right the index of the other element to be compared against the first
     * @param searchKey the attribute to perform the comparison
     * @return the FoodItem deemed the most optimal by the comparison
     */
    private FoodItem binarySearch(String sKey, int sortKey) throws IllegalArgumentException{
        int searchKey = Database.stringToNumericKey(sKey);
        ArrayList<FoodItem> foodItems = itemList;
        int leftBound = 0;
        int rightBound = foodItems.size() - 1;
        if(sortKey < foodItems.get(leftBound).getNumericAttribute(searchKey)){
            //if the key occurs at the start of the array
            return foodItems.get(0);
        }
        if(sortKey > foodItems.get(rightBound).getNumericAttribute(searchKey)){
            //if the key occurs at the beginning of the array
            return foodItems.get(rightBound);
        }
        while(leftBound <= rightBound){
            int middle = (leftBound + rightBound)/2;
            if(sortKey < foodItems.get(middle).getNumericAttribute(searchKey)){
                rightBound = middle - 1;
            }
            else if(sortKey > foodItems.get(middle).getNumericAttribute(searchKey)){
                leftBound = middle+1;
            }
            else{
                return foodItems.get(middle);
            }
        }
        leftBound = leftBound -1;
        int leftAttribute = foodItems.get(leftBound).getNumericAttribute(searchKey);
        int rightAttribute = foodItems.get(rightBound).getNumericAttribute(searchKey);
        if((leftAttribute - sortKey) < (sortKey - rightAttribute) && leftAttribute != 0){
            return foodItems.get(leftBound);
        }
        else if(((leftAttribute - sortKey) == (sortKey - rightAttribute)) && leftAttribute != 0){
           var option1 = foodItems.get(leftBound);
           var option2 = foodItems.get(rightBound);
           if(option1.compareTo(option2)<0){
               return option1;
           }
           else{
               return option2;
           }
        }
        else{
            while(foodItems.get(rightBound).getNumericAttribute(searchKey) == 0 
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
                return temp;
            case "fruit veggies content":
                temp = 2;
                return temp;
            case "grain content":
                temp = 3;
                return temp;
            case "protein content":
                temp = 4;
                return temp;
            case "other content":
                temp = 5;
                return temp;
            case "calories":
                temp = 6;
                return temp;
        }
        if(temp == 0){
            throw new IllegalArgumentException("Invalid search key argument " + key);
        }
        else return temp;
    }
    @Override
    public FoodItem call() throws IllegalArgumentException{
    try{
        try{
            return binarySearch(this.searchKey, this.sortKey);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException();
        }
    }catch(NullPointerException f){return null;}
    }
}
