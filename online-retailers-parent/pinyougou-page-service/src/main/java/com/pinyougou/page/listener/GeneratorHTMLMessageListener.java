package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.page.listener
 * @company www.itheima.com
 */
public class GeneratorHTMLMessageListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        //1.接收消息 是 Long[] ids

        if(message instanceof ObjectMessage){

            try {
                ObjectMessage objectMessage = (ObjectMessage)message;
                Long[] ids  = (Long[]) objectMessage.getObject();
                //2.调用生成静态页面的方法即可
                for (Long id : ids) {
                    itemPageService.genHtml(id);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
