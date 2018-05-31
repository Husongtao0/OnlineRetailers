package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.pay.service
 * @company www.itheima.com
 */
public interface WeiXinPayService {
    /**
     *
     * @param out_trade_no  交易订单号 由平有够来生成
     * @param total_fee   交易的金额（用户创建了订单的总金额）
     * @return
     */
    Map createNative(String out_trade_no, String total_fee);

    /**
     * 根据交易的订单号码，查询该订单是否支付成功
     * @param out_trade_no
     * @return  支付的结果（包括 支付成功、支付失败的状态以及值）
     */
    public Map queryStatus(String out_trade_no);

    /**
     * 根据交易订单号码 关闭订单
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);
}
