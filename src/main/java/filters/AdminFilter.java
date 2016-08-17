package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class is responsible for ensuring that users may only access the critical parts of the application if
 * they are logged in.
 */
@WebFilter(description = "This class intercepts any requests for a page in the application, and checks if the current session contains the user's login information", urlPatterns = { "/AdminFilter" })
public class AdminFilter implements Filter 
{
	final private String ADMIN_URL = "/miller/admin.html";
	final private String HOMEPAGE_URL = "/miller/homepage.html";
	final private String LOGIN_SERVLET = "/miller/Admin";
    private ServletContext context;
	
    public AdminFilter() 
    {
    	
    }
    
    public void init(FilterConfig fConfig) throws ServletException 
    {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

	public void destroy() 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * This method checks if the session variable contains 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		System.out.println("ITS WORKING!!!!!!!!!");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		
		HttpSession session = req.getSession(false);
		if ((session.getAttribute("user") != "brian" && uri.equals(ADMIN_URL) && uri.equals(LOGIN_SERVLET)) || session == null)
		{
			System.out.println(uri);
			resp.sendRedirect(HOMEPAGE_URL);
			return;
		}
		
		chain.doFilter(request, response);
	}
}
