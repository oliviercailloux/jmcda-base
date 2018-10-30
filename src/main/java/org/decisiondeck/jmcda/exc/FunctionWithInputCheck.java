package org.decisiondeck.jmcda.exc;

public interface FunctionWithInputCheck<F, V> {
	public V apply(F input) throws InvalidInputException;
}