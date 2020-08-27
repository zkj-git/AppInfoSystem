package cn.appsys.dao.appcategory;

import java.util.List;

import cn.appsys.pojo.AppCategory;

public interface AppCategoryMapper {
	public List<AppCategory> getCateGoryByParentId(List<Integer> parentIds);
}
