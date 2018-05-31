package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */


@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}



	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemMapper itemMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//1.插入商品SPU的基本信息表
		TbGoods goods1 = goods.getGoods();
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goods1.setAuditStatus("0");//默认是没有被审核的
		goodsMapper.insert(goods1);
		//2.插入商品的SPU的描述表
		goodsDesc.setGoodsId(goods1.getId());
		goodsDescMapper.insert(goodsDesc);

		//如果不启用规格  就是单品

		if(!"1".equals(goods1.getIsEnableSpec())){
			//是单品
			TbItem item = new TbItem();
			item.setTitle(goods1.getGoodsName());//SPU的名称
			item.setPrice(goods1.getPrice());
			item.setNum(9999);//给一个默认的值
			item.setStatus("1");//启用
			item.setIsDefault("1");//默认
			item.setSpec("{}");


			//设置图片路径

			String itemImages = goodsDesc.getItemImages();
			if (itemImages != null) {
				List<Map> imagesList = JSON.parseArray(itemImages, Map.class);//map：{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}
				item.setImage((String) imagesList.get(0).get("url"));
			}
			//设置三级类目的ID
			item.setCategoryid(goods1.getCategory3Id());
			//设置三级类目的名称
			System.out.println(goods1.getCategory3Id());
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());

			item.setCategory(tbItemCat.getName());

			//构建创建时间
			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());
			//设置goods_id:
			item.setGoodsId(goods1.getId());

			//设置商家的ID
			item.setSellerId(goods1.getSellerId());

			//设置商家的店铺名
			item.setSeller(sellerMapper.selectByPrimaryKey(goods1.getSellerId()).getNickName());

			//设置品牌名称

			item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());
			//保存

			itemMapper.insert(item);
		}else {
			//3.插入商品的SKU列表
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				//补全其他的属性
				//黑马手机  移动4G 16G 黑色
				//设置标题
				String spec = item.getSpec();//{"网络":"移动3G","机身内存":"16G"},
				String title = goods1.getGoodsName();//SPU商品名称
				Map<String, Object> map = JSON.parseObject(spec, Map.class);//{"网络":"移动3G","机身内存":"16G"},
				for (String key : map.keySet()) {
					title += map.get(key) + " ";
				}
				item.setTitle(title);

				//设置图片路径

				String itemImages = goodsDesc.getItemImages();
				if (itemImages != null) {
					List<Map> imagesList = JSON.parseArray(itemImages, Map.class);//map：{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}
					item.setImage((String) imagesList.get(0).get("url"));
				}
				//设置三级类目的ID
				item.setCategoryid(goods1.getCategory3Id());
				//设置三级类目的名称
				System.out.println(goods1.getCategory3Id());
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());

				item.setCategory(tbItemCat.getName());

				//构建创建时间
				item.setCreateTime(new Date());
				item.setUpdateTime(item.getCreateTime());
				//设置goods_id:
				item.setGoodsId(goods1.getId());

				//设置商家的ID
				item.setSellerId(goods1.getSellerId());

				//设置商家的店铺名
				item.setSeller(sellerMapper.selectByPrimaryKey(goods1.getSellerId()).getNickName());

				//设置品牌名称

				item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());
				//保存
				itemMapper.insert(item);
			}
		}


	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		TbGoods goods1 = goods.getGoods();
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goods1.setAuditStatus("0");
		//更新 商品的基本信息
		goodsMapper.updateByPrimaryKeySelective(goods1);
		//更新商品的描述信息
		goodsDescMapper.updateByPrimaryKeySelective(goodsDesc);
		//更新商品的SKU的列表
		//先删除原来的SKU的列表  然后再新增
		//delete from tbitem where goods_id=1
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(goods1.getId());//要删除的数据
		itemMapper.deleteByExample(exmaple);

		if(!"1".equals(goods1.getIsEnableSpec())){
			//是单品
			TbItem item = new TbItem();
			item.setTitle(goods1.getGoodsName());//SPU的名称
			item.setPrice(goods1.getPrice());
			item.setNum(9999);//给一个默认的值
			item.setStatus("1");//启用
			item.setIsDefault("1");//默认
			item.setSpec("{}");


			//设置图片路径

			String itemImages = goodsDesc.getItemImages();
			if (itemImages != null) {
				List<Map> imagesList = JSON.parseArray(itemImages, Map.class);//map：{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}
				item.setImage((String) imagesList.get(0).get("url"));
			}
			//设置三级类目的ID
			item.setCategoryid(goods1.getCategory3Id());
			//设置三级类目的名称
			System.out.println(goods1.getCategory3Id());
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());

			item.setCategory(tbItemCat.getName());

			//构建创建时间
			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());
			//设置goods_id:
			item.setGoodsId(goods1.getId());

			//设置商家的ID
			item.setSellerId(goods1.getSellerId());

			//设置商家的店铺名
			item.setSeller(sellerMapper.selectByPrimaryKey(goods1.getSellerId()).getNickName());

			//设置品牌名称

			item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());
			//保存

			itemMapper.insert(item);
		}else {//新增 前面已经删除了
			//3.插入商品的SKU列表
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				//补全其他的属性
				//黑马手机  移动4G 16G 黑色
				//设置标题
				String spec = item.getSpec();//{"网络":"移动3G","机身内存":"16G"},
				String title = goods1.getGoodsName();//SPU商品名称
				Map<String, Object> map = JSON.parseObject(spec, Map.class);//{"网络":"移动3G","机身内存":"16G"},
				for (String key : map.keySet()) {
					title += map.get(key) + " ";
				}
				item.setTitle(title);

				//设置图片路径

				String itemImages = goodsDesc.getItemImages();
				if (itemImages != null) {
					List<Map> imagesList = JSON.parseArray(itemImages, Map.class);//map：{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}
					item.setImage((String) imagesList.get(0).get("url"));
				}
				//设置三级类目的ID
				item.setCategoryid(goods1.getCategory3Id());
				//设置三级类目的名称
				System.out.println(goods1.getCategory3Id());
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());

				item.setCategory(tbItemCat.getName());

				//构建创建时间
				item.setCreateTime(new Date());
				item.setUpdateTime(item.getCreateTime());
				//设置goods_id:
				item.setGoodsId(goods1.getId());

				//设置商家的ID
				item.setSellerId(goods1.getSellerId());

				//设置商家的店铺名
				item.setSeller(sellerMapper.selectByPrimaryKey(goods1.getSellerId()).getNickName());

				//设置品牌名称

				item.setBrand(brandMapper.selectByPrimaryKey(goods1.getBrandId()).getName());
				//保存
				itemMapper.insert(item);
			}
		}
		
