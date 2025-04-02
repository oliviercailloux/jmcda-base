package org.decision_deck.jmcda.structure.weights.mess;

import java.util.Collections;
import java.util.LinkedList;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.weights.Coalitions;

public class NulWeights {

    /**
     * Must have at least one weight and lambda set. TODO remove from here.
     * 
     * @return {@code true} iff at least one weight is semantically nul, i.e. does not take part in any winning
     *         coalition except the total one. If {@code true}, at least the smallest weight is nul.
     */
    public boolean hasNulWeight(Coalitions coalitions) {
	/**
	 * The smallest weight w is useful iff we can find a coalition not including w such that the coal is greater or
	 * equal to lambda minus w but smaller than lambda.
	 * 
	 * Idea: take the smallest weight, then one more, then one more, etc. until coalition is too big (>= lambda).
	 * Then backtrack by removing the last one, replace by a bigger one, and go on. If no bigger one, continue
	 * removing.
	 */
	if (coalitions.getWeights().isEmpty() || !coalitions.containsMajorityThreshold()) {
	    throw new IllegalArgumentException("Coalition should be valid.");
	}
	final LinkedList<Double> weightsOrdered = new LinkedList<Double>();
	for (Criterion criterion : coalitions.getCriteria()) {
	    final double weight = coalitions.getWeight(criterion);
	    weightsOrdered.add(Double.valueOf(weight));
	}
	Collections.sort(weightsOrdered);
	throw new UnsupportedOperationException("Not yet implemented.");
    }

}
