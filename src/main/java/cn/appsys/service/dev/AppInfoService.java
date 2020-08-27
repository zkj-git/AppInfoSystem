package cn.appsys.service.dev;

import java.util.List;

import cn.appsys.pojo.AppInfo;

public interface AppInfoService {
	public List<AppInfo> getAppInfoList(String softwareName,Integer status,Integer flatformId,Integer categoryLevel1,Integer categoryLevel2,Integer categoryLevel3,Integer currentPageNo,Integer pageSize);

	public int getAppInfoCount(String softwareName,Integer status,Integer flatformId,Integer categoryLevel1,Integer categoryLevel2,Integer categoryLevel3);

	public boolean addAppInfo(AppInfo appInfo);
	
	public boolean getAppInfoByApkName(String apkName);
	
	
	public AppInfo getAppInfoById(Integer id);
	
	
	public boolean modifyAppInfoById(AppInfo appInfo);
	
	public boolean delAppInfo(Integer id);
	
	
	public boolean modifyStatus(Integer status,Integer id);

}
