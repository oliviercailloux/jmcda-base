package org.decision_deck.jmcda.structure.thresholds;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * A skeleton abstract implementation of Thresholds. All methods delegate to the map based abstract methods.
 * 
 * @author Olivier Cailloux
 * 
 */
public abstract class ThresholdsMapBased implements Thresholds {

    protected ThresholdsMapBased() {
	/** Public constructor. */
    }





    @Override
    public Set<Criterion> getCriteria() {
	return Sets.union(getPreferenceThresholds().keySet(),
		Sets.union(getIndifferenceThresholds().keySet(), getVetoThresholds().keySet()));
    }

    @Override
    public boolean isEmpty() {
	return getPreferenceThresholds().isEmpty() && getIndifferenceThresholds().isEmpty()
		&& getVetoThresholds().isEmpty();
    }

    @Override
    public abstract Map<Criterion, Double> getIndifferenceThresholds();

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Thresholds)) {
	    return false;
	}
	Thresholds t2 = (Thresholds) obj;
	return ThresholdsUtils.getEquivalence().equivalent(this, t2);
    }

    @Override
    public int hashCode() {
	return ThresholdsUtils.getEquivalence().hash(this);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.add("Preferences", getPreferenceThresholds());
	helper.add("Indifferences", getIndifferenceThresholds());
	helper.add("Vetoes", getVetoThresholds());
	return helper.toString();
    }

    @Override
    public double getVetoThreshold(Criterion criterion) {
	checkArgument(containsVetoThreshold(criterion));
	return getVetoThresholds().get(criterion).doubleValue();
    }

    @Override
    public boolean containsVetoThreshold(Criterion criterion) {
	return getVetoThresholds().containsKey(criterion);
    }

    @Override
    public boolean containsPreferenceThreshold(Criterion criterion) {
	return getPreferenceThresholds().containsKey(criterion);
    }

    @Override
    public boolean containsIndifferenceThreshold(Criterion criterion) {
	return getIndifferenceThresholds().containsKey(criterion);
    }

    @Override
    public double getIndifferenceThreshold(Criterion criterion) {
	checkArgument(containsIndifferenceThreshold(criterion));
	return getIndifferenceThresholds().get(criterion).doubleValue();
    }

    @Override
    public double getPreferenceThreshold(Criterion criterion) {
	checkArgument(containsPreferenceThreshold(criterion));
	return getPreferenceThresholds().get(criterion).doubleValue();
    }

    @Override
    public Double setPreferenceThreshold(Criterion criterion, double threshold) {
	checkNotNull(criterion);
	checkArgument(!Double.isNaN(threshold));
	return getPreferenceThresholds().put(criterion, Double.valueOf(threshold));
    }

    @Override
    public Double setIndifferenceThreshold(Criterion criterion, double threshold) {
	checkNotNull(criterion);
	checkArgument(!Double.isNaN(threshold));
	return getIndifferenceThresholds().put(criterion, Double.valueOf(threshold));
    }

    @Override
    public Double setVetoThreshold(Criterion criterion, double threshold) {
	checkNotNull(criterion);
	checkArgument(!Double.isNaN(threshold));
	return getVetoThresholds().put(criterion, Double.valueOf(threshold));
    }

}
