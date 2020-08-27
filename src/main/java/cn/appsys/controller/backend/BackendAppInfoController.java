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
	 * ����parentId��ѯ ���� �б�
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
	 * ������������APP��Ϣ�б�
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


		//����ƽ̨
		Integer flatformId=null;

		//һ������
		Integer categoryLevel1=null;

		//��������
		Integer categoryLevel2=null;

		//��������
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

		//��ȡһ������
		List<AppCategory> categoryLevel1List = appCategoryService.getCateGoryByParentId(null);
		//��ȡһ������id
		List<Integer> categoryLevel1IdList = new ArrayList<Integer>();
		if (!"".equals(queryCategoryLevel1)&&queryCategoryLevel1!=null) {
			categoryLevel1IdList.add(Integer.parseInt(queryCategoryLevel1));
		}else {
			for (AppCategory appCategory : categoryLevel1List) {
				categoryLevel1IdList.add(appCategory.getId());
			}
		}
		//��ȡ��������
		List<AppCategory> categoryLevel2List = appCategoryService.getCateGoryByParentId(categoryLevel1IdList);
		//��ȡ��������id
		List<Integer> categoryLevel2IdList = new ArrayList<Integer>();
		if (!"".equals(queryCategoryLevel2)&&queryCategoryLevel2!=null) {
			categoryLevel2IdList.add(Integer.parseInt(queryCategoryLevel2));
		}else {
			for (AppCategory appCategory : categoryLevel2List) {
				categoryLevel2IdList.add(appCategory.getId());
			}
		}
		//��ȡ��������
		List<AppCategory> categoryLevel3List = appCategoryService.getCateGoryByParentId(categoryLevel2IdList);


		//��ѯ����APP����ƽ̨
		List<DataDictionary> flatFormList= dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
		//��ѯ����APP״̬
		List<DataDictionary> statusList= dataDictionaryService.getDataDictionaryList("APP_STATUS");

		//��ȡҳ���С��Ĭ��Ϊ5
		int pageSize = Constants.pageSize;

		//���õ�ǰҳ��Ϊ1
		int currentPageNo = 1;

		//���õ�ǰҳ�������Ϊnull��ǰҳ��ΪpageIndex
		if (null!=pageIndex) {
			currentPageNo = Integer.parseInt(pageIndex);
		}

		//��ѯ�ܼ�¼��
		int totalCount = appInfoService.getAppInfoCount(softwareName, 1, flatformId, categoryLevel1, categoryLevel2, categoryLevel3);
		PageSupport pages = new PageSupport();
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		
		//���Ƶ�ǰҳ��
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
