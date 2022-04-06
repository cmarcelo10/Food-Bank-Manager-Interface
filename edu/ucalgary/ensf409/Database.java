package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;
/**
 * @version 1.5
 * @since 1.3
 * Part of the package {@code edu.ucalgary.ensf409}. Handles any connections with an SQL database.
 * Exists as an intermediate between the user and the database to maintain security
 * and integrity.
 */
public class Database extends Thread{
    private String username;
    private String url;
    private String password;
    private Connection dbConnect;
    private ResultSet results;
    private FoodList inventory;
    private int sortKey;
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
        boolean status = true;
        boolean updateStatus = true;
        ArrayList<FoodItem> pointer = inventory.getFoodList();
        try{
            try{
                dbConnect = DriverManager.getConnection(url, username, password);
                String query = "DELETE FROM AVAILABLE_FOOD WHERE Name='"+foodItem.getName()+"'";
                PreparedStatement stmt = dbConnect.prepareStatement(query);
                stmt.execute();
                if(update == true){
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
                status = false;
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
            return this.inventory.getFoodList();
        }
        else{return pointer;}
    }
    private ArrayList<FoodItem> resolveConflicts(ArrayList<ArrayList<FoodItem>> listOfLists){
        return null;
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
        this.sortKey = key;
        var foodList = this.inventory.getFoodList();
        QuickSort(foodList, 0, foodList.size()-1,key);
    }
    /**
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
    public FoodItem binarySearch(int sortKey, int searchKey) throws DatabaseException{
        if(this.sortKey != sortKey || sortKey == 0){
            QuickSort(inventory.getFoodList(), 0, inventory.getFoodList().size()-1, sortKey);
            this.sortKey = sortKey;
        }
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
    public FoodList getLeastWasteful(ArrayList<Client> clients) throws DatabaseException, SQLException{
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
        int grains=totalGrainNeeds*7;
        int fruitVeggies=totalFVNeeds*7;
        int protein=totalProteinNeeds*7;
        int other=totalOtherNeeds*7;
        int calories=totalCalories*7;
        FoodItem itemA = null;
        FoodItem itemB = null;
        FoodItem itemC = null;
        FoodItem itemD = null;
        //Multithreading:
        //May not be possible because of the sorting of the array.
        /*
        while(calories>0){
            FoodItem foodItem = searchByValue("Calories", calories);
            foodList.addFoodItem(foodItem);
            calories -= foodItem.getCalories();
            grains -=foodItem.getGrainContent();
            fruitVeggies -=foodItem.getFruitVeggiesContent();
            protein -= foodItem.getProteinContent();
            other -= foodItem.getOtherContent();
            removeFromInventory(foodItem,true);
        }
        */
        //double searchFactor = 1.00-(float)(1.5000/(float)(clients.size()));
        
        //Search for four food items, and add only then 
        int ticker = 1;
        while(grains > 50 || protein> 50 || other > 50 || fruitVeggies > 50){
            double searchFactor = 1.10 - Math.random();
            System.out.println("Current search factor: " + searchFactor);
            if(grains > 50){
                itemA = searchByValue("grain content", (int)Math.round((float)grains*searchFactor*searchFactor));
                foodList.addFoodItem(itemA);
                this.inventory.setFoodList(removeFromInventory(itemA,true)); //!!
                grains -=itemA.getGrainContent();
                fruitVeggies -=itemA.getFruitVeggiesContent();
                protein -=itemA.getProteinContent();
                other -=itemA.getOtherContent();
            }
           else if(protein > 50){
                itemB = searchByValue("protein content", (int)Math.round((float)protein*searchFactor*searchFactor));
                foodList.addFoodItem(itemB);
                this.inventory.setFoodList(removeFromInventory(itemB, false)); //!
                grains -=itemB.getGrainContent();
                fruitVeggies -=itemB.getFruitVeggiesContent();
                protein -=itemB.getProteinContent();
                other -=itemB.getOtherContent();
            }
            else if(fruitVeggies > 50){
                itemC = searchByValue("fruit veggies content",(int)Math.round((float)(fruitVeggies)*searchFactor*searchFactor));
                foodList.addFoodItem(itemC);
                this.inventory.setFoodList(removeFromInventory(itemC,true)); //!!
                grains -=itemC.getGrainContent();
                fruitVeggies -=itemC.getFruitVeggiesContent();
                protein -=itemC.getProteinContent();
                other -=itemC.getOtherContent();

            }else{
                itemD = searchByValue("other content",(int)Math.round((float)other*searchFactor*searchFactor));
                foodList.addFoodItem(itemD);
                this.inventory.setFoodList(removeFromInventory(itemD,true)); //!!
                grains -=itemD.getGrainContent();
                fruitVeggies -=itemD.getFruitVeggiesContent();
                protein -=itemD.getProteinContent();
                other -=itemD.getOtherContent();
            }
        }
        return foodList;
    }

    //private FoodItem decisionAlgorithm(FoodItem itemA, FoodItem itemB, FoodItem itemC, FoodItem itemD){

    public Hamper createHamper(ArrayList<Client> clients)throws SQLException, DatabaseException{
        FoodList foodList = getLeastWasteful(clients);
        Hamper hamper = new Hamper(clients,foodList);
        return hamper;
    }
} 

