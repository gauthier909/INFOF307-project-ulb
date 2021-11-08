package be.ac.ulb.infof307.g03.exceptions;

/**
 * Local export exception can be thrown when trying to export a project locally on a read-only directory, on corrupted directory, or for whatever writing impossibility.
 */
public class LocalExportException extends Exception{

    /**
     * This is the constructor for a LocalExportException
     * @param e the exception throws
     */
    public LocalExportException(Throwable e){
        super(e);
    }

    /**
     * This is the constructor for a LocalExportException
     * @param message to describe the error
     */
    public LocalExportException(String message){
        super(message);
    }
}
