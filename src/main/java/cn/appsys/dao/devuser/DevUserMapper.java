package cn.appsys.dao.devuser;

import cn.appsys.pojo.DevUser;

public interface DevUserMapper {
	
	/**
	 * 根据用户编码查询用户
	 * 实现登录功能
	 * @param devCode
	 * @return
	 */
	public DevUser getDevUserByCode(String devCode);
}
