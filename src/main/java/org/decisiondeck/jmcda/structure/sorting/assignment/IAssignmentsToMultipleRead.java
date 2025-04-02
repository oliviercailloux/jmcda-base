package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilitiesRead;

/**
 * <p>
 * A read interface for a set of assignments, where an assignment is a mapping of an alternative to a set of categories,
 * i.e., at least one category.
 * </p>
 * <p>
 * An assignments to multiple categories object which is not an {@link IAssignmentsWithCredibilitiesRead} equals an
 * other one iff they contain the same alternatives assigned to the same categories and the same set of overall
 * categories. The last condition is not redundant as the set of categories may be a superset of the set of categories
 * to which alternatives are assigned. <em>Warning</em>: an assignments to multiple categories object that does not
 * implement {@link IAssignmentsWithCredibilitiesRead} never equals an object implementing
 * {@link IAssignmentsWithCredibilitiesRead}: as the latter adds a capability, the notion of equality changes. For
 * equality to remain symmetric, objects implementing this interface must treat specifically the case where they are
 * tested for equality against an object with credibilities: the equality test must return {@code false} in that
 * case. The same remark applies for objects implementing {@link IOrderedAssignmentsToMultipleRead}: the ordering adds a
 * capability which is taken into account in the equality check, thus an object without ordering is defined to never
 * equal an object with ordering. See Joshua Bloch, <a href="http://java.sun.com/docs/books/effective/">Effective Java
 * Second Edition</a>, Chapter 3, Item 8 (or Item 7 in the sample chapter of the <a
 * href="http://java.sun.com/developer/Books/effectivejava/">first edition</a>).
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignmentsToMultipleRead {
    /**
     * Retrieves a read-only view of the assigned alternatives.
     * 
     * @return not {@code null}.
     */
    public Set<Alternative> getAlternatives();

    /**
     * <p>
     * Indicates whether the given object is equal to this one. Supposing this object does not implement
     * {@link IAssignmentsWithCredibilitiesRead} nor {@link IOrderedAssignmentsToMultipleRead}, this is
     * {@code true} iff the given object is a IAssignmentsToMultipleRead, does not implement any of these mentioned
     * interfaces either, and contains the same alternatives assigned to the same categories and the same set of overall
     * categories as this object.
     * </p>
     * <p>
     * Other cases are more subtle. Objects implementing IAssignmentsToMultipleRead may be categorized, according to
     * their capabilities, into four classes: objects having no credibilities and no ordered assignments, objects having
     * credibilities but no ordered assignments, objects having ordered assignments but no credibilities, and objects
     * having both credibilities and ordered assignments. It is meaningless to compare objects belonging to different
     * classes of capabilities and the equality test between objects which are non homogeneous in that sense return
     * {@code false}. If this object and the compared object both have credibilities, a supplementary condition is
     * added for the objects to be equal, namely that the assignments must be associated with the same credibilities to
     * the same categories. If this object and the compared object both are ordered, the added condition is that the set
     * of categories contained in the objects must have the same order. Both supplementary conditions are required if
     * the objects being tested for equality are ordered and have credibilities.
     * </p>
     * 
     * @param obj
     *            may be {@code null}.
     * @return {@code true} iff both objects are considered equal.
     */
    @Override
    public boolean equals(Object obj);

    /**
     * <p>
     * Retrieves a read-only view, or copy, of the alternatives that are assigned to the given category, or to a set of
     * categories including the given category.
     * </p>
     * <p>
     * If the given category is not in the set returned by {@link #getCategories()}, the returned set is empty.
     * </p>
     * 
     * @param category
     *            not {@code null}.
     * 
     * @return not {@code null}.
     */
    public Set<Alternative> getAlternatives(Category category);

    /**
     * Retrieves the categories to which an alternative is assigned.
     * 
     * @param alternative
     *            not {@code null}.
     * @return a read-only copy of the set of categories to which this alternative is assigned, or {@code null} iff
     *         the alternative is not assigned. The returned set is never empty. The returned set is a copy (if the
     *         assignment related to the given alternative later change, this change is not reflected to the object this
     *         method returns).
     */
    public Set<Category> getCategories(Alternative alternative);

    /**
     * <p>
     * Retrieves a (possibly read-only) copy of a set containing at least all the categories to which at least one
     * alternative is assigned. Depending on the implementing object, the returned set may be larger than this. It may
     * for example contain all the categories that are available in some context, even when the alternatives assignments
     * do not cover the whole set of possibilities.
     * </p>
     * <p>
     * The returned set is a copy: if the assignment related to the given alternative later change, this change is not
     * reflected to the object this method returns.
     * </p>
     * 
     * @return a set, not {@code null}, empty iff no alternatives are assigned.
     */
    public Set<Category> getCategories();
}
