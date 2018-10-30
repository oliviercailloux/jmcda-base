package org.decision_deck.jmcda.structure.sorting.category;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Contains exactly one category or (exclusive or) one profile. Immutable.
 * 
 * @author Olivier Cailloux
 * 
 */
public class CatOrProf {
    private final Category m_category;

    public Category getCategory() {
	checkState(m_category != null);
	return m_category;
    }

    public boolean hasCategory() {
	return m_category != null;
    }

    public boolean hasProfile() {
	return m_profile != null;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof CatOrProf)) {
	    return false;
	}
	final CatOrProf c2 = (CatOrProf) obj;
	return Objects.equal(m_category, c2.m_category) && Objects.equal(m_profile, c2.m_profile);
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(m_category, m_profile);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(hasProfile() ? getProfile() : getCategory());
	return helper.toString();
    }

    public Alternative getProfile() {
	checkState(m_profile != null);
	return m_profile;
    }

    /**
     * @param category
     *            not <code>null</code>.
     */
    public CatOrProf(Category category) {
	checkNotNull(category);
	m_category = category;
	m_profile = null;
    }

    /**
     * @param profile
     *            not <code>null</code>.
     */
    public CatOrProf(Alternative profile) {
	checkNotNull(profile);
	m_profile = profile;
	m_category = null;
    }

    private final Alternative m_profile;
}
