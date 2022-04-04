package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.*;

/**
 * @version 1.0
 * @since 1.0
 * @author Carter Marcelo <ahref>mailto:carter.marcelo@ucalgary.ca</a>
 */


public class Hamper {
    private ArrayList<Client> clients;
    private ArrayList<FoodItem> foodItems;
    private int totalCalories;
    private int totalFruitsVeggies;
    private int totalGrains;
    private int totalProtein;
    private int totalOther;

    public Hamper(){
        this.clients = null;
        this.foodItems = null;
        this.totalCalories = 0;
        this.totalFruitsVeggies = 0;
        this.totalGrains = 0;
        this.totalOther = 0;
        this.totalProtein = 0;
    }
    public Hamper(ArrayList<Client> clients, ArrayList<FoodItem>foodItems){
        this.clients = clients;
        this.foodItems = foodItems;
        this.totalCalories = 0;
        this.totalFruitsVeggies = 0;
        this.totalGrains = 0;
        this.totalOther = 0;
        this.totalProtein = 0;
    }
}
