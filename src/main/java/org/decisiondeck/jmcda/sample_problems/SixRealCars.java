package org.decisiondeck.jmcda.sample_problems;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.interval.Intervals;
import org.decision_deck.jmcda.structure.interval.PreferenceDirection;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.matrix.MatrixesMC;
import org.decision_deck.jmcda.structure.matrix.SparseAlternativesMatrixFuzzy;
import org.decision_deck.jmcda.structure.scores.AlternativesScores;
import org.decision_deck.jmcda.structure.sorting.SortingMode;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.thresholds.ThresholdsUtils;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.CoalitionsUtils;
import org.decision_deck.jmcda.structure.weights.Weights;
import org.decision_deck.utils.matrix.SparseMatrixFuzzy;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.ProblemFactory;
import org.decisiondeck.jmcda.structure.sorting.problem.data.IProblemData;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsToMultiple;
import org.decisiondeck.xmcda_oo.structure.sorting.SortingProblemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * <p>
 * Sample data used only for testing the components, but it has been placed here
 * instead of in the test tree because it is used by other packages as well (and
 * test trees are not supposed to be exposed to other packages).
 * </p>
 * <p>
 * Immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 */
public class SixRealCars {
	private static SixRealCars s_instance;

	private static final Logger s_logger = LoggerFactory.getLogger(SixRealCars.class);

	static public SixRealCars getInstance() {
		if (s_instance == null) {
			s_instance = new SixRealCars();
		}
		return s_instance;
	}

	private final Map<Alternative, String> m_alternativeNames = Maps.newHashMap();
	private boolean m_altsOk;
	private final Map<Criterion, String> m_criteriaNames = Maps.newHashMap();
	private boolean m_critsOk;
	private final ISortingPreferences m_data = ProblemFactory.newSortingPreferences();
	private final ISortingResults m_data55 = SortingProblemUtils.newResults();
	private final ISortingResultsToMultiple m_data75 = ProblemFactory.newSortingResultsToMultiple();
	private final ISortingResults m_data75Optimistic = SortingProblemUtils.newResults();
	private final ISortingResults m_data75Pessimistic = SortingProblemUtils.newResults();

	private SixRealCars() {
		m_critsOk = false;
		/** Private constructor. */
		m_altsOk = false;
	}

	public Set<Alternative> getAllAlternatives() {
		getAlternatives();
		getProfiles();
		return m_data.getAllAlternatives();
	}

	/**
	 * @return the real alternatives names.
	 */
	public Map<Alternative, String> getAlternativeNames() {
		getAlternatives();
		return m_alternativeNames;
	}

	public Set<Alternative> getAlternatives() {
		s_logger.info("Begin alts: {}.", m_data.getAlternatives());
		if (!m_altsOk) {
			final Alternative a1 = new Alternative("a01");
			final Alternative a2 = new Alternative("a02");
			final Alternative a3 = new Alternative("a03");
			final Alternative a4 = new Alternative("a04");
			final Alternative a5 = new Alternative("a05");
			final Alternative a6 = new Alternative("a06");
			m_data.getAlternatives().add(a1);
			m_data.getAlternatives().add(a2);
			m_data.getAlternatives().add(a3);
			m_data.getAlternatives().add(a4);
			m_data.getAlternatives().add(a5);
			m_data.getAlternatives().add(a6);
			m_alternativeNames.put(a1, "Audi A3 TDI e");
			m_alternativeNames.put(a2, "Audi A4 berline");
			m_alternativeNames.put(a3, "BMW 118d Hatch");
			m_alternativeNames.put(a4, "BMW 320d Berline");
			m_alternativeNames.put(a5, "Volvo C30");
			m_alternativeNames.put(a6, "Volvo S40 D5");
			s_logger.info("Populated alts: {}.", m_data.getAlternatives());
			m_altsOk = true;
		}
		return Collections.unmodifiableSet(m_data.getAlternatives());
	}

	public EvaluationsRead getAlternativesEvaluations() {
		if (m_data.getAlternativesEvaluations().isEmpty()) {
			/** First for order correct. */
			getAlternatives();
			getCriteria();
			final Evaluations evals = EvaluationsUtils.newEvaluationMatrix();
			final Alternative a1 = new Alternative("a01");
			final Alternative a2 = new Alternative("a02");
			final Alternative a3 = new Alternative("a03");
			final Alternative a4 = new Alternative("a04");
			final Alternative a5 = new Alternative("a05");
			final Alternative a6 = new Alternative("a06");
			final Criterion c1 = new Criterion("c01");
			final Criterion c2 = new Criterion("c02");
			final Criterion c3 = new Criterion("c03");
			final Criterion c4 = new Criterion("c04");
			final Criterion c5 = new Criterion("c05");
			evals.put(a1, c1, 22080f);
			evals.put(a1, c2, 105f);
			evals.put(a1, c3, 11.40f);
			evals.put(a1, c4, 5.8f);
			evals.put(a1, c5, 119f);
			evals.put(a2, c1, 28100f);
			evals.put(a2, c2, 160f);
			evals.put(a2, c3, 8.6f);
			evals.put(a2, c4, 9.6f);
			evals.put(a2, c5, 164f);
			evals.put(a3, c1, 24650f);
			evals.put(a3, c2, 143f);
			evals.put(a3, c3, 9.0f);
			evals.put(a3, c4, 4.5f);
			evals.put(a3, c5, 119f);
			evals.put(a4, c1, 32700f);
			evals.put(a4, c2, 177f);
			evals.put(a4, c3, 7.9f);
			evals.put(a4, c4, 6.7f);
			evals.put(a4, c5, 128f);
			evals.put(a5, c1, 22750f);
			evals.put(a5, c2, 136f);
			evals.put(a5, c3, 9.4f);
			evals.put(a5, c4, 7.6f);
			evals.put(a5, c5, 151f);
			evals.put(a6, c1, 27350f);
			evals.put(a6, c2, 180f);
			evals.put(a6, c3, 7.9f);
			evals.put(a6, c4, 8.4f);
			evals.put(a6, c5, 164f);
			m_data.setEvaluations(evals);
		}
		return m_data.getAlternativesEvaluations();
	}

