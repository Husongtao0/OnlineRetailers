package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbTypeTemplate;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou
 * @company www.itheima.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-*.xml")
public class testSolrCloud {


    //通过solrj来实现CRUD

    //添加索引
    @Test
    public void testSorlCloud() throws Exception{

        //1.建立连接  solrserver

        CloudSolrServer solrServer = new CloudSolrServer("192.168.25.154:2181,192.168.25.154:2182,192.168.25.154:2183");
        //必须要指定
        solrServer.setDefaultCollection("collection2");

        //2.添加文档

        SolrInputDocument document = new SolrInputDocument();
        document.addField("id","test003");
        document.addField("item_title","solrcloud值测试");
        //3.添加域
        //4.将文档添加索引库中
        solrServer.add(document);
        //5.提交
        solrServer.commit();

    }

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testSpringSorlCloud() throws Exception{
        TbItem tbItem = new TbItem();
        tbItem.setId(11100000l);
        tbItem.setTitle("cesispringsolrtemplate");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();

    }
}
