package org.decision_deck.jmcda.structure.scores;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.collection.extensional_order.KeyValueOrderedMap;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingNavigableMap;
import com.google.common.collect.Ordering;

/**
 * <p>
 * An ordered map of alternatives with their scores (ordered by score, ascending, thus worst alternatives first, then
 * (in case of ex-æquo) by alternative id ascending). Note that this map ordering depends on the key
 * <em>as well as on the values</em> , which is unusual and has implications on the possible uses of such object. E.g.
 * it is not allowed to change the value of an entry while iterating (as doing that would impact the underlying
 * sequence).
 * </p>
 * <p>
 * This map does not allow {@code null} keys or values. A put operation with a {@code null} value will throw
 * an exception.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class AlternativesScores extends ForwardingNavigableMap<Alternative, Double> {
    private KeyValueOrderedMap<Alternative, Double> m_delegate;
    /**
     * <P>
     * Two maps of alternative scores are "approximately equal" to a given degree of precision iff they contain values
     * for the same mappings and the value they contain for each mapping are not more different than the given allowed
     * imprecision.
     * </P>
     * <P>
     * This definition implies that the ordering of the alternatives could be different while two maps still being
     * equal, i.e. approximate equality does <em>not</em> imply same ordering.
     * </P>
     * 
     * @param s2
     *            the scores to which to compare this object for approximate equality. If {@code null}, this method
     *            returns {@code false}.
     * @param imprecision
     *            the maximal imprecision allowed for accepting equality.
     * @return {@code true} iff the given scores map is approximately equal to this one.
     */
    public boolean approxEquals(AlternativesScores s2, double imprecision) {
	if (s2 == null) {
	    return false;
	}
	final Set<Alternative> keySet = keySet();
	if (!keySet.equals(s2.keySet())) {
	    return false;
	}

	for (Alternative alternative : keySet) {
	    final double score1 = get(alternative).doubleValue();
	    final double score2 = s2.get(alternative).doubleValue();
	    if (Math.abs(score2 - score1) > imprecision) {
		return false;
	    }
	}

	return true;
    }

    public AlternativesScores() {
	super();
	final Ordering<Double> doubleNatural = Ordering.<Double> natural();
	final Function<Entry<Alternative, Double>, Double> getValue = CollectionUtils
		.<Alternative, Double> getFunctionEntryValue();
	final Ordering<Map.Entry<Alternative, Double>> compareUsingValues = doubleNatural.onResultOf(getValue);
	final Ordering<Alternative> alternativesNatural = Ordering.<Alternative> natural();
	final Function<Entry<Alternative, Double>, Alternative> getKey = CollectionUtils
		.<Alternative, Double> getFunctionEntryKey();
	final Ordering<Map.Entry<Alternative, Double>> compareUsingKeys = alternativesNatural.onResultOf(getKey);
	m_delegate = KeyValueOrderedMap.create(compareUsingValues.compound(compareUsingKeys));
    }



    public AlternativesScores(Map<Alternative, Double> map) {
	this();
	putAll(map);
    }

    /**
     * Retrieves the score associated to the given alternative. This object must contain the given alternative.
     * 
     * @param alternative
     *            must be in this object.
     * @return the associated score.
     */
    public double getScore(Alternative alternative) {
	checkArgument(containsKey(alternative));
	return get(alternative).doubleValue();
    }

    @Override
    protected NavigableMap<Alternative, Double> delegate() {
	return m_delegate;
    }

    public Double put(Alternative alternative, double score) {
	return put(alternative, Double.valueOf(score));
    }

}
