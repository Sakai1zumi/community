package com.th1024.community.controller;

import com.th1024.community.bean.Emp;
import com.th1024.community.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author izumisakai
 * @create 2022-04-04 17:19
 */

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService service;

    @RequestMapping("/1")
    @ResponseBody
    public String test() {
        return "Hello SpringBoot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String testData() {
        return service.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        String contextPath = request.getContextPath();
        System.out.println(contextPath);
        String method = request.getMethod();
        System.out.println(method);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String header = request.getHeader(name);

            System.out.println(name + ":" + header);
        }

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>测试</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //测试GET请求
    ///student?current=1&limits=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,//获取请求路径中的参数
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "students";
    }

    // student/100
    @RequestMapping(path = "student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable(name = "id") int id) {
        System.out.println(id);
        return "1 student";
    }

    //POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) { //获取POST请求所携带的参数
        System.out.println(name);
        System.out.println(age);

        return "success";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "zhangsan");
        mav.addObject("age", 3);
        mav.setViewName("/demo/view"); //模版页面存放于templates目录下

        return mav;
    }

    //常用
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) { //返回视图名
        model.addAttribute("name", "学校");
        model.addAttribute("age", 30);
        return "/demo/view";
    }

    //响应JSON数据（异步请求）
    //Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody //返回JSON数据
//    public Map<String, Object> getEmp() {
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("name", "zhangsan");
//        map.put("age", 3);
//
//        return map;
//    }
    public Emp getEmp() {
        Emp emp = new Emp("zhangsan", 5);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Emp> getEmps() {
        List<Emp> list = new ArrayList<>();

        list.add(new Emp("zhangsan",1));
        list.add(new Emp("lisi", 2));

        return list;
    }
}
