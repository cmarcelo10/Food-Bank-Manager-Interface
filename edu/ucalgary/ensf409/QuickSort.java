package edu.ucalgary.ensf409;
import java.util.*;
public class QuickSort{
    public QuickSort(){
        //default constructor
    }
    public void sort(ArrayList<FoodItem> arr, int left, int right, int key){
        while(left < right){
            int partIndex = partition(arr, left, right,key);// partIndex is partitioning index,
            //If the left partition is smaller, handle it recursively
            if(partIndex - left < right - partIndex){
                sort(arr, left, partIndex,key);
                left = partIndex+1;
            }
            else{
                sort(arr, partIndex + 1, right,key);
                right = partIndex - 1;
            }
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
    private int partition(ArrayList<FoodItem> arrayList, int left, int right, int key){ // geeksforgeeks
        int pivot = medianOfThree(arrayList, left, right,key).getNumericAttribute(key); //find the array pivot
        int i = left;
        int j = right;
        while (true){
            while(arrayList.get(i).getNumericAttribute(key) < pivot){
                i=i+1;
            }
            while(arrayList.get(j).getNumericAttribute(key) > pivot){
                j=j-1;
            }
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
    private FoodItem medianOfThree(ArrayList<FoodItem> arrayList, 
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
}
