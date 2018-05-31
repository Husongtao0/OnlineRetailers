package com.pinyougou.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.util
 * @company www.itheima.com
 */
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importDataToIndex(){
        //1.查询所有的商品的数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//有效的商品
        List<TbItem> items = tbItemMapper.selectByExample(example);
        //取出规格的数据 存入到map中
        for (TbItem item : items) {
            String spec = item.getSpec();//{"机身内存":"kkkk","屏幕尺寸":"4英寸"}

            Map specmap = JSON.parseObject(spec, Map.class);

            item.setSpecMap(specmap);

        }
        //2.存入索引库中
        solrTemplate.saveBeans(items);
        solrTemplate.commit();

    }


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importDataToIndex();
    }
}
