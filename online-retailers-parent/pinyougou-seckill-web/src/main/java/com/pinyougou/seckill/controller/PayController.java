package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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


    @Reference(timeout = 70000)
    private WeiXinPayService weiXinPayService;

    @Reference(timeout = 100000)
    private SeckillOrderService orderService;

    //调用微信的统一下单的API 生成二维码的连接的URL 返回页面  由页面生成一个二维码 ，并且显示金额 和交易订单号
    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //应该从redis中获取该用户的订单信息
        TbSeckillOrder seckillOrder = orderService.getOrderFromRedis(userId);
        System.out.println(">>>>>是否为空"+seckillOrder+">>>userId");

        System.out.println(">>>>");
        if(seckillOrder!=null) {
            Map aNative = weiXinPayService.createNative(seckillOrder.getId()+"", (long)(seckillOrder.getMoney().doubleValue()*100)+"");//单位就是分
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
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result  = null;
        //1.调用接口查询数据
        int x=0;
        while (true){
            //2.循环调用查询的接口 获取数据  判断  返回的状态是否为支付成功，直到支付成功为止

            Map map = weiXinPayService.queryStatus(out_trade_no);

//            orderService.saveOrderFromRedisToDb(userId,out_trade_no,(String)map.get("transaction_id"));

            //一致循环 直到支付成功
            if("SUCCESS".equals(map.get("trade_state"))){
                result = new Result(true,"支付成功");

                //更新订单的状态  然后删除redis中的订单  更新redis中的订单到数据库中
                orderService.saveOrderFromRedisToDb(userId,out_trade_no,(String)map.get("transaction_id"));
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
                //1.交易的订单的关闭（关闭微信的订单）
                Map map1 = weiXinPayService.closePay(out_trade_no);

                if("SUCCESS".equals(map1.get("return_code"))){
                    if("ORDERPAID".equals(map1.get("err_code"))){
                        //支付成功
                        //更新订单的状态  然后删除redis中的订单  更新redis中的订单到数据库中
                        orderService.saveOrderFromRedisToDb(userId,out_trade_no,(String)map.get("transaction_id"));
                        result = new Result(true,"支付成功");
                        break;

                    }
                }

                //关闭了订单
                if(result==null || !result.isSuccess()){
                    //2.将redis中的订单 删除  恢复库存
                    orderService.deleteOrderFromRedis(userId,out_trade_no);
                }
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
