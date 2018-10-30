package org.decisiondeck.jmcda.exc;

/**
 * Indicates an exception related to missing or incorrect information (e.g., a threshold greater than an other one which
 * should be the greatest).
 * 
 * @author Olivier Cailloux
 * 
 */
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInputException() {
	super();
    }

    public InvalidInputException(String message, Throwable cause) {
	super(message, cause);
    }

    public InvalidInputException(String message) {
	super(message);
    }

    public InvalidInputException(Throwable cause) {
	super(cause);
    }

}