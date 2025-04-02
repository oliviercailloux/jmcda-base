package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;

/**
 * <p>
 * A read-only interface for a set of assignments where one assignment is a mapping of an alternative to a set of
 * categories with associated credibilities, meaning that each category to which an alternative is mapped has an
 * associated degree of credibility (a real number greater than zero) indicating how strong the alternative belongs to
 * that category. An alternative is said to be assigned if it is assigned to at least one category (i.e. the set of
 * categories to which it is mapped may not be empty).
 * </p>
 * <p>
 * Credibility degrees are required to be greater than zero, but no constraint is set on their sum. In particular, they
 * do not have to all sum to one. This permits to have different sum of degrees to different assignments, which allows
 * all assigned alternative to not necessarily have the same sum of credibility (and hence to compare their sums, e.g.).
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignmentsWithCredibilitiesRead extends IAssignmentsToMultipleRead {

    /**
     * Retrieves a (possibly read-only) copy of the categories an alternative is assigned to together with their
     * associated credibility degrees. The returned map is a copy (if the assignment related to the given alternative
     * later change, this change is not reflected to the object this method returns) and contain no {@code null}
     * keys or values and only values greater than zero.
     * 
     * @param alternative
     *            not {@code null}.
     * @return {@code null} iff the given alternative is not assigned, otherwise, a map containing at least one
     *         entry.
     */
    public Map<Category, Double> getCredibilities(Alternative alternative);

    /**
     * <p>
     * Indicates whether the given object is equal to this one.
     * </p>
     * <ul>
     * <li>Supposing this object does not implement {@link IOrderedAssignmentsToMultipleRead}, this is {@code true}
     * iff the given object is a IAssignmentsWithCredibilitiesRead, does not implement the mentioned interface either,
     * and contains the same alternatives assigned to the same categories with the same credibilities and the same set
     * of overall categories as this object.</li>
     * <li>If the given object does not implement {@link IAssignmentsWithCredibilitiesRead}, this method returns
     * {@code false}.</li>
     * <li>If this object and the compared object both implement IOrderedAssignmentsToMultipleRead, a supplementary
     * condition is added for the objects to be equal, namely that the set of categories contained in both objects must
     * have the same order.</li>
     * <li>If this object does not have ordered assignments but the given object does, or conversely, they are never
     * considered equal.</li>
     * </ul>
     * 
     * @param obj
     *            may be {@code null}.
     * @return {@code true} iff both objects are considered equal.
     */
    @Override
    public boolean equals(Object obj);

}
