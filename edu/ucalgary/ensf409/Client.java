package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;
public class Client{    
    private int clientID;
    private String clientType;
    private int grains;
    private int protein;
    private int fruitsVeggies;
    private int calories;
    private int other;
    public Client(int clientID, String clientType, int grains, int protein, int fruitsVeggies, 
    int calories, int other){
        this.clientID = clientID;
        this.clientType = clientType;
        this.calories = calories;
        this.grains = (calories*grains)/100;
        this.protein = (calories*protein)/100;
        this.fruitsVeggies = (calories*fruitsVeggies)/100;
        this.other = calories*(other/100);
    }
    public String getNeeds(){
        //Should get the client profile;
        return null;
    }
    public int getClientID(){
        return this.clientID;
    }
    public String getClientType(){
        return this.clientType;
    }
    public int getGrains(){
        return this.grains;
    }
    public int getCalories(){
        return this.calories;
    }
    public int getProtein(){
        return this.protein;
    }
    public int getFruitsVeggies(){
        return this.fruitsVeggies;
    }
    public int getOther(){
        return this.other;
    }

}
