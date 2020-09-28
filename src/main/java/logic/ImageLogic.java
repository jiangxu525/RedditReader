/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import dal.ImageDAL;
import common.ValidationException;
import entity.Board;
import entity.Image;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.Arrays;

/**
 *
 * @author Xu Jiang
 */
public class ImageLogic extends GenericLogic< Image, ImageDAL> {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String LOCAL_PATH = "localPath";
    public static final String BOARD_ID = "boardId";

    /**
     * default constructor
     */
    ImageLogic() {
        super(new ImageDAL());
    }

    /**
     *
     * @return return all entities in the DB
     */
    @Override
    public List<Image> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     *
     * @param id id
     * @return return the entity with the id
     */
    @Override
    public Image getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
     *
     * @param boardId boardId
     * @return all entities with the foreign key boardId
     */
    public List<Image> getImagesWithBoardId(int boardId) {
        return get(() -> dal().findByBoardId(boardId));
    }

    /**
     *
     * @param title title of the image
     * @return all entities with the same title
     */
    public List<Image> getImagesWithTitle(String title) {
        return get(() -> dal().findByTitle(title));
    }

    @Override
    public List<Image> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    /**
     *
     * @param url the url of the image
     * @return the entity with the url searched
     */
    public Image getImageWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    /**
     *
     * @param path local path of the image
     * @return return the entity with the local path
     */
    public Image getImageWithLocalPath(String path) {
        return get(() -> dal().findByLocalPath(path));
    }

    /**
     *
     * @param date the date of the image
     * @return returns the image entities with the date specified
     */
    public List<Image> getImagesWithDate(Date date) {
        return get(() -> dal().findByDate(date));
    }

    /**
     *
     * @param date
     * @return convert the date to string format
     */
    public String convertDate(Date date) {
        return FORMATTER.format(date);
    }

    /**
     *
     * @param parameterMap stores entities data
     * @return the new entity using parameterMap
     */
    @Override
    public Image createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Image entity = new Image();

        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        //valiator to check the data, eg the length of the string data
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };

        String url = parameterMap.get(URL)[0];
        String title = parameterMap.get(TITLE)[0];
        String date = parameterMap.get(DATE)[0];
        String localPath = parameterMap.get(LOCAL_PATH)[0];

        validator.accept(url, 255);
        validator.accept(title, 1000);
        validator.accept(localPath, 255);

        entity.setTitle(title);
        entity.setUrl(url);
        entity.setLocalPath(localPath);

        try {
            Date UtilDate = FORMATTER.parse(date);
            entity.setDate(UtilDate);
        } catch (ParseException e) {
            throw new ValidationException(e);
        }

        return entity;

    }

    @Override
    public Image updateEntity(Map<String, String[]> parameterMap) {
        Image image = createEntity(parameterMap);
        BoardLogic bLogic = LogicFactory.getFor("Board");
        try {
            int boardId = Integer.parseInt(parameterMap.get(BOARD_ID)[0]);
            Board board = bLogic.getWithId(boardId);
            if (board != null) {
                image.setBoard(board);
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(e);
        }
        return image;
    }

    /**
     *
     * @return column names of image
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Title", "Date", "Local Path", "Board ID");
    }

    /**
     *
     * @return Column Codes of entity
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, TITLE, DATE, LOCAL_PATH, BOARD_ID);
    }

    /**
     *
     * @param e the entity that needs to be extracted
     * @return a List<> that store the data set of the image entity e
     */
    @Override
    public List<?> extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getTitle(), FORMATTER.format(e.getDate()), e.getLocalPath(), e.getBoard().getId());
    }
}
