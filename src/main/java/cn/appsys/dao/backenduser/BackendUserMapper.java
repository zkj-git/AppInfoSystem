package cn.appsys.dao.backenduser;

import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DevUser;
import org.apache.ibatis.annotations.Param;

public interface BackendUserMapper {
	
	public BackendUser getBanckendUserByCode(@Param("userCode")String userCode);
}
