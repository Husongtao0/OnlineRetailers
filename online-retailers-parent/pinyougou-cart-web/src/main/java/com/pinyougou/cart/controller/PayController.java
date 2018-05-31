package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.util.IdWorker;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.cart.controller
 * @company www.itheima.com
 */
@RequestMapping("/pay")
@RestController
public class PayController {


    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private OrderService orderService;

    //调用微信的统一下单的API 生成二维码的连接的URL 返回页面  由页面生成一个二维码 ，并且显示金额 和交易订单号
    @RequestMapping("/createNative")
    public Map createNative(){
        //订单号要生成的
       // IdWorker idWorker = new IdWorker(0, 0);//这个时候需要从redis中获取 out_trade_no
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLogFromRedis = orderService.getPayLogFromRedis(userId);
        if(payLogFromRedis!=null) {
            Map aNative = weiXinPayService.createNative(payLogFromRedis.getOutTradeNo(), payLogFromRedis.getTotalFee()+"");//单位就是分
            return aNative;
        }
        return new HashMap();
    }

    /**
     * 交易订单号
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        Result result  = null;
        //1.调用接口查询数据
        int x=0;
        while (true){
            //2.循环调用查询的接口 获取数据  判断  返回的状态是否为支付成功，直到支付成功为止

            Map map = weiXinPayService.queryStatus(out_trade_no);

            //一致循环 直到支付成功
            if("SUCCESS".equals(map.get("trade_state"))){
                result = new Result(true,"支付成功");

                //成功需要 更新订单表中的状态
                //获取到微信给我们的返回值的transcation_id 设置到payLog中  更新其状态 和支付完成时间
                orderService.updateOrderPayLogStatus((String)map.get("transaction_id"),out_trade_no);
                break;
            }
            //隔3秒钟 执行一次
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置超时时间为5分钟
            x++;
            if(x>=100){
                //超时
                result = new Result(false,"支付超时");
                break;
            }
        }
        if(result==null){
            result = new Result(false,"支付失败");
        }
        return result;

    }

}
