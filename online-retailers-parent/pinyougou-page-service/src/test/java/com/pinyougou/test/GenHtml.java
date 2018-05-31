package com.pinyougou.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.test
 * @company www.itheima.com
 */
public class GenHtml {

    //模板 +  数据集 =html
    //模板 后缀 官方推荐是使用ftl 但是可以是任意的后缀
    @Test
    public void GenHtml() throws Exception{
        //1.创建一个配置对象congfiguration 对象
        Configuration configuration = new Configuration(Configuration.getVersion());

        //2.设置模板所在的目录（路径）
        configuration.setDirectoryForTemplateLoading(new File("C:\\Users\\ThinkPad\\pinyougou28\\pinyougou-parent\\pinyougou-page-service\\src\\main\\resources\\template"));

        //3.设置模板的文件的字符编码 utf-8
        configuration.setDefaultEncoding("utf-8");

        //4.加载模板 到模板对象中
        //参数是相对设置好的模板的目录的路径
        Template template = configuration.getTemplate("template.ftl");

        //5.设置数据集（将来应该从数据库中获取数据）
        Map model = new HashMap();
        model.put("hello","world");

        //遍历输出的List


        //输出 一个日期类型
        model.put("date",new Date());

        //先定义一个输出流对象---》指定生成静态页面的文件名和目录
        FileWriter out = new FileWriter(new File("G:\\freemarker\\hello.html"));
        //6.调用输出文本的方法输出一个html文件
        template.process(model,out);
        //7.关闭流 在finally
        out.close();
    }
}
