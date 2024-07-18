package com.project.blog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MemberDao {
    @Autowired
    JdbcTemplate jt;
    public List<Map<String,Object>> selectMemberList(String memberType){
        // memberList메소드가 원하는 쿼리문
        String sqlStmt = "";
        if(memberType.equals("")){
                sqlStmt += "select  seq, id, pw, name, ";
                sqlStmt += "birth_date as birthDate, email, if(grade=0,'준회원',if(grade=1,'정회원',if(grade=2,'관리회원','운영회원'))) as gradeNm, grade, ";
                sqlStmt += "reg_dt as regDt, if(del_fg=0,'정상','탈퇴') as delFgNm, del_fg as delFg ";
                sqlStmt += "from tb_member_mst";
        }else {
                sqlStmt += "select  seq, id, pw, name, ";
                sqlStmt += "birth_date as birthDate, email, if(grade=0,'준회원',if(grade=1,'정회원',if(grade=2,'관리회원','운영회원'))) as gradeNm, grade, ";
                sqlStmt += "reg_dt as regDt, if(del_fg=0,'정상','탈퇴') as delFgNm, del_fg as delFg ";
                sqlStmt += "from tb_member_mst where grade ='"+memberType+"'";
        }
        return jt.queryForList(sqlStmt);
    }
    public void insertMemberRegisterAction(String id,
                                           String pw,
                                           String name,
                                           String birthDate,
                                           String email){
        String sqlStmt1 = "insert into tb_member_mst( ";
               sqlStmt1 += "id,pw,name,birth_date,email) values(?,?,?,?,?)";
               jt.update(sqlStmt1,id,pw,name,birthDate,email);
        String sqlStmt2 = "insert into tb_member_detail(id) values(?)";
               jt.update(sqlStmt2,id);
        String sqlStmt3 = "insert into tb_image (id,img_url) values(?,'unknown.jpg')";
               jt.update(sqlStmt3, id);

    }
    public int depIdCheck(String id){
        String sqlStmt = "select count(*) from tb_member_mst where id = ?";
        return jt.queryForObject(sqlStmt,Integer.class,id); //integer타입으로 가져오기 위해
    }
    public void memberRegisgerDetailInsert(String id,
                                           String phone,
                                           String zipCode,
                                           String address,
                                           String addressDetail){
        String sqlStmtUpdate = "update tb_member_detail set phone = ? where id = ?";
        String sqlStmtInsert = "insert into tb_address_mst(address_id,zip_code,address,address_detail) values(?,?,?,?)";
        jt.update(sqlStmtUpdate,phone,id);
        jt.update(sqlStmtInsert,id,zipCode,address,addressDetail);
    }
    public Map<String,Object> selectMemberInfo(String id){
        String sqlStmt = "";
        sqlStmt += "select a.id as id, ";
        sqlStmt += "a.name as name, ";
        sqlStmt += "a.birth_date as birthDate, ";
        sqlStmt += "a.email as email, ";
        sqlStmt += "a.grade as grade, ";
        sqlStmt += "b.phone as phone ";
        sqlStmt += "from tb_member_mst a ";
        sqlStmt += "left join tb_member_detail b on a.id = b.id ";
        sqlStmt += "where a.id = ? ";
        return jt.queryForMap(sqlStmt,id);
    } 
    public List<Map<String,Object>> selectMemberAddressInfo(String id){
        String sqlStmt = "";
              sqlStmt += "select address, zip_code as zipCode, ";
              sqlStmt += "address_detail as addressDetail from tb_address_mst where address_id = '"+id+"'";
        return jt.queryForList(sqlStmt);
    }
    public void insertMemberRegsiterAddAddressAction(String id, String address, String addressDetail){
        String sqlStmt = "insert into tb_address_mst(address_id,address,address_detail) values(?,?,?)";
        jt.update(sqlStmt, id,address,addressDetail);
    }
    public Map<String,Object> selectMemberDetail(String id){
        String sqlStmt = "";
        sqlStmt += "select a.id as id, ";
        sqlStmt += "a.pw as pw, ";
        sqlStmt += "a.name as name, ";
        sqlStmt += "a.birth_date as birthDate, ";
        sqlStmt += "a.email as email, ";
        sqlStmt += "if(a.grade = 0,'준회원',if(a.grade=1,'정회원','우수회원')) as gradeNm, ";
        sqlStmt += "a.grade as grade, ";
        sqlStmt += "a.reg_dt as regDt, ";
        sqlStmt += "b.phone as phone ";
        sqlStmt += "from tb_member_mst a left join ";
        sqlStmt += "tb_member_detail b on a.id = b.id ";
        sqlStmt += "where a.id = ?";
        return jt.queryForMap(sqlStmt, id);

    }
    public void updateMemberUpdateGrade(String id, String grade){
        String sqlStmt = "";
        if(grade.equals("1") | grade.equals("2")){
            sqlStmt = "update tb_member_mst set grade = grade +1 where id = ?";
            jt.update(sqlStmt,id);
        }
    }
    public void deleteMemberDelete(String id){
        String sqlStmt = "delete from tb_member_mst where id = ?";
        jt.update(sqlStmt,id);
    }
    public int selectUserCheck(String userId, String password){
        String sqlStmt = "select count(*) from tb_member_mst where id = ? and pw = ?";
        return jt.queryForObject(sqlStmt,Integer.class,userId,password);
    }
    public List<Map<String,Object>> selectUserInfo(String userId, String password){
        String sqlStmt = "select id, birth_date, name, grade from tb_member_mst where id='";
               sqlStmt += userId+"' and pw = '"+password+"'";
        return jt.queryForList(sqlStmt);
    }
    public List<Map<String,Object>> selectMemberUpdateAddress(String id){
        String sqlStmt = "select seq ,";
               sqlStmt += "zip_code as zipCode ,";
               sqlStmt += "address, "; 
               sqlStmt += "address_detail as addressDetail "; 
               sqlStmt += "from tb_address_mst where address_id = '"+id+"'";
        return jt.queryForList(sqlStmt);
    }
    public Map<String, Object> selectMemberUpdatePhone(String id){
        String sqlStmt = "select phone from tb_member_detail where id = ?";
        return jt.queryForMap(sqlStmt,id);
    }
    public void updateMemberDetail(String id, String phone){
        String sqlStmt = "update tb_member_detail set phone = ? where id = ?";
        jt.update(sqlStmt, phone, id);
    }
    public void updateMemberAddress(String seq_,
                                    String address_, 
                                    String addressDetail_, 
                                    String zipCode_,
                                    String id){
        String sqlStmt = "update tb_address_mst set address_id = ?, ";
               sqlStmt += "address = ?, ";
               sqlStmt += "address_detail = ?, ";
               sqlStmt += "zip_code = ? where seq = ?";

        jt.update(sqlStmt, id, address_, addressDetail_, zipCode_,  seq_);
    }
    public Map<String, Object> selectMemberUpdate(String id){
        String sqlStmt = "select id, pw, name, email from tb_member_mst where id = ?";
        return jt.queryForMap(sqlStmt,id);
    }
    public void updateMemberInfo(String pw, String name, String email, String id){
        String sqlStmt = "update tb_member_mst set pw = ?, ";
               sqlStmt += "name = ?, ";
               sqlStmt += "email = ? where id = ?";
        jt.update(sqlStmt, pw,name,email,id);
    }
    public void registerSubscribe(String subscribeId, String userId){
        String sqlStmt = "insert into tb_subscribe_mst(subscribe_id,user_id) values(?,?)";
        jt.update(sqlStmt,subscribeId,userId);
    }
    public List<Map<String,Object>> selectMypage(String userId){
        String sqlStmt = "";
        sqlStmt += "select a.seq as seq, ";
        sqlStmt += "a.title as title, ";
        sqlStmt += "a.reg_dt as regDt, ";
        sqlStmt += "a.id as id, ";
        sqlStmt += "b.name as name, ";
        sqlStmt += "c.code_nm as codeNm, ";
        sqlStmt += "a.category as category ";
        sqlStmt += "from tb_board_mst a, tb_member_mst b, code c ";
        sqlStmt += "where a.id = b.id ";
        sqlStmt += "and a.category = c.code_id ";
        sqlStmt += "and a.id in (select subscribe_id ";
        sqlStmt +=        "from tb_subscribe_mst ";
        sqlStmt +=      " where user_id =?)";
        return jt.queryForList(sqlStmt,userId);  
    }
    public Map<String, Object> selectMyInfo(String userId){
        String sqlStmt = "";
              sqlStmt += "select a.seq as seq, ";
              sqlStmt += "a.id as id, ";
              sqlStmt += "a.email as email, ";
              sqlStmt += "a.name as name, ";
              sqlStmt += "b.img_url as imgUrl ";
              sqlStmt += "from tb_member_mst a ";
              sqlStmt += "left join tb_image b on a.id = b.id ";
              sqlStmt += "where a.id = ? ";
        return jt.queryForMap(sqlStmt,userId);
    }
    public void updateUserImage(String userId,
                                String imgUrl){
        String sqlStmt = "update tb_image set img_url = ? where id = ?";
        jt.update(sqlStmt, userId,imgUrl);
    }
}
