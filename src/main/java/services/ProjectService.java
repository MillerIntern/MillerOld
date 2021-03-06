package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import objects.HibernateUtil;

import org.hibernate.Criteria;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import projectObjects.ChangeOrder;
import projectObjects.CloseoutCheckList;
import projectObjects.EquipmentStatus;
import projectObjects.EquipmentVendor;
import projectObjects.Equipment;
import projectObjects.Permits;
import projectObjects.Person;
import projectObjects.Project;
import projectObjects.ProjectClass;
import projectObjects.ProjectItem;
import projectObjects.ProjectObject;
import projectObjects.ProjectStage;
import projectObjects.ProjectStatus;
import projectObjects.ProjectType;
import projectObjects.Region;
import projectObjects.SalvageValue;
import projectObjects.Warehouse;
import projectObjects.CloseoutInfo;
import projectObjects.CloseoutDetails;
import projectObjects.Inspections;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.RequestHandler;
import services.ChangeOrderService;
import services.EquipmentService;
import services.ProjectObjectService;
import services.ProjectService;
import services.QueryService;
import services.CloseoutListService;

import java.util.Scanner;


/**
 * This class encapsulates logic to add, edit, and retrieve construction project information
 * @author Alex Campbell
 *
 */
public class ProjectService extends ProjectObjectService
{
	
	//private static Long iID;
	
	/**
	 * This method adds a project to the database. The fields REQUIRED for a project are specific arguments.
	 * The OPTIONAL fields for a projects are parsed from the HTTP Request parameter mapping
	 * 
	 * @param warehouseID the warehouse id
	 * @param managerID the manager id
	 * @param supervisorID the supervisor id
	 * @param classID the ProjectClass id
	 * @param projectItemID the ProjectItem id
	 * @param statusID the ProjectStatus id
	 * @param stageID the ProjectStage id
	 * @param scope the scope of the project
	 * @param params the parameter mapping of the HTTP Request
	 * @return
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public  String addProject(Long warehouseID, Long managerID, Long supervisorID, Long classID, Long projectItemID, Long statusID, Long stageID, Long typeID, String scope, 
			Map<String, String>params, Long inspectionTN, HttpServletRequest req) throws ClassNotFoundException, ParseException, NumberFormatException, IOException
	{
		System.out.println("in add");
		//Initialize Services
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	
		//Read Max ID from file
		File file = new File("projectIdCount.txt");
		Integer maxId = 0;
		for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()))) {
			System.out.println("path:" +Paths.get(file.getAbsolutePath()));
		   maxId = Integer.valueOf(line) + 1;
		   
		}
			Long maxID = (long) maxId;	
			System.out.println("Current ID" + maxID);
		//Get REQUIRED project data
		Warehouse warehouse = (Warehouse) ProjectObjectService.get(warehouseID, "Warehouse");
		Person manager = (Person) ProjectObjectService.get(new Long(managerID), "Person");
		Person supervisor = (Person) ProjectObjectService.get(new Long(supervisorID), "Person");
		ProjectClass projectClass = (ProjectClass) ProjectObjectService.get(new Long(classID), "ProjectClass");
		ProjectStatus status = (ProjectStatus) ProjectObjectService.get(new Long(statusID), "ProjectStatus");
		ProjectItem item = (ProjectItem) ProjectObjectService.get(new Long(projectItemID), "ProjectItem");
		ProjectStage stage = (ProjectStage) ProjectObjectService.get(stageID, "ProjectStage");
		ProjectType pType = (ProjectType) ProjectObjectService.get(typeID, "ProjectType");
		String mcsNumString = params.get("mcsNumber");
		

		
		
		//Equipment Data
		/*Warehouse warehouse_eq = (Warehouse) ProjectObjectService.get(Long.parseLong(params.get("project_eq")), "Warehouse");	
		ProjectItem item_eq = (ProjectItem) ProjectObjectService.get(Long.parseLong(params.get("component_eq")), "ProjectItem");
		EquipmentVendor vendor_eq = (EquipmentVendor) ProjectObjectService.get(new Long(params.get("vendor_eq")), "EquipmentVendor");
		String fpoString = params.get("po_eq");
		System.out.print(fpoString);
			*/	
		
		Inspections inspections = null;
		if(inspectionTN!=-1)
			inspections= (Inspections) ProjectObjectService.get(inspectionTN, "Inspections");
	
		int mcsNum = -1;
		//int fpo = -1;
		//Parse mcsNumber, change to -1 if it's not a number
		try
		{
			mcsNum = Integer.parseInt(mcsNumString);
			//fpo = Integer.parseInt(fpoString);
			System.out.println(mcsNum);
			
			
		}catch(Exception e){}
		
		
		
		//Get invoice values and notes
		int shouldInvoice = Integer.parseInt(params.get("shouldInvoice"));
		int actualInvoice = Integer.parseInt(params.get("actualInvoice"));
		String notes = params.get("notes");

		//Create and persist change orders if they exist
		String changeOrderJsonString = params.get("coItems");
		HashSet<ChangeOrder> changeOrders = ChangeOrderService.getChangeOrdersFromString(changeOrderJsonString);
				
		//assign values to dates, if they are not null
		
		//
		Date fsalvageDate = null;
		Date finitiatedDate = null;
		Date fsurvey = null;
		Date fcostco = null;
		Date fproposal = null;
		Date fasBuilts = null;
		Date fpunchList = null;
		Date falarmHvac = null;
		Date fverisae = null;
		Date fcloseoutBook = null;
		Date fcloseoutNotes = null;
		Date fstart = null;
		Date fscheduled = null;
		Date factual = null;
		Date fairGas = null;
		Date fpermits = null;
		Date permitApp = null;
		
