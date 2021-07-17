package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	PowerOutageDAO podao;
	private NercIdMap nercIdMap;
	private List<Nerc> nercList;
	
	private List<PowerOutages> eventList;
	private List<PowerOutages> eventListFiltered;
	private List<PowerOutages> solution;
	
	private int maxAffectedPeople;
	
	public Model() {
		podao = new PowerOutageDAO();
		
		nercIdMap = new NercIdMap();
		nercList = podao.getNercList(nercIdMap);
		eventList = podao.getPowerOutagesList(nercIdMap);
	}
	
	public List<Nerc> getNercList() {
		return podao.getNercList(nercIdMap);
	}
	
	public List<PowerOutages> getWorstCase(int maxNumberOfYears, int maxHoursOfOutage, Nerc nerc){
		solution = new ArrayList<PowerOutages>();
		maxAffectedPeople = 0;
		eventListFiltered = new ArrayList<PowerOutages>();
		
		for(PowerOutages event:eventList) {
			if(event.getNerc().equals(nerc)) {
				eventListFiltered.add(event);
			}
		}
		
		Collections.sort(eventListFiltered);
		recursive(new ArrayList<PowerOutages>(), maxNumberOfYears, maxHoursOfOutage);
		
		return solution;
	}
	
	private void recursive(ArrayList<PowerOutages> partial, int maxNumberOfYears, int maxHoursOfOutage) {
		if(sumAffectedPeople(partial)>maxAffectedPeople) {
			maxAffectedPeople = sumAffectedPeople(partial);
			solution = partial;
		}
		
		for(PowerOutages event : eventListFiltered) {
			
			if(!partial.contains(event)) {
				
				partial.add(event);
				
				if(checkMaxYears(partial, maxNumberOfYears) && checkMaxHoursOfOutage(partial, maxHoursOfOutage)) {
					recursive(partial, maxNumberOfYears, maxHoursOfOutage);
					partial.remove(event);
				}
			}
		}
		
	}

	public int sumAffectedPeople(List<PowerOutages> partial) {
		int sum = 0;
		for(PowerOutages event : partial) {
			
		}
		return 0;
	}
	
	private boolean checkMaxYears(List<PowerOutages> partial, int maxNumberOfYears) {
		if(partial.size()>=2) {
			int y1 = partial.get(0).getYear();
			int y2 = partial.get(partial.size()-1).getYear();
			if((y2-y1+1)>maxNumberOfYears) {
				return false;
			}
		}
		return true;
	}
	
	public int sumOutageHours(List<PowerOutages> partial) {
		int sum = 0;
		for(PowerOutages event : partial) {
			sum += event.getOutageDuration();
		}
		return sum; 
	}
	
	public boolean checkMaxHoursOfOutage(List<PowerOutages> partial, int maxHoursOfOutages) {
		int sum = sumOutageHours(partial);
		if(sum>maxHoursOfOutages) {
			return false;
		}
		return true;
	}
	
	public List<Integer> getYearList(){
		Set<Integer> yearSet = new HashSet<Integer>();
		for(PowerOutages event : eventList) {
			yearSet.add(event.getYear());
		}
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.sort(new Comparator<Integer>() {
			public int compare(Integer i1, Integer i2) {
				return i2.compareTo(i1);
			}
		});
		return yearList;
	}

}
