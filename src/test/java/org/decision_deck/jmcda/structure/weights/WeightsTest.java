package org.decision_deck.jmcda.structure.weights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.decision_deck.jmcda.structure.Criterion;
import org.junit.Test;

@SuppressWarnings("boxing")
public class WeightsTest {

    @Test
    public void testAddRemove() {
        final WeightsImpl weights = WeightsImpl.create();
	final Criterion g1 = new Criterion("g1");
	weights.put(g1, 10d);
	assertTrue(weights.containsKey(g1));
        assertEquals(10d, weights.getSum(), 1e-6d);
        weights.put(new Criterion("g2"), 90d);
        assertEquals(100d, weights.getSum(), 1e-6d);
	weights.remove(new Criterion("g1"));
	assertEquals(90d, weights.getSum(), 1e-6d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
	final WeightsImpl weights = WeightsImpl.create();
	weights.put(new Criterion("g1"), null);
    }

    @Test
    public void testClear() {
        final WeightsImpl weights = WeightsImpl.create();
        weights.put(new Criterion("g1"), 10d);
        assertEquals(10d, weights.getSum(), 1e-6d);
        weights.put(new Criterion("g2"), 90d);
        assertEquals(100d, weights.getSum(), 1e-6d);
	weights.clear();
	assertEquals(0d, weights.getSum(), 1e-6d);
    }

    @Test
    public void testChange() {
        final WeightsImpl weights = WeightsImpl.create();
        weights.put(new Criterion("g1"), 10d);
        assertEquals(10d, weights.getSum(), 1e-6d);
        weights.put(new Criterion("g2"), 90d);
        assertEquals(100d, weights.getSum(), 1e-6d);
        weights.put(new Criterion("g1"), 110d);
        assertEquals(200d, weights.getSum(), 1e-6d);
    }

    @Test
    public void testNorm() {
        final Criterion g1 = new Criterion("g1");
        final Criterion g2 = new Criterion("g2");
    
        final WeightsImpl weights = WeightsImpl.create();
        final Weights normalized = weights.getNormalized();
    
        assertFalse(normalized.containsKey(g1));
    
        weights.put(g1, 10d);
        assertEquals(1d, normalized.get(g1), 1e-6d);
        assertEquals(1d, normalized.getSum(), 1e-6d);
    
        weights.put(g2, 90d);
        assertEquals(0.1d, normalized.get(g1), 1e-6d);
        assertEquals(0.9d, normalized.get(g2), 1e-6d);
        assertEquals(100d, weights.getSum(), 1e-6d);
    
        weights.remove(g1);
        assertFalse(normalized.containsKey(g1));
        assertEquals(90d, weights.getSum(), 1e-6d);
        assertEquals(1d, normalized.get(g2), 1e-6d);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNormPut() {
	final WeightsImpl weights = WeightsImpl.create();
	final Weights normalized = weights.getNormalized();

	normalized.put(new Criterion("g1"), 1d);
    }

}
