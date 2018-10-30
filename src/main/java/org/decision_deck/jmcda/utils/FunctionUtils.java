package org.decision_deck.jmcda.utils;

import org.decisiondeck.jmcda.exc.FunctionWithInputCheck;
import org.decisiondeck.jmcda.exc.InvalidInputException;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;

public class FunctionUtils {

    static public <A, B, C> FunctionWithInputCheck<A, C> compose(final Function<B, ? extends C> g,
	    final FunctionWithInputCheck<A, ? extends B> f) {
	final FunctionWithInputCheck<B, ? extends C> functionWithInputCheck = functionWithInputCheck(g);
	return compose(functionWithInputCheck, f);
    }

    static public <A, B, C> FunctionWithInputCheck<A, C> compose(final FunctionWithInputCheck<B, ? extends C> g,
	    final Function<A, ? extends B> f) {
	final FunctionWithInputCheck<A, ? extends B> functionWithInputCheck = functionWithInputCheck(f);
	final FunctionWithInputCheck<A, C> composed = compose(g, functionWithInputCheck);
	return composed;
    }

    static public <A, B, C> FunctionWithInputCheck<A, C> compose(final FunctionWithInputCheck<B, ? extends C> g,
	    final FunctionWithInputCheck<A, ? extends B> f) {
	return new FunctionWithInputCheck<A, C>() {
	    @Override
	    public C apply(A input) throws InvalidInputException {
		final B intermediary = f.apply(input);
		try {
		    return g.apply(intermediary);
		} catch (InvalidInputException exc) {
		    throw new InvalidInputException("Exception while attempting to transform input " + input + ".", exc);
		}
	    }
	};
    }

    /**
     * Transforms a function into a function with input check. This is mainly intended as a technical method to provide
     * type compatibility where a function with input check is expected instead of a function. The returned function
     * does not throw {@link InvalidInputException}s.
     * 
     * @param <F>
     *            the domain of the given function: the type of value it expects.
     * @param <V>
     *            the codomain of the given function: the type of value it returns.
     * @param <F2>
     *            the domain of the returned function: the type of value it expects.
     * @param <V2>
     *            the codomain of the returned function: the type of value it returns.
     * @param f
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <F, F2 extends F, V2, V extends V2> FunctionWithInputCheck<F2, V2> functionWithInputCheck(
	    final Function<F, V> f) {
	Preconditions.checkNotNull(f);
	return new FunctionWithInputCheck<F2, V2>() {
	    @Override
	    public V2 apply(F2 input) {
		return f.apply(input);
	    }
	};
    }

    static public <F, V> FunctionWithInputCheck<F, V> constant(V value) {
	return functionWithInputCheck(Functions.constant(value));
    }

    /**
     * @param <E>
     *            the input and output type of the function.
     * @return the identity function.
     */
    static public <E> FunctionWithInputCheck<E, E> identity() {
	return functionWithInputCheck(Functions.<E> identity());
    }
}
