package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.SparseMatrixD;

/**
 * A matrix of evaluations. Just a shortcut for a matrix of alternatives in row and criteria in columns.
 * 
 * @author Olivier Cailloux
 * 
 */
public interface Evaluations extends SparseMatrixD<Alternative, Criterion>, EvaluationsRead {
    /** Nothing more. */
}
