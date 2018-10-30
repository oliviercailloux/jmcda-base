package org.decision_deck.jmcda.utils;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.StringUtils;

/**
 * Helper methods to get string forms of various collections of MCDA objects.
 * 
 * @author Olivier Cailloux
 * 
 */
public class StringUtilsMC {
    static public String toStringDmsAndAlternatives(Map<DecisionMaker, Set<Alternative>> source) {
	return StringUtils.toStringMap(source, DecisionMaker.getIdFct(),
		StringUtils.getJoiner(Alternative.getIdFct(), ", "));
    }

}