//		//新增
//		List<TbItem> itemList = goods.getItemList();
//		for (TbItem item : itemList) {
//			itemMapper.insert(item);
//		}

	}	



	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//封装goods
		Goods goods = new Goods();

		//1.查询的是tbgoods
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//2.查询的是tbgoodsdesc
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		//3.查询的是商品的SKU列表（List<tbitem>）
		goods.setGoodsDesc(goodsDesc);
		TbItemExample exmaple = new TbItemExample();
		exmaple.createCriteria().andGoodsIdEqualTo(id);//select *from tbitem where goodsid=1
		List<TbItem> items = itemMapper.selectByExample(exmaple);
		goods.setItemList(items);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//改成逻辑删除
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete(true);//设置为要删除也就是1
			goodsMapper.updateByPrimaryKeySelective(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//查询未被删除的数据

			//criteria.andIsDeleteNotEqualTo("1"); //where is_delete is null
			criteria.andIsDeleteEqualTo(false);//查询未未删除的
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			//设置查询的条件
			if(goods.getIsDelete()!=null ){
				criteria.andIsDeleteEqualTo(goods.getIsDelete());
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}



    @Override
    public void updateStatus(Long[] ids, String status) {
		TbGoodsExample example = new TbGoodsExample();
		example.createCriteria().andIdIn(Arrays.asList(ids));

		TbGoods goods = new TbGoods();//表示要更新的数据  有值就更新
		goods.setAuditStatus(status);

		goodsMapper.updateByExampleSelective(goods,example);//update tb_goods set status=1 where id in (1,2,3)
	}


    @Override
    public List<TbItem> findTbItemList(Long[] ids) {


		TbItemExample exmaple = new TbItemExample();
		TbItemExample.Criteria criteria = exmaple.createCriteria();
		criteria.andStatusEqualTo("1");//select * from tbitem where status=1 and goodsid in (12,23)
		criteria.andGoodsIdIn(Arrays.asList(ids));
		List<TbItem> tbItems = itemMapper.selectByExample(exmaple);
		return tbItems;
    }

}
