package org.decision_deck.jmcda.structure.sorting.category;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A category is immutable and has a string identifier. It also possibly has a down profile and a upper profile (which
 * might be removed in the future, because that's an odd design). Equality is based on the id only, not on the profiles.
 * The id is sometimes referred to as the category name, for legacy reasons.
 * 
 * @author Olivier Cailloux
 * 
 */
public class Category {
    private String m_id;
    private Alternative m_profileDown;
    private Alternative m_profileUp;

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Category)) {
	    return false;
	}
	return Categories.getEquivalence().equivalent(this, (Category) obj);
    }

    @Override
    public int hashCode() {
	return Categories.getEquivalence().hash(this);
    }

    @Override
    public String toString() {
	final ToStringHelper stringHelper = Objects.toStringHelper(this);
	stringHelper.addValue(getId());
	return stringHelper.toString();
    }

    public Category(Category base) {
	if (base == null) {
	    throw new NullPointerException();
	}
	m_id = base.getId();
	m_profileDown = base.getProfileDown();
	m_profileUp = base.getProfileUp();
    }

    /**
     * @param id
     *            not {@code null}, not empty.
     * @param profileDown
     *            may be {@code null}.
     * @param profileUp
     *            may be {@code null}.
     */
    public Category(String id, Alternative profileDown, Alternative profileUp) {
	if (id == null || id.isEmpty()) {
	    throw new IllegalArgumentException("Should have a name.");
	}
	m_id = id;
	m_profileDown = profileDown;
	m_profileUp = profileUp;
    }

    /**
     * Creates a category with no profiles down or up.
     * 
     * @param id
     *            not {@code null}, not empty.
     */
    public Category(String id) {
	if (id == null || id.isEmpty()) {
	    throw new IllegalArgumentException("Should have a name.");
	}
	m_id = id;
	m_profileDown = null;
	m_profileUp = null;
    }

    public String getId() {
	return m_id;
    }

    public Alternative getProfileDown() {
	return m_profileDown;
    }

    public Alternative getProfileUp() {
	return m_profileUp;
    }

    /**
     * Creates a new category containing the same informations as this one except a different down profile.
     * 
     * @param profileDown
     *            {@code null} for no down profile.
     * @return a new category.
     */
    public Category newProfileDown(Alternative profileDown) {
	return new Category(m_id, profileDown, m_profileUp);
    }

    /**
     * Creates a new category containing the same informations as this one except a different up profile.
     * 
     * @param profileUp
     *            {@code null} for no up profile.
     * @return a new category.
     */
    public Category newProfileUp(Alternative profileUp) {
	return new Category(m_id, m_profileDown, profileUp);
    }

    /**
     * Creates a new category containing the same informations as this one except a new id.
     * 
     * @param id
     *            not {@code null}, not empty (after trimming).
     * @return a new category.
     */
    public Category newId(String id) {
	return new Category(id, m_profileDown, m_profileUp);
    }

    /**
     * @param c2
     *            may be {@code null}.
     * @return {@code true} iff the given category is equal to this object and has the same profiles (down and up).
     */
    public boolean identicalTo(Category c2) {
	if (this == c2) {
	    return true;
	}
	if (!equals(c2)) {
	    return false;
	}
	if (!Objects.equal(getProfileDown(), c2.getProfileDown())) {
	    return false;
	}
	if (!Objects.equal(getProfileUp(), c2.getProfileUp())) {
	    return false;
	}
	return true;
    }

}
