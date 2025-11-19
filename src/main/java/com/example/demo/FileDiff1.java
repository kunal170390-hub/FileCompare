package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.repository.init.ResourceReader;

public class FileDiff1 {

	public static void main(String[] args) throws IOException {
//      List<String> oldFile = Files.readAllLines(Paths.get("file1.txt"));
//      List<String> newFile = Files.readAllLines(Paths.get("file2.txt"));
  	 List<String> oldFile = readResourceFile("file1.txt");
  	 List<String> newFile = readResourceFile("file2.txt");

      Set<String> oldSet = new HashSet<>(oldFile);
      Set<String> newSet = new HashSet<>(newFile);
      
      Map<String, List<String>> oldFileMap = prepareMap(oldSet);
      Map<String, List<String>> newFileMap = prepareMap(newSet);
      
      Map<String, List<String>> oldFileFilterMap = filterUnMatchedRecods(oldFileMap, newFileMap);
      Map<String, List<String>> newFileFilterMap = filterChangedData(oldFileFilterMap, newFileMap);

      System.out.println(newFileFilterMap);

  }
	public static Map<String, List<String>> filterChangedData(Map<String, List<String>> fileFilterMap,
			Map<String, List<String>> newFileMap) {
		Map<String, List<String>> filteredMap = new HashMap<>();
		for (String key : fileFilterMap.keySet()) {
	    	  List<String> file2DataList = newFileMap.get(key);
	    	  if(file2DataList != null) {
	    		  List<String> filteredData = fileFilterMap.get(key).stream().filter(item -> !file2DataList.contains(item)).collect(Collectors.toList());
	    		  List<String> filteredData1 = file2DataList.stream().filter(item -> !fileFilterMap.get(key).contains(item)).collect(Collectors.toList());
	    		  filteredData.addAll(filteredData1);
	    		  filteredMap.put(key, filteredData);
	    	  } else {
	    		  filteredMap.put(key, fileFilterMap.get(key));
	    	  }
	      }
		return filteredMap;
	}
	
	public static Map<String, List<String>> filterUnMatchedRecods(Map<String, List<String>> oldFileMap,
			Map<String, List<String>> newFileMap) {
		Map<String, List<String>> filteredMap = new HashMap<>();
		for (String key : oldFileMap.keySet()) {
			// Same order
			// boolean exists = newFileMap.containsValue(oldFileMap.get(key));
			// order does not matter
			boolean exists = newFileMap.values().stream().anyMatch(
					list -> list.size() == oldFileMap.get(key).size() && list.containsAll(oldFileMap.get(key)));
			if (!exists) {
				filteredMap.put(key, oldFileMap.get(key));
			}
		}
		return filteredMap;
	}
	
  public static Map<String, List<String>> prepareMap(Set<String> records){
	  Map<String, List<String>> fileMap = new HashMap<>();
	  for(String record : records) {
		  String[] dataAray = record.split("[$|]");
		  fileMap.put(dataAray[0], Arrays.asList(dataAray).subList(1, dataAray.length));
	  }
	  return fileMap;
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