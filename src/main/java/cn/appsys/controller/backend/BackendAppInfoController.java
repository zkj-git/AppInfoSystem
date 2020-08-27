package cn.appsys.controller.backend;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.service.dev.AppCategoryService;
import cn.appsys.service.dev.AppInfoService;
import cn.appsys.service.dev.AppVersionService;
import cn.appsys.service.dev.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/manage/backend/app")
public class BackendAppInfoController {
	@Resource
	AppCategoryService appCategoryService;
	@Resource
	DataDictionaryService dataDictionaryService;
	@Resource
	AppInfoService appInfoService;
	@Resource
	AppVersionService appVersionService;
	
	
	@RequestMapping(value="/checksave")
	public String name(String id,String status) {
		if (appInfoService.modifyStatus(Integer.parseInt(status), Integer.parseInt(id))) {
			return "redirect:list";
		}
		return "check";
	}
	
	
	@RequestMapping(value="/check")
	public String check(String aid,String vid,Model model) {
		AppInfo appInfo = null;
		try {
			appInfo = appInfoService.getAppInfoById(Integer.parseInt(aid));
		}catch (Exception e) {
			e.printStackTrace();
		}
		AppVersion appVersion = appVersionService.getAppVersionById(Integer.parseInt(vid));
		model.addAttribute(appInfo);
		model.addAttribute("appVersion", appVersion);
		return "backend/appcheck";
	}
	
	
	/**
	 * 根据parentId查询 分类 列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/categorylevellist.json")
	@ResponseBody
	public Object getAppCateGoryList(String pid) {
		List<Integer> pids = new ArrayList<Integer>();
		if (null!=pid) {
			pids.add(Integer.parseInt(pid));
		}
		List<AppCategory> appCategorielList = appCategoryService.getCateGoryByParentId(pids);
		return appCategorielList;
	}
	

	/**
	 * 根据条件查找APP信息列表
	 * @param model
	 * @param softwareName
	 * @param queryStatus
	 * @param queryFlatformId
	 * @param queryCategoryLevel1
	 * @param queryCategoryLevel2
	 * @param queryCategoryLevel3
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping(value="/list")
	public String appInfoList(Model model,@RequestParam(required=false,value="querySoftwareName")String softwareName,
			@RequestParam(required=false,value="queryFlatformId")String queryFlatformId,
			@RequestParam(required=false,value="queryCategoryLevel1")String queryCategoryLevel1,
			@RequestParam(required=false,value="queryCategoryLevel2")String queryCategoryLevel2,
			@RequestParam(required=false,value="queryCategoryLevel3")String queryCategoryLevel3,
			@RequestParam(required=false,value="pageIndex")String pageIndex,
			String pid) {


		//所属平台
		Integer flatformId=null;

		//一级分类
		Integer categoryLevel1=null;

		//二级分类
		Integer categoryLevel2=null;

		//三级分类
		Integer categoryLevel3=null;

		model.addAttribute("querySoftwareName", softwareName);
		if (!StringUtils.isNullOrEmpty(queryFlatformId)) {
			try {
				flatformId=Integer.parseInt(queryFlatformId);
				model.addAttribute("queryFlatformId", flatformId);
			} catch (NumberFormatException e) {
			}
		}
		if (!StringUtils.isNullOrEmpty(queryCategoryLevel1)) {
			try {
				categoryLevel1=Integer.parseInt(queryCategoryLevel1);
				model.addAttribute("queryCategoryLevel1", categoryLevel1);
			} catch (NumberFormatException e) {
			}
		}
		if (!StringUtils.isNullOrEmpty(queryCategoryLevel2)) {
			try {
				categoryLevel2=Integer.parseInt(queryCategoryLevel2);
				model.addAttribute("queryCategoryLevel2", categoryLevel2);
			} catch (NumberFormatException e) {
			}
		}
		if (!StringUtils.isNullOrEmpty(queryCategoryLevel3)) {
			try {
				categoryLevel3=Integer.parseInt(queryCategoryLevel3);
				model.addAttribute("queryCategoryLevel3", categoryLevel3);
			} catch (NumberFormatException e) {
			}
		}
		List<AppInfo> appinfoList = new ArrayList<AppInfo>();

		//获取一级分类
		List<AppCategory> categoryLevel1List = appCategoryService.getCateGoryByParentId(null);
		//获取一级分类id
		List<Integer> categoryLevel1IdList = new ArrayList<Integer>();
		if (!"".equals(queryCategoryLevel1)&&queryCategoryLevel1!=null) {
			categoryLevel1IdList.add(Integer.parseInt(queryCategoryLevel1));
		}else {
			for (AppCategory appCategory : categoryLevel1List) {
				categoryLevel1IdList.add(appCategory.getId());
			}
		}
		//获取二级分类
		List<AppCategory> categoryLevel2List = appCategoryService.getCateGoryByParentId(categoryLevel1IdList);
		//获取二级分类id
		List<Integer> categoryLevel2IdList = new ArrayList<Integer>();
		if (!"".equals(queryCategoryLevel2)&&queryCategoryLevel2!=null) {
			categoryLevel2IdList.add(Integer.parseInt(queryCategoryLevel2));
		}else {
			for (AppCategory appCategory : categoryLevel2List) {
				categoryLevel2IdList.add(appCategory.getId());
			}
		}
		//获取三级分类
		List<AppCategory> categoryLevel3List = appCategoryService.getCateGoryByParentId(categoryLevel2IdList);


		//查询所有APP所属平台
		List<DataDictionary> flatFormList= dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
		//查询所有APP状态
		List<DataDictionary> statusList= dataDictionaryService.getDataDictionaryList("APP_STATUS");

		//获取页面大小，默认为5
		int pageSize = Constants.pageSize;

		//设置当前页码为1
		int currentPageNo = 1;

		//设置当前页码如果不为null则当前页码为pageIndex
		if (null!=pageIndex) {
			currentPageNo = Integer.parseInt(pageIndex);
		}

		//查询总记录数
		int totalCount = appInfoService.getAppInfoCount(softwareName, 1, flatformId, categoryLevel1, categoryLevel2, categoryLevel3);
		PageSupport pages = new PageSupport();
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		
		//控制当前页码
		if (currentPageNo>totalCount) {
			currentPageNo = totalCount;
		}else if (currentPageNo<1) {
			currentPageNo = 1;
		}

		appinfoList = appInfoService.getAppInfoList(softwareName, 1, flatformId, categoryLevel1, categoryLevel2, categoryLevel3,(currentPageNo-1)*pageSize,pageSize);


		pages.setCurrentPageNo(currentPageNo);
		model.addAttribute("appInfoList", appinfoList);
		model.addAttribute("pages", pages);
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("categoryLevel2List", categoryLevel2List);
		model.addAttribute("categoryLevel3List", categoryLevel3List);
		model.addAttribute("flatFormList", flatFormList);
		model.addAttribute("statusList", statusList);

		return "backend/applist";
	}

}
