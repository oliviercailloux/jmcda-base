package org.decisiondeck.jmcda.persist.utils;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.StringUtils;
import org.decision_deck.utils.collection.SetBackedMap;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class ExportSettingsUtils {

    public static class ReorderDms implements Function<Set<DecisionMaker>, Set<DecisionMaker>> {
	private final ExportSettings m_exportSettings;

	public ReorderDms(ExportSettings exportSettings) {
	    m_exportSettings = exportSettings;
	}

	@Override
	public Set<DecisionMaker> apply(Set<DecisionMaker> input) {
	    return m_exportSettings.interOrderDms(input);
	}
    }

    public static class ReorderAlternatives implements Function<Set<Alternative>, Set<Alternative>> {
        private final ExportSettings m_exportSettings;
    
        public ReorderAlternatives(ExportSettings exportSettings) {
            m_exportSettings = exportSettings;
        }
    
        @Override
        public Set<Alternative> apply(Set<Alternative> input) {
            return m_exportSettings.interOrderAlternatives(input);
        }
    }

    static public String toStringDmsAndAlternatives(Map<DecisionMaker, Set<Alternative>> allAlternatives,
	    ExportSettings s) {
	final Set<DecisionMaker> dms = new ReorderDms(s).apply(allAlternatives.keySet());
	final Function<DecisionMaker, Set<Alternative>> dmToAlts = Functions.forMap(allAlternatives);
	final Function<Set<Alternative>, Set<Alternative>> reorder = new ReorderAlternatives(s);
	final SetBackedMap<DecisionMaker, Set<Alternative>> reordered = SetBackedMap.create(dms,
		Functions.compose(reorder, dmToAlts));

	final Function<Iterable<Alternative>, String> joiner = StringUtils.getJoiner(s.getAlternativesToString(), ", ");

	return StringUtils.toStringMap(reordered, s.getDmsToString(), joiner);
    }

}
