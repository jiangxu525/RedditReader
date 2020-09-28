/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Account;
import entity.Board;
import entity.Host;
import entity.Image;
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
import logic.AccountLogic;
import logic.BoardLogic;
import logic.HostLogic;
import logic.ImageLogic;
import logic.Logic;
import logic.LogicFactory;

/**
 *
 * @author Xu Jiang
 */
@WebServlet(name = "CreateEnties", urlPatterns = {"/CreateHost", "/CreateBoard", "/CreateAccount"})
public class CreateEntity extends HttpServlet {

    private String errorMessage = null;

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            //retrieve the entity name from the servlet path
            String typeName = request.getServletPath().substring(7);

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create" + typeName + "</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");

            Logic logic = LogicFactory.getFor(typeName);
            List<String> columnNames = logic.getColumnNames();
            List<?> columnCodes = logic.getColumnCodes();
            for (int i = 1; i < columnNames.size(); i++) {
                out.println(columnNames.get(i) + ":<br>");
                out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>", columnCodes.get(i));
                out.println("<br>");
            }

            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
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
        //retrieve the entity name from the servlet path
        String typeName = request.getServletPath().substring(7);

        if ("Host".equals(typeName)) {
            HostLogic hLogic = LogicFactory.getFor("Host");
            String name = request.getParameter(HostLogic.NAME);
            if (hLogic.getHostWithName(name) == null) {
                Host host = hLogic.createEntity(request.getParameterMap());
                hLogic.add(host);
            } else {
                errorMessage = "Name: \"" + name + "\" already exists";
            }
        } else if ("Board".equals(typeName)) {
            BoardLogic bLogic = LogicFactory.getFor("Board");
            String url = request.getParameter(BoardLogic.URL);
            if (bLogic.getBoardsWithUrl(url) == null) {
                HostLogic hLogic = LogicFactory.getFor("Host");
                try {
                    int hostId = Integer.parseInt(request.getParameter(BoardLogic.HOST_ID));
                    Host host = hLogic.getWithId(hostId);
                    if (host != null) {
                        Board board = bLogic.createEntity(request.getParameterMap());
                        board.setHostid(host);
                        bLogic.add(board);
                    } else {
                        errorMessage = "Host id: \"" + hostId + "\" does not exist";
                    }
                } catch (NumberFormatException e) {
                    errorMessage = "Host id typed is not an integer";
                }
            } else {
                errorMessage = "URL: \"" + url + "\" already exists";
            }
        }  else if ("Account".equals(typeName)) {
            AccountLogic aLogic = LogicFactory.getFor("Account");
            String username = request.getParameter(AccountLogic.USERNAME);
            if (aLogic.getAccountWithUsername(username) == null) {
                //call create entity and create a board enitty
                //create host lgoic
                //get host with desired id from host logic
                //set the host to board entity. board.setHostId(host)
                //add board to db
                Account account = aLogic.createEntity(request.getParameterMap());
                aLogic.add(account);
            } else {
                //if duplicate print the error message
                errorMessage = "Username: \"" + username + "\" already exists";
            }
        }

        if (request.getParameter("add") != null) {
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            response.sendRedirect(typeName + "Table");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create an Entity";
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
