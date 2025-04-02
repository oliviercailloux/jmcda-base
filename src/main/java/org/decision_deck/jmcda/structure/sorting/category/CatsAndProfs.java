package org.decision_deck.jmcda.structure.sorting.category;

import java.util.NavigableSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.IObserver;

/**
 * <p>
 * Objects implementing this interface hold an ordered set of categories and an ordered set of profiles, together with
 * relations between them on the basis that a category has, possibly, a down and an up profile. The profiles may be set
 * before the categories, and conversely. This object associates automatically the profiles to the adequate categories.
 * E.g. adding a profile p1, then adding two categories c1 and c2, results in c1 having p1 as up profile and c2 having
 * p1 as down profile. Detailed explanation about how these associations work are given below; alternatively the user
 * may simply follow the simple usage patterns suggested here.
 * </p>
 * <p>
 * A new category, or a profile, must be inserted in this object relative to some existing object (except the first
 * object added, naturally): better than, or worst than, a profile or a category. There may already exist an object
 * (i.e. a category or a profile) at that position, in which case that object is replaced by the new one. When a profile
 * is inserted in this object, it is associated to the categories immediately down and up to it, if they exist. When a
 * category is inserted in this object, possibly existing profiles down or up to it are associated to it. It is also
 * possible to insert a category with its associated profiles, which is simply a shortcut, thus has the same effect, as
 * inserting the simple category (without profiles) then its down and up profiles, or equivalently, inserting first the
 * profiles then the category.
 * <p>
 * To avoid the possible complexity involved with making sure that objects are inserted in the right place, some simple
 * usage patterns may be followed.
 * </p>
 * <ul>
 * <li>Simple ways to populate this object are the following.
 * <ul>
 * <li>Add the categories one by one (without any associated profiles), from worst to best, preferably through the
 * {@link #addCategory(String)} method as it makes clear that no profiles are added as a side effect, then add a number
 * of profiles equal to the number of categories minus one, one by one, from worst to best.</li>
 * <li>Conversely, add the profiles then the categories.</li>
 * <li>Add the categories with their associated profiles through the use of {@link #addCategory(Category)}.</li>
 * </ul>
 * <li>Simple ways to read the content of this object are the following.</li>
 * <ul>
 * <li>Read the list of categories through the categories view. The associated profiles can be accessed through the same
 * view, with caution because the same profile may be read twice, once as the up profile of a category, and once as the
 * down profile of the next category.</li>
 * <li>Read the list of profiles through the profiles view. This permits to ensure that each profile is read exactly
 * once.</li>
 * </ul>
 * </ul><p>Note that if this object is not complete (see below), there is no guarantee that the categories have associated
 * profiles.
 * </p>
 * <p>
 * This object is said to be complete iff at least one category has been set and all categories have both their profiles
 * set, except the worst one which has no down profile, and the best one which has no up profile. This implies that the
 * number of profiles set equals the number of categories minus one. Note that completeness in that sense does not imply
 * that no categories will be added any more to this object.
 * </p>
 * <p>
 * Note that no order is defined on categories, or profiles, not contained in this object: there is no total ordering of
 * every possible categories, or profiles, only a total ordering defined on the categories, or profiles, contained in
 * this object. This implies some caution when using the ordered interfaces this object proposes. E.g., suppose that
 * using the categories view, the first one is asked, yielding some category c1. If then this object is modified, c1
 * might cease to exist in this object. That fact is reflected in the provided view. If then the next element (next to
 * c1) is asked through that view, unpredictable behavior occurs. To avoid that, the caller should always iterate from
 * some element he is sure is in this object at the time he iterates.
 * </p>
 * <p>
 * Two such objects are equal iff they have the same categories and profiles in the same order.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface CatsAndProfs extends Iterable<CatOrProf> {
    /**
     * Retrieves an unmodifiable view of the categories currently set from worst to best, with their profiles set when
     * the profiles have been set in this object.</p>
     * <p>
     * Note that this does not necessarily permit to access all profiles, as some of them may not correspond to any
     * category (this happens if there is at least as much profiles than categories).
     * </p>
     * <p>
     * If this object is complete, the worst category has no down profile but its up profile is set, the best category
     * has no up profile but has a down profile, and all other categories have both their profiles set. If this object
     * is not complete, none of these guarantees hold.
     * </p>
     * 
     * @return not {@code null}.
     */
    public NavigableSet<Category> getCategories();

    /**
     * Adds all the objects from the given source to the end of this object. This object may not already contain
     * categories and profiles from the given source, the source must not contain duplicates categories or profiles.
     * 
     * @param source
     *            not {@code null}.
     */
    public void addAll(Iterable<CatOrProf> source);

    /**
     * Retrieves the categories currently set from best to worst, with their profiles set when the profiles have been
     * set in this object, as an immutable set. NB the definition of what constitutes an "up" and a "down" profile does
     * not change according to the direction in which the set of categories is considered, i.e. if the worst category
     * has p1 as an up profile when considering the set of categories from worst to best, it still has an up profile of
     * p1 in the result given by this method (the difference being that the worst category is reached last when using
     * this method).
     * 
     * @return not {@code null}.
     */
    public NavigableSet<Category> getCategoriesFromBest();

    /**
     * Retrieves the up profile corresponding to the category whose name is given, if it exists.
     * 
     * @param categoryName
     *            not {@code null}.
     * @return {@code null} iff the category name corresponds to no category in this object, or the given category
     *         has no up profile.
     */
    public Alternative getProfileUp(String categoryName);

    /**
     * Retrieves the down profile corresponding to the category whose name is given, if it exists.
     * 
     * @param categoryName
     *            not {@code null}.
     * @return {@code null} iff the category name corresponds to no category in this object, or the given category
     *         has no down profile.
     */
    public Alternative getProfileDown(String categoryName);

    /**
     * Retrieves the category corresponding to the given name, if it exists. Its profiles are set.
     * 
     * @param categoryName
     *            not {@code null}.
     * @return a category, or {@code null} if no such category were found.
     */
    public Category getCategory(String categoryName);

    /**
     * Retrieves the category having the given profile as up profile, if it exists.
     * 
     * @param profile
     *            not {@code null}.
     * @return {@code null} if the given profile is not in this object, or has no bound down category (which does
     *         not imply that it is not assigned yet as it may have an up category).
     */
    public Category getCategoryDown(Alternative profile);

    /**
     * Retrieves the category having the given profile as down profile, if it exists.
     * 
     * @param profile
     *            not {@code null}.
     * @return the category, or {@code null} iff the profile is not contained in this object or has no immediate
     *         upper category.
     */
    public Category getCategoryUp(Alternative profile);

    /**
     * @return {@code true} iff this object is complete as defined in {@link CatsAndProfs}.
     */
    public boolean isComplete();

    /**
     * Replaces the given old category. The old category must be in this object. Usual rules for association of existing
     * and new profiles apply.
     * 
     * @param oldName
     *            not {@code null}, must exist in this object. The name of the category to replace.
     * @param newCategory
     *            not {@code null}, must not exist in this object.
     */
    public void setCategory(String oldName, Category newCategory);

    /**
     * Adds a category having the given name as the new best one. If the current best category has an up profile, that
     * profile is also set as the down profile of the new category. If there is, moreover, at least one supplementary
     * profile previously set but not associated yet, the worst of these orphan profiles is set as the new category up
     * profile. If there is no category in this object yet, only the up profile of the new category is possibly set.
     * 
     * @param name
     *            not {@code null}, must not be the name of some already existing category in this object.
     * @return the category that has just been added, with possible supplementary profiles set.
     */
    public Category addCategory(String name);

    /**
     * Adds the given profile as the new best profile. The given profile is set as the up profile of a category that has
     * no up profile set if a category is available at the right position, i.e. the category which has the best profile
     * as down profile or the worst category if this object contains no profiles yet, and also set as the down profile
     * of the next category if it exists (e.g. this happens if this object contains two categories and no profiles at
     * the time of this method call). If all categories set in this object already have an associated up profile, the
     * given profile is only added to the set of profiles and will be associated to a category added later. No profiles
     * are replaced when using this method.
     * 
     * @param profile
     *            not {@code null}.
     */
    public void addProfile(Alternative profile);

    /**
     * Sets or replaces the up profile corresponding to the category whose name is given (and hence, also sets or
     * replaces the down profile of the next category, if it exists).
     * 
     * @param categoryName
     *            not {@code null}, must exist in this object.
     * @param profile
     *            not {@code null}.
     * @return {@code true} iff the call changed the state of this object.
     */
    public boolean setProfileUp(String categoryName, Alternative profile);

    /**
     * Removes the given profile from this object, if it exists.
     * 
     * @param profile
     *            not {@code null}, the profile to remove.
     * @return {@code true} iff the call changed the state of this object, thus iff the profile has been removed,
     *         thus iff it existed.
     */
    public boolean removeProfile(Alternative profile);

    /**
     * Removes the given category from this object, if it exists. The profiles possibly bound to that category are
     * <em>not</em> removed from this object.
     * 
     * @param id
     *            not {@code null}, the id of the category to remove.
     * @return {@code true} iff the call changed the state of this object, thus iff the category has been removed,
     *         thus iff it existed.
     */
    public boolean removeCategory(String id);

    /**
     * TODO mandate that the profile is new (thus does not exist in this object). Hence, no return value is needed.
     * Otherwize risk to have twice the same profile at different positions! Sets or replaces the down profile
     * corresponding to the category whose name is given (and hence, also sets or replaces the up profile of the
     * previous category, if it exists).
     * 
     * @param categoryName
     *            not {@code null}, must exist in this object.
     * @param profile
     *            not {@code null}.
     * @return {@code true} iff the call changed the state of this object.
     */
    public boolean setProfileDown(String categoryName, Alternative profile);

    /**
     * Sets or replaces the category up to the given profile (i.e. the one for which the given profile is the down
     * profile). If the given category has associated down or up profiles, they are used to replace corresponding ones
     * in this object, and if the given category has no down or up profile, existing ones in this object are associated
     * to the new category. That means that it is possible to replace at the same time the category up to the given
     * profile <em>and</em> the profile itself, by assigning a different down profile to the given category.
     * 
     * @param profile
     *            not {@code null}, must exist in this object.
     * @param newCategory
     *            not {@code null}, must not exist in this object.
     */
    public void setCategoryUp(Alternative profile, Category newCategory);

    /**
     * Sets or replaces the category down to the given profile (i.e. the one whose given profile is the up profile). If
     * the given category has associated down or up profiles, they are used to replace corresponding ones in this
     * object, and if the given category has no down or up profile, existing ones in this object are associated to the
     * new category. That means that it is possible to replace at the same time the category down to the given profile
     * <em>and</em> the profile itself, by assigning a different up profile to the given category.
     * 
     * @param profile
     *            not {@code null}, must exist in this object.
     * @param newCategory
     *            not {@code null}, must not exist in this object.
     */
    public void setCategoryDown(Alternative profile, Category newCategory);

    /**
     * Retrieves an unmodifiable view of the profiles currently set from worst to best.
     * 
     * @return not {@code null}.
     */
    public NavigableSet<Alternative> getProfiles();

    /**
     * Adds the given category as the new best one. The down and up profiles possibly associated with the given category
     * are also set. Thus the up profile possibly associated with the current best category is replaced by the down
     * profile of the given category, if it has a down profile. If the given category has no down profile, the previous
     * profile is <em>not</em> removed (explicit profile removal should be asked to remove a profile). The worst of the
     * remembered profiles not yet associated (if any) will be erased by the up profile of the given category, if it has
     * one. Conversely, if the given category has no down or up profile, its down or up profile is set to be the
     * corresponding one already present in this object, if any. If this object hosts no category yet, no existing
     * profile in this object corresponds to the new category's down profile (thus if it comes with a down profile, that
     * profile is added to this object as a new worst profile), and the current worst profile in this object corresponds
     * to the new category's up profile.
     * 
     * @param category
     *            not {@code null}. If its down or up profile is set, they will be set as well in this object.
     * @return the category that has just been set. This is the given category, possibly enriched with a down and up
     *         profiles as these could have been previously set in this object.
     */
    public Category addCategory(Category category);

    /**
     * @return {@code true} iff this object contains no categories and no profiles.
     */
    public boolean isEmpty();

    public boolean clear();

    /**
     * Observes the addition of profiles. The given observer is called with {@link IObserver#update} when a profile is
     * added, the object being the added profile. For optimisation reasons, this object may choose to call the update
     * method with a {@code null} argument when several profiles have been added at the same time (or sometimes
     * even when only one profile has been added). Therefore, the argument should be used only for optimization.
     * 
     * @param observer
     *            not {@code null}.
     */
    public void addObserverAddedProfile(IObserver<Alternative> observer);

    public void addObserverRemovedProfile(IObserver<Alternative> observer);
}
