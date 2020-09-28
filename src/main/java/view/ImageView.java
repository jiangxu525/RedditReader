/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Board;
import entity.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.ImageLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;

/**
 *
 * @author Xu Jiang
 * @Description downloads images based on board, and get the outputstream from
 * ImageDelivery to display the images
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet {

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
        /* TODO output your page here. You may use following sample code. */

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
        String imageDirectory = System.getProperty("user.home");
        FileUtility.createDirectory(imageDirectory + "/My Documents/Reddit Images/");
        ImageLogic iLogic = LogicFactory.getFor("Image");
        BoardLogic bLogic = LogicFactory.getFor("Board");
        //create a new scraper
        Reddit scrap = new Reddit().authenticate();
        List<Board> boards = bLogic.getAll();
        request.setAttribute("boards", boards);
        for (Board board : boards) {
            if("LocalBoard".equals(board.getName())){
                continue;
            }
            //create a lambda that accepts post
            Consumer<Post> saveImage = (Post post) -> {
                //if post is an image and SFW
                if (post.isImage() && !post.isOver18()) {
                    //get the url for the image which is unique
                    String url = post.getUrl();
                    //save it in img directory
                    FileUtility.downloadAndSaveFile(url, imageDirectory + "/My Documents/Reddit Images/");

                    if (iLogic.getImageWithUrl(url) == null) {
                        Map<String, String[]> parameterMap = new HashMap<>();
                        parameterMap.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(post.getDate())});
                        parameterMap.put(ImageLogic.LOCAL_PATH, new String[]{imageDirectory + "/My Documents/Reddit Images/" + FileUtility.getFileName(url)});
                        parameterMap.put(ImageLogic.URL, new String[]{url});
                        parameterMap.put(ImageLogic.TITLE, new String[]{post.getTitle()});
                        Image entity = iLogic.createEntity(parameterMap);
                        entity.setBoard(board);
                        iLogic.add(entity);
                    }
                }
            };
            //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
            scrap.buildRedditPagesConfig(board.getName(), 5, Sort.BEST);
            //get the next page 3 times and save the images.
            scrap.requestNextPage().proccessNextPage(saveImage);
        }

        List<Image> entities = iLogic.getAll();
        List<String> entityFileNames = new ArrayList<>();
        for (Image entity : entities) {
            String fileName = FileUtility.getFileName(entity.getUrl());
            entityFileNames.add(fileName);
        }
        request.setAttribute("action", "get");
        request.setAttribute("entityFileNames", entityFileNames);
        request.getRequestDispatcher("/jsp/ImageViewJSP.jsp").forward(request, response);

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
        Map<String, String[]> map = request.getParameterMap();
        ImageLogic iLogic = LogicFactory.getFor("Image");
        String[] selectedBoards = map.get("checkbox");
        List<String> entityFileNames = new ArrayList<>();
        if (selectedBoards != null) {
            for (int i = 0; i < selectedBoards.length; i++) {
                int boardId = Integer.parseInt(selectedBoards[i]);
                List<Image> entities = iLogic.getImagesWithBoardId(boardId);
                for (Image entity : entities) {
                    String fileName = FileUtility.getFileName(entity.getUrl());
                    entityFileNames.add(fileName);
                }
            }
        }

        BoardLogic bLogic = LogicFactory.getFor("Board");
        List<Board> boards = bLogic.getAll();
        request.setAttribute("boards", boards);
        request.setAttribute("selectedBoards", selectedBoards);
        request.setAttribute("entityFileNames", entityFileNames);
        request.getRequestDispatcher("/jsp/ImageViewJSP.jsp").forward(request, response);

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
