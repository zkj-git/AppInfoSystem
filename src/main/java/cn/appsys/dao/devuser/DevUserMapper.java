package cn.appsys.dao.devuser;

import cn.appsys.pojo.DevUser;

public interface DevUserMapper {
	
	/**
	 * �����û������ѯ�û�
	 * ʵ�ֵ�¼����
	 * @param devCode
	 * @return
	 */
	public DevUser getDevUserByCode(String devCode);
}
