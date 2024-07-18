package com.project.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import com.project.blog.dao.MemberDao;

import jakarta.servlet.http.HttpSession;

@Controller
public class SessionController {
    @Autowired
    MemberDao memberDao;

    @GetMapping("/login")
    public String getLogin(@RequestParam(defaultValue = "/") String page, Model model) {
        model.addAttribute(page, "page");
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam String userId,
            @RequestParam String password,
            @RequestParam String page,
            HttpSession session) {
        // 회원이 정상적인가?
        int cnt = memberDao.selectUserCheck(userId, password);
        if (cnt > 0) {
            // 회원정보를 담아 사용할 수 있도록
            List<Map<String, Object>> resultSet = new ArrayList<>();
            resultSet = memberDao.selectUserInfo(userId, password);
            session.setAttribute("id", resultSet.get(0).get("id").toString());
            session.setAttribute("birthDate", resultSet.get(0).get("birth_date").toString());
            session.setAttribute("grade", resultSet.get(0).get("grade").toString());
            session.setAttribute("name", resultSet.get(0).get("name").toString());

            return "redirect:" + page;
        } else {
            return "session_test.html";
        }

    }

    @GetMapping("admin/warning")
    public String adminWarning() {
        return "warning";
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
