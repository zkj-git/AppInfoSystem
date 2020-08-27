package cn.appsys.service.dev.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.service.dev.AppInfoService;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {
	
	@Resource
	AppInfoMapper appInfoMapper;

	/**
	 * 查讯App信息列表
	 */
	public List<AppInfo> getAppInfoList(String softwareName, Integer status, Integer flatformId, Integer categoryLevel1,
			Integer categoryLevel2, Integer categoryLevel3, Integer currentPageNo, Integer pageSize) {
		return appInfoMapper.getAppInfoList(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3, currentPageNo, pageSize);
	}
	
	/**
	 * 查讯app信息记录数
	 */
	public int getAppInfoCount(String softwareName, Integer status, Integer flatformId, Integer categoryLevel1,
			Integer categoryLevel2, Integer categoryLevel3) {
		return appInfoMapper.getAppInfoCount(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3);
	}

	/**
	 * 新增app信息
	 */
	public boolean addAppInfo(AppInfo appInfo) {
		int result = appInfoMapper.addAppInfo(appInfo);
		if (result>0) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 根据apkName查找app信息
	 */
	public boolean getAppInfoByApkName(String apkName) {
		int result = appInfoMapper.getAppInfoByApkName(apkName);
		if (result>0) {
			return false;
		}
		return true;
	}

	public AppInfo getAppInfoById(Integer id) {
		// TODO Auto-generated method stub
		return appInfoMapper.getAppInfoById(id);
	}

	public boolean modifyAppInfoById(AppInfo appInfo) {
		int result = appInfoMapper.modifyAppInfoById(appInfo);
		if (result>0) {
			return true;
		}
		return false;
	}

	public boolean delAppInfo(Integer id) {
		int result = appInfoMapper.delAppInfo(id);
		if (result==1) {
			return true;
		}
		return false;
	}

	public boolean modifyStatus(Integer status,Integer id) {
		int result = appInfoMapper.modifyStatus(status,id);
		
		if (result==1) {
			return true;
		}
		return false;
	}

}
