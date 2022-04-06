package edu.ucalgary.ensf409;
import java.util.*;
public class Client{    
    private final int clientID;
    private final ClientTypeClasses clientType;
    private final int grains;
    private final int protein;
    private final int fruitVeggies;
    private final int other;
    private final int calories;
    /**
     * 
     * @param clientID the clientID number
     * @param clientType the type of client
     * @param grains is the raw percentage of calories comprised of grains
     * @param protein is the raw percentage of calories comprised of protein
     * @param fruitVeggies
     * @param other
     * @param calories
     * @throws IllegalArgumentException
     */
    public Client(int clientID, String clientType, int grains, int fruitVeggies, int protein,
    int other, int calories) throws IllegalArgumentException{
        this.clientID = clientID;
        this.clientType = getValidClientType(clientType);
        this.calories = calories;
        this.grains = Math.round((float)((float)(calories*grains)/100));
        this.protein = Math.round((float)((float)(calories*protein)/100));
        this.fruitVeggies = Math.round((float)((float)(calories*fruitVeggies)/100));
        this.other = Math.round((float)((float)(calories*other)/100));

    }
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
    public ArrayList<Integer> getNeeds(){
        //Should get the client profile;
        return null;
    }
    public int getClientID(){
        return this.clientID;
    }
    public ClientTypeClasses getClientType(){
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
    public int getFruitVeggies(){
        return this.fruitVeggies;
    }
    public int getOther(){
        return this.other;
    }
}
