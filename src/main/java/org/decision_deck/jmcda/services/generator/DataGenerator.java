package org.decision_deck.jmcda.services.generator;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.interval.PreferenceDirection;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.structure.sorting.problem.ProblemFactory;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsWithCredibilitiesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResultsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * An object to generate basic problem data.
 * 
 * @author Olivier Cailloux
 * 
 */
public class DataGenerator {

    public DataGenerator() {
	m_alternativeNamer = null;
	m_prefix = null;
    }

    static public class DefaultNamer implements Function<Integer, String> {
	private final NumberFormat m_fmt;
	private final String m_prefix;

	public DefaultNamer(String prefix, int nbObjects) {
	    m_prefix = prefix;
	    m_fmt = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	    m_fmt.setMinimumIntegerDigits((int) Math.floor(Math.log10(nbObjects)) + 1);
	}

	@Override
	public String apply(Integer input) {
	    final String nb = m_fmt.format(input.intValue());
	    return m_prefix + nb;
	}
    }

    private Function<Integer, String> m_alternativeNamer;
    private final IGroupSortingResultsWithCredibilities m_data = new GroupSortingResultsWithCredibilitiesImpl();
    private String m_prefix;

    public ISortingPreferences getAsSortingPreferences() {
	final ISortingPreferences preferences = ProblemFactory.newSortingPreferences(m_data.getAlternativesEvaluations(), m_data.getScales(),
		m_data.getCatsAndProfs(), m_data.getSharedProfilesEvaluations(), m_data.getSharedThresholds(),
		m_data.getSharedCoalitions());
	preferences.getAlternatives().addAll(m_data.getAlternatives());
	preferences.getCriteria().addAll(m_data.getCriteria());
	return preferences;
    }

    /**
     * <p>
     * Reuses scales and profiles stored in this object and generates profiles evaluations that split the categories in
     * sizes that are as equal as possible. Discrete intervals as scales are accepted. In that case this object will
     * divide each category so that an equal number of steps is available in each category, as far as possible. When an
     * equal number of steps is not possible to reach, some categories will receive one step more. These are the worst
     * categories. This is motivated by the fact that it possibly provides more balanced categories when using the
     * resulting evaluations together with a pessimistic sorting with ELECTRE TRI. For example, if a criterion has a
     * scale [0, 1] with a step size of 1, it has only two available steps. If two categories are asked, thus one
     * profile, one category will contain one step, an other one, zero. This object will then return a profile
     * evaluation for that criterion of 1, giving advantage to the worst category. This could be improved by alternating
     * big size categories with small size categories to avoid having all the best categories small than the worst ones
     * (which can yield much smaller possibilities of being average to good compared to being bad to average).
     * </p>
     * <p>
     * The returned profiles evaluations may be such that several (or all) profiles are equal: this happens if the
     * number of available steps on each criteria is insufficient compared to the number of requested profiles.
     * Similarily, some profiles could have, on some criterion, an evaluation that is exactly the minimum or maximum
     * bound of the corresponding scale.
     * </p>
     * <p>
     * The returned evaluations are such that the strict dominance relation (which is possibly incomplete because of the
     * possible equal profiles) is a subset of the order of the profiles as stored in this object. When no profiles are
     * ex-æquo, the dominance order of the resulting evaluations is exactly the stored profiles order.
     * </p>
     * <p>
     * The profiles order as stored in this object is used to determine the evaluations order.
     * </p>
     * <p>
     * The stored scales must contain for each value an interval with a non infinite minimum and maximum bound, and must
     * contain the preference direction. The interval may be discrete or continuous.
     * </p>
     * 
     * @return not <code>null</code>.
     * @throws InvalidInputException
     *             if an infinite interval is found, or a preference direction is missing.
     */
    public Evaluations genSplitProfilesEvaluations() throws InvalidInputException {
	int nbCategories = m_data.getProfiles().size() + 1;

	final Evaluations profilesEvaluations = EvaluationsUtils.newEvaluationMatrix();

	for (Criterion criterion : m_data.getScales().keySet()) {
	    final Interval scale = m_data.getScales().get(criterion);
	    if (Double.isInfinite(scale.getMaximum())) {
		throw new InvalidInputException("Infinite interval for " + criterion + ".");
	    }
	    if (Double.isInfinite(scale.getMinimum())) {
		throw new InvalidInputException("Infinite interval for " + criterion + ".");
	    }
	    if (scale.getPreferenceDirection() == null) {
		throw new InvalidInputException("Unknown preference direction for " + criterion + ".");
	    }
	    if (scale.getPreferenceDirection() == PreferenceDirection.MINIMIZE) {
		throw new UnsupportedOperationException();
	    }
	    if (scale.getStepSize() != null) {
		/**
		 * The discrete case (more subtle). We view this as a fair sharing problem: distribute fair shares (as
		 * equal as possible) to categories. We count the number of shares to distribute to the categories. That
		 * number may not divide exactly. Thus we distribute small shares (counting one step less) and big
		 * shares.
		 */
		/** NB may be zero. */
		int nbIntervals = scale.getAsDiscreteInterval().getNbSteps() - 1;
		final double averageShareNbSteps = (double) nbIntervals / nbCategories;
		final int bigSharesNbSteps = (int) Math.ceil(averageShareNbSteps);
		final int smallSharesNbSteps = (int) Math.floor(averageShareNbSteps);
		// /**
		// * We count the missing shares supposing every category receive a big share, that gives the number of
		// * categories which receive a small share instead of a big one.
		// */
		// final int nbSmallShares = nbCategories * bigSharesNbSteps - nbShares;
		/**
		 * We count the remaining shares supposing every category receive a small share, that gives the number
		 * of categories which receive a big share instead of a small one.
		 */
		final int nbBigShares = nbIntervals - nbCategories * smallSharesNbSteps;

		double current = scale.getMinimum();
		final Iterator<Alternative> iterator = m_data.getProfiles().iterator();
		for (int i = 0; i < nbBigShares; ++i) {
		    final Alternative profile = iterator.next();
		    current += scale.getStepSize().doubleValue() * bigSharesNbSteps;
		    profilesEvaluations.put(profile, criterion, current);
		}
		for (int i = nbBigShares; i < nbCategories - 1; ++i) {
		    final Alternative profile = iterator.next();
		    current += scale.getStepSize().doubleValue() * smallSharesNbSteps;
		    profilesEvaluations.put(profile, criterion, current);
		}
		assert (!iterator.hasNext());
	    } else {
		final double lengthTot = scale.getMaximum() - scale.getMinimum();
		final double lengthOneInterval = lengthTot / nbCategories;
		double current = scale.getMinimum();
		for (Alternative profile : m_data.getProfiles()) {
		    current += lengthOneInterval;
		    final double thisProfile;
		    thisProfile = current;
		    profilesEvaluations.put(profile, criterion, thisProfile);
		}
	    }
	}

	m_data.setSharedProfilesEvaluations(profilesEvaluations);
	return profilesEvaluations;
    }

