package com.sma.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sma.controller.request.GetFareRequest;

@Service
public class CsvReader {
	 public List<GetFareRequest> csvToTutorials(MultipartFile file) throws IOException {
		 InputStream is = file.getInputStream();
		    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		        CSVParser csvParser = new CSVParser(fileReader,
		            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
		      List<GetFareRequest> tutorials = new ArrayList<GetFareRequest>();
		      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
		      for (CSVRecord csvRecord : csvRecords) {
		    	  GetFareRequest tutorial = new GetFareRequest();
		    	  String fromLine = csvRecord.get("FromLine");
		    	  tutorial.setFromLine(fromLine);
		    	  String toLine = csvRecord.get("ToLine");
		    	  tutorial.setToLine(toLine);
		    	  String dateTime = csvRecord.get("DateTime");
		    	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    	  LocalDateTime dateTime2 = LocalDateTime.parse(dateTime, formatter);
		    	  tutorial.setTravelDate(dateTime2);
		        tutorials.add(tutorial);
		      }
		      return tutorials;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		    }
		  }
}
