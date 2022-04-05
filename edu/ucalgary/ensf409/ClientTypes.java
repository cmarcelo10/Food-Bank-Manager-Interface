package edu.ucalgary.ensf409;

public enum ClientTypes {

    ADULT_MALE{
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
