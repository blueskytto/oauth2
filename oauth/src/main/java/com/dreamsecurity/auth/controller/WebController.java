package com.dreamsecurity.auth.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dreamsecurity.auth.common.vo.PagingVo;
import com.dreamsecurity.auth.exception.AuthorizationException;
import com.dreamsecurity.auth.service.AuthorizationService;
import com.dreamsecurity.auth.service.UserService;
import com.dreamsecurity.util.CheckUtil;

@Controller
@SessionAttributes({"USER_ID", "USER_NAME", "USER_ROLE"})
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	UserService userService;
	
	@Resource(name = "errorMsg")
	private Map<String, Map<String, Object>> errorMsg;
	
	
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String main(Model model) {
		
		return "main/main";
	}
	
	@RequestMapping(value = {"/guide"}, method = RequestMethod.GET)
	public String guide(Model model) {
		
		return "main/guide";
	}
	
	@RequestMapping(value = {"/guide/app"}, method = RequestMethod.GET)
	public String guideApp(Model model) {
		
		return "main/guide_app_add";
	}
	
	@RequestMapping(value = {"/guide/dev"}, method = RequestMethod.GET)
	public String guideDev(Model model) {
		
		return "main/guide_app_dev";
	}
	
	@RequestMapping(value = {"/test"}, method = RequestMethod.GET)
	public String test(Model model) {
		
		return "test";
	}
	
	/*
	 * 로그인 화면 View
	 */
	@RequestMapping(value = {"/login"}, method = RequestMethod.GET)
	public String login(Model model) {
		
		return "login/login";
	}
	
	/*
	 * 로그인 처리
	 */
	@RequestMapping(value = {"/login"}, method = RequestMethod.POST)
	public ModelAndView login(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "userPwd") String userPwd,
			HttpSession session) {
		
		logger.debug("### login.do 시작 ###");
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();

		/*
		 * 토큰 발급용 로그인일 경우 returnUrl 이 담겨서 옴
		 * 그 경우에는 Url에 파라미터를 담겨서 해당 페이지로 이동 시키고,
		 * 그 외에 바로 로그인 하는 경우에는 Url에 정보가 담지 않도록 메인페이지를 띄워준다.
		 * redirectType : TRUE 일땐 Oauth에서 요청한 경우
		 */
		boolean redirectType = false;
		String returnUrl = (String) session.getAttribute("SAVED_REQUEST");
		
		if (CheckUtil.isNotEmpty(returnUrl)) {
			redirectType = true;
			returnUrl = "redirect:" + returnUrl;
		} else {
			returnUrl = "/";
		}
		
		logger.debug("### returnUrl ### : " + returnUrl);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		
		params.put("userId", userId);
		params.put("userPwd", userPwd);
		
		try {
			
			returnMap = userService.login(params);
			
			logger.debug("### returnMap ### : " + returnMap);
			
			userId = (String)returnMap.get("userId");
			String userRole = (String)returnMap.get("userRole");
			
			mv.addObject("USER_ID", userId);
			mv.addObject("USER_ROLE", userRole);
			
		} catch (AuthorizationException e) {
			redirectType = true;
			logger.error(e.toString());
			mv.addObject("error", errorMsg.get(Integer.toString(e.getErrorCode())).get("error_description"));
			returnUrl = "login/login";
		} catch (Exception e) {
			redirectType = true;
			logger.error(e.toString());
			mv.addObject("error", errorMsg.get("4104").get("error"));
			returnUrl = "login/login";
		}
		
		if(redirectType) {
			mv.setViewName(returnUrl);
		} else {
			redirectView.setUrl(returnUrl);
			redirectView.setExposeModelAttributes(false);
			
			mv.setView(redirectView);
		}
		
		return mv;
	}
	
	/*
	 * 로그아웃 처리
	 */
	@RequestMapping(value = {"/logout"}, method = RequestMethod.POST)
	public ModelAndView logout(SessionStatus status) {
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/";
		
		status.setComplete();
		
		redirectView.setUrl(returnUrl);
		redirectView.setExposeModelAttributes(false);		
		mv.setView(redirectView);
		
		return mv;
		
	}
	
	/*
	 * 클라이언트 목록 View
	 */
	@RequestMapping(value = {"/main/clients"}, method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView clientList(
			@ModelAttribute("USER_ROLE") String userRole,
			@ModelAttribute("USER_ID") String userId,
			HttpServletRequest request,
			PagingVo paging
			) {
		
		ModelAndView mv = new ModelAndView();
		List<HashMap<String, Object>> clientList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			int total = 0;
			
			if(userRole.equals("ROLE_ADMIN")) {
				total = userService.getClientTotalList();
				paging.setTotal(total);
				clientList = userService.clientList(paging);
			}
			else {
				total = userService.getClientTotalList(userId);
				paging.setTotal(total);
				logger.debug(" >>> total :" + total);
				clientList = userService.clientList(paging, userId); 
			}
						
			mv.addObject("clientList", clientList);
			mv.addObject("p", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		logger.debug(" >>> list :" + clientList);
		
		mv.setViewName("main/client_list");
		
		return mv;
	}
	
	/*
	 * 클라이언트 정보 View
	 */
	@RequestMapping(value = {"/main/clients/{clientId}"}, method = RequestMethod.GET)
	public ModelAndView clientDetail(
			@PathVariable String clientId,
			@ModelAttribute("USER_ROLE") String userRole,
			@ModelAttribute("USER_ID") String userId
			) {
		
		ModelAndView mv = new ModelAndView();
		Map<String, Object> clientInfo = new HashMap<>();
		List<HashMap<String, Object>> scopesList = new ArrayList<HashMap<String,Object>>();
		List<HashMap<String, Object>> grantTypesList = new ArrayList<HashMap<String,Object>>();
		List<HashMap<String, Object>> clientUriList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			clientInfo = userService.clientDetail(clientId);
			
			// 관리자가 아니면서 소유자가 아닌 사용자가 접근할 경우 에러 처리
			if(!userRole.matches("ROLE_ADMIN") && !userId.matches((String)clientInfo.get("CLIENT_OWNER"))) {
				throw new AuthorizationException(8001, (String)errorMsg.get("8001").get("error_description"));
			}
			
			scopesList = userService.scopesList();
			grantTypesList = userService.grantTypesList();
			clientUriList = userService.getClientUriList(clientId);
			
			// View단으로 보내는 클라이언트 비밀번호는 삭제
			clientInfo.remove("CLIENT_SECRET");
			
			mv.addObject("clientInfo", clientInfo);
			mv.addObject("scopesList", scopesList);
			mv.addObject("grantTypesList", grantTypesList);
			mv.addObject("clientUriList", clientUriList);
			
			mv.setViewName("main/client_detail");
			
			logger.debug(" >>> clientInfo :" + clientInfo);
			logger.debug(" >>> clientUriList :" + clientUriList);
			logger.debug(" >>> Default scopesList :" + scopesList);
			logger.debug(" >>> Default grantTypesList :" + grantTypesList);
			
		} catch (Exception e) {
			logger.error(e.toString());
			
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl("/");
			redirectView.setExposeModelAttributes(false);		
			mv.setView(redirectView);
			
		}
		
		return mv;
	}
	
	/*
	 * 클라이언트 수정 처리
	 */
	@RequestMapping(value = {"/main/clients/{clientId}"}, method = RequestMethod.PUT)
	public ModelAndView clientDetailModify(
			@RequestParam HashMap<String, Object> allRequestParams,
			@RequestParam(value = "uriList", required = false) List<String> uriList
			) {
		
		logger.debug(">>>>>>>> allRequestParams : " + allRequestParams);
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/main/clients";
		
		try {
			
			userService.modifyClient(allRequestParams, uriList);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		redirectView.setUrl(returnUrl);
		redirectView.setExposeModelAttributes(false);		
		mv.setView(redirectView);
		
		return mv;
	}
	
	/*
	 * 클라이언트 삭제 처리
	 */
	@RequestMapping(value = {"/main/clients/{clientId}"}, method = RequestMethod.DELETE)
	public ModelAndView clientDelete(
			@PathVariable String clientId,
			@ModelAttribute("USER_ROLE") String userRole,
			@ModelAttribute("USER_ID") String userId
			) {
		
		Map<String, Object> clientInfo = new HashMap<>();
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/main/clients";
		
		try {
			
			clientInfo = userService.clientDetail(clientId);
			
			// 관리자가 아니면서 소유자가 아닌 사용자가 접근할 경우 에러 처리
			if(!userRole.matches("ROLE_ADMIN") && !userId.matches((String)clientInfo.get("CLIENT_OWNER"))) {
					throw new AuthorizationException(8001, (String)errorMsg.get("8001").get("error_description"));
			}
			
			userService.deleteClient(clientId);
									
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		redirectView.setUrl(returnUrl);
		redirectView.setExposeModelAttributes(false);		
		mv.setView(redirectView);
		
		return mv;
	}
	
	/*
	 * 클라이언트 추가 등록 View
	 */
	@RequestMapping(value = {"/main/client/regist"}, method = RequestMethod.GET)
	public ModelAndView clientAdd() {
		
		ModelAndView mv = new ModelAndView();
		List<HashMap<String, Object>> scopesList = new ArrayList<HashMap<String,Object>>();
		List<HashMap<String, Object>> grantTypesList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			scopesList = userService.scopesList();
			grantTypesList = userService.grantTypesList();
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		mv.addObject("scopesList", scopesList);
		mv.addObject("grantTypesList", grantTypesList);
		
		mv.setViewName("main/client_regist");
		
		return mv;
	}
	
	/*
	 * 클라이언트 추가 처리
	 */
	@RequestMapping(value = {"/main/client/regist"}, method = RequestMethod.POST)
	public ModelAndView clientAddSubmit(
			@RequestParam HashMap<String, Object> allRequestParams,
			@RequestParam(value = "uriList", required = false) List<String> uriList
			) {
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/main/clients";
		
		try {
			
			userService.addClient(allRequestParams, uriList);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		redirectView.setUrl(returnUrl);
		redirectView.setExposeModelAttributes(false);		
		mv.setView(redirectView);
		
		return mv;
	}
	
	/*
	 * 클라이언트 아이디 중복 체크 처리
	 */
	@RequestMapping(value = {"/main/client/check"}, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> clientIdCheck(
			@RequestBody String clientId
			) {
		
		Map<String, Object> returnMap = new HashMap<>();
		
		try {
			
			int rs = userService.getClientCheck(clientId);
			returnMap.put("result", rs);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		return returnMap;
	}
	
	/*
	 * 클라이언트별 로그 확인 페이지 호출
	 */
	@RequestMapping(value = {"/main/logs"}, method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView logs(
			@ModelAttribute("USER_ROLE") String userRole,
			@ModelAttribute("USER_ID") String userId,
			HttpServletRequest request,
			PagingVo paging
			) {
		
		ModelAndView mv = new ModelAndView();
		List<HashMap<String, Object>> clientList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			int total = 0;
			
			if(userRole.equals("ROLE_ADMIN")) {
				total = userService.getClientTotalList();
				paging.setTotal(total);
				clientList = userService.clientList(paging);
			}
			else {
				total = userService.getClientTotalList(userId);
				paging.setTotal(total);
				logger.debug(" >>> total :" + total);
				clientList = userService.clientList(paging, userId); 
			}
						
			mv.addObject("clientList", clientList);
			mv.addObject("p", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		logger.debug(" >>> list :" + clientList);
		
		mv.setViewName("main/log_list");
		
		return mv;
	}
	
	/*
	 * 클라이언트에 해당되는 로그 수집
	 */
	@RequestMapping(value = {"/main/clients/{clientId}/logs"}, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> clientlogs(
			@PathVariable String clientId,
			@RequestParam(value = "startDate") String startDate,
			@RequestParam(value = "endDate") String endDate,
			@ModelAttribute("USER_ROLE") String userRole,
			@ModelAttribute("USER_ID") String userId
			) {
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> clientInfo = new HashMap<>();
		List<HashMap<String, Object>> clientLogList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			clientInfo = userService.clientDetail(clientId);
			
			// 관리자가 아니면서 소유자가 아닌 사용자가 접근할 경우 에러 처리
			if(!userRole.matches("ROLE_ADMIN") && !userId.matches((String)clientInfo.get("CLIENT_OWNER"))) {
				throw new AuthorizationException(8001, (String)errorMsg.get("8001").get("error_description"));
			}
			
			clientLogList = userService.getApiLog(clientId, startDate, endDate);
			
			returnMap.put("clientInfo", clientInfo);
			returnMap.put("clientLogList", clientLogList);
			
			logger.debug(" >>> clientInfo :" + clientInfo);
			logger.debug(" >>> clientLogList :" + clientLogList);
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		return returnMap;
	}
	
}
