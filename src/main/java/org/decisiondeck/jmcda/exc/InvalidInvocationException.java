package org.decisiondeck.jmcda.exc;

public class InvalidInvocationException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInvocationException() {
        super();
    }

    public InvalidInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInvocationException(String message) {
        super(message);
    }

    public InvalidInvocationException(Throwable cause) {
        super(cause);
    }

}