package cn.appsys.controller.backend;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.appsys.dao.backenduser.BackendUserMapper;
import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DevUser;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping("manage")
public class BackendLoginController {
	
	@Resource
	BackendUserMapper backendUserMapper;
	
	/**
	 * 页面跳转--后台管理系统入口
	 * @return
	 */
	@RequestMapping("loginaaaaa")
	public String toLogin() {
		return "backendlogin";
	}
	
	/**
	 * 登出方法
	 * @param session
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login";
	}
	
	/**
	 * 登录方法
	 * @param request
	 * @param userCode
	 * @param userPassword
	 * @return
	 */
	@RequestMapping("/dologin")
	public String devLoginSave(HttpServletRequest request,@RequestParam(required=true)String userCode,@RequestParam(required=true)String userPassword) {
		BackendUser backendUser = new BackendUser();
		backendUser = backendUserMapper.getBanckendUserByCode(userCode);
		if (null == backendUser) {
			request.setAttribute("error", "账号不存在！");
			return "backendlogin";
		}else {
			if (backendUser.getUserPassword().equals(userPassword)) {
				request.getSession().setAttribute(Constants.USER_SESSION, backendUser);
				return "backend/main";
			}
			request.setAttribute("error", "密码错误！");
			return "backendlogin";
		}
	}
}
