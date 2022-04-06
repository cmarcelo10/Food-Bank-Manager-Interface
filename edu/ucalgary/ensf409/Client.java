package edu.ucalgary.ensf409;
public abstract class Client{    
    protected final int clientID;
    protected final ClientTypeClasses clientType;
    protected final int grains;
    protected final int protein;
    protected final int fruitsVeggies;
    protected final int calories;
    protected final int other;
    public Client(int clientID, String clientType, int grains, int protein, int fruitsVeggies, 
    int calories, int other) throws IllegalArgumentException{
        this.clientID = clientID;
        this.clientType = getValidClientType(clientType);
        this.calories = calories;
        this.grains = (calories*grains)/100;
        this.protein = (calories*protein)/100;
        this.fruitsVeggies = (calories*fruitsVeggies)/100;
        this.other = (calories*other)/100;
    }
    private ClientTypeClasses getValidClientType(String clientType) throws IllegalArgumentException{
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
    public String getNeeds(){
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
    public int getFruitsVeggies(){
        return this.fruitsVeggies;
    }
    public int getOther(){
        return this.other;
    }
}
