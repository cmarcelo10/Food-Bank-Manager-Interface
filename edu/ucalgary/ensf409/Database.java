package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
/**
 * @version 3.0
 * @since 1.3
 * Part of the package {@code edu.ucalgary.ensf409}. Handles any connections with an SQL database.
 * Exists as an intermediate between the user and the database to maintain security
 * and integrity.
 */
public class Database{
    private String username;
    private String url;
    private String password;
    private Connection dbConnect;
    private FoodList inventory;
    protected Comparator<FoodItem> byProtein 
    = Comparator.comparing((item -> item.getProteinContent()));
    protected Comparator<FoodItem> byWholeGrains
     = Comparator.comparing(item -> item.getGrainContent());
    protected Comparator<FoodItem> byFruitVeggies 
    = Comparator.comparing(item -> item.getFruitVeggiesContent());
    protected Comparator<FoodItem> byOther 
    = Comparator.comparing(item -> item.getOtherContent());
    protected Comparator<FoodItem> byCalorie
     = Comparator.comparing(item -> item.getCalories());
    public Database(String url, String user, String password)
    throws DatabaseException, SQLException{
        this.url = url;
        this.username = user;
        this.password = password;
        boolean updateStatus = updateAvailableFood();
        int attempts = 0;
        //Keep trying to connect:
        while(!(updateStatus) && attempts < 10){
            System.out.println("Failed to connect... retrying ("+ attempts+1+"/10)");
            updateStatus = updateAvailableFood();
            attempts++;
        }
        if(updateStatus == false){
            throw new DatabaseException();
        }
    }

