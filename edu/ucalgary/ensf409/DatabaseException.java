package edu.ucalgary.ensf409;
/**
 * An exception thrown by methods of the class {@code edu.ucalgary.ensf409.InventoryManager},
 * whenever an exception-causing state is encountered at runtime that is not an {@code SQLException}.
 * @see java.sql.SQLException
 */
public class DatabaseException extends Exception{
    public DatabaseException(){
        super("Something went wrong while trying to access the database");
    }
    public DatabaseException(String msg){
        super(msg);
    }
}
