package cn.appsys.service.dev.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appcategory.AppCategoryMapper;
import cn.appsys.pojo.AppCategory;
import cn.appsys.service.dev.AppCategoryService;

@Service("appCategoryService")
public class AppCategoryServiceImpl implements AppCategoryService {

	@Resource
	AppCategoryMapper appCategoryMapper;
	
	public List<AppCategory> getCateGoryByParentId(List<Integer> parentIds) {
		return appCategoryMapper.getCateGoryByParentId(parentIds);
	}

}
