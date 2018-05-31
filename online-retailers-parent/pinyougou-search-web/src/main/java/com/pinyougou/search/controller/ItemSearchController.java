package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.search.controller
 * @company www.itheima.com
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference(timeout = 3000)
    private ItemSearchService itemSearchService;


    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @RequestMapping("/search")
    public Map search(@RequestBody  Map searchMap){
        System.out.println(">>>>"+searchMap.get("keywords"));
        Map resultmap = itemSearchService.search(searchMap);
        return resultmap;
    }
}