    public Client createClient(String clientType)throws SQLException{
        dbConnect = DriverManager.getConnection(url,username,password);
        clientType = Client.getValidClientType(clientType).toString(); 
        //Standardizes input and prevents SQL injection. 
        String sql = "SELECT * FROM DAILY_CLIENT_NEEDS";
        Statement statement = dbConnect.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            if(resultSet.getString("Client").equals(clientType)){
                break;
            }
            else{
                continue;
            }
        }
        int clientID = resultSet.getInt("ClientID");
        int grains = resultSet.getInt("WholeGrains");
        int protein = resultSet.getInt("Protein");
        int fruitVeggies = resultSet.getInt("FruitVeggies");
        int other = resultSet.getInt("Other");
        int calories = resultSet.getInt("Calories");
        Client client = new Client(clientID, clientType, grains, protein, fruitVeggies, other, calories);
        return client;
    }
    public FoodList getAvailableFoodList(){
        return this.inventory;
    }
    /**
     * 
     * @param foodItem is the {@code FoodItem} object to remove from the database and inventory
     * @param update whether or not to update the SQL food inventory. If performing a large
     * number of updates, this should be false;
     * @return the updated inventory as an ArrayList
     * @throws SQLException
     * @throws DatabaseException
     */
    public ArrayList<FoodItem> removeFromInventory(FoodItem foodItem, boolean update) throws SQLException, DatabaseException{
        boolean updateStatus = true;
        ArrayList<FoodItem> pointer = inventory.toArrayList();
        try{
            try{
                dbConnect = DriverManager.getConnection(url, username, password);
                String query = "DELETE FROM AVAILABLE_FOOD WHERE Name='"+foodItem.getName()+"'";
                PreparedStatement stmt = dbConnect.prepareStatement(query);
                if(update == true){
                    stmt.execute();
                    updateStatus = updateAvailableFood();
                    dbConnect.commit();
                    int attempts = 0;
                    while(!(updateStatus) && attempts < 10){
                        System.out.println("Failed to connect... retrying ("+ attempts+1+"/10)");
                        updateStatus = updateAvailableFood();
                        attempts++;
                    }
                }else{
                    pointer.remove(foodItem);
                }
            }
            catch(SQLException exception){
                dbConnect.rollback();
            }
        }
        catch(SQLException except){
            //Catches an SQL exception in the rollback command, if it occurs
        }
        finally{
            dbConnect.close();
            //throws an SQLException if something goes awry while closing the database;
        }
        if(updateStatus == false){
            throw new DatabaseException();
        }
        if(update == true){
            return this.inventory.toArrayList();
        }
        else{return pointer;}
    }
    /**
     * Compares the value of a numeric field from two foods items, and returns the
     * {@code int} result of the comparison by calling {@code Integer.compare(x,y)}.
     * @param item1 is the first FoodItem to compare
     * @param item2 is the other FoodItem to compare
     * @param stringKey is the name of the attribute to be compared.
     * @return the value of {@code 0} if {@code item1_attribute == item2_attribute};
     * a value {@code < 0} if{@code item1_attribute < item2_attribute}; and a value
     * {@code > 0} {@code item1_attribute < item2_attribute}
     */
    public static int compareByNumericAttribute(FoodItem item1, FoodItem item2, String stringKey){
        int numKey = stringToNumericKey(stringKey);
        return Integer.compare(item1.getNumericAttribute(numKey),item2.getNumericAttribute(numKey));
    }
    /**
     * Updates the FoodList contained by the inventory to reflect 
     * the state of the SQL database
     * @return {@code true} if the update was successful; {@code false} otherwise.
     * @throws SQLException
     */
    public boolean updateAvailableFood() throws SQLException{
        this.inventory = null;
        boolean status = true;
        FoodList list = new FoodList();
        ResultSet results = null;
        try{
            try{
                dbConnect = DriverManager.getConnection(url, username, password);
                Statement stmt = dbConnect.createStatement();
                String query = "SELECT * FROM AVAILABLE_FOOD";
                results = stmt.executeQuery(query);
                while(results.next()){
                    int itemid = results.getInt(1);
                    String name = results.getString(2);
                    int grainContent = results.getInt(3);
                    int fvContent = results.getInt(4);
                    int proteinContent = results.getInt(5);
                    int otherContent = results.getInt(6);
                    int calories = results.getInt(7);
                    list.addFoodItem(new FoodItem(itemid, name, grainContent, 
                    proteinContent, fvContent, otherContent, calories));
                }
                this.inventory = list;
            }
            catch(Exception e){
                try{
                    dbConnect.rollback();
                }catch(SQLException exception){}
                status = false;
            }
        }
        finally{
            dbConnect.close();
        }
        return status;
    }
    /**
     * @since 1.1
     * @param sortKey the string key to perform sorting by/
     * @throws DatabaseException
     */
    public void sortByKey(String sortKey)throws DatabaseException{
        int key = 0;
        if(sortKey.toLowerCase().equals("itemid")){
            key = 1;
        }
        else if(sortKey.toLowerCase().equals("fruitsveggies content")){
            key = 2;
        }else if(sortKey.toLowerCase().equals("grain content")){
            key = 3;
        }else if(sortKey.toLowerCase().equals("protein content")){
            key = 4;
        }else if(sortKey.toLowerCase().equals("other content")){
            key = 5;
        }else if(sortKey.toLowerCase().equals("calories")){
            key = 6;
        }
        else{
            throw new DatabaseException(sortKey + " is not a valid option");
        }
        var foodList = this.inventory.toArrayList();
        QuickSort(foodList, 0, foodList.size()-1,key);
    }
    /**
     * @since 1.5
     * Searches for a food item value within the database, based on the passed search key.
     * @param searchKey is the primary key used to order the data
     * @param searchValue is the the <b>value to search<i> for</i> </b><br></br>
     * <br>{@code searchKey = 1} returns the value of {@code getItemID()}</br> 
     * <br>{@code searchKey = 2} returns the value of {@code getGrainContent()}</br>
     * <br>{@code searchKey = 3} returns the value of {@code getFruitVeggiesContent()}</br>
     * <br>{@code searchKey = 4} returns the value of {@code getProteinContent()}</br>
     * <br>{@code searchKey = 5} returns the value of {@code getOtherContent()}</br>
     * <br>{@code searchKey = 6}returns the value of {@code getCalories()}</br>
     * @return The Food Item with the value closest to the search value.
     */
    public FoodItem searchByValue(String searchKey, int searchValue)throws DatabaseException{
        searchKey = searchKey.toLowerCase().trim();
        int key = 0;
        if(searchKey.equals("itemid") || searchKey.equals("item id")){
            key = 1;
        }
        else if(searchKey.equals("fruitveggies content") || searchKey.equals("fruit veggies content")){
            key = 3;
        }else if(searchKey.equals("grain content")){
            key = 2;
        }else if(searchKey.equals("protein content")){
            key = 4;
        }else if(searchKey.equals("other content")){
            key = 5;
        }else if(searchKey.equals("calories")){
            key = 6;
        }
        else{
            throw new DatabaseException(searchKey + " is not a valid option");
        }
        return binarySearch(key, searchValue);
    }
    private void QuickSort(ArrayList<FoodItem> arr, int left, int right, int key){
        if(left < right){
            int partIndex = partition(arr, left, right, key);
            QuickSort(arr, left, partIndex,key);
            QuickSort(arr, partIndex + 1, right,key);
        }
    }
        /**
        * @since 1.4
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
     * Finds the median of three.
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
    private FoodItem binarySearch(int sortKey, int searchKey) throws DatabaseException{
        ArrayList<FoodItem> foodItems = inventory.toArrayList();
        int leftBound = 0;
        int rightBound = foodItems.size() - 1;
        if(searchKey < foodItems.get(leftBound).getNumericAttribute(sortKey)){
            //if the key occurs at the start of the array
            return foodItems.get(0);
        }
        if(searchKey > foodItems.get(rightBound).getNumericAttribute(sortKey)){
            //if the key occurs at the beginning of the array
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
    public FoodItem binarySearch(int sortKey, int searchKey, ArrayList<FoodItem>foodItems) throws DatabaseException{
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
    /**
     * Lines 560 - 563 inclusively hold the adjustment parameters for overflow limits on each 
     * nutrient type;
     * @param clients
     * @return the least waste
     * @throws DatabaseException
     * @throws SQLException
     */
    public FoodList createHamperFoodList(ArrayList<Client> clients) throws DatabaseException, SQLException{
        Iterator<Client> iterator = clients.iterator();
        int totalGrainNeeds = 0;
        int totalFVNeeds = 0;
        int totalProteinNeeds = 0;
        int totalOtherNeeds = 0;
        int totalCalories = 0;
        while(iterator.hasNext()){
            Client client = iterator.next();
            totalGrainNeeds+=client.getGrains();
            totalFVNeeds+=client.getFruitVeggies();
            totalProteinNeeds+=client.getProtein();
            totalOtherNeeds+=client.getOther();
            totalCalories+=client.getCalories();
        }
        FoodList foodList = new FoodList();
        totalGrainNeeds = totalGrainNeeds*7;
        totalProteinNeeds = totalProteinNeeds*7;
        totalFVNeeds = totalFVNeeds*7;
        totalOtherNeeds = totalOtherNeeds*7;
        totalCalories = totalCalories*7;
        int grains=0; int fruitVeggies=0; 
        int protein=0; int other=0; int calories = 0;
        boolean needsMet = false; boolean gMet = false;
        boolean pMet = false; boolean fMet = false;
        boolean oMet = false; boolean exceptionCaught = false;
        boolean updateGrains = true; boolean updatePro = true;
        boolean updateFV = true; boolean updateOth = true;
        //Search for items that meet the calorie requirements first
        //sort items into food groups
        //find the lowest calorie food that fit the requirements
        ArrayList<FoodItem> grainsList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> proteinList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> fruitVeggieList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> otherList = new ArrayList<FoodItem>();
        Object[] availableFoodItems = inventory.toArrayList().toArray();
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        for(Object foodItem : availableFoodItems){
            Callable<Void> grouper = new Callable<Void>(){
                public Void call(){
                    int g = ((FoodItem)foodItem).getGrainContent();
                    int fv =((FoodItem)foodItem).getFruitVeggiesContent();
                    int pro = ((FoodItem)foodItem).getProteinContent();
                    int ot = ((FoodItem)foodItem).getOtherContent();
                    int[] a = {g,fv,pro,ot};
                    int max = 0;
                    for(int x = 0; x < 4; x++){
                        if(a[x] > max){
                            max = a[x];
                        }
                    }
                    if(max == g){
                        grainsList.add((FoodItem)foodItem);
                    }
                    else if(max == fv){
                        fruitVeggieList.add((FoodItem)foodItem);
        
                    }
                    else if(max == pro){
                        proteinList.add((FoodItem)foodItem);
                    }
                    else{
                        otherList.add((FoodItem)foodItem);
                    }
                    return null;
                };
            };
            callables.add(grouper);
        }
        boolean exception1 = false;
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor1 = Executors.newFixedThreadPool(cores);
        try{
            executor1.invokeAll(callables);

        }
        catch(InterruptedException e){
            e.printStackTrace();
            exception1 = true;
        }
        finally{
            executor1.shutdown();
            if(exception1){
                throw new DatabaseException("An error occurred while parsing the data");
            }
        }

            //Concurrency = doing two different things at the same time
            //Parallelism = doing the same thing multiple times concurrently

        var list = inventory.toArrayList();
        QuickSort(list,0,list.size()-1,6); //sorts the foodList by calorie amounts
        int key = stringToNumericKey("calories");
    
        while(calories < totalCalories){
            int difference = totalCalories - calories;
            FoodItem pointer = binarySearch(key,difference,list);
            grains+=pointer.getGrainContent();
            fruitVeggies+=pointer.getFruitVeggiesContent();
            protein+=pointer.getProteinContent();
            other+=pointer.getOtherContent();
            calories+=pointer.getCalories();
            if(grainsList.contains(pointer)){
                grainsList.remove(pointer);
            }
            else if(proteinList.contains(pointer)){
                proteinList.remove(pointer);
            }
            else if(fruitVeggieList.contains(pointer)){
                fruitVeggieList.remove(pointer);
            }
            else{
                otherList.remove(pointer);
            }
            foodList.addFoodItem(pointer);
        }
        int cpus = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cpus);
        callables = new ArrayList<Callable<Void>>();
        callables.add(new Callable<Void>(){public Void call(){proteinList.sort(byProtein); return null;}});
        callables.add(new Callable<Void>(){public Void call(){grainsList.sort(byWholeGrains); return null;}});
        callables.add(new Callable<Void>(){public Void call(){fruitVeggieList.sort(byFruitVeggies);return null;}});
        callables.add(new Callable<Void>(){public Void call(){ otherList.sort(byOther);return null;}});
        try{
            executor.invokeAll(callables);
        }catch(Exception e){
            e.printStackTrace();
            exceptionCaught = true;
        }finally{
            executor.shutdown();
            if(exceptionCaught){
                throw new DatabaseException("an unknown error occured while handling the request");
            }
        }
        //brute-force find food items that fill the needs gap
        cpus = Runtime.getRuntime().availableProcessors();
        while(!needsMet){
            ExecutorService taskPool = Executors.newFixedThreadPool(cpus);
            List<Parser> tasks = new ArrayList<Parser>();
            if(!gMet && grainsList.isEmpty() == false){
                tasks.add(new Parser(grainsList,"grains", (totalGrainNeeds - grains)));
            }
            if(!pMet && proteinList.isEmpty() == false){
                tasks.add(new Parser(proteinList,"protein",(totalProteinNeeds - protein)));
            }
            if(!fMet && fruitVeggieList.isEmpty()==false){
                tasks.add(new Parser(fruitVeggieList,"fruit veggies",(totalFVNeeds - fruitVeggies)));
            }
            if(!oMet && other < totalOtherNeeds && updateOth && otherList.isEmpty() == false){
                tasks.add(new Parser(otherList,"other",totalOtherNeeds - other));
            }
            List<Future<FoodItem>> futures = null;
            try{
                futures = taskPool.invokeAll(tasks);
            }catch(Exception e){
                e.printStackTrace();
                exceptionCaught = true;
            }finally{
                taskPool.shutdown();
                if(exceptionCaught){
                    throw new DatabaseException("an unknown error occured while handling the request");
                }
            }
            ArrayList<FoodItem> options = new ArrayList<FoodItem>();
            //Add the parsed FoodItems from each thread
            for(int p = 0; p < futures.size(); p++){
                Future<FoodItem> results = futures.get(p);
                try{
                    FoodItem item = results.get();
                    options.add(item);
                }
                catch(InterruptedException | ExecutionException e){
                    e.printStackTrace();
                    throw new DatabaseException("An error occurred while handling the parallel proccessed food list");
                }
            }
            int k = 0;
            int x = options.size();
            int y = 5;
            boolean[][] table = new boolean[x][y];
            //x++ = increment food item
            //y++ = increment field for item 

            // Field  (G, F, P, O, added?)
            // Column (0, 1, 2, 3,   5   )

            int acceptableGCOverflow = 0; //individually adjust these parameters
            int acceptableFVCOverflow = 0; // to set overflow limits for each content type
            int acceptablePCOverflow = 0;
            int acceptableOCOverflow = 0;

            //Determine if an item should or should not be added to the FoodList
            options.sort(byCalorie);
            while(k < options.size()){
                FoodItem item = options.get(k);
                boolean addItem = true;
                int additionalGC = item.getGrainContent();
                int additionalFVC = item.getFruitVeggiesContent();
                int additionalPC = item.getProteinContent();
                int additionalOC = item.getOtherContent();
                if((additionalGC + grains) > grains + acceptableGCOverflow){
                    table[k][0] = false;
                    addItem = false;
                }
                else{
                    table[k][0] = true;
                }
                if((additionalFVC + fruitVeggies) > fruitVeggies + acceptableFVCOverflow){
                    table[k][1] = false;
                    addItem = false;
                }
                else{
                    table[k][1] = true;
                }
                if((additionalPC + protein) > (protein + acceptablePCOverflow)){
                    table[k][2] = false;
                    addItem = false;
                }
                else{
                    table[k][2] = true;
                }
                if((additionalOC + other) > (other + acceptableOCOverflow)){
                    table[k][3] = false;
                    addItem = false;
                }
                else{
                    table[k][3] = true;
                }
                if(addItem == true){
                    //remove the item from the respective list;
                    table[k][4] = true;
                    if(grainsList.contains(item)){
                        grainsList.remove(item);
                    }
                    else if(proteinList.contains(item)){
                        proteinList.remove(item);
                    }
                    else if(fruitVeggieList.contains(item)){
                        fruitVeggieList.remove(item);
                    }
                    else{
                        otherList.remove(item);
                    }
                    foodList.addFoodItem(item);
                    grains+=item.getGrainContent();
                    protein+=item.getProteinContent();
                    fruitVeggies+=item.getFruitVeggiesContent();
                    other+=item.getOtherContent();
                }else{
                    table[k][4] = false;
                }
                addItem = true; //reset the "addItem" flag;
                k++; //increment k;
            }
            int m = 0;
            for(boolean[] bool : table){
                if(m >= options.size()){
                    break;
                }
                if(bool[4] == true){
                    options.remove(options.get(m));
                }
                m++;
            }
            if(options.size() > 0){
                FoodItem item = options.get(options.size()-1);
                foodList.addFoodItem(item);
                grains+=item.getGrainContent();
                protein+=item.getProteinContent();
                fruitVeggies+=item.getFruitVeggiesContent();
                other+=item.getOtherContent();
                if(grainsList.contains(item)){
                    grainsList.remove(item);
                }
                else if(proteinList.contains(item)){
                    proteinList.remove(item);
                }
                else if(fruitVeggieList.contains(item)){
                    fruitVeggieList.remove(item);
                }
                else{
                    otherList.remove(item);
                }
            }
            /*
            //decide what the best item is to add: the best item causes the least overflow;
            int[][] decisionMatrix = new int[options.size()][4];
            for(FoodItem f : options){
                int gc = f.getGrainContent();
                int fvc = f.getFruitVeggiesContent();
                int pc = f.getProteinContent();
                int oc = f.getOtherContent();
            }
            */
            gMet = grains >= totalGrainNeeds;
            pMet = protein >=totalProteinNeeds;
            fMet = fruitVeggies >= totalFVNeeds;
            oMet = other >= totalOtherNeeds;
            if(gMet && pMet && fMet && oMet){needsMet = true;}
            else{
                boolean a = grainsList.isEmpty();
                boolean b = proteinList.isEmpty();
                boolean c = fruitVeggieList.isEmpty();
                boolean d = otherList.isEmpty();
                if(a){updateGrains = false;}
                if(b){updatePro = false;}
                if(c){updateFV = false;}
                if(d){updateOth = false;}
            }
            if(!updateFV && !updatePro && !updateOth && !updateGrains){
                System.err.println("Client needs cannot be fulfilled");
                break;
            }
        }
        return foodList;
    }
    /**
     * Translates a String key into the appropriate numeric value for the system
     * @param key is the String key to be converted into a numeric key.
     * <br>{@code searchKey = 1} correlates to the value of ItemID </br> 
     * <br>{@code searchKey = 2} correlates to the value of {@code getGrainContent()}</br>
     * <br>{@code searchKey = 3} correlates to the value of {@code getFruitVeggiesContent()}</br>
     * <br>{@code searchKey = 4} correlates to the value of {@code getProteinContent()}</br>
     * <br>{@code searchKey = 5} correlates to the value of {@code getOtherContent()}</br>
     * <br>{@code searchKey = 6} correlates to the value of {@code getCalories()}</br>
     * <br><b>Note: values 1 and 6 are not allowed in this method.</b></br>.
     * @return the {@int} key corresponding the input String key; -1 if the key was invalid.
     * @throws DatabaseException if an invalid search key is specified (0);
     * @return
     */
    public static int stringToNumericKey(String key) throws IllegalArgumentException{
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
            case "grains":
                temp = 6;
            case "fruit veggies":
                temp = 2;
            case "protein":
                temp = 4;
            case "other":
                temp = 5;
        }
        if(temp == 0){
            throw new IllegalArgumentException("Invalid search key argument " + key);
        }
        else return temp;
    }
    public int findAverageParameter(String searchKey, ArrayList<FoodItem> list) throws DatabaseException, IllegalArgumentException{
        int key = stringToNumericKey(searchKey);
        if(key == 1){
            throw new IllegalArgumentException("Invalid parameter: " + key);
        }
        int count = list.size();
        int total = 0;
        for(FoodItem item : list){
           total += item.getNumericAttribute(key);
        }
        double avg = total / count;
        return (int)Math.round(avg);

    }
    public void optimizeHamperItems(Hamper hamper)throws DatabaseException{
        //Take a single item from the list
        //Compare it to two other items in the inventory
        //Item 1 + Item 2 should be able to replace the existing item
        //replace one item with two;
        FoodList foodList = hamper.getFoodList();
        ArrayList<FoodItem> grainsList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> proteinList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> fruitVeggieList = new ArrayList<FoodItem>();
        ArrayList<FoodItem> otherList = new ArrayList<FoodItem>();
        Object[] availableFoodItems = foodList.toArrayList().toArray();
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        for(Object foodItem : availableFoodItems){
            Callable<Void> grouper = new Callable<Void>(){
                public Void call(){
                    int g = ((FoodItem)foodItem).getGrainContent();
                    int fv =((FoodItem)foodItem).getFruitVeggiesContent();
                    int pro = ((FoodItem)foodItem).getProteinContent();
                    int ot = ((FoodItem)foodItem).getOtherContent();
                    int[] a = {g,fv,pro,ot};
                    int max = 0;
                    for(int x = 0; x < 4; x++){
                        if(a[x] > max){
                            max = a[x];
                        }
                    }
                    if(max == g){
                        grainsList.add((FoodItem)foodItem);
                    }
                    else if(max == fv){
                        fruitVeggieList.add((FoodItem)foodItem);
        
                    }
                    else if(max == pro){
                        proteinList.add((FoodItem)foodItem);
                    }
                    else{
                        otherList.add((FoodItem)foodItem);
                    }
                    return null;
                };
            };
            callables.add(grouper);
        }
        boolean exception1 = false;
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor1 = Executors.newFixedThreadPool(cores);
        try{
            executor1.invokeAll(callables);
        }
        catch(InterruptedException e){
            e.printStackTrace();
            exception1 = true;
        }
        finally{
            executor1.shutdown();
            if(exception1){
                throw new DatabaseException("An error occurred while parsing the data");
            }
        }
        //Find the highest-calorie item and remove it from the list
        //repeat until under the threashold

        /*
        FoodList wholeGrains = null;
        FoodList protein = null;
        FoodList fruitVeggies = null;
        FoodList other = null;
        List<Callable<Object[]>> taskSetA = new ArrayList<Callable<Object[]>>();
        Callable<Object[]> listMaker1 = new Callable<Object[]>(){
            public Object[] call(){
                Object objects[] = new Object[2];
                objects[0] = new FoodList(grainsList);
                objects[1] = "grains";
                return objects;
            }
        };
        Callable<Object[]> listMaker2 = new Callable<Object[]>(){
            public Object[] call(){
                Object objects[] = new Object[2];
                objects[0] = new FoodList(proteinList);
                objects[1] = "protein";
                return objects;
            }
        };
        Callable<Object[]> listMaker3 = new Callable<Object[]>(){
            public Object[] call(){
                Object objects[] = new Object[2];
                objects[0] = new FoodList(fruitVeggieList);
                objects[1] = "fruitVeggies";
                return objects;
            }
        };
        Callable<Object[]> listMaker4 = new Callable<Object[]>(){
            public Object[] call(){
                Object objects[] = new Object[2];
                objects[0] = new FoodList(otherList);
                objects[1] = "other";
                return objects;
            }
        };
        taskSetA.add(listMaker1);
        taskSetA.add(listMaker2);
        taskSetA.add(listMaker3);
        taskSetA.add(listMaker4);
        cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        boolean exception2 = false;
        List<Future<Object[]>> futures = null;
        try{
            futures = executorService.invokeAll(taskSetA);
        }
        catch(InterruptedException e){
            e.printStackTrace();
            exception2 = true;
        }
        finally{
            executorService.shutdown();
            if(exception2){
                throw new DatabaseException("An error occurred while parsing the data");
            }
        }
        for(Future<Object[]> future : futures){
            try{
                    Object[] objects = future.get();
                    String temp = (String)objects[1];
                    switch(temp){
                        case "grains":
                        wholeGrains = (FoodList)objects[0];
                        case"fruitVeggies":
                        fruitVeggies = (FoodList)(objects)[0];
                        case"protein":
                        protein = (FoodList)objects[0];
                        case"other":
                        other = (FoodList)objects[0];
                    }
            }
            catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
                throw new DatabaseException("An error occurred while handling the parallel proccessed food list");
            }
        }
        */
    
        
        int gNeeds = 0;
        int fvNeeds = 0;
        int pNeeds = 0;
        int oNeeds = 0;
        int calNeeds = 0;

        for(Client c : hamper.getClients()){
            gNeeds += c.getGrains()*7;
            fvNeeds+= c.getFruitVeggies()*7;
            pNeeds += c.getProtein()*7;
            oNeeds += c.getOther()*7;
            calNeeds+=c.getCalories()*7;
        };
        final int limitGCOverflow = 1000;
        final int limitFVCOverflow = 1000;
        final int limitPCOverflow = 2000;
        final int limitOCOverflow = 1000;

        int grainCals = foodList.getGrainContent();
        int fvCals = foodList.getFruitVeggiesContent();
        int proteinCals = foodList.getProteinContent();
        int otherCals = foodList.getOtherContent();
        int array[] = {grainCals, fvCals, proteinCals, otherCals};
            //while list is not optimal
        grainCals = foodList.getGrainContent();
        fvCals = foodList.getFruitVeggiesContent();
        proteinCals = foodList.getProteinContent();
        otherCals = foodList.getOtherContent();
        int max = 0;

        for(int i : array){
            if(max < i){
                max = i;
            }
        }
        int grainOverflow = foodList.getGrainContent() - gNeeds;
        int fvOverflow = foodList.getFruitVeggiesContent() - fvNeeds;
        int proOverflow = foodList.getProteinContent() - pNeeds;
        int otherOverflow = foodList.getOtherContent() - oNeeds;
        int calorieOverlow = foodList.getTotalCalories() - calNeeds;

        FoodItem maxItem = new FoodItem(0,"none",0,0,0,0,0); //a dummy item.
        grainsList.sort(byCalorie);
        proteinList.sort(byCalorie);
        fruitVeggieList.sort(byCalorie);
        otherList.sort(byCalorie);
        if(max == grainCals){
            for(FoodItem item: grainsList){
                if(maxItem.compareTo(item) < 0){maxItem = item;}
            }
            foodList.removeFoodItem(maxItem);
            //go fish;

        }else if(max == fvCals){
            for(FoodItem item: grainsList){
                if(maxItem.compareTo(item) < 0){maxItem = item;}
            }
            foodList.removeFoodItem(maxItem);
            //go fish;
        }else if(max == proteinCals){
            for(FoodItem item: proteinList){
                if(maxItem.compareTo(item) < 0){maxItem = item;}
            }
            foodList.removeFoodItem(maxItem);
            //go fish;
        }else{
            for(FoodItem item: proteinList){
                if(maxItem.compareTo(item) < 0){maxItem = item;}
            }
            foodList.removeFoodItem(maxItem);  
            //go fish
        }
    }

    public Hamper createHamper(ArrayList<Client> clients)throws SQLException, DatabaseException{
        FoodList foodList = createHamperFoodList(clients);
        Hamper hamper = new Hamper(clients,foodList);
        var ptr = foodList.toArrayList();
        for(FoodItem item: ptr){
            removeFromInventory(item,false);
        }
        optimizeHamperItems(hamper);
        return hamper;
    }
    public Hamper createHamper(ArrayList<Client> clients, ArrayList<FoodItem> foodList)throws SQLException, DatabaseException{
        FoodList foodList2 = new FoodList(foodList);
        var ptr = foodList2.toArrayList();
        Hamper hamper = new Hamper(clients,foodList2);
        for(FoodItem item: ptr){
            removeFromInventory(item,false);
        }
        //optimizeHamperItems(hamper);
        return hamper;
    }
} 

