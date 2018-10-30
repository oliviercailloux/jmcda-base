package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.NavigableMap;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;

/**
 * <p>
 * Equivalent to {@link IAssignmentsWithCredibilitiesRead} to use when the categories the alternatives are assigned to
 * are provided with a total ordering, thus can be sorted from worst to best without ex-æquo.
 * </p>
 * <p>
 * An assignments to multiple categories with credibilities object equals an other one iff they contain the same
 * alternatives assigned to the same categories with the same credibilities and contain the same set of overall
 * categories in the same order.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsWithCredibilitiesRead extends IAssignmentsWithCredibilitiesRead,
	IOrderedAssignmentsToMultipleRead {

    /**
     * <p>
     * Retrieves a (possibly read-only) copy of the categories an alternative is assigned to together with their
     * associated credibility degrees, ordered from the worst category to the best category the given alternative is
     * assigned to. That ordering relates to the preference order on the categories and <em>not</em> to the degrees of
     * credibilities, i.e. it is usually not ordered by degree of credibility. The order is compatible with the ordering
     * given by {@link #getCategories()}.
     * </p>
     * 
     * @param alternative
     *            not <code>null</code>.
     * @return <code>null</code> iff the given alternative is not assigned, otherwise, a map containing at least one
     *         entry.
     */
    @Override
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative);

    /**
     * <p>
     * Indicates whether the given object is equal to this one. This is <code>true</code> iff the given object is a
     * IOrderedAssignmentsWithCredibilitiesRead, contains the same alternatives assigned to the same categories with the
     * same credibilities, and contains the same set of overall categories as this object in the same order.
     * </p>
     * 
     * @param obj
     *            may be <code>null</code>.
     * @return <code>true</code> iff both objects are considered equal.
     */
    @Override
    public boolean equals(Object obj);

}
