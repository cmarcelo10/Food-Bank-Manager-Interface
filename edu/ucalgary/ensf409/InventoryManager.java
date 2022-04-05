package edu.ucalgary.ensf409;
import java.util.*;


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
    public InventoryManager(String url, String user, String password) throws SQLException{
        try{
            try{
                this.url = url;
                this.username = user;
                this.password = password;
                dbConnect = DriverManager.getConnection(url, user, password);
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
                    this.inventory.addFoodItem(new FoodItem(itemid, name, grainContent, 
                    proteinContent, fvContent, otherContent, calories));
                }
            }catch(Exception e){
                System.err.println("A fatal error occurred while trying to initialize the inventory");
                e.printStackTrace();
            }
        }finally{
            dbConnect.close();
        }
    }
    public void closeConnection() throws SQLException{
            this.dbConnect.close();
    }
    public ArrayList<FoodItem> getAvailableFood(){
        return this.inventory.getFoodList();
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
                    this.inventory.addFoodItem(new FoodItem(itemid, name, grainContent, 
                    proteinContent, fvContent, otherContent, calories));
                }
            }catch(Exception e){
                status = false;
                System.err.println("A fatal error occurred while trying to initialize the inventory");
                e.printStackTrace();
            }
        }
        finally{
            try{
                dbConnect.close();
            }
            catch(SQLException e){
                System.err.println("Something went wrong while attempting to close connection with database");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return status;
    }
}
