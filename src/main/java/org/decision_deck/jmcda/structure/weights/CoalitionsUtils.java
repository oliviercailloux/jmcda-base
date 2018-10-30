package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Equivalence;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;

public class CoalitionsUtils {

    public Set<Criterion> getAllCriteria(Collection<Coalitions> allCoals) {
	final Set<Criterion> allCrits = Sets.newLinkedHashSet();
	for (Coalitions coals : allCoals) {
	    final Set<Criterion> criteria = coals.getCriteria();
	    allCrits.addAll(criteria);
	}
	return allCrits;
    }

    static public Coalitions newCoalitions(Weights source, double majorityThreshold) {
	final Coalitions coalitions = new CoalitionsImpl();
	coalitions.getWeights().putAll(source);
	coalitions.setMajorityThreshold(majorityThreshold);
	return coalitions;
    }

    static public Coalitions newCoalitions() {
	return new CoalitionsImpl();
    }

    /**
     * Retrieves a read-only view of the given coalitions.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public Coalitions asReadView(Coalitions delegate) {
	checkNotNull(delegate);
	if (delegate instanceof CoalitionsView) {
	    return delegate;
	}
	return new CoalitionsView(delegate);
    }

    /**
     * Retrieves a read-only view of the given coalitions, viewing only the criteria allowed by the given predicate.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @param predicateCriteria
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public Coalitions getFilteredView(Coalitions delegate, Predicate<Criterion> predicateCriteria) {
	checkNotNull(delegate);
	checkNotNull(predicateCriteria);
	return new CoalitionsView(delegate, predicateCriteria);
    }

    public static Equivalence<Coalitions> getCoalitionsEquivalenceRelation() {
	return new Equivalence<Coalitions>() {
	    @Override
	    public boolean doEquivalent(Coalitions c1, Coalitions c2) {
		if (c1.containsMajorityThreshold() != c2.containsMajorityThreshold()) {
		    return false;
		} else if (c1.containsMajorityThreshold() && c2.containsMajorityThreshold()) {
		    if (c1.getMajorityThreshold() != c2.getMajorityThreshold()) {
			return false;
		    }
		}
		if (c1.getWeights() == null) {
		    if (c2.getWeights() != null) {
			return false;
		    }
		} else if (!c1.getWeights().equals(c2.getWeights())) {
		    return false;
		}
		return true;
	    }

	    @Override
	    public int doHash(Coalitions c) {
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((!c.containsMajorityThreshold()) ? 0 : Doubles.hashCode(c.getMajorityThreshold()));
		result = prime * result + c.getWeights().hashCode();
		return result;
	    }

	};
    }

    static public boolean approxEqual(Coalitions c1, Coalitions c2, double tolerance) {
	if (c1 == c2) {
	    return true;
	}
	if (c2 == null) {
	    return false;
	}
	if (c1.containsMajorityThreshold() != c2.containsMajorityThreshold()) {
	    return false;
	} else if (c1.containsMajorityThreshold() && c2.containsMajorityThreshold()) {
	    if (Math.abs(c1.getMajorityThreshold() - c2.getMajorityThreshold()) > tolerance) {
		return false;
	    }
	}
	if (!c1.getWeights().approxEquals(c2.getWeights(), tolerance)) {
	    return false;
	}
	return true;
    }

    /**
     * Provides a copy of the given source contents.
     * 
     * @param source
     *            not <code>null</code>.
     */
    static public Coalitions newCoalitions(Coalitions source) {
	if (source instanceof CoalitionsImpl) {
	    return new CoalitionsImpl((CoalitionsImpl) source);
	}

	final Coalitions coalitions = newCoalitions();
	coalitions.getWeights().putAll(source.getWeights());
	if (source.containsMajorityThreshold()) {
	    coalitions.setMajorityThreshold(source.getMajorityThreshold());
	}
	return coalitions;
    }

    static public Coalitions wrap(Weights weights) {
	return new CoalitionsImpl(weights);
    }

    static public Coalitions newCoalitions(Weights source) {
	final Coalitions coalitions = new CoalitionsImpl();
	coalitions.getWeights().putAll(source);
	return coalitions;
    }

}
