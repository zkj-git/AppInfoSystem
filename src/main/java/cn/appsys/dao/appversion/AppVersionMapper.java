package cn.appsys.dao.appversion;

import java.util.List;

import cn.appsys.pojo.AppVersion;

public interface AppVersionMapper {
	
	public List<AppVersion> getAppVersionByAppId(Integer appId);
	
	public int addAppVersion(AppVersion appVersion);
	
	public AppVersion getAppVersionById(Integer Id);
	
	public int modifyAppVersion(AppVersion appVersion);
	
	public int delAppVersion(Integer appId);
}
