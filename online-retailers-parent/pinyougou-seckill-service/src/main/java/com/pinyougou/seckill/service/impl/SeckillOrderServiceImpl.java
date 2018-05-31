package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 100000)
public class SeckillOrderServiceImpl implements SeckillOrderService {



	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper goodsMapper;

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;

	@Override
	public void submitOrder(Long seckillId, String userId) {
		//1.根据商品的ID从redis中获取商品对象
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

		//2.判断该对象是否存在   还有判断该商品的库存是否0
		if(seckillGoods==null){
			throw new RuntimeException("商品不存在啦");
		}

		if(seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已经抢购完成");
		}

		//3.将库存-1   再更新回redis中

		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);//库存-1

		redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);


		//如果数量为0 需要将数据更新到数据库 表示这个商品已经被抢完了   从redis中删除商品
		if(seckillGoods.getStockCount()==0){
			goodsMapper.updateByPrimaryKey(seckillGoods);//更新到数据库中

			redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
		}

		//4.创建订单  现在只在redis中操作
		TbSeckillOrder seckillOrder  = new TbSeckillOrder();

		seckillOrder.setId(new IdWorker(0,1).nextId());
		seckillOrder.setSeckillId(seckillGoods.getId());
		seckillOrder.setMoney(seckillGoods.getCostPrice());//设置秒杀价格

		seckillOrder.setUserId(userId);

		seckillOrder.setSellerId(seckillGoods.getSellerId());//所属的商品的商家

		seckillOrder.setCreateTime(new Date());//创建时间

		seckillOrder.setStatus("0");//未支付


		//创建订单

		redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);//用户下了这个订单
	}

	@Override
	public TbSeckillOrder getOrderFromRedis(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void saveOrderFromRedisToDb(String userId, String out_trade_no, String transaction_id) {
		//1.获取订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		System.out.println("=========================================================================");
		System.out.println(Long.valueOf(out_trade_no).longValue());
		System.out.println((Long)seckillOrder.getId());
		System.out.println(Long.valueOf(out_trade_no).longValue()==(Long)seckillOrder.getId());

		System.out.println("=========================================================================");
		if(seckillOrder==null){
			throw new RuntimeException("订单不存在");
		}
		if(Long.valueOf(out_trade_no).longValue()!=((Long)seckillOrder.getId())){
			throw new RuntimeException("订单号不一致");
		}

		//2.更新状态

		seckillOrder.setStatus("1");//已支付
		seckillOrder.setPayTime(new Date());//
		seckillOrder.setTransactionId(transaction_id);//交易流水

		//3.保存到数据库中
		seckillOrderMapper.insert(seckillOrder);

		//4.redis中的订单删除

		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

	@Override
	public void deleteOrderFromRedis(String userId, String out_trade_no) {
		//1.删除redis中的订单
		//2.恢复库存

		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if(seckillOrder!=null && seckillOrder.getId().longValue()==Long.valueOf(out_trade_no)){
			//删除订单
			redisTemplate.boundHashOps("seckillOrder").delete(userId);

			//恢复库存  ----》seckiilgoods

			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());


			if(seckillGoods!=null) {
				seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
			}else{
				//说明商品已经被抢购完了， 数据库中的商品的库存就会被更新为0
				//从数据库中获取该商品的数据 数据库中也变成库存为1 添加到redis中并且剩余库存就是1
				System.out.println(".....");
			}


		}


	}

}
