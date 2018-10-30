package org.decisiondeck.xmcda_oo.structure;

import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.internal.AbstractSharedDecisionMakerPreferences;
import org.decision_deck.jmcda.structure.internal.Modifier;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.thresholds.ThresholdsUtils;

public class SharedThresholds extends AbstractSharedDecisionMakerPreferences<Thresholds> {

    @Override
    protected boolean copyContents(Thresholds source, Thresholds target) {
	return copyContentsExtended(source, target);
    }

    @Override
    protected void empty(Thresholds value) {
	for (Criterion criterion : value.getCriteria()) {
	    value.getPreferenceThresholds().remove(criterion);
	    value.getIndifferenceThresholds().remove(criterion);
	    value.getVetoThresholds().remove(criterion);
	}
    }

    @Override
    protected Thresholds getNew() {
	return ThresholdsUtils.newThresholds();
    }

    @Override
    protected Thresholds copyFrom(Thresholds source) {
	final Thresholds thresholds = ThresholdsUtils.newThresholds();
	copyContents(source, thresholds);
	return thresholds;
    }

    @Override
    protected boolean isEmpty(Thresholds value) {
	return value.isEmpty();
    }

    public boolean remove(Criterion criterion) {
	final Criterion toRemove = criterion;
	return m_director.applyToAll(new Modifier<Thresholds>() {
	    @Override
	    public boolean modify(Thresholds thresholds) {
		if (!thresholds.getCriteria().contains(toRemove)) {
		    return false;
		}
		thresholds.getPreferenceThresholds().remove(toRemove);
		thresholds.getIndifferenceThresholds().remove(toRemove);
		thresholds.getVetoThresholds().remove(toRemove);
		return true;
	    }
	}, false);
    }

    public boolean merge(DecisionMaker dm, Thresholds thresholds) {
	final Thresholds thresholdsHere = get(dm);
	return merge(thresholds, thresholdsHere);
    }

    public boolean replaceShared(Thresholds newShared) {
	final Thresholds newSharedParam = newShared;
	final boolean changed = m_director.applyToAll(new Modifier<Thresholds>() {
	    @Override
	    public boolean modify(Thresholds target) {
		return copyContentsExtended(newSharedParam, target);
	    }
	}, true);
	return changed;
    }

    protected boolean copyContentsExtended(Thresholds source, Thresholds target) {
	final Set<Criterion> criteria = source.getCriteria();
	boolean changed = false;
	for (Criterion criterion : criteria) {
	    final boolean prefChanged;
	    if (source.containsPreferenceThreshold(criterion)) {
		final double p = source.getPreferenceThreshold(criterion);
		final Double oldP = target.setPreferenceThreshold(criterion, p);
		prefChanged = oldP == null || oldP.doubleValue() != p;
	    } else {
		final Double old = target.getPreferenceThresholds().remove(criterion);
		prefChanged = old != null;
	    }
	    final boolean indifChanged;
	    if (source.containsIndifferenceThreshold(criterion)) {
		final double i = source.getIndifferenceThreshold(criterion);
		final Double oldI = target.setIndifferenceThreshold(criterion, i);
		indifChanged = oldI == null || oldI.doubleValue() != i;
	    } else {
		final Double old = target.getIndifferenceThresholds().remove(criterion);
		indifChanged = old != null;
	    }
	    final boolean vetoChanged;
	    if (source.containsVetoThreshold(criterion)) {
		final double v = source.getVetoThreshold(criterion);
		final Double oldV = target.setVetoThreshold(criterion, v);
		vetoChanged = oldV == null || oldV.doubleValue() != v;
	    } else {
		final Double old = target.getVetoThresholds().remove(criterion);
		vetoChanged = old != null;
	    }
	    changed = changed || prefChanged || indifChanged || vetoChanged;
	}
	return changed;
    }

    /**
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @return <code>true</code> iff the call changed the state of the target object.
     */
    private boolean merge(Thresholds source, final Thresholds target) {
	if (source == null || target == null) {
	    throw new NullPointerException("" + source + target);
	}
	boolean changed = false;
	for (Criterion criterion : source.getCriteria()) {
	    if (source.containsPreferenceThreshold(criterion)) {
		final double p = source.getPreferenceThreshold(criterion);
		final Double old = target.setPreferenceThreshold(criterion, p);
		final boolean thisChanged = old == null || old.doubleValue() != p;
		changed = changed || thisChanged;
	    }
	    if (source.containsIndifferenceThreshold(criterion)) {
		final double i = source.getIndifferenceThreshold(criterion);
		final Double old = target.setIndifferenceThreshold(criterion, i);
		final boolean thisChanged = old == null || old.doubleValue() != i;
		changed = changed || thisChanged;
	    }
	    if (source.containsVetoThreshold(criterion)) {
		final double v = source.getVetoThreshold(criterion);
		final Double old = target.setVetoThreshold(criterion, v);
		final boolean thisChanged = old == null || old.doubleValue() != v;
		changed = changed || thisChanged;
	    }
	}
	return changed;
    }

}
