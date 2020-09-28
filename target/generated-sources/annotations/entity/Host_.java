package entity;

import entity.Board;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2020-07-04T22:05:11")
@StaticMetamodel(Host.class)
public class Host_ { 

    public static volatile SingularAttribute<Host, String> extractionType;
    public static volatile SingularAttribute<Host, String> name;
    public static volatile SingularAttribute<Host, Integer> id;
    public static volatile ListAttribute<Host, Board> boardList;
    public static volatile SingularAttribute<Host, String> url;

}