package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.cart.service.impl
 * @company www.itheima.com
 */
@Service(timeout = 6000)
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper mapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 向已有的购物车列表中 添加商品  返回一个最新的购物车的列表
     * @param cartList  原来旧的购物车列表
     * @param itemId  要添加的商品的ID
     * @param num  数量
     * @return
     */
    @Override
    public List<Cart> addCartToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //List<Cart>     [{sellerId:123,orderList:[{商品的数据},{}]},{sellerId:124,orderList:[{},{}]}]

        //1.根据商品的ID 获取商品的数据
        TbItem item = mapper.selectByPrimaryKey(itemId);
        //2.先获取该商品对应的商家的ID     用于判断
        String sellerId = item.getSellerId();


        Cart cart =  searchCartBySellerId(cartList,sellerId);

        if(cart!=null) {
             List<TbOrderItem> orderItemList = cart.getOrderItemList();
             TbOrderItem orderItem = searchOrderItemByItemId(orderItemList,itemId);
             if(orderItem!=null) {
                 //3.根据获取到的商家的ID  在已有的购物车列表中判断是否存在   如果存在         说明  已经有该商家了

                 // 判断该商家对应的 明细列表中是否包含 要添加的商品   如果存在  ----》更新数量  更新金额
                 orderItem.setNum(orderItem.getNum()+num);//更新数量
                 orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));

                 if(orderItem.getNum()<=0){
                     //移除商品
                     orderItemList.remove(orderItem);
                 }

                 if(orderItemList.size()==0){
                     cartList.remove(cart);
                 }


             }else {

                 // 判断该商家对应的 明细列表中是否包含 要添加的商品   如果不存在  ----》添加商品

                 TbOrderItem orderItem1 = new TbOrderItem();
                 //补全数据
                 orderItem1.setItemId(itemId);
                 orderItem1.setGoodsId(item.getGoodsId());
                 orderItem1.setTitle(item.getTitle());
                 orderItem1.setPrice(item.getPrice());
                 orderItem1.setNum(num);//新添加的
                 orderItem1.setTotalFee(new BigDecimal(orderItem1.getNum()*orderItem1.getPrice().doubleValue()));
                 orderItem1.setPicPath(item.getImage());
                 orderItem1.setSellerId(sellerId);
                 orderItemList.add(orderItem1);
             }

        }else {
            //4 根据获取到的商家的ID  在已有的购物车列表中判断是否存在   不存在    说明要添加的商品  不存在  商品所属的商家也不存在
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());//店铺
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem2 = new TbOrderItem();
            //补全属性
            orderItem2.setItemId(itemId);
            orderItem2.setGoodsId(item.getGoodsId());
            orderItem2.setTitle(item.getTitle());
            orderItem2.setPrice(item.getPrice());
            orderItem2.setNum(num);//新添加的
            orderItem2.setTotalFee(new BigDecimal(orderItem2.getNum()*orderItem2.getPrice().doubleValue()));
            orderItem2.setPicPath(item.getImage());
            orderItem2.setSellerId(item.getSellerId());

            orderItemList.add(orderItem2);//添加全新的商品
            cart.setOrderItemList(orderItemList);
            

            cartList.add(cart);

        }

        return cartList;
    }

    @Override
    public List<Cart> getCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartsnewfromRedis, String name) {
        redisTemplate.boundHashOps("cartList").put(name,cartsnewfromRedis);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
//        hebing
        //循环遍历 cookieList  获取到它的元素  然后到 redisList中进行对比  （如果要添加的商品  在已有的redis中的列表中存在，则数量相加 否则  直接添加）

        for (Cart cart : cookieList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            for (TbOrderItem orderItem : orderItemList) {
                Long itemId = orderItem.getItemId();
                //循环遍历cookie中的商品  把cookie中的商品添加到已有的购物车列表中 （合并）
                redisList= addCartToCartList(redisList,itemId,orderItem.getNum());
            }

            
        }
        return redisList;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
