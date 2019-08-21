package leClient;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ViewerServlet
 */
@WebServlet(description = "Used to Create the View Page for the Logo Encoder's Results", urlPatterns = { "/ViewerServlet" })
public class ViewerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private String baseDir = "images/";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		
		req.getSession().setAttribute("orig",baseDir+id+"_orig.jpg");
		req.getSession().setAttribute("enc",baseDir+id+"_encoded.jpg");
		req.getSession().setAttribute("dec",baseDir+id+"_decoded.jpg");
		req.getSession().setAttribute("logo",baseDir+id+"_logo.jpg");
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/pages/viewer.jsp");
		rd.forward(req, resp);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
