package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.util.PhoneFormatCheckUtils;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smsCode){
		try {
			//校验用户验证码是否正确
			boolean flag = PhoneFormatCheckUtils.isPhoneLegal(user.getPhone());
			if(!flag){
				return new Result(false,"手机号不正确");
			}
			boolean isRight = userService.checkSmsCode(smsCode,user.getPhone());
			if(isRight) {
				userService.add(user);
				return new Result(true, "增加成功");
			}else{
				return new Result(false, "验证失败");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	@RequestMapping("/sendSms")
	public Result createSms(String mobile){

		//CAS和springscurity集成之后，可以通过这个方式来获取用户的名称。
		String name = SecurityContextHolder.getContext().getAuthentication().getName();

		boolean flag = PhoneFormatCheckUtils.isPhoneLegal(mobile);
		if(!flag){
			return new Result(false,"手机号不正确");
		}

		//生成验证码 发送短信
		try {
			userService.createSms(mobile);
			return new Result(true,"注意查看手机");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"发送失败");
		}
	}
	//接收 请求 获取用户名  显示到页面中

	
}
