package example;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloServlet extends HttpServlet {
  private Connection conn = null;
  
  @Override
  public void init() throws ServletException{
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
      this.conn = DriverManager.getConnection(
        "jdbc:derby:javadb;create=true"
      );
    } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
      e.printStackTrace();
    }

    try {
      Statement stat = this.conn.createStatement();
      try {
        stat.execute("CREATE TABLE members( id int, name varchar(100) )");
      } catch (SQLException e) {
        if (e.getSQLState().equals("X0Y32")) {
          // Already exists, OK.
          return;
        }
        e.printStackTrace();
      } finally {
        stat.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void destroy() {
    try {
      this.conn.close();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    // Set the response message's MIME type
    response.setContentType("text/html;charset=UTF-8");
    // Allocate a output writer to write the response message into the network socket
    PrintWriter out = response.getWriter();

    // Write the response message, in an HTML page
    try {
      out.println("<!DOCTYPE html>");
      out.println("<html><head>");
      out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
      out.println("<title>Hello, World</title></head>");
      out.println("<body>");
      out.println("<h1>Hello, world!</h1>");  // says Hello
      // Echo client's request information
      out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
      out.println("<p>Protocol: " + request.getProtocol() + "</p>");
      out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
      out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
      // Generate a random number upon each request
      out.println("<p>A Random Number: <strong>" + Math.random() + "</strong></p>");

      try {
        Statement stat = this.conn.createStatement();
        try {
          int id = (int)(Math.random() * 90000 + 10000);
          stat.execute("INSERT INTO members (id, name) VALUES (" + String.valueOf(id) + ", 'hoge')");
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          stat.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }

      try {
        Statement stat = this.conn.createStatement();
        try {
          ResultSet rs = stat.executeQuery("SELECT * FROM members");
          out.println("id, name<br>");
          while(rs.next()){
            out.println(String.valueOf(rs.getInt("id")) + ", " + rs.getString("name") + "<br>");
          }
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          stat.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      
      out.println("</body>");
      out.println("</html>");
    } finally {
      out.close();  // Always close the output writer
    }
  }
}