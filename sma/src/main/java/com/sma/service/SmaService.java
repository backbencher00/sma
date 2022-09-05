package com.sma.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sma.controller.request.GetFareRequest;
import com.sma.controller.response.SmaApiResponse;
import com.sma.helper.DayAndIsPeak;
import com.sma.helper.FareRule;

@Service
public class SmaService {
	@Autowired
	CsvReader csvReader;
	public SmaApiResponse calculateFare(MultipartFile file ) throws Exception {
		if(!validateRequestBody(file))return  new SmaApiResponse("400", "success", "file is empty", null );
		List<GetFareRequest> fareList =csvReader.csvToTutorials(file);
		if(null == fareList || fareList.isEmpty())return new SmaApiResponse("400", "success", "No data in File or field are not as expected", null );
		HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap =buildFareRuleHashMap();
		Integer totalFareInGG = 0,totalFareInRR = 0,totalFareInGR = 0, totalFareInRG = 0;
		for(GetFareRequest fares : fareList) {
			DayAndIsPeak dayAndIsPeak  = getDayAndisPeak(fares);
			 if(fares.getFromLine().equals("Green") && fares.getToLine().equals("Green")) {
				 totalFareInGG = validateGG(fareCap, totalFareInGG, dayAndIsPeak);
			 }
			else if(fares.getFromLine().equals("Red") && fares.getToLine().equals("Red")) {
				 totalFareInRR = validateRR(fareCap, totalFareInRR, dayAndIsPeak);
			}
			else if(fares.getFromLine().equals("Green") && fares.getToLine().equals("Red")) {
				 totalFareInGR = validateGR(fareCap, totalFareInGR, dayAndIsPeak);
			}
			else if(fares.getFromLine().equals("Red") && fares.getToLine().equals("Green")){
				 totalFareInRG = validateRG(fareCap, totalFareInRG, dayAndIsPeak);
			}
			
			else throw new Exception("Please try diff output");
		}
		return new SmaApiResponse("200", "success", "your fare is here ", totalFareInGG + totalFareInRR + totalFareInGR + totalFareInRG );
	}
	public Boolean validateRequestBody(MultipartFile file) throws IOException {
		if(null == file || null == file.getInputStream())return false;
		return true;
	}
	private Integer validateRG(HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap, Integer totalFareInRG,
			DayAndIsPeak dayAndIsPeak) {
		HashMap<DayOfWeek, Integer> mp = fareCap.get(FareRule.R_G);
		 Integer fare = dayAndIsPeak.getIsPeak() ? 3 : 2;
		 if(mp.containsKey(dayAndIsPeak.getDay())) {
			 mp.put(dayAndIsPeak.getDay(), Math.min(mp.get(dayAndIsPeak.getDay()) + fare , 15));
		 }else {
			 mp.put(dayAndIsPeak.getDay(), fare);
		 }
		 totalFareInRG=0;
		 totalFareInRG = totalFareInRG + weeklyCapRG(mp);
		 fareCap.put(FareRule.R_G, mp);
		return totalFareInRG;
	}
	private Integer validateGR(HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap, Integer totalFareInGR,
			DayAndIsPeak dayAndIsPeak) {
		HashMap<DayOfWeek, Integer> mp = fareCap.get(FareRule.G_R);
		 Integer fare = dayAndIsPeak.getIsPeak() ? 4 : 3;
		 if(mp.containsKey(dayAndIsPeak.getDay())) {
			 mp.put(dayAndIsPeak.getDay(), Math.min(mp.get(dayAndIsPeak.getDay()) + fare , 15));
		 }else {
			 mp.put(dayAndIsPeak.getDay(), fare);
		 }
		 totalFareInGR=0;
		 totalFareInGR = totalFareInGR + weeklyCapGR(mp);
		 fareCap.put(FareRule.G_R, mp);
		return totalFareInGR;
	}
	private Integer validateRR(HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap, Integer totalFareInRR,
			DayAndIsPeak dayAndIsPeak) {
		HashMap<DayOfWeek, Integer> mp = fareCap.get(FareRule.R_R);
		 Integer fare = dayAndIsPeak.getIsPeak() ? 3 : 2;
		 if(mp.containsKey(dayAndIsPeak.getDay())) {
			 mp.put(dayAndIsPeak.getDay(), Math.min(mp.get(dayAndIsPeak.getDay()) + fare , 12));
		 }else {
			 mp.put(dayAndIsPeak.getDay(), fare);
		 }
		 totalFareInRR=0;
		 totalFareInRR = totalFareInRR + weeklyCapRR(mp);
		 fareCap.put(FareRule.R_R, mp);
		return totalFareInRR;
	}
	private Integer validateGG(HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap, Integer totalFareInGG,
			DayAndIsPeak dayAndIsPeak) {
			HashMap<DayOfWeek, Integer> mp = fareCap.get(FareRule.G_G);
			Integer fare = dayAndIsPeak.getIsPeak() ? 2 : 1;
			 if(mp.containsKey(dayAndIsPeak.getDay())) {
				 mp.put(dayAndIsPeak.getDay(), Math.min(mp.get(dayAndIsPeak.getDay()) + fare , 8));
			 }else {
				 mp.put(dayAndIsPeak.getDay(), fare);
			 }
		 totalFareInGG=0;
		 totalFareInGG = totalFareInGG + weeklyCapGG(mp);
		 fareCap.put(FareRule.G_G, mp);
		return totalFareInGG;
	}
	private HashMap<FareRule, HashMap<DayOfWeek, Integer>> buildFareRuleHashMap() {
		HashMap<FareRule, HashMap<DayOfWeek, Integer>> fareCap = new HashMap<>();
		fareCap.put(FareRule.G_G, new HashMap<DayOfWeek, Integer>());
		fareCap.put(FareRule.R_R, new HashMap<DayOfWeek, Integer>());
		fareCap.put(FareRule.R_G, new HashMap<DayOfWeek, Integer>());
		fareCap.put(FareRule.G_R, new HashMap<DayOfWeek, Integer>());
		return fareCap;
	}
    
