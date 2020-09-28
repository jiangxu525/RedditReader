package entity;

import entity.Board;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2020-07-04T22:05:11")
@StaticMetamodel(Image.class)
public class Image_ { 

    public static volatile SingularAttribute<Image, Date> date;
    public static volatile SingularAttribute<Image, String> localPath;
    public static volatile SingularAttribute<Image, Integer> id;
    public static volatile SingularAttribute<Image, String> title;
    public static volatile SingularAttribute<Image, String> url;
    public static volatile SingularAttribute<Image, Board> board;

}