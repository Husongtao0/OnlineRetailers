package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou
 * @company www.itheima.com
 */
public class TestSolrJ {

    //add

    @Test
    public void testAdd() throws  Exception{
//        //1.创建一个连接对象
//        SolrServer solrServer = new HttpSolrServer("http://192.168.25.154:8080/solr");
//        //2.创建文档对象solrinputdocument
//        SolrInputDocument document = new SolrInputDocument();
//
//        //3.向文档添加域
//        document.addField("id","test001");//id域的数据类型是String---->pojo
//
//        document.addField("item_title","测试solrj的商品名");
//        //4.将文档添加到索引库中
//        solrServer.add(document);
//        //5.commit
//        solrServer.commit();

        SolrServer solrServer = new HttpSolrServer("http://192.168.25.154:8080/solr");
        TbItem tbItem = new TbItem();
        tbItem.setId(10l);
        tbItem.setTitle("测试Feild注解的商品");
        solrServer.addBean(tbItem);
        solrServer.commit();

    }

    @Test
    public void testQuery() throws  Exception{
        //1.创建一个连接对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.154:8080/solr");
        //2.创建一个查询的对象（里面是封装了所有的查询的语法）item_title:
        SolrQuery query = new SolrQuery("*:*");

        //3.执行查询
        QueryResponse response = solrServer.query(query);

       // List<TbItem> beans = response.getBeans(TbItem.class);

        //4.获取结果集
        SolrDocumentList results = response.getResults();
        //5.
        System.out.println("查询的总命中数:"+results.getNumFound());

        //循环遍历结果集
        for (SolrDocument result : results) {
            System.out.println(result.get("item_title"));
        }

    }
}
