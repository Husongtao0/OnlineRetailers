package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;
import entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

    void submitOrder(Long seckillId, String userId);

    //从redis中获取某一个用户下的订单  要用来生成支付的二维码
	public TbSeckillOrder getOrderFromRedis(String userId);

	//更新订单的状态  删除redis中的订单  再更新到数据库中
	void saveOrderFromRedisToDb(String userId, String out_trade_no, String transaction_id);

    void deleteOrderFromRedis(String userId, String out_trade_no);

}
