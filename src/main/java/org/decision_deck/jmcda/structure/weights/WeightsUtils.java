package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;


public class WeightsUtils {

    static public Weights newWeights() {
	return WeightsImpl.create();
    }

    static public Weights newWeights(Weights source) {
	if (source instanceof WeightsImpl) {
	    return WeightsImpl.newWeights((WeightsImpl) source);
	}
	final Weights weights = newWeights();
	weights.putAll(source);
	return weights;
    }

    /**
     * <p>
     * Returns a new weights object containing the same data than the source one, with possibly renamed criteria and
     * with a possibly different iteration order.
     * </p>
     * <p>
     * The returned object iteration order reflects the one from the given map keyset. The rename key set must equal the
     * source set of criteria, so that the order is completely defined.
     * </p>
     * <p>
     * To avoid renaming a given criterion, put a value equal to the key in the rename map.
     * </p>
     * 
     * @param source
     *            not {@code null}.
     * @param rename
     *            not {@code null}, no {@code null} key or values.
     * @return not {@code null}.
     */
    static public Weights newRenameAndReorder(Weights source, Function<? super Criterion, Criterion> rename,
	    Comparator<? super Criterion> newOrder) {
	checkNotNull(source);
	checkNotNull(rename);
	checkNotNull(newOrder);
	final Weights newWeights = WeightsUtils.newWeights();

	for (Criterion sourceCriterion : Ordering.from(newOrder).sortedCopy(source.keySet())) {
	    final Double weight = source.get(sourceCriterion);
	    final Criterion renamedCriterion = rename.apply(sourceCriterion);
	    checkArgument(renamedCriterion != null);
	    checkArgument(!newWeights.containsKey(renamedCriterion));
	    newWeights.put(renamedCriterion, weight);
	}
	return newWeights;
    }

    /**
     * <p>
     * Returns a new weights object containing the same data than the source one, with possibly renamed criteria and
     * with a possibly different iteration order.
     * </p>
     * <p>
     * The returned object iteration order reflects the one from the given map keyset. The rename key set must equal the
     * source set of criteria, so that the order is completely defined.
     * </p>
     * <p>
     * To avoid renaming a given criterion, put a value equal to the key in the rename map.
     * </p>
     * 
     * @deprecated The other one is more general.
     * @param source
     *            not {@code null}.
     * @param rename
     *            not {@code null}, no {@code null} key or values.
     * @return not {@code null}.
     */
    @Deprecated
    static public Weights newRenameAndReorder(Weights source, Map<Criterion, Criterion> rename) {
        checkNotNull(source);
        checkNotNull(rename);
        checkArgument(rename.keySet().equals(source.keySet()));
        final Weights newWeights = WeightsUtils.newWeights();
        for (Criterion sourceCriterion : rename.keySet()) {
            final Double weight = source.get(sourceCriterion);
            final Criterion renamedCriterion = rename.get(sourceCriterion);
            checkArgument(renamedCriterion != null);
	    checkArgument(!newWeights.containsKey(renamedCriterion));
            newWeights.put(renamedCriterion, weight);
        }
        return newWeights;
    }

}
