package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

/**
 * <p>
 * A matrix of evaluations whose values can be read. Just a shortcut for a matrix of alternatives in row and criteria in
 * columns.
 * </p>
 * <p>
 * This interface has no support for writing to the given matrix, but this does not imply that implementing objects are
 * immutable.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface EvaluationsRead extends SparseMatrixDRead<Alternative, Criterion> {
    /** Nothing more. */
}
