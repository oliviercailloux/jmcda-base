package org.decision_deck.jmcda.structure;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

/**
 * <p>
 * An alternative, or action, decision object, option, choice proposal, etc. Has a unique id (not <code>null</code>).
 * </p>
 * <p>
 * Objects of this type are immutable.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class Alternative implements Comparable<Alternative> {
    @Override
    public int compareTo(Alternative a2) {
	return m_id.compareTo(a2.m_id);
    }

    /**
     * Never <code>null</code>.
     */
    private final String m_id;

    /**
     * Builds an alternative with an integer id (which will also be available as string).
     * 
     * @param id
     *            the id as an integer.
     */
    public Alternative(int id) {
	m_id = String.valueOf(id);
    }

    /**
     * Builds an alternative (which is, by default, not fictitious).
     * 
     * @param id
     *            not <code>null</code>.
     */
    public Alternative(String id) {
	checkNotNull(id);
	m_id = id;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Alternative other = (Alternative) obj;
	if (!m_id.equals(other.m_id)) {
	    return false;
	}
	return true;
    }

    public String getId() {
	return m_id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
	return result;
    }

    @Override
    public String toString() {
	final StringBuffer str = new StringBuffer("Alternative");
	str.append(" [");
	str.append(m_id);
	str.append("]");
	return str.toString();
    }

    /**
     * <p>
     * Retrieves a function which gives the id of the given alternative. No <code>null</code> values are accepted.
     * </p>
     * <p>
     * This provides an easy way to get short debug strings. E.g. to get a string representing the contents of a set of
     * alternatives <em>s</em>, use <code>Joiner.on(", ").join(Iterables.transform(s, getIdFct()))</code>.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    static public Function<Alternative, String> getIdFct() {
	final Function<Alternative, String> alternativeNamer = new Function<Alternative, String>() {
	    @Override
	    public String apply(Alternative input) {
		return input.getId();
	    }
	};
	return alternativeNamer;
    }
}