		//INspections
		Date fframing = null ; 
		Date fceiling = null; 
		Date froughMech = null; 
		Date froughElec= null;  
		Date froughPlumb = null; 
		Date fmechLightSmoke = null; 
		Date fmechFinal = null; 
		Date  felecFinal = null; 
		Date fplumbFinal = null; 
		Date ffireMarshal = null; 
		Date fhealth = null; 
		Date fbuildFinal = null; 
		Long finspectionID = null; 
		
		//Permits
		Date fbuilding_p =null;
		Date fmechanical_p = null;
		Date felectrical_p = null;
		Date plumbing_p = null;
		Date ffireSprinler_p = null;
		Date ffireAlarm_p = null;
		Date flowVoltage_p = null;
		
		//closeout
		Date mPunchListCLf =null;
		Date closeoutPhotosCLf=null;
		Date subConWarrantiesCLf =null;
		Date MCSWarrantyf =null;
		Date equipmentSubClf =null;
		Date traneCLf=null;
		Date frontPagef=null;
		Date subContractorCLf = null;
		Date buldingPermitCLf = null;
		Date inspectionSOCLf = null;
		Date certCompletionCLf = null;
		
		
		Date festimatedDeliveryDate = null;
		Date fvendorDate = null;
		
		//String noteseq = params.get("notes_eq");
		//String equipmentName = params.get("equipName");

		
		
		//Additional Fields
		String zachNotes = params.get("zachUpdates");
		String cost = params.get("cost");
		String customerNumber = params.get("customerNumber");
	
		
		if (!(params.get("salvageDate")).isEmpty())
			fsalvageDate = formatter.parse(params.get("salvageDate"));
		
		if (!(params.get("initiated")).isEmpty())
			finitiatedDate = formatter.parse(params.get("initiated"));
		
		if (!(params.get("survey")).isEmpty())
			fsurvey = formatter.parse(params.get("survey"));
		
		if (!(params.get("costco")).isEmpty())
			fcostco = formatter.parse(params.get("costco"));
		
		if (!(params.get("proposal")).isEmpty())
			fproposal = formatter.parse(params.get("proposal"));
		
//***********************Close OUT DETAILS******************************\\
		
		if (!(params.get("asBuilts")).isEmpty())
			fasBuilts = formatter.parse(params.get("asBuilts"));
		
		if (!(params.get("punchList")).isEmpty())
			fpunchList = formatter.parse(params.get("punchList"));
		
		if (!(params.get("alarmHvac")).isEmpty())
			falarmHvac = formatter.parse(params.get("alarmHvac"));
		
		if (!params.get("verisae").isEmpty())
			fverisae = formatter.parse(params.get("verisae"));
		
		if (!params.get("closeoutNotes").isEmpty())
			fcloseoutNotes = formatter.parse(params.get("closeoutNotes"));
		
		if (!params.get("startDate").isEmpty())
			fstart = formatter.parse(params.get("startDate"));
		
		if (!params.get("scheduledTurnover").isEmpty())
			fscheduled = formatter.parse(params.get("scheduledTurnover"));
		
		if (!params.get("actualTurnover").isEmpty())
			factual = formatter.parse(params.get("actualTurnover"));

		if (!params.get("airGas").isEmpty())
			fairGas = formatter.parse(params.get("airGas"));
		
		if (!params.get("permits").isEmpty())
			fpermits = formatter.parse(params.get("permits"));
		
		if(!params.get("permitApp").isEmpty()) 
			permitApp = formatter.parse(params.get("permitApp"));
		
	// ***************Optional Inspections************************ \\
		if(!params.get("framing").isEmpty()) 
			fframing = formatter.parse(params.get("framing"));
		
		if(!params.get("ceiling").isEmpty()) 
			fceiling = formatter.parse(params.get("ceiling"));
		
		if(!params.get("roughMech").isEmpty()) 
			froughMech = formatter.parse(params.get("roughMech"));
		
		if(!params.get("roughElec").isEmpty()) 
			froughElec = formatter.parse(params.get("roughElec"));
			System.out.println(froughElec);
		
		if(!params.get("roughPlumb").isEmpty()) 
			froughPlumb = formatter.parse(params.get("roughPlumb"));
		
		if(!params.get("mechLightSmoke").isEmpty()) 
		{
			fmechLightSmoke = formatter.parse(params.get("mechLightSmoke"));
		}
		if(!params.get("mechFinal").isEmpty()) 
			fmechFinal = formatter.parse(params.get("mechFinal"));
		
		if(!params.get("elecFinal").isEmpty()) 
			felecFinal = formatter.parse(params.get("elecFinal"));
		
		if(!params.get("plumbFinal").isEmpty()) 
			fplumbFinal = formatter.parse(params.get("plumbFinal"));
		
		if(!params.get("fireMarshal").isEmpty()) 
			ffireMarshal = formatter.parse(params.get("fireMarshal"));
		
		if(!params.get("health").isEmpty()) 
			fhealth = formatter.parse(params.get("health"));
		
		if(!params.get("buildFinal").isEmpty()) 
			fbuildFinal = formatter.parse(params.get("buildFinal"));
		
		// ***************Optional Permits************************ \\
		if(!params.get("building_p").isEmpty()) 
			fbuilding_p = formatter.parse(params.get("building_p"));
		
		if(!params.get("mechanical_p").isEmpty()) 
			fmechanical_p = formatter.parse(params.get("mechanical_p"));
		
		if(!params.get("electrical_p").isEmpty()) 
			felectrical_p = formatter.parse(params.get("electrical_p"));
		
		if(!params.get("plumbing_p").isEmpty()) 
			plumbing_p = formatter.parse(params.get("plumbing_p"));
		
		if(!params.get("fireSprinkler_p").isEmpty()) 
			ffireSprinler_p = formatter.parse(params.get("fireSprinkler_p"));
		
		if(!params.get("fireAlarm_p").isEmpty()) 
			ffireAlarm_p = formatter.parse(params.get("fireAlarm_p"));
		
