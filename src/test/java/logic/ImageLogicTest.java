package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Board;
import entity.Image;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Xu Jiang
 */
class ImageLogicTest {

    private ImageLogic logic;
    private Image expectedImage;
    private Board board;
    private final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditReader", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {
        Image image = new Image();
        image.setTitle("junit");
        image.setDate(FORMATTER.parse("2020-06-24 10:10:10"));
        image.setLocalPath("Junit test path");
        image.setUrl("Junit url");

        BoardLogic bLogic = LogicFactory.getFor("Board");
        board = bLogic.getWithId(4);
        image.setBoard(board);

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedImage = em.merge(image);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = LogicFactory.getFor("Image");
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }

    @Test
    final void testGetAll() {
        List<Image> list = logic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedImage);
        //delete the new account
        logic.delete(expectedImage);

        list = logic.getAll();
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing image fields
     */
    private void assertImageEquals(Image expected, Image actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBoard().getId(), actual.getBoard().getId());
        assertTrue(Math.abs(expected.getDate().getTime() - actual.getDate().getTime()) < 2000);
        assertEquals(expected.getLocalPath(), actual.getLocalPath());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
    }

    @Test
    final void testGetWithId() {
        System.out.println(expectedImage.getId());
        Image returnedImage = logic.getWithId(expectedImage.getId());
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetImagesWithBoardId() {
        List<Image> list = logic.getImagesWithBoardId(board.getId());
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);
        list = logic.getImagesWithBoardId(board.getId());
        assertEquals(originalSize - 1, list.size());
    }

    @Test
    final void testGetImagesWithTitle() {
        List<Image> list = logic.getImagesWithTitle(expectedImage.getTitle());
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);
        list = logic.getImagesWithTitle(expectedImage.getTitle());
        assertEquals(originalSize - 1, list.size());
    }

    @Test
    final void testGetImageWithLocalPath() {
        Image returnedImage = logic.getImageWithLocalPath(expectedImage.getLocalPath());
        assertNotNull(expectedImage);
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetImageWithUrl() {
        Image returnedImage = logic.getImageWithUrl(expectedImage.getUrl());
        assertNotNull(expectedImage);
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testConvertDate() {
        assertEquals(FORMATTER.format(expectedImage.getDate()), logic.convertDate(expectedImage.getDate()));
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = fillMap();
        Image returnedImage = logic.createEntity(sampleMap);
        returnedImage.setBoard(board);
        assertImageEquals(expectedImage, returnedImage);
    }

    /**
     * helper
     */
    final Map<String, String[]> fillMap() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.DATE, new String[]{FORMATTER.format(expectedImage.getDate())});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        sampleMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});

        return sampleMap;

    }

    @Test
    final void testCreateEntityIdNull() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityIdEmpty() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityTitleNull() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityTitleEmpty() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityUrlNull() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityUrlEmpty() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityDateNull() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.DATE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityDateEmpty() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.DATE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityLocalPathNull() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.LOCAL_PATH, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityLocalPathEmpty() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    final String randomText(int length) {
        return new Random().ints('a', 'z' + 1).limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    final void testCreateEntityWrongId() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityWrongLocalPath() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{randomText(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityWrongDate() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.DATE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{randomText(10)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityWrongTitle() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{randomText(1001)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityWrongUrl() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{randomText(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityEdgeIdValue() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.ID, new String[]{"1"});
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ImageLogic.ID)[0]), returnedImage.getId());
    }

    @Test
    final void testCreateEntityEdgeTitleValue() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.TITLE, new String[]{randomText(1)});
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());

        sampleMap.replace(ImageLogic.TITLE, new String[]{randomText(1000)});
        returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
    }

    @Test
    final void testCreateEntityEdgeLocalPathValue() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{randomText(1)});
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());

        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{randomText(255)});
        returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
    }

    @Test
    final void testCreateEntityEdgeUrlValue() {
        Map<String, String[]> sampleMap = fillMap();
        sampleMap.replace(ImageLogic.URL, new String[]{randomText(1)});
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());

        sampleMap.replace(ImageLogic.URL, new String[]{randomText(255)});
        returnedImage = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
    }

    @Test
    final void testAddWithNullBoard() {
        Map<String, String[]> sampleMap = fillMap();
        Image returnedImage = logic.createEntity(sampleMap);
        returnedImage.setBoard(null);
        assertThrows(PersistenceException.class, () -> logic.add(returnedImage));
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Title", "Date", "Local Path", "Board ID"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.URL, ImageLogic.TITLE, ImageLogic.DATE, ImageLogic.LOCAL_PATH, ImageLogic.BOARD_ID), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getUrl(), list.get(1));
        assertEquals(expectedImage.getTitle(), list.get(2));
        assertEquals(FORMATTER.format(expectedImage.getDate()), list.get(3));
        assertEquals(expectedImage.getLocalPath(), list.get(4));
        assertEquals(expectedImage.getBoard().getId(), list.get(5));
    }

}
