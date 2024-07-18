package com.project.blog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class ZipcodeDao {
    @Autowired
    JdbcTemplate jt;
    public List<Map<String,Object>> seachZipcode(String q){
        String sqlStmt = "";
               sqlStmt += "select zip_code as zipCode, ";
               sqlStmt += "sido, ";
               sqlStmt += "road_nm as roadNm, ";
               sqlStmt += "sigu_building_nm as siguBuildingNm from tb_zip_address ";
               sqlStmt += "where leg_nm like '%"+q+"%' ";
        return jt.queryForList(sqlStmt); 
           
    }
}
