package edu.ucalgary.ensf409;
import java.util.*;
public class Parser extends Database{
    private static ArrayList<FoodItem> itemList;
    private int searchKey;
    private volatile int sortKey;
    public static final Comparator<FoodItem> BY_WHOLE_GRAINS = 
     Comparator.comparing(x->x.getProperty(FoodItem.WHOLE_GRAINS));
    public static final Comparator<FoodItem> BY_FRUIT_VEGGIES 
    = Comparator.comparing(x->x.getProperty(FoodItem.FRUIT_VEGGIES));
    public static final Comparator<FoodItem> BY_PROTEIN 
    = Comparator.comparing(x->x.getProperty(FoodItem.PROTEIN));
    public static final Comparator<FoodItem> BY_OTHER
    = Comparator.comparing(x->x.getProperty(FoodItem.OTHER));
    public static final Comparator<FoodItem> BY_CALORIES
    = Comparator.comparing(x->x.getProperty(FoodItem.CALORIE_AMOUNT));
    private Parser(){}
    /**
     * 
     * @param left the index of the first element to be compared
     * @param right the index of the other element to be compared against the first
     * @param searchKey the attribute to perform the comparison
     * @return the FoodItem deemed the most optimal by the comparison
     */
    public static FoodItem binarySearch(int searchKey, int sortKey,ArrayList<FoodItem>itemList) 
    throws IllegalArgumentException{
        ArrayList<FoodItem> foodItems = itemList;
        int leftBound = 0;
        int rightBound = foodItems.size() - 1;
        if(sortKey < foodItems.get(leftBound).getProperty(searchKey)){
            //if the key occurs at the start of the array
            return foodItems.get(0);
        }
        if(sortKey > foodItems.get(rightBound).getProperty(searchKey)){
            //if the key occurs at the beginning of the array
            return foodItems.get(rightBound);
        }
        while(leftBound <= rightBound){
            int middle = (leftBound + rightBound)/2;
            if(sortKey < foodItems.get(middle).getProperty(searchKey)){
                rightBound = middle - 1;
            }
            else if(sortKey > foodItems.get(middle).getProperty(searchKey)){
                leftBound = middle+1;
            }
            else{
                return foodItems.get(middle);
            }
        }
        leftBound = leftBound -1;
        int leftAttribute = foodItems.get(leftBound).getProperty(searchKey);
        int rightAttribute = foodItems.get(rightBound).getProperty(searchKey);
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
            while(foodItems.get(rightBound).getProperty(searchKey) == 0 
            && rightBound < foodItems.size()){
                rightBound ++;
            }
            return foodItems.get(rightBound);
        }
    }
    /**
     * Takes an {@code ArrayList} of {@code FoodItems}, and splits the list
     * into 15 sublists based on the properites of each {@code FoodItem} object. 
     * The naming system is as follows:
     * <br></<br>
     * <br>{@code 'G' = Whole Grain Content};</br><br> {@code'F' = Fruit Veggies Content};</br>
     * <br>{@code 'P' = Protein Content};</br><br>{@code 'O' = Other Content}.</br>
     * <br>The list names are ordered such that all items within a list containing a capital letter
     * are guaranteed to have a content value greater than 0. For example, {@code Gxxx} contains items such that
     * only {@code item.getGrainContent()} would return a non-zero value.
     * The letter 'x' in a list's name indicates that all FoodItem objects contained within said
     * list do not contain the content of the letter at the specified position. Positioning is consistent
     * throughout. As a result, it should be kept in mind that<b> some sublists may be empty</b>.</br>
     * @param REMAINING_ITEMS the list of {@code FoodItem} objects to be sorted
     * @return an arraylist containing all of the lists.
     */
    public static ArrayList<ArrayList<FoodItem>> splitIntoSublistsByContent(ArrayList<FoodItem>REMAINING_ITEMS){
        ArrayList<FoodItem> xxxO = new ArrayList<>();
        ArrayList<FoodItem> xxPx = new ArrayList<>();
        ArrayList<FoodItem> xxPO = new ArrayList<>();
        ArrayList<FoodItem> xFxx = new ArrayList<>();
        ArrayList<FoodItem> xFxO = new ArrayList<>();
        ArrayList<FoodItem> xFPx = new ArrayList<>();
        ArrayList<FoodItem> xFPO = new ArrayList<>();
        ArrayList<FoodItem> Gxxx = new ArrayList<>();
        ArrayList<FoodItem> GxxO = new ArrayList<>();
        ArrayList<FoodItem> GxPx = new ArrayList<>();
        ArrayList<FoodItem> GxPO = new ArrayList<>();
        ArrayList<FoodItem> GFxx = new ArrayList<>();
        ArrayList<FoodItem> GFxO = new ArrayList<>();
        ArrayList<FoodItem> GFPx = new ArrayList<>();
        ArrayList<FoodItem> GFPO = new ArrayList<>();
        ArrayList<ArrayList<FoodItem>> list = new ArrayList<>();
        list.add(xxxO);
        list.add(xxPx);
        list.add(xxPO);
        list.add(xFxx);
        list.add(xFxO);
        list.add(xFPx);
        list.add(xFPO);
        list.add(Gxxx);
        list.add(GxxO);
        list.add(GxPx);
        list.add(GxPO);
        list.add(GFxx);
        list.add(GFxO);
        list.add(GFPx);
        list.add(GFPO);
        try{
                REMAINING_ITEMS.parallelStream().forEach(item ->{
                int[] arr = Database.getAllItemData(item);
                if(arr[2] == 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){xxxO.add(item);}
                else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){xxPx.add(item);}
                else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){xxPO.add(item);}
                else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){xFxx.add(item);}
                else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){xFxO.add(item);}
                else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){xFPx.add(item);}
                else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){xFPO.add(item);}
                else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){Gxxx.add(item);}
                else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){GxxO.add(item);}
                else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){GxPx.add(item);}
                else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){GxPO.add(item);}
                else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){GFxx.add(item);}
                else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){GFxO.add(item);}
                else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){GFPx.add(item);}
                else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){GFPO.add(item);}
            });

            }
            //if something goes wrong, retry. If the same error occurs twice, work iteratively
            catch(Exception e){
            try{
                list.parallelStream().forEach(subList -> subList.clear());
                REMAINING_ITEMS.parallelStream().forEach(item -> {
                    int[] arr = Database.getAllItemData(item);
                         if(arr[2] == 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){xxxO.add(item);}
                    else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){xxPx.add(item);}
                    else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){xxPO.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){xFxx.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){xFxO.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){xFPx.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){xFPO.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){Gxxx.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){GxxO.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){GxPx.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){GxPO.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){GFxx.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){GFxO.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){GFPx.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){GFPO.add(item);}
                });
            }
            catch(Exception f){
                xxxO.clear();
                xxPx.clear();
                xxPO.clear();
                xFxx.clear();
                xFxO.clear();
                xFPx.clear();
                xFPO.clear();
                Gxxx.clear();
                GxxO.clear();
                GxPx.clear();
                GxPO.clear();
                GFxx.clear();
                GFxO.clear();
                GFPx.clear();
                GFPO.clear();
                REMAINING_ITEMS.forEach(item -> {
                    int[] arr = Database.getAllItemData(item);
                         if(arr[2] == 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){xxxO.add(item);}
                    else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){xxPx.add(item);}
                    else if(arr[2] == 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){xxPO.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){xFxx.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){xFxO.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){xFPx.add(item);}
                    else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){xFPO.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){Gxxx.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){GxxO.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){GxPx.add(item);}
                    else if(arr[2] != 0 && arr[3] == 0 && arr[4] != 0 && arr[5] != 0){GxPO.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){GFxx.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){GFxO.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){GFPx.add(item);}
                    else if(arr[2] != 0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){GFPO.add(item);}
                });
            }
           }
        try{
             list.parallelStream().forEach(array -> {if(array.isEmpty() == false){array.sort(Parser.BY_CALORIES);}});
        }catch(Exception g){
            list.forEach(array -> {if(array.isEmpty() == false){array.sort(Parser.BY_CALORIES);}});
        }
        return list;

    }
    /**
     *A helper method that optimizes the
     * @param hamper
     * @throws DatabaseException
     */
    public static void removeSurplusHamperContents(Hamper hamper, FoodList inventory) throws DatabaseException{
        final ArrayList<FoodItem> REMAINING_ITEMS = inventory.toArrayList();
        final ArrayList<FoodItem> HAMPER_ITEMS = hamper.getFoodList().toArrayList();
        REMAINING_ITEMS.sort(BY_CALORIES);
        HAMPER_ITEMS.sort(BY_CALORIES);
        HAMPER_ITEMS.parallelStream().forEach(item -> item.setIfAdded(false));
        REMAINING_ITEMS.parallelStream().forEach(item -> item.setIfAdded(false));
        HAMPER_ITEMS.sort(BY_CALORIES);
        REMAINING_ITEMS.sort(BY_CALORIES);
        final ArrayList<Client> clients = hamper.getClients();
        Iterator<Client> iterator = clients.iterator();
        int totalGrainNeeds = 0;
        int totalFVNeeds = 0;
        int totalProteinNeeds = 0;
        int totalOtherNeeds = 0;
        int totalCalories = 0;
        int currentGrain = hamper.getTotalGrains();
        int currentFV = hamper.getTotalFruitVeggies();
        int currentProtein = hamper.getTotalProtein();
        int currentOther = hamper.getTotalOther();
        int currentCalories = hamper.getTotalCalories();
        while(iterator.hasNext()){
            Client client = iterator.next();
            totalGrainNeeds+=client.getGrains();
            totalFVNeeds+=client.getFruitVeggies();
            totalProteinNeeds+=client.getProtein();
            totalOtherNeeds+=client.getOther();
            totalCalories+=client.getCalories();
        }
        totalGrainNeeds = totalGrainNeeds*7;
        totalProteinNeeds = totalProteinNeeds*7;
        totalFVNeeds = totalFVNeeds*7;
        totalOtherNeeds = totalOtherNeeds*7;
        totalCalories = totalCalories*7;
        ArrayList<ArrayList<FoodItem>> masterList = splitIntoSublistsByContent(REMAINING_ITEMS);
        ArrayList<FoodItem> xxxO = masterList.get(0);
        ArrayList<FoodItem> xxPx = masterList.get(1);
        ArrayList<FoodItem> xxPO = masterList.get(2);
        ArrayList<FoodItem> xFxx = masterList.get(3);
        ArrayList<FoodItem> xFxO = masterList.get(4);
        ArrayList<FoodItem> xFPx = masterList.get(5);
        ArrayList<FoodItem> xFPO = masterList.get(6);
        ArrayList<FoodItem> Gxxx = masterList.get(7);
        ArrayList<FoodItem> GxxO = masterList.get(8);
        ArrayList<FoodItem> GxPx = masterList.get(9);
        ArrayList<FoodItem> GxPO = masterList.get(10);
        ArrayList<FoodItem> GFxx = masterList.get(11);
        ArrayList<FoodItem> GFxO = masterList.get(12);
        ArrayList<FoodItem> GFPx = masterList.get(13);
        ArrayList<FoodItem> GFPO = masterList.get(14);
        int i = 0;
        int dg = currentGrain - totalGrainNeeds;
        int df = currentFV - totalFVNeeds;
        int dp = currentProtein - totalProteinNeeds;
        int dx = currentOther -totalOtherNeeds;
        while(i < HAMPER_ITEMS.size() && REMAINING_ITEMS.isEmpty() == false){
            dg = currentGrain - totalGrainNeeds;
            df = currentFV - totalFVNeeds;
            dp = currentProtein - totalProteinNeeds;
            dx = currentOther -totalOtherNeeds;
            FoodItem item = HAMPER_ITEMS.get(i);
            int[] arr = Database.getAllItemData(item);
            //16 possible states
            /*
            1   0000 *unhelpful*
            2   0001 *easy to remove*
            3   0010 *easy to remove*
            4   0011 *harder to remove*
            5   0100 *easy*
            6   0101 *hard*
            7   0110 *hard*
            8   0111 *impossible*
            9   1000 *easy*
            10  1001 *hard*
            11  1010 *hard*
            12  1011 *impossible*
            13  1100 *hard*
            14  1101 *impossible*
            15  1110 *impossible*
            16  1111 *beyond impossible*
            */
            //other
            if(arr[2] == 0 && arr[3] == 0 && arr[4] == 0 && arr[5] !=0){
                if(arr[5] < dx){
                    HAMPER_ITEMS.remove(item);
                    REMAINING_ITEMS.add(item);
                    currentOther-=item.getOtherContent();
                    i++;
                    continue;
                }else{  
                    REMAINING_ITEMS.sort(BY_OTHER);
                    FoodItem pointerItem = binarySearch(FoodItem.OTHER, 
                    Math.abs(currentOther-arr[5]),REMAINING_ITEMS);
                    if((pointerItem.getOtherContent()- arr[5]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        HAMPER_ITEMS.remove(item);
                        item.setIfAdded(false);
                        REMAINING_ITEMS.remove(pointerItem);
                        HAMPER_ITEMS.add(pointerItem);
                        REMAINING_ITEMS.add(item);
                        xxxO.add(item);
                        currentOther -= item.getOtherContent();
                        currentCalories -=item.getCalories();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                        //search for the sum of items which meets the criteria
                        int resetValue = currentOther;
                        //Find a bunch of items that can collectively fill the needs gap
                        xxxO.sort(BY_OTHER);
                        while((totalOtherNeeds - currentOther) > 0 && xxxO.size() > 0){
                            FoodItem replacement = binarySearch(FoodItem.OTHER,
                            Math.abs(totalOtherNeeds - currentOther),xxxO);
                            replacement.getOtherContent();
                            int u = 0;
                            if(replacement.getOtherContent() < item.getOtherContent()){
                                currentOther += replacement.getOtherContent();
                                HAMPER_ITEMS.add(replacement);
                                replacement.setIfAdded(true);
                                xxxO.remove(replacement);
                                REMAINING_ITEMS.remove(replacement);
                                continue;
                            }
                            else{
                                int index = xxxO.indexOf(replacement);
                                FoodItem alt = null;
                                u = index;
                                ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                                while(u >=0){
                                    alt = xxxO.get(u);
                                    if(alt.getOtherContent() < replacement.getOtherContent() && 
                                    alt.getOtherContent() < item.getOtherContent()){ 
                                        //find an item smaller than both previous options.
                                        bufferedItems.add(alt);
                                        currentOther+=alt.getOtherContent();
                                        alt.setIfAdded(true);
                                        xxxO.remove(alt);
                                        REMAINING_ITEMS.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    HAMPER_ITEMS.add(item);
                                    bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xxxO.add(foodItem));
                                    currentOther = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                                }
                            }
                        }
                        if(dx < 0){
                            REMAINING_ITEMS.remove(item);
                            HAMPER_ITEMS.add(item);
                            item.setIfAdded(true);
                            currentOther = resetValue;
                            i++;
                            continue;
                        }
                    }
                }
            }
            //protein
            else if(arr[2] == 0 && arr[3] ==0 && arr[4] != 0 && arr[5] == 0){
                if(arr[4] < dp){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentProtein -=item.getProteinContent();
                    i++;
                    continue;
                }
                else{  
                    REMAINING_ITEMS.sort(Parser.BY_PROTEIN);
                    FoodItem pointerItem = binarySearch(FoodItem.PROTEIN,
                    Math.abs(currentProtein-arr[4]),REMAINING_ITEMS);
                    if((pointerItem.getOtherContent()- arr[4]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        HAMPER_ITEMS.remove(item);
                        item.setIfAdded(false);
                        REMAINING_ITEMS.remove(pointerItem);
                        HAMPER_ITEMS.add(pointerItem);
                        REMAINING_ITEMS.add(item);
                        xxPx.add(item);
                        currentProtein -= item.getProteinContent();
                        currentCalories -=item.getCalories();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                        //search for the sum of items which meets the criteria
                        int resetValue = currentProtein;
                        //Find a bunch of items that can collectively fill the needs gap
                        xxPx.sort(Parser.BY_PROTEIN);
                        while((totalProteinNeeds - currentProtein) > 0 && xxPx.size() > 0){
                            FoodItem replacement = binarySearch(FoodItem.PROTEIN,
                            Math.abs(currentProtein),xxPx);
                            replacement.getProteinContent();
                            int u = 0;
                            if(replacement.getProteinContent() < item.getProteinContent()){
                                currentProtein += replacement.getProteinContent();
                                HAMPER_ITEMS.add(replacement);
                                replacement.setIfAdded(true);
                                xxPx.remove(replacement);
                                REMAINING_ITEMS.remove(replacement);
                                continue;
                            }
                            else{
                                int index = xxPx.indexOf(replacement);
                                FoodItem alt = null;
                                u = index;
                                ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                                while(u >=0){
                                    alt = xxPx.get(u);
                                    if(alt.getProteinContent() < replacement.getProteinContent() && 
                                    alt.getProteinContent() < item.getProteinContent()){ 
                                        //find an item smaller than both previous options.
                                        bufferedItems.add(alt);
                                        currentProtein+=alt.getProteinContent();
                                        alt.setIfAdded(true);
                                        xxPx.remove(alt);
                                        REMAINING_ITEMS.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    HAMPER_ITEMS.add(item);
                                    bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xxPx.add(foodItem));
                                    currentOther = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                                }
                            }
                        }
                        if(dp < 0){
                            REMAINING_ITEMS.remove(item);
                            HAMPER_ITEMS.add(item);
                            item.setIfAdded(true);
                            currentProtein = resetValue;
                            i++;
                            continue;
                        }
                    }
                }
            }
            //fruit veggies
            else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] ==0){
                if(item.getIfAdded() == true){
                    i++;
                    continue;
                }
                if(arr[3] < df){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentFV -= item.getFruitVeggiesContent();
                    df = currentFV - totalFVNeeds;
                    i++;
                    continue;
                }else{  
                    REMAINING_ITEMS.sort(BY_FRUIT_VEGGIES);
                    FoodItem pointerItem = binarySearch(FoodItem.FRUIT_VEGGIES,
                    Math.abs(currentFV-arr[3]),REMAINING_ITEMS);
                    if((pointerItem.getFruitVeggiesContent()- arr[3]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        HAMPER_ITEMS.remove(item);
                        item.setIfAdded(false);
                        REMAINING_ITEMS.remove(pointerItem);
                        HAMPER_ITEMS.add(pointerItem);
                        REMAINING_ITEMS.add(item);
                        xFxx.add(item);
                        currentFV -= item.getFruitVeggiesContent();
                        currentCalories -=item.getCalories();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                        //search for the sum of items which meets the criteria
                        int resetValue = currentFV;
                        //Find a bunch of items that can collectively fill the needs gap
                        xFxx.sort(BY_FRUIT_VEGGIES);
                        while((totalFVNeeds-currentFV) > 0 && xFxx.size() > 0){
                            FoodItem replacement = binarySearch(FoodItem.FRUIT_VEGGIES,Math.abs(totalFVNeeds-currentFV),xFxx);
                            replacement.getFruitVeggiesContent();
                            int u = 0;
                            if(replacement.getFruitVeggiesContent() < item.getFruitVeggiesContent()){
                                currentFV += replacement.getFruitVeggiesContent();
                                HAMPER_ITEMS.add(replacement);
                                replacement.setIfAdded(true);
                                xFxx.remove(replacement);
                                REMAINING_ITEMS.remove(replacement);
                                continue;
                            }
                            else{
                                int index = xFxx.indexOf(replacement);
                                FoodItem alt = null;
                                u = index;
                                ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                                while(u >=0){
                                    alt = xFxx.get(u);
                                    if(alt.getFruitVeggiesContent() < replacement.getFruitVeggiesContent() && 
                                    alt.getFruitVeggiesContent() < item.getFruitVeggiesContent()){ 
                                        //find an item smaller than both previous options
                                        bufferedItems.add(alt);
                                        currentFV+=alt.getFruitVeggiesContent();
                                        alt.setIfAdded(true);
                                        xFxx.remove(alt);
                                        REMAINING_ITEMS.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    HAMPER_ITEMS.add(item);
                                    bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xFxx.add(foodItem));
                                    currentFV = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                                }
                            }
                        }
                        if(df < 0){
                            REMAINING_ITEMS.remove(item);
                            HAMPER_ITEMS.add(item);
                            item.setIfAdded(true);
                            currentFV = resetValue;
                            i++;
                            continue;
                        }
                    }
                }
                        //Remove the item from the list. Store it in a pointer
                        //Find a bunch of other items.
                        //If the sum of N items meets the requirements but is less that
                        //the content of the original item
                        // remove the original item
                        //and add the new item(s)
                        //Iterate through the whole inventory and pick out items where only one content
                        //field is nonzero;
                        //then add that to a separate list
                        //take the sum of the items in the separate list from the
                        //end to the beginning because less items is better.
                        //Continue with another iteration until we've either reached the end of the loop
                        //or met the requierments
                        //If we hit the end of the loop but and did not meet the requirements
                        //Add the original item back to the list and continue
                        //Otherwise add the new items back to the list. 
                //whole grains;
            }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){
                if(arr[2] < dg){
                    HAMPER_ITEMS.remove(item);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein += item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++; 
                    continue;
                }else{  
                    REMAINING_ITEMS.sort(BY_WHOLE_GRAINS);
                    FoodItem pointerItem = binarySearch(FoodItem.WHOLE_GRAINS, 
                    Math.abs(currentGrain-arr[2]),REMAINING_ITEMS);
                    if((pointerItem.getGrainContent()- arr[2]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        HAMPER_ITEMS.remove(item);
                        item.setIfAdded(false);
                        REMAINING_ITEMS.remove(pointerItem);
                        HAMPER_ITEMS.add(pointerItem);
                        REMAINING_ITEMS.add(item);
                        Gxxx.add(item);
                        currentGrain -= item.getGrainContent();
                        currentCalories -=item.getCalories();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                        //search for the sum of items which meets the criteria
                        int resetValue = currentGrain;
                        //Find a bunch of items that can collectively fill the needs gap
                        Gxxx.sort(BY_WHOLE_GRAINS);
                        while((totalGrainNeeds - currentGrain) > 0 && Gxxx.size() > 0){
                            FoodItem replacement = binarySearch(FoodItem.WHOLE_GRAINS,Math.abs(totalGrainNeeds - currentGrain),Gxxx);
                            replacement.getGrainContent();
                            int u = 0;
                            if(replacement.getGrainContent() < item.getGrainContent()){
                                currentGrain += replacement.getGrainContent();
                                HAMPER_ITEMS.add(replacement);
                                replacement.setIfAdded(true);
                                Gxxx.remove(replacement);
                                REMAINING_ITEMS.remove(replacement);
                                continue;
                            }
                            else{
                                int index = Gxxx.indexOf(replacement);
                                FoodItem alt = null;
                                u = index;
                                ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                                while(u >=0){
                                    alt = Gxxx.get(u);
                                    if(alt.getGrainContent() < replacement.getGrainContent() && 
                                    alt.getGrainContent() < item.getGrainContent()){ 
                                        //find an item smaller than both previous options
                                        bufferedItems.add(alt);
                                        currentGrain+=alt.getGrainContent();
                                        alt.setIfAdded(true);
                                        Gxxx.remove(alt);
                                        REMAINING_ITEMS.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    HAMPER_ITEMS.add(item);
                                    bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                    bufferedItems.forEach(foodItem -> Gxxx.add(foodItem));
                                    currentGrain = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                                }
                            }
                        }
                        if(dg < 0){
                            REMAINING_ITEMS.remove(item);
                            HAMPER_ITEMS.add(item);
                            item.setIfAdded(true);
                            currentGrain = resetValue;
                            i++;
                            continue;
                        }
                    }
                }
            }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){
                if((arr[2] < dg) && (arr[5] < dx)){
                    //if the item is less than the total surplus, then remove it and continue
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }else{
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    int resetValue1 = currentOther;
                    int resetValue2 = currentGrain;
                    while((totalGrainNeeds - currentGrain) > 0 || (totalOtherNeeds - currentOther > 0) && GxxO.size() > 0){
                        dg = currentGrain - totalGrainNeeds;
                        df = currentFV - totalFVNeeds;
                        dp = currentProtein - totalProteinNeeds;
                        dx = currentOther -totalOtherNeeds;
                        int key = -1;
                        int diff = 0;
                        if((totalGrainNeeds - currentGrain) > (totalOtherNeeds - currentOther)){
                            diff = totalGrainNeeds - currentGrain;
                            GxxO.sort(BY_WHOLE_GRAINS);
                            //pick the larger of the two needs to search for.
                            key = FoodItem.WHOLE_GRAINS;
                        }else{
                            GxxO.sort(BY_OTHER);
                            diff = totalOtherNeeds - currentOther;
                            key = FoodItem.OTHER;
                        }
                        FoodItem replacement = binarySearch(key,Math.abs(diff),GxxO);
                        replacement.getGrainContent();
                        int u = 0;
                        if(replacement.getGrainContent() < item.getGrainContent() 
                        && replacement.getOtherContent() < item.getOtherContent()){
                            currentGrain += replacement.getGrainContent();
                            currentOther += replacement.getOtherContent();
                            HAMPER_ITEMS.add(replacement);
                            replacement.setIfAdded(true);
                            GxxO.remove(replacement);
                            REMAINING_ITEMS.remove(replacement);
                            dg = currentGrain - totalGrainNeeds;
                            df = currentFV - totalFVNeeds;
                            dp = currentProtein - totalProteinNeeds;
                            dx = currentOther -totalOtherNeeds;
                            continue;
                        }
                        else{
                            int index = GxxO.indexOf(replacement);
                            FoodItem alt = null;
                            u = index;
                            ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                            while(u >=0){
                                dg = currentGrain - totalGrainNeeds;
                                df = currentFV - totalFVNeeds;
                                dp = currentProtein - totalProteinNeeds;
                                dx = currentOther -totalOtherNeeds;
                                alt = GxxO.get(u);
                                if(((alt.getGrainContent() < replacement.getGrainContent() && 
                                alt.getGrainContent() < item.getGrainContent()))
                                &&((alt.getOtherContent() < replacement.getOtherContent())&& 
                                alt.getOtherContent() < item.getOtherContent())){ 
                                    //find an item smaller than both previous options
                                    bufferedItems.add(alt);
                                    currentGrain+=alt.getGrainContent();
                                    currentOther+=alt.getOtherContent();
                                    alt.setIfAdded(true);
                                    GxxO.remove(alt);
                                    REMAINING_ITEMS.remove(alt);
                                    break;
                                }
                                u--;
                            }
                            if(u < 0){
                                //if no item combinations can fulfill the needs
                                //add the item back to the list and continue the overall loop
                                HAMPER_ITEMS.add(item);
                                bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                bufferedItems.forEach(foodItem -> GxxO.add(foodItem));
                                currentGrain = resetValue2;
                                currentOther = resetValue1;
                                break;
                            }else{
                                bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                            }
                        }
                    }
                    dg = currentGrain - totalGrainNeeds;
                    df = currentFV - totalFVNeeds;
                    dp = currentProtein - totalProteinNeeds;
                    dx = currentOther -totalOtherNeeds;
                    if(dg < 0 || dx < 0){
                        REMAINING_ITEMS.remove(item);
                        HAMPER_ITEMS.add(item);
                        item.setIfAdded(true);
                        currentGrain = resetValue2;
                        currentOther = resetValue1;
                        i++;
                        continue;
                    }
                }
            }
            else if(arr[2] !=0 && arr[3] != 0 && arr[4] == 0 && arr[5] == 0){
                if((arr[2] < dg) && (arr[3] < df)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                }else{
                        HAMPER_ITEMS.remove(item);
                        item.setIfAdded(false);
                        REMAINING_ITEMS.add(item);
                        currentGrain -= item.getGrainContent();
                        currentCalories -=item.getCalories();
                        currentFV -= item.getFruitVeggiesContent();
                        currentProtein -= item.getProteinContent();
                        currentOther -= item.getOtherContent();
                        int resetValue1 = currentGrain;
                        int resetValue2 = currentOther;
                        while((totalGrainNeeds - currentGrain) > 0 || (totalFVNeeds - currentFV > 0) && GFxx.size() > 0){
                            String key = "";
                            int diff = 0;
                            if((totalGrainNeeds - currentGrain) > (totalFVNeeds - currentFV )){
                                diff = totalGrainNeeds - currentGrain;
                                GFxx.sort(BY_WHOLE_GRAINS);
                                //pick the larger of the two needs to search for.
                                key = "grain";
                            }else{
                                GFxx.sort(BY_FRUIT_VEGGIES);
                                diff = totalFVNeeds - currentFV;
                                key = "fruit veggies";
                            }
                            FoodItem replacement = binarySearch(stringToNumericKey(key),Math.abs(diff),GFxx);
                            int u = 0;
                            if(replacement.getGrainContent() < item.getGrainContent() 
                            && replacement.getFruitVeggiesContent() < item.getFruitVeggiesContent()){
                                currentGrain += replacement.getGrainContent();
                                currentFV += replacement.getFruitVeggiesContent();
                                HAMPER_ITEMS.add(replacement);
                                replacement.setIfAdded(true);
                                GFxx.remove(replacement);
                                REMAINING_ITEMS.remove(replacement);
                                continue;
                            }
                            else{
                                int index = GFxx.indexOf(replacement);
                                FoodItem alt = null;
                                u = index;
                                ArrayList<FoodItem>bufferedItems = new ArrayList<>();
                                while(u >=0){
                                    alt = GFxx.get(u);
                                    if(((alt.getGrainContent() < replacement.getGrainContent() && 
                                    alt.getFruitVeggiesContent() < item.getFruitVeggiesContent()))
                                    &&((alt.getFruitVeggiesContent() < replacement.getFruitVeggiesContent())&& 
                                    alt.getFruitVeggiesContent() < item.getFruitVeggiesContent())){ 
                                        //find an item smaller than both previous options
                                        bufferedItems.add(alt);
                                        currentGrain+=alt.getGrainContent();
                                        currentFV+=alt.getOtherContent();
                                        alt.setIfAdded(true);
                                        GFxx.remove(alt);
                                        REMAINING_ITEMS.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    HAMPER_ITEMS.add(item);
                                    bufferedItems.forEach(foodItem -> REMAINING_ITEMS.add(foodItem));
                                    bufferedItems.forEach(foodItem -> GFxx.add(foodItem));
                                    currentGrain = resetValue1;
                                    currentFV= resetValue2;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> HAMPER_ITEMS.add(queuedItem));
                                }
                            }
                        }
                        
                        if(dg < 0 || df < 0){
                            REMAINING_ITEMS.remove(item);
                            HAMPER_ITEMS.add(item);
                            item.setIfAdded(true);
                            currentGrain = resetValue1;
                            currentFV = resetValue2;
                            i++;
                            continue;
                        }
                    }
                }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] != 0 && arr[5] == 0){
                if((arr[2] < dg) && (arr[4] < dp)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }
            }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] != 0){
                if((arr[2] < dg) && (arr[5] < dx)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }
            }
            else if(arr[2] == 0 && arr[3] != 0 && arr[4] != 0 && arr[5] == 0){
                if((arr[3] < df) && (arr[4] < dp)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }
            }
            else if(arr[2] !=0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){
                if((arr[2] < dg) && (arr[5] < dx) && (arr[3] < df)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                }
                i++;
                continue;
            }
            else if(arr[2] ==0 && arr[3] != 0 && arr[4] != 0 && arr[5] != 0){
                if((arr[4] < dp) && (arr[5] < dx) && (arr[3] < df)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                }
                i++;
                continue;
            }
            else if(arr[2] ==0 && arr[3] != 0 && arr[4] == 0 && arr[5] != 0){
                if((arr[5] < dx) && (arr[3] < df)){
                    HAMPER_ITEMS.remove(item);
                    item.setIfAdded(false);
                    REMAINING_ITEMS.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }
            }
            i++;
        }
        hamper.setFoodList(new FoodList(HAMPER_ITEMS));
        Database.validateHamperContents(hamper);
    }
}
