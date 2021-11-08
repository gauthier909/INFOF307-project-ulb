package be.ac.ulb.infof307.g03.exceptions;

/**
 * DAO exception are thrown whenever a SQL query fails inside of a Dao object from the entire application.
 */
public class DaoException extends Exception{
    /**
     * This is the constructor for a DaoException
     * @param e the exception throws
     */
    public DaoException(Throwable e){
        super(e);
    }

    /**
     * This is the constructor for a DaoException
     * @param message to describe the error
     * @param e the exception throws
     */
    public DaoException(String message, Throwable e){
        super(message, e);
    }
}
