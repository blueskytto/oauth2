package com.dreamsecurity.auth.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dreamsecurity.auth.common.vo.PagingVo;
import com.dreamsecurity.auth.security.Auth;
import com.dreamsecurity.auth.security.Auth.Role;
import com.dreamsecurity.auth.service.AuthorizationService;
import com.dreamsecurity.auth.service.UserService;

@Controller
@SessionAttributes({"USER_ID", "USER_NAME", "USER_ROLE"})
public class WebAdminController {

	private static final Logger logger = LoggerFactory.getLogger(WebAdminController.class);
	
	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	UserService userService;
	
	@Resource(name = "errorMsg")
	private Map<String, Map<String, Object>> errorMsg;
	
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/guide/api"}, method = RequestMethod.GET)
	public String guideDev(Model model) {
		
		return "main/guide_app_api";
	}
	
	/*
	 * 관리자 환경설정 페이지 호출
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/oauth-config"}, method = RequestMethod.GET)
	public ModelAndView oauthConfig(Model model) {
		
		String returnUrl = "admin/oauth_config";
		
		ModelAndView mv = new ModelAndView();
		Map<String, Object> oauthConfig = new HashMap<>();
		
		try {
			
			oauthConfig = authorizationService.getOauthConfig();
			mv.addObject("oauthConfig", oauthConfig);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		mv.setViewName(returnUrl);
		
		return mv;
	}
	
	/*
	 * 관리자 환경설정 처리
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/oauth-config"}, method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> oauthConfigModify(
			@RequestBody HashMap<String, Object> allRequestParams
			) {
		
		logger.debug(">>>>>>>> allRequestParams : " + allRequestParams);
		
		boolean result = false;
		Map<String, Object> returnMap = new HashMap<>();
		
		try {
			
			result = authorizationService.setOauthConfig(allRequestParams);
			returnMap.put("result", result);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		return returnMap;
	}
	
	/*
	 * API 서버 목록 페이지 호출
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/api-servers"}, method = RequestMethod.GET)
	public ModelAndView apiList(
			@RequestParam(value = "apiId", required = false) String apiId,
			Model model,
			PagingVo paging
			) {
		
		ModelAndView mv = new ModelAndView();
		List<HashMap<String, Object>> apiList = new ArrayList<HashMap<String,Object>>();
		
		try {
			
			int total = 0;
			
			total = userService.getApiTotalList(apiId);
			paging.setTotal(total);
			apiList = userService.getApiList(paging, apiId);
			mv.addObject("apiList", apiList);
			mv.addObject("p", paging);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		mv.setViewName("admin/api_list");
		
		return mv;
	}
	
	/*
	 * API 서버 정보 상세 페이지
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/api-servers/{apiId}"}, method = RequestMethod.GET)
	public ModelAndView apiDetail(
			@PathVariable String apiId
			) {
		
		ModelAndView mv = new ModelAndView();
		Map<String, Object> apiInfo = new HashMap<>();
		
		try {
			
			apiInfo = userService.apiDetail(apiId);

			mv.addObject("apiInfo", apiInfo);
			mv.setViewName("admin/api_detail");
			
			logger.debug(" >>> apiInfo :" + apiInfo);
			
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
	 * API 서버 정보 수정 처리
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/api-servers/{apiId}"}, method = RequestMethod.PUT)
	public ModelAndView apiDetailModify(
			@RequestParam HashMap<String, Object> allRequestParams
			) {
		
		logger.debug(">>>>>>>> allRequestParams : " + allRequestParams);
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/admin/api-servers";
		
		try {
			
			userService.modifyApi(allRequestParams);
			
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
	 * API 서버 삭제 처리
	 */
	@Auth(role=Role.ADMIN)
	@RequestMapping(value = {"/admin/api-servers/{apiId}"}, method = RequestMethod.DELETE)
	public ModelAndView apiDelete(
			@PathVariable String apiId
			) {
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/admin/api-servers";
		
		try {
			
			userService.deleteApi(apiId);
									
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
	 * API 추가 등록 View
	 */
	@RequestMapping(value = {"/admin/api-server/regist"}, method = RequestMethod.GET)
	public ModelAndView apiAdd() {
		
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("admin/api_regist");
		
		return mv;
	}
	
	/*
	 * API 추가 처리
	 */
	@RequestMapping(value = {"/admin/api-server/regist"}, method = RequestMethod.POST)
	public ModelAndView apiAddSubmit(
			@RequestParam HashMap<String, Object> allRequestParams
			) {
		
		logger.debug(">>>>>>>> apiAddSubmit allRequestParams : " + allRequestParams);
		
		ModelAndView mv = new ModelAndView();
		RedirectView redirectView = new RedirectView();
		
		String returnUrl = "/admin/api-servers";
		
		try {
			
			userService.addApi(allRequestParams);
			
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
	 * API 아이디 중복 체크 처리
	 */
	@RequestMapping(value = {"/admin/api-server/check"}, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> apiIdCheck(
			@RequestBody String apiId
			) {
		
		Map<String, Object> returnMap = new HashMap<>();
		
		try {
			
			int rs = userService.getApiCheck(apiId);
			returnMap.put("result", rs);
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		
		return returnMap;
	}
	
}
