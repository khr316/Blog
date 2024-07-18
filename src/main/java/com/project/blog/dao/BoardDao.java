package com.project.blog.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDao {
    @Autowired
    JdbcTemplate jt;

    public List<Map<String, Object>> selectCategory() {
        String sqlStmt = "select code_id as codeId, code_nm as codeNm from code order by code_id";
        return jt.queryForList(sqlStmt);
    }

    public void insertBoard(String id, String title,
            String content, String category) {
        String sqlStmt = "insert into tb_board_mst(id,title,content,category) values(?,?,?,?)";
        jt.update(sqlStmt, id, title, content, category);
    }

    public List<Map<String, Object>> seletBoardList() {
        String sqlStmt = "";
        sqlStmt += "select a.title as title, a.seq as seq, ";
        sqlStmt += "b.name as name, ";
        sqlStmt += "b.email as email, ";
        sqlStmt += "c.code_nm as category, ";
        sqlStmt += "a.like_cnt as likeCnt, ";
        sqlStmt += "a.dislike_cnt as dislikeCnt, ";
        sqlStmt += "a.search_cnt as searchCnt ";
        sqlStmt += "from tb_board_mst a, tb_member_mst b, code c ";
        sqlStmt += "where a.id = b.id and c.code_id = a.category ";
        return jt.queryForList(sqlStmt);
    }

    public Map<String, Object> selectBoardDetail(String seq) {
        String sqlStmt = "";
        sqlStmt += "select a.seq as seq, ";
        sqlStmt += "a.title as title, ";
        sqlStmt += "a.content as content, ";
        sqlStmt += "a.category as category, ";
        sqlStmt += "c.code_nm as codeNm, ";
        sqlStmt += "a.id as id, ";
        sqlStmt += "b.name as name ";
        sqlStmt += "from tb_board_mst a, tb_member_mst b, code c ";
        sqlStmt += "where a.id = b.id and c.code_id = a.category and a.seq = ?";
        return jt.queryForMap(sqlStmt, seq);
    }

    public void updateLike(String seq, String column) {
        String sqlStmt = "update tb_board_mst set " + column;
        sqlStmt += "=" + column + "+1 where seq =?";
        jt.update(sqlStmt, seq);
    }

    public void updateSearchCnt(String seq) {
        String sqlStmt = "update tb_board_mst set search_cnt = search_cnt + 1 where seq = ?";
        jt.update(sqlStmt, seq);
    }

    public void insertComment(String id, String seq, String pw, String comment, String checkbox) {
        String sqlStmt = "insert into tb_comment(id,board_id,pw,comment,secret_fg) values(?,?,?,?,?)";
        jt.update(sqlStmt, id, seq, pw, comment, checkbox);
    }

    public List<Map<String, Object>> selectComment(String seq) {
        String sqlStmt = "select seq, id, pw, comment, reg_dt as regDt, secret_fg as secretFg from tb_comment where board_id =? ";
        return jt.queryForList(sqlStmt, seq);
    }

    public void deleteBoardComment(String seq) {
        String sqlStmt = "delete from tb_comment where seq = ?";
        System.out.println(sqlStmt);
        jt.update(sqlStmt, seq);
    }

    public void updateBoardComment(String seq, String comment) {
        String sqlStmt = "update tb_comment set comment = ? where seq =?";
        jt.update(sqlStmt, comment, seq);
    }
}
