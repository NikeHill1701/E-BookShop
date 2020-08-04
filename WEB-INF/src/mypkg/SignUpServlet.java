package mypkg;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.sql.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.servlet.annotation.*;

public class SignUpServlet extends HttpServlet {
    private DataSource pool = null;

    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
        InitialContext ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null) {
                throw new ServletException("Unknown data source 'java:comp/env/jdbc/mysql_ebookshop'");
            }
        } catch (NamingException ex) {
            Logger.getLogger(SignUpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Signup Page</title></head>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rSet = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();
            
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String mobile = req.getParameter("mobile");
            String password = req.getParameter("password");
            String sqlStr = "";
            Boolean flag = true;
            
            sqlStr = "select * from users where (username='" + username +"')";
            rSet = stmt.executeQuery(sqlStr);
            if (rSet.next()) {
                out.println("Username already in use!");
                flag = false;
            }
            sqlStr = "select * from users where (email='" + email +"')";
            rSet = stmt.executeQuery(sqlStr);
            if (rSet.next()) {
                out.println("Email already in use!");
                flag = false;
            }
            sqlStr = "select * from users where (mobile='" + mobile +"')";
            rSet = stmt.executeQuery(sqlStr);
            if (rSet.next()) {
                out.println("Mobile number already in use!");
                flag = false;
            }

            if (flag) {
                sqlStr = "insert into users values('" + username + "', sha1('" + password + "'), '" 
                        +  mobile + "','" + email + "')";
                stmt.executeUpdate(sqlStr);
                sqlStr = "insert into user_roles values('" + username + "', 'customer')";
                stmt.executeUpdate(sqlStr);
                out.println("<h1>Signup successful!</h1>");
                out.println("<h1><a href='login.html'>Login</a> to continue</h1>");
            }
            else {
                out.println("<h1>Signup unsuccessful!</h1>");
                out.println("<h1><a href='/signup'>Try Again.</a></h1>");
            }
            out.println("</body></html>");
        } catch (SQLException ex) {
            //TODO: handle exception
            out.println("Service currently unavailable. Please try again later");
            Logger.getLogger(SignUpServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
            try {                                                   
                if (stmt != null) 
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SignUpServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}