package com.pinyougou.manager.controller;

import com.pinyougou.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.shop.controller
 * @company www.itheima.com
 */
@RestController
public class FileUploadController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file) {

        try {
            //将接受到的文件流 上传到fastdfs
            //需要字节数组
            byte[] bytes = file.getBytes();
            //需要扩展名
            String originalFilename = file.getOriginalFilename();//获取原来文件名  kaola.jpg
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);


            FastDFSClient client = new FastDFSClient("classpath:config/fastdfs_client.conf");
            String parturl = client.uploadFile(bytes, extName);//  group1/M00/00/04/wKgZhVrhoreAcfkEAA1rIuRd3Es897.jpg
            //拼接全URL 回显到页面展示
            String url =  IMAGE_SERVER_URL+parturl;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
