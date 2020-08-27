package cn.appsys.service.dev;

import java.util.List;

import cn.appsys.pojo.AppVersion;

public interface AppVersionService {
	public List<AppVersion> getAppVersionByAppId(Integer appId);
	
	public Integer addAppVersion(AppVersion appVersion);
	
	public AppVersion getAppVersionById(Integer Id);
	
	public boolean modifyAppVersion(AppVersion appVersion);
	
	public boolean delAppVersion(Integer appId);
}
