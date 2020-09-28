package entity;

import entity.Host;
import entity.Image;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2020-07-04T22:05:11")
@StaticMetamodel(Board.class)
public class Board_ { 

    public static volatile SingularAttribute<Board, String> name;
    public static volatile SingularAttribute<Board, Host> hostid;
    public static volatile SingularAttribute<Board, Integer> id;
    public static volatile ListAttribute<Board, Image> imageList;
    public static volatile SingularAttribute<Board, String> url;

}