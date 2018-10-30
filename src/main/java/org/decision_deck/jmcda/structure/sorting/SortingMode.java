package org.decision_deck.jmcda.structure.sorting;

import java.util.Collection;

import org.decision_deck.utils.collection.CollectionUtils;

public enum SortingMode {
    OPTIMISTIC, PESSIMISTIC, /**
     * Each alternative assigned to both the optimistic and the pessimistic categories, as well
     * as each category in between.
     */
    BOTH;

    static public Collection<String> strings() {
        return CollectionUtils.asStrings(values());
    }
}
