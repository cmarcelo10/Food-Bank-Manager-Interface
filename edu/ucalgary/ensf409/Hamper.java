package edu.ucalgary.ensf409;
import java.util.*;

/**
 * @version 1.0
 * @since 1.0
 * @author Carter Marcelo <ahref>mailto:carter.marcelo@ucalgary.ca</a>
 */
public class Hamper{
    private ArrayList<Client> clients;
    private FoodList foodList;
    private int totalCalories;
    private int totalFruitVeggies;
    private int totalGrains;
    private int totalProtein;
    private int totalOther;
    public Hamper(){
        this.clients = null;
        this.foodList = null;
        this.totalCalories = 0;
        this.totalFruitVeggies = 0;
        this.totalGrains = 0;
        this.totalOther = 0;
        this.totalProtein = 0;
    }
    public Hamper(ArrayList<Client> clients,FoodList foodList){
        this.clients = clients;
        this.foodList = foodList;
        this.totalCalories = foodList.getTotalCalories();
        this.totalFruitVeggies = foodList.getFruitVeggiesContent();
        this.totalGrains = foodList.getGrainContent();
        this.totalOther = foodList.getOtherContent();
        this.totalProtein = foodList.getProteinContent();
    }
    public ArrayList<Client> getClients(){
        return this.clients;
    }
    public void setClients(ArrayList<Client> clients){
        this.clients = clients;
    }
    public FoodList toArrayList(){
        return this.foodList;
    }
    public void setFoodList (FoodList foodList){
        this.foodList = foodList;
        this.totalCalories = foodList.getTotalCalories();
        this.totalGrains = foodList.getGrainContent();
        this.totalFruitVeggies = foodList.getFruitVeggiesContent();
        this.totalOther = foodList.getOtherContent();
        this.totalProtein = foodList.getProteinContent();
    }
    public FoodList getFoodList(){
        return this.foodList;
    }
    public int getTotalCalories(){
        return this.totalCalories;
    }
    public int getTotalFruitVeggies(){
        return this.totalFruitVeggies;
    }
    public int getTotalProtein(){
        return this.totalProtein;
    }
    public int getTotalOther(){
        return this.totalOther;
    }
    public int getTotalGrains(){
        return this.totalGrains;
    }
    public String printSummary(){
        ArrayList<String> clientInfo = new ArrayList<>();
        clients.parallelStream().forEach(client ->clientInfo.add(client.getClientType().toString()));
        StringBuilder builder = new StringBuilder();
        int AM = 0;
        int AF = 0;
        int CO8 = 0;
        int CU8 = 0;
        String childOverEight = null;
        String childUnderEight = null;
        String adultFemales = null;
        String adultMales = null;
        final String NEWLINE = "\n";
        final String UNDERLINE= "------------------";
        for(String str : clientInfo){
            if(str.equals("Child under 8")){
                CU8++;
                childUnderEight = String.format("%s --- x %d",str,(CU8));
            }
            else if(str.equals("Child over 8")){
                CO8++;
                childOverEight = String.format("%s ---- x %d",str,(CO8));
            }
            else if(str.equals("Adult Female")){
                AF++;
                adultFemales = String.format("%s ---- x %d",str,(AF));
            }
            else{
                AM++;
                adultMales = String.format("%s ------ x %d",str,(AM));
            }
        }
        builder.append(NEWLINE);
        builder.append("CLIENTS:");
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        if(adultMales != null){
            builder.append(adultMales);
            if(adultFemales != null || childOverEight != null || childUnderEight != null){
                builder.append("\n");
            }
        }
        if(adultFemales != null){
            builder.append(adultFemales);
            if(childOverEight != null || childUnderEight != null){
                builder.append("\n");
            }
        }  
        if(childOverEight != null){
            builder.append(childOverEight);
            if(childUnderEight != null){
                builder.append("\n");
            }
        }
        if(childUnderEight  != null){
            builder.append(childUnderEight);
        }
        builder.append("\n");
        builder.append(UNDERLINE);
        builder.append(UNDERLINE);
        builder.append(NEWLINE);
        builder.append(NEWLINE);
        builder.append("ITEMS:\n");
        builder.append(NEWLINE);
        Comparator<FoodItem>  byItemID = Comparator.comparing(item -> item.getItemID());
        foodList.toArrayList().sort(byItemID);
        for(FoodItem item : foodList.toArrayList()){
            String str = "";
            if(item.getItemID() >=10 && item.getItemID() < 100){
               str = String.format("#%d --- %6s\n", item.getItemID(),item.getName());
            }else if(item.getItemID() < 10){
                str = String.format("#%d ---- %7s\n", item.getItemID(),item.getName());
            }
            else{
                str = String.format("#%d -- %5s\n", item.getItemID(),item.getName());
            }
            
            builder.append(str);
        }
        return builder.toString();
    }
    public String getNutritionalDetails(){
        ArrayList<Client> clients = this.getClients();
        Iterator<Client> iter = clients.iterator();
        int calTotal = 0;
        int othTotal = 0;
        int gTotal = 0;
        int fvTotal = 0;
        int pTotal = 0;
        while(iter.hasNext()){
           var temp = iter.next();
           calTotal+=temp.getCalories();
           othTotal+=temp.getOther();
           gTotal+=temp.getGrains();
           fvTotal+=temp.getFruitVeggies();
           pTotal+=temp.getProtein();
        }
        final StringBuilder builder = new StringBuilder();
        final String NEWLINE = "\n";
        final String UNDERLINE= "------------------";
        FoodList list = this.getFoodList();
        builder.append((String.format(String.format("Food Group %14s","Required")+"%10s","Included")));
        builder.append(NEWLINE);
        builder.append(UNDERLINE);
        builder.append(UNDERLINE);
        builder.append(NEWLINE);
        builder.append(String.format(String.format("Whole Grains: %9d",
        gTotal*7)+"%10d", list.getGrainContent()));
        builder.append(NEWLINE);
        builder.append(String.format(String.format("Fruit Veggies: %8d", 
        fvTotal*7)+"%10d",list.getFruitVeggiesContent()));
        builder.append(NEWLINE);
        builder.append(String.format(String.format("Protein: %14d",
        pTotal*7)+"%10d", list.getProteinContent()));
        builder.append(NEWLINE);
        builder.append(String.format(String.format("Other: %16d",
        othTotal*7)+"%10d", list.getOtherContent()));
        builder.append(NEWLINE);
        builder.append(UNDERLINE);
        builder.append(UNDERLINE);
        builder.append(NEWLINE);
        builder.append(String.format(String.format("TOTAL CALORIES: %7d", 
        calTotal*7)+"%10d",list.getTotalCalories()));
        builder.append(NEWLINE);
        return builder.toString();
    }
    public void getOptimizationProperties(){
        ArrayList<Client> clients = this.getClients();
        Iterator<Client> iter = clients.iterator();
        int calTotal = 0;
        int othTotal = 0;
        int gTotal = 0;
        int fvTotal = 0;
        int pTotal = 0;
        while(iter.hasNext()){
           var temp = iter.next();
           calTotal+=temp.getCalories();
           othTotal+=temp.getOther();
           gTotal+=temp.getGrains();
           fvTotal+=temp.getFruitVeggies();
           pTotal+=temp.getProtein();
        }
        FoodList list = this.getFoodList();
        System.out.println(String.format("\n\n%-15s%-15s%-15s%-15s","Field","Required","Included","Surplus"));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Calories:", 
        calTotal*7,list.getTotalCalories(),list.getTotalCalories()-calTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Grains:", 
        gTotal*7, list.getGrainContent(), list.getGrainContent()-gTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","FV:", 
        fvTotal*7, list.getFruitVeggiesContent(),list.getFruitVeggiesContent()-fvTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d","Protein:", 
        pTotal*7, list.getProteinContent(), list.getProteinContent()-pTotal*7));
        System.out.println(String.format("%-15s%-15d%-15d%-15d\n","Other:",
        othTotal*7, list.getOtherContent(),list.getOtherContent()-othTotal*7));
        System.out.println((double)((double)list.getTotalCalories()/(double)(calTotal*7))*100);
    }
}
