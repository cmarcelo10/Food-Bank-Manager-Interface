package edu.ucalgary.ensf409;
import java.util.*;

public class Parser implements Runnable{
    private int sortKey;
    private ArrayList<FoodItem> list = new ArrayList<FoodItem>();
    private static void QuickSort(ArrayList<FoodItem> arr, int left, int right, int key){
        if(left < right){
            int partIndex = partition(arr, left, right, key);
            QuickSort(arr, left, partIndex,key);
            QuickSort(arr, partIndex + 1, right,key);
        }
    }
        /**
     *The Hoare partitioning scheme adataped for use with an {@code ArrayList}
        * of {@code FoodItem} objects.
        * @param arr is an array of {@code FoodItem} objects to be sorted
        * @param left is the leftmost index of the current partition
        * @param right is the rightmost index of the current partition
        * @return an{@code int} that is the index of the pivot element within 
        * the array of {@code FoodItem} objects
        */
    private static int partition(ArrayList<FoodItem> arrayList, int left, int right, int key){ // geeksforgeeks
        int pivot = medianOfThree(arrayList, left, right,key).getNumericAttribute(key); //find the array pivot
        int i = left-1;
        int j = right+1;
        while (true){
            do{
                i++;
            }
            while(arrayList.get(i).getNumericAttribute(key) < pivot);
            do{
                j--;
            }
            while(arrayList.get(j).getNumericAttribute(key) > pivot);
            if(i>=j){
                    return j;
            }
            FoodItem temp = arrayList.get(i);
            arrayList.set(i, arrayList.get(j));
            arrayList.set(j, temp);
        }
    }
    /**
     * Finds the median of three single-word strings, based on their lexicographical order.
     * @param arr is an ArrayList of {@code FoodItem} objects to be sorted.
     * @param left is an {@code int} corresponding the leftmost index of the current partition.
     * @param right is an {@code int} correpsonding the rightmost index of the current partition.
     * @return the pivot element of the current partition.
     */
    private static FoodItem medianOfThree(ArrayList<FoodItem> arrayList, 
    int left, int right, int key){
        int middle = (left + right)/2;
        if(arrayList.get(left).getNumericAttribute(key)> 
        arrayList.get(middle).getNumericAttribute(key)){ 
            FoodItem temp = arrayList.get(left);
            arrayList.set(left,arrayList.get(middle));
            arrayList.set(middle,temp);
        }
        if(arrayList.get(left).getNumericAttribute(key)> 
        arrayList.get(right).getNumericAttribute(key)){
            FoodItem temp = arrayList.get(left);
            arrayList.set(left,arrayList.get(right));
            arrayList.set(right,temp);
        }
        if(arrayList.get(right).getNumericAttribute(key) < 
        arrayList.get(middle).getNumericAttribute(key)){
            FoodItem temp = arrayList.get(middle);
            arrayList.set(middle, arrayList.get(right));
            arrayList.set(right, temp);
        }
        FoodItem temp = arrayList.get(middle);
        arrayList.set(middle, arrayList.get(right));
        arrayList.set(right, temp);
        FoodItem pivot = arrayList.get(right);
        return pivot;
    }
    public static FoodItem binarySearch(int sortKey, int searchKey, FoodList inventory) throws DatabaseException{
        ArrayList<FoodItem> foodItems = inventory.getFoodList();
        int leftBound = 0;
        int rightBound = foodItems.size() - 1;
        if(searchKey < foodItems.get(leftBound).getNumericAttribute(sortKey)){
            return foodItems.get(0);
        }
        if(searchKey > foodItems.get(rightBound).getNumericAttribute(sortKey)){
            return foodItems.get(rightBound);
        }
        while(leftBound <= rightBound){
            int middle = (leftBound + rightBound)/2;
            if(searchKey < foodItems.get(middle).getNumericAttribute(sortKey)){
                rightBound = middle - 1;
            }
            else if(searchKey > foodItems.get(middle).getNumericAttribute(sortKey)){
                leftBound = middle+1;
            }
            else{
                return foodItems.get(middle);
            }
        }
        int leftAttribute = foodItems.get(leftBound-1).getNumericAttribute(sortKey);
        int rightAttribute = foodItems.get(rightBound).getNumericAttribute(sortKey);
        if((leftAttribute - searchKey) < (searchKey - rightAttribute) && leftAttribute != 0){
            return foodItems.get(leftBound);
        }
        else{
            while(foodItems.get(rightBound).getNumericAttribute(sortKey) == 0 
            && rightBound < foodItems.size()){
                rightBound ++;
            }
            return foodItems.get(rightBound);
        }
    }
}
