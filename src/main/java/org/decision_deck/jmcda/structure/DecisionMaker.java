package org.decision_deck.jmcda.structure;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * <p>
 * A decision maker object. Has a unique id (not <code>null</code>).
 * </p>
 * <p>
 * Objects of this type are immutable.
 * </p>
 * <p>
 * A decision maker {@link #equals(Object)} an other one iff they have the same id.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class DecisionMaker implements Comparable<DecisionMaker> {
    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(m_id);
	return helper.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((m_id == null) ? 0 : m_id.hashCode());
	return result;
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
	DecisionMaker other = (DecisionMaker) obj;
	if (m_id == null) {
	    if (other.m_id != null) {
		return false;
	    }
	} else if (!m_id.equals(other.m_id)) {
	    return false;
	}
	return true;
    }

    private String m_id;

    /**
     * @param id
     *            not <code>null</code>, not empty.
     */
    public DecisionMaker(String id) {
	if (id == null || id.trim().length() == 0) {
	    throw new NullPointerException("" + id);
	}
	m_id = id;
    }

    public String getId() {
	return m_id;
    }

    @Override
    public int compareTo(DecisionMaker o) {
	return m_id.compareTo(o.m_id);
    }

    /**
     * <p>
     * Retrieves a function which gives the id of the given decision maker. No <code>null</code> values are accepted.
     * </p>
     * <p>
     * This provides an easy way to get short debug strings. E.g. to get a string representing the contents of a set of
     * decision makers <em>s</em>, use <code>Joiner.on(", ").join(Iterables.transform(s, getIdFct()))</code>.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    static public Function<DecisionMaker, String> getIdFct() {
	final Function<DecisionMaker, String> namer = new Function<DecisionMaker, String>() {
            @Override
	    public String apply(DecisionMaker input) {
        	return input.getId();
            }
        };
	return namer;
    }
}