		if(!params.get("lowVoltage_p").isEmpty()) 
		{
			flowVoltage_p = formatter.parse(params.get("lowVoltage_p"));
		}
		
//-------------------------CloseoutLIST----------------------------------------------------------------------------------------
		if(!params.get("buildingPermitCL").isEmpty()) 
			buldingPermitCLf = formatter.parse(params.get("buildingPermitCL"));
		if(!params.get("inspectionSOCL").isEmpty()) 
			inspectionSOCLf = formatter.parse(params.get("inspectionSOCL"));
		if(!params.get("certCompletionCL").isEmpty()) 
			certCompletionCLf = formatter.parse(params.get("certCompletionCL"));
		if(!params.get("mPunchListCL").isEmpty()) 
			mPunchListCLf = formatter.parse(params.get("mPunchListCL"));
		if(!params.get("closeoutPhotosCL").isEmpty()) 
			closeoutPhotosCLf = formatter.parse(params.get("closeoutPhotosCL"));
		if(!params.get("subConWarrantiesCL").isEmpty()) 
			subConWarrantiesCLf = formatter.parse(params.get("subConWarrantiesCL"));
		if(!params.get("MCSWarranty").isEmpty()) 
			MCSWarrantyf = formatter.parse(params.get("MCSWarranty"));
		if(!params.get("equipmentSubCL").isEmpty()) 
			equipmentSubClf  = formatter.parse(params.get("equipmentSubCL"));
		if(!params.get("traneCL").isEmpty()) 
			traneCLf = formatter.parse(params.get("traneCL"));
		
//---------------------------------------------Receiving ARRAYS ------------------------------------------------------------
		
		//Get equipment from the array
		String[] equipToAdd = getGSONArray(req, "newEquip");

		//If their was any added equipments
		if(equipToAdd.length!=0)
		{
		System.out.println("in equipment");
		
		String[][] equipProj = getGSON2DArray(req, "project_eq");
		String[][] equipComponent = getGSON2DArray(req, "component_eq");
		String[][] equipVendor = getGSON2DArray(req, "vendor_eq");
		
		String[] equipIDS = getGSONArray(req, "equipIDS");
		String[] equipPO = getGSONArray(req, "po_eq");
		String[] equipEDD = getGSONArray(req, "estimatedDeliveryDate_eq");
		String[] equipName  = getGSONArray(req, "equipName"); 
		String[] equipNotes = getGSONArray(req, "notes_eq");
		
		String[] equipStatus = getGSONArray(req, "status_eq");
		
		
		
		long longEquipPO[] = new long[equipToAdd.length];
		Date[] dateEquipEDD = new Date[equipToAdd.length];
		Date[] dateEquipVD = new Date[equipToAdd.length];
		int[] array = new int[equipToAdd.length];
		
		
//-------------------------------------------------------------------------------------------------------
		
		/*-------Equipment Objects----------*/
		Equipment equip;
		Warehouse warehouse_eq;
		ProjectItem item_eq;
		EquipmentVendor vendor_eq;
		EquipmentStatus status_eq;
		
		DateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

		
		for(int i=0; i<equipToAdd.length;i++)
			{
			equip = new Equipment();
			
			// Convert Arrays to either int, long or Date
			array[i] =(int) Long.parseLong(equipToAdd[i]);
			
			//crashing
			if(!equipPO[array[i]].equals(""))
			{
				longEquipPO[i] = new Long(equipPO[array[i]]);
			}
			
			if(!equipEDD[array[i]].equals(""))
			{
				dateEquipEDD[i] = format1.parse(equipEDD[array[i]]) ;
				System.out.println(dateEquipEDD[i]);
			}

			
			 warehouse_eq = (Warehouse) ProjectObjectService.get(Long.parseLong(equipProj[array[i]][2]), "Warehouse");	
			 item_eq = (ProjectItem) ProjectObjectService.get(Long.parseLong(equipComponent[array[i]][1]), "ProjectItem");
			 vendor_eq = (EquipmentVendor) ProjectObjectService.get(new Long(equipVendor[array[i]][1]), "EquipmentVendor");
			 status_eq = (EquipmentStatus) ProjectObjectService.get(new Long(equipStatus[array[i]]), "EquipmentStatus");
			 	
			 // 2D arrays
				equip.setWarehouse(warehouse_eq);
				equip.setEquipmentVendor(vendor_eq);
				equip.setProjectItem(item_eq);
				equip.setEquipStatus(status_eq);
				
				//Regular Arrays
				equip.setPO(longEquipPO[i]);
				equip.setEstimatedDelivery(dateEquipEDD[i]);
				equip.setEquipName(equipName[array[i]]);
				equip.setNotes(equipNotes[array[i]]);
				equip.setEqpd(maxID);
				
				System.out.println("EQPD:" +  maxID);
	
				ProjectObjectService.addObject("Equipment", equip);
			}
		}

				
		//Create SalvageValue object
		SalvageValue sv = null;
		try
		{
			double salvageAmount = Double.parseDouble(params.get("salvageAmount"));
			if (salvageAmount != 0 && !params.get("salvageDate").isEmpty())
			{
				sv = new SalvageValue(fsalvageDate, salvageAmount);
			}
		}
		catch(NumberFormatException nfe){}
		
		
		//Create CloseoutDetails Object.
		CloseoutDetails cd = new CloseoutDetails();
		
//-------------------------Closeout fields---------------------------------------------
		cd.setPunchList(fpunchList);
		cd.setAsBuilts(fasBuilts);
		cd.setAirGas(fairGas);
		cd.setAlarmHvacForm(falarmHvac);
		cd.setCloseoutBook(fcloseoutBook);
		cd.setCloseoutNotes(fcloseoutNotes);
		cd.setPermitsClosed(fpermits);
		cd.setPunchList(fpunchList);
		cd.setVerisaeShutdownReport(fverisae);
		cd.setSalvageValue(sv);
		
