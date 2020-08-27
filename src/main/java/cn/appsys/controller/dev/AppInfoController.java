package cn.appsys.controller.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.dev.AppCategoryService;
import cn.appsys.service.dev.AppInfoService;
import cn.appsys.service.dev.AppVersionService;
import cn.appsys.service.dev.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/dev/flatform/app")
public class AppInfoController {
	@Resource
	AppCategoryService appCategoryService;
	@Resource
	DataDictionaryService dataDictionaryService;
	@Resource
	AppInfoService appInfoService;
	@Resource
	AppVersionService appVersionService;
	

	@RequestMapping(value = "/{id}/sale.json", method = RequestMethod.PUT)
	@ResponseBody
	public Object sale(@PathVariable String id) {
		Integer appId = Integer.parseInt(id);
		Map<String, String> map = new HashMap<String, String>();
		
		AppInfo appInfo = appInfoService.getAppInfoById(appId);
		if (appInfo.getStatus()==2||appInfo.getStatus()==5) {
			map.put("errorCode", "0");
			if (appInfoService.modifyStatus(4,appId)) {
				map.put("resultMsg", "success");
			}else {
				map.put("resultMsg", "failed");
			}
		}else if (appInfo.getStatus()==4) {
			map.put("errorCode", "0");
			if (appInfoService.modifyStatus(5,appId)) {
				map.put("resultMsg", "success");
			}else {
				map.put("resultMsg", "failed");
			}
		}
		
		return map;
	 }
	
	@RequestMapping(value="delapp.json")
	@ResponseBody
	public String delApp(String id) {
		Integer appid = Integer.parseInt(id);
		Map<String, String> data = new HashMap<String, String>();
		
		appVersionService.delAppVersion(appid);
		if (appInfoService.delAppInfo(appid)) {
			data.put("delResult", "true");
		}else {
			data.put("delResult", "false");
		}
		return JSONArray.toJSONString(data);
	}
	
	
	@RequestMapping(value="/appversionmodifysave")
	public String appversionmodifysave(AppVersion appVersion,HttpSession session,HttpServletRequest request,@RequestParam(value="attach",required=false)MultipartFile attach) {
		String appId = session.getAttribute("appId").toString();
		if(!attach.isEmpty()){
			String APKName = "";
			
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//ԭ�ļ���
			String prefix = FilenameUtils.getExtension(oldFileName);//ԭ�ļ���׺
			if(prefix.equalsIgnoreCase("apk")){//�ж��ļ���׺�Ƿ�Ϊapk
				AppInfo appInfo = appInfoService.getAppInfoById(Integer.parseInt(appId));
				APKName= appInfo.getAPKName();
				
				//ƴ����ApkName����ʽΪApkName-appVersion.apk
				String fileName = APKName +"-"+ appVersion.getVersionNo() + ".apk";
				
				//���������ļ���apk�ļ�
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
				}
				appVersion.setDownloadLink(request.getContextPath()+"/statics/uploadfiles/"+fileName);
				appVersion.setApkLocPath(path+File.separator+fileName);
				appVersion.setApkFileName(fileName);
			}
		}
		appVersion.setModifyBy(((DevUser)request.getSession().getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setModifyDate(new Date());
		if (appVersionService.modifyAppVersion(appVersion)) {
			return "redirect:/dev/flatform/app/list";
		}
		return "developer/appversionmodify";
	}
	
	
	/**
	 * ����aid��ȡapp�汾�б�����vid��ȡapp���°汾
	 * @param vid
	 * @param aid
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appversionmodify")
	public String appversionmodify(String vid,String aid,Model model,HttpSession session) {
		
		List<AppVersion> appVersionList = appVersionService.getAppVersionByAppId(Integer.parseInt(aid));
		AppVersion appVersion = appVersionService.getAppVersionById(Integer.parseInt(vid));
		session.setAttribute("appId", aid);
		model.addAttribute("appVersion", appVersion);
		model.addAttribute("appVersionList", appVersionList);
		
		return "developer/appversionmodify";
	}
	
	
	/**
	 * �鿴app��Ϣ
	 * @param appInfo
	 * @return
	 */
	@RequestMapping(value="/appview/{id}",method=RequestMethod.GET)
	public String view(@PathVariable String id,Model model){
		//AppVersionService appVersionService= new  AppVersionMapper;
		AppInfo appInfo = null;
		List<AppVersion> appVersionList = null;
		try {
			appInfo = appInfoService.getAppInfoById(Integer.parseInt(id));
			appVersionList = appVersionService.getAppVersionByAppId(Integer.parseInt(id));
		}catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("appVersionList", appVersionList);
		model.addAttribute(appInfo);
		return "developer/appinfoview";
	}

	@RequestMapping(value="/addversionsave")
	public String addVersionSave(AppVersion appVersion,HttpSession session,HttpServletRequest request,@RequestParam(value="a_downloadLink",required=true)MultipartFile attach) {
		String appId = session.getAttribute("appId").toString();
		if(!attach.isEmpty()){
			String APKName = "";
			
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//ԭ�ļ���
			String prefix = FilenameUtils.getExtension(oldFileName);//ԭ�ļ���׺
			if(prefix.equalsIgnoreCase("apk")){//�ж��ļ���׺�Ƿ�Ϊapk
				
				appVersion.setAppId(Integer.parseInt(appId));
				AppInfo appInfo = appInfoService.getAppInfoById(Integer.parseInt(appId));
				APKName= appInfo.getAPKName();
				
				//ƴ����ApkName����ʽΪApkName-appVersion.apk
				String fileName = APKName +"-"+ appVersion.getVersionNo() + ".apk";
				
				//���������ļ���apk�ļ�
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
				}
				appVersion.setDownloadLink(request.getContextPath()+"/statics/uploadfiles/"+fileName);
				appVersion.setApkLocPath(path+File.separator+fileName);
				appVersion.setApkFileName(fileName);
			}else{
				
			}
			appVersion.setCreatedBy(((DevUser)request.getSession().getAttribute(Constants.DEV_USER_SESSION)).getId());
			appVersion.setCreationDate(new Date());

		}
		//�����汾��Ϣ����������������appVersionId
		Integer appVersionId=appVersionService.addAppVersion(appVersion);
		if (null!=appVersionId) {
			AppInfo appInfo = new AppInfo();
			appInfo.setId(Integer.parseInt(appId));
			appInfo.setVersionId(appVersionId);
			appInfoService.modifyAppInfoById(appInfo);
			return "redirect:/dev/flatform/app/list";
		}
		return "developer/appversionadd";
	}

