package cn.appsys.dao.appinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appsys.pojo.AppInfo;

public interface AppInfoMapper {
	
	
	public List<AppInfo> getAppInfoList(@Param("softwareName")String softwareName,
									@Param("status")Integer status,
									@Param("flatformId")Integer flatformId,
									@Param("categoryLevel1")Integer categoryLevel1,
									@Param("categoryLevel2")Integer categoryLevel2,
									@Param("categoryLevel3")Integer categoryLevel3,
									@Param("currentPageNo")Integer currentPageNo,
									@Param("pageSize")Integer pageSize);
	
	public int getAppInfoCount(@Param("softwareName")String softwareName,
									@Param("status")Integer status,
									@Param("flatformId")Integer flatformId,
									@Param("categoryLevel1")Integer categoryLevel1,
									@Param("categoryLevel2")Integer categoryLevel2,
									@Param("categoryLevel3")Integer categoryLevel3);
	
	public int addAppInfo(AppInfo appInfo);
	
	
	public int getAppInfoByApkName(String apkName);
	
	
	public AppInfo getAppInfoById(Integer id);
	
	
	public int modifyAppInfoById(AppInfo appInfo);
	
	public int delAppInfo(Integer id);
	
	
	public int modifyStatus(@Param("status")Integer status,@Param("id")Integer id);
	
}
