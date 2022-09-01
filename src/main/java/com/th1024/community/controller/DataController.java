package com.th1024.community.controller;

import com.th1024.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-08-29 14:39
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    // 统计页面
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    // 统计网站UV
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);

        // 将数据传回模板，便于页面显示
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);

        // forward：将请求发到dispatcherServlet，再转发到路径
        return "forward:/data";
    }

    // 统计网站DAU
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);

        // 将数据传回模板，便于页面显示
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);

        // forward：将请求发到dispatcherServlet，再转发到路径
        return "forward:/data";
    }
}
