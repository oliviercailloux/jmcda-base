/**
 * 
 */
package org.decision_deck.jmcda.structure.matrix.mess;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.AlternativeEvaluations;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

import com.google.common.collect.Table;

/**
 * Better use a normal evaluations matrix and wrap things into {@link AlternativeEvaluations} to provide view of single
 * rows.
 * 
 * @author Olivier Cailloux
 * 
 */
public class EvaluationsByRows implements Evaluations {
    private int m_values;

    public EvaluationsByRows() {
	m_values = 0;
    }

    public Collection<AlternativeEvaluations> values() {
	return m_evaluations.values();
    }

    public boolean contains(AlternativeEvaluations evaluations) {
	return m_evaluations.values().contains(evaluations);
    }

    public void put(Alternative row, AlternativeEvaluations evaluations) {
	final AlternativeEvaluations previous = m_evaluations.put(row, evaluations);
	final int previousValues = previous == null ? 0 : previous.getEvaluations().size();
	m_values += (evaluations.getEvaluations().size() - previousValues);
    }

    /**
     * Contains no empty evaluations.
     */
    private final Map<Alternative, AlternativeEvaluations> m_evaluations = new HashMap<Alternative, AlternativeEvaluations>();

    @Override
    public Double put(Alternative row, Criterion column, double value) {
	final AlternativeEvaluations eval;
	if (m_evaluations.get(row) == null) {
	    eval = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	    m_evaluations.put(row, eval);
	} else {
	    eval = m_evaluations.get(row);
	}
	final Double previous = eval.getEvaluations().put(column, Double.valueOf(value));
	if (previous == null) {
	    ++m_values;
	}
	return previous;
    }

    @Override
    public Double remove(Alternative row, Criterion column) {
	if (m_evaluations.get(row) == null) {
	    return null;
	}
	final Map<Criterion, Double> evaluations = m_evaluations.get(row).getEvaluations();
	if (!evaluations.containsKey(column)) {
	    return null;
	}
	final Double value = evaluations.remove(column);
	if (evaluations.isEmpty()) {
	    m_evaluations.remove(row);
	}
	--m_values;
	return value;
    }

    @Override
    public boolean approxEquals(SparseMatrixDRead<Alternative, Criterion> m2, double imprecision) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Set<Criterion> getColumns() {
	throw new UnsupportedOperationException();
    }

    @Override
    public Double getEntry(Alternative row, Criterion column) {
	final AlternativeEvaluations eval = m_evaluations.get(row);
	if (eval == null) {
	    return null;
	}
	return eval.getEvaluations().get(column);
    }

    @Override
    public Set<Alternative> getRows() {
	return m_evaluations.keySet();
    }

    @Override
    public int getValueCount() {
	return m_values;
    }

    @Override
    public boolean isComplete() {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
	return m_evaluations.isEmpty();
    }

    @Override
    public Table<Alternative, Criterion, Double> asTable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeColumn(Criterion column) {
        if (!getColumns().contains(column)) {
            return false;
        }
        for (Alternative row : getRows()) {
            remove(row, column);
        }
        return true;
    }

    @Override
    public boolean removeRow(Alternative row) {
        if (!getRows().contains(row)) {
            return false;
        }
	for (Criterion column : getColumns()) {
            remove(row, column);
        }
        return true;
    }

    @Override
    public double getValue(Alternative row, Criterion column) {
        final Double entry = getEntry(row, column);
        if (entry == null) {
            throw new IllegalArgumentException("No value at " + row + ", " + column + ".");
        }
        return entry.doubleValue();
    }

}