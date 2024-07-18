package com.project.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.project.blog.dao.BoardDao;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@Controller
public class BoardController {
    @Autowired
    BoardDao boardDao;
    @GetMapping("board")
    public String boardList(Model model){
        List<Map<String,Object>> resultSet = boardDao.seletBoardList();
        model.addAttribute("resultSet", resultSet);
        return "board/list";
    }
    @GetMapping("board/detail")
    public String boardDetail(@RequestParam String seq, 
                              @RequestParam(defaultValue="2") String message, 
                              Model model ){
        Map<String, Object> resultSet = boardDao.selectBoardDetail(seq);
        List<Map<String,Object>> comments = boardDao.selectComment(seq);
        boardDao.updateSearchCnt(seq);
        model.addAttribute("resultSet", resultSet);
        model.addAttribute("message", message);
        model.addAttribute("comments", comments);
        return "board/detail";
    }
    @GetMapping("board/like")
    public String boardLike(@RequestParam String seq, @RequestParam String m){
        String column = "";
        if(m.equals("0")){
            column = "like_cnt";
        }else {
            column = "dislike_cnt";
        }
        boardDao.updateLike(seq,column);
        String URI = "redirect:/board/detail?seq="+seq;
        return URI;
        
    }


    @GetMapping("board/insert")
    public String boardInsertForm(HttpSession session, Model model){
        if(session.getAttribute("id").toString() != null){
            List<Map<String,Object>> resultSet = boardDao.selectCategory(); 
            model.addAttribute("resultSet", resultSet);
            return "board/insert";
        }else {
            return "redirect:/";
        }
    }
    @PostMapping("board/insert/action")
    public String boardInsertAction(@RequestParam String id,
                                    @RequestParam String title,
                                    @RequestParam String content,
                                    @RequestParam String category){
            boardDao.insertBoard(id,title,content,category);    
        return "redirect:/board";
    }
    @GetMapping("board/comment/insert")
    public String commentInsert(@RequestParam String comment,
                                @RequestParam String seq,
                                @RequestParam String pw,
                                @RequestParam(defaultValue = "0") String checkbox,
                                HttpSession session){
        String id = "";
        System.out.println("checkboxValue : "+checkbox);
        if(session.getAttribute("id") == null){
            id = "guest";
        }else{
            id = session.getAttribute("id").toString();
        }
        boardDao.insertComment(id,seq,pw,comment,checkbox);
        return "redirect:/board/detail?seq="+seq;

    }
    @GetMapping("board/comment/delete")
    public String boardCommentDete(@RequestParam String seq,
                                   @RequestParam String boardId){
        boardDao.deleteBoardComment(seq);
        return "redirect:/board/detail?seq="+boardId;
    }
    @GetMapping("board/comment/update")
    public String boardCommentUpdate(@RequestParam String seq, 
                                     @RequestParam String boardId,
                                     @RequestParam String comment){
        boardDao.updateBoardComment(seq,comment);
        return "redirect:/board/detail?seq="+boardId;
    }

}
