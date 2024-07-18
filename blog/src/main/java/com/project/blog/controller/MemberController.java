package com.project.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.blog.dao.MemberDao;

import jakarta.servlet.http.HttpSession;

import java.util.*;

@Controller
public class MemberController {
    @Autowired
    MemberDao memberDao;
    @GetMapping("member")
    public String memberList(@RequestParam(defaultValue="") String memberType, 
                             Model model, 
                             HttpSession session){
        if(session.getAttribute("grade") == null){
            return "redirect:/login";
        }
        int grade = Integer.parseInt(session.getAttribute("grade").toString());
        if(grade < 2 ){
            return "redirect:/admin/warning";
        }
        List<Map<String,Object>> resultSet = memberDao.selectMemberList(memberType);
        model.addAttribute("resultSet", resultSet);

        return "member/list";
    }
    @GetMapping("member/register")
    public String memberRegisterForm(){
        return "member/register";
    }
    @PostMapping("member/register/action")
    public String memberRegisterAction(@RequestParam String id,
                                       @RequestParam String pw,
                                       @RequestParam String name,
                                       @RequestParam String year,
                                       @RequestParam String month,
                                       @RequestParam String day,
                                       @RequestParam String email1,
                                       @RequestParam String email2,
                                       HttpSession session){
        if(month.length()<2){
            month = '0'+month;
        }
        if(day.length()<2){
            day = '0'+day;
        }
        String birthDate = year+month+day;
        String email = email1+"@"+email2;
        memberDao.insertMemberRegisterAction(id,pw,name,birthDate,email);
        session.setAttribute("id", id);
        session.setAttribute("name",name);
        session.setAttribute("grade", 0);
        session.setAttribute("brithDate", year+month+day);
        String redirectURI = "redirect:/member/register/detail?id="+id; 
        return redirectURI;
    }
    @GetMapping("/member/dupcheck")
    public String memberDupCheck(@RequestParam String id, Model model){
        int resultSet = memberDao.depIdCheck(id);
        if(resultSet >0 ){
            model.addAttribute("result", id+"는  사용불가능한 아이디입니다");
            model.addAttribute("fg", "1");
        }else{
            model.addAttribute("result", id+"는 사용가능한 아이디 입니다");
            model.addAttribute("fg","0");
        }
        model.addAttribute("id", id);
        return "member/dupcheck";
    }
    @GetMapping("/member/register/detail")
    public String memberRegisterDetail(@RequestParam String id, Model model){
        model.addAttribute("id", id);
        return "member/register_detail";
    }
    @GetMapping("/member/register/detail/action")
    public String memberRegisterDetailAction(@RequestParam String id, 
                                             @RequestParam(defaultValue = "") String phone,
                                             @RequestParam(defaultValue = "") String zipCode,
                                             @RequestParam(defaultValue = "") String address,
                                             @RequestParam(defaultValue = "") String addressDetail){
            memberDao.memberRegisgerDetailInsert(id,phone,zipCode,address,addressDetail);
            String redirectURI = "redirect:/";
        return redirectURI;
    }
    @GetMapping("/member/info")
    public String memberInfo(@RequestParam String id,Model model){   
        Map<String,Object> basicMemberInfo = memberDao.selectMemberInfo(id);
        List<Map<String,Object>> addressMemberInfo = memberDao.selectMemberAddressInfo(id);
        model.addAttribute("id",id);
        model.addAttribute("basicMemberInfo", basicMemberInfo);
        model.addAttribute("addressMemberInfo", addressMemberInfo);
        return "member/member_info";
    }
    @GetMapping("member/register/addAddress")
    public String memberRegisterAddAddress(@RequestParam String id, Model model){
        model.addAttribute("id", id);
        return "member/register_addaddress";
    }
    @GetMapping("member/register/addAddress/action")
    public String memberRegisterAddAddressAction(@RequestParam String id,
                                                 @RequestParam String address,
                                                 @RequestParam String addressDetail){
        memberDao.insertMemberRegsiterAddAddressAction(id,address,addressDetail);
        String redirectURI = "redirect:/member/info?id="+id;
        return redirectURI;
    }
    @GetMapping("member/detail")
    public String memberDetail(String id,Model model, HttpSession session){
        int grade = Integer.parseInt(session.getAttribute("grade").toString());
        String userId = session.getAttribute("id").toString();
        if(grade == 3 | userId.equals(id)){
            Map<String,Object> memberDetail = memberDao.selectMemberDetail(id);
            model.addAttribute("memberDetail", memberDetail);
            return "member/detail";
        }
        else {
            return "redirect:/admin/warning";
        }
    }
    @GetMapping("/member/update/grade")
    public String memberUpdateGrade(@RequestParam String id, @RequestParam String grade){
        memberDao.updateMemberUpdateGrade(id, grade);
        return " redirect:/member";
    }
    @GetMapping("member/delete")
    public String memberDelete(@RequestParam String id, HttpSession session){
        int grade = Integer.parseInt(session.getAttribute("grade").toString());
        if(grade < 3){
            return "redirect:/admin/warning";
        }
        memberDao.deleteMemberDelete(id);
        return "redirect:/member";
    }
   