		cd.setMPunchListCL(mPunchListCLf) ;
		cd.setCloseoutPhotosCL(closeoutPhotosCLf);
		cd.setSubConWarrantiesCL(subConWarrantiesCLf);
		cd.setMCSWarranty(MCSWarrantyf);
		cd.setEquipmentSubCL(equipmentSubClf);
		cd.setTraneCL(traneCLf);

		cd.setBuildingPermitCL(buldingPermitCLf);
		cd.setInspectionSOCL(inspectionSOCLf);
		cd.setCertCompletionCL(certCompletionCLf);
	
		
//-------------------------Inspections -------------------------------------------------------------------	
		if(inspections==null)
		{
			inspections = new Inspections();
		}
		
		//set inspection fields
		System.out.println("inspection ticket number " + inspectionTN);
		inspections.setTicketNumber(inspectionTN);
		inspections.setFraming(fframing);
		inspections.setCeiling(fceiling);
		inspections.setRoughin_Mechanical(froughMech);
		inspections.setRoughin_Electric(froughElec);
		inspections.setRoughin_Plumbing(froughPlumb);
		inspections.setMechanicalLightSmoke(fmechLightSmoke);
		inspections.setMechanical_Final(fmechFinal);
		inspections.setElectrical_Final(felecFinal);
		inspections.setPlumbing_Final(fplumbFinal);
		inspections.setFire_Marshal(ffireMarshal);
		inspections.setHealth(fhealth);
		inspections.setBuilding_Final(fbuildFinal);
		
		
		
		//Permit Fields
		Permits	permits = new Permits();
		permits.setBuildingPermitDate(fbuilding_p);
		permits.setElectricalPermitDate(fmechanical_p);
		permits.setFireAlarmPermitDate(felectrical_p);
		permits.setFireSprinklerDate(plumbing_p);
		permits.setLowVoltagePermitDate(ffireSprinler_p);
		permits.setMechanicalPermitDate(ffireAlarm_p);
		permits.setPlumbingPermitDate(flowVoltage_p);
	

		//Set required fields
		Project p = new Project();
		//p.setEqpd(equip.getEqpd());
		p.setMcsNumber(mcsNum);
		p.setProjectClass(projectClass);
		p.addProjectManager(manager);
		p.addSupervisor(supervisor);
		p.setStatus(status);
		p.setWarehouse(warehouse);
		p.setScope(scope);
		p.setProjectItem(item);
		p.setStage(stage);
		p.setProjectType(pType);
		
//***********Set optional fields*******************\\
		
		//objects for different tables\\
		
		p.setInspections(inspections);
		p.setCloseoutDetails(cd);
		p.setPermits(permits);
		//p.setEquipment(equip);

		p.setProjectInitiatedDate(finitiatedDate);
		p.setSiteSurvey(fsurvey);
		p.setCostcoDueDate(fcostco);
		p.setProposalSubmitted(fproposal);
		p.setScheduledStartDate(fstart);
		p.setScheduledTurnover(fscheduled);
		p.setActualTurnover(factual);
		p.setShouldInvoice(shouldInvoice);
		p.setInvoiced(actualInvoice);
		p.setProjectNotes(notes);
		p.setChangeOrders(changeOrders);
		p.setZachUpdates(zachNotes);
		p.setCost(cost);
		p.setCustomerNumber(customerNumber);
		p.setPermitApplication(permitApp);
		
