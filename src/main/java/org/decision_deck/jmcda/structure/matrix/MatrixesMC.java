package org.decision_deck.jmcda.structure.matrix;



public class MatrixesMC {
    static public <R, C> SparseAlternativesMatrixFuzzy newAlternativesFuzzy() {
	return new SparseAlternativesMatrixFuzzyImpl();
    }
}
