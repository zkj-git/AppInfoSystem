package cn.appsys.service.dev.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppVersion;
import cn.appsys.service.dev.AppVersionService;

@Service("appVersionService")
public class AppVersionServiceImpl implements AppVersionService {

	@Resource
	AppVersionMapper appVersionMapper;
	
	/**
	 * 查询APP版本信息
	 */
	public List<AppVersion> getAppVersionByAppId(Integer appId) {
		// TODO Auto-generated method stub
		return appVersionMapper.getAppVersionByAppId(appId);
	}

	public Integer addAppVersion(AppVersion appVersion) {
		int result = appVersionMapper.addAppVersion(appVersion);
		if (result>0) {
			return appVersion.getId();
		}
		return null;
	}

	public AppVersion getAppVersionById(Integer Id) {
		// TODO Auto-generated method stub
		return appVersionMapper.getAppVersionById(Id);
	}

	public boolean modifyAppVersion(AppVersion appVersion) {
		int result = appVersionMapper.modifyAppVersion(appVersion);
		if (result==1) {
			return true;
		}
		return false;
	}

	public boolean delAppVersion(Integer appId) {
		int result = appVersionMapper.delAppVersion(appId);
		if (result>0) {
			return true;
		}
		return false;
	}

}
