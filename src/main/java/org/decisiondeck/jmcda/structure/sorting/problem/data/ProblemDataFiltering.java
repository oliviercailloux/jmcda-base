package org.decisiondeck.jmcda.structure.sorting.problem.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created.
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
public class ProblemDataFiltering implements IProblemData {

    /**
     * {@code null} for no filter.
     */
    private final Predicate<Alternative> m_predicateAlternatives;
    private final IProblemData m_delegate;
    private final Predicate<Criterion> m_predicateCriteria;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public ProblemDataFiltering(ISortingData delegate) {
	this(delegate, null, null);
    }

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. To filter a constant set of alternatives, use
     * {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param predicateAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     * @param predicateCriteria
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public ProblemDataFiltering(IProblemData delegate, Predicate<Alternative> predicateAlternatives,
	    Predicate<Criterion> predicateCriteria) {
	checkNotNull(delegate);
	m_delegate = delegate;
	m_predicateAlternatives = predicateAlternatives;
	m_predicateCriteria = predicateCriteria;
    }

    /**
     * Retrieves a read-only view of the alternatives.
     */
    @Override
    public Set<Alternative> getAlternatives() {
	final Set<Alternative> source = m_delegate.getAlternatives();
	final Set<Alternative> filtered = m_predicateAlternatives == null ? source : Sets.filter(source,
		m_predicateAlternatives);
	return Collections.unmodifiableSet(filtered);
    }

    /**
     * Retrieves a read-only view of the criteria.
     */
    @Override
    public Set<Criterion> getCriteria() {
	final Set<Criterion> source = m_delegate.getCriteria();
	final Set<Criterion> filtered = m_predicateCriteria == null ? source : Sets.filter(source, m_predicateCriteria);
	return Collections.unmodifiableSet(filtered);
    }

    @Override
    public EvaluationsRead getAlternativesEvaluations() {
	return EvaluationsUtils.getFilteredView(m_delegate.getAlternativesEvaluations(), m_predicateAlternatives,
		m_predicateCriteria);
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

    /**
     * @return {@code null} for allow everything.
     */
    public Predicate<Alternative> getAlternativesPredicate() {
	return m_predicateAlternatives;
    }

    /**
     * @return {@code null} to allow everything.
     */
    public Predicate<Criterion> getCriteriaPredicate() {
	return m_predicateCriteria;
    }

    @Override
    public Map<Criterion, Interval> getScales() {
	final Map<Criterion, Interval> source = m_delegate.getScales();
	if (m_predicateCriteria == null) {
	    return source;
	}
	return Maps.filterKeys(source, m_predicateCriteria);
    }

}