    @GetMapping("member/update/detail")
    public String memberUpdateDetail(Model model, HttpSession session){
        String id = session.getAttribute("id").toString();
        List<Map<String, Object>> resultAddress = memberDao.selectMemberUpdateAddress(id);
        Map<String,Object> resultPhone = memberDao.selectMemberUpdatePhone(id);
        model.addAttribute("resultAddress", resultAddress);
        model.addAttribute("resultPhone", resultPhone);
        return "member/update_detail";
    }
    @GetMapping("member/update/detail/action")
    public String memberUpdateDetailAction(@RequestParam String phone, 
                                           @RequestParam List<String> seq,
                                           @RequestParam List<String> address,
                                           @RequestParam List<String> zipCode,
                                           @RequestParam List<String> addressDetail,
                                           @RequestParam String id){
        
        memberDao.updateMemberDetail(id, phone);
        for(int i=0; i<seq.size();i++){
            String seq_ = seq.get(i).toString();
            String address_ = address.get(i).toString();
            String addressDetail_ = addressDetail.get(i).toString();
            String zipCode_ = zipCode.get(i).toString();
            memberDao.updateMemberAddress(seq_, address_, addressDetail_, zipCode_,id);
        }
        return "redirect:/";
    }
    @GetMapping("member/update")
    public String memberUpdate(HttpSession session, Model model){
        if(session.getAttribute("id").toString() == null){
            return "redirect:/";
        }
        String id = session.getAttribute("id").toString();
        Map<String, Object> resultSet = memberDao.selectMemberUpdate(id);
        String email1 = resultSet.get("email").toString();
        int index = email1.indexOf("@");
        String transEmail1 = email1.substring(0, index);
        String transEmail2 = email1.substring(index+1);
        System.out.println(transEmail2);
        model.addAttribute("email1", transEmail1);
        model.addAttribute("email2", transEmail2);
        model.addAttribute("resultSet", resultSet);
        return "member/update";
    }
    @PostMapping("member/update/action")
    public String memberUpdateAction(@RequestParam String pw,
                                     @RequestParam String name,
                                     @RequestParam String email1,
                                     @RequestParam String email2,
                                     HttpSession session){
        String email = email1+"@"+email2;
        String id = session.getAttribute("id").toString();
        memberDao.updateMemberInfo(pw, name, email, id);
        return "redirect:/";
    }
    @GetMapping("/member/subscribe")
    public String memberSubscribe(@RequestParam String id, 
                                  @RequestParam String seq,
                                  HttpSession session){
        if(session.getAttribute("id") == null){
            return "redirect:/login";
        }else if(session.getAttribute("id").equals(id))
        {
            String URI = "redirect:/board/detail?seq="+seq+"&message=1";
            return URI;
        }else {
            String subscribeId = id;
            String userId = session.getAttribute("id").toString();
            memberDao.registerSubscribe(subscribeId,userId);
            String URI = "redirect:/board/detail?seq="+seq+"&message=1";
            return URI;  
        }
    }
    @GetMapping("member/mypage")
    public String myPage(Model model, HttpSession session){
        String userId = session.getAttribute("id").toString();
        List<Map<String,Object>> resultSet = memberDao.selectMypage(userId);
        model.addAttribute("resultSet", resultSet);
        return "member/mypage";
    }
    @GetMapping("member/myinfo")
    public String myInfo(Model model, HttpSession session){
        String userId = session.getAttribute("id").toString();
        Map<String, Object> resultSet = memberDao.selectMyInfo(userId);
        model.addAttribute("resultSet", resultSet);
        return "member/myinfo";
    } 

}
