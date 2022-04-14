/*
    Group 40 Test Suite
    Group Members:
    
    Nicole Boilard 
    UCID: 30111842

    Nicholas Lam 
    UCID: 30115728
    
    Carter Marcelo 
    UCID: 30089543
    
    Ethan Winters
    UCID: 30116419
*/


package edu.ucalgary.ensf409;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class ProjectTest{
    // the following are all tests on the FoodItem Class
    @Test 
    public void foodItemConstructorTest(){
        boolean passed = false;
        FoodItem newFood = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        if(newFood != null){
            passed = true;
        }
        assertTrue("FoodItem Constructor failed to create a FoodItem object when given all the required arguments.", passed);
    }
    @Test
    public void NumericAttributesTest(){
        boolean passed = false;
        FoodItem newFood = new FoodItem(3045, "Hotdog", 45, 40, 5, 10, 900);
        int ItemID = newFood.getNumericAttribute(1);
        if(ItemID == 3045){
            passed = true;
        }
        assertTrue("Method getItemID or getNumericAttributesTest did not return correct value; expected: " + 3045 + ", but was: " + ItemID, passed);
        passed = false;
        int GrainContent = newFood.getNumericAttribute(2);
        if(GrainContent == 405){
            passed = true;
        }
        assertTrue("Method getGrainContent or getNumericAttributesTest did not return correct value; expected: " + 405 + ", but was: " + GrainContent, passed);
        passed = false;
        int FVContent = newFood.getNumericAttribute(3);
        if(FVContent == 45){
            passed = true;
        }
        assertTrue("Method getFVContent or getNumericAttributesTest did not return correct value.", passed);
        passed = false;
        int proteinContent = newFood.getNumericAttribute(4);
        if(proteinContent == 360){
            passed = true;
        }
        assertTrue("Method getProteinContent or getNumericAttributesTest did not return correct value.", passed);
        passed = false;
        int otherContent = newFood.getNumericAttribute(5);
        if(otherContent == 90){
            passed = true;
        }
        assertTrue("Method getOtherContent or getNumericAttributesTest did not return correct value; expected: " + 90 + ", but was: " + otherContent, passed);
        passed = false;
        int Calories = newFood.getNumericAttribute(6);
        if(Calories == 900){
            passed = true;
        }
        assertTrue("Method getCalories or getNumericAttributesTest did not return correct value.", passed);
    }
    @Test
    public void getPercentContentTest(){
        boolean passed = false;
        FoodItem newFood = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        int grains = newFood.getPercentContent("grains");
        if(grains == 45){
            passed = true;
        }
        assertTrue("Method getPercentContent did not return the correct grain percentage.", passed);
        passed = false;
        int protein = newFood.getPercentContent("protein");
        if(protein == 40){
            passed = true;
        }
        assertTrue("Method getPercentContent did not return the correct protein percentage.", passed);
        passed = false;
        int FV = newFood.getPercentContent("fruit veggies");
        if(FV == 5){
            passed = true;
        }
        assertTrue("Method getPercentContent did not return the correct fruits veggies percentage.", passed);
        passed = false;
        int other = newFood.getPercentContent("other");
        if(other == 10){
            passed = true;
        }
        assertTrue("Method getPercentContent did not return the correct other percentage.", passed);
        passed = false;
        try{
            int fail = newFood.getPercentContent("food");
            }
        catch(IllegalArgumentException e){
            passed = true;
        }
        assertTrue("Method getPercentContent did not throw and exception when given an invalid fooditem attribute.", passed);
    }
    // the following are all tests on the FoodList Class
    @Test
    public void FoodItemConstructorTest(){
        boolean passed = true;
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        FoodItem theFood = theList.getFoodItem(0);
        if(theFood.equals(newFood1)){
            passed = true;
        }
        assertTrue("FoodList Constructor or getFoodItem method do not return correct data.", passed);
    }
    @Test
    public void removeFoodItemTest(){
        boolean passed = true;
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        theList.removeFoodItem(newFood2);
        FoodItem theFood = theList.getFoodItem(1);
        if(theFood.equals(newFood2)){
            passed = false;
        }
        assertTrue("removeFoodItem failed to remove the requested food item", passed);
    }
    @Test
    public void replaceFoodItemTest(){
        boolean passed = false;
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        theList.replaceFoodItem(newFood2, newFood4);
        FoodItem theFood = theList.getFoodItem(1);
        if(theFood.equals(newFood4)){
            passed = true;
        }
        assertTrue("replaceFoodItem failed to replace the required food item", passed);
    }
    @Test
    public void addFoodItemTest(){
        boolean passed = false;
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        FoodItem newFoodTest = new FoodItem(4000, "Hamburger", 45, 20, 5, 30, 1000);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        theList.addFoodItem(newFoodTest);
        FoodItem retrieve = theList.getFoodItem(4);
        if(retrieve.equals(newFoodTest)){
            passed = true;
        }
        assertTrue("addFoodItem failed to add the item requested.", passed);
    }
    @Test
    public void gettersTest(){
        boolean passed = false;
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        int totalItems = theList.getTotalItems(); 
        if(totalItems == 4){
            passed = true;
        }
        assertTrue("getTotalItems did not return the correct number of items; expected: " + 4 + ", but was: " + totalItems, passed);
        passed = false;
        int totalCalories = theList.getTotalCalories();
        if(totalCalories == 2625){
            passed = true;
        }
        assertTrue("getTotalCalories did not return correct number of calories; expected: " + 2625 + ", but was: " + totalCalories, passed);
        passed = false;
        int totalGrains = theList.getGrainContent();
        if(totalGrains == 925){ // 405+420+100+0=925
            passed = true;
        }
        assertTrue("getGrainContent did not return correct grain contents; ; expected: " + 925 + ", but was: " + totalGrains, passed);
        passed = false;
        int totalFV = theList.getFruitVeggiesContent();
        if(totalFV == 769){ // 45+600+100+23=768
            passed = true;
        }
        assertTrue("getFruitsVeggiesContent did not return correct FV contents; ; expected: " + 768 + ", but was: " + totalFV, passed);
        passed = false;
        int totalProtein = theList.getProteinContent();
        if(totalProtein == 740){ //360+180+200+0=740
            passed = true;
        }
        assertTrue("getProteinContent did not return correct protein content; expected: " + 740 + ", but was: " + totalProtein, passed);
        passed = false;
        int totalOther = theList.getOtherContent();
        if(totalOther == 191){ //90+0+100+1=191
            passed = true;
        }
        assertTrue("getOther did not return the correct other content.", passed);
    }
    // the following are all tests on the Client Class
    @Test
    public void ClientConstructorTest(){
        boolean passed = false;
        Client theClient = new Client(2004, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        if(theClient != null){
            passed = true;
        }
        assertTrue("FoodItem Constructor failed to create a FoodItem object when given all the required arguments.", passed);
    }
    @Test
    public void TestValidClientType(){
        boolean passed = false;
        try{
            Client invalidClient = new Client(2004, "ADULT", 40, 30, 20, 10, 1300);
        }
        catch(IllegalArgumentException e){
            passed = true;
        }
        assertTrue("getValidClientType failed to throw an error when an invalid client type was provided", passed);
    }
    @Test
    public void TestClientGetters(){
        boolean passed = false;
        Client theClient = new Client(2004, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        int ClientID = theClient.getClientID();
        if(ClientID == 2004){
            passed = true;
        }
        assertTrue("Client Getters did not return the correct client ID", passed);
        passed = false;
        ClientTypeClasses ClientType = theClient.getClientType();
        if(ClientType.equals(ClientTypeClasses.ADULT_FEMALE)){
            passed = true;
        }
        assertTrue("Client Getters did not return the correct client type", passed);
        passed = false;
        int grains = theClient.getGrains();
        if(grains == 520){
            passed = true;
        }
        assertTrue("Client Getters did not return the correct grain contents", passed);
        passed = false;
        int FV = theClient.getFruitVeggies();
        if(FV == 390){
            passed = true;
        }
        assertTrue("Client Getters did not return the correct fruits veggies contents", passed);
        passed = false;
        int protein = theClient.getProtein();
        if(protein == 260){
            passed = true;
        }
        assertTrue("Client Getters did not return the corrent protein content", passed);
        passed = false;
        int other = theClient.getOther();
        if(other == 130){
            passed = true;
        }
        assertTrue("Client Getters did not return the corrent other content", passed);
        passed = false;
        int calories = theClient.getCalories();
        if(calories == 1300){
            passed = true;
        }
        assertTrue("Client Getters did not return the corrent calories", passed);
    }
    // the following are all tests on the Hamper Class
    @Test
    public void HamperConstructorTest(){
        boolean passed = false;
        Client newClient1 = new Client(2004, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient2 = new Client(2005, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient3 = new Client(2006, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        ArrayList<Client> theClients = new ArrayList<Client>();
        theClients.add(newClient1);
        theClients.add(newClient2);
        theClients.add(newClient3);
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        Hamper theHamper = new Hamper(theClients, theList);
        if(theHamper != null){
            passed = true;
        }
        assertTrue("Hamper Constructor failed to create a hamper object given a client list and a food item list.", passed);
    }
    @Test
    public void setAndGetClientsTest(){
        boolean passed = false;
        Client newClient1 = new Client(2004, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient2 = new Client(2005, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient3 = new Client(2006, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        ArrayList<Client> theClients = new ArrayList<Client>();
        theClients.add(newClient1);
        theClients.add(newClient2);
        theClients.add(newClient3);
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        Hamper theHamper = new Hamper(theClients, theList);

        Client testClient = new Client(2006, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        theClients.add(testClient);
        theHamper.setClients(theClients);
        ArrayList<Client> returnList = theHamper.getClients();
        if(returnList.equals(theClients)){
            passed = true;
        }
        assertTrue("setClients or getClients failed to set and/or return the correct data.", passed);
    }
    @Test
    public void setAndGetFoodListTest(){
        boolean passed = false;
        Client newClient1 = new Client(2004, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient2 = new Client(2005, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        Client newClient3 = new Client(2006, "ADULT_FEMALE", 40, 30, 20, 10, 1300);
        ArrayList<Client> theClients = new ArrayList<Client>();
        theClients.add(newClient1);
        theClients.add(newClient2);
        theClients.add(newClient3);
        ArrayList<FoodItem> fooditems = new ArrayList<FoodItem>();
        FoodItem newFood1 = new FoodItem(0045, "Hotdog", 45, 40, 5, 10, 900);
        FoodItem newFood2 = new FoodItem(0012, "Pizza", 35, 15, 50, 0, 1200);
        FoodItem newFood3 = new FoodItem(0047, "Sandwitch", 20, 40, 20, 20, 500);
        FoodItem newFood4 = new FoodItem(0001, "Apple", 0, 0, 95, 5, 25);
        fooditems.add(newFood1);
        fooditems.add(newFood2);
        fooditems.add(newFood3);
        fooditems.add(newFood4);
        FoodList theList = new FoodList(fooditems);
        Hamper theHamper = new Hamper(theClients, theList);

        FoodItem newFoodTest = new FoodItem(4000, "Hamburger", 45, 20, 5, 30, 1000);
        theList.addFoodItem(newFoodTest);
        theHamper.setFoodList(theList);
        FoodList returnList = theHamper.getFoodList();
        if(returnList.equals(theList)){
            passed = true;
        }
        assertTrue("setFoodList or getFoodList failed to set and/or return the correct data.", passed);
    }    
}   