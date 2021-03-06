package Servlets;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.RequestHandler;
import services.TriggerService;

/**
 * Servlet implementation class Trigger
 */
@WebServlet(description = "This servlet handles requests for Project Triggers", urlPatterns = { "/Trigger" })
public class Trigger extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	PrintWriter out;
       
    public Trigger() 
    {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		//Initialize the response
		resp.setContentType("application/json");
		out = resp.getWriter();
		String response = "";
		String action = req.getParameter("action");
		
		if (action.equals("getTriggers"))
		{
			TriggerService ts = new TriggerService();
			response = ts.getAllTriggersAsJson();
		}
		else if(action.equals("getProjectTriggers"))
		{
			TriggerService ts = new TriggerService(Long.parseLong(req.getParameter("project_id")));
			response = ts.getAllSpecificTriggersAsJson();
		}
		
		out.print(response);
	}

}
