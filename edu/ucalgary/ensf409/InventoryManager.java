package edu.ucalgary.ensf409;
import java.sql.*;
/**
 * Handles the connection to the SQL database. Ensures that the user
 * can never directly access the database to preseve integrity.
 */
public class InventoryManager{
    private String username;
    private String url;
    private String password;
    private Connection dbConnect;
    private ResultSet results;
    private FoodList inventory;
    public InventoryManager(String url, String user, String password)
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
    public FoodList getAvailableFoodList(){
        return this.inventory;
    }
    /**
     * 
     * @param foodItem
     * @param update whether or not to update the food inventory. If performing a large
     * number of updates, this should be false;
     * @return true if the update was successful; false otherwise
     * @throws SQLException
     * @throws DatabaseException
     */
    public boolean removeFromDatabase(FoodItem foodItem, boolean update) throws SQLException, DatabaseException{
        boolean status = true;
        boolean updateStatus = true;
        try{
            try{
                dbConnect = DriverManager.getConnection(url, username, password);
                Statement stmt = dbConnect.createStatement();
                String query = "DELETE FROM AVAILABLE_FOOD WHERE ItemID="+foodItem.getItemID();
                stmt.executeQuery(query);
                dbConnect.commit();
                if(update == true){
                    updateStatus = updateAvailableFood();
                    int attempts = 0;
                    while(!(updateStatus) && attempts < 10){
                        System.out.println("Failed to connect... retrying ("+ attempts+1+"/10)");
                        updateStatus = updateAvailableFood();
                        attempts++;
                    }
                }
            }
            catch(SQLException exception){
                dbConnect.rollback();
                status = false;
            }
        }
        catch(SQLException except){
            //Catches an SQL exception in the rollback command
        }
        finally{
            dbConnect.close();
            //throws an SQLException if something goes awry while closing the database;
        }
        if(updateStatus == false){
            throw new DatabaseException();
        }
        return status;
    }
    public boolean updateAvailableFood() throws SQLException{
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
}