	/**
	 * @return the real alternatives with nice names instead of ugly ids.
	 */
	public Set<Alternative> getAlternativesWithNiceNames() {
		final Collection<Alternative> alts = Maps
				.transformValues(getAlternativeNames(), new Function<String, Alternative>() {
					@Override
					public Alternative apply(String input) {
						return new Alternative(input);
					}
				}).values();
		return Sets.newLinkedHashSet(alts);
	}

	public IProblemData getAsProblemData() {
		return getAsSortingPreferences70();
	}

	public IOrderedAssignmentsRead getAssignments55() {
		/**
		 * These results hold for sorting mode BOTH (hence also hold for
		 * optimistic and pessimistic).
		 */
		if (m_data55.getAssignments().getAlternatives().isEmpty()) {
			final CatsAndProfs target = m_data55.getCatsAndProfs();
			target.clear();
			target.addAll(getCatsAndProfs());
			final IOrderedAssignments assignments = m_data55.getAssignments();
			assignments.setCategories(getCatsAndProfs().getCategories());
			assignments.setCategory(new Alternative("a01"), new Category("Good"));
			assignments.setCategory(new Alternative("a02"), new Category("Medium"));
			assignments.setCategory(new Alternative("a03"), new Category("Good"));
			assignments.setCategory(new Alternative("a04"), new Category("Medium"));
			assignments.setCategory(new Alternative("a05"), new Category("Good"));
			assignments.setCategory(new Alternative("a06"), new Category("Medium"));
		}
		return AssignmentsUtils.getReadView(m_data55.getAssignments());
	}

	public IOrderedAssignmentsToMultipleRead getAssignments75(SortingMode mode) {
		switch (mode) {
		case PESSIMISTIC:
			return getAssignments75Pessimistic();
		case OPTIMISTIC:
			return getAssignments75Optimistic();
		case BOTH:
			return getAssignments75Both();
		default:
			throw new IllegalStateException("Invalid sorting mode.");
		}
	}

	public IOrderedAssignmentsToMultipleRead getAssignments75Both() {
		if (m_data75.getAssignments().getAlternatives().isEmpty()) {
			final CatsAndProfs target = m_data75.getCatsAndProfs();
			target.clear();
			target.addAll(getCatsAndProfs());
			final IOrderedAssignmentsToMultiple assignments = m_data75.getAssignments();
			assignments.setCategories(getCatsAndProfs().getCategories());
			final Set<Category> mG = Sets.newHashSet();
			mG.add(new Category("Medium"));
			mG.add(new Category("Good"));
			final Set<Category> bM = Sets.newHashSet();
			bM.add(new Category("Bad"));
			bM.add(new Category("Medium"));
			assignments.setCategories(new Alternative("a01"), mG);
			assignments.setCategories(new Alternative("a02"), Collections.singleton(new Category("Medium")));
			assignments.setCategories(new Alternative("a03"), Collections.singleton(new Category("Medium")));
			assignments.setCategories(new Alternative("a04"), bM);
			assignments.setCategories(new Alternative("a05"), Collections.singleton(new Category("Medium")));
			assignments.setCategories(new Alternative("a06"), Collections.singleton(new Category("Medium")));
		}
		return AssignmentsUtils.getReadView(m_data75.getAssignments());
	}

	public IOrderedAssignmentsRead getAssignments75Optimistic() {
		if (m_data75Optimistic.getAssignments().getAlternatives().isEmpty()) {
			final CatsAndProfs target = m_data75Optimistic.getCatsAndProfs();
			target.clear();
			target.addAll(getCatsAndProfs());
			final IOrderedAssignments assignments = m_data75Optimistic.getAssignments();
			assignments.setCategories(getCatsAndProfs().getCategories());
			assignments.setCategory(new Alternative("a01"), new Category("Good"));
			assignments.setCategory(new Alternative("a02"), new Category("Medium"));
			assignments.setCategory(new Alternative("a03"), new Category("Medium"));
			assignments.setCategory(new Alternative("a04"), new Category("Medium"));
			assignments.setCategory(new Alternative("a05"), new Category("Medium"));
			assignments.setCategory(new Alternative("a06"), new Category("Medium"));
		}
		return AssignmentsUtils.getReadView(m_data75Optimistic.getAssignments());
	}

