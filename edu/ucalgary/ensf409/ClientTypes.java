package edu.ucalgary.ensf409;
public enum ClientTypes{
    ADULT_MALE{
        private int grainNeeds;
        private int fruitVeggiesNeeds;
        private int proteinNeeds;
        private int otherNeeds;
        private int calorieNeeds;
        
        public void setNeeds(int grainNeeds, int fruitVeggiesNeeds, int proteinNeeds,
        int otherNeeds, int calorieNeeds){
            this.grainNeeds = grainNeeds;
            this.calorieNeeds = calorieNeeds;
            this.fruitVeggiesNeeds = fruitVeggiesNeeds;
            this.proteinNeeds = proteinNeeds;
            this.otherNeeds = otherNeeds;
        }
        
        public int getClientID(){
            return 1;
        }
        public String toString(){
            return "Adult male";
        }
    },
    ADULT_FEMALE{
        public int getClientID(){
            return 2;
        }
        public String toString(){
            return "Adult female";
        }
    },
    CHILD_OVER_8{
        public int getClientID(){
            return 3;
        }
        public String toString(){
            return "Child over 8";
        }
    },
    CHILD_UNDER_8{

        public int getClientID(){
            return 4;
        }
        public String toString(){
            return "Child under 8";
        }
    },
} 