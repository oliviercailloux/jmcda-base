package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.PredicateUtils;
import org.decision_deck.utils.matrix.ForwardingSparseMatrix;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.matrix.ValidatingDecoratedMatrix;

import com.google.common.base.Predicate;

class SparseAlternativesMatrixFuzzyImpl extends ForwardingSparseMatrix<Alternative, Alternative> implements
	SparseAlternativesMatrixFuzzy {

    public static Predicate<Double> VALIDATOR = PredicateUtils.inBetween(0d, 1d);

    /**
     * Creates a new matrix decorating the given matrix by ensuring every element it contains is between zero and one.
     * 
     * @param delegate
     *            not <code>null</code>, must be empty.
     */
    SparseAlternativesMatrixFuzzyImpl(SparseMatrixD<Alternative, Alternative> delegate) {
	super(new ValidatingDecoratedMatrix<Alternative, Alternative>(delegate, VALIDATOR));
    }

    /**
     * Creates a new matrix ensuring every element it contains is between zero and one.
     * 
     */
    SparseAlternativesMatrixFuzzyImpl() {
	super(Matrixes.<Alternative, Alternative> newValidating(VALIDATOR));
    }
}
