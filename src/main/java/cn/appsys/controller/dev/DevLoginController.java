package cn.appsys.controller.dev;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.appsys.pojo.DevUser;
import cn.appsys.service.dev.DevUserService;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping(value="/dev")
public class DevLoginController {
	
	@Resource
	DevUserService devUserService;
	
	@RequestMapping(value="/login")
	public String toLogin() {
		return "devlogin";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login";
	}
	
	@RequestMapping(value="/dologin")
	public String devLoginSave(HttpServletRequest request,@RequestParam(required=true)String devCode,@RequestParam(required=true)String devPassword) {
		DevUser devUser = new DevUser();
		devUser = devUserService.getDevUserByCode(devCode, devPassword);
		if (null == devUser) {
			request.setAttribute("error", "’À∫≈≤ª¥Ê‘⁄£°");
			return "devlogin";
		}else {
			if (devUser.getDevPassword().equals(devPassword)) {
				request.getSession().setAttribute(Constants.DEV_USER_SESSION, devUser);
				return "developer/main";
			}
			request.setAttribute("error", "√‹¬Î¥ÌŒÛ£°");
			return "devlogin";
		}
	}
}
