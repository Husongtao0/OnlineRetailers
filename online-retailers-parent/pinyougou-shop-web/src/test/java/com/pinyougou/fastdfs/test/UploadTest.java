package com.pinyougou.fastdfs.test;

import com.pinyougou.util.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.Test;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.fastdfs.test
 * @company www.itheima.com
 */
public class UploadTest {

    //上传图片
    @Test
    public void testUpload() throws Exception {
        //1.创建一个配置文件  配置服务端的IP地址和端口

        //2.加载配置文件 初始化
        ClientGlobal.init("C:\\Users\\ThinkPad\\pinyougou28\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");

        //3.先创建一个trackerClient对象  直接nEW一个即可
        TrackerClient trackerClient = new TrackerClient();


        //4.通过trackerClient对象获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.定义一个storageServer对象 赋值为null 就可以了
        StorageServer storageServer = null;
        //6.构建一个storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        //7.上传图片
        //参数1：本地文件的路径
        //参数2：文件的扩展名  不要带“.”
        //参数3：元数据（图片的像素 大小 高度 时间戳）
        String[] jpgs = storageClient.upload_file("C:\\Users\\Public\\Pictures\\Sample Pictures\\Koala.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }


    }

    @Test
    public void testFASTClientTest() throws Exception{
        FastDFSClient client = new FastDFSClient("C:\\Users\\ThinkPad\\pinyougou28\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = client.uploadFile("C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg", "jpg");
        System.out.println(jpg);
    }

}
