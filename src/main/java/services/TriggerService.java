package services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objects.Trigger;
import projectObjects.Project;

public class TriggerService 
{
	/**
	 * This holds all of the triggers of the system
	 */
	ArrayList<Trigger> triggers;
	
	Long projectNum;
	
	public TriggerService()
	{
		triggers = new ArrayList<Trigger>();
		initializeTriggers();
	}
	
	public TriggerService(long pID)
	{
		triggers = new ArrayList<Trigger>();
		projectNum = pID;
		initializeTriggers();
	}
	
	/**
	 * This method creates all of the triggers
	 */
	public void initializeTriggers()
	{
		triggers.add(getInvoiceTrigger());
		triggers.add(getUnassignedMCS());
		triggers.add(costcoDueDateInfo());
		triggers.add(costcoDueDateWarning());
		triggers.add(costcoDueDateSevere());
		triggers.add(startDateInfo());
		triggers.add(startDateWarning());
		triggers.add(startDateSevere());
		triggers.add(scheduledTurnOverInfo());
		triggers.add(scheduledTurnOverWarning());
		triggers.add(scheduledTurnOverSevere());
	}
	
	/**
	 * This method creates a Trigger that is fired when a project's "should Invoice" field is greater
	 * than a project's "invoiced" field.
	 * @return the Invoice Trigger
	 */
	public Trigger getInvoiceTrigger()
	{
		String desc = "Should Invoice/Actual Invoice Mismatch";
		Trigger s = new Trigger(1, desc);
		s.addExpression(Restrictions.sqlRestriction("(shouldInvoice - invoiced) != 0"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects have an MCS number that is -1
	 * @return the MCSNumber trigger
	 */
	public Trigger getUnassignedMCS()
	{
		String desc = "MCS Number not assigned";
		Trigger s = new Trigger(1, desc);
		s.addExpression(Restrictions.sqlRestriction("mcsNumber = -1 AND stage_id = 2"));
		return s;
	}

	/**
	 * This method creates a trigger that is fired when any projects are starting in the next 2 weeks 
	 * @return the timeElapsed
	 */
	public Trigger startDateInfo()
	{
		String desc = "Project is starting soon!";
		Trigger s = new Trigger(0, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledStartDate,INTERVAL 14 DAY) and DATE_SUB(scheduledStartDate,INTERVAL 7 DAY)"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects are starting in the next 1 week 
	 * @return the timeElapsed
	 */
	public Trigger startDateWarning()
	{
		String desc = "Project is starting soon!";
		Trigger s = new Trigger(1, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledStartDate,INTERVAL 6 DAY) and DATE_SUB(scheduledStartDate,INTERVAL 3 DAY)"));
		return s;
	}
	
	/**	
	 * This method creates a trigger that is fired when any projects are starting in the next 3 days 
	 * @return the timeElapsed
	 */
	public Trigger startDateSevere()
	{
		String desc = "Project is starting soon!";
		Trigger s = new Trigger(2, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledStartDate,INTERVAL 2 DAY) and scheduledStartDate"));
		return s;
	}
	
	public Trigger costcoDueDateInfo()
	{
		String desc = "Costco due date is soon!";
		Trigger s = new Trigger(0, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(costcoDueDate,INTERVAL 7 DAY) and DATE_SUB(costcoDueDate,INTERVAL 4 DAY)"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects are starting in the next 1 week 
	 * @return the timeElapsed
	 */
	public Trigger costcoDueDateWarning()
	{
		String desc = "Costco due date is soon!";
		Trigger s = new Trigger(1, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(costcoDueDate,INTERVAL 3 DAY) and DATE_SUB(costcoDueDate,INTERVAL 2 DAY)"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects are starting in the next day 
	 * @return the timeElapsed
	 */
	public Trigger costcoDueDateSevere()
	{
		String desc = "Costco due date is soon!";
		Trigger s = new Trigger(2, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(costcoDueDate,INTERVAL 1 DAY) and costcoDueDate"));
		return s;
	}
	
	public Trigger scheduledTurnOverInfo()
	{
		String desc = "Turn over is soon!";
		Trigger s = new Trigger(0, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledTurnover,INTERVAL 7 DAY) and DATE_SUB(scheduledTurnover,INTERVAL 4 DAY)"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects are starting in the next 1 week 
	 * @return the timeElapsed
	 */
	public Trigger scheduledTurnOverWarning()
	{
		String desc = "Turn over is soon!";
		Trigger s = new Trigger(1, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledTurnover,INTERVAL 3 DAY) and DATE_SUB(scheduledTurnover,INTERVAL 2 DAY)"));
		return s;
	}
	
	/**
	 * This method creates a trigger that is fired when any projects are starting in the next day 
	 * @return the timeElapsed
	 */
	public Trigger scheduledTurnOverSevere()
	{
		String desc = "Turn over is soon!";
		Trigger s = new Trigger(2, desc);
		s.addExpression(Restrictions.sqlRestriction("CURDATE() between DATE_SUB(scheduledTurnover,INTERVAL 1 DAY) and scheduledTurnover"));
		return s;
	}
	
	/**
	 * This method returns all of the project triggers as JSON variables
	 * @return A string representing the triggers as Json objects
	 */
	public String getAllTriggersAsJson()
	{
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		
		for (int i = 0; i < triggers.size(); i++)
		{
			triggers.get(i).runTrigger();
		}
		
		return gson.toJson(triggers);
	}
	
	/**
	 * This method returns all of the project triggers as JSON variables
	 * @return A string representing the triggers as Json objects
	 */
	public String getAllSpecificTriggersAsJson()
	{
		Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
		for (int i = 0; i < triggers.size(); i++)
		{
			triggers.get(i).runCertainTrigger(projectNum);
		}
		
		return gson.toJson(triggers);
	}
}
