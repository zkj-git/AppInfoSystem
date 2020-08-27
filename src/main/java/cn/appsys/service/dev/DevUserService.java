package cn.appsys.service.dev;

import cn.appsys.pojo.DevUser;

public interface DevUserService {
	
	/**
	 * 根据用户编码查询用户
	 * 实现登录功能
	 * @param devCode
	 * @return
	 */
	public DevUser getDevUserByCode(String devCode,String devPassword);

}