		//Add the project to the database.
		ProjectObjectService.addObject("Project", p);
	
		
		PrintWriter writer = new PrintWriter(file.getAbsoluteFile(),"UTF-8" );
		writer.println(maxID);
		writer.close();
        return "OK";  
	}
	
	/**
	 * This method edits a project that exists in the database. The REQUIRED fields for a project are given as arguments,
	 * while optional fields for a project are parsed from the HTTP request parameter mapping.
	 * 
	 * @param warehouseID the warehouse id
	 * @param managerID the manager id
	 * @param supervisorID the supervisor id
	 * @param classID the class id
	 * @param projectItemID the projectItem id
	 * @param statusID the ProjectStatus id
	 * @param stageID the ProjectStage id
	 * @param scope the scope of the project
	 * @param params the HTTP request parameter mapping
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 */
	public static void editProject(Long warehouseID, Long managerID, Long supervisorID, Long classID, Long projectItemID, Long statusID, Long stageID, Long typeID, String scope, Map<String, String>params, Long inspectionTN, HttpServletRequest req) throws ClassNotFoundException, ParseException
	{
		//Initialize Services
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println("in edit");
		//Get essential project data
		Warehouse warehouse = (Warehouse) ProjectObjectService.get(warehouseID, "Warehouse");
		Person manager = (Person) ProjectObjectService.get(new Long(managerID), "Person");
		Person supervisor = (Person) ProjectObjectService.get(new Long(supervisorID), "Person");
		ProjectClass projectClass = (ProjectClass) ProjectObjectService.get(new Long(classID), "ProjectClass");
		ProjectStatus status = (ProjectStatus) ProjectObjectService.get(new Long(statusID), "ProjectStatus");
		ProjectItem item = (ProjectItem) ProjectObjectService.get(new Long(projectItemID), "ProjectItem");
		ProjectStage stage = (ProjectStage) ProjectObjectService.get(stageID, "ProjectStage");
		ProjectType pType = (ProjectType) ProjectObjectService.get(typeID, "ProjectType");
		
		String mcsNumString = params.get("mcsNumber");

		
		//ID's 
		String iIDString = params.get("inspectionID");
		String closeoutIDString = params.get("closeoutID");
		String salvageIDString = params.get("salvageID");
		String permitsIDString = params.get("permitsID");

		
		int mcsNum = -1;
		Long iID = (long)-1;
		Long closeoutID = (long)-1;
		Long salvageID = (long)-1;
		Long permitsID = (long)-1;

		
		//Parse mcsNumber, change to -1 if it's not a number
		try
		{
			mcsNum = Integer.parseInt(mcsNumString);
			iID = Long.parseLong(iIDString);
			closeoutID = Long.parseLong(closeoutIDString);
			salvageID = Long.parseLong(salvageIDString);
			permitsID = Long.parseLong(permitsIDString);
		

			
		}catch(Exception e){}
		
		//SMALL FIX BUT NOT CORRECT
		
		Inspections inspections = null;
		
		//inspections=(Inspections) ProjectObjectService.get(new Long(iID), "Inspections");
		
		System.out.println("inspection ID : " + iID);
		System.out.println("closeoutID : " + closeoutID);

		
		
		int shouldInvoice = Integer.parseInt(params.get("shouldInvoice"));
		int actualInvoice = Integer.parseInt(params.get("actualInvoice"));
		String notes = params.get("notes");
		
		//Additional fields
		String zachNotes = params.get("zachUpdates");
		String cost = params.get("cost");
		String customerNumber = params.get("customerNumber");
		
		//Parse change orders from strong
		String changeOrderJsonString = params.get("coItems");
		ChangeOrderService orderService = new ChangeOrderService();
		HashSet<ChangeOrder> changeOrders = ChangeOrderService.getChangeOrdersFromString(changeOrderJsonString);
		
		Date fsalvageDate = null;
		Date finitiatedDate = null;
		Date fsurvey = null;
		Date fcostco = null;
		Date fproposal = null;
		Date fasBuilts = null;
		Date fpunchList = null;
		Date falarmHvac = null;
		Date fverisae = null;
		Date fcloseoutBook = null;
		Date fcloseoutNotes = null;
		Date fstart = null;
		Date fscheduled = null;
		Date factual = null;
		Date fairGas = null;
		Date fpermits = null;
		Date permitApp = null;
		
		Date fframing = null ; 
		Date fceiling = null; 
		Date froughMech = null; 
		Date froughElec= null;  
		Date froughPlumb = null; 
		Date fmechLightSmoke = null; 
		Date fmechFinal = null; 
		Date  felecFinal = null; 
		Date fplumbFinal = null; 
		Date ffireMarshal = null; 
		Date fhealth = null; 
		Date fbuildFinal = null; 
		
		//Permits
		Date fbuilding_p =null;
		Date fmechanical_p = null;
		Date felectrical_p = null;
		Date plumbing_p = null;
		Date ffireSprinler_p = null;
		Date ffireAlarm_p = null;
		Date flowVoltage_p = null;
		
		//closeout
		Date mPunchListCLf =null;
		Date closeoutPhotosCLf=null;
		Date subConWarrantiesCLf =null;
		Date MCSWarrantyf =null;
		Date equipmentSubClf =null;
		Date traneCLf=null;
		Date frontPagef=null;
		Date subContractorCLf = null;
		Date buldingPermitCLf = null;
		Date inspectionSOCLf = null;
		Date certCompletionCLf = null;

		
		//assign values to dates, if they are not null
		if (!(params.get("salvageDate")).isEmpty())
			fsalvageDate = formatter.parse(params.get("salvageDate"));
		
		if (!(params.get("initiated")).isEmpty())
			finitiatedDate = formatter.parse(params.get("initiated"));
		
		if (!(params.get("survey")).isEmpty())
			fsurvey = formatter.parse(params.get("survey"));
		
		if (!(params.get("costco")).isEmpty())
			fcostco = formatter.parse(params.get("costco"));
		
		if (!(params.get("proposal")).isEmpty())
			fproposal = formatter.parse(params.get("proposal"));
		
//********************CLOSEOUTDETAILS OPTIONAL**************************\\
		
		if (!params.get("closeoutNotes").isEmpty())
			fcloseoutNotes = formatter.parse(params.get("closeoutNotes"));
		
		if (!(params.get("asBuilts")).isEmpty())
			fasBuilts = formatter.parse(params.get("asBuilts"));
		
		if (!(params.get("punchList")).isEmpty())
			fpunchList = formatter.parse(params.get("punchList"));
		
		if (!(params.get("alarmHvac")).isEmpty())
			falarmHvac = formatter.parse(params.get("alarmHvac"));
		
		if (!params.get("verisae").isEmpty())
			fverisae = formatter.parse(params.get("verisae"));
		
		if (!params.get("startDate").isEmpty())
			fstart = formatter.parse(params.get("startDate"));
		
		if (!params.get("scheduledTurnover").isEmpty())
			fscheduled = formatter.parse(params.get("scheduledTurnover"));
		
		if (!params.get("actualTurnover").isEmpty())
			factual = formatter.parse(params.get("actualTurnover"));

		if (!params.get("airGas").isEmpty())
			fairGas = formatter.parse(params.get("airGas"));
		
		if (!params.get("permits").isEmpty())
			fpermits = formatter.parse(params.get("permits"));
		
		if (!params.get("permitApp").isEmpty())
			permitApp = formatter.parse(params.get("permitApp"));
		
// ************************Optional Inspections************************ \\
		if(!params.get("framing").isEmpty()) 
			fframing = formatter.parse(params.get("framing"));
				
		if(!params.get("ceiling").isEmpty()) 
			fceiling = formatter.parse(params.get("ceiling"));
				
		if(!params.get("roughMech").isEmpty()) 
			froughMech = formatter.parse(params.get("roughMech"));
				
		if(!params.get("roughElec").isEmpty()) 
			froughElec = formatter.parse(params.get("roughElec"));
			
		if(!params.get("roughPlumb").isEmpty()) 
			froughPlumb = formatter.parse(params.get("roughPlumb"));
		
		if(!params.get("mechLightSmoke").isEmpty()) 
		{
			fmechLightSmoke = formatter.parse(params.get("mechLightSmoke"));
		}
		
		if(!params.get("mechFinal").isEmpty()) 
			fmechFinal = formatter.parse(params.get("mechFinal"));
				
		if(!params.get("elecFinal").isEmpty()) 
			felecFinal = formatter.parse(params.get("elecFinal"));
				
		if(!params.get("plumbFinal").isEmpty()) 
			fplumbFinal = formatter.parse(params.get("plumbFinal"));
				
		if(!params.get("fireMarshal").isEmpty()) 
			ffireMarshal = formatter.parse(params.get("fireMarshal"));
				
		if(!params.get("health").isEmpty()) 
			fhealth = formatter.parse(params.get("health"));
						
		if(!params.get("buildFinal").isEmpty()) 
			fbuildFinal = formatter.parse(params.get("buildFinal"));
		
		
		// ***************Optional Permits************************ \\
		if(!params.get("building_p").isEmpty()) 
			fbuilding_p = formatter.parse(params.get("building_p"));
		
		if(!params.get("mechanical_p").isEmpty()) 
			fmechanical_p = formatter.parse(params.get("mechanical_p"));
		
		if(!params.get("electrical_p").isEmpty()) 
			felectrical_p = formatter.parse(params.get("electrical_p"));
		
		if(!params.get("plumbing_p").isEmpty()) 
			plumbing_p = formatter.parse(params.get("plumbing_p"));
		
		if(!params.get("fireSprinkler_p").isEmpty()) 
			ffireSprinler_p = formatter.parse(params.get("fireSprinkler_p"));
		
		if(!params.get("fireAlarm_p").isEmpty()) 
			ffireAlarm_p = formatter.parse(params.get("fireAlarm_p"));
		
		if(!params.get("lowVoltage_p").isEmpty()) 
			flowVoltage_p = formatter.parse(params.get("lowVoltage_p"));
		
		
		//CloseoutLIST
		if(!params.get("buildingPermitCL").isEmpty()) 
			buldingPermitCLf = formatter.parse(params.get("buildingPermitCL"));
		if(!params.get("inspectionSOCL").isEmpty()) 
			inspectionSOCLf = formatter.parse(params.get("inspectionSOCL"));
		if(!params.get("certCompletionCL").isEmpty()) 
			certCompletionCLf = formatter.parse(params.get("certCompletionCL"));
		if(!params.get("mPunchListCL").isEmpty()) 
			mPunchListCLf = formatter.parse(params.get("mPunchListCL"));
		if(!params.get("closeoutPhotosCL").isEmpty()) 
			closeoutPhotosCLf = formatter.parse(params.get("closeoutPhotosCL"));
		if(!params.get("subConWarrantiesCL").isEmpty()) 
			subConWarrantiesCLf = formatter.parse(params.get("subConWarrantiesCL"));
		if(!params.get("MCSWarranty").isEmpty()) 
			MCSWarrantyf = formatter.parse(params.get("MCSWarranty"));
		if(!params.get("equipmentSubCL").isEmpty()) 
			equipmentSubClf  = formatter.parse(params.get("equipmentSubCL"));
		if(!params.get("traneCL").isEmpty()) 
			traneCLf = formatter.parse(params.get("traneCL"));
		



		
		
		
		Long id = Long.parseLong(params.get("projectID"));
		String[] equipToAdd = getGSONArray(req, "newEquip");
		
		
		if(equipToAdd.length!=0)
		{
		System.out.println("in equipment");
		// ---------------------------Receiving ARRAYS -----------------------------------------------
			
		String[][] equipProj = getGSON2DArray(req, "project_eq");
		String[][] equipComponent = getGSON2DArray(req, "component_eq");
		String[][] equipVendor = getGSON2DArray(req, "vendor_eq");

	 	// 2D arrays
		String[] equipIDS = getGSONArray(req, "equipIDS");
		String[] equipPO = getGSONArray(req, "po_eq");
		String[] equipEDD = getGSONArray(req, "estimatedDeliveryDate_eq");
		String[] equipName  = getGSONArray(req, "equipName"); 
		String[] equipNotes = getGSONArray(req, "notes_eq");
		String[] equipStatus = getGSONArray(req, "status_eq");
		
		
		// Creating new converted arrays
		long longEquipPO[] = new long[equipToAdd.length];
		Date[] dateEquipEDD = new Date[equipToAdd.length];
		int[] array = new int[equipToAdd.length];
		long[] longEquipIds = new long[equipIDS.length];
		System.out.println("equip length" + array.length);
		
		
		//-------------------------------------------------------------------------------------------------
		
		//Equipment Objects
		Equipment equip;
		Warehouse warehouse_eq;
		ProjectItem item_eq;
		EquipmentVendor vendor_eq;
		EquipmentStatus status_eq;
		
		DateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

		
		for(int i=0; i<equipToAdd.length;i++)
			{
			equip = new Equipment();
			// Convert Arrays to either int, long or Date
			array[i] =(int) Long.parseLong(equipToAdd[i]);
			
			//crashing
			if(!equipPO[array[i]].equals(""))
			{
				longEquipPO[i] = new Long(equipPO[array[i]]);
			}
			
			if(!equipEDD[array[i]].equals(""))
			{
				System.out.println("estimated date " + equipEDD[array[i]]);
				dateEquipEDD[i] = format1.parse(equipEDD[array[i]]) ;
			}
			
			 warehouse_eq = (Warehouse) ProjectObjectService.get(Long.parseLong(equipProj[array[i]][2]), "Warehouse");	
			 item_eq = (ProjectItem) ProjectObjectService.get(Long.parseLong(equipComponent[array[i]][1]), "ProjectItem");
			 vendor_eq = (EquipmentVendor) ProjectObjectService.get(new Long(equipVendor[array[i]][1]), "EquipmentVendor");
			 status_eq = (EquipmentStatus) ProjectObjectService.get(new Long(equipStatus[array[i]]), "EquipmentStatus");
			 
				longEquipIds[i] = new Long(equipIDS[array[i]]);
				System.out.println("equip ids" + longEquipIds[i]);
			
			 	// 2D arrays
			 
				equip.setWarehouse(warehouse_eq);
				equip.setEquipmentVendor(vendor_eq);
				equip.setProjectItem(item_eq);
				equip.setEquipStatus(status_eq);
				
				//Regular Arrays
				equip.setPO(longEquipPO[i]);
				equip.setEstimatedDelivery(dateEquipEDD[i]);
				equip.setEquipName(equipName[array[i]]);
				equip.setNotes(equipNotes[array[i]]);
				equip.setEqpd(id);
				
				if((Equipment) ProjectObjectService.get(longEquipIds[i], "Equipment")!=null)
					ProjectObjectService.editObject("Equipment",longEquipIds[i],equip, 0);
				else
				{
					System.out.println("here");
					ProjectObjectService.addObject("Equipment", equip);
				}
				
				
				
				}
		}
		
		Double salvageAmount = null;
		try
		{
			salvageAmount = Double.parseDouble(params.get("salvageAmount"));
		}catch(NumberFormatException nfe){}
        
        //Edit salvage values and closeout details
		SalvageValue sv = null;
		
		//CloseoutDetails cd = (CloseoutDetails) ProjectObjectService.get(closeoutID, "CloseoutDetails");
		CloseoutDetails cd = new CloseoutDetails();
		
		if (salvageAmount != null && !params.get("salvageDate").isEmpty())
		{
			//If the salvageValue doesn't exist, make one
			if (cd.getSalvageValue() == null)
			{
				sv = new SalvageValue(fsalvageDate, salvageAmount);
			}
		}
		//ID's
		System.out.println("Salvage ID: " + salvageID);

		
		//Create CloseoutDetails Object.
				
		//Closeout fields
		cd.setPunchList(fpunchList);
		cd.setAsBuilts(fasBuilts);
		cd.setAirGas(fairGas);
		cd.setAlarmHvacForm(falarmHvac);
		cd.setCloseoutBook(fcloseoutBook);
		cd.setCloseoutNotes(fcloseoutNotes);
		cd.setPermitsClosed(fpermits);
		cd.setPunchList(fpunchList);
		cd.setVerisaeShutdownReport(fverisae);
		cd.setSalvageValue(sv);
		
		cd.setMPunchListCL(mPunchListCLf) ;
		cd.setCloseoutPhotosCL(closeoutPhotosCLf);
		cd.setSubConWarrantiesCL(subConWarrantiesCLf);
		cd.setMCSWarranty(MCSWarrantyf);
		cd.setEquipmentSubCL(equipmentSubClf);
		cd.setTraneCL(traneCLf);

		cd.setBuildingPermitCL(buldingPermitCLf);
		cd.setInspectionSOCL(inspectionSOCLf);
		cd.setCertCompletionCL(certCompletionCLf);
				
		
		//create inspections Object.
		if(inspections==null)
		{
			 inspections = new Inspections();
			 System.out.println("Inpsections was empty in edit");
		}		
				//set inspection fields
		System.out.println(inspectionTN);
		inspections.setTicketNumber(inspectionTN);
		inspections.setFraming(fframing);
		inspections.setCeiling(fceiling);
		inspections.setRoughin_Mechanical(froughMech);
		inspections.setRoughin_Electric(froughElec);
		inspections.setRoughin_Plumbing(froughPlumb);
		inspections.setMechanicalLightSmoke(fmechLightSmoke);
		inspections.setMechanical_Final(fmechFinal);
		inspections.setElectrical_Final(felecFinal);
		inspections.setPlumbing_Final(fplumbFinal);
		inspections.setFire_Marshal(ffireMarshal);
		inspections.setHealth(fhealth);
		inspections.setBuilding_Final(fbuildFinal);
		
		
		Permits	permits = new Permits();
		permits.setBuildingPermitDate(fbuilding_p);
		permits.setElectricalPermitDate(felectrical_p);
		permits.setFireAlarmPermitDate(ffireAlarm_p);
		permits.setFireSprinklerDate(ffireSprinler_p);
		permits.setLowVoltagePermitDate(flowVoltage_p);
		permits.setMechanicalPermitDate(fmechanical_p);
		permits.setPlumbingPermitDate(plumbing_p);
		


		
		//Create new project to replace the old one
		Project p = new Project();
		p.setMcsNumber(mcsNum);
		p.setProjectClass(projectClass);
		p.addProjectManager(manager);
		p.addSupervisor(supervisor);
		p.setStatus(status);
		p.setWarehouse(warehouse);
		p.setScope(scope);
		p.setProjectItem(item);
		p.setStage(stage);
		p.setProjectInitiatedDate(finitiatedDate);
		p.setSiteSurvey(fsurvey);
		p.setCostcoDueDate(fcostco);
		p.setProposalSubmitted(fproposal);
		p.setScheduledStartDate(fstart);
		p.setScheduledTurnover(fscheduled);
		p.setActualTurnover(factual);
		p.setShouldInvoice(shouldInvoice);
		p.setInvoiced(actualInvoice);
		p.setProjectNotes(notes);
		p.setChangeOrders(changeOrders);
		p.setProjectType(pType);
		p.setZachUpdates(zachNotes);
		p.setCost(cost);
		p.setCustomerNumber(customerNumber);
		p.setPermitApplication(permitApp);
		p.setCloseoutDetails(cd);
		p.setInspections(inspections);
		p.setPermits(permits);
				

		//Replace the old project with the new project.
		
		System.out.println("Project ID : " + id);
		
		
		System.out.println("SV number: " + salvageID);
		System.out.println("PERMITS number: " + permitsID); 
	//	long[] iDs = {iID,/*closeoutID,*/id} ;
	//	String[] domains = {"Inspections",/*"CloseoutDetails",*/"Project" };
	//  ProjectObject[] objects ={inspections, /*cd,*/ p};
				
		
	int i = 0;
	int k = 0;
		if(iID!=0)
		{
			 i = 0;
			ProjectObjectService.editObject("Inspections",iID,inspections, i);	
			k = 1;
		}
		if(closeoutID!=0 && salvageID ==0)
		{	
			i=0;
			ProjectObjectService.editObject("CloseoutDetails",closeoutID,cd,i);
			k=1;
		}
		if(closeoutID!=0 && salvageID !=0)
		{	
			i=2;
			ProjectObjectService.editObject("CloseoutDetails",closeoutID,cd,i);
			k=1;
		}
		
		if(salvageID!=0)
		{
			i=0;
			System.out.println("SV number: " + salvageID);
			ProjectObjectService.editObject("SalvageValue", salvageID, sv, i);
			k = 1;
		}
		if(permitsID!=0)
		{
			i=0;
			ProjectObjectService.editObject("Permits",permitsID,permits, i);	
			k = 1;
		}
		
		
		 System.out.println("k = " + k);
			ProjectObjectService.editObject("Project",id,p,k);
			//ProjectObjectService.editObjects(iDs,domains, objects);


		System.out.println("END");

		
	}
	
	/**
	 * This method gets all of the enumerated data that is needed by the dropdowns for the
	 * editSelect.html page
	 * @return a string representing a JSON array of data containing this data
	 */
	public static String getEditEnumsAsJSON()
	{
		Gson g = new Gson();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("warehouse", ProjectObjectService.getAllAsJsonString("Warehouse"));
		map.put("stage", ProjectObjectService.getAllAsJsonString("ProjectStage"));
		map.put("item", ProjectObjectService.getAllAsJsonString("ProjectItem"));
		map.put("class", ProjectObjectService.getAllAsJsonString("ProjectClass"));
		map.put("inspections", ProjectObjectService.getAllAsJsonString("Inspections"));
		map.put("permits",ProjectObjectService.getAllAsJsonString("Permits"));
		map.put("equipmentvendor",ProjectObjectService.getAllAsJsonString("EquipmentVendor"));
		
		
		return g.toJson(map);
	}
	
	/**
	 * This method returns the data needed for the dropdowns on the query.html page
	 * @return a string repsenting a JSON array containing the data
	 */
	public static String getQueryEnumsAsJSON()
	{
		Gson g = new Gson();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("warehouse", ProjectObjectService.getAllAsJsonString("Warehouse"));
		map.put("stage", ProjectObjectService.getAllAsJsonString("ProjectStage"));
		map.put("item", ProjectObjectService.getAllAsJsonString("ProjectItem"));
		map.put("class", ProjectObjectService.getAllAsJsonString("ProjectClass"));
		map.put("region", g.toJson(Region.returnAllAsList()));
		map.put("status", ProjectObjectService.getAllAsJsonString("ProjectStatus"));
		map.put("person", ProjectObjectService.getAllAsJsonString("Person"));
		map.put("type", ProjectObjectService.getAllAsJsonString("ProjectType"));
		map.put("inspections", ProjectObjectService.getAllAsJsonString("Inspections"));
		map.put("permits",ProjectObjectService.getAllAsJsonString("Permits"));
		map.put("equipmentvendor",ProjectObjectService.getAllAsJsonString("EquipmentVendor"));
		map.put("equipmentstatus",ProjectObjectService.getAllAsJsonString("EquipmentStatus"));
	
		
		//Turn the hashmap into a JSON array and return it
		return g.toJson(map);
	}
	
	/**
	 * This method gets all of the information in the database.
	 * @return A string representing a JSON array containing this information
	 */
	public static String getAllEnumsAsJson()
	{
		Gson g = new Gson();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("warehouse", ProjectObjectService.getAllAsJsonString("Warehouse"));
		map.put("stage", ProjectObjectService.getAllAsJsonString("ProjectStage"));
		map.put("item", ProjectObjectService.getAllAsJsonString("ProjectItem"));
		map.put("class", ProjectObjectService.getAllAsJsonString("ProjectClass"));
		map.put("status", ProjectObjectService.getAllAsJsonString("ProjectStatus"));
		map.put("person", ProjectObjectService.getAllAsJsonString("Person"));
		map.put("type", ProjectObjectService.getAllAsJsonString("ProjectType"));
		map.put("inspections", ProjectObjectService.getAllAsJsonString("Inspections"));
		map.put("permits",ProjectObjectService.getAllAsJsonString("Permits"));
		map.put("equipmentvendor",ProjectObjectService.getAllAsJsonString("EquipmentVendor"));	
		map.put("equipment",ProjectObjectService.getAllAsJsonString("Equipment"));
		map.put("equipmentstatus",ProjectObjectService.getAllAsJsonString("EquipmentStatus"));
		
		return g.toJson(map);
	}
	
	public static String getAllEnumsEquipAsJson()
	{
		Gson g = new Gson();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("person", ProjectObjectService.getAllAsJsonString("Person"));
		map.put("warehouse", ProjectObjectService.getAllAsJsonString("Warehouse"));
		map.put("item", ProjectObjectService.getAllAsJsonString("ProjectItem"));
		map.put("equipmentvendor",ProjectObjectService.getAllAsJsonString("EquipmentVendor"));	
		map.put("equipment",ProjectObjectService.getAllAsJsonString("Equipment"));
		map.put("equipmentstatus",ProjectObjectService.getAllAsJsonString("EquipmentStatus"));
		
		return g.toJson(map);
	}
	
	public static String getAllEquipmentAsJson()
	{
		Gson g = new Gson();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("equipment",ProjectObjectService.getAllAsJsonString("Equipment"));
		return g.toJson(map);
	}
	
	public static String[][] getGSON2DArray(HttpServletRequest req, String var)
	{
		Gson gson = new Gson();
		String[][] dummy = new String[0][0];  // The same type as your "newMap"
		String[][] array;
		
		array = gson.fromJson(req.getParameter(var), dummy.getClass());
		return array;	
	}
	
	public static String[] getGSONArray(HttpServletRequest req, String var)
	{
		Gson gson = new Gson();
		String[] dummy = new String[0];  // The same type as your "newMap"
		String[] array;
		
		array = gson.fromJson(req.getParameter(var), dummy.getClass());
		
		return array;	
	}
	
	
	
}
