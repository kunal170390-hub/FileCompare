package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.repository.init.ResourceReader;

public class FileDiff {
    public static void main(String[] args) throws IOException {

//        List<String> oldFile = Files.readAllLines(Paths.get("file1.txt"));
//        List<String> newFile = Files.readAllLines(Paths.get("file2.txt"));
    	 List<String> oldFile = readResourceFile("file1.txt");
    	 List<String> newFile = readResourceFile("file2.txt");

        Set<String> oldSet = new HashSet<>(oldFile);
        Set<String> newSet = new HashSet<>(newFile);

        // Lines added in file2
        Set<String> added = new HashSet<>(newSet);
        added.removeAll(oldSet);

        // Lines removed in file2
        Set<String> removed = new HashSet<>(oldSet);
        removed.removeAll(newSet);

        System.out.println("ADDED:");
        added.forEach(System.out::println);

        System.out.println("\nREMOVED:");
        removed.forEach(System.out::println);
        
        System.out.println("\n----------------------------------------------------------------");
        // If you want to detect modified lines
        for (int i = 0; i < Math.min(oldFile.size(), newFile.size()); i++) {
            if (!oldFile.get(i).equals(newFile.get(i))) {
                System.out.println("Modified line " + (i+1));
                System.out.println("Old: " + oldFile.get(i));
                System.out.println("New: " + newFile.get(i));
                
                String[] newData = newFile.get(i).split("[$|]");
                String[] oldData = oldFile.get(i).split("[$|]");
                List<String> filteredData = Arrays.asList(newData).stream().filter(item -> !Arrays.asList(oldData).contains(item)).collect(Collectors.toList());
                filteredData.forEach(System.out::println);
            }
        }
    }
    
    public static List<String> readResourceFile(String fileName) throws IOException {
        ClassLoader classLoader = ResourceReader.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("File not found in resources: " + fileName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.toList());
            }
        }
    }
}