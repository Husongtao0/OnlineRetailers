package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.util.PinyougouContants;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		try {
			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());//原来要更新的内容


		contentMapper.updateByPrimaryKey(content);
		try {
			//获取原来的分类的ID
			Long categoryId = tbContent.getCategoryId();//
			if(categoryId!=content.getCategoryId().longValue()) {
				redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(categoryId);
			}
			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			try {
				TbContent tbContent = contentMapper.selectByPrimaryKey(id);
				redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).delete(tbContent.getCategoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {

		//缓存目的：提高效率  ，缓存不能影响正常的业务逻辑。try catch

		//先从缓存中获取值，判断如果有数据 直接返回
		try {
			List<TbContent> content = (List) redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).get(categoryId);

			if(content!=null && content.size()>0){
				System.out.println("从缓存中获取");
				return content;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		TbContentExample exmaple = new TbContentExample();
		exmaple.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
		exmaple.setOrderByClause("sort_order");//order by sor_order asc 升序排列
		List<TbContent> contents = contentMapper.selectByExample(exmaple);

		//第一次从数据库中查询数据 将其数据存储在redis中
		try {
			System.out.println("从数据库中获取");
			redisTemplate.boundHashOps(PinyougouContants.TBCONTENT_REDIS_LUNBO_KEY).put(categoryId,contents);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//如果使用String  为了管理方便 可以在使用String类型的时候
		// 将String类型的key 用":"拼接起来  从桌面客户端看的时候就会出现有目录的请情况。以此来鉴别不同的数据

		return contents;
    }

}