	public IOrderedAssignmentsRead getAssignments75Pessimistic() {
		if (m_data75Pessimistic.getAssignments().getAlternatives().isEmpty()) {
			final CatsAndProfs target = m_data75Pessimistic.getCatsAndProfs();
			target.clear();
			target.addAll(getCatsAndProfs());
			final IOrderedAssignments assignments = m_data75Pessimistic.getAssignments();
			assignments.setCategories(getCatsAndProfs().getCategories());
			assignments.setCategory(new Alternative("a01"), new Category("Medium"));
			assignments.setCategory(new Alternative("a02"), new Category("Medium"));
			assignments.setCategory(new Alternative("a03"), new Category("Medium"));
			assignments.setCategory(new Alternative("a04"), new Category("Bad"));
			assignments.setCategory(new Alternative("a05"), new Category("Medium"));
			assignments.setCategory(new Alternative("a06"), new Category("Medium"));
		}
		return AssignmentsUtils.getReadView(m_data75Pessimistic.getAssignments());
	}

	public ISortingPreferences getAsSortingPreferences70() {
		getAllAlternatives();
		getCriteria();
		getAlternativesEvaluations();
		getProfilesEvaluations();
		getCatsAndProfs();
		getCoalitions70();
		getScales();
		getThresholds();
		return SortingProblemUtils.getReadView(m_data);
	}

	public ISortingPreferences getAsSortingPreferences75() {
		if (m_data75.getCoalitions().isEmpty()) {
			SortingProblemUtils.copyPreferencesToTarget(getAsSortingPreferences70(), m_data75);
			getAssignments75Both();
			m_data75.setCoalitions(getCoalitions75());
		}
		return SortingProblemUtils.getReadView((ISortingPreferences) m_data75);
	}

	public ISortingResults getAsSortingResults55() {
		if (m_data55.getCoalitions().isEmpty()) {
			SortingProblemUtils.copyPreferencesToTarget(getAsSortingPreferences70(), m_data55);
			getAssignments55();
			m_data55.setCoalitions(getCoalitions55());
		}
		return SortingProblemUtils.getReadView(m_data55);
	}

	public ISortingResultsToMultiple getAsSortingResults75Both() {
		if (m_data75.getCoalitions().isEmpty()) {
			SortingProblemUtils.copyPreferencesToTarget(getAsSortingPreferences70(), m_data75);
			getAssignments75Both();
			m_data75.setCoalitions(getCoalitions75());
		}
		return SortingProblemUtils.getReadViewToMultiple(m_data75);
	}

	public ISortingResults getAsSortingResults75Optimistic() {
		if (m_data75Optimistic.getCoalitions().isEmpty()) {
			SortingProblemUtils.copyPreferencesToTarget(getAsSortingPreferences70(), m_data75Optimistic);
			getAssignments75Optimistic();
			m_data75Optimistic.setCoalitions(getCoalitions75());
		}
		return SortingProblemUtils.getReadView(m_data75Optimistic);
	}

	public ISortingResults getAsSortingResults75Pessimistic() {
		if (m_data75Pessimistic.getCoalitions().isEmpty()) {
			SortingProblemUtils.copyPreferencesToTarget(getAsSortingPreferences70(), m_data75Pessimistic);
			getAssignments75Pessimistic();
			m_data75Pessimistic.setCoalitions(getCoalitions75());
		}
		return SortingProblemUtils.getReadView(m_data75Pessimistic);
	}

	public CatsAndProfs getCatsAndProfs() {
		if (m_data.getCatsAndProfs().isEmpty()) {
			final CatsAndProfs categories = m_data.getCatsAndProfs();
			categories.addCategory("Bad");
			categories.addCategory("Medium");
			categories.addCategory("Good");
			final Alternative pBM = new Alternative("pBM");
			final Alternative pMG = new Alternative("pMG");
			categories.addProfile(pBM);
			categories.addProfile(pMG);
		}
		return Categories.getReadView(m_data.getCatsAndProfs());
	}

	public Coalitions getCoalitions55() {
		final Coalitions coalitions70 = getCoalitions70();
		final Coalitions coalitions = CoalitionsUtils.newCoalitions(coalitions70);
		coalitions.setMajorityThreshold(0.55d);
		return coalitions;
	}

	public Coalitions getCoalitions70() {
		if (m_data.getCoalitions().isEmpty()) {
			final Coalitions coalitions = CoalitionsUtils.newCoalitions();
			final Weights weights = coalitions.getWeights();
			final Criterion c1 = new Criterion("c01");
			final Criterion c2 = new Criterion("c02");
			final Criterion c3 = new Criterion("c03");
			final Criterion c4 = new Criterion("c04");
			final Criterion c5 = new Criterion("c05");
			weights.putWeight(c1, 0.4d);
			weights.putWeight(c2, 0.18d);
			weights.putWeight(c3, 0.12d);
			weights.putWeight(c4, 0.21d);
			weights.putWeight(c5, 0.09d);
			coalitions.setMajorityThreshold(0.70d);
			m_data.setCoalitions(coalitions);
		}
		return m_data.getCoalitions();
	}

	public Coalitions getCoalitions75() {
		final Coalitions coalitions70 = getCoalitions70();
		final Coalitions coalitions = CoalitionsUtils.newCoalitions(coalitions70);
		coalitions.setMajorityThreshold(0.75d);
		return coalitions;
	}

