package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;

import com.google.common.base.Preconditions;

public class ProblemDataForwarder implements IProblemData {
    private final IProblemData m_delegate;

    /**
     * @param delegate
     *            not {@code null}.
     */
    public ProblemDataForwarder(IProblemData delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public EvaluationsRead getAlternativesEvaluations() {
	return m_delegate.getAlternativesEvaluations();
    }

    @Override
    public Set<Criterion> getCriteria() {
	return m_delegate.getCriteria();
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public Map<Criterion, Interval> getScales() {
	return m_delegate.getScales();
    }

    @Override
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value) {
	return m_delegate.setEvaluation(alternative, criterion, value);
    }

    @Override
    public boolean setEvaluations(EvaluationsRead evaluations) {
	return m_delegate.setEvaluations(evaluations);
    }

    @Override
    public boolean setScale(Criterion criterion, Interval scale) {
	return m_delegate.setScale(criterion, scale);
    }

    protected IProblemData delegate() {
	return m_delegate;
    }

}
