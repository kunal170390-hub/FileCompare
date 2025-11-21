package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Small utility to diff two resource files containing records in the form:
 *   key$val1$val2|val3  (splitters supported: '$' or '|')
 *
 * Optimizations:
 * - Avoid unnecessary set copies when comparing sets (use Set.equals).
 * - Build sets with known initial capacity.
 * - Only compute & store detailed diffs when there actually are differences.
 * - Use the class' classloader to read resource streams and specify UTF-8.
 * - Minor readability / API improvements (accept args for filenames).
 */
public class FileDiff2 {

    public static void main(String[] args) throws IOException {
        String fileOld = args.length > 0 ? args[0] : "file1.txt";
        String fileNew = args.length > 1 ? args[1] : "file2.txt";

        long start = System.nanoTime();
        List<String> oldFile = readResourceFile(fileOld);
        List<String> newFile = readResourceFile(fileNew);

        Map<String, Set<String>> oldMap = prepareMap(oldFile);
        Map<String, Set<String>> newMap = prepareMap(newFile);

        Map<String, Set<String>> missingOrChanged = findMissingOrChanged(oldMap, newMap);
        Map<String, Set<String>> changedValues = findChangedValues(missingOrChanged, newMap);

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("Diff computed in %d ms. Result:%n%s%n", elapsedMs, changedValues);
    }

    // -------------------------------
    // Find keys missing in newMap OR records that no longer match (order-independent)
    // -------------------------------
    public static Map<String, Set<String>> findMissingOrChanged(
            Map<String, Set<String>> oldMap,
            Map<String, Set<String>> newMap) {

        // Pre-size result to avoid rehashes in many-diff cases
        Map<String, Set<String>> result = new HashMap<>(Math.min(oldMap.size(), 16));

        for (Map.Entry<String, Set<String>> entry : oldMap.entrySet()) {
            String key = entry.getKey();
            Set<String> oldValues = entry.getValue();

            Set<String> newValues = newMap.get(key);

            // Key completely missing
            if (newValues == null) {
                result.put(key, oldValues);
                continue;
            }

            // Both are sets already; equals is order-independent and quicker than making copies
            if (!oldValues.equals(newValues)) {
                result.put(key, oldValues);
            }
        }

        return result;
    }

    // -------------------------------
    // For changed keys, find actual differences (symmetric difference)
    // -------------------------------
    public static Map<String, Set<String>> findChangedValues(
            Map<String, Set<String>> diffOldMap,
            Map<String, Set<String>> newMap) {

        Map<String, Set<String>> result = new HashMap<>(Math.min(diffOldMap.size(), 16));

        for (Map.Entry<String, Set<String>> entry : diffOldMap.entrySet()) {
            String key = entry.getKey();
            Set<String> oldSet = entry.getValue();
            Set<String> newSet = newMap.get(key);

            // missing entirely in newMap -> report oldSet as removed
            if (newSet == null) {
                result.put(key, new HashSet<>(oldSet));
                continue;
            }

            // compute symmetric difference: (old \ new) U (new \ old)
            // Avoid allocating more collections than necessary when possible.
            Set<String> removed = new HashSet<>(Math.max(16, oldSet.size()));
            removed.addAll(oldSet);
            removed.removeAll(newSet); // items present in old but not in new

            Set<String> added = new HashSet<>(Math.max(16, newSet.size()));
            added.addAll(newSet);
            added.removeAll(oldSet); // items present in new but not in old

            if (!removed.isEmpty() || !added.isEmpty()) {
                // union removed + added
                removed.addAll(added);
                result.put(key, removed);
            }
            // if neither removed nor added, keys were flagged earlier but ended up equal — skip storing empty diffs
        }

        return result;
    }

    // -------------------------------
    // Convert records into a map: key -> Set(values)
    // -------------------------------
    public static Map<String, Set<String>> prepareMap(List<String> records) {
        Map<String, Set<String>> result = new HashMap<>(Math.max(16, records.size()));

        for (String record : records) {
            // assuming key$val1$val2...  (split on $ or |)
            String[] parts = record.split("[\\$\\|]");
            if (parts.length <= 1) {
                continue;
            }
            String key = parts[0];

            // create set with expected capacity to avoid rehashing
            int valueCount = parts.length - 1;
            Set<String> values = new HashSet<>(Math.max(4, (int) (valueCount / 0.75f) + 1));
            for (int i = 1; i < parts.length; i++) {
                values.add(parts[i]);
            }
            result.put(key, values);
        }

        return result;
    }

    // -------------------------------
    // Read file from classpath resources (UTF-8)
    // -------------------------------
    public static List<String> readResourceFile(String fileName) throws IOException {
        // Prefer class' classloader; look for resource at root of classpath
        ClassLoader cl = FileDiff2.class.getClassLoader();
        try (InputStream is = cl.getResourceAsStream(fileName)) {
            if (is == null) {
                // try with leading slash (some classloaders expect it from the caller)
                try (InputStream is2 = FileDiff2.class.getResourceAsStream("/" + fileName)) {
                    if (is2 == null) {
                        throw new IllegalArgumentException("File not found on classpath: " + fileName);
                    }
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8))) {
                        return br.lines().toList();
                    }
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return br.lines().toList();
            }
        }
    }
}