package edu.ucalgary.ensf409;
public enum ClientTypeClasses{
    ADULT_MALE{
        public String toString(){
            return "Adult Male";
        }
    },
    ADULT_FEMALE{
        public String toString(){
            return "Adult Female";
        }
    },
    CHILD_OVER_EIGHT{
        public String toString(){
            return "Child Over 8";
        }
    },
    CHILD_UNDER_EIGHT{
        public String toString(){
            return "Child Under 8";
        }
    };
    public abstract String toString();
}