    public DayOfWeek getDay(LocalDateTime dateTime) {
    	DayOfWeek day = dateTime.getDayOfWeek();
    	return day;
    }
    
    public Integer getHour(LocalDateTime dateTime) {
    	return dateTime.getHour();
    }
    
    public Integer getMinute(LocalDateTime dateTime) {
    	return dateTime.getMinute();
    }
    public DayAndIsPeak getDayAndisPeak(GetFareRequest fares) {
    		DayAndIsPeak cp = new DayAndIsPeak();
    		LocalDateTime  dateTime =  fares.getTravelDate();
			DayOfWeek day = getDay(dateTime);
			if((day.equals(DayOfWeek.MONDAY) || day.equals(DayOfWeek.TUESDAY) || day.equals(DayOfWeek.WEDNESDAY) || day.equals(DayOfWeek.THURSDAY) || day.equals(DayOfWeek.FRIDAY)) 
					&& ((8<=getHour(dateTime) && getHour(dateTime)<=10) || ( 16<=getHour(dateTime) && getHour(dateTime)<=19))){
				if(16==getHour(dateTime) && getMinute(dateTime)<30) {
					cp.setIsPeak(Boolean.FALSE);
				}else cp.setIsPeak(Boolean.TRUE);
			}
			else if(day.equals(DayOfWeek.SATURDAY)
					&& ((10<=getHour(dateTime) && getHour(dateTime)<=14) || (18<=getHour(dateTime) && getHour(dateTime)<=23))){
				cp.setIsPeak(Boolean.TRUE);
			}
			else if(day.equals(DayOfWeek.SUNDAY) && (18<=getHour(dateTime) && getHour(dateTime)<=23)){
				cp.setIsPeak(Boolean.TRUE);
			}
			else {
				 
				cp.setIsPeak(Boolean.FALSE);
			}
			cp.setDay(day);
 			return cp;
    	}
    
    public Integer weeklyCapGG(HashMap<DayOfWeek, Integer> fareCap) {
    	Integer sum = 0;
    	for(Integer it : fareCap.values()) sum = it+sum;
    	return (sum>55)?55 : sum;
    }
    public Integer weeklyCapRR(HashMap<DayOfWeek, Integer> fareCap) {
    	Integer sum = 0;
    	for(Integer it : fareCap.values()) sum = it+sum;
    	return (sum>70)?70 : sum;
    }
    public Integer weeklyCapRG(HashMap<DayOfWeek, Integer> fareCap) {
    	Integer sum = 0;
    	for(Integer it : fareCap.values()) sum = it+sum;
    	return (sum>90)?90 : sum;
    }
    public Integer weeklyCapGR(HashMap<DayOfWeek, Integer> fareCap) {
    	Integer sum = 0;
    	for(Integer it : fareCap.values()) sum = it+sum;
    	return (sum>90)?90 : sum;

    }
    
 }
