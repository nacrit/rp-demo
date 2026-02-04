package com.example.addressexport.controller;

import cn.hutool.core.util.StrUtil;
import com.example.addressexport.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class LoginController {
    @PostMapping("/login/add")
    public String handleTokenSearch(@RequestParam("token") String token, Model model) {
        Set<String> results = LoginUtil.getTokens();
        model.addAttribute("token", token);
        model.addAttribute("results", results);
        model.addAttribute("code", "88888888");
        String result = null;
        if (StrUtil.isBlank(token) || token.length() < 20) {
            result = "结果：token不能为空";
        } else if (token.split(StrUtil.COLON).length < 2) {
            result = "token缺少备注";
        } else if (results.stream().map(e -> e.split(":")[0])
                .collect(Collectors.toList()).contains(token.split(":")[0])) {
            result = "结果：token已存在";
        } else if (!LoginUtil.checkToken(token)) {
            result = "结果：token 无效";
        }
        if (result != null) {
            model.addAttribute("result", result);
            return "login-add";
        }
        log.info("[添加token] token={}", token);
        results.add(token);
        LoginUtil.writeToken(token);
        model.addAttribute("result", "结果：操作成功");
        return "login-add"; // 返回的视图名称
    }


    @PostMapping("/login/clear")
    public String handleTokenSearch(Model model) {
        model.addAttribute("results", LoginUtil.getTokens());
        model.addAttribute("result", "结果：清除成功");
        model.addAttribute("code", "88888888");
        LoginUtil.cleanToken();
        // 重定向到显示搜索页面的方法，或者返回相同的视图以刷新页面
        return "/login-add";
    }


    @GetMapping("/login/add")
    public String loginAdd(@RequestParam(required = false) String token, Model model) {
        Set<String> results = LoginUtil.getTokens();
        model.addAttribute("token", token);
        model.addAttribute("results", results);
        model.addAttribute("result", "结果：操作成功");
        model.addAttribute("code", "88888888");
        return "login-add"; // 返回的视图名称
    }


    @PostMapping("/login/red-p")
    public String redP(@RequestParam("code") String code, Model model) {
        model.addAttribute("code", code);
        model.addAttribute("result", "结果：操作成功");
        List<String> results = LoginUtil.openRedP(code);
        model.addAttribute("results", results);
        return "login-add"; // 返回的视图名称
    }
}
