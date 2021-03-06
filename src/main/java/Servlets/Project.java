package Servlets;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import objects.RequestHandler;
import services.ChangeOrderService;
import services.EquipmentService;
import services.ProjectObjectService;
import services.ProjectService;
import services.QueryService;
import services.CloseoutListService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;


@WebServlet("/Project")
public class Project extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private static PrintWriter out;
       
    public Project() 
    {
        super();
    }

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		resp.setContentType("application/json");
		out = resp.getWriter();
		String response = "";
		Map<String, String> parameters = RequestHandler.getParameters((req.getParameterMap()));
		
		//Get the domain and desired action
		String domain = parameters.get("domain");
		String action = parameters.get("action");
		ProjectService ps = new ProjectService();
		
		if (action.equals("getAllObjects"))
		{
			System.out.println("get All Objects");
			response = ps.getAllEnumsAsJson();
			
		}

		else if (action.equals("getEditQueryObjects"))
		{
			System.out.println("qenum");
			response = ps.getEditEnumsAsJSON();
		}
		else if (action.equals("getProjectToEdit"))
		{
			System.out.println("getEdit");
			String warehouse = (parameters.get("warehouse"));
			String stage = (parameters.get("stage"));
			String classID = (parameters.get("class"));
			String itemID = (parameters.get("item"));
			String projectID = (parameters.get("id"));
			//add a search by permit
			//String inspections= (parameters.get("inspections"));
			response = QueryService.getProjectToEdit(warehouse, stage, classID, itemID, projectID);
		}
		else if (action.equals("getEditableProject"))
		{
			System.out.println("getEditableProject");
			String warehouse = (parameters.get("whID"));
			String stage = (parameters.get("stageID"));
			String classID = (parameters.get("classID"));
			String itemID = (parameters.get("itemID"));
			String projectID = (parameters.get("projectID"));
			String inspections=(parameters.get("inspections"));
			response = QueryService.getProjectToEdit(warehouse, stage, classID, itemID, projectID);
		}
		else if (action.equals("add"))
		{
			System.out.println("add");
			Long inspections;
			Long warehouse = Long.parseLong(parameters.get("warehouse"));
			Long projectClass = Long.parseLong(parameters.get("class"));
			Long projectItem = Long.parseLong(parameters.get("projectItem"));
			Long manager = Long.parseLong(parameters.get("manager"));
			Long supervisor = Long.parseLong(parameters.get("supervisor"));
			Long status = Long.parseLong(parameters.get("status"));
			Long stage = Long.parseLong(parameters.get("stage"));
			String scope = parameters.get("scope");
			Long pType = Long.parseLong(parameters.get("pType"));	
		
			if((String) parameters.get("inspections")=="")		
				inspections = (long) -1;
			else
				inspections =Long.parseLong(parameters.get("inspections"));
						
			try 
			{ 	
				ps.addProject(warehouse, manager, supervisor, projectClass, projectItem, status, stage, pType, scope, parameters,inspections, req);
					
			} catch (ClassNotFoundException | ParseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (action.equals("edit"))
		{
			System.out.println("edit");
			Long inspections;
			Long warehouse = Long.parseLong(parameters.get("warehouse"));
			Long projectClass = Long.parseLong(parameters.get("class"));
			Long projectItem = Long.parseLong(parameters.get("projectItem"));
			Long manager = Long.parseLong(parameters.get("manager"));
			Long supervisor = Long.parseLong(parameters.get("supervisor"));
			Long status = Long.parseLong(parameters.get("status"));
			Long stage = Long.parseLong(parameters.get("stage"));
			//Long inspections= Long.parseLong(parameters.get("inspections"));
			String scope = parameters.get("scope");
			Long pType = Long.parseLong(parameters.get("pType"));
 
			
			if((String) parameters.get("inspections")=="")		
				inspections = (long) -1;
			else
				inspections =Long.parseLong(parameters.get("inspections"));
			
			try 
			{
				    System.out.println("inspections in edit: " + inspections);
				    ps.editProject(warehouse, manager, supervisor, projectClass, projectItem, status, stage, pType, scope, parameters,inspections, req);
			  
			} catch (ClassNotFoundException | ParseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		else if (action.equals("get"))
		{
			System.out.println("GET");
			System.out.println(response);
			try 
			{
				response = (String) ProjectObjectService.getAsJSON(Long.parseLong(parameters.get("id")), "Project");
				System.out.println("project: "+response);
			} 
			catch (NumberFormatException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (action.equals("getQueryEnums"))
		{
			response = ProjectService.getQueryEnumsAsJSON();
		}
		else if (action.equals("getAllEquipmentObjects"))
		{
			response = ps.getAllEnumsEquipAsJson();
		}
		/*
		else if(action.equals("retrieveEquipment"))
		{	
			response = EquipmentService.retrieveProjectEquipmentSet(parameters.get("id"));
		}*/
	/*	
		else if(action.equals("addEquipment"))
		{
			EquipmentService.addEquipment(parameters.get("equipName"));
		}
		
		else if(action.equals("addVendor"))
		{
			EquipmentService.addVendor(parameters.get("vendorName"));
		}
		*/
		/*else if(action.equals("getDropdownNames"))
		{
			if(parameters.get("getType").equals("equips"))
			{
				response = EquipmentService.getAllEquipssAsJson();	
			}
			else if(parameters.get("getType").equals("vendors"))
			{
				
				response = EquipmentService.getAllVendorsAsJson();
			}*/
			
		
		/*
		else if(action.equals("setEquipment"))
		{
			System.out.println("in equipment");
			EquipmentService.setEquipment(parameters.get("equipment"),parameters.get("projectID"));
		}
		*/
		else if(action.equals("setCloseoutList"))
		{
			System.out.println("in SetCLOSEOUT LIST");
			CloseoutListService.setCheckList(Long.parseLong(parameters.get("id")),new String[]{parameters.get("asBuilts"),parameters.get("punchList"),parameters.get("permits"),parameters.get("closeOutPhoto"),parameters.get("revisions"),parameters.get("mechanicalInspection"),parameters.get("electricInspection"),parameters.get("plumbingInspection"),parameters.get("fireSprinklerInspection"),parameters.get("ansulInspection"),parameters.get("buildingInspection"),parameters.get("alarmForm"),
												parameters.get("hvacShutDown"),parameters.get("airGas"),parameters.get("hvacForm"),parameters.get("salvageValue"),parameters.get("mulvannyPunchList"),parameters.get("substantialCompletion"),parameters.get("subcontractorWarranty"),parameters.get("mcsWarranty"),parameters.get("lienRelease"),parameters.get("confirmCOs"),parameters.get("g704"),parameters.get("g706"),parameters.get("g706a")});
		}
		System.out.println(response);
		out.println(response);
		
		
	}
	
}
