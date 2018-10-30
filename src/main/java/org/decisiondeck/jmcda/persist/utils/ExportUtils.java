package org.decisiondeck.jmcda.persist.utils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.exc.FunctionWithInputCheck;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataArray;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataWithOrder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataArray;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataWithOrder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ExportUtils {

    /**
     * Copies the settings found in the given source to the given target, overriding the target to make it equivalent to
     * the source. If some orders are not defined in the source, they end up being not defined either in the target.
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     */
    static public void copySettings(ExportSettings source, ExportSettings target) {
	target.setAlternativesOrder(source.getAlternativesOrder());
	target.setAlternativesToString(source.getAlternativesToString());
	target.setCategoriesToString(source.getCategoriesToString());
	target.setCriteriaOrder(source.getCriteriaOrder());
	target.setCriteriaToString(source.getCriteriaToString());
	target.setDmsOrder(source.getDmsOrder());
	target.setDmsToString(source.getDmsToString());
	target.setProfilesOrder(source.getProfilesOrder());
	target.setProfilesToString(source.getProfilesToString());
	target.setNumberFormatter(source.getNumberFormatter());
    }

    private static class Increment implements Function<Integer, Integer> {
	private final int m_countFrom;

	public Increment(int countFrom) {
	    m_countFrom = countFrom;
	}

	@Override
	public Integer apply(Integer input) {
	    return Integer.valueOf(input.intValue() + m_countFrom);
	}
    }

    private static final class CheckedToNormal<I> implements Function<I, String> {
	private final FunctionWithInputCheck<I, String> m_functionWithCheck;

	public CheckedToNormal(FunctionWithInputCheck<I, String> functionWithCheck) {
	    Preconditions.checkNotNull(functionWithCheck);
	    m_functionWithCheck = functionWithCheck;
	}

	@Override
	public String apply(I input) {
	    try {
		return m_functionWithCheck.apply(input);
	    } catch (InvalidInputException exc) {
		throw new IllegalArgumentException(exc);
	    }
	}
    }

    static private class InputToIntToString<I> implements FunctionWithInputCheck<I, String> {
	private final Map<I, Integer> m_ints;

	public InputToIntToString(Map<I, Integer> ints) {
	    m_ints = ImmutableMap.copyOf(ints);
	}

	@Override
	public String apply(I input) throws InvalidInputException {
	    final Integer theId = m_ints.get(input);
	    if (theId == null) {
		throw new InvalidInputException("" + input);
	    }
	    final NumberFormat integer = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	    return integer.format(theId.intValue());
	}
    }

    /**
     * Retrieves a function that associates to an object an integer as an English formatted string. The returned
     * function knows the mapping at the time this method is called, it is not a view to the given map (which means that
     * if the data is changed, this is not reflected in the returned function). The returned function will throw an
     * {@link InvalidInputException} iff it is asked about an input object that did not exist in the given data at the
     * time this method was called.
     * 
     * @param <I>
     *            the input object type.
     * 
     * @param ints
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <I> FunctionWithInputCheck<I, String> getInputToIntToStringFctWithInputCheck(Map<I, Integer> ints) {
	return new InputToIntToString<I>(ints);
    }

    /**
     * Retrieves a function that associates to a category its rank (1 is the best one) as an English formatted string.
     * The returned function knows the categories at the time this method is called, it is not a view to the given data
     * (which means that if the data is changed, this is not reflected in the returned function). If the categories are
     * incomplete, the rank is still computed according to the order from best to worst, even though it will not
     * necessarily be compatible with the profiles ranks. The returned function will throw an
     * {@link InvalidInputException} iff it is asked for a rank of a category that did not exist in the given data at
     * the time this method was called, otherwize it returns a string corresponding to a number between one and the
     * number of categories.
     * 
     * @param sortingData
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public FunctionWithInputCheck<Category, String> getCategoryRankFct(ISortingData sortingData) {
	return new InputToIntToString<Category>(new SortingDataArray(sortingData).getCategoriesRanks());
    }

    /**
     * Retrieves a function that associates to an object an integer as an English formatted string. The returned
     * function knows the mapping at the time this method is called, it is not a view to the given map (which means that
     * if the data is changed, this is not reflected in the returned function). The returned function will throw an
     * {@link InvalidInputException} iff it is asked about an input object that did not exist in the given data at the
     * time this method was called.
     * 
     * @param <I>
     *            the input object type.
     * 
     * @param ints
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <I> Function<I, String> getInputToIntToStringFct(Map<I, Integer> ints) {
	final FunctionWithInputCheck<I, String> check = new InputToIntToString<I>(ints);
	return new CheckedToNormal<I>(check);
    }

    /**
     * Note that the returned settings contain copy of the given source data, thus writing to the source data is not
     * reflected in the settings, thus after a write the previously returned settings object should not be used anymore
     * as it will be outdated.
     * 
     * @param source
     *            not <code>null</code>. The profiles must all be ordered through the categories and profiles object.
     * @param countFrom
     *            the index to start counting from (typically zero or one). Applies to alternatives, profiles, criteria,
     *            categories.
     * @return a settings object configured to export indexes of the objects (i.e. alternatives, profiles, criteria,
     *         categories), ordered by their natural ordering, thus alphabetical order of their id, except categories
     *         and profiles which are ordered from worst to best.
     */
    static public ExportSettings newExportByIndexSettings(ISortingData source, final int countFrom) {
	final SortingDataWithOrder order = new SortingDataWithOrder(source);
	order.setProfilesOrderByPreference();
	final SortingDataArray input = new SortingDataArray(order);

	final ExportSettings settings = new ExportSettings();
	settings.setAlternativesOrder(input.getAlternatives());
	settings.setAlternativesToString(ExportUtils.getInputToIntToStringFct(getIncremented(
		input.getAlternativesIndexes(), countFrom)));
	settings.setCategoriesToString(ExportUtils.getInputToIntToStringFct(getIncremented(
		input.getCategoriesIndexes(), countFrom)));
	settings.setCriteriaOrder(input.getCriteria());
	settings.setCriteriaToString(ExportUtils.getInputToIntToStringFct(getIncremented(input.getCriteriaIndexes(),
		countFrom)));
	settings.setProfilesOrder(input.getProfiles());
	settings.setProfilesToString(ExportUtils.getInputToIntToStringFct(getIncremented(input.getProfilesIndexes(),
		countFrom)));
	return settings;
    }

    static private <T> Map<T, Integer> getIncremented(final Map<T, Integer> map, final int increment) {
	return Maps.transformValues(map, new Increment(increment));
    }

    /**
     * Note that the returned settings contain copy of the given source data, thus writing to the source data is not
     * reflected in the settings, thus after a write the previously returned settings object should not be used anymore
     * as it will be outdated. The returned settings order on the decision makers is unspecified.
     * 
     * @param source
     *            not <code>null</code>. The profiles must all be ordered through the categories and profiles object.
     * @return a settings object configured to export ids of the objects (i.e. alternatives, profiles, criteria,
     *         categories), ordered by their natural ordering, thus alphabetical order of their id, except categories
     *         and profiles which are ordered from worst (index 0) to best.
     */
    static public ExportSettings newExportSettings(ISortingData source) {
	final SortingDataWithOrder order = new SortingDataWithOrder(source);
	order.setProfilesOrderByPreference();

	final ExportSettings settings = new ExportSettings();
	settings.setAlternativesOrder(order.getAlternatives());
	settings.setCriteriaOrder(order.getCriteria());
	settings.setProfilesOrder(order.getProfiles());
	return settings;
    }

    static public void setOrder(ISortingData source, ExportSettings target) {
	copySettings(newExportSettings(source), target);
    }

    /**
     * Note that the returned settings contain copy of the given source data, thus writing to the source data is not
     * reflected in the settings, thus after a write the previously returned settings object should not be used anymore
     * as it will be outdated.
     * 
     * @param source
     *            not <code>null</code>. The profiles must all be ordered through the categories and profiles object.
     * @return a settings object configured to export indexes of the objects (i.e. alternatives, profiles, criteria,
     *         decision makers, categories), counting from zero, ordered by their natural ordering, thus alphabetical
     *         order of their id, except categories and profiles which are ordered from worst (index 0) to best.
     */
    static public ExportSettings newExportByIndexSettings(IGroupSortingData source) {
	final GroupSortingDataWithOrder order = new GroupSortingDataWithOrder(source);
	order.setProfilesOrderByPreference();
	final GroupSortingDataArray input = new GroupSortingDataArray(order);

	final ExportSettings settings = new ExportSettings();
	settings.setAlternativesOrder(input.getAlternatives());
	settings.setAlternativesToString(ExportUtils.getInputToIntToStringFct(input.getAlternativesIndexes()));
	settings.setCategoriesToString(ExportUtils.getInputToIntToStringFct(input.getCategoriesIndexes()));
	settings.setCriteriaOrder(input.getCriteria());
	settings.setCriteriaToString(ExportUtils.getInputToIntToStringFct(input.getCriteriaIndexes()));
	settings.setDmsOrder(input.getDms());
	settings.setDmsToString(ExportUtils.getInputToIntToStringFct(input.getDmsIndexes()));
	settings.setProfilesOrder(input.getProfiles());
	settings.setProfilesToString(ExportUtils.getInputToIntToStringFct(input.getProfilesIndexes()));
	return settings;
    }

}
