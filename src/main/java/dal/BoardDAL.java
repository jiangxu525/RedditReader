/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Board;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jiang
 */
public class BoardDAL extends GenericDAL<Board> {

    public BoardDAL(){
        super(Board.class);
    }
    
    @Override
    public List<Board> findAll() {
    return findResults( "Board.findAll", null);
    }

    @Override
    public Board findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult( "Board.findById", map);
    }
    
    public List<Board> findByHostId(int hostId){
        Map<String, Object> map = new HashMap<>();
        map.put("hostid", hostId);
        return findResults( "Board.findByHostId", map);
    }
    
    public Board findByUrl(String url){
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult( "Board.findByUrl", map);
    }
    
        public List<Board> findContaining(String search){
        Map<String, Object> map = new HashMap<>();
        map.put("search", search);
        return findResults( "Board.findContaining", map);
    }
    
    public List<Board> findByName(String name){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return findResults( "Board.findByName", map);
    }
}