	public SparseAlternativesMatrixFuzzy getConcordance() {
		/** Results exact to the fourth decimal (rounded). */
		final SparseAlternativesMatrixFuzzy res = MatrixesMC.newAlternativesFuzzy();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		res.put(a1, a1, 1.0000d);
		res.put(a1, a2, 0.7000d);
		res.put(a1, a3, 0.4900d);
		res.put(a1, a4, 0.7000d);
		res.put(a1, a5, 0.7000d);
		res.put(a1, a6, 0.7000d);
		res.put(a2, a1, 0.3495d);
		res.put(a2, a2, 1.0000d);
		res.put(a2, a3, 0.3495d);
		res.put(a2, a4, 0.6136d);
		res.put(a2, a5, 0.3783d);
		res.put(a2, a6, 0.5880d);
		res.put(a3, a1, 0.6688d);
		res.put(a3, a2, 0.8740d);
		res.put(a3, a3, 1.0000d);
		res.put(a3, a4, 0.7540d);
		res.put(a3, a5, 0.7760d);
		res.put(a3, a6, 0.7540d);
		res.put(a4, a1, 0.4029d);
		res.put(a4, a2, 0.6000d);
		res.put(a4, a3, 0.3819d);
		res.put(a4, a4, 1.0000d);
		res.put(a4, a5, 0.6000d);
		res.put(a4, a6, 0.5820d);
		res.put(a5, a1, 0.7340d);
		res.put(a5, a2, 0.8080d);
		res.put(a5, a3, 0.6952d);
		res.put(a5, a4, 0.5203d);
		res.put(a5, a5, 1.0000d);
		res.put(a5, a6, 0.7300d);
		res.put(a6, a1, 0.3495d);
		res.put(a6, a2, 1.0000d);
		res.put(a6, a3, 0.3975d);
		res.put(a6, a4, 0.7576d);
		res.put(a6, a5, 0.4203d);
		res.put(a6, a6, 1.0000d);
		return res;
	}

	/**
	 * Retrieves the concordance, restricted to the first three alternatives,
	 * and taking only into account the first two criteria.
	 *
	 * @return not <code>null</code>.
	 */
	public SparseAlternativesMatrixFuzzy getConcordanceRestricted() {
		/** Results exact to the fourth decimal (rounded). */
		final SparseAlternativesMatrixFuzzy res = MatrixesMC.newAlternativesFuzzy();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		res.put(a1, a1, 1.0000d);
		res.put(a1, a2, 0.6897d);
		res.put(a1, a3, 0.6897d);
		res.put(a2, a1, 0.3103d);
		res.put(a2, a2, 1.0000d);
		res.put(a2, a3, 0.3103d);
		res.put(a3, a1, 0.4290d);
		res.put(a3, a2, 0.8241d);
		res.put(a3, a3, 1.0000d);
		return res;
	}

	public Set<Criterion> getCriteria() {
		if (!m_critsOk) {
			final Criterion c1 = new Criterion("c01");
			final Criterion c2 = new Criterion("c02");
			final Criterion c3 = new Criterion("c03");
			final Criterion c4 = new Criterion("c04");
			final Criterion c5 = new Criterion("c05");
			m_data.getCriteria().add(c1);
			m_data.getCriteria().add(c2);
			m_data.getCriteria().add(c3);
			m_data.getCriteria().add(c4);
			m_data.getCriteria().add(c5);
			m_criteriaNames.put(c1, "Price");
			m_criteriaNames.put(c2, "Power");
			m_criteriaNames.put(c3, "0-100");
			m_criteriaNames.put(c4, "Consumption");
			m_criteriaNames.put(c5, "CO2");
			m_data.setScale(c1, Intervals.newInterval(PreferenceDirection.MINIMIZE, 10000d, 35000d));
			m_data.setScale(c2, Intervals.newInterval(PreferenceDirection.MAXIMIZE, 100d, 200d));
			m_data.setScale(c3, Intervals.newMinimizeDirection());
			m_data.setScale(c4, Intervals.newMinimizeDirection());
			m_data.setScale(c5, Intervals.newMinimizeDirection());
			m_critsOk = true;
		}
		return Collections.unmodifiableSet(m_data.getCriteria());
	}

	public Map<Criterion, String> getCriteriaNames() {
		getCriteria();
		return m_criteriaNames;
	}

	public Set<Criterion> getCriteriaWithNiceNames() {
		final Collection<Criterion> crits = Maps.transformValues(getCriteriaNames(), new Function<String, Criterion>() {
			@Override
			public Criterion apply(String input) {
				return new Criterion(input);
			}
		}).values();
		return Sets.newLinkedHashSet(crits);
	}