	/**
	 * ����appId��ѯapp�汾��Ϣ
	 * @param appId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appversionadd")
	public String appVersionAdd(@RequestParam(value="id")String appId,Model model,HttpServletRequest request) {
		List<AppVersion> appInfoList = appVersionService.getAppVersionByAppId(Integer.parseInt(appId));
		model.addAttribute("appVersionList", appInfoList);
		request.getSession().setAttribute("appId", appId);
		return "developer/appversionadd";
	}


	/**
	 * ����id��ѯapp��Ϣ
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appinfomodify")
	public String appinfomodify(String id,Model model) {
		Integer queryId = Integer.parseInt(id);
		AppInfo appInfo = appInfoService.getAppInfoById(queryId);
		model.addAttribute("appInfo", appInfo);
		return "developer/appinfomodify";
	}

	/**
	 * ɾ�������ļ�
	 * @param flag
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delfile.json",method=RequestMethod.GET)
	@ResponseBody
	public Object delFile(@RequestParam(value="flag",required=false) String flag,
			@RequestParam(value="id",required=false) String id){
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String fileLocPath = null;
		if(flag == null || flag.equals("") ||
			id == null || id.equals("")){
			resultMap.put("result", "failed");
		}else if(flag.equals("logo")){//ɾ��logoͼƬ������app_info��
			try {
				resultMap.put("result", "success");
				fileLocPath = (appInfoService.getAppInfoById(Integer.parseInt(id))).getLogoLocPath();
				File file = new File(fileLocPath);
				if(file.exists())
					if(file.delete()){//ɾ���������洢�������ļ�
						resultMap.put("result", "success");
					}else {
						resultMap.put("result", "failed");
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(flag.equals("apk")){//ɾ��apk�ļ�������app_version��
			try {
				fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
				File file = new File(fileLocPath);
				resultMap.put("result", "success");
			    if(file.exists()){
			    	if(file.delete()){//ɾ���������洢�������ļ�
				    	resultMap.put("result", "success");
				    }else {
				    	resultMap.put("result", "failed");
					}
			    }
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JSONArray.toJSONString(resultMap);
	}



	/**
	 * �޸�appInfo
	 * @param appInfo
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/appinfomodifysave",method=RequestMethod.POST)
	public String modifySave(AppInfo appInfo,HttpSession session,HttpServletRequest request,
			@RequestParam(value="attach",required= false) MultipartFile attach){		
		String APKName = appInfo.getAPKName();
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();//ԭ�ļ���
			String prefix = FilenameUtils.getExtension(oldFileName);//ԭ�ļ���׺
			int filesize = 500000;
			if(attach.getSize() > filesize){//�ϴ���С���ó��� 50k
				return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()
				+"&error=error4";
			}else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
					||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//�ϴ�ͼƬ��ʽ
				String fileName = APKName + ".jpg";//�ϴ�LOGOͼƬ����:apk����.apk
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()
					+"&error=error2";
				}
				appInfo.setLogoLocPath(path+File.separator+fileName);
				appInfo.setLogoPicPath(request.getContextPath()+"/statics/uploadfiles/"+fileName);
			}else{
				return "redirect:/dev/flatform/app/appinfomodify?id="+appInfo.getId()
				+"&error=error3";
			}
		}
		appInfo.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setModifyDate(new Date());
		try {
			if(appInfoService.modifyAppInfoById(appInfo)){
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appinfomodify";
	}

	/**
	 * ҳ����ת--->����APP��Ϣ
	 * @return
	 */
	@RequestMapping(value="/appinfoadd")
	public String toAppInfoAdd() {
		return "developer/appinfoadd";
	}

