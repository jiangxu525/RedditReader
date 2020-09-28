/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.BoardDAL;
import entity.Board;
import entity.Host;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Xu Jiang
 * @date Jun 9, 2020
 */
public class BoardLogic extends GenericLogic<Board, BoardDAL> {

    public static final String ID = "id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String HOST_ID = "hostid";

    /**
     * default constructor
     */
    BoardLogic() {
        super(new BoardDAL());
    }

    /**
     *
     * @return a list of all board entities
     */
    @Override
    public List<Board> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     *
     * @param id board id
     * @return the board entity with the specific id
     */
    @Override
    public Board getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
     *
     * @param hostId host id of the board
     * @return a list of board entities with the specific host id
     */
    public List<Board> getBoardsWithHostID(int hostId) {
        return get(() -> dal().findByHostId(hostId));
    }

    /**
     *
     * @param name name of the board
     * @return a list of board entities with the specific name
     */
    public List<Board> getBoardsWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    /**
     *
     * @param url board url
     * @return the board entity with the specific board url
     */
    public Board getBoardsWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    /**
     *
     * @param parameterMap data set for new board entity
     * @return the board entity with the parameterMap data set
     */
    @Override
    public Board createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Board entity = new Board();
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        //validate to validate the data --string length
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };
        String url = parameterMap.get(URL)[0];
        String name = parameterMap.get(NAME)[0];
        validator.accept(url, 255);
        validator.accept(name, 100);
        entity.setUrl(url);
        entity.setName(name);
        return entity;
    }

    /**
     *
     * @return column names of board
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Name", "Host ID");
    }

    /**
     *
     * @return Column Codes of entity
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, NAME, HOST_ID);
    }

    @Override
    public Board updateEntity(Map<String, String[]> parameterMap) {
        Board board = createEntity(parameterMap);
        HostLogic hLogic = LogicFactory.getFor("Host");
        try {
            int hostId = Integer.parseInt(parameterMap.get(HOST_ID)[0]);
            Host host = hLogic.getWithId(hostId);
            if (host != null) {
                board.setHostid(host);
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(e);
        }

        return board;
    }
    
        @Override
    public List<Board> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    /**
     *
     * @param e the entity that needs to be extracted
     * @return a List<> that store the data set of the board entity e
     */
    @Override
    public List<?> extractDataAsList(Board e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getName(), e.getHostid().getId());
    }

}