    public Set<Alternative> genAlternatives(int nbAlternatives) {
	final Set<Alternative> alts = Sets.newLinkedHashSet();
	final String prefix = m_prefix == null ? "Alt" : m_prefix;
	final Function<Integer, String> namer = m_alternativeNamer == null ? new DefaultNamer(prefix, nbAlternatives)
		: m_alternativeNamer;
	for (int i = 1; i <= nbAlternatives; ++i) {
	    alts.add(new Alternative(namer.apply(Integer.valueOf(i))));
	}
	m_data.getAlternatives().clear();
	m_data.getAlternatives().addAll(alts);
	return alts;
    }

    public void setAlternativeNamer(Function<Integer, String> alternativeNamer) {
	m_alternativeNamer = alternativeNamer;
    }

    public void setAlternatives(Set<Alternative> alternatives) {
	m_data.getAlternatives().clear();
	m_data.getAlternatives().addAll(alternatives);
    }

    public void setCatsAndProfs(CatsAndProfs categories) {
	m_data.getCatsAndProfs().clear();
	m_data.getCatsAndProfs().addAll(categories);
    }

    public void setCriteria(Set<Criterion> criteria) {
	m_data.getCriteria().clear();
	m_data.getCriteria().addAll(criteria);
    }



    public void setDms(Set<DecisionMaker> dms) {
	m_data.setKeepSharedProfilesEvaluations(false);
	m_data.getDms().clear();
	m_data.getDms().addAll(dms);
	m_data.setKeepSharedProfilesEvaluations(true);
    }

    public void setScales(Map<Criterion, Interval> scales) {
	for (Criterion criterion : scales.keySet()) {
	    final Interval scale = scales.get(criterion);
	    m_data.setScale(criterion, scale);
	}
    }

