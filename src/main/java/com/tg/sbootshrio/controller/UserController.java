package com.tg.sbootshrio.controller;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tg.sbootshrio.common.CommonResult;
import com.tg.sbootshrio.common.Constans;
import com.tg.sbootshrio.mapper.UserMapper;
import com.tg.sbootshrio.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Created by admin on 2019/7/4.
 */
@RequestMapping("/user")
@Controller
@Slf4j
public class UserController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String fileLocal="D:\\wb\\fileLocal";
    String ipAndPort="192.168.149.128/";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    @PostMapping("/uploadFile")
    @ResponseBody
    @Transactional
    public CommonResult uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

//        File fil=new File("D:\\wb\\dfs\\sdadasda.jpg");
//        String fileName = fil.getName();
//        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
//        FileInputStream inputStream=new FileInputStream(fil);
//        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, fil.length(), extName, null);
//        String fullPath = storePath.getFullPath();
//        String path = storePath.getPath();
//        System.out.println("path = " + path);
//        System.out.println("fullPath = " + fullPath);
//
        String originalFilename = file.getOriginalFilename();
        String extentionName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String newName= UUID.randomUUID()+extentionName;
        File newFile=new File(fileLocal+newName);
        file.transferTo(newFile);
        FileInputStream inputStream=new FileInputStream(newFile);
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, newFile.length(), extentionName, null);
        String fullPath = storePath.getFullPath();
        String path = storePath.getPath();
        System.out.println("path = " + path);
        System.out.println("fullPath = " + fullPath);
        HttpSession session =
                request.getSession();
        Object userObject = session.getAttribute("userId");
        String userId = (String) userObject;
        // 路径存库
        User u = new User();
        u.setFilepath(ipAndPort+fullPath);
        Example uExample = new Example(User.class);
        Example.Criteria criteria = uExample.createCriteria();
        criteria.andEqualTo("id", userId);
        userMapper.updateByExampleSelective(u, uExample);
        return new CommonResult(Constans.SUCESS, "设置头像成功！", null);
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
