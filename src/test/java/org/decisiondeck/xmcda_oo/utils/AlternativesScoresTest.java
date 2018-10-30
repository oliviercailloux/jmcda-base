package org.decisiondeck.xmcda_oo.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.scores.AlternativesScores;
import org.junit.Test;

public class AlternativesScoresTest {
    @Test
    public void testOrdering() throws Exception {
	final Alternative worstAlt = new Alternative("a01");
	final Alternative middleAlt = new Alternative("a00");
	final Alternative bestAlt = new Alternative("a03");
	final Alternative perfectAlt = new Alternative("a02");
	final AlternativesScores sc = new AlternativesScores();
	sc.put(middleAlt, 150);
	sc.put(bestAlt, 200);
	sc.put(perfectAlt, 1000);
	sc.put(worstAlt, 100);
	final Iterator<Alternative> alternativesIterator = sc.keySet().iterator();
	final Alternative alt1 = alternativesIterator.next();
	final Alternative alt2 = alternativesIterator.next();
	final Alternative alt3 = alternativesIterator.next();
	final Alternative alt4 = alternativesIterator.next();
	assertEquals("Unexpected ordering.", worstAlt, alt1);
	assertEquals("Unexpected ordering.", middleAlt, alt2);
	assertEquals("Unexpected ordering.", bestAlt, alt3);
	assertEquals("Unexpected ordering.", perfectAlt, alt4);
	assertFalse("Unexpected ordering.", middleAlt.equals(alt4));
    }

    /**
     * Tests if changing a value, hence, changing the order, introduces no error.
     * 
     * @throws Exception
     *             heh
     */
    @Test
    public void testDuplicate() throws Exception {
        final AlternativesScores sc = new AlternativesScores();
	final Alternative a1 = new Alternative("1");
	final Alternative a3 = new Alternative("3");
	final Alternative a4 = new Alternative("4");
	final Alternative a5 = new Alternative("5");
	sc.put(a1, 1d);
	sc.put(a1, 2d);
	sc.put(a3, 3d);
	sc.put(a5, 5d);
	sc.put(a4, 4d);
	sc.put(a1, 1d);
	sc.put(a4, 4d);
	sc.put(a5, 1.5d);
	assertTrue(sc.containsKey(a5));
	assertTrue(sc.containsKey(a4));
	assertTrue(sc.containsKey(a3));
	assertTrue(sc.containsKey(a1));
        assertEquals(4, sc.entrySet().size());
    }
}
