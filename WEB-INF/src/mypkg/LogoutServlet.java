package mypkg;

import java.util.*;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

public class LogoutServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8'><title>Your Shopping Cart</title></head>");
        out.println("<body>");

        HttpSession session = req.getSession(false);
        if (session == null) {
            out.println("<h1>You are already logged out!</h1>");
        }
        else {
            session.invalidate();
            out.println("<h1>Successfully logged out!</h1>");
        }
        out.println("<h1><a href='login.html'>Login</a></h1>");
        out.println("</body></html>");
        out.close();
    }
}