package edu.ucalgary.ensf409;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
/**
 * Copyright Carter Marcelo 2022
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
    protected volatile FoodList inventory;
    protected volatile Comparator<FoodItem> byProtein;
    protected volatile Comparator<FoodItem> byWholeGrains;
    protected volatile Comparator<FoodItem> byFruitVeggies;
    protected volatile Comparator<FoodItem> byOther;
    protected volatile Comparator<FoodItem> byCalorie;
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
    public FoodList getAvailableFoodList() throws SQLException{
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
                    int prevSize = pointer.size();
                    pointer.remove(foodItem);
                    //if deletion was unsuccessful delete the hard way.
                    if(pointer.size()==prevSize){
                        int i = 0;
                        while(i < pointer.size()){
                            if(pointer.get(i).getItemID() == foodItem.getItemID()){
                                pointer.remove(pointer.get(i));
                                break;
                            }
                            i++;
                        }
                    }
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
        else{
            this.inventory = new FoodList(pointer);
            return(pointer);
        
        }
    }
    /**
     * Updates the FoodList contained by the inventory to reflect 
     * the state of the SQL database
     * @return {@code true} if the update was successful; {@code false} otherwise.
     * @throws SQLException
     */
    private boolean updateAvailableFood() throws SQLException{
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
     * Translates a String key into the appropriate numeric value for the system.
     * Does not require a database to be initialized.
     * @param key is the String key to be converted into a numeric key.<br></br>
     * <br>{@code ItemID} returns 1 </br> 
     * <br>{@code fruit veggies content} or {@code fruit veggies} returns 2 </br>
     * <br>{@code grains} or {@code grain content} returns 3 </br>
     * <br>{@code protein} or {@code protein content} returns 4 </br>
     * <br>{@code other} or {@code other content} returns 5</br>
     * <br>{@code calories} returns 6 </br>
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
    /**
     * 
     * @param tableOfDeficits is the table of deficits in the client needs
     * @return
     */
    private String createAlertTooManyClients(int [][] tableOfDeficits){
        int wgDeficit = tableOfDeficits[0][1] - tableOfDeficits[0][0];
        int fvDeficit = tableOfDeficits[1][1] - tableOfDeficits[1][0];
        int pDeficit = tableOfDeficits[2][1] - tableOfDeficits[2][0];
        int oDeficit = tableOfDeficits[3][1] - tableOfDeficits[3][0];
        int calDeficit = tableOfDeficits[4][1] - tableOfDeficits[4][0];
        int[] temp = {calDeficit,wgDeficit,fvDeficit,-pDeficit,oDeficit};
        String newline = "\n";
        String indicator = " <-";
        StringBuilder builder = new StringBuilder();
        String stringA = String.format("Calories: %13d",calDeficit);
        String stringB = String.format("Whole Grains: %9d",wgDeficit);
        String stringC = String.format("Fruit Veggies: %8d",fvDeficit);
        String stringD = String.format("Protein: %14d",pDeficit);
        String stringE = String.format("Other: %16d",oDeficit);
        String[] msgs = {stringA, stringB, stringC, stringD, stringE};
        builder.append(newline);
        for(int i = 0; i < temp.length; i++){
            builder.append(msgs[i]);
            if(temp[i] < 0){
                builder.append(indicator);
            }
            builder.append(newline);
        }
        return builder.toString();
    }
    /**
     * Generates a FoodList object which fulfills the needs of the clients provided
     * as input. The filling algorithm prioritizes filling the clients' needs. While it 
     * aims to minimize surplus, it is not guaranteeed to return an optimal combination of items.
     * However, any non-null 
     * FoodList object returned by this method is guaranteed to fulfill the clients' needs
     * @param clients the ArrayList of Clients with needs to be fulfilled
     * @return a foodList that meets the clients needs
     * @throws DatabaseException
     * @throws SQLException
     */
    private FoodList createHamperFoodList(ArrayList<Client> clients) throws DatabaseException{
        if(clients.isEmpty()){
            System.err.println("List of clients cannot be empty!");
            return null;
        }
        ArrayList<FoodItem> grainsList = new ArrayList<>();
        ArrayList<FoodItem> proteinList = new ArrayList<>();
        ArrayList<FoodItem> fruitVeggieList = new ArrayList<>();
        ArrayList<FoodItem> otherList = new ArrayList<>();
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
            System.err.println(
                "Warning: The current nutritional content of the inventory is insufficient to fulfill the request!");
            int[][] deficits = 
            {{totalGrainNeeds, inventory.getGrainContent()},
            {totalFVNeeds, inventory.getFruitVeggiesContent()},
            {totalProteinNeeds, inventory.getProteinContent()},
            {totalOtherNeeds, inventory.getOtherContent()},
            {totalCalories, inventory.getTotalCalories()}};
            String message = createAlertTooManyClients(deficits);
            System.err.println(message);
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
                executor1.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
            }catch(InterruptedException e){exception1 = true;}
            if(exception1){
                throw new DatabaseException("An error occurred while parsing the data");
            }
        }
        int cpus = Runtime.getRuntime().availableProcessors();
        try{
            ExecutorService executor = Executors.newFixedThreadPool(cpus);
            callables = null;
            callables = new ArrayList<Callable<Void>>();
            callables.add(new Callable<Void>(){public Void call(){proteinList.sort(byProtein); return null;}});
            callables.add(new Callable<Void>(){public Void call(){grainsList.sort(byWholeGrains); return null;}});
            callables.add(new Callable<Void>(){public Void call(){fruitVeggieList.sort(byFruitVeggies);return null;}});
            callables.add(new Callable<Void>(){public Void call(){ otherList.sort(byOther);return null;}});
            cpus = Runtime.getRuntime().availableProcessors();
                try{
                    executor.invokeAll(callables);
                }
                catch(Exception e){
                    e.printStackTrace();
                    exceptionCaught = true;
                }
                finally{
                    executor.shutdown();
                    try{
                        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
                    }catch(InterruptedException e){
                        throw new DatabaseException("an unknown error occured while handling the request");
                    }
                    if(exceptionCaught){
                        throw new DatabaseException("an unknown error occured while handling the request");
                    }
                }
        }
        catch(Exception e){
            proteinList.sort(byProtein);
            grainsList.sort(byWholeGrains);
            fruitVeggieList.sort(byFruitVeggies);
            otherList.sort(byOther);
        }
        int failsafe = 0;
        int overrideLimits = 0;
        int sizeCheck = Integer.MAX_VALUE;
        while(!needsMet && failsafe < Integer.MAX_VALUE){ //TOP of the loop :)
            failsafe++;
            if(foodList.toArrayList().size() == sizeCheck){ //check if the size has changed
                overrideLimits++;
            }else{
                overrideLimits = 0;
            }
            boolean manualOverrideOther = false;
            boolean manualOverrideGrains = false;
            boolean manualOverrideProtein = false;
            boolean manualOverrideFruitVeggies = false;
            //one by one override limits until the loop continues forwards;
            if(overrideLimits > 2){
                manualOverrideOther = true;
                if(overrideLimits > 3){
                    manualOverrideGrains = true;
                    if(overrideLimits > 4){
                        manualOverrideFruitVeggies = true;
                        if(overrideLimits > 5){
                            manualOverrideProtein = true;
                        }
                    }
                }
            }
            sizeCheck = foodList.toArrayList().size();
            ExecutorService taskPool = Executors.newFixedThreadPool(cpus);
            List<Parser> tasks = new ArrayList<Parser>();
            if(((grains < totalGrainNeeds && grainsList.isEmpty() == false) || 
            (manualOverrideGrains && grainsList.isEmpty() == false))){
                tasks.add(new Parser(grainsList,"grains", (totalGrainNeeds - grains)));
            }
            if((protein < totalProteinNeeds) && proteinList.isEmpty() == false || 
            (manualOverrideProtein && proteinList.isEmpty() == false)){
                tasks.add(new Parser(proteinList,"protein",(totalProteinNeeds - protein)));
            }
            if(((fruitVeggies < totalFVNeeds) && (fruitVeggieList.isEmpty()==false)) || 
            (manualOverrideFruitVeggies && fruitVeggieList.isEmpty() == false)){
                tasks.add(new Parser(fruitVeggieList,"fruit veggies",(totalFVNeeds - fruitVeggies)));
            }
            if(((other < totalOtherNeeds) && (otherList.isEmpty() == false)) || 
            (manualOverrideOther && otherList.isEmpty() == false)){
                tasks.add(new Parser(otherList,"other",totalOtherNeeds - other));
            }
            List<Future<FoodItem>> futures = null;
            ArrayList<FoodItem> options = new ArrayList<FoodItem>();
            try{
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
                //Add the parsed FoodItems from each thread
                for(int p = 0; p < futures.size(); p++){
                    Future<FoodItem> results = futures.get(p);
                    try{
                        if(results != null){
                        FoodItem item = results.get();
                            if(item != null){
                                options.add(item);
                            }
                        }
                    }
                    catch(InterruptedException | ExecutionException e){
                        e.printStackTrace();
                    }
                }
            }
            //Vanilla, iterative implementation
            catch(Exception e){
                e.printStackTrace();
                if(((grains < totalGrainNeeds && grainsList.isEmpty() == false) || 
                (manualOverrideGrains && grainsList.isEmpty() == false))){
                options.add((new Parser(grainsList,"grains", (totalGrainNeeds - grains))).call());
                }
                if((protein < totalProteinNeeds) && proteinList.isEmpty() == false || 
                (manualOverrideProtein && proteinList.isEmpty() == false)){
                    options.add((new Parser(proteinList,"protein",(totalProteinNeeds - protein))).call());
                }
                if(((fruitVeggies < totalFVNeeds) && (fruitVeggieList.isEmpty()==false)) || 
                (manualOverrideFruitVeggies && fruitVeggieList.isEmpty() == false)){
                    options.add((new Parser(fruitVeggieList,"fruit veggies",(totalFVNeeds - fruitVeggies))).call());
                }
                if(((other < totalOtherNeeds) && (otherList.isEmpty() == false)) || (manualOverrideOther && otherList.isEmpty() == false)){
                   options.add((new Parser(otherList,"other",totalOtherNeeds - other)).call());
                }
            }
            int k = 0;
            int x = options.size();
            int y = 5;
            boolean[][] table = new boolean[x][y];
            //x++ = increment food item
            //y++ = increment field for item 

            // Field  (G, F, P, O, added?)
            // Column (0, 1, 2, 3,  4  )

            int acceptableGCOverflow = 0; //individually adjust these parameters
            int acceptableFVCOverflow = 0; // to set overflow limits for each content type
            int acceptablePCOverflow = 0;
            int acceptableOCOverflow = 0;
            //!!
            //Determine if an item should or should not be added to the FoodList
            //The algorithm has the power to "ignore" bad options making it more efficient.
            try{
                options.sort(byCalorie);
            }catch(NullPointerException e){
                e.printStackTrace();
                System.err.println("Unknown failure while handling data");
            }
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
                if(clients.size() > 5){
                    //Reversing the order of the list 
                    //appeared to help for large amounts of clients.
                    Collections.reverse(options);
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
                System.err.println("Client needs cannot be fulfilled!");
                break;
            }

        }//bottom of the while loop;
        return foodList;
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
     * @param remainingItems the list of {@code FoodItem} objects to be sorted
     * @return an arraylist containing all of the lists.
     */
    public ArrayList<ArrayList<FoodItem>> splitIntoSublistsByContent(ArrayList<FoodItem>remainingItems){
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
                remainingItems.parallelStream().forEach(item ->{
                int[] arr = this.getAllItemData(item);
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
                remainingItems.parallelStream().forEach(item -> {
                    int[] arr = this.getAllItemData(item);
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
                remainingItems.forEach(item -> {
                    int[] arr = this.getAllItemData(item);
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
             list.parallelStream().forEach(array -> {if(array.isEmpty() == false){array.sort(byCalorie);}});
        }catch(Exception g){
            list.forEach(array -> {if(array.isEmpty() == false){array.sort(byCalorie);}});
        }
        return list;

    }
    /**
     *A helper method that optimizes the
     * @param hamper
     * @throws DatabaseException
     */
    private void removeSurplusHamperContents(Hamper hamper) throws DatabaseException{
        final ArrayList<FoodItem> remainingItems = inventory.toArrayList();
        final ArrayList<FoodItem> hamperItems = hamper.getFoodList().toArrayList();
        remainingItems.sort(byCalorie);
        hamperItems.sort(byCalorie);
        hamperItems.parallelStream().forEach(item -> item.setIfAdded(false));
        remainingItems.parallelStream().forEach(item -> item.setIfAdded(false));
        hamperItems.sort(byCalorie);
        remainingItems.sort(byCalorie);
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
        ArrayList<ArrayList<FoodItem>> masterList = splitIntoSublistsByContent(remainingItems);
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
        while(i < hamperItems.size() && remainingItems.isEmpty() == false){
            dg = currentGrain - totalGrainNeeds;
            df = currentFV - totalFVNeeds;
            dp = currentProtein - totalProteinNeeds;
            dx = currentOther -totalOtherNeeds;
            FoodItem item = hamperItems.get(i);
            int[] arr = this.getAllItemData(item);
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
                    hamperItems.remove(item);
                    remainingItems.add(item);
                    currentOther-=item.getOtherContent();
                    i++;
                    continue;
                }else{  
                    remainingItems.sort(byOther);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("other"), 
                    Math.abs(currentOther-arr[5]));
                    if((pointerItem.getOtherContent()- arr[5]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        hamperItems.remove(item);
                        item.setIfAdded(false);
                        remainingItems.remove(pointerItem);
                        hamperItems.add(pointerItem);
                        remainingItems.add(item);
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
                        xxxO.sort(byOther);
                        while((totalOtherNeeds - currentOther) > 0 && xxxO.size() > 0){
                            FoodItem replacement = binarySearch(stringToNumericKey("other"),
                            Math.abs(totalOtherNeeds - currentOther),xxxO);
                            replacement.getOtherContent();
                            int u = 0;
                            if(replacement.getOtherContent() < item.getOtherContent()){
                                currentOther += replacement.getOtherContent();
                                hamperItems.add(replacement);
                                replacement.setIfAdded(true);
                                xxxO.remove(replacement);
                                remainingItems.remove(replacement);
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
                                        remainingItems.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    hamperItems.add(item);
                                    bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xxxO.add(foodItem));
                                    currentOther = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                                }
                            }
                        }
                        if(dx < 0){
                            remainingItems.remove(item);
                            hamperItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
                    currentProtein -=item.getProteinContent();
                    i++;
                    continue;
                }
                else{  
                    remainingItems.sort(byProtein);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("protein"), 
                    Math.abs(currentProtein-arr[4]));
                    if((pointerItem.getOtherContent()- arr[4]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        hamperItems.remove(item);
                        item.setIfAdded(false);
                        remainingItems.remove(pointerItem);
                        hamperItems.add(pointerItem);
                        remainingItems.add(item);
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
                        xxPx.sort(byProtein);
                        while((totalProteinNeeds - currentProtein) > 0 && xxPx.size() > 0){
                            FoodItem replacement = binarySearch(stringToNumericKey("protein"),
                            Math.abs(currentProtein),xxPx);
                            replacement.getProteinContent();
                            int u = 0;
                            if(replacement.getProteinContent() < item.getProteinContent()){
                                currentProtein += replacement.getProteinContent();
                                hamperItems.add(replacement);
                                replacement.setIfAdded(true);
                                xxPx.remove(replacement);
                                remainingItems.remove(replacement);
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
                                        remainingItems.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    hamperItems.add(item);
                                    bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xxPx.add(foodItem));
                                    currentOther = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                                }
                            }
                        }
                        if(dp < 0){
                            remainingItems.remove(item);
                            hamperItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
                    currentFV -= item.getFruitVeggiesContent();
                    df = currentFV - totalFVNeeds;
                    i++;
                    continue;
                }else{  
                    remainingItems.sort(byFruitVeggies);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("fruit veggies"), 
                    Math.abs(currentFV-arr[3]));
                    if((pointerItem.getFruitVeggiesContent()- arr[3]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        hamperItems.remove(item);
                        item.setIfAdded(false);
                        remainingItems.remove(pointerItem);
                        hamperItems.add(pointerItem);
                        remainingItems.add(item);
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
                        xFxx.sort(byFruitVeggies);
                        while((totalFVNeeds-currentFV) > 0 && xFxx.size() > 0){
                            FoodItem replacement = binarySearch(stringToNumericKey("fruit veggies"),Math.abs(totalFVNeeds-currentFV),xFxx);
                            replacement.getFruitVeggiesContent();
                            int u = 0;
                            if(replacement.getFruitVeggiesContent() < item.getFruitVeggiesContent()){
                                currentFV += replacement.getFruitVeggiesContent();
                                hamperItems.add(replacement);
                                replacement.setIfAdded(true);
                                xFxx.remove(replacement);
                                remainingItems.remove(replacement);
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
                                        remainingItems.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    hamperItems.add(item);
                                    bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                    bufferedItems.forEach(foodItem -> xFxx.add(foodItem));
                                    currentFV = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                                }
                            }
                        }
                        if(df < 0){
                            remainingItems.remove(item);
                            hamperItems.add(item);
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
                    hamperItems.remove(item);
                    remainingItems.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein += item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++; 
                    continue;
                }else{  
                    remainingItems.sort(byWholeGrains);
                    FoodItem pointerItem = binarySearch(stringToNumericKey("grains"), 
                    Math.abs(currentGrain-arr[2]));
                    if((pointerItem.getGrainContent()- arr[2]) < 0){
                        if(item.getIfAdded() == true){
                            i++;
                            continue;
                        }
                        hamperItems.remove(item);
                        item.setIfAdded(false);
                        remainingItems.remove(pointerItem);
                        hamperItems.add(pointerItem);
                        remainingItems.add(item);
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
                        Gxxx.sort(byWholeGrains);
                        while((totalGrainNeeds - currentGrain) > 0 && Gxxx.size() > 0){
                            FoodItem replacement = binarySearch(stringToNumericKey("grain"),Math.abs(totalGrainNeeds - currentGrain),Gxxx);
                            replacement.getGrainContent();
                            int u = 0;
                            if(replacement.getGrainContent() < item.getGrainContent()){
                                currentGrain += replacement.getGrainContent();
                                hamperItems.add(replacement);
                                replacement.setIfAdded(true);
                                Gxxx.remove(replacement);
                                remainingItems.remove(replacement);
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
                                        remainingItems.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    hamperItems.add(item);
                                    bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                    bufferedItems.forEach(foodItem -> Gxxx.add(foodItem));
                                    currentGrain = resetValue;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                                }
                            }
                        }
                        if(dg < 0){
                            remainingItems.remove(item);
                            hamperItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                    i++;
                    continue;
                }else{
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                        String key = "";
                        int diff = 0;
                        if((totalGrainNeeds - currentGrain) > (totalOtherNeeds - currentOther)){
                            diff = totalGrainNeeds - currentGrain;
                            GxxO.sort(byWholeGrains);
                            //pick the larger of the two needs to search for.
                            key = "grain";
                        }else{
                            GxxO.sort(byOther);
                            diff = totalOtherNeeds - currentOther;
                            key = "other";
                        }
                        FoodItem replacement = binarySearch(stringToNumericKey(key),Math.abs(diff),GxxO);
                        replacement.getGrainContent();
                        int u = 0;
                        if(replacement.getGrainContent() < item.getGrainContent() 
                        && replacement.getOtherContent() < item.getOtherContent()){
                            currentGrain += replacement.getGrainContent();
                            currentOther += replacement.getOtherContent();
                            hamperItems.add(replacement);
                            replacement.setIfAdded(true);
                            GxxO.remove(replacement);
                            remainingItems.remove(replacement);
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
                                    remainingItems.remove(alt);
                                    break;
                                }
                                u--;
                            }
                            if(u < 0){
                                //if no item combinations can fulfill the needs
                                //add the item back to the list and continue the overall loop
                                hamperItems.add(item);
                                bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                bufferedItems.forEach(foodItem -> GxxO.add(foodItem));
                                currentGrain = resetValue2;
                                currentOther = resetValue1;
                                break;
                            }else{
                                bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                            }
                        }
                    }
                    dg = currentGrain - totalGrainNeeds;
                    df = currentFV - totalFVNeeds;
                    dp = currentProtein - totalProteinNeeds;
                    dx = currentOther -totalOtherNeeds;
                    if(dg < 0 || dx < 0){
                        remainingItems.remove(item);
                        hamperItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
                    currentGrain -= item.getGrainContent();
                    currentCalories -=item.getCalories();
                    currentFV -= item.getFruitVeggiesContent();
                    currentProtein -= item.getProteinContent();
                    currentOther -= item.getOtherContent();
                }else{
                        hamperItems.remove(item);
                        item.setIfAdded(false);
                        remainingItems.add(item);
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
                                GFxx.sort(byWholeGrains);
                                //pick the larger of the two needs to search for.
                                key = "grain";
                            }else{
                                GFxx.sort(byFruitVeggies);
                                diff = totalFVNeeds - currentFV;
                                key = "fruit veggies";
                            }
                            FoodItem replacement = binarySearch(stringToNumericKey(key),Math.abs(diff),GFxx);
                            int u = 0;
                            if(replacement.getGrainContent() < item.getGrainContent() 
                            && replacement.getFruitVeggiesContent() < item.getFruitVeggiesContent()){
                                currentGrain += replacement.getGrainContent();
                                currentFV += replacement.getFruitVeggiesContent();
                                hamperItems.add(replacement);
                                replacement.setIfAdded(true);
                                GFxx.remove(replacement);
                                remainingItems.remove(replacement);
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
                                        remainingItems.remove(alt);
                                        break;
                                    }
                                    u--;
                                }
                                if(u < 0){
                                    //if no item combinations can fulfill the needs
                                    //add the item back to the list and continue the overall loop
                                    hamperItems.add(item);
                                    bufferedItems.forEach(foodItem -> remainingItems.add(foodItem));
                                    bufferedItems.forEach(foodItem -> GFxx.add(foodItem));
                                    currentGrain = resetValue1;
                                    currentFV= resetValue2;
                                    break;
                                }else{
                                    bufferedItems.forEach(queuedItem -> hamperItems.add(queuedItem));
                                }
                            }
                        }
                        
                        if(dg < 0 || df < 0){
                            remainingItems.remove(item);
                            hamperItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
                    hamperItems.remove(item);
                    item.setIfAdded(false);
                    remainingItems.add(item);
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
        hamper.setFoodList(new FoodList(hamperItems));
        validateHamperContents(hamper);
    }
    private void validateHamperContents(Hamper hamper){
        final var HAMPER_FOODLIST = hamper.getFoodList().toArrayList();
        int previousItemID = -1;
        int i = 0;
        while(i < HAMPER_FOODLIST.size()){
            FoodItem item = HAMPER_FOODLIST.get(i);
            if(item.getItemID() == previousItemID){
                HAMPER_FOODLIST.remove(item);
            }
            previousItemID = item.getItemID();
            i++;
        }
    }
    public ExecutorService getFixedThreadPool(){
        int cpus = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cpus);
        return executorService;
    }
    private Callable<Hamper> createHamperCallable(ArrayList<Client> clients){
        Callable<Hamper> callable = new Callable<Hamper>(){
            public Hamper call(){
                FoodList foodList = null;
                try{
                    foodList = createHamperFoodList(clients);
                    Hamper hamper = new Hamper(clients, foodList);
                    return hamper;
                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return callable;
    }
    private Hamper getHamperWithLeastSurplus(List<Future<Hamper>> hamperOptions){
        ArrayList<Hamper> listOfHampers = new ArrayList<>();
        Comparator<Hamper> byOverflow = Comparator.comparing(item -> item.getTotalCalories());
        for(int p = 0; p < hamperOptions.size(); p++){
            Future<Hamper> hamperOption = hamperOptions.get(p);
            try{
                if(hamperOption != null){
                Hamper item = hamperOption.get();
                    if(item != null){
                        listOfHampers.add(item);
                    }
                }
            }
            catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        }
        try{
            listOfHampers.sort(byOverflow);
        }catch(NullPointerException e){
            e.printStackTrace();
            return new Hamper();
        }
        Hamper hamper = listOfHampers.get(0);
        removeHamperItemsFromLocalInventory(hamper);
        return hamper;

    }
    private boolean removeHamperItemsFromLocalInventory(Hamper hamper){
        int originalInventorySize = inventory.toArrayList().size();
        var pointer = hamper.getFoodList().toArrayList();
        pointer.forEach(item -> {
            try{
                removeFromInventory(item,false);//Should remain false.
            }
            catch(DatabaseException | SQLException exception){
                exception.printStackTrace();
            }
        });
        int newInventorySize = inventory.toArrayList().size();
        int hamperFoodListSize = hamper.getFoodList().toArrayList().size();
        if((newInventorySize + hamperFoodListSize == originalInventorySize)){
            return true;
        }else{return false;}
    }
    private boolean removeAllHamperItemsFromSQLDatabase(Hamper hamper){
        var pointer = hamper.getFoodList().toArrayList();
        var backup = this.inventory;
        try{
            pointer.forEach(item -> {
                try{
                    removeFromInventory(item,false);//Should remain false.
                }
                catch(DatabaseException | SQLException exception){
                    exception.printStackTrace();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            this.inventory = backup;
            return false;
        }
        return true;
    }
    private List<Future<Hamper>> invokeAllHamperAlgorithms(ExecutorService executor,ArrayList<Callable<Hamper>>callables)
    throws DatabaseException{
        int attempts = 0;
        List<Future<Hamper>> hamperOptions = null;
        try{
            while(attempts < 5){
                try{
                hamperOptions = executor.invokeAll(callables);
                break;
                }catch(InterruptedException e){
                    e.printStackTrace();
                    attempts++;
                }
            }
        }finally{
            if(attempts == 5){
                executor.shutdownNow();
            }
            try{
                executor.shutdown();
                executor.awaitTermination(Long.SIZE,TimeUnit.NANOSECONDS);
            }catch(Exception e){
                e.printStackTrace();
                executor.shutdownNow();
                throw new DatabaseException("A fatal error has occurred while processing. Please reboot the program.");
            }
        }
        return hamperOptions;
    }
    /**
     * A backup method to be called if an error occurs while attempting to process hamper
     * options concurrently.
     * @param clients the arrayList of clients with needs to be fulfilled
     * @return
     */
    private Hamper generateHamperFoodListsIteratively(ArrayList<Client> clients) throws DatabaseException, SQLException{
        FoodList foodList = null;
        ArrayList<Hamper> listOfHampers = new ArrayList<>();
        Hamper hamper = null;
        int i = 0;
        while(i < 4){
            foodList = new FoodList();
            if(clients.size() > 10){
                System.err.println("Too many clients specified at once");
                return new Hamper();
            }
            else if(clients.isEmpty()){
                System.err.println("Clients list cannot be empty");
                return null;
            }
            else{
                foodList = createHamperFoodList(clients);
                if(foodList == null){
                    i++;
                    continue;
                }
                //create Hamper foodlist creates the foodlist for the hamper
                //If it encounters a fatal error, it returns null.
            }
            listOfHampers.add(new Hamper(clients,foodList));
            i++;
        }
        Comparator<Hamper> byOverflow = Comparator.comparing(item -> item.getTotalCalories());
        try{
            listOfHampers.sort(byOverflow);
        }catch(NullPointerException e){
            e.printStackTrace();
            return new Hamper();
        }
        hamper = listOfHampers.get(0);
        var ptr = foodList.toArrayList();
        ptr.forEach(item -> {
            try{
                removeFromInventory(item,false);//Should remain false.
            }
            catch(DatabaseException | SQLException exception){
                exception.printStackTrace();
            }
        });
        //A secondary optimization call.
        try{
            removeSurplusHamperContents(hamper);
        }catch(NullPointerException | DatabaseException e ){
            e.printStackTrace();
            return hamper;
        }
        ArrayList<FoodItem> usedItems = hamper.getFoodList().toArrayList();
        usedItems.forEach((item)->{try{removeFromInventory(item, false);} //finalize changes
        catch(SQLException | DatabaseException e){e.printStackTrace();}});
        return hamper;
    }
    public Hamper createHamper(ArrayList<Client> clients){
        Hamper hamper = null;
        try{
            ArrayList<Callable<Hamper>> algorithms = new ArrayList<>();
            for(int m = 0; m < 4; m++){
                algorithms.add(createHamperCallable(clients));
            }
            ExecutorService executor = getFixedThreadPool();
            List<Future<Hamper>> listOfPossibleHampers = invokeAllHamperAlgorithms(executor,algorithms);
            hamper = getHamperWithLeastSurplus(listOfPossibleHampers);
            //Sync the inventory with the SQL databases
        }catch(Exception q){
            try{
                return generateHamperFoodListsIteratively(clients);
            }catch(Exception x){
                q.printStackTrace();
                x.printStackTrace();
                return new Hamper();
            }
        }
        try{
            removeHamperItemsFromLocalInventory(hamper);
            removeSurplusHamperContents(hamper);
            validateHamperContents(hamper);
        }catch(DatabaseException e){
            e.printStackTrace();
        }finally{
            //if(removeAllHamperItemsFromSQLDatabase(hamper) == false){
                try{
                    validateHamperContents(hamper);
                    removeHamperItemsFromLocalInventory(hamper);
                }catch(Exception exception){
                    exception.printStackTrace();
                    return new Hamper();
                }
            //}
        }
        return hamper;
    }
    /**
     * 
     * @param adultMale the number of "adult male"-profiled clients to add
     * @param adultFemale the number of "adult female"-profiled clients to add
     * @param childOver8 the number of children over the age of eight to add
     * @param childUnder8 the number of children under the age of eight to add.
     * @return an ArrayList containing the generated client objects.
     */
    public ArrayList<Client> createClients(int adultMale, int adultFemale, int childOver8, int childUnder8){
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
            return clientList;
        }
    }
    /**
     * 
     * @param clientsList the ArrayList of Client objects to be validated
     * @return true if the list is valid
     */
    public boolean validateClientList(ArrayList<Client> clientsList){
        if(clientsList.size() <=10 && clientsList.size() > 0){
            return true;
        }
        else return false;
    }
    /**
     * Creates a single concatenated string with the details of the order.
     * @param hampers the list of hampers to include on the order form
     * @return a concatenated string representing the order form.
     */
    public static String generateOrderForm(ArrayList<Hamper> hampers){
        StringBuilder builder1 = new StringBuilder();
        final String titleField = "ORDER RECEIPT\n";
        final String underlineTitle = "------------------";
        String nameField = "Name:\n";
        String dateField = "Date: ";
        String date = "";
        builder1.append(titleField);
        builder1.append(underlineTitle+"\n");
        builder1.append(nameField);
        builder1.append(dateField + date + "\n");
        builder1.append(underlineTitle+"\n");
        int k = 1;
        for(Hamper hamper : hampers){
            builder1.append(String.format("HAMPER %d:",k));
            if(hamper.getFoodList() == null){
                builder1.append("Created in error.\n\n\n");
            }else{
            builder1.append("\n");
            String str = hamper.printSummary();
            builder1.append(str);
            builder1.append("\n");
            builder1.append("\n");
            builder1.append(hamper.getNutritionalDetails());
            builder1.append(underlineTitle);
            builder1.append(underlineTitle);
            builder1.append(underlineTitle);
            builder1.append("\n");
            }
            k++;
        }
        builder1.append("THANK YOU");
        builder1.append("\n");
        String text = builder1.toString();
        return text;
    }
    public static String generateOrderForm(ArrayList<Hamper> hampers, String name, int day, int month, int year){
        StringBuilder builder1 = new StringBuilder();
        final String titleField = "ORDER FORM\n";
        final String underlineTitle = "------------------";
        String nameField = "Name: " + name + "\n";
        String dateField = "Date: ";
        String date = "";
        try{
        date = LocalDate.of(year,month,day).toString();
        }catch(Exception e){
            System.err.println("Invalid date specified");
        }
        builder1.append(titleField);
        builder1.append(underlineTitle+"\n");
        builder1.append(nameField);
        builder1.append(dateField + date + "\n");
        builder1.append(underlineTitle+"\n");
        int k = 1;
        for(Hamper hamper : hampers){
            builder1.append(String.format("HAMPER %d:",k));
            if(hamper.getFoodList() == null){
                builder1.append("Created in error.\n\n\n");
            }else{
            String str = hamper.printSummary();
            builder1.append(str);
            builder1.append("\n");
            builder1.append(hamper.getNutritionalDetails());
            builder1.append(underlineTitle);
            builder1.append(underlineTitle);
            builder1.append(underlineTitle);
            builder1.append("\n");
            }
            k++;
        }
        return builder1.toString();
    }
    public static boolean writeToFile(String text, String fname){
        boolean status = true;
        PrintWriter writer = null;
        StringBuilder builder = new StringBuilder();
        builder.append(fname);
        builder.append(".txt");
        try{
            File file = new File(builder.toString());
            int n = 1;
            while(file.exists()){
                builder.replace(0,builder.length(),"");
                builder.append(fname+"("+n+")");
                builder.append(".txt");
                file = new File(builder.toString());
                n++;
            }
            writer = new PrintWriter(file);
            writer.write(text);
        }
        catch(Exception e){
            e.printStackTrace();
            status = false;
        }finally{
            writer.close();
        }
        return status;
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
    private int[] getAllItemData(FoodItem item){
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

