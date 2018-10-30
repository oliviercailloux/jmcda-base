package org.decision_deck.jmcda.structure.thresholds;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ThresholdsUtils {

    static public Thresholds getFilteredReadView(Thresholds delegate, Predicate<Criterion> criteriaPredicate) {
	return new ThresholdsReadFilter(delegate, criteriaPredicate);
    }

    static public Equivalence<Thresholds> getEquivalence() {
	return new Equivalence<Thresholds>() {
	    @Override
	    public boolean doEquivalent(Thresholds t1, Thresholds t2) {
		if (!t1.getCriteria().equals(t2.getCriteria())) {
		    return false;
		}
		if (!t1.getPreferenceThresholds().equals(t2.getPreferenceThresholds())) {
		    return false;
		}
		if (!t1.getIndifferenceThresholds().equals(t2.getIndifferenceThresholds())) {
		    return false;
		}
		if (!t1.getVetoThresholds().equals(t2.getVetoThresholds())) {
		    return false;
		}
		return true;
	    }

	    @Override
	    public int doHash(Thresholds t) {
		final int hashCode = Objects.hashCode(t.getPreferenceThresholds(), t.getIndifferenceThresholds(),
			t.getVetoThresholds());
		return hashCode;
	    }
	};
    }

    static public Predicate<Thresholds> getPredicateIsEmpty() {
	return new Predicate<Thresholds>() {
	    @Override
	    public boolean apply(Thresholds input) {
		return input.isEmpty();
	    }
	};
    }

    static public Set<Criterion> getAllCriteriaFromThresholds(Collection<Thresholds> allThresholds) {
	final Set<Criterion> allCrits = Sets.newLinkedHashSet();
	for (Thresholds thresholds : allThresholds) {
	    final Set<Criterion> criteria = thresholds.getCriteria();
	    allCrits.addAll(criteria);
	}
	return allCrits;
    }

    static public Thresholds getReadView(Thresholds delegate) {
	return new ThresholdsReadFilter(delegate, Predicates.<Criterion> alwaysTrue());
    }

    static public Thresholds newThresholds() {
	return new ThresholdsImpl();
    }

    static public Thresholds newThresholds(Map<Criterion, Double> preferenceThresholds,
	    Map<Criterion, Double> indifferenceThresholds, Map<Criterion, Double> vetoThresholds) {
	return new ThresholdsImpl(preferenceThresholds == null ? ImmutableMap.<Criterion, Double> of()
		: preferenceThresholds, indifferenceThresholds == null ? ImmutableMap.<Criterion, Double> of()
		: indifferenceThresholds, vetoThresholds == null ? ImmutableMap.<Criterion, Double> of()
		: vetoThresholds);
    }

    static public Thresholds newThresholds(Thresholds source) {
	return new ThresholdsImpl(source);
    }

    /**
     * Retrieves a thresholds object which views all preference and indifference thresholds bound to the given set of
     * criteria as zeroes. The returned object is a read-only view. Changes in the given set are reflected in the
     * returned thresholds. The returned object has no vetoes thresholds.
     * 
     * @param criteria
     *            not <code>null</code>.
     * @return a read-only view of thresholds.
     */
    static public Thresholds getZeroes(Set<Criterion> criteria) {
	return new ThresholdsZeroes(criteria);
    }
}
