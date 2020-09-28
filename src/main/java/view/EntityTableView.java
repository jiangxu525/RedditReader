/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.Logic;
import logic.LogicFactory;

/**
 *
 * @author Xu Jiang
 */
@WebServlet(name = "EntityTables", urlPatterns = {"/BoardTable", "/ImageTable", "/HostTable", "/AccountTable"})
public class EntityTableView extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //retrieve the entity name from the servlet path
        String typeName = request.getServletPath().substring(1, request.getServletPath().length() - 5);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>" + typeName + "ViewNormal</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>" + typeName + "</caption>");

            Logic logic = LogicFactory.getFor(typeName);
            List<String> columNames = logic.getColumnNames();

            out.println("<tr>");
            for (String columName : columNames) {
                out.println("<th>" + columName + "</th>");
            }
            out.println("</tr>");

            List<Object> entities = logic.getAll();
            for (Object obj : entities) {
                out.println("<tr>");

                logic.extractDataAsList(obj).forEach(value -> {
                    out.print("<td>" + value + "</td>");
                });

                out.println("</tr>");
            }

            out.println("<tr>");

            for (String columName : columNames) {
                out.println("<th>" + columName + "</th>");
            }
            out.println("</tr>");

            out.println("</table>");
            out.printf("<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap(request.getParameterMap()));
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Entity View Normal";
    }// </editor-fold>

    private static final boolean DEBUG = true;

    @Override
    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    @Override
    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
