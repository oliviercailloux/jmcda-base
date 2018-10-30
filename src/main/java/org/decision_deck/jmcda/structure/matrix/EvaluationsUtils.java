package org.decision_deck.jmcda.structure.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.MatrixesHelper;
import org.decision_deck.utils.matrix.MatrixesHelper.MatrixFactory;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class EvaluationsUtils {

    static public EvaluationsRead getReadView(EvaluationsRead evaluations) {
	return getFilteredView(evaluations, null, null);
    }

    /**
     * Returns a matrix containing the mappings in <code>unfiltered</code> satisfying the given predicates. The returned
     * matrix is a live view of <code>unfiltered</code>; changes to the source affects the view.
     * 
     * @param unfiltered
     *            not <code>null</code>.
     * @param alternativePredicate
     *            not <code>null</code>, use {@link Predicates#alwaysTrue()} for no restriction.
     * @param criterionPredicate
     *            not <code>null</code>, use {@link Predicates#alwaysTrue()} for no restriction.
     * @return not <code>null</code>, a read-only view.
     */
    static public EvaluationsRead getFilteredView(EvaluationsRead unfiltered,
	    Predicate<Alternative> alternativePredicate, Predicate<Criterion> criterionPredicate) {
	checkNotNull(unfiltered);
	return new EvaluationsView(Matrixes.getFilteredView(unfiltered, alternativePredicate, criterionPredicate));
    }

    /**
     * Returns an evaluation matrix representing a copy of the source data. Changing the source does not change the
     * copy.
     * 
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    public static Evaluations newEvaluationMatrix(EvaluationsRead source) {
	checkNotNull(source);
	final Evaluations target = newEvaluationMatrix();
	Matrixes.putAll(source, target);
	return target;
    }

    static public Evaluations newEvaluationMatrix() {
	return new EvaluationMatrixImpl();
    }

    static public boolean contains(EvaluationsRead evaluations, AlternativeEvaluations content) {
	checkNotNull(evaluations);
	checkNotNull(content);
	return Matrixes.containsRow(evaluations, content.getEvaluations());
    }

    /**
     * TODO protect somehow against entries existing in both matrixes (possibly with different values), probably delete
     * this method.
     * 
     * @param m1
     *            not <code>null</code>.
     * @param m2
     *            not <code>null</code>.
     * @return a copy of all entries.
     */
    public static Evaluations merge(SparseMatrixDRead<Alternative, Criterion> m1,
	    SparseMatrixDRead<Alternative, Criterion> m2) {
	final MatrixesHelper<Alternative, Criterion> helper = new MatrixesHelper<Alternative, Criterion>(
		new MatrixFactory<Alternative, Criterion>() {
		    @Override
		    public SparseMatrixD<Alternative, Criterion> newMatrix() {
			return newEvaluationMatrix();
		    }
		});
	return (Evaluations) helper.merge(m1, m2);
    }

    /**
     * Should be replaced by Matrix.asTable().columnMap() but table view is not yet fully implemented.
     * 
     * @param evaluations
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public Map<Criterion, Map<Alternative, Double>> asColumnMap(final EvaluationsRead evaluations) {
	checkNotNull(evaluations);
	/**
	 * Alternate impl to use if asTable.column map does not work. This should be moved somewhere else, or should be
	 * deleted when table views are implemented correctly.
	 */
	final Map<Criterion, Map<Alternative, Double>> columnMap = new SetBackedMap<Criterion, Map<Alternative, Double>>(
		evaluations.getColumns(), new Function<Criterion, Map<Alternative, Double>>() {

		    @Override
		    public Map<Alternative, Double> apply(final Criterion criterion) {
			final SetBackedMap<Alternative, Double> colMap = new SetBackedMap<Alternative, Double>(
				evaluations.getRows(), new Function<Alternative, Double>() {
				    @Override
				    public Double apply(Alternative alternative) {
					return evaluations.getEntry(alternative, criterion);
				    }
				});
			return Maps.filterValues(colMap, Predicates.notNull());
		    }
		});
	return columnMap;
    }

    /**
     * Retrieves a copy of the given evaluations matrix, with possibly renamed alternatives and criteria, and possibly
     * in a different order. The given comparators indicate the iteration order on the new sets of alternatives and
     * criteria, thus, after rename.
     * 
     * @param source
     *            not <code>null</code>.
     * @param renameAlternatives
     *            not <code>null</code>.
     * @param renameCriteria
     *            not <code>null</code>.
     * @param orderAlternatives
     *            not <code>null</code>.
     * @param orderCriteria
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public Evaluations newRenamedAndOrdered(EvaluationsRead source,
	    Function<? super Alternative, Alternative> renameAlternatives,
	    Function<? super Criterion, Criterion> renameCriteria, Comparator<? super Alternative> orderAlternatives,
	    Comparator<? super Criterion> orderCriteria) {
	final Evaluations newMatrix = newEvaluationMatrix();
	for (Alternative alternative : Ordering.from(orderAlternatives).onResultOf(renameAlternatives)
		.sortedCopy(source.getRows())) {
	    for (Criterion criterion : Ordering.from(orderCriteria).onResultOf(renameCriteria)
		    .sortedCopy(source.getColumns())) {
		final Double entry = source.getEntry(alternative, criterion);
		if (entry == null) {
		    continue;
		}
		final double value = entry.doubleValue();
		newMatrix.put(renameAlternatives.apply(alternative), renameCriteria.apply(criterion), value);
	    }
	}
	return newMatrix;
    }
}
