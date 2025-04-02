package org.decisiondeck.xmcda_oo.structure;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.internal.AbstractSharedDecisionMakerPreferences;
import org.decision_deck.jmcda.structure.internal.Modifier;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.CoalitionsUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SharedCoalitions extends AbstractSharedDecisionMakerPreferences<Coalitions> {

    @Override
    protected boolean copyContents(Coalitions source, Coalitions target) {
	return copyContentsExtended(source, target);
    }

    public boolean copyContentsExtended(Coalitions source, Coalitions target) {
	boolean changed = false;
	final ImmutableSet<Criterion> toRemoveAll = Sets.difference(target.getCriteria(), source.getCriteria())
		.immutableCopy();
	for (Criterion toRemove : toRemoveAll) {
	    target.removeWeight(toRemove);
	}
	changed = !toRemoveAll.isEmpty();

	for (Criterion criterion : source.getCriteria()) {
	    final double weight = source.getWeight(criterion);
	    final Double oldWeight = target.putWeight(criterion, weight);
	    changed = changed || !Double.valueOf(weight).equals(oldWeight);
	}
	if (source.containsMajorityThreshold()) {
	    final Double previous = target.setMajorityThreshold(source.getMajorityThreshold());
	    changed = changed || !Double.valueOf(source.getMajorityThreshold()).equals(previous);
	} else {
	    final Double previous = target.removeMajorityThreshold();
	    changed = changed || previous != null;
	}
	return changed;
    }

    @Override
    protected void empty(Coalitions value) {
	boolean modified = false;
	for (Criterion criterion : ImmutableSet.copyOf(value.getCriteria())) {
	    final Double previous = value.removeWeight(criterion);
	    final boolean changed = previous != null;
	    modified = modified || changed;
	}
	final Double previous = value.removeMajorityThreshold();
	final boolean changed = previous != null;
	modified = modified || changed;
	// return modified;
    }

    @Override
    protected Coalitions getNew() {
	return CoalitionsUtils.newCoalitions();
    }

    @Override
    protected Coalitions copyFrom(Coalitions source) {
	final Coalitions coalitions = CoalitionsUtils.newCoalitions();
	copyContentsExtended(source, coalitions);
	return coalitions;
    }

    @Override
    protected boolean isEmpty(Coalitions value) {
	return value.getWeights().isEmpty() && !value.containsMajorityThreshold();
    }

    public boolean remove(Criterion criterion) {
	final Criterion toRemove = criterion;
	return m_director.applyToAll(new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions coalitions) {
		if (!coalitions.getWeights().containsKey(toRemove)) {
		    return false;
		}
		coalitions.removeWeight(toRemove);
		return true;
	    }
	}, false);
    }

    public boolean replaceShared(Coalitions coalitions) {
	final Coalitions coalitionsParam = coalitions;
	final boolean changed = m_director.applyToAll(new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions target) {
		return copyContentsExtended(coalitionsParam, target);
	    }
	}, true);
	return changed;
    }

    public boolean merge(DecisionMaker dm, Coalitions coalitions) {
	Coalitions coalitionsHere = m_director.get(dm);
	Preconditions.checkArgument(coalitionsHere != null, "Has no coalitions: " + dm + ".");
	return merge(coalitions, coalitionsHere);
    }

    public boolean removeMajorityThreshold(DecisionMaker dm) {
	final boolean changed = m_director.apply(dm, new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions target) {
		final Double previous = target.removeMajorityThreshold();
		return previous != null;
	    }
	}, false);
	return changed;
    }

    public boolean setMajorityThreshold(DecisionMaker dm, double threshold) {
	final double thresholdParam = threshold;
	final boolean changed = m_director.apply(dm, new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions target) {
		final Double previous = target.setMajorityThreshold(thresholdParam);
		return !Double.valueOf(thresholdParam).equals(previous);
	    }
	}, true);
	return changed;
    }

    public boolean putWeight(DecisionMaker dm, Criterion criterion, double weight) {
	final Criterion criterionParam = criterion;
	final double weightParam = weight;
	final boolean changed = m_director.apply(dm, new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions target) {
		final Double previous = target.putWeight(criterionParam, weightParam);
		return !Double.valueOf(weightParam).equals(previous);
	    }
	}, true);
	return changed;
    }

    public boolean removeWeight(DecisionMaker dm, Criterion criterion) {
	final Criterion criterionParam = criterion;
	final boolean changed = m_director.apply(dm, new Modifier<Coalitions>() {
	    @Override
	    public boolean modify(Coalitions target) {
		final Double previous = target.removeWeight(criterionParam);
		return previous != null;
	    }
	}, false);
	return changed;
    }

    /**
     * @param source
     *            not {@code null}.
     * @param target
     *            not {@code null}.
     * @return {@code true} iff the call changed the state of the target object.
     */
    static private boolean merge(Coalitions source, Coalitions target) {
	checkNotNull(source);
	checkNotNull(target);
	final boolean majorityChanged;
	if (source.containsMajorityThreshold()) {
	    final Double previousMajority = target.setMajorityThreshold(source.getMajorityThreshold());
	    majorityChanged = !Double.valueOf(source.getMajorityThreshold()).equals(previousMajority);
	} else {
	    majorityChanged = false;
	}

	boolean weightChanged = false;
	for (Criterion criterion : source.getCriteria()) {
	    final double newWeight = source.getWeight(criterion);
	    weightChanged = weightChanged || !target.getWeights().containsKey(criterion)
		    || target.getWeight(criterion) == newWeight;
	    target.putWeight(criterion, newWeight);
	}
	return majorityChanged || weightChanged;
    }
}
