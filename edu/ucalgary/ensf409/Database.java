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
    public final ArrayList<StackTraceElement[]> ERROR_LOG = new ArrayList<>();
    protected volatile FoodList inventory;
    public Database(){}
    public Database(String url, String user, String password)
    throws DatabaseException, SQLException{
        this.url = url;
        this.username = user;
        this.password = password;
        boolean updateStatus = updateAvailableFood();
        int attempts = 0;
        //Keep trying to connect:
        try{
            while(!(updateStatus) && attempts < 10){
                System.out.println("Failed to connect... retrying ("+ attempts+1+"/10)");
                updateStatus = updateAvailableFood();
                attempts++;
            }
            if(updateStatus == false){
                throw new DatabaseException();
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setUsername(String username){
        this.username = username;
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
                ERROR_LOG.add(exception.getStackTrace());
                dbConnect.rollback();
            }
        }
        catch(SQLException except){
            //Catches an SQL exception in the rollback command, if it occurs
            ERROR_LOG.add(except.getStackTrace());
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
    protected boolean updateAvailableFood() throws SQLException{
        boolean status = true;
        FoodList list = new FoodList();
        ResultSet results = null;
        var temp = this.inventory;
        this.inventory = null;
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
                }catch(SQLException exception){ERROR_LOG.add(exception.getStackTrace());}
                status = false;
            }
        }
        finally{
            try{
                dbConnect.close();
            }
            catch(Exception e){
                ERROR_LOG.add(e.getStackTrace());
                this.inventory = temp;
            }
            
        }
        return status;
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
    private Callable<FoodItem> createParserCallable(ArrayList<FoodItem> list,int sortKey, int searchKey){
        Callable<FoodItem> callable = new Callable<FoodItem>() {
            public FoodItem call(){
                return Parser.binarySearch(sortKey, searchKey,list);
            }
        };
        return callable;
    }
    /**
     * Generates a FoodList object which fulfills the needs of the clients provided
     * as input. The filling algorithm prioritizes filling the clients' needs. While it 
     * aims to minimize surplus, it is not guaranteeed to return an optimal combination of items.
     * FoodList object returned by this method is guaranteed to fulfill the clients' needs
     * @param clients the ArrayList of Clients with needs to be fulfilled
     * @return a foodList that meets the clients needs
     * @throws DatabaseException
     * @throws SQLException
     */
    private FoodList createHamperFoodList(ArrayList<Client> clients, boolean suppressWarnings) throws DatabaseException{
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
        if(
        (!validCalorieNeeds || !validGrainsNeeds || !validFruitVeggiesNeeds || !validProteinNeeds ||!validOtherNeeds)){
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
                executor1.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
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
            callables.add(new Callable<Void>(){public Void call(){proteinList.sort(Parser.BY_PROTEIN); return null;}});
            callables.add(new Callable<Void>(){public Void call(){grainsList.sort(Parser.BY_WHOLE_GRAINS); return null;}});
            callables.add(new Callable<Void>(){public Void call(){fruitVeggieList.sort(Parser.BY_FRUIT_VEGGIES);return null;}});
            callables.add(new Callable<Void>(){public Void call(){ otherList.sort(Parser.BY_OTHER);return null;}});
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
                        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MICROSECONDS);
                    }catch(InterruptedException e){
                        throw new DatabaseException("an unknown error occured while handling the request");
                    }
                    if(exceptionCaught){
                        throw new DatabaseException("an unknown error occured while handling the request");
                    }
                }
        }
        catch(Exception e){
            proteinList.sort(Parser.BY_PROTEIN);
            grainsList.sort(Parser.BY_WHOLE_GRAINS);
            fruitVeggieList.sort(Parser.BY_FRUIT_VEGGIES);
            otherList.sort(Parser.BY_OTHER);
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
            List<Callable<FoodItem>> tasks = new ArrayList<Callable<FoodItem>>();
            if(((grains < totalGrainNeeds && grainsList.isEmpty() == false) || 
            (manualOverrideGrains && grainsList.isEmpty() == false))){
                tasks.add(createParserCallable(grainsList,FoodItem.WHOLE_GRAINS, (totalGrainNeeds - grains)));
            }
            if((protein < totalProteinNeeds) && proteinList.isEmpty() == false || 
            (manualOverrideProtein && proteinList.isEmpty() == false)){
                tasks.add(createParserCallable(proteinList,FoodItem.PROTEIN,(totalProteinNeeds - protein)));
            }
            if(((fruitVeggies < totalFVNeeds) && (fruitVeggieList.isEmpty()==false)) || 
            (manualOverrideFruitVeggies && fruitVeggieList.isEmpty() == false)){
                tasks.add(createParserCallable(fruitVeggieList,FoodItem.FRUIT_VEGGIES,(totalFVNeeds - fruitVeggies)));
            }
            if(((other < totalOtherNeeds) && (otherList.isEmpty() == false)) || 
            (manualOverrideOther && otherList.isEmpty() == false)){
                tasks.add(createParserCallable(otherList,FoodItem.OTHER,totalOtherNeeds - other));
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
            catch(Exception e){ e.printStackTrace();
                try{
               
                    if(((grains < totalGrainNeeds && grainsList.isEmpty() == false) || 
                    (manualOverrideGrains && grainsList.isEmpty() == false))){
                    options.add((createParserCallable(grainsList,FoodItem.WHOLE_GRAINS, (totalGrainNeeds - grains))).call());
                    }
                    if((protein < totalProteinNeeds) && proteinList.isEmpty() == false || 
                    (manualOverrideProtein && proteinList.isEmpty() == false)){
                        options.add((createParserCallable(proteinList,FoodItem.PROTEIN,(totalProteinNeeds - protein))).call());
                    }
                    if(((fruitVeggies < totalFVNeeds) && (fruitVeggieList.isEmpty()==false)) || 
                    (manualOverrideFruitVeggies && fruitVeggieList.isEmpty() == false)){
                        options.add((createParserCallable(fruitVeggieList,FoodItem.FRUIT_VEGGIES,(totalFVNeeds - fruitVeggies))).call());
                    }
                    if(((other < totalOtherNeeds) && (otherList.isEmpty() == false)) || (manualOverrideOther && otherList.isEmpty() == false)){
                        options.add((createParserCallable(otherList,FoodItem.OTHER,totalOtherNeeds - other)).call());
                    }
                }catch(Exception u){
                    u.printStackTrace();
                    return null;
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
                options.sort(Parser.BY_CALORIES);
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
                    options.sort(Parser.BY_PROTEIN);
                }else if(!fMet){
                    options.sort(Parser.BY_FRUIT_VEGGIES);
                }
                else if(!gMet){
                    options.sort(Parser.BY_OTHER);
                }
                else{
                    options.sort(Parser.BY_WHOLE_GRAINS);
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
    public static void validateHamperContents(Hamper hamper){
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
    private Callable<Hamper> createHamperCallable(ArrayList<Client> clients, boolean override){
        Callable<Hamper> callable = new Callable<Hamper>(){
            public Hamper call(){
                FoodList foodList = null;
                try{
                    foodList = createHamperFoodList(clients,override);
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
        try{
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
        }catch(Exception e){
            return null;
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
                    removeFromInventory(item,true);//Should remain false.
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
    private Hamper generateHamperFoodListsIteratively(ArrayList<Client> clients, boolean override) throws DatabaseException, SQLException{
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
                foodList = createHamperFoodList(clients,true);
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
            if(override == false){
                Parser.removeSurplusHamperContents(hamper,inventory);
            }
        }catch(NullPointerException | DatabaseException e ){
            e.printStackTrace();
            return hamper;
        }
        ArrayList<FoodItem> usedItems = hamper.getFoodList().toArrayList();
        usedItems.forEach((item)->{try{removeFromInventory(item, true);} //finalize changes
        catch(SQLException | DatabaseException e){e.printStackTrace();}});
        return hamper;
    }
    public String determineIfClientNeedsCanBeMet(ArrayList<Client> clients){
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
                int[][] deficits = 
                {{totalGrainNeeds, inventory.getGrainContent()},
                {totalFVNeeds, inventory.getFruitVeggiesContent()},
                {totalProteinNeeds, inventory.getProteinContent()},
                {totalOtherNeeds, inventory.getOtherContent()},
                {totalCalories, inventory.getTotalCalories()}};
                return createAlertTooManyClients(deficits);
        }else{
            return null;
        }
    }
    /** 
     * Creates a hamper for an ArrayList of Client objects.
     * @param clients the list of clients to fill the hamper for
     * @param override whether to fill the hamper if the needs cannot be met. 
     * Doing so will skip the optimization calls and return a single iteratively-generated
     * hamper object.
     * @return a hamper object filled as efficiently as possible.
     */
    public Hamper createHamper(ArrayList<Client> clients, boolean override){
        Hamper hamper = null;
        if(override ==true){
            try{
                return generateHamperFoodListsIteratively(clients,true);
            }catch(SQLException | DatabaseException e){
                return new Hamper();
            }
        }
        try{
            ArrayList<Callable<Hamper>> algorithms = new ArrayList<>();
            for(int m = 0; m < 4; m++){
                algorithms.add(createHamperCallable(clients,false));
            }
            ExecutorService executor = getFixedThreadPool();
            List<Future<Hamper>> listOfPossibleHampers = invokeAllHamperAlgorithms(executor,algorithms);
            hamper = getHamperWithLeastSurplus(listOfPossibleHampers);
            if(hamper == null){
                return null;
            }
            //Sync the inventory with the SQL databases
        }catch(Exception q){
            try{
                return generateHamperFoodListsIteratively(clients,false);
            }catch(Exception x){
                ERROR_LOG.add(q.getStackTrace());
                ERROR_LOG.add(x.getStackTrace());
                q.printStackTrace();
                x.printStackTrace();
                return new Hamper();
            }
        }
        try{
            removeHamperItemsFromLocalInventory(hamper);
            Parser.removeSurplusHamperContents(hamper,inventory);
            validateHamperContents(hamper);
        }catch(DatabaseException e){
            e.printStackTrace();
        }finally{
            if(removeAllHamperItemsFromSQLDatabase(hamper) == false){
                try{
                    validateHamperContents(hamper);
                    removeHamperItemsFromLocalInventory(hamper);
                }catch(Exception exception){
                    exception.printStackTrace();
                    return new Hamper();
                }
            }
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
                for(int i = 0; i < adultFemale; i++){
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
    public static int[] getAllItemData(FoodItem item){
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