	public Map<Criterion, SparseAlternativesMatrixFuzzy> getDiscordanceByCriteria() {
		final Map<Criterion, SparseAlternativesMatrixFuzzy> discs = new HashMap<Criterion, SparseAlternativesMatrixFuzzy>();
		final Set<Criterion> crits = getCriteria();
		for (Criterion criterion : crits) {
			discs.put(criterion, MatrixesMC.newAlternativesFuzzy());
		}
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		final Criterion c1 = new Criterion("c01");
		final Criterion c2 = new Criterion("c02");
		final Criterion c3 = new Criterion("c03");
		final Criterion c4 = new Criterion("c04");
		final Criterion c5 = new Criterion("c05");
		final SparseMatrixFuzzy<Alternative, Alternative> d01 = discs.get(c1);
		d01.put(a1, a1, 0.0000d);
		d01.put(a1, a2, 0.0000d);
		d01.put(a1, a3, 0.0000d);
		d01.put(a1, a4, 0.0000d);
		d01.put(a1, a5, 0.0000d);
		d01.put(a1, a6, 0.0000d);
		d01.put(a2, a1, 1.0000d);
		d01.put(a2, a2, 0.0000d);
		d01.put(a2, a3, 0.4500d);
		d01.put(a2, a4, 0.0000d);
		d01.put(a2, a5, 1.0000d);
		d01.put(a2, a6, 0.0000d);
		d01.put(a3, a1, 0.0000d);
		d01.put(a3, a2, 0.0000d);
		d01.put(a3, a3, 0.0000d);
		d01.put(a3, a4, 0.0000d);
		d01.put(a3, a5, 0.0000d);
		d01.put(a3, a6, 0.0000d);
		d01.put(a4, a1, 1.0000d);
		d01.put(a4, a2, 1.0000d);
		d01.put(a4, a3, 1.0000d);
		d01.put(a4, a4, 0.0000d);
		d01.put(a4, a5, 1.0000d);
		d01.put(a4, a6, 1.0000d);
		d01.put(a5, a1, 0.0000d);
		d01.put(a5, a2, 0.0000d);
		d01.put(a5, a3, 0.0000d);
		d01.put(a5, a4, 0.0000d);
		d01.put(a5, a5, 0.0000d);
		d01.put(a5, a6, 0.0000d);
		d01.put(a6, a1, 1.0000d);
		d01.put(a6, a2, 0.0000d);
		d01.put(a6, a3, 0.0000d);
		d01.put(a6, a4, 0.0000d);
		d01.put(a6, a5, 1.0000d);
		d01.put(a6, a6, 0.0000d);
		final SparseMatrixFuzzy<Alternative, Alternative> d02 = discs.get(c2);
		d02.put(a1, a1, 0.0000d);
		d02.put(a1, a2, 0.0000d);
		d02.put(a1, a3, 0.0000d);
		d02.put(a1, a4, 0.0000d);
		d02.put(a1, a5, 0.0000d);
		d02.put(a1, a6, 0.0000d);
		d02.put(a2, a1, 0.0000d);
		d02.put(a2, a2, 0.0000d);
		d02.put(a2, a3, 0.0000d);
		d02.put(a2, a4, 0.0000d);
		d02.put(a2, a5, 0.0000d);
		d02.put(a2, a6, 0.0000d);
		d02.put(a3, a1, 0.0000d);
		d02.put(a3, a2, 0.0000d);
		d02.put(a3, a3, 0.0000d);
		d02.put(a3, a4, 0.0000d);
		d02.put(a3, a5, 0.0000d);
		d02.put(a3, a6, 0.0000d);
		d02.put(a4, a1, 0.0000d);
		d02.put(a4, a2, 0.0000d);
		d02.put(a4, a3, 0.0000d);
		d02.put(a4, a4, 0.0000d);
		d02.put(a4, a5, 0.0000d);
		d02.put(a4, a6, 0.0000d);
		d02.put(a5, a1, 0.0000d);
		d02.put(a5, a2, 0.0000d);
		d02.put(a5, a3, 0.0000d);
		d02.put(a5, a4, 0.0000d);
		d02.put(a5, a5, 0.0000d);
		d02.put(a5, a6, 0.0000d);
		d02.put(a6, a1, 0.0000d);
		d02.put(a6, a2, 0.0000d);
		d02.put(a6, a3, 0.0000d);
		d02.put(a6, a4, 0.0000d);
		d02.put(a6, a5, 0.0000d);
		d02.put(a6, a6, 0.0000d);
		final SparseMatrixFuzzy<Alternative, Alternative> d03 = discs.get(c3);
		d03.put(a1, a1, 0.0000d);
		d03.put(a1, a2, 0.0000d);
		d03.put(a1, a3, 0.0000d);
		d03.put(a1, a4, 0.0000d);
		d03.put(a1, a5, 0.0000d);
		d03.put(a1, a6, 0.0000d);
		d03.put(a2, a1, 0.0000d);
		d03.put(a2, a2, 0.0000d);
		d03.put(a2, a3, 0.0000d);
		d03.put(a2, a4, 0.0000d);
		d03.put(a2, a5, 0.0000d);
		d03.put(a2, a6, 0.0000d);
		d03.put(a3, a1, 0.0000d);
		d03.put(a3, a2, 0.0000d);
		d03.put(a3, a3, 0.0000d);
		d03.put(a3, a4, 0.0000d);
		d03.put(a3, a5, 0.0000d);
		d03.put(a3, a6, 0.0000d);
		d03.put(a4, a1, 0.0000d);
		d03.put(a4, a2, 0.0000d);
		d03.put(a4, a3, 0.0000d);
		d03.put(a4, a4, 0.0000d);
		d03.put(a4, a5, 0.0000d);
		d03.put(a4, a6, 0.0000d);
		d03.put(a5, a1, 0.0000d);
		d03.put(a5, a2, 0.0000d);
		d03.put(a5, a3, 0.0000d);
		d03.put(a5, a4, 0.0000d);
		d03.put(a5, a5, 0.0000d);
		d03.put(a5, a6, 0.0000d);
		d03.put(a6, a1, 0.0000d);
		d03.put(a6, a2, 0.0000d);
		d03.put(a6, a3, 0.0000d);
		d03.put(a6, a4, 0.0000d);
		d03.put(a6, a5, 0.0000d);
		d03.put(a6, a6, 0.0000d);
		final SparseMatrixFuzzy<Alternative, Alternative> d04 = discs.get(c4);
		d04.put(a1, a1, 0.0000d);
		d04.put(a1, a2, 0.0000d);
		d04.put(a1, a3, 0.0000d);
		d04.put(a1, a4, 0.0000d);
		d04.put(a1, a5, 0.0000d);
		d04.put(a1, a6, 0.0000d);
		d04.put(a2, a1, 0.0000d);
		d04.put(a2, a2, 0.0000d);
		d04.put(a2, a3, 0.0000d);
		d04.put(a2, a4, 0.0000d);
		d04.put(a2, a5, 0.0000d);
		d04.put(a2, a6, 0.0000d);
		d04.put(a3, a1, 0.0000d);
		d04.put(a3, a2, 0.0000d);
		d04.put(a3, a3, 0.0000d);
		d04.put(a3, a4, 0.0000d);
		d04.put(a3, a5, 0.0000d);
		d04.put(a3, a6, 0.0000d);
		d04.put(a4, a1, 0.0000d);
		d04.put(a4, a2, 0.0000d);
		d04.put(a4, a3, 0.0000d);
		d04.put(a4, a4, 0.0000d);
		d04.put(a4, a5, 0.0000d);
		d04.put(a4, a6, 0.0000d);
		d04.put(a5, a1, 0.0000d);
		d04.put(a5, a2, 0.0000d);
		d04.put(a5, a3, 0.0000d);
		d04.put(a5, a4, 0.0000d);
		d04.put(a5, a5, 0.0000d);
		d04.put(a5, a6, 0.0000d);
		d04.put(a6, a1, 0.0000d);
		d04.put(a6, a2, 0.0000d);
		d04.put(a6, a3, 0.0000d);
		d04.put(a6, a4, 0.0000d);
		d04.put(a6, a5, 0.0000d);
		d04.put(a6, a6, 0.0000d);
		final SparseMatrixFuzzy<Alternative, Alternative> d05 = discs.get(c5);
		d05.put(a1, a1, 0.0000d);
		d05.put(a1, a2, 0.0000d);
		d05.put(a1, a3, 0.0000d);
		d05.put(a1, a4, 0.0000d);
		d05.put(a1, a5, 0.0000d);
		d05.put(a1, a6, 0.0000d);
		d05.put(a2, a1, 0.0000d);
		d05.put(a2, a2, 0.0000d);
		d05.put(a2, a3, 0.0000d);
		d05.put(a2, a4, 0.0000d);
		d05.put(a2, a5, 0.0000d);
		d05.put(a2, a6, 0.0000d);
		d05.put(a3, a1, 0.0000d);
		d05.put(a3, a2, 0.0000d);
		d05.put(a3, a3, 0.0000d);
		d05.put(a3, a4, 0.0000d);
		d05.put(a3, a5, 0.0000d);
		d05.put(a3, a6, 0.0000d);
		d05.put(a4, a1, 0.0000d);
		d05.put(a4, a2, 0.0000d);
		d05.put(a4, a3, 0.0000d);
		d05.put(a4, a4, 0.0000d);
		d05.put(a4, a5, 0.0000d);
		d05.put(a4, a6, 0.0000d);
		d05.put(a5, a1, 0.0000d);
		d05.put(a5, a2, 0.0000d);
		d05.put(a5, a3, 0.0000d);
		d05.put(a5, a4, 0.0000d);
		d05.put(a5, a5, 0.0000d);
		d05.put(a5, a6, 0.0000d);
		d05.put(a6, a1, 0.0000d);
		d05.put(a6, a2, 0.0000d);
		d05.put(a6, a3, 0.0000d);
		d05.put(a6, a4, 0.0000d);
		d05.put(a6, a5, 0.0000d);
		d05.put(a6, a6, 0.0000d);

		return discs;
	}

