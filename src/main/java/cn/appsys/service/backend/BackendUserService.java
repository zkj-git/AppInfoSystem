package cn.appsys.service.backend;

import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DevUser;

public interface BackendUserService {

	public BackendUser getDevUserByCode(String userCode,String userPassword);
}
