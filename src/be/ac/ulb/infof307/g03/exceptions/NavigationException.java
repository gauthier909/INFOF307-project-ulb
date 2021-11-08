package be.ac.ulb.infof307.g03.exceptions;

/**
 * NavigationException can be thrown whenever navigation between windows of our application cannot be realized.
 */
public class NavigationException extends Exception{

    /**
     * This the constructor for a navigation exception.
     * @param e the exception throws
     */
    public NavigationException(Throwable e){
        super(e);
    }

    /**
     * This the constructor for a navigation exception.
     * @param message to describe the error
     * @param e the exception throws
     */
    public NavigationException(String message, Throwable e){
        super(message, e);
    }
}