	public AlternativesScores getNetFlows() {
		/** Results exact to the fourth decimal (rounded). */
		final AlternativesScores flows = new AlternativesScores();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		flows.put(a1, Double.valueOf(+0.1571d));
		flows.put(a2, Double.valueOf(-0.3406d));
		flows.put(a3, Double.valueOf(+0.3025d));
		flows.put(a4, Double.valueOf(-0.1557d));
		flows.put(a5, Double.valueOf(+0.1226d));
		flows.put(a6, Double.valueOf(-0.0858d));
		return flows;
	}

	public SparseAlternativesMatrixFuzzy getOutranking() {
		/** Results exact to the fourth decimal (rounded). */
		final SparseAlternativesMatrixFuzzy res = MatrixesMC.newAlternativesFuzzy();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		res.put(a1, a1, 1.0000d);
		res.put(a1, a2, 0.7000d);
		res.put(a1, a3, 0.4900d);
		res.put(a1, a4, 0.7000d);
		res.put(a1, a5, 0.7000d);
		res.put(a1, a6, 0.7000d);
		res.put(a2, a1, 0.0000d);
		res.put(a2, a2, 1.0000d);
		res.put(a2, a3, 0.2955d);
		res.put(a2, a4, 0.6136d);
		res.put(a2, a5, 0.0000d);
		res.put(a2, a6, 0.5880d);
		res.put(a3, a1, 0.6688d);
		res.put(a3, a2, 0.8740d);
		res.put(a3, a3, 1.0000d);
		res.put(a3, a4, 0.7540d);
		res.put(a3, a5, 0.7760d);
		res.put(a3, a6, 0.7540d);
		res.put(a4, a1, 0.0000d);
		res.put(a4, a2, 0.0000d);
		res.put(a4, a3, 0.0000d);
		res.put(a4, a4, 1.0000d);
		res.put(a4, a5, 0.0000d);
		res.put(a4, a6, 0.0000d);
		res.put(a5, a1, 0.7340d);
		res.put(a5, a2, 0.8080d);
		res.put(a5, a3, 0.6952d);
		res.put(a5, a4, 0.5203d);
		res.put(a5, a5, 1.0000d);
		res.put(a5, a6, 0.7300d);
		res.put(a6, a1, 0.0000d);
		res.put(a6, a2, 1.0000d);
		res.put(a6, a3, 0.3975d);
		res.put(a6, a4, 0.7576d);
		res.put(a6, a5, 0.0000d);
		res.put(a6, a6, 1.0000d);
		return res;
	}

