package cn.appsys.service.backend.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.backenduser.BackendUserMapper;
import cn.appsys.pojo.BackendUser;
import cn.appsys.service.backend.BackendUserService;

@Service("backendUserService")
public class BackendUserServiceImpl implements BackendUserService{

	@Resource(name="backendUserMapper")
	BackendUserMapper backendUserMapper;

	public BackendUser getDevUserByCode(String userCode, String userPassword) {
		BackendUser backendUser=null;
		try {
			backendUser = backendUserMapper.getBanckendUserByCode(userCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return backendUser;
	}
}
