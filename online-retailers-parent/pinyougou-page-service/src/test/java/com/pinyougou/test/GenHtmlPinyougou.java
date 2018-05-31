package com.pinyougou.test;

import com.pinyougou.page.service.ItemPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.test
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class GenHtmlPinyougou {

    @Autowired
    private ItemPageService itemPageService;
    @Test
    public void gent(){
        itemPageService.genHtml(149187842867961l);
    }
}
