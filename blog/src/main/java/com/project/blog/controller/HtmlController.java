package com.project.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.blog.dao.MemberDao;
import com.project.blog.dao.ZipcodeDao;

import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.io.*;

@Controller
public class HtmlController {
    @Autowired
    ZipcodeDao zipcodeDao;
    @Autowired
    MemberDao memberDao;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("util/search")
    public String utilSearch(@RequestParam String zipCode,
                             @RequestParam String addressDetail,
                             Model model) {
        model.addAttribute("addressDetail", addressDetail);
        model.addAttribute("zipCode", zipCode);
        return "util/search";
    }

    @PostMapping("util/search")
    public String utilSearchAction(@RequestParam String q,
                                   @RequestParam String zipCode,
                                   @RequestParam String addressDetail,
                                   Model model) {
        List<Map<String, Object>> resultSet = zipcodeDao.seachZipcode(q);
        model.addAttribute("resultSet", resultSet);
        model.addAttribute("zipCode", zipCode);
        model.addAttribute("addressDetail", addressDetail);
        return "util/search";
    }

    @PostMapping("util/upload")
    public String utilUpload(@RequestParam MultipartFile file,
                             HttpSession session) throws IOException {
        String fileContentType = file.getContentType();
        String fileType = fileContentType.substring(0, fileContentType.indexOf("/"));

        if (!fileType.equals("image")) {
            return "redirect:/member/myinfo";
        }

        UUID uuid = UUID.randomUUID();
        String originFileName = file.getOriginalFilename();
        String filePath = "C:/KEPCO/project/blog/src/main/resources/static/image/";
        String fileName = uuid.toString() + "_" + originFileName;
        File saveFile = new File(filePath + fileName);
        file.transferTo(saveFile);
        String userId = session.getAttribute("id").toString();
        memberDao.updateUserImage(userId, fileName);
        return "redirect:/member/myinfo";
    }
}