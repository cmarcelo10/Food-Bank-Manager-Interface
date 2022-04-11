package edu.ucalgary.ensf409;
import java.util.*;
/**
 * A class of objects that encaspulate client information
 * for use in other parts of the system. Each field 
 */
public class Client{    
    private final int CLIENT_ID;
    private final ClientTypeClasses CLIENT_TYPE;
    private final int WHOLE_GRAINS;
    private final int PROTEIN;
    private final int FRUIT_VEGGIES;
    private final int OTHER;
    private final int CALORIES;
    /**
     * Constructs a profile to encapsulate client information
     * @param CLIENT_ID is the client's CLIENT_ID number;
     * @param clientType is the category under which the client falls
     * @param grains is the raw percentage of the client diet comprised of grains
     * @param protein is the raw percentage of the client diet comprised of protein
     * @param fruitVeggies is the raw percentage of the client diet comprised of fruit & veggie content
     * @param other is the raw percentage of the client diet comprised of "other" content 
     * @param calories is the total amount of calories needed to fulfill the client's
     * dietary needs on a daily basis
     * @throws IllegalArgumentException
     */
    public Client(int clientID, String clientType, int grains, int fruitVeggies, int protein,
    int other, int calories) throws IllegalArgumentException{
        this.CLIENT_ID = clientID;
        this.CLIENT_TYPE = getValidClientType(clientType);
        this.CALORIES = calories;
        this.WHOLE_GRAINS = Math.round((float)((float)(calories*grains)/100));
        this.PROTEIN = Math.round((float)((float)(calories*protein)/100));
        this.FRUIT_VEGGIES = Math.round((float)((float)(calories*fruitVeggies)/100));
        this.OTHER = Math.round((float)((float)(calories*other)/100));

    }
    /** 
     * @since 1.2
     * Converts a string into a ClientTypeClasses object, a
     * universal system that is standardized among all client objects which 
     * dictates the client object's diet category.
     * @param clientType is the string to convert into a standardized ClientTypeClasses object]
     * @return
    */
    public static ClientTypeClasses getValidClientType(String clientType) throws IllegalArgumentException{
        clientType = clientType.trim();
        clientType = clientType.replaceAll("( )","_");
        clientType = clientType.toUpperCase();
        if(clientType.equals("ADULT_MALE")){
            return ClientTypeClasses.ADULT_MALE;
        }
        else if(clientType.equals("ADULT_FEMALE")){
            return ClientTypeClasses.ADULT_FEMALE;
        }
        else if(clientType.equals("CHILD_OVER_EIGHT")||clientType.equals("CHILD_OVER_8")){
            return ClientTypeClasses.CHILD_OVER_EIGHT;
        }
        else if(clientType.equals("CHILD_UNDER_EIGHT") || clientType.equals("CHILD_UNDER_8")){
            return ClientTypeClasses.CHILD_UNDER_EIGHT;
        }
        else{
            throw new IllegalArgumentException("Failed to initialize client: invalid client type \"" + clientType + "\"");
        }
    }
    public ArrayList<String> getNeeds(){
        String wholeGrains = String.format("Whole grains: %d",WHOLE_GRAINS);
        String fruitVeggies = String.format("Fruit and Veggies: %d",FRUIT_VEGGIES);
        String protein = String.format("Protein: %d",PROTEIN);
        String other = String.format("Other: %d", OTHER);
        ArrayList<String> list = new ArrayList<>();
        list.add(wholeGrains);
        list.add(fruitVeggies);
        list.add(protein);
        list.add(other);
        return list;
    }
    public int getClientID(){
        return this.CLIENT_ID;
    }
    public ClientTypeClasses getClientType(){
        return this.CLIENT_TYPE;
    }
    public int getGrains(){
        return this.WHOLE_GRAINS;
    }
    public int getCalories(){
        return this.CALORIES;
    }
    public int getProtein(){
        return this.PROTEIN;
    }
    public int getFruitVeggies(){
        return this.FRUIT_VEGGIES;
    }
    public int getOther(){
        return this.OTHER;
    }
}
