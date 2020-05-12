package com.dreamsecurity.auth.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dreamsecurity.auth.exception.AuthorizationException;
import com.dreamsecurity.auth.service.AuthorizationService;
import com.dreamsecurity.util.CheckUtil;

@Controller
public class AccessTokenController {

	private static final Logger logger = LoggerFactory.getLogger(AccessTokenController.class);
	
	@Autowired
	AuthorizationService authorizationService;

	@Resource(name = "errorMsg")
	private Map<String, Map<String, Object>> errorMsg;
	
	@RequestMapping(value = {"/authorize"}, method = RequestMethod.GET, produces = "application/x-www-form-urlencoded; charset=utf8")
	public String authorize (
			@RequestParam(value = "response_type", required = false) String responseType,
			@RequestParam(value = "client_id", required = false) String clientId,
			@RequestParam(value = "redirect_uri", required = false) String redirectUri,
			@RequestParam(value = "scope", required = false, defaultValue = "") String scope,
			@RequestParam(value = "state", required = false) String state,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request
			) throws UnsupportedEncodingException {

		logger.info("### Authorization Start ###");
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		
		try {
			
			String userId = (String) request.getSession().getAttribute("USER_ID");

			// 공통. 필수 인자값 체크
			if(CheckUtil.isEmpty(responseType) || CheckUtil.isEmpty(clientId) || CheckUtil.isEmpty(redirectUri) || CheckUtil.isEmpty(state) || CheckUtil.isEmpty(state)) {
				throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
			}
			
			params.put("clientId", clientId);
			params.put("redirectUri", redirectUri);
			params.put("scope", scope);
			params.put("userId", userId);
			
			if(responseType.equals("code")) {
				returnMap = authorizationService.getAuthorizationCode(params);
			}
			else if(responseType.equals("token")) {
				returnMap = authorizationService.getAccessTokenByImplicit(params);
			}
			
			else {
				// ERROR : 알수 없는 responseType이 올 경우
				logger.error("요청 온 response_type : " + responseType);
				throw new AuthorizationException(4103, (String)errorMsg.get("4103").get("error_description"));
			}

			returnMap.put("state", state);
			
		} catch (AuthorizationException e) {
			logger.error(e.toString());
			returnMap.clear();
			returnMap.put("error", errorMsg.get(Integer.toString(e.getErrorCode())).get("error"));
			returnMap.put("error_description",errorMsg.get(Integer.toString(e.getErrorCode())).get("error_description"));
		} catch (Exception e) {
			logger.error(e.toString());
			returnMap.clear();
			returnMap.put("error", errorMsg.get("4104").get("error"));
		}
				
		redirectAttributes.addAllAttributes(returnMap);
				
		return "redirect:" + redirectUri;
		
	}
	
	@RequestMapping(value = {"/token"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> token(
			@RequestParam(value = "grant_type", required = true) String grantType,
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "redirect_uri", required = false, defaultValue = "") String redirectUri,
			@RequestParam(value = "client_id", required = true) String clientId,
			@RequestParam(value = "client_secret", required = false) String clientSecret,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "refresh_token", required = false, defaultValue = "") String refreshToken,
			@RequestParam(value = "scope", required = false) String scope,
			HttpServletResponse response
			) {
				
		logger.info("### Access Token Request Start ###");
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		
		try {
			
			// 공통. 필수 인자값 체크
			if(CheckUtil.isEmpty(grantType) || CheckUtil.isEmpty(clientId)) {
				throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
			}
			
			params.put("clientId", clientId);
			params.put("redirectUri", redirectUri);
			params.put("scope", scope);
			
			if(grantType.equals("authorization_code")) {
				// Code 방식.  추가 필수 인자값 체크
				if(CheckUtil.isEmpty(code)) {
					throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
				}
				
				params.put("code", code);
				
				returnMap = authorizationService.getAccessTokenByCode(params);
			}
			else if(grantType.equals("password")) {
				// Resource Owner Password 방식.  추가 필수 인자값 체크
				if(CheckUtil.isEmpty(username) || CheckUtil.isEmpty(password)) {
					throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
				}
				
				params.put("username", username);
				params.put("password", password);
				
				returnMap = authorizationService.getAccessTokenByPassword(params);
			}
			else if(grantType.equals("client_credentials")) {
				// client Credentials 방식.  추가 필수 인자값 체크
				if(CheckUtil.isEmpty(clientSecret)) {
					throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
				}
				
				params.put("clientSecret", clientSecret);
				
				returnMap = authorizationService.getAccessTokenByclientCredentials(params);
			}
			else if(grantType.equals("refresh_token")) {
				// refresh_token 방식.  추가 필수 인자값 체크
				if(CheckUtil.isEmpty(refreshToken)) {
					throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
				}
				
				params.put("refreshToken", refreshToken);
				
				returnMap = authorizationService.getAccessTokenByRefreshToken(params);
				
			}
			else {
				// ERROR : 알수 없는 grantType이 올 경우
				logger.error("요청 온 response_type : " + grantType);
				throw new AuthorizationException(4103, (String)errorMsg.get("4103").get("error_description"));
			}
			
		} catch (AuthorizationException e) {
			logger.error(e.toString());
			returnMap.clear();
			returnMap.put("error", errorMsg.get(Integer.toString(e.getErrorCode())).get("error"));
			returnMap.put("error_description", errorMsg.get(Integer.toString(e.getErrorCode())).get("error_description"));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (Exception e) {
			logger.error(e.toString());
			returnMap.clear();
			returnMap.put("error", errorMsg.get("4104").get("error"));
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return returnMap;
	}
	
	@RequestMapping(value = {"/introspect"}, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> tokeninfo(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "token", required = false) String token,
			@RequestParam(value = "token_type_hint", required = false, defaultValue = "access_token") String tokenTypeHint,
			HttpServletResponse response
			) {
				
		logger.info("### tokeninfo Start ###");
		logger.info("headers : " + headers);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		
		String authorization = headers.get("authorization");
		
		try {
			
			// authorization 인자값 체크
			if(CheckUtil.isEmpty(authorization) && CheckUtil.isEmpty(token)) {
				throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
			}
			// authorization Value Check
			if (authorization.indexOf("Basic") < 0 && authorization.indexOf("Bearer") < 0) {
				throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
			}
			
			params.put("authorization", authorization);
			params.put("token", token);
			params.put("token_type_hint", tokenTypeHint);
			
			returnMap = authorizationService.tokenValidation(params);
			
		} catch (AuthorizationException e) {
			returnMap.clear();
			
			switch (e.getErrorCode()) {
			case 6001:
			case 6002:
				returnMap.put("active", "false");
				break;
			default:
				logger.error(e.toString());
				returnMap.put("error", errorMsg.get(Integer.toString(e.getErrorCode())).get("error"));
				returnMap.put("error_description", errorMsg.get(Integer.toString(e.getErrorCode())).get("error_description"));
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				break;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			returnMap.clear();
			returnMap.put("error", errorMsg.get("4104").get("error"));
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		return returnMap;
	}
	/*
	 * 
	 * // get request headers private Map<String, String> getHeadersInfo() {
	 * 
	 * Map<String, String> map = new HashMap<String, String>();
	 * 
	 * Enumeration<?> headerNames = request.getHeaderNames(); while
	 * (headerNames.hasMoreElements()) { String key = (String)
	 * headerNames.nextElement(); String value = request.getHeader(key);
	 * map.put(key, value); }
	 * 
	 * return map; }
	 */

}
