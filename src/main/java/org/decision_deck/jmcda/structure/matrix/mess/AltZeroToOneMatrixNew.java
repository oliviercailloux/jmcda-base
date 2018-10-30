package org.decision_deck.jmcda.structure.matrix.mess;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.matrix.SparseAlternativesMatrixFuzzy;
import org.decision_deck.utils.matrix.mess.ZeroToOneMatrix;

public class AltZeroToOneMatrixNew extends ZeroToOneMatrix<Alternative, Alternative> implements SparseAlternativesMatrixFuzzy {

    public AltZeroToOneMatrixNew() {
	/** NB could also be a wrapper around a IAltZeroToOneMatrix delegate! */
	super();
    }

}
