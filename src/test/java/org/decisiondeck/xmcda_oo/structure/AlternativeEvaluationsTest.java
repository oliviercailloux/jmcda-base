package org.decisiondeck.xmcda_oo.structure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.PreferenceDirection;
import org.decision_deck.jmcda.structure.matrix.AlternativeEvaluations;
import org.junit.Test;

import com.google.common.collect.Maps;

public class AlternativeEvaluationsTest {
    @Test
    public void testDominance() throws Exception {
	final AlternativeEvaluations main = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	final Criterion g1 = new Criterion("g1");
	final Criterion g2 = new Criterion("g2");
	final Map<Criterion, PreferenceDirection> directions = Maps.newLinkedHashMap();
	directions.put(g1, PreferenceDirection.MAXIMIZE);
	directions.put(g2, PreferenceDirection.MINIMIZE);
	main.getEvaluations().put(g1, Double.valueOf(0d));
	main.getEvaluations().put(g2, Double.valueOf(0d));
	final AlternativeEvaluations better = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	better.getEvaluations().put(g1, Double.valueOf(2d));
	better.getEvaluations().put(g2, Double.valueOf(-0.1d));
	assertTrue(better.dominates(main, directions));
	assertFalse(main.dominates(better, directions));
	final AlternativeEvaluations worst = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	worst.getEvaluations().put(g1, Double.valueOf(-2d));
	worst.getEvaluations().put(g2, Double.valueOf(0.1d));
	assertTrue(main.dominates(worst, directions));
	assertFalse(worst.dominates(main, directions));
	final AlternativeEvaluations incomp = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	incomp.getEvaluations().put(g1, Double.valueOf(2d));
	incomp.getEvaluations().put(g2, Double.valueOf(0.1d));
	assertFalse(incomp.dominates(main, directions));
	assertFalse(main.dominates(incomp, directions));
    }

    @Test
    public void testEquality() throws Exception {
	final AlternativeEvaluations main = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	final Criterion g1 = new Criterion("g1");
	final Criterion newG1 = new Criterion("g1");
	final Criterion g2 = new Criterion("g2");
	main.getEvaluations().put(g1, Double.valueOf(5f));
	main.getEvaluations().put(g2, Double.valueOf(2f));
	final AlternativeEvaluations equal = new AlternativeEvaluations(new HashMap<Criterion, Double>());
	equal.getEvaluations().put(newG1, Double.valueOf(5f));
	equal.getEvaluations().put(g2, Double.valueOf(2f));
	assertTrue(equal.equals(main));
    }
}
