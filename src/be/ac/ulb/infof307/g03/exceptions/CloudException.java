package be.ac.ulb.infof307.g03.exceptions;

/**
 * Cloud exception can be thrown if the connection to the cloud is closed or non stable, or if for whatever reason it is not possible
 * to read or write information from the cloud.
 */
public class CloudException extends Exception{

    /**
     * This is the constructor for a CloudException
     * @param e the exception throws
     */
    public CloudException(Throwable e){
        super(e);
    }

    /**
     * This is the constructor for a CloudException
     * @param message to describe the error
     * @param e the exception throws
     */
    public CloudException(String message, Throwable e){
        super(message, e);
    }
}