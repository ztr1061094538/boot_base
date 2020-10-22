package com.tg.sbootshrio.controller;


import com.tg.sbootshrio.common.CommonResult;
import com.tg.sbootshrio.common.Constans;
import com.tg.sbootshrio.mapper.*;
import com.tg.sbootshrio.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by admin on 2019/7/4.
 */
@RequestMapping("/user")
@Controller
@Slf4j
public class UserController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UserMapper userMapper;


    @PostMapping("/uploadFile")
    @ResponseBody
    @Transactional
    public CommonResult uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("file = " + file);
        String profilesPath = "D:\\util\\zzz\\boot_base\\src\\main\\resources\\static\\img\\";
        // 根据Windows和Linux配置不同的头像保存路径
        if (!file.isEmpty()) {
            // 当前用户
            HttpSession session =
                    request.getSession();
            Object userObject = session.getAttribute("userId");
            if (userObject == null) {
                //前台根据返回结果 选择跳转到登录页面
                return new CommonResult(Constans.SESSION_OUT, "登录超时，请重新登录", null);
            }
            String userId = (String) userObject;
            // 若头像名称不存在
            //String newProfileName = profilesPath + System.currentTimeMillis() + file.getOriginalFilename();
            String newProfileName = profilesPath + file.getOriginalFilename();
            // 路径存库
            User u = new User();
            u.setFilepath(file.getOriginalFilename());
            Example uExample = new Example(User.class);
            Example.Criteria criteria = uExample.createCriteria();
            criteria.andEqualTo("id", userId);
            userMapper.updateByExampleSelective(u, uExample);
            // 磁盘保存
            BufferedOutputStream out = null;
            try {
                File folder = new File(profilesPath);
                if (!folder.exists())
                    folder.mkdirs();
                out = new BufferedOutputStream(new FileOutputStream(newProfileName));
                // 写入新文件
                out.write(file.getBytes());
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                return new CommonResult(Constans.FAIL, "设置头像失败", null);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new CommonResult(Constans.SUCESS, "设置头像成功！", null);
        } else {
            return new CommonResult(Constans.FAIL, "设置头像失败", null);
        }

    }

    @GetMapping("/getImg")
    @ResponseBody
    @Transactional
    public CommonResult uploadFile(HttpServletRequest request) {
        HttpSession session =
                request.getSession();
        Object userObject = session.getAttribute("userId");
        String userId = (String) userObject;
        User u = new User();
        u.setId(Long.valueOf(userId));
        String imgpath = userMapper.select(u).get(0).getFilepath();
        return new CommonResult(Constans.SUCESS, "succes", imgpath);
    }


}
