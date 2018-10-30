package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.matrix.ForwardingSparseMatrixRead;
import org.decision_deck.utils.matrix.SparseMatrixDRead;

/**
 * A read-only view of a delegate matrix. This view is read-only. An instance of this class is immutable iff the
 * delegate is immutable, or if no external references to the delegate is kept.
 * 
 * @author Olivier Cailloux
 * 
 */
public class EvaluationsView extends ForwardingSparseMatrixRead<Alternative, Criterion> implements EvaluationsRead {
    /**
     * Builds a new evaluations view delegating to the given evaluations.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public EvaluationsView(SparseMatrixDRead<Alternative, Criterion> delegate) {
	super(delegate);
    }

}
