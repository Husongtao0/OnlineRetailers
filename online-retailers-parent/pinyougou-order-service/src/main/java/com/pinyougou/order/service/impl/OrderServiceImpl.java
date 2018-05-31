package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.util.IdWorker;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //需要根据不同商家拆分订单

        //从redis中获取该用户的对应的购物车列表  循环遍历购物车列表   [{sellerID,orderItemList:[{},{}]},{}]

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //用于存放所有拆分的订单的Id的集合
        List orderIds = new ArrayList();
        double totalFee = 0;
        for (Cart cart : cartList) {
            //生成订单的ID
            IdWorker idWorker = new IdWorker(0, 0);
            long orderId = idWorker.nextId();//
            //设置属性值
            TbOrder neworder = new TbOrder();

            neworder.setOrderId(orderId);

            double money = 0;//对应每一个商家对应购买的商品的总金额
            //创建订单项
            for (TbOrderItem orderItem : cart.getOrderItemList()) {//用 Num*price  +
                money += orderItem.getTotalFee().doubleValue();//金额的累加

                //补全 tborderItem的属性
                long orderItemId = idWorker.nextId();

                orderItem.setId(orderItemId);
                orderItem.setSellerId(cart.getSellerId());


                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());//商品
                orderItem.setGoodsId(item.getGoodsId());//设置商品的SPU的ID
                orderItem.setOrderId(orderId);
                orderItemMapper.insert(orderItem);
            }

            //计算出来
            neworder.setPayment(new BigDecimal(money));


            neworder.setPaymentType(order.getPaymentType());//支付类型

            neworder.setStatus("1");//未付款，等到了支付成功之后由支付系统来更新这个字段
            neworder.setCreateTime(new Date());
            neworder.setUpdateTime(neworder.getCreateTime());
            neworder.setUserId(order.getUserId());
            neworder.setReceiverAreaName(order.getReceiverAreaName());//寄送的地址
            neworder.setReceiver(order.getReceiver());
            neworder.setReceiverMobile(order.getReceiverMobile());
            neworder.setSourceType(order.getSourceType());
            neworder.setSellerId(cart.getSellerId());//商家ID
            orderMapper.insert(neworder);

            orderIds.add(orderId + "");//字符串

            totalFee += money;//计算所有的商家对应的总金额 也就是 支付的时候的金额
        }

        //需要创建支付日志表的一条记录（应该是未支付的状态）
        TbPayLog payLog = new TbPayLog();
        //设置属性的值
        payLog.setTradeState("0");//未支付
        payLog.setCreateTime(new Date());

        System.out.println(orderIds);
        String s = orderIds.toString();
        String orderIdstring = s.replace("[", "").replace("]", "");
        //设置订单的Id列表 以逗号分隔
        payLog.setOrderList(orderIdstring);
        payLog.setOutTradeNo(new IdWorker(0, 0).nextId() + "");//生成的主键

        payLog.setPayType("1");//微信支付

        payLog.setUserId(order.getUserId());

        payLog.setTotalFee((long) (totalFee * 100));//所有的订单的总金额  一定要是 单位为分
        payLogMapper.insert(payLog);

        //就是 将这个数据 存储在redis中  将来 在 创建支付二维码的时候提前获取数据将     总金额 和 支付的订单号获取出来 传递过去给微信
        redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }

    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("1");
        a.add("2");
        String orderIdstring = a.toString().replace("[", "").replace("]", "");
        System.out.println(orderIdstring);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public TbPayLog getPayLogFromRedis(String userId) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        return payLog;
    }

    @Override
    public void updateOrderPayLogStatus(String transaction_id, String out_trade_no) {
        //1.获取PayLog对象
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);

        //2.更新payLog的值
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(transaction_id);//更新流水号
        payLog.setPayTime(new Date());//设置支付的时间

        //3.更新订单中的状态 表示已经支付成功了
        String orderList = payLog.getOrderList();//订单的ID

        String[] split = orderList.split(",");

        for (String orderId : split) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));//订单
            tbOrder.setStatus("2");//已付款
            tbOrder.setPaymentTime(new Date());//
            orderMapper.updateByPrimaryKey(tbOrder);//更新
        }

        payLogMapper.updateByPrimaryKey(payLog);
        //4.删除redis中的支付日志记录

        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

}
