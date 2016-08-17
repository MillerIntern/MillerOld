package services;
	
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import projectObjects.Project;
import projectObjects.EquipmentType;
import projectObjects.VendorStorage;
import projectObjects.EquipmentStorage;
import projectObjects.Equipment;

public class EquipmentService {

		
	public static void setEquipment( String id, String equipmentString) 
	{
		Project p = new Project();
		Equipment equipSet = new Equipment();
		try{		
		p = (Project)ProjectObjectService.get(Long.parseLong(id), "Project");
		}
		
		catch(ClassNotFoundException e)
		{	
			e.printStackTrace();
		}
		EquipmentStorage equip = new EquipmentStorage(equipmentString, Long.parseLong(id));
		VendorStorage vS = new VendorStorage(vendInfo, Long.parseLong(id));
		System.out.println("The equipment string to be stored: "+equipmentString + " " + p.getEquipmentData().getEquipmentIDs());
		System.out.println("The vendor string to be stored: "+vendInfo);
		if(p.getEquipmentData().getEquipmentIDs() == 1 && p.getEquipmentData().getVendorIDs() == 1)
		{
			ProjectObjectService.addObject("EquipmentStorage", equip);
			ProjectObjectService.addObject("VendorStorage", vS);
			equipSet.setVendorIDs(Long.parseLong(id));
			equipSet.setEquipmentIDs(Long.parseLong(id));
		}
		else
		{
		try{			
			ProjectObjectService.editObject("EquipmentStorage", p.getEquipmentData().getEquipmentIDs(), equip,0);
			ProjectObjectService.editObject("VendorStorage", p.getEquipmentData().getVendorIDs(), vS,0);
			}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		}
	}
	/*
	public static void addEquipment(String equipmentName)
	{
		EquipmentType equip = new EquipmentType(equipmentName);
		System.out.println(equipmentName);
		ProjectObjectService.addObject("EquipmentType", equip);
	}
	
	public static void addVendor(String venName)
	{
		EquipVendor vendor = new EquipVendor(venName);
		System.out.println(venName);
		ProjectObjectService.addObject("EquipVendor", vendor);
	}
	*/
	public static String retrieveProjectEquipmentSet(String id)
	{
		String ret = null;
			try{
				Project equipData = (Project)ProjectObjectService.get(Long.parseLong(id), "Project");
				System.out.println(equipData);
				ret = new Gson().toJson(equipData.getEquipment());
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			return ret;
	}
	
	public static String getAllEquipssAsJson()
	{
		
		List<Object> list = ProjectObjectService.getAll("Project");
		return new Gson().toJson(list);
	}
	
	/*public static String getAllVendorsAsJson()
	{
		List<Object> list = ProjectObjectService.getAll("EquipVendor");
		return new Gson().toJson(list);
	}
		/**
		 * This method gets all of the equipment in the database as a json string
		 * @return a string representing a JSON array
		 */
	public static String getAllRequestsAsJson()
	{
		List<Object> list = ProjectObjectService.getAll("Equipment");
	    return new Gson().toJson(list);
	}
}


