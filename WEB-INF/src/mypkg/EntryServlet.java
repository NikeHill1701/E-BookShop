package mypkg;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import javax.sql.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

public class EntryServlet extends HttpServlet {
    private DataSource pool;

    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource)ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null)
                throw new ServletException("Unkown data source 'jdbc/mysql_ebookshop'");
        } catch (NamingException ex) {
            Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
        
        // set MIME represtation type
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rSet = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();
            
            out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Welcome to Yet Another E-BookShop</title></head>");
            out.println("<body>");

            String sqlStr = "Select distinct author from books where qty > 0";
            rSet = stmt.executeQuery(sqlStr);
            out.println("<form method='GET' action='query'>");
            out.println("<label for='author'>Select Author</label>&nbsp;");
            out.println("<select id='author' name='author'>");
            out.println("<option value='" + "None" + "'>" + "None" + "</option>");
            while (rSet.next()) {
                out.println("<option value='" + rSet.getString("author") + "'>" + rSet.getString("author") + "</option>");
            }
            out.println("</select><br>");
            out.println("<p>OR</p>");
            out.println("<label for='keyWord'>Search Titles/Authors:</label>&nbsp;");
            out.println("<input type='text' id='keyWord' name='keyWord'><br>");
            out.println("<label for='minPrice'>Minimum Price:</label>&nbsp;");
            out.println("<input type='text' id='minPrice' name='minPrice'><br>");
            out.println("<label for='maxPrice'>Maximum Price:</label>&nbsp;");
            out.println("<input type='text' id='maxPrice' name='maxPrice'><br>");
            //out.println("<input type='button' value='Search' onclick='validateSearchParameters()'>&nbsp;");
            out.println("<input type='submit' value='Search'>&nbsp;");
            out.println("<input type='reset' value='Reset'><br>");

            HttpSession session = req.getSession(false);
            if (session != null) {
                Cart cart;
                synchronized(session) {
                    cart = (Cart)session.getAttribute("cart");
                    if (cart != null && !cart.isEmpty()) {
                        out.println("<p><a href='/cart?todo=view'>View Shopping Cart</a></p>");
                    }
                }
            }
            out.println("<h1><a href='/yaebookshop/logout'>Logout</a></h1>");
            out.println("</body></html>");
        } catch (SQLException ex) {
            out.println("Service not currently available, please comeback later!");
            Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
            try {
                if (stmt != null) 
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
        
        doGet(req,res);
    }
}

