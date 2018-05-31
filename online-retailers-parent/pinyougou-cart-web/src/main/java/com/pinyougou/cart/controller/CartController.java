package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.cart.util.CookieUtil;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * /k/**
 * /j/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.cart.controller
 * @company www.itheima.com
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){

        //1.判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        if(name.equals("anonymousUser")) {
            //2.如果没有登录  从cookie获取数据
            String cartList = CookieUtil.getCookieValue(request, "cartList", true);//获取cookie中的购物车列表的字符串  而且是经过转码的，这里就需要解码
            List<Cart> carts= new ArrayList<>();
            if(StringUtils.isNotBlank(cartList)){
                carts = JSON.parseArray(cartList, Cart.class);
            }
            return carts;
        }else {

            //3.如果有登录  从redis中获取数据

            List<Cart> oldfromRedis = cartService.getCartListFromRedis(name);

            System.out.println("从redis中获取数据");

            String cartList = CookieUtil.getCookieValue(request, "cartList", true);//获取cookie中的购物车列表的字符串  而且是经过转码的，这里就需要解码
            List<Cart> carts= new ArrayList<>();
            if(StringUtils.isNotBlank(cartList)){
                carts = JSON.parseArray(cartList, Cart.class);
            }


            if(carts.size()==0){
                return oldfromRedis;
            }


            List<Cart> cartListmerge = cartService.mergeCartList(carts, oldfromRedis);//合并cookie中的购物车到redis中


            //将最新合并后的数据  插入到redis中
            cartService.saveCartListToRedis(cartListmerge,name);

            //删除cookie中的数据
            CookieUtil.deleteCookie(request,response,"cartList");
            //重新查询redis的数据
            List<Cart> cartListnew = cartService.getCartListFromRedis(name);
            return cartListnew;

        }
//        return null;
    }

    /**
     * 添加购物车
     * @return
     */
    @RequestMapping("/addCartToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addCartToCartList(Integer num, Long itemId, HttpServletRequest request, HttpServletResponse response){
        //表示在服务端添加头信息允许跨域
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        //表示接受请求携带cookie等认证的信息  如果涉及到cookie的操作就必须开启他  如果开启认证，那么Access-Control-Allow-Origin 对应的值不能是* 只能是指定的域
       // response.setHeader("Access-Control-Allow-Credentials", "true");
        //1.判断用户是否登录
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);

        if(name.equals("anonymousUser")) {
            //2.如果没有登录  从cookie获取数据 操作cookie

            //1.先从cookie中获取已有的购物车的列表

            String cartList = CookieUtil.getCookieValue(request, "cartList", true);//获取cookie中的购物车列表的字符串  而且是经过转码的，这里就需要解码
            List<Cart> carts= new ArrayList<>();
            if(StringUtils.isNotBlank(cartList)){
               carts = JSON.parseArray(cartList, Cart.class);
            }

            //2.向已有的购物车的列表中添加商品（写一个方法）  返回一个最新的购物车的列表

            List<Cart> cartsnew = cartService.addCartToCartList(carts, itemId, num);

            //3.将最新的购物车的列表 存入cookie中
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartsnew),24*3600,true);//要转码是UTF-8
            return new Result(true,"添加成功");

        }else {
            //3.如果有登录  从redis中获取数据  操作redis
            //1.从redis中获取已有的购物车的数据    key:cartList   field:用户名  value:List<cart>
            List<Cart> oldfromRedis = cartService.getCartListFromRedis(name);
            //2.向已有的购物车的列表中添加商品（写一个方法）  返回一个最新的购物车的列表
            List<Cart> cartsnewfromRedis = cartService.addCartToCartList(oldfromRedis, itemId, num);
            //3.将新的购物车的列表又存入到redis中
            cartService.saveCartListToRedis(cartsnewfromRedis,name);

            return new Result(true,"添加成功");
        }
//        return  new Result(true,"添加成功");
    }
}
