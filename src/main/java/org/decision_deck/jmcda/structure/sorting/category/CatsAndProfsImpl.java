package org.decision_deck.jmcda.structure.sorting.category;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.IObserver;
import org.decision_deck.utils.ObservableTyped;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class CatsAndProfsImpl implements CatsAndProfs {
	private final class IteratorImpl implements Iterator<CatOrProf> {
		/**
		 * <code>null</code> for not yet iterated.
		 */
		private CatOrProf m_current;
		/**
		 * <code>null</code> for not computed yet or no next (end of iterator).
		 */
		private CatOrProf m_next;

		public IteratorImpl() {
			m_current = null;
			m_next = null;
		}

		@Override
		public boolean hasNext() {
			if (m_next == null) {
				m_next = computeNext();
			}
			return m_next != null;
		}

		@Override
		public CatOrProf next() {
			final CatOrProf nextObject = computeNext();
			if (nextObject == null) {
				throw new NoSuchElementException();
			}
			m_current = nextObject;
			m_next = null;
			return m_current;
		}

		@Override
		public void remove() {
			final CatsAndProfs delegate = CatsAndProfsImpl.this;
			if (m_current.hasProfile()) {
				final Alternative currentProfile = m_current.getProfile();
				final boolean removed = delegate.removeProfile(currentProfile);
				assert(removed);
			}
			if (m_current.hasCategory()) {
				final Category currentCategory = m_current.getCategory();
				final boolean removed = delegate.removeCategory(currentCategory.getId());
				assert(removed);
			}
		}

		private CatOrProf computeNext() {
			if (m_current == null) {
				return firstObject(CatsAndProfsImpl.this);
			}
			return nextObject(CatsAndProfsImpl.this, m_current);
		}
	}

	static private CatOrProf firstObject(CatsAndProfs catsAndProfs) {
		if (catsAndProfs.isEmpty()) {
			return null;
		}
		final CatOrProf current;
		if (catsAndProfs.getCategories().isEmpty() || catsAndProfs.getCategories().first().getProfileDown() != null) {
			current = new CatOrProf(catsAndProfs.getProfiles().first());
		} else {
			current = new CatOrProf(catsAndProfs.getCategories().first());
		}
		return current;
	}

	static private CatOrProf nextObject(CatsAndProfs catsAndProfs, CatOrProf object) {
		if (object.hasCategory()) {
			final Category category = object.getCategory();
			assert(catsAndProfs.getCategories().contains(category));
			final Alternative profileUp = catsAndProfs.getProfileUp(category.getId());
			if (profileUp != null) {
				return new CatOrProf(profileUp);
			}
			final Category higher = catsAndProfs.getCategories().higher(category);
			return higher == null ? null : new CatOrProf(higher);
		} else if (object.hasProfile()) {
			final Alternative profile = object.getProfile();
			assert(catsAndProfs.getProfiles().contains(profile));
			final Category categoryUp = catsAndProfs.getCategoryUp(profile);
			if (categoryUp != null) {
				return new CatOrProf(categoryUp);
			}
			final Alternative higher = catsAndProfs.getProfiles().higher(profile);
			return higher == null ? null : new CatOrProf(higher);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Required for the view of all profiles. Or we could use the map profiles
	 * to categories, transformed to {@link NavigableMap}, but we currently lack
	 * an implementation of that.
	 */
	private final ExtentionalTotalOrder<Alternative> m_allProfiles;

	private final ExtentionalTotalOrder<Category> m_categories;

	private final ObservableTyped<Alternative> m_observableAddedProfile = new ObservableTyped<Alternative>();

	private final ObservableTyped<Alternative> m_observableRemovedProfile = new ObservableTyped<Alternative>();

	/**
	 * <p>
	 * Permits to find back the right category.
	 * </p>
	 * <p>
	 * The profiles of the categories in the set of values are not maintained
	 * (not needed).
	 * </p>
	 * <p>
	 * Contains a <code>null</code> value for a given <code>profile</code> key
	 * iff no down category is associated with that profile, i.e., when no
	 * category has <code>profile</code> as an up profile. The set of profiles
	 * contains all profiles, even those not yet assigned to any category which
	 * have a <code>null</code> value.
	 * </p>
	 * <p>
	 * Note that some categories could have no up profile, thus the set of
	 * values does not necessarily contain all categories.
	 * </p>
	 */
	private final Map<Alternative, Category> m_profilesToDownCategories = new HashMap<Alternative, Category>();

	CatsAndProfsImpl() {
		m_categories = ExtentionalTotalOrder.create();
		m_allProfiles = ExtentionalTotalOrder.create();
	}

	CatsAndProfsImpl(CatsAndProfs source) {
		this();
		addAll(source);
	}

	CatsAndProfsImpl(Set<Category> toCopy) {
		this();
		for (Category givenCat : toCopy) {
			addCategory(givenCat);
		}
	}

	@Override
	public void addAll(Iterable<CatOrProf> source) {
		checkNotNull(source);
		for (CatOrProf object : source) {
			if (object.hasCategory()) {
				final Category newCategory = object.getCategory();
				if (getCategories().contains(newCategory)) {
					throw new IllegalArgumentException("This object already contains " + newCategory + ".");
				}
				if (getProfiles().isEmpty()) {
					addCategory(newCategory.getId());
				} else {
					final Alternative lastProfile = getProfiles().last();
					final Category up = getCategoryUp(lastProfile);
					if (up == null) {
						setCategoryUp(lastProfile, new Category(newCategory.getId()));
					} else {
						addCategory(newCategory.getId());
					}
				}
			} else if (object.hasProfile()) {
				final Alternative newProfile = object.getProfile();
				if (getProfiles().contains(newProfile)) {
					throw new IllegalArgumentException("This object already contains " + newProfile + ".");
				}
				if (getCategories().isEmpty()) {
					addProfile(newProfile);
				} else {
					final Category last = getCategories().last();
					if (getProfileUp(last.getId()) == null) {
						setProfileUp(last.getId(), newProfile);
					} else {
						addProfile(newProfile);
					}
				}
			} else {
				throw new IllegalStateException("Unexpected " + object + ".");
			}
		}
	}

	@Override
	public Category addCategory(Category category) {
		final Category previousCat = m_categories.isEmpty() ? null : m_categories.last();
		final Category preceedingCat = previousCat;
		final Category nextCat = null;
		final Category followingCat = null;
		final Alternative previousProfile = previousCat == null ? null : previousCat.getProfileUp();
		final Alternative nextProfile = getFreeProfile();
		final Alternative precedingProfile = getPrecedingProfile(previousCat);
		final Alternative followingProfile = nextProfile;
		final Category currentCat = null;
		return set(category, currentCat, previousProfile, precedingProfile, previousCat, preceedingCat, nextProfile,
				followingProfile, nextCat, followingCat);
	}

	@Override
	public Category addCategory(String name) {
		final Category category = new Category(name);
		return addCategory(category);
	}

	@Override
	public void addObserverAddedProfile(IObserver<Alternative> observer) {
		Preconditions.checkNotNull(observer);
		m_observableAddedProfile.addObserver(observer);
	}

	@Override
	public void addObserverRemovedProfile(IObserver<Alternative> observer) {
		Preconditions.checkNotNull(observer);
		m_observableRemovedProfile.addObserver(observer);
	}

	@Override
	public void addProfile(Alternative profile) {
		if (profile == null) {
			throw new NullPointerException();
		}
		final Alternative previousProfile = m_allProfiles.isEmpty() ? null : m_allProfiles.last();
		final Alternative preceedingProfile = previousProfile;
		final Alternative currentProfile = null;
		final Alternative followingProfile = null;
		final Category previousCat;
		if (previousProfile == null) {
			if (m_categories.isEmpty()) {
				previousCat = null;
			} else {
				previousCat = m_categories.first();
			}
		} else {
			// final Category downPreviousCat =
			// m_profilesToDownCategories.get(previousProfile);
			// if (downPreviousCat == null) {
			// previousCat = null;
			// } else {
			// previousCat = m_categories.higher(downPreviousCat);
			// }
			previousCat = getCategoryUp(previousProfile);
		}
		final Category nextCat;
		if (previousCat == null) {
			nextCat = null;
		} else {
			nextCat = m_categories.higher(previousCat);
		}
		setProfile(profile, currentProfile, preceedingProfile, followingProfile, previousCat, nextCat);
	}

	@Override
	public boolean clear() {
		if (isEmpty()) {
			return false;
		}
		m_allProfiles.clear();
		m_categories.clear();
		m_profilesToDownCategories.clear();
		m_observableRemovedProfile.notifyObserversChanged();
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CatsAndProfs)) {
			return false;
		}

		CatsAndProfs c2 = (CatsAndProfs) obj;
		// if (!Iterables.elementsEqual(m_categories, c2.getCategories())) {
		// return false;
		// }
		// if (m_categories.isEmpty() || c2.getCategories().isEmpty()) {
		// if (!m_categories.isEmpty() || !c2.getCategories().isEmpty()) {
		// return false;
		// }
		// return Iterables.elementsEqual(getProfiles(), c2.getProfiles());
		// }
		//
		// final Alternative lastProfileBeforeCategories1 =
		// m_categories.first().getProfileDown();
		// final Alternative lastProfileBeforeCategories2 =
		// c2.getCategories().first().getProfileDown();
		// if ((lastProfileBeforeCategories1 == null) !=
		// (lastProfileBeforeCategories2 == null)) {
		// return false;
		// }
		// if (lastProfileBeforeCategories1 != null) {
		// final NavigableSet<Alternative> profilesBefore1 =
		// m_allProfiles.headSet(lastProfileBeforeCategories1, true);
		// final NavigableSet<Alternative> profilesBefore2 =
		// c2.getProfiles().headSet(lastProfileBeforeCategories2,
		// true);
		// if (!Iterables.elementsEqual(profilesBefore1, profilesBefore2)) {
		// return false;
		// }
		// }
		//
		// final Alternative firstProfileAfterCategories1 =
		// m_categories.last().getProfileUp();
		// final Alternative firstProfileAfterCategories2 =
		// c2.getCategories().last().getProfileUp();
		// if ((firstProfileAfterCategories1 == null) !=
		// (firstProfileAfterCategories2 == null)) {
		// return false;
		// }
		// if (firstProfileAfterCategories1 != null) {
		// final NavigableSet<Alternative> profilesAfter1 =
		// m_allProfiles.tailSet(firstProfileAfterCategories1, true);
		// final NavigableSet<Alternative> profilesAfter2 =
		// c2.getProfiles().tailSet(firstProfileAfterCategories2,
		// true);
		// if (!Iterables.elementsEqual(profilesAfter1, profilesAfter2)) {
		// return false;
		// }
		// }
		// return true;
		return Iterables.elementsEqual(this, c2);
	}

	@Override
	public NavigableSet<Category> getCategories() {
		return Sets.unmodifiableNavigableSet(m_categories);
	}

	@Override
	public NavigableSet<Category> getCategoriesFromBest() {
		return Sets.unmodifiableNavigableSet(m_categories.descendingSet());
	}

	@Override
	public Category getCategory(String categoryName) {
		final Category notUpToDateCat = new Category(categoryName);
		if (!m_categories.contains(notUpToDateCat)) {
			return null;
		}
		return m_categories.floor(notUpToDateCat);
	}

	@Override
	public Category getCategoryDown(Alternative profile) {
		final Category notUpToDateCat = m_profilesToDownCategories.get(profile);
		if (notUpToDateCat == null) {
			return null;
		}
		return m_categories.ceiling(notUpToDateCat);
	}

	@Override
	public Category getCategoryUp(Alternative profile) {
		if (!m_profilesToDownCategories.containsKey(profile)) {
			return null;
		}

		final Category downCat = m_profilesToDownCategories.get(profile);
		if (downCat != null) {
			final Category candidate = m_categories.higher(downCat);
			if (candidate != null && profile.equals(candidate.getProfileDown())) {
				return candidate;
			}
			/**
			 * Then we have that the candidate found is not the immediate
			 * following category, hence there is no matching category.
			 */
			return null;
		}

		if (m_categories.isEmpty()) {
			return null;
		}

		/**
		 * Note that the next profile could be not immediately following, hence
		 * the category found is not necessarily the right one.
		 */
		final Alternative higher = m_allProfiles.higher(profile);
		Category startCat;
		if (higher != null) {
			final Category notUpToDateCat = m_profilesToDownCategories.get(higher);
			startCat = notUpToDateCat == null ? m_categories.last() : m_categories.ceiling(notUpToDateCat);
		} else {
			startCat = m_categories.last();
		}

		/**
		 * In some pathological situations, a series of profiles (without down
		 * categories except possibly the first profile of the series) could be
		 * followed by a series of categories (without up profiles except
		 * possibly the last one of the series). This should not happen often,
		 * but in that case it is difficult to find the right category. For
		 * better performances we would need pointers to upper categories, not
		 * only down ones.
		 */
		for (Category cat = startCat; cat != null; cat = m_categories.lower(cat)) {
			if (profile.equals(cat.getProfileDown())) {
				return cat;
			}
		}

		return null;
	}

	@Override
	public Alternative getProfileDown(String categoryName) {
		final Category category = getCategory(categoryName);
		if (category == null) {
			return null;
		}
		return category.getProfileDown();
	}

	@Override
	public NavigableSet<Alternative> getProfiles() {
		return Sets.unmodifiableNavigableSet(m_allProfiles);
	}

	@Override
	public Alternative getProfileUp(String categoryName) {
		final Category category = getCategory(categoryName);
		if (category == null) {
			return null;
		}
		return m_categories.floor(category).getProfileUp();
	}

	@Override
	public int hashCode() {
		return 71 + m_categories.hashCode() + m_allProfiles.hashCode();
	}

	@Override
	public boolean isComplete() {
		if (m_categories.isEmpty()) {
			return false;
		}
		if (m_categories.first().getProfileDown() != null) {
			return false;
		}
		if (m_categories.last().getProfileUp() != null) {
			return false;
		}
		if (m_allProfiles.size() != m_categories.size() - 1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return m_categories.isEmpty() && m_allProfiles.isEmpty();
	}

	@Override
	public Iterator<CatOrProf> iterator() {
		return new IteratorImpl();
	}

	@Override
	public boolean removeCategory(String id) {
		Preconditions.checkNotNull(id);
		final Category toRemove = getCategory(id);
		if (!m_categories.contains(toRemove)) {
			return false;
		}
		final Category previous = m_categories.lower(toRemove);
		m_categories.remove(toRemove);
		final Alternative profileUp = toRemove.getProfileUp();
		if (profileUp != null) {
			if (previous != null && previous.getProfileUp() == null) {
				setProfileUp(previous.getId(), profileUp);
				m_profilesToDownCategories.put(profileUp, previous);
				final Category newPrevious = previous.newProfileUp(profileUp);
				m_categories.replace(previous, newPrevious);
			} else {
				m_profilesToDownCategories.put(profileUp, null);
			}
		}
		return true;
	}

	@Override
	public boolean removeProfile(Alternative profile) {
		Preconditions.checkNotNull(profile);

		if (!m_allProfiles.contains(profile)) {
			return false;
		}
		final Category previousCategory = getCategoryDown(profile);
		final Category nextCategory = getCategoryUp(profile);

		final Alternative preceeding = m_allProfiles.lower(profile);
		final Alternative following = m_allProfiles.higher(profile);
		final Alternative previous = previousCategory == null ? preceeding : null;
		final Alternative next = nextCategory == null ? following : null;
		removeProfileInternal(profile, previous, next, previousCategory, nextCategory);
		return true;
	}

	@Override
	public void setCategory(String oldName, Category newCategory) {
		Preconditions.checkNotNull(newCategory);
		Preconditions.checkArgument(!m_categories.contains(newCategory));
		final Category old = new Category(oldName);
		Preconditions.checkArgument(m_categories.contains(old), "Cat " + oldName + " not found.");

		final Category currentCat = m_categories.floor(old);
		final boolean changed = replaceCategory(currentCat, newCategory);
		assert changed;
	}

	@Override
	public void setCategoryDown(Alternative profile, Category newCategory) {
		Preconditions.checkNotNull(newCategory);
		Preconditions.checkArgument(!m_categories.contains(newCategory));
		Preconditions.checkArgument(m_allProfiles.contains(profile), "Unknown " + profile + ".");

		final Category currentCategory = getCategoryDown(profile);
		final Alternative precedingProfile = m_allProfiles.lower(profile);
		final Alternative previousProfile;
		if (currentCategory != null) {
			previousProfile = currentCategory.getProfileDown();
		} else {
			previousProfile = precedingProfile;
		}
		final Alternative followingProfile = profile;
		final Alternative nextProfile = profile;
		final Category precedingCat;
		final Category previousCategory;
		if (previousProfile != null) {
			final Category notUpToDatePrevCat = m_profilesToDownCategories.get(previousProfile);
			if (notUpToDatePrevCat == null) {
				previousCategory = null;
			} else {
				previousCategory = m_categories.floor(notUpToDatePrevCat);
			}
			precedingCat = getPrecedingCategory(previousProfile);
		} else if (currentCategory != null) {
			precedingCat = m_categories.lower(currentCategory);
			previousCategory = precedingCat;
		} else {
			assert(profile.equals(m_allProfiles.first()));
			previousCategory = null;
			precedingCat = null;
		}
		final Category followingCat;
		if (currentCategory != null) {
			followingCat = m_categories.higher(currentCategory);
		} else if (precedingCat != null) {
			followingCat = m_categories.higher(precedingCat);
		} else {
			followingCat = m_categories.isEmpty() ? null : m_categories.first();
		}
		final Category nextCategory;
		if (followingCat != null && profile.equals(followingCat.getProfileDown())) {
			nextCategory = followingCat;
		} else {
			nextCategory = null;
		}

		// final Category set =
		set(newCategory, currentCategory, previousProfile, precedingProfile, previousCategory, precedingCat,
				nextProfile, followingProfile, nextCategory, followingCat);

		// return !set.identicalTo(currentCategory);
	}

	@Override
	public void setCategoryUp(Alternative profile, Category newCategory) {
		Preconditions.checkNotNull(newCategory);
		Preconditions.checkArgument(!m_categories.contains(newCategory));
		Preconditions.checkArgument(m_allProfiles.contains(profile), "Unknown " + profile + ".");

		final Category previousCategory = getCategoryDown(profile);
		final Category precedingCat = getPrecedingCategory(profile);

		final Alternative precedingProfile = profile;
		final Alternative previousProfile = profile;
		final Alternative followingProfile = m_allProfiles.higher(profile);
		final Alternative nextProfile = getNextFromFollowing(followingProfile);
		final Category currentCategory = getCategoryUp(profile);
		final Category followingCat;
		if (currentCategory != null) {
			followingCat = m_categories.higher(currentCategory);
		} else if (precedingCat != null) {
			followingCat = m_categories.higher(precedingCat);
		} else {
			followingCat = m_categories.isEmpty() ? null : m_categories.first();
		}
		final Category nextCategory = getNextFromFollowing(followingCat);

		set(newCategory, currentCategory, previousProfile, precedingProfile, previousCategory, precedingCat,
				nextProfile, followingProfile, nextCategory, followingCat);
	}

	@Override
	public boolean setProfileDown(String categoryName, Alternative profile) {
		Preconditions.checkNotNull(profile);

		final Category category = getCategory(categoryName);
		if (category == null) {
			throw new IllegalArgumentException("Cat " + categoryName + " not found.");
		}

		final Category precedingCategory = m_categories.lower(category);
		final Category previousCategory;
		final Alternative currentDown = category.getProfileDown();
		if (currentDown == null) {
			previousCategory = precedingCategory;
		} else {
			previousCategory = m_profilesToDownCategories.get(currentDown);
		}
		final Alternative precedingProfile = getPrecedingProfile(category);
		final Alternative followingProfile = getFollowingProfile(category);

		return setProfile(profile, currentDown, precedingProfile, followingProfile, previousCategory, category);
	}

	@Override
	public boolean setProfileUp(String categoryName, Alternative profile) {
		Preconditions.checkNotNull(profile);

		final Category category = getCategory(categoryName);
		if (category == null) {
			throw new IllegalArgumentException("Cat " + categoryName + " not found.");
		}

		final Category followingCategory = m_categories.higher(category);
		final Alternative precedingProfile = getPrecedingProfile(category);
		final Alternative followingProfile = getFollowingProfile(category);
		// final Category nextCategory =
		// getNextFromFollowing(followingCategory);

		return setProfile(profile, category.getProfileUp(), precedingProfile, followingProfile, category,
				followingCategory);
	}

	@Override
	public String toString() {
		// final ToStringHelper helper = Objects.toStringHelper(this);
		final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		final Iterator<Category> categoriesIterator = m_categories.iterator();
		final Iterator<Alternative> profilesIterator = m_allProfiles.iterator();
		Category cat = null;
		while (categoriesIterator.hasNext()) {
			cat = categoriesIterator.next();
			if (cat.getProfileDown() != null) {
				break;
			}
			builder.append(cat.toString());
		}
		Alternative prof;
		while (categoriesIterator.hasNext() || profilesIterator.hasNext()) {
			while (profilesIterator.hasNext()) {
				prof = profilesIterator.next();
				builder.append(prof);
				if (cat != null && prof.equals(cat.getProfileDown())) {
					break;
				}
			}
			if (cat != null) {
				builder.append(cat);
			}
			while (cat != null) {
				cat = categoriesIterator.hasNext() ? categoriesIterator.next() : null;
				if (cat == null || cat.getProfileDown() != null) {
					break;
				}
				builder.append(cat.toString());
			}
		}

		return builder.toString();
	}

	/**
	 * Considers the given category and all the categories better than this one
	 * until a profile is found.
	 *
	 * @param category
	 *            not <code>null</code>, must be in this object.
	 * @return the closest profile up to the given category which is not
	 *         <code>null</code>, or <code>null</code> iff all categories better
	 *         than and including the given one have no up profile set.
	 */
	private Alternative getFollowingProfile(Category category) {
		for (Category nextCategory = category; nextCategory != null; nextCategory = m_categories.higher(nextCategory)) {
			final Alternative up = nextCategory.getProfileUp();
			if (up != null) {
				return up;
			}
		}
		return null;
	}

	/**
	 * @return the first profile not yet bound to any category, whether as up or
	 *         down profile.
	 */
	private Alternative getFreeProfile() {
		final Alternative used = getLastUsedProfile();
		final Alternative first = m_allProfiles.isEmpty() ? null : m_allProfiles.first();
		return used == null ? first : m_allProfiles.higher(used);
	}

	private Alternative getLastUsedProfile() {
		if (!m_categories.isEmpty() && m_categories.last().getProfileUp() != null) {
			return m_categories.last().getProfileUp();
		}
		for (Category category : m_categories.descendingSet()) {
			final Alternative profile = category.getProfileDown();
			if (profile != null) {
				return profile;
			}
		}
		return null;
	}

	private Alternative getNextFromFollowing(final Alternative followingProfile) {
		final Alternative nextProfile;
		if (followingProfile == null) {
			nextProfile = null;
		} else {
			/**
			 * Next profile equals following profile iff there is no hole
			 * before, i.e. iff there is a profile immediately preceding,
			 * following profile.
			 */
			final boolean hasHole;
			final Category followingDown = m_profilesToDownCategories.get(followingProfile);
			if (followingDown == null) {
				hasHole = false;
			} else if (followingDown.getProfileDown() != null) {
				hasHole = false;
			} else {
				hasHole = true;
			}
			if (hasHole) {
				nextProfile = null;
			} else {
				nextProfile = followingProfile;
			}
		}
		return nextProfile;
	}

	private Category getNextFromFollowing(final Category followingCategory) {
		final Category nextCategory;
		if (followingCategory == null) {
			nextCategory = null;
		} else {
			/**
			 * Next cat is following cat iff there is no hole before, i.e. iff
			 * there is a category immediately preceding, following cat.
			 */
			final Alternative followingDown = followingCategory.getProfileDown();
			final boolean hasHole;
			if (followingDown == null) {
				hasHole = false;
			} else if (m_profilesToDownCategories.get(followingDown) != null) {
				hasHole = false;
			} else {
				hasHole = true;
			}
			if (!hasHole) {
				nextCategory = followingCategory;
			} else {
				nextCategory = null;
			}
		}
		return nextCategory;
	}

	private Category getPrecedingCategory(Alternative profile) {
		final NavigableSet<Alternative> lowProfiles = m_allProfiles.headSet(profile, true);
		for (Alternative profileIter : lowProfiles.descendingSet()) {
			Category category = m_profilesToDownCategories.get(profileIter);
			if (category != null) {
				Category realCategory = m_categories.floor(category);
				return realCategory;
			}
		}
		return null;
	}

	/**
	 * Considers the given category and all the categories worst than this one
	 * until a profile is found.
	 *
	 * @param category
	 *            not <code>null</code>, must be in this object.
	 * @return the closest profile down to the given category which is not
	 *         <code>null</code>, or <code>null</code> iff all categories worst
	 *         than and including the given one have no down profile set.
	 */
	private Alternative getPrecedingProfile(Category category) {
		for (Category previousCategory = category; previousCategory != null; previousCategory = m_categories
				.lower(previousCategory)) {
			final Alternative down = previousCategory.getProfileDown();
			if (down != null) {
				return down;
			}
		}
		return null;
	}

	/**
	 * @param toRemove
	 *            not <code>null</code>.
	 * @param previousProfile
	 *            immediately preceeding (no category in between), may be
	 *            <code>null</code>.
	 * @param nextProfile
	 *            immediately following (no category in between), may be
	 *            <code>null</code>.
	 * @param previousCategory
	 *            has the profile to remove as up profile, may be
	 *            <code>null</code>.
	 * @param nextCategory
	 *            has the profile to remove as down profile, may be
	 *            <code>null</code>.
	 */
	private void removeProfileInternal(final Alternative toRemove, Alternative previousProfile, Alternative nextProfile,
			Category previousCategory, Category nextCategory) {
		m_allProfiles.remove(toRemove);

		/** Replace previous category - up profile with next profile. */
		final Category newPreviousCategory;
		if (previousCategory != null) {
			newPreviousCategory = previousCategory.newProfileUp(nextProfile);
			m_categories.replace(previousCategory, newPreviousCategory);
		} else {
			newPreviousCategory = previousCategory;
		}
		if (nextProfile != null) {
			/** If previouscat == null, has no effect. */
			m_profilesToDownCategories.put(nextProfile, newPreviousCategory);
		}

		/** Replace next category - down profile with previous profile. */
		if (nextCategory != null) {
			final Category newNextCategory = nextCategory.newProfileDown(previousProfile);
			m_categories.replace(nextCategory, newNextCategory);
		}

		m_profilesToDownCategories.remove(toRemove);
		m_observableRemovedProfile.notifyObserversChanged(toRemove);
	}

	/**
	 * @param currentCategory
	 *            not <code>null</code>, must exist in this object.
	 * @param newCategory
	 *            not <code>null</code>.
	 * @return <code>true</code> iff the state changed.
	 */
	private boolean replaceCategory(final Category currentCategory, Category newCategory) {
		final Alternative previousProfile = currentCategory.getProfileDown();
		final Alternative nextProfile = currentCategory.getProfileUp();
		final Category precedingCat = m_categories.lower(currentCategory);
		final Category followingCat = m_categories.higher(currentCategory);
		final Category previousCategory;
		if (previousProfile != null) {
			previousCategory = m_profilesToDownCategories.get(previousProfile);
		} else {
			previousCategory = precedingCat;
		}
		final Category nextCategory;
		if (nextProfile.equals(followingCat.getProfileDown())) {
			nextCategory = followingCat;
		} else {
			nextCategory = null;
		}
		final Alternative precedingProfile = getPrecedingProfile(currentCategory);
		final Alternative followingProfile = getFollowingProfile(currentCategory);
		final Category set = set(newCategory, currentCategory, previousProfile, precedingProfile, previousCategory,
				precedingCat, nextProfile, followingProfile, nextCategory, followingCat);

		return !currentCategory.identicalTo(set);
	}

	/**
	 *
	 *
	 * <ul>
	 * <li>If new down profile is set, if previous profile is set, replace
	 * current previous profile with new down profile, or if no current previous
	 * profile, adds new down profile between preceding profile and following
	 * profile.</li>
	 * <li>If new down profile is set, replace previous category - up profile
	 * with new down profile.</li>
	 * <li>Up profile: the same.</li>
	 * <li>If no new down profile is set, new category down profile is set to
	 * the previous profile.</li>
	 * <li>Up profile: the same.</li>
	 * <li>Adds new category (or replace current one) between preceding and
	 * following category.</li>
	 * </ul>
	 *
	 * @param newCategory
	 *            not <code>null</code>, its name does not already exist in this
	 *            object.
	 * @param currentCategory
	 *            <code>null</code> iff the new category does not replace any
	 *            other one but is an addition (in which case it must be at an
	 *            end, i.e. at least one of preceding or following category must
	 *            be <code>null</code>).
	 * @param previousProfile
	 *            <code>null</code> iff no immediate previous profile; not equal
	 *            to nextProfile.
	 * @param precedingProfile
	 *            Preceding, following means possibly not immediate, i.e. first
	 *            non null, preceding, following ( <code>null</code> iff new is
	 *            the first, last).
	 * @param previousCategory
	 *            Previous, next means immediate previous, next (
	 *            <code>null</code> iff no immediate previous or next).
	 * @param precedingCategory
	 *            equals previousCategory if previousCategory is not
	 *            <code>null</code>.
	 * @param nextProfile
	 *            Previous, next means immediate previous, next (
	 *            <code>null</code> iff no immediate previous or next).
	 * @param followingProfile
	 *            Preceding, following means possibly not immediate, i.e. first
	 *            non null, preceding, following ( <code>null</code> iff new is
	 *            the first, last).
	 * @param nextCategory
	 *            Previous, next means immediate previous, next (
	 *            <code>null</code> iff no immediate previous or next).
	 * @param followingCategory
	 *            equals nextCategory if nextCategory is not <code>null</code>.
	 * @return the new category just added. Not <code>null</code>.
	 */
	private Category set(Category newCategory, Category currentCategory, Alternative previousProfile,
			Alternative precedingProfile, Category previousCategory, final Category precedingCategory,
			Alternative nextProfile, Alternative followingProfile, Category nextCategory,
			final Category followingCategory) {
		final boolean catReplaceEqual = currentCategory != null && newCategory.equals(currentCategory);
		if (!catReplaceEqual && m_categories.contains(newCategory)) {
			throw new IllegalArgumentException("Already exists: " + newCategory + ".");
		}

		final Alternative givenDown = newCategory.getProfileDown();
		final Alternative givenUp = newCategory.getProfileUp();

		final Alternative newDown;
		if (givenDown != null) {
			newDown = givenDown;
		} else {
			newDown = previousProfile;
		}
		final Alternative newUp;
		if (givenUp != null) {
			newUp = givenUp;
		} else {
			newUp = nextProfile;
		}
		final Category newCat = new Category(newCategory.getId(), newDown, newUp);
		if (newUp != null) {
			m_profilesToDownCategories.put(newUp, newCat);
		}

		/** Add new cat. */
		final boolean equalCat = currentCategory != null && currentCategory.equals(newCat);
		if (!equalCat) {
			if (currentCategory != null) {
				m_categories.replace(currentCategory, newCat);
			} else if (precedingCategory == null) {
				m_categories.addAsLowest(newCat);
			} else if (followingCategory == null) {
				/** This could probably be merged with next case. */
				m_categories.addAsHighest(newCat);
			} else {
				m_categories.addAfter(precedingCategory, newCat);
			}
		}

		if (givenDown != null) {
			setProfile(givenDown, previousProfile, precedingProfile, followingProfile, previousCategory, newCat);
		}

		final Alternative newPrecedingProfile;
		if (givenDown != null) {
			newPrecedingProfile = givenDown;
		} else {
			newPrecedingProfile = precedingProfile;
		}

		/**
		 * The preceding category may have changed profile and hence be not up
		 * to date any more, but we do not care because the matching is done
		 * only on the name. Same for the following category.
		 */
		// final ImmutableCategory newPrecedingCat;
		// if ((insertedDown) && (previousCategory != null)) {
		// newPrecedingCat = newPreviousCategory;
		// } else {
		// newPrecedingCat = precedingCategory;
		// }
		// final ImmutableCategory newFollowingCat;
		// if (insertedUp && nextCategory != null) {
		// newFollowingCat = newNextCategory;
		// } else {
		// newFollowingCat = followingCategory;
		// }

		if (givenUp != null) {
			setProfile(givenUp, nextProfile, newPrecedingProfile, followingProfile, newCat, nextCategory);
		}

		return newCat;
	}

	/**
	 * @param newProfile
	 *            not <code>null</code>.
	 * @param currentProfile
	 *            may be <code>null</code>.
	 * @param precedingProfile
	 *            used only when currentProfile is <code>null</code>.
	 * @param followingProfile
	 *            used only when currentProfile is <code>null</code>.
	 * @param previousCategory
	 *            may be <code>null</code>.
	 * @param nextCategory
	 *            may be <code>null</code>.
	 * @return <code>true</code> iff state changed.
	 */
	private boolean setProfile(final Alternative newProfile, final Alternative currentProfile,
			Alternative precedingProfile, Alternative followingProfile, Category previousCategory,
			Category nextCategory) {
		Preconditions.checkNotNull(newProfile);
		final boolean replaceEqual = newProfile.equals(currentProfile);
		if (replaceEqual) {
			return false;
		}

		if (m_allProfiles.contains(newProfile)) {
			throw new IllegalArgumentException("Already exists " + newProfile + ".");
		}
		if (currentProfile != null) {
			m_allProfiles.replace(currentProfile, newProfile);
			m_observableRemovedProfile.notifyObserversChanged(currentProfile);
		} else {
			if (precedingProfile == null) {
				m_allProfiles.addAsLowest(newProfile);
			} else if (followingProfile == null) {
				/** This could probably be merged with next case. */
				m_allProfiles.addAsHighest(newProfile);
			} else {
				assert!precedingProfile.equals(followingProfile);
				m_allProfiles.addAfter(precedingProfile, newProfile);
			}
		}

		/** Replace previous category - up profile with new profile. */
		final Category newPreviousCategory;
		if (previousCategory != null && (!newProfile.equals(previousCategory.getProfileUp()))) {
			newPreviousCategory = previousCategory.newProfileUp(newProfile);
			m_categories.replace(previousCategory, newPreviousCategory);
		} else {
			newPreviousCategory = previousCategory;
		}

		/** Replace next category - down profile with new profile. */
		if (nextCategory != null && (!newProfile.equals(nextCategory.getProfileDown()))) {
			final Category newNextCategory = nextCategory.newProfileDown(newProfile);
			m_categories.replace(nextCategory, newNextCategory);
		}

		if (currentProfile != null) {
			m_profilesToDownCategories.remove(currentProfile);
		}
		m_profilesToDownCategories.put(newProfile, newPreviousCategory);
		m_observableAddedProfile.notifyObserversChanged(newProfile);
		return true;
	}

}