    /**
     * Generate the given number minus one profiles and the given number of categories, possibly overriding profiles or
     * categories stored in this object.
     * 
     * @param nbCategories
     *            the number of categories to generate.
     * @return not <code>null</code>.
     */
    public CatsAndProfs genCatsAndProfs(int nbCategories) {
	checkArgument(nbCategories >= 1);

	genProfiles(nbCategories - 1);

	return genCatsAndProfsUsingProfiles();
    }

    /**
     * Reuses the profiles stored in this object. Generates a number of categories equal to the number of profiles + 1.
     * If no profiles are stored in this object, the returned object contains one category and no profiles.
     * 
     * @return not <code>null</code>.
     */
    public CatsAndProfs genCatsAndProfsUsingProfiles() {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	for (Alternative profile : m_data.getProfiles()) {
	    cats.addProfile(profile);
	}

	final int nbCategories = m_data.getProfiles().size() + 1;

	final String prefix = m_prefix == null ? "Cat" : m_prefix;
	final DefaultNamer namer = new DefaultNamer(prefix, nbCategories);
	for (int i = 1; i <= nbCategories; ++i) {
	    cats.addCategory(namer.apply(Integer.valueOf(i)));
	}
	m_data.getCatsAndProfs().clear();
	m_data.getCatsAndProfs().addAll(cats);
	return cats;
    }

    /**
     * Note that this replaces possibly existing profiles in this object.
     * 
     * @param nbProfiles
     *            to generate.
     * @return the generated profiles.
     */
    public Set<Alternative> genProfiles(int nbProfiles) {
	final Set<Alternative> profs = Sets.newLinkedHashSet();
	final String prefix = m_prefix == null ? "p" : m_prefix;
	final DefaultNamer namer = new DefaultNamer(prefix, nbProfiles);
	for (int i = 1; i <= nbProfiles; ++i) {
	    final String name = namer.apply(Integer.valueOf(i));
	    profs.add(new Alternative(name));
	}
	m_data.getProfiles().clear();
	m_data.getProfiles().addAll(profs);
	return profs;
    }

    public String getPrefix() {
	return m_prefix;
    }

    public void setPrefix(String prefix) {
	m_prefix = prefix;
    }

    public Set<Criterion> genCriteria(int nbCriteria) {
	final Set<Criterion> crits = Sets.newLinkedHashSet();
	final String prefix = m_prefix == null ? "g" : m_prefix;
	final DefaultNamer namer = new DefaultNamer(prefix, nbCriteria);
	for (int i = 1; i <= nbCriteria; ++i) {
	    crits.add(new Criterion(namer.apply(Integer.valueOf(i))));
	}
	m_data.getCriteria().clear();
	m_data.getCriteria().addAll(crits);
	return crits;
    }

    public Set<DecisionMaker> genDms(int nbDms) {
	final Set<DecisionMaker> dms = Sets.newLinkedHashSet();
	final String prefix = m_prefix == null ? "dm" : m_prefix;
	final DefaultNamer namer = new DefaultNamer(prefix, nbDms);
	for (int i = 1; i <= nbDms; ++i) {
	    dms.add(new DecisionMaker(namer.apply(Integer.valueOf(i))));
	}

	m_data.getDms().clear();
	m_data.getDms().addAll(dms);
	return dms;
    }

    /**
     * Associates the given scale to each criterion stored in this object.
     * 
     * @param scale
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    public Map<Criterion, Interval> setScales(Interval scale) {
	final Map<Criterion, Interval> scales = Maps.newLinkedHashMap();
	for (Criterion criterion : m_data.getCriteria()) {
	    scales.put(criterion, scale);
	    m_data.setScale(criterion, scale);
	}
	return scales;
    }

    /**
     * <p>
     * A convenience method that stores the given profiles and categories and then generate the split profiles
     * evaluations as if #gen had been called.
     * </p>
     * 
     * @param profiles
     *            not <code>null</code>.
     * @param scales
     *            not <code>null</code>.
     * 
     * @return not <code>null</code>.
     * @throws InvalidInputException
     *             if an infinite interval is found, or a preference direction is missing.
     */
    public Evaluations genSplitProfilesEvaluations(Set<Alternative> profiles, Map<Criterion, Interval> scales)
	    throws InvalidInputException {
	setProfiles(profiles);
	setScales(scales);
	return genSplitProfilesEvaluations();
    }

    public void setProfiles(Set<Alternative> profiles) {
	m_data.getProfiles().clear();
	m_data.getProfiles().addAll(profiles);
    }

    protected IGroupSortingResultsWithCredibilities getWriteableData() {
	return m_data;
    }
}
