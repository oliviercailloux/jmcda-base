package org.decision_deck.jmcda.structure.matrix;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.matrix.SparseMatrixFuzzy;

/**
 * A zero-to-one matrix of alternatives. Currently used to represent concordance, discordance, outranking relations...,
 * but this should be changed.
 * 
 * @author Olivier Cailloux
 * 
 */
public interface SparseAlternativesMatrixFuzzy extends SparseMatrixFuzzy<Alternative, Alternative> {
    // just a shortcut!
}