	/**
	 * @return the binary relation representing an outranking over or equal to
	 *         0.7.
	 */
	public SparseAlternativesMatrixFuzzy getOutrankingAtDotSeven() {
		final SparseAlternativesMatrixFuzzy res = MatrixesMC.newAlternativesFuzzy();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		res.put(a1, a1, 1);
		res.put(a1, a2, 1);
		res.put(a1, a3, 0);
		res.put(a1, a4, 1);
		res.put(a1, a5, 1);
		res.put(a1, a6, 1);
		res.put(a2, a1, 0);
		res.put(a2, a2, 1);
		res.put(a2, a3, 0);
		res.put(a2, a4, 0);
		res.put(a2, a5, 0);
		res.put(a2, a6, 0);
		res.put(a3, a1, 0);
		res.put(a3, a2, 1);
		res.put(a3, a3, 1);
		res.put(a3, a4, 1);
		res.put(a3, a5, 1);
		res.put(a3, a6, 1);
		res.put(a4, a1, 0);
		res.put(a4, a2, 0);
		res.put(a4, a3, 0);
		res.put(a4, a4, 1);
		res.put(a4, a5, 0);
		res.put(a4, a6, 0);
		res.put(a5, a1, 1);
		res.put(a5, a2, 1);
		res.put(a5, a3, 0);
		res.put(a5, a4, 0);
		res.put(a5, a5, 1);
		res.put(a5, a6, 1);
		res.put(a6, a1, 0);
		res.put(a6, a2, 1);
		res.put(a6, a3, 0);
		res.put(a6, a4, 1);
		res.put(a6, a5, 0);
		res.put(a6, a6, 1);
		return res;
	}

	public AlternativesScores getPositiveFlows() {
		/** Results exact to the fourth decimal (rounded). */
		final AlternativesScores flows = new AlternativesScores();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		flows.put(a1, Double.valueOf(0.4991d));
		flows.put(a2, Double.valueOf(0.2036d));
		flows.put(a3, Double.valueOf(0.5372d));
		flows.put(a4, Double.valueOf(0.3309d));
		flows.put(a5, Double.valueOf(0.4251d));
		flows.put(a6, Double.valueOf(0.3292d));
		return flows;
	}

	public SparseAlternativesMatrixFuzzy getPreference() {
		/** Results exact to the fourth decimal (rounded). */
		final SparseAlternativesMatrixFuzzy res = MatrixesMC.newAlternativesFuzzy();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		res.put(a1, a1, 0.0000d);
		res.put(a1, a2, 0.6505d);
		res.put(a1, a3, 0.3312d);
		res.put(a1, a4, 0.5971d);
		res.put(a1, a5, 0.2660d);
		res.put(a1, a6, 0.6505d);
		res.put(a2, a1, 0.3000d);
		res.put(a2, a2, 0.0000d);
		res.put(a2, a3, 0.1260d);
		res.put(a2, a4, 0.4000d);
		res.put(a2, a5, 0.1920d);
		res.put(a2, a6, 0.0000d);
		res.put(a3, a1, 0.5100d);
		res.put(a3, a2, 0.6505d);
		res.put(a3, a3, 0.0000d);
		res.put(a3, a4, 0.6181d);
		res.put(a3, a5, 0.3048d);
		res.put(a3, a6, 0.6025d);
		res.put(a4, a1, 0.3000d);
		res.put(a4, a2, 0.3864d);
		res.put(a4, a3, 0.2460d);
		res.put(a4, a4, 0.0000d);
		res.put(a4, a5, 0.4797d);
		res.put(a4, a6, 0.2424d);
		res.put(a5, a1, 0.3000d);
		res.put(a5, a2, 0.6217d);
		res.put(a5, a3, 0.2240d);
		res.put(a5, a4, 0.4000d);
		res.put(a5, a5, 0.0000d);
		res.put(a5, a6, 0.5797d);
		res.put(a6, a1, 0.3000d);
		res.put(a6, a2, 0.4120d);
		res.put(a6, a3, 0.2460d);
		res.put(a6, a4, 0.4180d);
		res.put(a6, a5, 0.2700d);
		res.put(a6, a6, 0.0000d);
		return res;
	}

