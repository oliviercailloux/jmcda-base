package org.decision_deck.jmcda.structure.matrix;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.PreferenceDirection;

import com.google.common.collect.Maps;

public class AlternativeEvaluations {
    private Map<Criterion, Double> m_evaluations;

    /**
     * @return the evaluations contained in this object. Not <code>null</code>. Can also be used to modify this object's
     *         evaluations.
     */
    public Map<Criterion, Double> getEvaluations() {
	return m_evaluations;
    }

    /**
     * @param criterion
     *            not <code>null</code>.
     * @return <code>null</code> iff no evaluation is defined for that criterion (i.e. that criterion is not contained
     *         in this object).
     */
    public Double getEvaluation(Criterion criterion) {
	if (criterion == null) {
	    throw new NullPointerException();
	}
	return m_evaluations.get(criterion);
    }

    /**
     * @param criterion
     *            not <code>null</code>.
     * @param evaluation
     *            <code>null</code> to remove the evaluation associated to the given criterion.
     * @return the previous evaluation associated to this criterion, or <code>null</code> if there was none.
     */
    public Double putEvaluation(Criterion criterion, Double evaluation) {
	if (criterion == null) {
	    throw new NullPointerException();
	}
	return m_evaluations.put(criterion, evaluation);
    }

    public AlternativeEvaluations() {
	m_evaluations = new HashMap<Criterion, Double>();
    }

    /**
     * Sets the order on which the criteria are enumerated when using the {@link #toString()} method. If other criteria
     * are added afterwards to this object, the order is not defined.
     * 
     * @param critsOrder
     *            will be iterated over, thus should be ordered (a {@link SortedSet} or a {@link LinkedHashSet} may be
     *            used, e.g.). Must contain exactly the same set of criteria this object contains evaluations for.
     */
    public void setOrder(Set<Criterion> critsOrder) {
	if (!critsOrder.equals(m_evaluations.keySet())) {
	    throw new IllegalStateException("The given criteria are not the ones used in this evaluation.");
	}
	final Map<Criterion, Double> newEvals = Maps.newLinkedHashMap();
	for (Criterion crit : critsOrder) {
	    newEvals.put(crit, m_evaluations.get(crit));
	}
	m_evaluations = newEvals;
    }

    /**
     * Iteration order of the given evaluations is kept: this is used in the {@link #toString()} method, for debug.
     * 
     * @param evaluations
     *            not <code>null</code>.
     */
    public AlternativeEvaluations(Map<Criterion, Double> evaluations) {
	if (evaluations == null) {
	    throw new NullPointerException();
	}
	m_evaluations = Maps.newLinkedHashMap(evaluations);
    }

    @Override
    public String toString() {
	final int maxLen = 4;
	StringBuilder builder = new StringBuilder();
	builder.append("AlternativeEvaluations [");
	int i = 0;
	for (Iterator<Entry<Criterion, Double>> iterator = m_evaluations.entrySet().iterator(); iterator.hasNext()
		&& i < maxLen; i++) {
	    if (i > 0) {
		builder.append(", ");
	    }
	    final Entry<Criterion, Double> eval = iterator.next();
	    builder.append(eval.getKey().getId() + ": " + eval.getValue().doubleValue());
	}
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((m_evaluations == null) ? 0 : m_evaluations.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	AlternativeEvaluations other = (AlternativeEvaluations) obj;
	if (!m_evaluations.equals(other.m_evaluations)) {
	    return false;
	}
	return true;
    }

    /**
     * @param target
     *            must be evaluated on the same criteria set than this object.
     * @param directions
     *            must contain directions for every criteria used in the evaluations.
     * @return <code>true</code> iff this object strictly dominates the given one.
     */
    public boolean dominates(AlternativeEvaluations target, Map<Criterion, PreferenceDirection> directions) {
	if (!target.getEvaluations().keySet().equals(m_evaluations.keySet())) {
	    throw new IllegalArgumentException("Different criteria set.");
	}
	checkArgument(directions.keySet().containsAll(m_evaluations.keySet()));
	boolean existsBetter = false;
	boolean existsWorst = false;
	for (Criterion criterion : m_evaluations.keySet()) {
	    final Double targetValue = target.getEvaluations().get(criterion);
	    final Double thisValue = m_evaluations.get(criterion);
	    final PreferenceDirection pDir = directions.get(criterion);
	    assert (pDir != null);
	    final int comp = thisValue.compareTo(targetValue);
	    switch (pDir) {
	    case MAXIMIZE:
		if (comp > 0) {
		    existsBetter = true;
		} else if (comp < 0) {
		    existsWorst = true;
		}
		break;
	    case MINIMIZE:
		if (comp < 0) {
		    existsBetter = true;
		} else if (comp > 0) {
		    existsWorst = true;
		}
		break;
	    default:
		throw new IllegalStateException("Invalid preference direction.");
	    }
	    if (existsBetter && existsWorst) {
		return false;
	    }
	}
	return existsBetter;// && !existsWorst (implied)
    }

}