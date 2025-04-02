package org.decision_deck.jmcda.structure;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * <p>
 * A criterion object. Has a unique id (not {@code null}).
 * </p>
 * <p>
 * A criterion may also represent an attribute, depending on the context. An advantage is that a matrix of evaluation of
 * alternatives against criteria may also be used to represent evaluations of alternatives against attributes. The
 * difference exists when more context is known. Typically, a criterion has a preference direction, an attribute does
 * not.
 * </p>
 * <p>
 * Objects of this type are immutable.
 * </p>
 * <p>
 * A criterion {@link #equals(Object)} an other one iff they have the same id.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class Criterion implements Comparable<Criterion> {
    /**
     * Never {@code null}.
     */
    private final String m_id;

    /**
     * @param id
     *            not {@code null}.
     */
    public Criterion(final String id) {
	checkNotNull(id);
	m_id = id;
    }

    /**
     * Creates a new criterion by copying the one given.
     * 
     * @param criterion
     *            not {@code null}.
     */
    public Criterion(Criterion criterion) {
	if (criterion == null) {
	    throw new NullPointerException();
	}
	m_id = criterion.m_id;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Criterion other = (Criterion) obj;
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
	result = prime * result + (m_id.hashCode());
	return result;
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(m_id);
	return helper.toString();
    }

    @Override
    public int compareTo(Criterion o) {
	return m_id.compareTo(o.m_id);
    }

    /**
     * <p>
     * Retrieves a function which gives the id of the given criterion. No {@code null} values are accepted.
     * </p>
     * <p>
     * This provides an easy way to get short debug strings. E.g. to get a string representing the contents of a set of
     * criteria <em>s</em>, use {@code Joiner.on(", ").join(Iterables.transform(s, getIdFct()))}.
     * </p>
     * 
     * @return not {@code null}.
     */
    static public Function<Criterion, String> getIdFct() {
	final Function<Criterion, String> namer = new Function<Criterion, String>() {
            @Override
	    public String apply(Criterion input) {
        	return input.getId();
            }
        };
	return namer;
    }

}
