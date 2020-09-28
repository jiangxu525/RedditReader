/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.HostDAL;
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
public class HostLogic extends GenericLogic< Host, HostDAL> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String EXTRACTION_TYPE = "extractionType";

    /**
     * default constructor
     */
    HostLogic() {
        super(new HostDAL());
    }

    /**
     *
     * @return return all host entities in the DB
     */
    @Override
    public List<Host> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     *
     * @param id
     * @return return the entity with the id
     */
    @Override
    public Host getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
     *
     * @param name
     * @return return the entity with name
     */
    public Host getHostWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    /**
     *
     * @param url
     * @return return the entity with the related url
     */
    public Host getHostWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    /**
     *
     * @param type
     * @return return a list of entities with the type
     */
    public List<Host> getHostWithExtractionType(String type) {
        return get(() -> dal().findByExtractionType(type));
    }

    /**
     *
     * @param parameterMap
     * @return the new entity using parameterMap
     */
    @Override
    public Host createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");

        Host entity = new Host();

        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //valiator to check the data, eg the length of the string
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };

        String name = parameterMap.get(NAME)[0];
        String url = parameterMap.get(URL)[0];
        String extractionType = parameterMap.get(EXTRACTION_TYPE)[0];

        validator.accept(name, 100);
        validator.accept(url, 255);
        validator.accept(extractionType, 45);

        entity.setName(name);
        entity.setUrl(url);
        entity.setExtractionType(extractionType);

        return entity;
    }

    /**
     *
     * @return column names of image
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Name", "URL", "Extraction Type");
    }

    /**
     *
     * @return Column Codes of entity
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NAME, URL, EXTRACTION_TYPE);
    }

    @Override
    public List<Host> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    /**
     *
     * @param e the entity that needs to be extracted
     * @return a List<> that store the data set of the host entity e
     */
    @Override
    public List<?> extractDataAsList(Host e) {
        return Arrays.asList(e.getId(), e.getName(), e.getUrl(), e.getExtractionType());
    }

}
