package com.pinyougou.pagehelper.test;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.pagehelper.test
 * @company www.itheima.com
 */

@RunWith(SpringRunner.class)
//ContextConfiguration：加载spring容器 并初始化
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class PageHelperTest {

    @Autowired
    private TbBrandMapper mapper;

    @Test
    public void pageHelper(){
        //设置分页的条件  每页显示 10行，当前的页码是1
        PageHelper.startPage(1, 10);
        //2.紧跟着的第一个查询才会被分页
        List<TbBrand> tbBrands = mapper.selectByExample(null);
        System.out.println("查询的第一个查询的数目是："+tbBrands.size());
        List<TbBrand> tbBrands2 = mapper.selectByExample(null);
        System.out.println("查询的第2个查询的数目是："+tbBrands2.size());

        //获取分页的信息 总记录数 第一种
        Page<TbBrand> page =  (Page)tbBrands;
        System.out.println(page.getTotal());

        //获取分页的信息 总记录数 第二种

        PageInfo<TbBrand> info = new PageInfo<>(tbBrands);
        System.out.println(info.getTotal());

    }
}
