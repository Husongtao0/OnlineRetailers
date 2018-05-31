package com.pinyougou.page.service;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.page.service
 * @company www.itheima.com
 */
public interface ItemPageService {

    /**
     * 根据SPU的商品的ID 生成这个商品的详情页面
     * @param goodsId
     * @return
     */
    public boolean genHtml(Long goodsId);
}
