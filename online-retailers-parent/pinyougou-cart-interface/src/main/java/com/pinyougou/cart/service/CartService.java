package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.cart.service
 * @company www.itheima.com
 */
public interface CartService {

    /**
     * 向已有的购物车列表中添加商品
     * @param cartList  原来旧的购物车列表
     * @param itemId  要添加的商品的ID
     * @param num  数量
     * @return
     */
    public List<Cart> addCartToCartList(List<Cart> cartList,Long itemId,Integer num);

    List<Cart> getCartListFromRedis(String name);

    void saveCartListToRedis(List<Cart> cartsnewfromRedis, String name);

    /**
     * 传递两个集合
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieList,List<Cart> redisList);

}
