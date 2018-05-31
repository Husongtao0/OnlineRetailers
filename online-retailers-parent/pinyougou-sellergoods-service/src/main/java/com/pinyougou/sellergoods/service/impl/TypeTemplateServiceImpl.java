package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 5000)
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	@Autowired
	private RedisTemplate redisTemplate;

		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);

			List<TbTypeTemplate> all = findAll();

			for (TbTypeTemplate tbTypeTemplate : all) {
				//缓存品牌列表
				String brandIds = tbTypeTemplate.getBrandIds();
				List<Map> mapList = JSON.parseArray(brandIds, Map.class);
				redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),mapList);

				//缓存规格列表
				List<Map> specList = findSpecList(tbTypeTemplate.getId());
				redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
			}
			System.out.println("缓存了所有的模板对应的规格和品牌数据");
			//每一个操作CRUD 都需要经过这个方法  这里去实现缓存操作
			//大key： brandList   field:模板的ID    value：品牌列表
			//大key： specList   field:模板的ID    value：规格列表


		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbSpecificationOptionMapper optionMapper;

    @Override
    public List<Map> findSpecList(Long id) {
		//根据模板的ID 获取模板的对象
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);

		//获取模板对象中的规格列表字符串(没有带有规格选项的)
		String specIds = tbTypeTemplate.getSpecIds();
		List<Map> mapList = JSON.parseArray(specIds, Map.class);

		for (Map map : mapList) {
			//根据规格的ID 查询该规格对象的选项列表
			//select * from tbspecficationoption where specId=1
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			Integer id1 =(Integer) map.get("id");
			example.createCriteria().andSpecIdEqualTo(Long.valueOf(id1));
			List<TbSpecificationOption> options = optionMapper.selectByExample(example);
			map.put("options",options);
		}

		return mapList;
    }

}
