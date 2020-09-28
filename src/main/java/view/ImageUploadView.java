/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import common.ValidationException;
import entity.Board;
import entity.Host;
import entity.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.HostLogic;
import logic.ImageLogic;
import logic.Logic;
import logic.LogicFactory;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author jiang
 */
@WebServlet(name = "ImageUploadView", urlPatterns = {"/ImageUploadView"})
public class ImageUploadView extends HttpServlet {

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
        request.getRequestDispatcher("/jsp/UploadImage.jsp").forward(request, response);
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
        String errorMessage = "";
        String fileName = "";
        String imageDirectory = "";
        String result = "";
        Map<String, String> formMap = new HashMap<>();
        //add new org.apache.commons.fileupload to Maven
        //this is used to check whether a form item is file or not and save files to the server.
        if (ServletFileUpload.isMultipartContent(request)) {
            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);

            try {
                //parse the request to extract File item
                List<FileItem> fileList = servletFileUpload.parseRequest(request);
                for (FileItem fileItem : fileList) {
                    //true: normal form field
                    //false: file uploaded
                    if (fileItem.isFormField()) {
                        formMap.put(fileItem.getFieldName(), fileItem.getString());
                    } else {
                        imageDirectory = System.getProperty("user.home") + "/My Documents/Reddit Images/";
                        FileUtility.createDirectory(imageDirectory);
                        fileName = fileItem.getName();
                        File file = new File(imageDirectory + fileName);
                        fileItem.write(file);
                    }
                }
            } catch (FileUploadException e) {
                throw new ValidationException(e);
            } catch (Exception ex) {
                Logger.getLogger(ImageUploadView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //by default, the database should have a LocalHost, but if not, create one.
        HostLogic hLogic = LogicFactory.getFor("Host");
        Host host = hLogic.getHostWithName("LocalHost");
        //if local host does not exists, create one, all parameters are pre-set
        if (host == null) {
            Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(HostLogic.NAME, new String[]{"LocalHost"});
            parameterMap.put(HostLogic.URL, new String[]{imageDirectory});
            parameterMap.put(HostLogic.EXTRACTION_TYPE, new String[]{"LocalType"});
            host = hLogic.createEntity(parameterMap);
            hLogic.add(host);
            host = hLogic.getHostWithName("LocalHost");
        }

        //by default, the database should have a LocalBoard, but if not, create one.
        BoardLogic bLogic = LogicFactory.getFor("Board");
        Board board = bLogic.getBoardsWithUrl(imageDirectory);
        if (board == null) {
            Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(BoardLogic.NAME, new String[]{"LocalBoard"});
            parameterMap.put(BoardLogic.URL, new String[]{imageDirectory});
            board = bLogic.createEntity(parameterMap);
            board.setHostid(host);
            bLogic.add(board);
            board = bLogic.getBoardsWithUrl(imageDirectory);
        }

        ImageLogic iLogic = LogicFactory.getFor("Image");
        Image image = iLogic.getImageWithLocalPath(imageDirectory + fileName);
        if (image == null) {
            Map<String, String[]> parameterMap = new HashMap<>();
            String url = imageDirectory + fileName;
            String localPath = imageDirectory + fileName;
            String title = formMap.get("title");
            Date date = new Date(System.currentTimeMillis());
            String dateStr = ImageLogic.FORMATTER.format(date);
            parameterMap.put(ImageLogic.DATE, new String[]{dateStr});
            parameterMap.put(ImageLogic.TITLE, new String[]{title});
            parameterMap.put(ImageLogic.LOCAL_PATH, new String[]{localPath});
            parameterMap.put(ImageLogic.URL, new String[]{url});
            image = iLogic.createEntity(parameterMap);
            image.setBoard(board);
            iLogic.add(image);
            result = "Image has been added to local board!";
        } else {
            errorMessage = "Image with the file name alread exists!";
        }

        request.setAttribute("result", result);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("jsp/UploadImage.jsp").forward(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
