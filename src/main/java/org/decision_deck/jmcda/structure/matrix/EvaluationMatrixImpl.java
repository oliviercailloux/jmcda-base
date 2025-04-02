package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.ForwardingSparseMatrix;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;

/**
 * <p>
 * A matrix which is able to store, at specific positions given by an alternative (as the row) and a criterion (as the
 * column), an evaluation of how the given alternative performs from the point of view of the given criterion.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class EvaluationMatrixImpl extends ForwardingSparseMatrix<Alternative, Criterion> implements Evaluations {
    EvaluationMatrixImpl() {
	this(Matrixes.<Alternative, Criterion> newSparseD());
    }

    /**
     * Copy constructor, by reference. This matrix will reflect the values inside the given matrix. Modifying this
     * matrix will modify the given matrix as well.
     * 
     * @param matrix
     *            not {@code null}.
     */
    public EvaluationMatrixImpl(SparseMatrixD<Alternative, Criterion> matrix) {
	super(matrix);
    }
}
