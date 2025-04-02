package org.decisiondeck.jmcda.structure.sorting.problem.preferences;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.thresholds.ThresholdsUtils;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.CoalitionsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles, proviles evaluations, etc., are untouched.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingPreferencesFiltering extends SortingDataForwarder implements ISortingPreferences {

    private final ISortingPreferences m_delegate;

    /**
     * Creates a view that only views the alternatives admitted by the given predicate, or equivalently, that filters
     * out the alternatives that do not pass the given predicate. This only concerns the real alternatives, the profiles
     * are untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param predicateAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     * @param predicateCriteria
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public SortingPreferencesFiltering(ISortingPreferences delegate, Predicate<Alternative> predicateAlternatives,
	    Predicate<Criterion> predicateCriteria) {
	super(new SortingDataFiltering(delegate, predicateAlternatives, predicateCriteria));
	m_delegate = delegate;
    }

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public SortingPreferencesFiltering(ISortingPreferences delegate) {
	this(delegate, null, null);
    }



    @Override
    public Coalitions getCoalitions() {
	if (getCriteriaPredicate() == null) {
	    return CoalitionsUtils.asReadView(m_delegate.getCoalitions());
	}
	return CoalitionsUtils.getFilteredView(m_delegate.getCoalitions(), getCriteriaPredicate());
    }

    @Override
    public EvaluationsRead getProfilesEvaluations() {
	EvaluationsRead source = m_delegate.getProfilesEvaluations();
	if (getCriteriaPredicate() == null) {
	    return source;
	}
	return EvaluationsUtils.getFilteredView(source, null, getCriteriaPredicate());
    }

    @Override
    public Thresholds getThresholds() {
	final Thresholds source = m_delegate.getThresholds();
	if (getCriteriaPredicate() == null) {
	    return source;
	}
	return ThresholdsUtils.getFilteredReadView(source, getCriteriaPredicate());
    }

    @Override
    public Double getWeight(Criterion criterion) {
	if (getCriteriaPredicate() != null && !getCriteriaPredicate().apply(criterion)) {
	    return null;
	}
	return m_delegate.getWeight(criterion);
    }

    @Override
    public boolean setCoalitions(Coalitions coalitions) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setProfilesEvaluations(EvaluationsRead evaluations) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setThresholds(Thresholds thresholds) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    protected SortingDataFiltering delegate() {
	return (SortingDataFiltering) super.delegate();
    }

    /**
     * @return {@code null} for allow everything.
     */
    public Predicate<Alternative> getAlternativesPredicate() {
	return delegate().getAlternativesPredicate();
    }

    /**
     * @return {@code null} to allow everything.
     */
    public Predicate<Criterion> getCriteriaPredicate() {
	return delegate().getCriteriaPredicate();
    }

}
