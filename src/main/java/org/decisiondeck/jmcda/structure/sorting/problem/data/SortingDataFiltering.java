package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Collections;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles are untouched.
 * </p>
 * <p>
 * The view is read-only because a writeable view allowing to filter the alternatives would have a strange behavior.
 * Adding an alternative could succeed in adding it in the underlying object, but then it could be filtered from the
 * view. Thus after a successful add of an alternative that was not contained in the set of alternatives, the number of
 * alternatives would still be seen as constant. Moreover, it is difficult to thing about a concrete use case for such a
 * filtered writeable object.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingDataFiltering extends ProblemDataForwarder implements ISortingData {

    private final Set<Alternative> m_allAlternativesView;
    private final ISortingData m_delegate;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public SortingDataFiltering(ISortingData delegate) {
	this(delegate, null, null);
    }

    @Override
    protected ProblemDataFiltering delegate() {
	return (ProblemDataFiltering) super.delegate();
    }

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param filterAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     * @param filterCriteria
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public SortingDataFiltering(ISortingData delegate, Predicate<Alternative> filterAlternatives,
	    Predicate<Criterion> filterCriteria) {
	super(new ProblemDataFiltering(delegate, filterAlternatives, filterCriteria));
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
	m_allAlternativesView = Sets.union(getAlternatives(), getProfiles());
    }

    /**
     * @return a read-only view of the profiles.
     */
    @Override
    public Set<Alternative> getProfiles() {
	final Set<Alternative> source = m_delegate.getProfiles();
	return Collections.unmodifiableSet(source);
    }

    @Override
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setEvaluations(EvaluationsRead evaluations) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setScale(Criterion criterion, Interval scale) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public CatsAndProfs getCatsAndProfs() {
	return Categories.getReadView(m_delegate.getCatsAndProfs());
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

    @Override
    public Set<Alternative> getAllAlternatives() {
	return m_allAlternativesView;
    }

}
