package com.pinyougou.search.listener;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.search.listener
 * @company www.itheima.com
 */
public class SolrUpdateMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Autowired
    private GoodsService goodsService;
    @Override
    public void onMessage(Message message) {

        if(message instanceof ObjectMessage) {
            try {
                //接收消息
                ObjectMessage objectMessage = (ObjectMessage)message;
                //获取到的就是SPU的iD的数组
                Long[] ids = (Long[]) objectMessage.getObject();
                //从数据库中获取SKU的列表
                List<TbItem> items = goodsService.findTbItemList(ids) ;
                //将SKU的列表更新到索引库中
                itemSearchService.importSKUList(items);
            } catch (JMSException e) {
                e.printStackTrace();
            }


        }
    }
}
