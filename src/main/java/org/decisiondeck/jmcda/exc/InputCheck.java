package org.decisiondeck.jmcda.exc;

public class InputCheck {
    static public void check(boolean condition) throws InvalidInputException {
	if (!condition) {
	    throw new InvalidInputException();
	}
    }

    static public void check(boolean condition, String message) throws InvalidInputException {
	if (!condition) {
	    throw new InvalidInputException(message);
	}
    }
}
