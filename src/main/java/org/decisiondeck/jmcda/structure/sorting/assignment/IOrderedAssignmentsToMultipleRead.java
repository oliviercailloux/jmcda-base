package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;

/**
 * <p>
 * Equivalent to {@link IAssignmentsToMultipleRead} to use when the categories the alternatives are assigned to are
 * provided with a total ordering, thus can be sorted from worst to best without ex-æquo.
 * </p>
 * <p>
 * An assignments to multiple categories object equals an other one iff they contain the same alternatives assigned to
 * the same categories and the same set of overall categories. The last condition is not redundant as the set of
 * categories may be a superset of the set of categories to which alternatives are assigned.
 * </p>
 * <p>
 * This interface does not extend {@link IOrderedAssignmentsWithCredibilitiesRead} because it would involve automatic
 * guess of arbitrary credibility degrees, e.g. one divided by the number of categories an alternative is assigned to.
 * This would then forbids an algorithm to clearly specify if it really uses credibility degrees as input or not. The
 * user could think that an algorithm will not use the credibility degrees whereas it will use them, therefore making
 * use of credibility numbers which have no real meaning.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsToMultipleRead extends IAssignmentsToMultipleRead {

    /**
     * Retrieves the categories to which the given alternative is assigned, ordered from the worst to the best category.
     * That ordering is compatible with the ordering given by {@link #getCategories()}.
     * 
     * @param alternative
     *            not {@code null}.
     * @return a (possibly read-only) copy of the set of categories to which this alternative is assigned, or
     *         {@code null} iff the alternative is not assigned. If the assignment related to the given alternative
     *         later change, this change is not reflected to the object this method returns.
     */
    @Override
    public NavigableSet<Category> getCategories(Alternative alternative);

    /**
     * <p>
     * Retrieves a (possibly read-only) copy of a set containing at least all the categories to which at least one
     * alternative is assigned, ordered from the worst to the best category. Depending on the implementing object, the
     * returned set may be larger than this. It may for example contain all the categories that are available in some
     * context, even when the alternatives assignments do not cover the whole set of possibilities.
     * </p>
     * <p>
     * The returned set is immutable (if the assignment related to the given alternative later change, this change is
     * not reflected to the object this method returns).
     * </p>
     * 
     * @return a set, not {@code null}, possibly empty if no alternatives are assigned.
     */
    @Override
    public NavigableSet<Category> getCategories();

    /**
     * <p>
     * Indicates whether the given object is equal to this one.
     * </p>
     * <ul>
     * <li>Supposing this object does not implement {@link IAssignmentsWithCredibilitiesRead}, this is {@code true}
     * iff the given object is a IOrderedAssignmentsToMultipleRead, does not implement the mentioned interface either,
     * and contains the same alternatives assigned to the same categories and the same set of overall categories as this
     * object in the same order.</li>
     * <li>If the given object does not implement {@link IOrderedAssignmentsToMultipleRead}, this method returns
     * {@code false}.</li>
     * <li>If this object and the compared object both have credibilities, a supplementary condition is added for the
     * objects to be equal, namely that the assignments must be associated with the same credibilities to the same
     * categories.</li>
     * <li>If this object does not have credibilities but the given object does, or conversely, they are never
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
