package com.amalitech.demo.utils;

import java.util.Comparator;
import java.util.List;

/**
 * Generic sorter contract — implementations should provide a stable, O(n log n) sort algorithm.
 */
public interface Sorter<T> {
    /**
     * Sorts the provided list using the comparator and returns a new sorted list.
     * Implementations should not modify the original list unless documented.
     */
    List<T> sort(List<T> items, Comparator<T> comparator);
}
