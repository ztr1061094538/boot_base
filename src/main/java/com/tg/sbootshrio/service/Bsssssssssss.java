package com.tg.sbootshrio.service;

/**
 * Created by DHAdmin on 2020/7/23.
 */

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tg.sbootshrio.pojo.MailBean;
import com.tg.sbootshrio.pojo.User;
import com.tg.sbootshrio.util.EmailUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Bsssssssssss {

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private EmailUtil emailUtil;


    public static void main(String[] args) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        String localTime = df.format(time);
        LocalDateTime ldt = LocalDateTime.parse(localTime, df);
        //System.out.println("LocalDateTime转成String类型的时间：" + localTime);
        System.out.println("String类型的时间转成LocalDateTime：" + ldt);


    }


    /**
     * 发送邮件
     */
    //@Scheduled(cron = "1 * * * * ?")
    public void sendEamil() {

        String sss = "sss";
        String s = SecureUtil.md5(sss);

        System.out.println("============sendEamil================= ");
        MailBean mailBean = new MailBean();
        mailBean.setSubject("测试email主题");
        mailBean.setContent("测试email 内容");
        mailBean.setRecipient("18061690593@163.com");
        Boolean aBoolean = emailUtil.sendSimpleMail(mailBean);
        System.out.println("aBoolean = " + aBoolean);

    }


    //发送消息方法
    //@Scheduled(cron = "1 * * * * ?")
    public void send() {
        System.out.println("============================= ");
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setPassword("iphonepassword" + i);
            user.setUserName("iphoneusername" + i);
            String s = JSON.toJSONString(user);
            System.out.println("发送消息s = " + s);

            //假如没有topic的话  会自动创建
            kafkaTemplate.send("iphone", s);
        }

    }


    //@KafkaListener(topics = "iphone")
    public void consumer(ConsumerRecord<String, User> data, Acknowledgment ack) {

        System.out.println("data = " + data);

        //可以从ConsumerRecord 对象里面拿到详细数据， topic  partition offset 等等

        Optional<?> kafkaMessage = Optional.ofNullable(data.value());

        //得到Optional实例中的值

//        data = ConsumerRecord(topic = zhan, partition = 0, offset = 6, CreateTime = 1597022041062,
//                serialized key size = -1, serialized value size = 94,
//                headers = RecordHeaders(headers = [], isReadOnly = false), key = null,
//                value = {"password":"===========================","userName":"zhantongren谁谁谁谁谁谁谁谁谁"})

        Object value = data.value();
        System.out.println("value = " + value);

        // System.out.println("消费msg:"+msg);
        ack.acknowledge();

        // ack.acknowledge();
        // System.out.println("msg="+new Date()+msg);
    }

//    @KafkaListener(topics = {"hello"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            logger.info("----------------- record =" + record);
//            logger.info("------------------ message =" + message);
//        }
//    }

    //@Scheduled(cron = "1 * * * * ?")
    public void testHttpResponse() throws URISyntaxException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 定义请求的参数
        URI uri = new URIBuilder("http://127.0.0.1:8070/users").setParameter("wd", "java").build();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(uri);
        //response 对象
        CloseableHttpResponse response = null;

        try {
            // 执行http get请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("content = " + content);
                List<User> users = JSON.parseArray(content, User.class);
                System.out.println("users.size = " + users.size());
                for (User user : users) {
                    System.out.println("user = " + user);
                }

                FileUtils.writeStringToFile(new File("D:\\devtest\\a.txt"), content, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

    }


    //@Scheduled(cron = "1 * * * * ?")
    public void testRestTemplate() throws URISyntaxException, IOException {
        System.out.println("jinru  testRestTemplate......");
        // URI uri = new URIBuilder("http://127.0.0.1:8070/users").setParameter("wd", "java").build();

        User user = new User();
        user.setPassword("00000");
        user.setUserName("zhantongren");
        //Map  封装参数
        String juser = JSON.toJSONString(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("u", juser);
        jsonObject.put("password", "123456");
        String jsonString = JSON.toJSONString(jsonObject);
        String url = "http://127.0.0.1:8070/templateusers?name={name}";

        String jsonString1 = JSON.toJSONString(user);
        Map<String, String> map = new HashMap();
        map.put("name", jsonString1);
        //List<User> users =(List<User>)RestTemplate.getForObject(url, Object.class,jsonString);
        List<User> users = (List<User>) restTemplate.getForObject(url, Object.class, map);
        //List<User> users =(List<User>)RestTemplate.getForObject(url, Object.class,name);
//      //getForEntity  获取响应头+响应数据
        System.out.println("users = " + users);


    }


    //@Scheduled(cron = "1 * * * * ?")
    public void testjson() throws Exception {
        String url = "http://127.0.0.1:8070/testJson";
        restTemplate.getForObject(url, Object.class);

    }


    //@Scheduled(cron = "2 * * * * ?")
    public void testPost() throws Exception {
        String url = "http://127.0.0.1:8070/testPost";
        JSONObject requestParam = new JSONObject();
        requestParam.put("id", "231607");
        String jsonString = JSON.toJSONString(requestParam);
        //返回list
        // List<User> users = (List<User> )restTemplate.postForObject(url, requestParam, Object.class);

        User user = new User();
        user.setUserName("11111");
        user.setPassword("111111");
        String jsonString1 = JSON.toJSONString(user);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", "y");
        jsonObject.put("c", user);

        MultiValueMap<String, JSONObject> map = new LinkedMultiValueMap();
        map.add("param", jsonObject);
        List<User> users = restTemplate.postForObject(url, map, List.class);
        System.out.println("users = " + users);
    }

    //@Scheduled(cron = "1 * * * * ?")
    public void testRedis() throws Exception {
        String url = "http://127.0.0.1:8070/testRedis";
        restTemplate.postForObject(url, null, List.class);
    }


    //@Scheduled(cron = "1 * * * * ?")
    public void testPos2t() throws Exception {
        String url = "http://127.0.0.1:8070/testPost";
        JSONObject requestParam = new JSONObject();
        requestParam.put("userName", "zahntonrgen");
        requestParam.put("password", "111111");
//        requestParam.put("Content-Type","application/json");
//        requestParam.put("Content-EnCoding","UTF-8");
        String s = JSON.toJSONString(requestParam);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(s, requestHeaders);
        Object result = restTemplate.postForObject(url, requestEntity, Object.class);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(result));
        System.out.println("objects:" + jsonObject);

    }


}
