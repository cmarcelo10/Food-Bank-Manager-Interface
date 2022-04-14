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
    private volatile FoodList inventory;
    private volatile ArrayList<FoodItem> grainsList;
    private volatile ArrayList<FoodItem> proteinList;
    private volatile ArrayList<FoodItem> fruitVeggieList;
    private volatile ArrayList<FoodItem> otherList;
    protected volatile Comparator<FoodItem> byProtein;
    protected volatile Comparator<FoodItem> byWholeGrains;
    protected volatile Comparator<FoodItem> byFruitVeggies;
    protected volatile Comparator<FoodItem> byOther;
    protected volatile Comparator<FoodItem> byCalorie;
    public final HashMap<Integer,FoodItem> itemLookupTable;
    public Database(String url, String user, String password)
    throws DatabaseException, SQLException{
        this.url = url;
        this.username = user;
        this.password = password;
        this.byCalorie = Comparator.comparing(item -> item.getCalories());
        this.byFruitVeggies = Comparator.comparing(item -> item.getFruitVeggiesContent());
        this.byOther = Comparator.comparing(item -> item.getOtherContent());
        this.byWholeGrains = Comparator.comparing(item -> item.getGrainContent());
        this.byProtein = Comparator.comparing((item -> item.getProteinContent()));
        this.grainsList = new ArrayList<FoodItem>();
        this.proteinList = new ArrayList<FoodItem>();
        this.fruitVeggieList = new ArrayList<FoodItem>();
        this.otherList = new ArrayList<FoodItem>();
        this.itemLookupTable = new HashMap<>();
        
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
        System.out.println(inventory.toArrayList().size());
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
    public FoodList getAvailableFoodList() throws SQLException{
        updateAvailableFood();
        return this.inventory;
    }
    /**

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
                    FoodItem item = new FoodItem(itemid, name, grainContent, 
                    proteinContent, fvContent, otherContent, calories);
                    list.addFoodItem(item);
                    itemLookupTable.putIfAbsent(itemid, item);
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
     * A complicated method that attempts to find the best combination of items for the clients;
     * Lines 560 - 563 inclusively hold the adjustment parameters for overflow limits on each 
     * nutrient type;

     * @param clients
     * @return a roughed out foodlist that is somewhat the least wasteful
     * @throws DatabaseException
     * @throws SQLException
     */
    public FoodList createHamperFoodList(ArrayList<Client> clients) throws DatabaseException{
        if(clients.isEmpty()){
            System.err.println("Clients list cannot be empty");
            return null;
        }
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
        boolean validGrainsNeeds = totalGrainNeeds < inventory.getGrainContent();
        boolean validProteinNeeds = totalProteinNeeds < inventory.getProteinContent();
        boolean validFruitVeggiesNeeds = totalFVNeeds < inventory.getFruitVeggiesContent();
        boolean validOtherNeeds = totalOtherNeeds < inventory.getOtherContent();
        boolean validCalorieNeeds = totalCalories < inventory.getTotalCalories();
        if(!validCalorieNeeds || !validGrainsNeeds || !validFruitVeggiesNeeds || !validProteinNeeds ||!validOtherNeeds){
            System.err.println("Clients' needs cannot possibly be fulfilled");
            return null;
        }
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
        int q1 = Math.round((inventory.toArrayList().size())/4);
        int q2 = Math.round((inventory.toArrayList().size())/2);
        int q3 = Math.round(3*(inventory.toArrayList().size())/4);
        int q4 = inventory.toArrayList().size();
        Object[] availableFoodItems = inventory.toArrayList().subList(0,q1).toArray();
        Object[] availableFoodItems2 = inventory.toArrayList().subList(q1,q2).toArray();
        Object[] availableFoodItems3 = inventory.toArrayList().subList(q2,q3).toArray();
        Object[] availableFoodItems4 = inventory.toArrayList().subList(q3,q4).toArray();
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        Callable<Void> subsort1 = new Callable<Void>(){
            public Void call(){
                for(Object foodItem : availableFoodItems){
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
                }
                return null;
            }
        };
        callables.add(subsort1);
        Callable<Void> subsort2 = new Callable<Void>(){
            public Void call(){
                for(Object foodItem : availableFoodItems2){
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
                }
                return null;
            }
        };
        callables.add(subsort2);
        Callable<Void> subsort3 = new Callable<Void>(){
            public Void call(){
                for(Object foodItem : availableFoodItems3){
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
                }
                return null;
            }
        };
        callables.add(subsort3);
        Callable<Void> subsort4 = new Callable<Void>(){
            public Void call(){
                for(Object foodItem : availableFoodItems4){
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
                }
                return null;
            }
        };
        callables.add(subsort4);
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
            try{
                executor1.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
            }catch(InterruptedException e){exception1 = true;}
            if(exception1){
                throw new DatabaseException("An error occurred while parsing the data");
            }
        }
        int cpus = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cpus);
        callables = null;
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
            try{
                executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
            }catch(InterruptedException e){
                throw new DatabaseException("an unknown error occured while handling the request");
            }
            if(exceptionCaught){
                throw new DatabaseException("an unknown error occured while handling the request");
            }
        }
        //brute-force find food items that fill the needs gap
        //TODO: add a "worst-case" mode that attempts to brute force the hamper if the needs cannot be met.
        cpus = Runtime.getRuntime().availableProcessors();
        int failsafe = 0;
        int overrideLimits = 0;
        int sizeCheck = Integer.MAX_VALUE;
        while(!needsMet && failsafe < Short.MAX_VALUE){ //TOP of the loop :)
            failsafe++;
            if(foodList.toArrayList().size() == sizeCheck){ //check if the size has changed
                overrideLimits++;
            }else{
                overrideLimits = 0;
            }
            boolean manualOverride = false;
            if(overrideLimits == 15){
                manualOverride = true;
                overrideLimits = 0;
            }
            sizeCheck = foodList.toArrayList().size();
            ExecutorService taskPool = Executors.newFixedThreadPool(cpus);
            List<Parser> tasks = new ArrayList<Parser>();
            if((grains < totalGrainNeeds && grainsList.isEmpty() == false)){
                tasks.add(new Parser(grainsList,"grains", (totalGrainNeeds - grains)));
            }
            if(proteinList.isEmpty() == false){
                tasks.add(new Parser(proteinList,"protein",(totalProteinNeeds - protein)));
            }
            if(fruitVeggieList.isEmpty()==false){
                tasks.add(new Parser(fruitVeggieList,"fruit veggies",(totalFVNeeds - fruitVeggies)));
            }
            if(otherList.isEmpty() == false || (manualOverride && otherList.isEmpty() == false)){
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
                try{
                    taskPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                }catch(InterruptedException e){
                    throw new DatabaseException("an unknown error occured while handling the request");
                }
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
            // Column (0, 1, 2, 3,   4   )

            int acceptableGCOverflow = 0; //individually adjust these parameters
            int acceptableFVCOverflow = 0; // to set overflow limits for each content type
            int acceptablePCOverflow = 0;
            int acceptableOCOverflow = 0;

            //Determine if an item should or should not be added to the FoodList
            //The algorithm has the power to "ignore" bad options making it more efficient.
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
                    calories +=item.getCalories();
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
            //the INTENT was to have this complex table determine which item to add
            //if none of them are ideal. However I never finished this.
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
            /*
            In the best case, all items are removed from the list of options
            In the worst case, NO items are remove from the list of options, which has the 
            potential to create an infinite loop.
           
            Currently this if-statement checks if the the "options" list
            is empty, and if not, it adds the food items anyways.

            What should happen is
            */
            if(options.size() > 0){
                gMet = grains >= totalGrainNeeds;
                pMet = protein >=totalProteinNeeds;
                fMet = fruitVeggies >= totalFVNeeds;
                oMet = other >= totalOtherNeeds;
                if(!pMet){
                    options.sort(byProtein);
                }else if(!fMet){
                    options.sort(byFruitVeggies);
                }
                else if(!gMet){
                    options.sort(byOther);
                }
                else{
                    options.sort(byWholeGrains);
                }
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
            gMet = grains >= totalGrainNeeds;
            pMet = protein >=totalProteinNeeds;
            fMet = fruitVeggies >= totalFVNeeds;
            oMet = other >= totalOtherNeeds;
            if(gMet && pMet && fMet && oMet)
            {needsMet = true;}
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

        }//bottom of the while loop;   
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
            case "grains":
                temp = 6;
                return temp;
            case "fruit veggies":
                temp = 2;
                return temp;
            case "protein":
                temp = 4;
                return temp;
            case "other":
                temp = 5;
                return temp;
        }
        throw new IllegalArgumentException("Invalid search key argument " + key);
    }

    public void optimize(Hamper hamper) throws DatabaseException{
        final ArrayList<FoodItem> remainingItems = inventory.toArrayList();
        final ArrayList<FoodItem> hamperItems = hamper.getFoodList().toArrayList();
        remainingItems.sort(byCalorie);
        hamperItems.sort(byCalorie);
        hamperItems.forEach(item -> item.setIfAdded(true));
        remainingItems.forEach(item -> item.setIfAdded(false));
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
        int i = 0;
        while(i < hamperItems.size()){
            int dg = currentGrain - totalGrainNeeds;
            int df = currentFV - totalFVNeeds;
            int dp = currentProtein - totalProteinNeeds;
            int dx = currentOther -totalOtherNeeds;
            FoodItem item = hamperItems.get(i);
            int[] arr = this.getAllItemData(item);
            //15 possible states
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
                    hamperItems.remove(item);
                    inventory.addFoodItem(item);
                    currentOther-=item.getOtherContent();
                }else if(arr[5] < dx+Math.round(0.25*dx)){
                    this.inventory.toArrayList().sort(byOther);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("other"), 
                    Math.abs(currentOther-arr[5]));
                    if((pointerItem.getOtherContent()- arr[5]) >=0){
                        hamperItems.remove(item);
                        inventory.removeFoodItem(pointerItem);
                        hamperItems.add(pointerItem);
                        inventory.addFoodItem(item);
                        currentOther -= item.getOtherContent();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                    }
                }
            }
            //protein
            else if(arr[2] == 0 && arr[3] ==0 && arr[4] != 0 && arr[5] == 0){
                if(arr[4] < dp){
                    hamperItems.remove(item);
                    inventory.addFoodItem(item);
                    currentProtein -=item.getProteinContent();
                }
                else if(arr[4] < dp+Math.round(0.25*dp)){
                    this.inventory.toArrayList().sort(byProtein);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("protein content"), 
                    Math.abs(currentProtein-arr[4]));
                    if((pointerItem.getProteinContent()- arr[4]) >=0){
                        hamperItems.remove(item);
                        inventory.removeFoodItem(pointerItem);
                        hamperItems.add(pointerItem);
                        inventory.addFoodItem(item);
                        currentProtein -= item.getProteinContent();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                    }
                }
            }
            //fruit veggies
            else if(arr[2] == 0 && arr[3] != 0 && arr[4] == 0 && arr[5] ==0){
                if(arr[3] < df){
                    hamperItems.remove(item);
                    inventory.addFoodItem(item);
                    currentFV -= item.getFruitVeggiesContent();
                }else if(arr[3] < dp+Math.round(0.25*dp)){
                    FoodItem pointerItem = binarySearch(stringToNumericKey("fruit veggies"), 
                    Math.abs(currentFV-arr[3]));
                    if((pointerItem.getFruitVeggiesContent()- arr[3]) >=0){
                        hamperItems.remove(item);
                        inventory.removeFoodItem(pointerItem);
                        hamperItems.add(pointerItem);
                        inventory.addFoodItem(item);
                        currentFV -= item.getFruitVeggiesContent();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
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
                    //}
            //whole grains;
            }
            else if(arr[2] !=0 && arr[3] == 0 && arr[4] == 0 && arr[5] == 0){
                if(arr[2] < dg){
                    hamperItems.remove(item);
                    inventory.addFoodItem(item);
                    currentGrain -= item.getGrainContent();
                }else if(arr[2] < dg+Math.round(0.5*dg)){
                    this.inventory.toArrayList().sort(byWholeGrains);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("grain content"), 
                    Math.abs(currentGrain-arr[2]));
                    if((pointerItem.getGrainContent()- arr[2]) >=0){
                        hamperItems.remove(item);
                        inventory.removeFoodItem(pointerItem);
                        hamperItems.add(pointerItem);
                        inventory.addFoodItem(item);
                        currentGrain -= item.getGrainContent();
                        currentFV += pointerItem.getFruitVeggiesContent();
                        currentCalories +=pointerItem.getCalories();
                        currentGrain += pointerItem.getGrainContent();
                        currentProtein += pointerItem.getProteinContent();
                        currentOther += pointerItem.getOtherContent();
                    }
                }
            }
            i++;
        }
        hamper.setFoodList(new FoodList(hamperItems));
    }
    public ArrayList<FoodItem> bruteForceAlgorithm(){
        return null;
    }
    public Hamper createHamper(ArrayList<Client> clients)throws SQLException, DatabaseException{
        FoodList foodList = null;
        ArrayList<Hamper> listOfHampers = new ArrayList<>();
        Hamper hamper = null;
        int i = 0;
        while(i < 4){
            foodList = new FoodList();
            if(clients.size() > 12){
                System.err.println("Too many clients specified at once");
                return null;
            }
            else if(clients.isEmpty()){
                System.err.println("Clients list cannot be empty");
                return null;
            }
            else{
                foodList = createHamperFoodList(clients);
            }
            listOfHampers.add(new Hamper(clients,foodList));
            i++;
        }
        Comparator<Hamper> byOverflow = Comparator.comparing(item -> item.getTotalCalories());
        listOfHampers.sort(byOverflow);
        hamper = listOfHampers.get(0);
        var ptr = foodList.toArrayList();
        for(FoodItem item: ptr){
            removeFromInventory(item,false);
        }
        hamper.printSummary();
        optimize(hamper);
        return hamper;
    }
    public ArrayList<Client> createClients(int adultMale, int adultFemale, int childOver8, int childUnder8){
        //Method is only responsible for:
        //Validating arguments
        //Converting arguments to string
        //Checking whether a valid number of clients has been specified is not this method's job.
        ArrayList<Client> clientList = new ArrayList<>();
        try{
            if(adultMale !=0){
                for(int i = 0; i < adultMale; i++){
                    clientList.add(createClient("Adult Male"));
                }
            }
            if(adultFemale !=0){
                for(int i = 0; i < adultMale; i++){
                    clientList.add(createClient("Adult Female"));
                }
            }
            if(childOver8 !=0){
                for(int i = 0; i < childOver8; i++){
                    clientList.add(createClient("Child over 8"));
                }
            }
            if(childUnder8 !=0){
                for(int i = 0; i < childUnder8; i++){
                    clientList.add(createClient("Child under 8"));
                }
            }
            return clientList;
        }catch(SQLException exception){
            exception.printStackTrace();
            return null;
        }
    }
    /**
     * An internal helper method, that gets the nutritional attributes of the food item
     * and returns them as an array of {@code int} literals such that:<br></br>
     * <br> {@code a[0] ->} <b>ItemID</b> </br>
     * <br> {@code a[1] ->} <b>Calorie Amount</b></br>
     * <br> {@code a[2] ->} <b>Whole Grain Amount</b></br>
     * <br> {@code a[3] ->} <b>Fruit Veggie Amount</b></br>
     * <br> {@code a[4] ->} <b>Protein Amount</b></br>
     * <br> {@code a[5] ->} <b>Other Amount</b></br>
     * @param item is the item to obtain data about
     * @return an {@code int} array containing information about the item
     */
    public int[] getAllItemData(FoodItem item){
        int[] a = new int[6];
        a[0] = item.getItemID();
        a[1] = item.getCalories();
        a[2] = item.getGrainContent();
        a[3] = item.getFruitVeggiesContent();
        a[4] = item.getProteinContent();
        a[5] = item.getOtherContent();
        return a;
    }
  
} 

