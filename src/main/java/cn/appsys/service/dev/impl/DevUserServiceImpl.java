package cn.appsys.service.dev.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.devuser.DevUserMapper;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.dev.DevUserService;


@Service("devUserService")
public class DevUserServiceImpl implements DevUserService {
	
	@Resource(name="devUserMapper")
	DevUserMapper devUserMapper;
	
	public DevUser getDevUserByCode(String devCode, String devPassword) {
		DevUser devUser=null;
		
		try {
			devUser = devUserMapper.getDevUserByCode(devCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return devUser;
	}
	
	
	
	
	

}