	/**
	 * ajx��֤apkName�Ƿ����
	 * @param apkName
	 * @return
	 */
	@RequestMapping(value="/apkexist.json",method=RequestMethod.GET)
	@ResponseBody
	public Object apkExist(@RequestParam(value="APKName")String apkName) {

		String data = "";
		if (StringUtils.isNullOrEmpty(apkName)) {
			data = "{\"APKName\":\"empty\"}";
		}else if (appInfoService.getAppInfoByApkName(apkName)) {
			data = "{\"APKName\":\"noexist\"}";
		}else {
			data = "{\"APKName\":\"exist\"}";
		}

		return JSON.toJSON(data);
	}


	/**
	 * ��ѯ����ƽ̨�б�����JSON����
	 * @return
	 */
	@RequestMapping(value="/datadictionarylist.json",method=RequestMethod.GET)
	@ResponseBody
	public Object datadictionarylist() {
		return dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
	}

	@RequestMapping(value="/categorylevellist.json",method=RequestMethod.GET)
	@ResponseBody
	public Object getCategoryLevel(@RequestParam(value="pid",required=false)String parentId) {

		List<Integer> parentIds = new ArrayList<Integer>();
		if (!StringUtils.isNullOrEmpty(parentId)) {
			parentIds.add(Integer.parseInt(parentId));
		}else {
			parentIds = null;
		}
		return appCategoryService.getCateGoryByParentId(parentIds);
	}

	@RequestMapping(value="/appinfoaddsave",method=RequestMethod.POST)
	public String appInfoInsert(HttpServletRequest request,AppInfo appInfo,@RequestParam(value="a_logoPicPath")MultipartFile attach) {

		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();
			String prefix = FilenameUtils.getExtension(oldFileName);
			int filesize = 500000;
			if (attach.getSize()>filesize) {

			}else if (prefix.equalsIgnoreCase("jpg")||prefix.equalsIgnoreCase("jpeg")||prefix.equalsIgnoreCase("png")||prefix.equalsIgnoreCase("pneg")) {
				String fileName = appInfo.getAPKName()+".jpg";
				File targtFile = new File(path,fileName); 
				if (!targtFile.exists()) {
					targtFile.mkdirs();
				}

				try {
					attach.transferTo(targtFile);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				appInfo.setLogoLocPath(path+File.separator+fileName);
				appInfo.setLogoPicPath(request.getContextPath()+"/statics/uploadfiles/"+fileName);

			}else {

			}
		}
		appInfo.setCreationDate(new Date());//����ʱ��
		//������id
		appInfo.setDevId(((DevUser)request.getSession().getAttribute(Constants.DEV_USER_SESSION)).getId());
		//������id
		appInfo.setCreatedBy(((DevUser)request.getSession().getAttribute(Constants.DEV_USER_SESSION)).getId());

		//����service������������trueΪ�����ɹ���falseΪʧ��
		if (appInfoService.addAppInfo(appInfo)) {
			return "redirect:/dev/flatform/app/list";//�ض��򵽲�ѯҳ��
		}

		return "developer/appinfoadd";//��������ҳ��
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
			@RequestParam(required=false,value="queryStatus")String queryStatus,
			@RequestParam(required=false,value="queryFlatformId")String queryFlatformId,
			@RequestParam(required=false,value="queryCategoryLevel1")String queryCategoryLevel1,
			@RequestParam(required=false,value="queryCategoryLevel2")String queryCategoryLevel2,
			@RequestParam(required=false,value="queryCategoryLevel3")String queryCategoryLevel3,
			@RequestParam(required=false,value="pageIndex")String pageIndex) {


		//app״̬
		Integer status=null;

		//����ƽ̨
		Integer flatformId=null;

		//һ������
		Integer categoryLevel1=null;

		//��������
		Integer categoryLevel2=null;

		//��������
		Integer categoryLevel3=null;

		model.addAttribute("querySoftwareName", softwareName);
		if (!StringUtils.isNullOrEmpty(queryStatus)) {
			try {
				status=Integer.parseInt(queryStatus);
				model.addAttribute("queryStatus", status);
			} catch (NumberFormatException e) {
			}  
		}
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
		int totalCount = appInfoService.getAppInfoCount(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3);
		PageSupport pages = new PageSupport();
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		
		//���Ƶ�ǰҳ��
		if (currentPageNo>pages.getTotalPageCount()&&pages.getTotalPageCount()!=0) {
			currentPageNo = totalCount;
		}else if (currentPageNo<1) {
			currentPageNo = 1;
		}

		appinfoList = appInfoService.getAppInfoList(softwareName, status, flatformId, categoryLevel1, categoryLevel2, categoryLevel3,(currentPageNo-1)*pageSize,pageSize);


		
		pages.setCurrentPageNo(currentPageNo);
		
		model.addAttribute("appInfoList", appinfoList);
		model.addAttribute("pages", pages);
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("categoryLevel2List", categoryLevel2List);
		model.addAttribute("categoryLevel3List", categoryLevel3List);
		model.addAttribute("flatFormList", flatFormList);
		model.addAttribute("statusList", statusList);

		return "developer/appinfolist";
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

}
