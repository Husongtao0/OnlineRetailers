package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package com.pinyougou.page.service.impl
 * @company www.itheima.com
 */
@Service(timeout = 3000)
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genHtml(Long goodsId) {
        try {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>goodsId"+goodsId);
            //1.创建configuration对象  设置模板所在的目录  设置字符集
            Configuration configuration = freeMarkerConfigurer.getConfiguration();

            Template template = configuration.getTemplate("item.ftl");

            //2.设置数据集  SPU的数据  spu对应的描述的信息
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            Map model = new HashMap();
            model.put("goods",goods);
            model.put("goodsDesc",goodsDesc);
            //查询SKU的列表数据
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");//有效的商品
            //按照is_default 降序排列
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            model.put("itemList",itemList);
            //3.创建一个流对象
            FileWriter writer = new FileWriter(new File("G:\\item\\"+goodsId+".html"));

            //4.输出
            template.process(model,writer);

            //5.关闭流
            writer.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
