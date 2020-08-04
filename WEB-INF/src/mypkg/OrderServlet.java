package mypkg;

import java.io.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.*;
import javax.naming.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.servlet.annotation.*;

public class OrderServlet extends HttpServlet {
    private DataSource pool;

    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource)ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null)
                throw new ServletException("Unkown data source 'jdbc/mysql_ebookshop'");
        } catch (NamingException ex) {
            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
        
            res.setContentType("text/html;charset=UTF-8");
            PrintWriter out = res.getWriter();

            Connection conn = null;
            Statement stmt = null;
    
            try {
                conn = pool.getConnection();
                stmt = conn.createStatement();    
    
                String[] bookIds = req.getParameterValues("id");
                String custName = req.getParameter("custName");
                String custEmail = req.getParameter("custEmail");
                String custMobile = req.getParameter("custMobile");
                
                Double totalPrice = 10.0;
                int totalNumberOfBooks = 10;

                out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Order Details</title></head>");
                out.println("<body>");
                out.println("<table>");
                out.println("<th><td>Order ID</td>  <td>Title</td>  <td>Author</td> <td>Price</td> <td>Qty Ordered</td>   </th>");
                out.println("</table>");
                out.println("<h3>Total Number of books ordered = " + totalNumberOfBooks +"</h3><br>");
                out.println("<h3>Total Price = " + totalPrice + "</h3><br>");
                out.println("<input type='submit' value='Confirm Order' action='/start'>&nbsp;");
                out.println("<input type='button' value='Cancel Order'");
    
                out.println("</body></html>");
            } catch (SQLException ex) {
                //TODO: handle exception
                out.println("Service not currently available, please comeback later!");
                Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
                try {
                    if (conn != null)
                        conn.close();
                    if (stmt != null)
                        stmt.close();   
                } catch (SQLException ex) {
                    //TODO: handle exception
                    Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();    

            String[] bookIds = req.getParameterValues("id");
            String custName = req.getParameter("custName");
            String custEmail = req.getParameter("custEmail");
            String custMobile = req.getParameter("custMobile");

            out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Order Details</title></head>");
            out.println("<body>");
            out.println("<table>");
            out.println("<th><td>Order ID</td>  <td>Title</td>  <td>Author</td> <td>Price</td> <td>Qty Ordered</td>   </th>");

            Double totalPrice = 0.0;
            int totalNumberOfBooks = 0;

            for (String str : bookIds) {
                String qtyOrdered = req.getParameter("qtyOrdered-" + str);
                String sqlStr = "insert into orders (bookId,qty_ordered,cust_name,cust_email,cust_phone)"
                + "values('" + str + "','" + qtyOrdered + "','" + custName + "','" + custEmail + "','" + custMobile + "')";
                out.println("<p>" + sqlStr + "</p>");
                stmt.executeUpdate(sqlStr);
                sqlStr = "select max(id) as max_id from orders";
                ResultSet rSet = stmt.executeQuery(sqlStr);
                rSet.next();
                out.println("<tr>");
                out.println("<td>" + rSet.getString("max_id") + "</td>");
                rSet = stmt.executeQuery("select * from books where id=" + str);
                rSet.next();
                out.println("<td>" + rSet.getString("title") + "</td>" + "<td>" + rSet.getString("author")
                + "<td>" + rSet.getString("price") + "</td>" + "<td>" + qtyOrdered + "</td>");
                out.println("</tr>");

                totalPrice += Double.parseDouble(rSet.getString("price")) * Double.parseDouble(qtyOrdered);
                totalNumberOfBooks += Integer.parseInt(qtyOrdered);
            }
            out.println("</table>");

            out.println("<h3>Total Number of books ordered = " + totalNumberOfBooks +"</h3><br>");
            out.println("<h3>Total Price = " + totalPrice + "</h3><br>");
            out.println("<input type='submit' value='Confirm Order' action='/start'>&nbsp;");
            out.println("<input type='button' value='Cancel Order'");

            out.println("</body></html>");
        } catch (SQLException ex) {
            //TODO: handle exception
            out.println("Service not currently available, please comeback later!");
            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
            try {
                if (conn != null)
                    conn.close();
                if (stmt != null)
                    stmt.close();   
            } catch (SQLException ex) {
                //TODO: handle exception
                Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}