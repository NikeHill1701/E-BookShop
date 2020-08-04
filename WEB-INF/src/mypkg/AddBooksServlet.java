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

public class AddBooksServlet extends HttpServlet {
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

        res.setContentType("text/html;charser=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Edit Catalogue</title></head>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rSet = null;
        String sqlStr = "";

        try {

            HttpSession session = req.getSession(false);
            if (session == null) {
                out.println("<h1>You need to be logged in as an Admin to add books</h1>");
                out.println("<h1><a href='/login.html'>Login</a></h1>");
            } else {
                User user = (User) session.getAttribute("user");
                if (user.getRole().compareTo("admin") != 0) {
                    out.println("<h1>You need to be logged in as an Admin to add books</h1>");
                    out.println("<h1><a href='/yaebookshop/logout'>Login</a></h1>");
                }
                else {

                    conn = pool.getConnection();
                    stmt = conn.createStatement();
                    
                    String[] titles = req.getParameterValues("title");
                    String[] authors = req.getParameterValues("author");
                    String[] prices = req.getParameterValues("price");
                    String[] qtys = req.getParameterValues("qty");
                    int n = titles.length;
                    
                    for (int i=0; i<n; i++) {
                        String title = titles[i];
                        String author = authors[i];
                        String price = prices[i];
                        String qty = qtys[i];
                        sqlStr = "select * from books where title='" + title 
                                    + "' and author='" + author + "'";
                        rSet = stmt.executeQuery(sqlStr);
                        if (rSet.next()) {
                            sqlStr = "update books set qty=qty+" + Integer.parseInt(qty) + " where title='" + title + "'" 
                                    + "and author='" + author + "'";
                            stmt.executeUpdate(sqlStr);
                        }
                        else {
                            sqlStr = "insert into books (title,author,price,qty) values" 
                                    + "('"+title+"','"+author+"','"+Double.parseDouble(price)+"','"+Integer.parseInt(qty) + "')";
                            stmt.executeUpdate(sqlStr);
                        }
                    }
                    out.println("<h1>Books added successfully!</h1>");
                    out.println("<h1><a href='addBooks.html'>Add more!</a></h1>");
                }
            }
            out.println("</body></html>");
        } catch (Exception ex) {
            //TODO: handle exception
            Logger.getLogger(AddBooksServlet.class.getName()).log(Level.SEVERE, null, ex);
            out.println(sqlStr);
            out.println("Service currently unavailable. Please try again later!");
        } finally {
            try {
                out.close();
                if (conn != null)
                    conn.close();
                if (stmt != null)
                    stmt.close();
                if (rSet != null)
                    rSet.close();
            } catch (Exception ex) {
                Logger.getLogger(AddBooksServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}