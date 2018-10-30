package org.decision_deck.jmcda.structure.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * <p>
 * Maintains a map of (key, value) pairs, together with a virtual value called <em>shared</em>. A value can have a
 * special state called empty, the state it has when it has just been created. When all values in the map are equal they
 * are said to be shared.
 * </p>
 * <p>
 * The {@link #equals(Object)} method is used to define the equality relation on the set of values.
 * </p>
 * <p>
 * This object must be associated with a factory permitting to create new value objects and copies of existing value
 * objects.
 * </p>
 * <p>
 * This object exposes a special value called <em>shared</em> that can be read and written. When read, and the values
 * are shared, the special value <em>shared</em> is equal to that shared value. When they are not, the special value
 * <em>shared</em> is empty. As a particular case, when this object holds no values, they are said to be shared, and the
 * shared object can hold a different value (the user may set a value to the shared value even when this object holds no
 * other values). Writing to the shared value amounts to change every value in the map. More precisely, it amounts to
 * erase every possibly previously set value and replace it with the shared value. However the user must take care to
 * call {@link #updateShared()} after updating the shared value for this object to update everything properly.
 * Similarily, it must call one of the put methods after having updated any value in the map.
 * <p>
 * To spare some memory and improve performance, it is also possible to associate some keys with the special values
 * empty (i.e. the value is as if it was just created) or shared (i.e. that value is the same as the value of
 * <em>shared</em> at the time the method is called). Using these shortcuts allows this object to only create these
 * values when needed, and to not maintain them up to date unneedlessly.
 * </p>
 * <p>
 * The <code>null</code> key is not allowed, no <code>null</code> values are allowed.
 * </p>
 * 
 * @author Olivier Cailloux
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type. The {@link #equals(Object)} method of that class is used to test for shared value.
 * 
 */
public class SharedDirector<K, V> {
    public interface CopyContents<T> {
	/**
	 * @param source
	 *            not <code>null</code>.
	 * @param target
	 *            not <code>null</code>, the object whose contents must be replaced.
	 * @return <code>true</code> iff the destination object changed, i.e. iff it was not equal to the source object.
	 */
	public boolean copyContents(T source, T target);
    }

    /**
     * A <code>null</code> value means that the value is equal to the shared value (or to an empty value before shared
     * is initialized).
     */
    private final Map<K, V> m_map = new LinkedHashMap<K, V>();

    /**
     * Is lazy-initialized. If <code>null</code>, nobody cares about the shared value and it must thus not be kept up to
     * date. If non empty, the values are shared. If empty, either all values are empty, or the values are not shared.
     */
    private V m_shared;

    private final Function<V, V> m_factory;
    private final Modifier<V> m_emptier;

    private final Predicate<V> m_isEmpty;

    private final CopyContents<V> m_copier;

    /**
     * @param factory
     *            a factory which gives a deep copy of any value. When given <code>null</code>, the factory must return
     *            a new empty value. It may never return <code>null</code>. Not <code>null</code>.
     * @param copier
     *            a function to copy a source content into a target object. Not <code>null</code>.
     * @param emptier
     *            a modifier able to empty any given value. The modifier is never given a <code>null</code> value. Not
     *            <code>null</code>.
     * @param isEmpty
     *            a predicate able to tell whether a given value is in the empty state. The predicate is never given a
     *            <code>null</code> value. Not <code>null</code>.
     */
    public SharedDirector(Function<V, V> factory, CopyContents<V> copier, Modifier<V> emptier, Predicate<V> isEmpty) {
	if (factory == null || copier == null || emptier == null || isEmpty == null) {
	    throw new NullPointerException();
	}
	m_factory = factory;
	m_copier = copier;
	m_emptier = emptier;
	m_shared = null;
	m_isEmpty = isEmpty;
    }

    /**
     * Retrieves the value associated with the given key. The value is modifiable and the modification is reflected in
     * this object. The appropriate put method must be called after a modification of any value in this object.
     * Conversely, modifications in this value through this object is reflected in the returned object.
     * 
     * @param key
     *            not <code>null</code>.
     * @return the value associated with the given key, or <code>null</code> if there is no such key.
     */
    public V get(K key) {
	if (!m_map.containsKey(key)) {
	    return null;
	}
	return lazyInitValue(key);
    }

    /**
     * Retrieves a view of the value shared by all values in this object. That view is writable. It is empty when the
     * values are not shared (some values are different than others); it is equal to the shared value when the values
     * are shared. Writing to the returned object will write through it and all values in this object will be replaced
     * by its value when the method {@link #updateShared()} is called. It is mandatory to call the method
     * {@link #updateShared()} after updating, otherwise the state is inconsistent.
     * 
     * @return not <code>null</code>.
     */
    public V getShared() {
	if (m_shared == null) {
	    initShared();
	}
	return m_shared;
    }

    private void initShared() {
	final V model = getUniqueValue();
	m_shared = m_factory.apply(model);
    }

    /**
     * Interprets <code>null</code> values as meaning empty values.
     * 
     * @return the value that is equal to all values in this object, or <code>null</code> if not all values are equal.
     *         May also be <code>null</code> for empty.
     */
    private V getUniqueValue() {
	final V model;
	if (m_map.isEmpty()) {
	    model = null;
	} else {
	    final V first = m_map.values().iterator().next();
	    boolean allEqual = true;
	    for (V value : m_map.values()) {
		if (value == null) {
		    /** At this point: either all values are empty, and we may return null for empty, or we return null. */
		    return null;
		} else if (!value.equals(first)) {
		    allEqual = false;
		    break;
		}
	    }
	    if (allEqual) {
		model = first;
	    } else {
		model = null;
	    }
	}
	return model;
    }

    /**
     * This method must be called after a modification of the shared view (see {@link #getShared()}) to update the state
     * of this object. All values in this object are set to the current value of the shared value. Note that if the
     * shared value has not been modified, and this object contains non shared values, calling this method resets all
     * values to empty.
     */
    public void updateShared() {
	final V model;
	if (m_shared == null) {
	    final V unique = getUniqueValue();
	    if (unique == null) {
		model = m_factory.apply(null);
	    } else {
		/** Nothing to do: values are shared already. */
		return;
	    }
	} else {
	    model = m_shared;
	}
	for (K key : m_map.keySet()) {
	    final V value = m_map.get(key);
	    if (value != null) {
		m_copier.copyContents(model, value);
	    }
	}
    }

    /**
     * <p>
     * Call this method after some value has been updated to update the state of this object. In that case the value
     * will typically be the same reference as the one already in this object. It is mandatory that the value has the
     * same content as the current value, thus the user must have modified directly the value in this object before
     * calling this method. The method may also be called to add a new pair to this object, in which case the value
     * parameter is used to update this object.
     * </p>
     * <p>
     * No guarantee is given that the given value will be used directly in this object, thus the caller should not use a
     * reference to it as a view through this object (except if it is the same reference than the one given by a
     * previous call to get, naturally). An accurate view is obtained through a call to the appropriate get method.
     * </p>
     * 
     * @param key
     *            not <code>null</code>. A new or already existing key.
     * @param value
     *            not <code>null</code>. If the given key already exists in this object, this value must equal the value
     *            currently associated with the key.
     * @param sameAsShared
     *            an optional hint to improve performance. If the value is not <code>null</code>, and if the values are
     *            shared before this method is called, it indicates whether the given value is the same as the currently
     *            shared value. If <code>true</code>, it implies that if the values are shared before this method is
     *            called, it will still be after this method returns. If <code>false</code>, they will still be shared
     *            after this method returns iff this object contains exactly one key. If the values are not shared
     *            before this method is called, this parameter has no effect. Note that if that parameter is set to
     *            <code>true</code>, this method has the same effect than {@link #putShared(Object)}.
     */
    public void put(K key, V value, Boolean sameAsShared) {
	if (key == null || value == null) {
	    throw new NullPointerException("" + key + value + sameAsShared);
	}
	if (m_shared == null || m_isEmpty.apply(m_shared)) {
	    if (!m_map.containsKey(key)) {
		m_map.put(key, value);
	    }
	    if (m_shared != null) {
		final V model = getUniqueValue();
		if (model != null) {
		    /** Became shared. */
		    m_copier.copyContents(model, m_shared);
		} else {
		    /** Still not shared (or everything is empty). */
		}
	    }
	} else {
	    final boolean same;
	    if (sameAsShared == null) {
		same = value.equals(m_shared);
	    } else {
		same = sameAsShared.booleanValue();
	    }
	    if (same) {
		/** Still shared, nothing to do. */
		if (!m_map.containsKey(key)) {
		    m_map.put(key, null);
		}
	    } else {
		if (m_map.size() == 1 && m_map.containsKey(key)) {
		    /** Still shared but have to update shared. */
		    m_copier.copyContents(value, m_shared);
		} else {
		    /** Not shared any more. */
		    lazyInitValues();
		    m_emptier.modify(m_shared);
		    if (!m_map.containsKey(key)) {
			m_map.put(key, value);
		    }
		}
	    }
	}
    }

    private void lazyInitValues() {
	for (K key : m_map.keySet()) {
	    lazyInitValue(key);
	}
    }

    /**
     * <p>
     * Associates to the given key the value equals to the currently shared value. Note that this method has no effect
     * when the key already exists and the values are already shared. If all the values are empty, this method is
     * equivalent to {@link #putEmpty(Object)}.
     * </p>
     * <p>
     * Caution is required to use this method only when the values are shared. When the values are not shared, the
     * result of the method is undetermined.
     * </p>
     * 
     * @param key
     *            not <code>null</code>.
     */
    public void putShared(K key) {
	if (key == null) {
	    throw new NullPointerException();
	}

	final V existing = m_map.get(key);
	if (existing == null) {
	    m_map.put(key, null);
	} else {
	    /**
	     * Nothing to do as value is (supposed to be) already shared. We could check whether it is really the case
	     * but that would cost time.
	     */
	}
    }

    /**
     * <p>
     * Associates an empty value to the given key. A possibly existing value content is replaced by the new one.
     * </p>
     * 
     * @param key
     *            not <code>null</code>.
     * @return <code>true</code> iff the state of this object changed as a result of this call, i.e. <code>true</code>
     *         iff there was no value or a non empty value associated with the given key.
     */
    public boolean putEmpty(K key) {
	if (key == null) {
	    throw new NullPointerException("" + key);
	}

	if (m_shared != null && !m_isEmpty.apply(m_shared)) {
	    /** Not shared any more. */
	    lazyInitValues();
	    m_emptier.modify(m_shared);
	}
	/** Here, shared is necessarily empty (or not initialized). */

	final boolean changed;
	if (m_map.containsKey(key)) {
	    final V existing = m_map.get(key);
	    if (existing == null) {
		changed = false;
	    } else {
		changed = m_emptier.modify(existing);
	    }
	} else {
	    m_map.put(key, null);
	    changed = true;
	}
	return changed;
    }

    /**
     * Removes the pair related to the given key. If this is the last pair contained in this object, it is possible to
     * keep the value as a shared value (shared by noone!), thus it is then still available when querying the shared
     * value bound to this object.
     * 
     * @param key
     *            not <code>null</code>, must exist in this object.
     * @param keepShared
     *            <code>true</code> to keep the value as a shared value if it is the last one in this object. If there
     *            is more than one value left in this object, this parameter has no effect.
     */
    public void remove(K key, boolean keepShared) {
	if (key == null) {
	    throw new NullPointerException("" + keepShared);
	}
	if (!m_map.containsKey(key)) {
	    throw new IllegalStateException("No such key in the map: " + key + ".");
	}

	if (m_map.size() > 1) {
	    final V existing = m_map.remove(key);
	    if (existing != null) {
		m_emptier.modify(existing);
	    }
	    if (m_shared == null) {
		/** Nothing special. */
	    } else if (m_isEmpty.apply(m_shared)) {
		/** May become shared. */
		final V model = getUniqueValue();
		if (model != null) {
		    /** Became shared. */
		    m_copier.copyContents(model, m_shared);
		} else {
		    /** Still not shared (or everything is empty). */
		}
	    } else {
		/** Still shared, nothing to do. */
	    }
	} else {
	    if (keepShared && m_shared == null) {
		final V existing = m_map.remove(key);
		if (existing != null) {
		    m_shared = m_factory.apply(existing);
		}
		if (existing != null) {
		    m_emptier.modify(existing);
		}
	    } else {
		final V existing = m_map.remove(key);
		if (existing != null) {
		    m_emptier.modify(existing);
		}
		if (!keepShared && m_shared != null) {
		    m_emptier.modify(m_shared);
		}
	    }
	}
    }

    private V lazyInitValue(K key) {
	final V value = m_map.get(key);
	if (value == null) {
	    /** If shared is null, we get an empty value. */
	    final V init = m_factory.apply(m_shared);
	    m_map.put(key, init);
	    return init;
	}
	return value;
    }

    public Map<K, V> getAll() {
	lazyInitValues();
	return m_map;
    }

    /**
     * Applies a modification to all values stored in this object.
     * 
     * @param modifier
     *            the modification to apply. No <code>null</code> value is given to the modifier. Not <code>null</code>.
     * @param applyToEmpty
     *            an optimization hint. If <code>false</code>, the modification has no effect on empty objects and must
     *            not necessarily be applied to it. Note that this object does not guarantee that the modifier is never
     *            called on an empty object, this is only used for optimization when useful. Set it to <code>true</code>
     *            to force this object to apply the modifier to empty objects as well.
     * @return <code>true</code> iff the state of this object has changed as a result of this method call, thus
     *         <code>true</code> iff the modifier returned <code>true</code> to one of the modifications calls.
     */
    public boolean applyToAll(Modifier<V> modifier, boolean applyToEmpty) {
	if (modifier == null) {
	    throw new NullPointerException();
	}
	boolean modified = false;
	if (m_shared != null && !m_isEmpty.apply(m_shared)) {
	    modified = modifier.modify(m_shared);
	    if (modified) {
		updateShared();
	    }
	} else if (m_map.isEmpty()) {
	    if (m_shared == null) {
		initShared();
	    }
	    modified = modifier.modify(m_shared);
	} else {
	    for (K key : m_map.keySet()) {
		final V valueStored = m_map.get(key);
		final V value;
		if (valueStored == null && applyToEmpty) {
		    value = m_factory.apply(null);
		    m_map.put(key, value);
		} else {
		    value = valueStored;
		}
		if (value != null) {
		    final boolean modifiedThis = modifier.modify(value);
		    modified = modified || modifiedThis;
		}
	    }
	}
	return modified;
    }

    /**
     * <p>
     * Retrieves the value associated with the given key <em>if it is not empty</em>. The value is modifiable and the
     * modification is reflected in this object. The appropriate put method must be called after a modification of any
     * value in this object. Conversely, modifications in this value through this object is reflected in the returned
     * object.
     * </p>
     * <p>
     * Using this method instead of {@link #get(Object)} permits to avoid unnecessary lazy-initialisations in some
     * circumstances.
     * </p>
     * 
     * @param key
     *            not <code>null</code>.
     * @return the value associated with the given key, or <code>null</code> if there is no such key or if the value is
     *         empty.
     */
    public V getNonEmpty(K key) {
	if (!m_map.containsKey(key)) {
	    return null;
	}
	final V value = m_map.get(key);
	if (value == null) {
	    if ((m_shared == null || m_isEmpty.apply(m_shared))) {
		return null;
	    }
	    return lazyInitValue(key);
	}
	if (m_isEmpty.apply(value)) {
	    return null;
	}
	return value;
    }

    public boolean apply(K key, Modifier<V> modifier, boolean applyToEmpty) {
	if (!m_map.containsKey(key)) {
	    throw new IllegalArgumentException("Unknown key.");
	}
	final V valueOrig = m_map.get(key);
	final V value;
	if (valueOrig == null) {
	    if (!applyToEmpty && (m_shared == null || m_isEmpty.apply(m_shared))) {
		return false;
	    }
	    value = lazyInitValue(key);
	} else {
	    value = valueOrig;
	}
	final boolean modified = modifier.modify(value);
	if (modified) {
	    put(key, value, Boolean.FALSE);
	}
	return modified;
    }
}
