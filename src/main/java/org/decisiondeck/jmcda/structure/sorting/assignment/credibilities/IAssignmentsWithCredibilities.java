package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * A set of assignments where one assignment is a mapping of an alternative to a set of categories with associated
 * credibilities, meaning that each category to which an alternative is mapped has an associated degree of credibility,
 * a real number greater than zero, generally indicating the strength of assignment of that alternative to that
 * category. An alternative is said to be assigned if it is assigned to at least one category (i.e. the set of
 * categories to which it is mapped may not be empty).
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignmentsWithCredibilities extends IAssignmentsWithCredibilitiesRead {
    /**
     * Sets, replaces, or removes the assignment of an alternative and the associated degrees of credibility. A zero
     * value as a degree of credibility is considered as equivalent to a missing entry: it is interpreted as meaning
     * that the given alternative is not assigned to the corresponding category.
     * 
     * @param alternative
     *            not {@code null}.
     * @param credibilities
     *            {@code null} or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. The map entries may not contain a {@code null} key or value, the values
     *            must be positive or zero. If the map contains only zeroes, it is considered empty.
     * @return {@code true} iff the call changed the assignments, i.e., iff the assignment existed and has been
     *         removed, or existed and has changed (be it a change in some credibility degrees or a change of category),
     *         or did not exist and has been added.
     */
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities);

    public boolean clear();
}
