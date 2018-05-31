package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/** 搜索相关
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.search.service
 * @company www.itheima.com
 */
public interface ItemSearchService {

    /**
     * 根据传递过来的数据封装到map中 去索引库中获取数据 返回Map
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);

    public void importSKUList(List<TbItem> items);
}
