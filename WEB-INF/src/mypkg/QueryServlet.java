package mypkg;

import java.util.*;
import java.io.*;
import java.lang.Thread.State;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.naming.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.servlet.annotation.*;

public class QueryServlet extends HttpServlet {
    private DataSource pool;
    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource)ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null)
                throw new ServletException("Unkown data source 'jdbc/mysql_ebookshop'");
        } catch (NamingException ex) {
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();      

        Connection conn = null;
        Statement stmt = null;
        ResultSet rSet = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();

            String author = req.getParameter("author");
            String keyWord = req.getParameter("keyWord");
            String minPrice = req.getParameter("minPrice");
            String maxPrice = req.getParameter("maxPrice");

            if (minPrice == null || minPrice.equals(""))
                minPrice = "0";
            if (maxPrice == null || maxPrice.equals(""))
                maxPrice = 100000 + "";
            if (keyWord == null) {
                keyWord = "";
            }
            String sqlStr = "";
            if (!author.equals("None")) {
                sqlStr = "Select * from books"
                        + " where qty > 0 and author = '" + author + "' and title like '%" +keyWord + "%'"
                        +" and price > " + minPrice + " and price < " + maxPrice + " order by" + " price asc";
            }
            else {
                sqlStr = "Select * from books"
                        + " where qty > 0 and (author like '%" + keyWord + "%' or title like '%" +keyWord + "%')"
                        + " and price > " + minPrice + " and price < " + maxPrice + " order by" + " price asc;";
            }
            rSet = stmt.executeQuery(sqlStr);

            out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Welcome to Yet Another E-BookShop</title>"
                        + "<script src='requestHandlerScript.js'></script>"
                        + "</head>");
            out.println("<body>");
            out.println("<form id='addToCartForm' method='POST' action='cart'>");
            out.println("<table>");
            out.println("<th><td>Title</td><td>Author</td><td>Price</td><td>Qty Available</td><td>Qty Req</td></th>");
            
            while (rSet.next()) {
                out.println("<tr>");
                out.println("<td><input type='checkbox' id='id' name='id' value='" + rSet.getString("id") +"'></td>");
                out.println("<td>" + rSet.getString("title") + "</td>");
                out.println("<td>" + rSet.getString("author") + "</td>");
                out.println("<td>" + rSet.getString("price") + "</td>");
                out.println("<td>" + rSet.getString("qty") + "</td>");
                out.println("<td><input type='number' id='qtyOrdered' name='qtyOrdered-" + rSet.getString("id") + "'></td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<input type='submit' value='Add to My Shopping Cart'>&nbsp;");
            out.println("<input type='reset' value='Clear'><br>");
            out.println("</form>");

            HttpSession session = req.getSession(false);
            if (session != null) {
                Cart cart;
                synchronized(session) {
                    cart = (Cart) session.getAttribute("cart");
                    if (cart != null && !cart.isEmpty()) {
                        out.println("<p><a href='cart?todo=view'>View Shopping Cart</a></p>");
                    }
                }
            }
            out.println("<h1><a href='/yaebookshop/logout'>Logout</a></h1>");
            out.println("</body></html>");

        } catch (SQLException ex) {
            //TODO: handle exception
            out.println("Service not currently available, please comeback later!");
            Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(QueryServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}