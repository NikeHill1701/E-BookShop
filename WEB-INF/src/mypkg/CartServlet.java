package mypkg;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.logging.*;

import javax.sql.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

public class CartServlet extends HttpServlet {
    private DataSource pool;

    @Override
    public void init(ServletConfig sConfig) throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            pool = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql_ebookshop");
            if (pool == null) {
                throw new ServletException("Unknown data source 'jdbc/mysql_ebookshop'");
            }
        } catch (NamingException ex) {
            Logger.getLogger(EntryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Your Shopping Cart</title></head>");
        out.println("<body>");

        HttpSession session = req.getSession(false);
        if (session != null) {
            synchronized(session) {
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null && !cart.isEmpty()) {
                    out.println("<table>");
                    out.println("<th>");
                    out.println("<td>Book ID</td> <td>Title</td> <td>Author</td> <td>Price</td> <td>Qty Ordered</td>");
                    out.println("</th>");
                    ArrayList<CartItem> itemList = cart.getItems();
                    for (CartItem item : itemList) {
                        out.println("<tr>");
                        out.println("<td>" + item.getId() + "</td>");
                        out.println("<td>" + item.getTitle() + "</td>");
                        out.println("<td>" + item.getAuthor() + "</td>");
                        out.println("<td>" + item.getPrice() + "</td>");
                        out.println("<td>" + item.getQtyOrdered() + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    out.println("<h3>Total number of books: " + cart.getTotalNumberOfBooks() + " </h3>");
                    out.println("<h3>Total cost: " + cart.getTotalPrice() + " </h3>");
                    out.println("<input type='button' value='Place Order'>&nbsp");
                    out.println("<input type='button' value='Update Cart'><br>");
                }
                else {
                    out.println("<h3>Your Shopping Cart is empty!</h3>");
                    out.println("<a href='/yaebookshop/start'> Start shopping </a>");
                }
            }
        }
        else {
            out.println("<h1><a href='/yaebookshop/login.html'>Login </a> to continue</h1>");
        }
        out.println("<h1><a href='/yaebookshop/logout'>Logout</a></h1>");
        out.println("</body></html>");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.createStatement();

            String[] bookIds = req.getParameterValues("id");

            HttpSession session = req.getSession(false);
            if (session == null) {
                res.setContentType("text/html;charset=UTF-8");
                PrintWriter out = res.getWriter();
        
                out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Your Shopping Cart</title></head>");
                out.println("<body>");
                out.println("<h1><a href='/yaebookshop/login.html'>Login </a> to continue</h1>");
                out.println("</body></html>");
                out.close();
                try {
                    if (conn != null)
                        conn.close();
                    if (stmt != null) 
                        stmt.close();   
                } catch (SQLException ex) {
                    //TODO: handle exception
                    Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) 
                cart = new Cart();
            String sqlStr = "";

            for (String bookId : bookIds) {
                String qtyOrdered = req.getParameter("qtyOrdered-" + bookId);
                sqlStr = "select title,author,price from books where id = " + bookId;
                ResultSet rSet = stmt.executeQuery(sqlStr);
                rSet.next();
                synchronized(cart) {
                    CartItem cartItem = new CartItem(Integer.parseInt(bookId), rSet.getString("title"), rSet.getString("author")
                    , Double.parseDouble(rSet.getString("price")), Integer.parseInt(qtyOrdered));

                    cart.add(cartItem);
                }
            }
            session.setAttribute("cart", cart);
        } catch (Exception ex) {
            Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (stmt != null) 
                    stmt.close();   
            } catch (SQLException ex) {
                //TODO: handle exception
                Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            doGet(req, res);
        }

    }
}