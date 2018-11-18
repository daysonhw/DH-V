import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class HeaderDemo extends HttpServlet {
    protected void processRequeset(HttpServletRequest request,
                                   HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<html>");
        printWriter.println("<head>");
        printWriter.println("<title>Servlet ShowHeader</title>");
        printWriter.println("</head>");
        printWriter.println("<body>");
        printWriter.println("<h1>Servlet Show Header at " +
                request.getContextPath() + "</h1>");
        Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String param = (String) enumeration.nextElement();
            printWriter.println(param + ":" +
                    request.getHeader(param) + "<br>");
        }
        printWriter.println("</body>");
        printWriter.close();
        }
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {
        processRequeset(request, response);
    }
    @Override
    protected  void doPost(HttpServletRequest request,
                           HttpServletResponse response)
        throws ServletException, IOException {
        processRequeset(request,response);
    }


}


