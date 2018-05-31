package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SpringDataSolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd(){
        //从数据库查询 数据放入到TBitem pojo中
        //将数据先存入TBitem   将POJO中的属性所对应的数据 添加到文档中
        //document.addField("pojo.id",pojo.getId());
        //solrTemplate.saveBean()

        TbItem tbItem = new TbItem();
        tbItem.setId(10000l);
        tbItem.setTitle("钛灰色");
        solrTemplate.saveBean(tbItem);//添加
        solrTemplate.commit();

//        for (int i = 0; i < 100; i++) {
//            TbItem tbItem = new TbItem();
//            tbItem.setId(Long.valueOf(10+i));
//            tbItem.setTitle("测试spring data solr Feild注解的商品"+i);
//            solrTemplate.saveBean(tbItem);//添加
//            solrTemplate.commit();
//        }

    }

    //删除
    @Test
    public void testDelet(){

        solrTemplate.deleteById("test001");
        solrTemplate.commit();
    }

    @Test
    public void testDeletQuery(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //查询
    @Test
    public void testQuery(){
        //1.创建一个查询的对象

        Query query = new SimpleQuery("*:*");
        query.setOffset(1);//第一页
        query.setRows(2);//每页显示2行
        Criteria criteria = new Criteria("item_title");
        criteria.contains("测试");//item_title:测试
        query.addCriteria(criteria);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);

        System.out.println("查询的总命中数："+tbItems.getTotalElements());
        //获取分页后的结果集
        List<TbItem> content = tbItems.getContent();
        for (TbItem item : content) {
            System.out.println(item.getId());
            System.out.println(item.getTitle());
        }

        System.out.println("当前的页显示的行数："+tbItems.getNumberOfElements());


    }

}
