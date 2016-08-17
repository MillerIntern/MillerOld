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
@WebFilter(description = "This class intercepts any requests for a page in the application, and checks if the current session contains the user's login information", urlPatterns = { "/LoginFilter" })
public class LoginFilter implements Filter 
{
	final private String LOGIN_URL = "/miller/index.html";
	final private String HEADER_URL = "/miller/header.html";
	final private String FOOTER_URL = "/miller/footer.html";
	final private String LOGIN_SERVLET = "/miller/Login";
    private ServletContext context;
	
    public LoginFilter() 
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
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		
		HttpSession session = req.getSession(false);
		if (session == null && !uri.equals(LOGIN_URL) 
				&& !uri.equals(HEADER_URL)
				&& !uri.equals(FOOTER_URL)
				&& !uri.equals(LOGIN_SERVLET)
				&& !uri.contains("/js") 
				&& !uri.contains("/css"))
		{
			System.out.println(uri);
			resp.sendRedirect(LOGIN_URL);
			return;
		}
		
		chain.doFilter(request, response);
	}
}
