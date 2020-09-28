package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Xu Jiang
 */
public class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";
    
    /**
     * default constructor
     */
    private LogicFactory(){
        
    }

    /**
     * 
     * @param <T> the logic type
     * @param entityName the name of the logic that users want to create
     * @return the entity logic with the specific type T
     */
    public static <T> T getFor(String entityName){
        try{
            T newInstance = getFor((Class<T>)Class.forName(PACKAGE + entityName + SUFFIX));
            return newInstance;
        }catch(ClassNotFoundException e){
            //throw IllegalArgumentException if the entityName was not fount
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * 
     * @param <T> the logic type
     * @param type the logic type class
     * @return the entity logic with the specific type T
     */
    public static <T> T getFor(Class<T> type){
        try{
        Constructor<T> declaredConstructor = type.getDeclaredConstructor();
        T newInstance = declaredConstructor.newInstance();
        return newInstance;
        }catch(InstantiationException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | NoSuchMethodException | SecurityException e){
            //throw IllegalArgumentException if the class type was not fount
            throw new IllegalArgumentException(e);
        }
    }
}
