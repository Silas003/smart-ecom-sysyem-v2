package com.amalitech.demo.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Merge sort implementation (stable) that implements Sorter<T>.
 * This implementation returns a new List with the sorted items.
 */
public class MergeSorter<T> implements Sorter<T> {

    @Override
    public List<T> sort(List<T> items, Comparator<T> comparator) {
        if (items == null || items.size() <= 1) return items == null ? null : new ArrayList<>(items);
        List<T> src = new ArrayList<>(items);
        List<T> dest = new ArrayList<>(src.size());
        for (int i = 0; i < src.size(); i++) dest.add(null);
        mergeSortRecursive(src, dest, 0, src.size(), comparator);
        return dest;
    }

    private void mergeSortRecursive(List<T> src, List<T> dest, int start, int end, Comparator<T> cmp) {
        int length = end - start;
        if (length <= 1) {
            if (length == 1) dest.set(start, src.get(start));
            return;
        }
        int mid = start + (length >> 1);
        mergeSortRecursive(src, dest, start, mid, cmp);
        mergeSortRecursive(src, dest, mid, end, cmp);
        int i = start, p = start, q = mid;
        while (p < mid && q < end) {
            T left = dest.get(p);
            T right = dest.get(q);
            if (cmp.compare(left, right) <= 0) {
                src.set(i++, left); p++;
            } else {
                src.set(i++, right); q++;
            }
        }
        while (p < mid) src.set(i++, dest.get(p++));
        while (q < end) src.set(i++, dest.get(q++));
        for (int k = start; k < end; k++) dest.set(k, src.get(k));
    }
}
