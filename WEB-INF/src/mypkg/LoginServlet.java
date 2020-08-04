package mypkg;

import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.servlet.annotation.*;

public class LoginServlet extends HttpServlet {
    private DataSource pool;

    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null) {
                throw new ServletException("Unknown data source jdbc/mysql_ebookshop");
            }
        } catch (NamingException ex) {
            //TODO: handle exception
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Login Page</title></head>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rSet = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            String sqlStr = "select * from users where username='" + username + "'"
                            + "and password=" + "sha1('" + password + "')";
            rSet = stmt.executeQuery(sqlStr);
    
            if (!rSet.next()) {
                // login failed!
                // display error message and redirect to login page
                // check if we can manipulate .html files from here.
                out.println("<h1>Invalid credentials</h1>");
                out.println("<h1><a href='login.html'>Login!</a></h1>");
            } else {
                // login successful!
                // create session and save username in it.
                // display welcome message and links to profile, start page.
                String email = rSet.getString("email");
                String mobile = rSet.getString("mobile");
                String role = "";

                sqlStr = "select role from user_roles where username = '" + username + "'";
                rSet = stmt.executeQuery(sqlStr);
                rSet.next();
                if (rSet.getString("role").compareTo("customer") == 0) {
                    // redirect to search page.
                    role = "customer";
                    out.println("<h1>Welcome customer, " + username + "</h1>");
                    out.println("<h1><a href='/yaebookshop/start'>Start Shopping!</a></h1>");
                }
                else {
                    // redirect to admin interface.
                    role = "admin";
                    out.println("<h1>Welcome admin, " + username + "</h1>");
                    out.println("<h1><a href='/yaebookshop/addBooks.html'>Edit Catalogue</a></h1>");
                }

                HttpSession session = req.getSession(false);
                if (session == null) {
                    session = req.getSession(true);
                    synchronized(session) {
                        User user = (User) session.getAttribute("user");
                        if (user == null) {
                            user = new User(username);
                            user.setEmail(email);
                            user.setMobile(mobile);
                            user.setRole(role);
                        }
                        out.println("<h1>Session Created</h1>");
                        session.setAttribute("user", user);
                    }
                }
            }
            out.println("</body></html>");

        } catch (SQLException ex) {
            //TODO: handle exception
            out.println("Service currently unavailable. Please try again later");
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
            try {                                                   
                if (rSet != null)
                    rSet.close();
                if (stmt != null) 
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}