	public Set<Alternative> getProfiles() {
		if (m_data.getProfiles().isEmpty()) {
			final Alternative pBM = new Alternative("pBM");
			final Alternative pMG = new Alternative("pMG");
			m_data.getProfiles().add(pBM);
			m_data.getProfiles().add(pMG);
			// m_alternativeNames.put(pBM, "profile bad to medium");
			// m_alternativeNames.put(pMG, "profile medium to good");
		}
		return Collections.unmodifiableSet(m_data.getProfiles());
	}

	public EvaluationsRead getProfilesEvaluations() {
		if (m_data.getProfilesEvaluations().isEmpty()) {
			final Evaluations evals = EvaluationsUtils.newEvaluationMatrix();
			final Alternative pBM = new Alternative("pBM");
			final Alternative pMG = new Alternative("pMG");
			final Criterion c1 = new Criterion("c01");
			final Criterion c2 = new Criterion("c02");
			final Criterion c3 = new Criterion("c03");
			final Criterion c4 = new Criterion("c04");
			final Criterion c5 = new Criterion("c05");
			evals.put(pBM, c1, 30000d);
			evals.put(pBM, c2, 100d);
			evals.put(pBM, c3, 11d);
			evals.put(pBM, c4, 8d);
			evals.put(pBM, c5, 125d);
			evals.put(pMG, c1, 23000d);
			evals.put(pMG, c2, 160d);
			evals.put(pMG, c3, 8d);
			evals.put(pMG, c4, 7d);
			evals.put(pMG, c5, 120d);
			m_data.setProfilesEvaluations(evals);
		}
		return m_data.getProfilesEvaluations();
	}

	public Evaluations getPrometheeProfiles() {
		final Evaluations profiles = EvaluationsUtils.newEvaluationMatrix();
		final Alternative a1 = new Alternative("a01");
		final Alternative a2 = new Alternative("a02");
		final Alternative a3 = new Alternative("a03");
		final Alternative a4 = new Alternative("a04");
		final Alternative a5 = new Alternative("a05");
		final Alternative a6 = new Alternative("a06");
		final Criterion c1 = new Criterion("c01");
		final Criterion c2 = new Criterion("c02");
		final Criterion c3 = new Criterion("c03");
		final Criterion c4 = new Criterion("c04");
		final Criterion c5 = new Criterion("c05");
		profiles.put(a1, c1, .7792d);
		profiles.put(a1, c2, -1.0000d);
		profiles.put(a1, c3, -1.0000d);
		profiles.put(a1, c4, .5800d);
		profiles.put(a1, c5, .2620d);
		profiles.put(a2, c1, -.4200d);
		profiles.put(a2, c2, .2267d);
		profiles.put(a2, c3, .1800d);
		profiles.put(a2, c4, -1.0000d);
		profiles.put(a2, c5, -.2780d);
		profiles.put(a3, c1, .2984d);
		profiles.put(a3, c2, -.2667d);
		profiles.put(a3, c3, -.0200d);
		profiles.put(a3, c4, 1.0000d);
		profiles.put(a3, c5, .2620d);
		profiles.put(a4, c1, -1.0000d);
		profiles.put(a4, c2, .6933d);
		profiles.put(a4, c3, .5300d);
		profiles.put(a4, c4, .2000d);
		profiles.put(a4, c5, .1540d);
		profiles.put(a5, c1, .6984d);
		profiles.put(a5, c2, -.4067d);
		profiles.put(a5, c3, -.2200d);
		profiles.put(a5, c4, -.2200d);
		profiles.put(a5, c5, -.1220d);
		profiles.put(a6, c1, -.3560d);
		profiles.put(a6, c2, .7533d);
		profiles.put(a6, c3, .5300d);
		profiles.put(a6, c4, -.5600d);
		profiles.put(a6, c5, -.2780d);
		return profiles;
	}

	public Map<Criterion, Interval> getScales() {
		getCriteria();
		return m_data.getScales();
	}

	public Thresholds getThresholds() {
		if (m_data.getThresholds().isEmpty()) {
			final Thresholds thresholds = ThresholdsUtils.newThresholds();

			final Criterion c1 = new Criterion("c01");
			final Criterion c2 = new Criterion("c02");
			final Criterion c3 = new Criterion("c03");
			final Criterion c4 = new Criterion("c04");
			final Criterion c5 = new Criterion("c05");

			thresholds.setPreferenceThreshold(c1, 3000d);
			thresholds.setPreferenceThreshold(c2, 30d);
			thresholds.setPreferenceThreshold(c3, 2d);
			thresholds.setPreferenceThreshold(c4, 1d);
			thresholds.setPreferenceThreshold(c5, 100d);

			thresholds.setIndifferenceThreshold(c1, 500d);
			thresholds.setIndifferenceThreshold(c2, 0d);
			thresholds.setIndifferenceThreshold(c3, 0d);
			thresholds.setIndifferenceThreshold(c4, 0d);
			thresholds.setIndifferenceThreshold(c5, 0d);

			thresholds.setVetoThreshold(c1, 4000d);

			m_data.setThresholds(thresholds);
		}
		return m_data.getThresholds();
	}

	public Weights getWeights() {
		return getCoalitions70().getWeights();
	}

}
