package com.dreamsecurity.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dreamsecurity.auth.common.vo.PagingVo;
import com.dreamsecurity.auth.dao.AuthDAO;
import com.dreamsecurity.auth.exception.AuthorizationException;
import com.dreamsecurity.util.CheckUtil;
import com.dreamsecurity.util.Crypto;

@Service(value = "UserService")
public class UserServiceImpl implements UserService {
	
	@Inject
	private AuthDAO authDAO;
	
	@Inject
	private Crypto crypto;

	@Autowired
	private ReloadableResourceBundleMessageSource messageSource;
	
	@Resource(name = "errorMsg")
	private Map<String, Map<String, Object>> errorMsg;
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private final int SHA256 = 1;
	//private final int PLAIN_TEXT = 0;
	
	@Transactional
	public Map<String, Object> login(Map<String, Object> params) throws AuthorizationException, Exception {
	
		logger.info("#### login Start ####");
		
		String userId = (String) params.get("userId");
		String userPwd = (String) params.get("userPwd");
		
		int userPwdType = Integer.parseInt(messageSource.getMessage("oauth.user.password.algo", null, Locale.getDefault()));
		
		if(userPwdType == SHA256) {
			userPwd = crypto.bytesToHex((crypto.digest(userPwd, "SHA-256")));
		}
		
		logger.debug(userPwd);
		
		Map<String, Object> userInfo = new HashMap<>();
		Map<String, Object> returnMap = new HashMap<>();
		
		userInfo = authDAO.getUserInfo(userId);
		
		if(CheckUtil.isEmpty(userInfo)) {
			throw new AuthorizationException(4300, (String)errorMsg.get("4300").get("error_description"));
		}
		else if(!userPwd.equals(userInfo.get("USER_PASSWORD"))) {
			throw new AuthorizationException(4301, (String)errorMsg.get("4301").get("error_description"));
		}
		
		returnMap.put("userId", userInfo.get("USER_ID"));
		returnMap.put("userRole", userInfo.get("USER_ROLE"));
		
		return returnMap;
	}
	
	@Transactional
	public List<HashMap<String, Object>> clientList(PagingVo paging, String clientOwner) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		
		int pStart = paging.getStart();
		int pLast = paging.getLast();
		
		keys.put("pStart", pStart);
		keys.put("pEnd", pLast);
		keys.put("clientOwner", clientOwner);
		
		List<HashMap<String, Object>> clientList = authDAO.getClientList(keys);
		
		return clientList;
	}
	
	@Transactional
	public List<HashMap<String, Object>> clientList(PagingVo paging) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		
		int pStart = paging.getStart();
		int pLast = paging.getLast();
		
		keys.put("pStart", pStart);
		keys.put("pEnd", pLast);
		
		List<HashMap<String, Object>> clientList = authDAO.getClientList(keys);
		
		return clientList;
	}
	
	@Transactional
	public List<HashMap<String, Object>> scopesList() throws Exception {
		
		List<HashMap<String, Object>> scopesList = authDAO.getScopesList();
		
		return scopesList;
	}
	
	@Transactional
	public List<HashMap<String, Object>> grantTypesList() throws Exception {
		
		List<HashMap<String, Object>> grantTypesList = authDAO.getGrantTypesList();
		
		return grantTypesList;
	}
	
	@Transactional
	public Map<String, Object> clientDetail(String param) throws Exception {
		
		Map<String, Object> returnMap = new HashMap<>();
		
		returnMap = authDAO.getClientInfoByClientId(param);
		
		return returnMap;
	}
	
	@Transactional
	public List<HashMap<String, Object>> getClientUriList(String clientId) throws Exception {
		
		List<HashMap<String, Object>> clientUriList = authDAO.getClientRedirectUriByClientId(clientId);
		
		return clientUriList;
	}
	
	@Transactional
	public int getClientTotalList(String clientOwner) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("clientOwner", clientOwner);
		
		int total = authDAO.getClientTotalList(keys);
		
		return total;
	}
	
	@Transactional
	public int getClientTotalList() throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		
		int total = authDAO.getClientTotalList(keys);
		
		return total;
	}
	
	@Transactional
	public void modifyClient(HashMap<String, Object> params, List<String> uriList) throws Exception {
	
		logger.info("#### modifyClient Start ####");
		logger.info(">>>>>> uriList :" + uriList);
		
		String clientId = (String)params.get("clientId");
		String clientSecret = (String)params.get("clientSecret");
		
		if(CheckUtil.isNotEmpty(clientSecret)){
			// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
			int clientPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
			
			if(clientPwdType == SHA256) {
				params.replace("clientSecret", crypto.bytesToHex((crypto.digest(clientSecret, "SHA-256"))));
			}
		}
		
		/*
		 * 1. 해당 Client 의 Redirect Uri 초기화후 저장
		 */
	    int rs1 = authDAO.updateClientInfo(params);
	    
		if(rs1 > 0 ) { // 클라이언트 정보 업데이트 성공시
			int rs2 = authDAO.deleteClientUriList(clientId);
			
			if(rs2 >= 0 ) { // 클라이언트에 해당되는 Uri 삭제 성공시
				
				if(!CheckUtil.isEmpty(uriList)){
					HashMap<String, Object> uriParams = new HashMap<>();
				    
				    uriParams.put("clientId", clientId);
				    uriParams.put("uriList", uriList);
					
					authDAO.setClientUri(uriParams);	
				}
				
			}
		}
	}
	
	@Transactional
	public int deleteClient(String clientId) throws Exception {
				
	    int rs = authDAO.deleteClient(clientId);
	    logger.debug("클라이언트 삭제 ( " + clientId +" ) : " + rs + "건");
	    
	    return rs;
	}
	
	@Transactional
	public void addClient(HashMap<String, Object> params, List<String> uriList) throws Exception {
	
		logger.info("#### addClient Start ####");
		logger.info(">>>>>> uriList :" + uriList);
		
		String clientId = (String)params.get("clientId");
		String clientSecret = (String)params.get("clientSecret");
		
		// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
		int clientPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
		
		if(clientPwdType == SHA256) {
			params.replace("clientSecret", crypto.bytesToHex((crypto.digest(clientSecret, "SHA-256"))));
		}
		
	    int rs = authDAO.setClient(params);
	    
		if(rs > 0 ) { // 클라이언트 정보 업데이트 성공시
			if(!CheckUtil.isEmpty(uriList)){
				HashMap<String, Object> uriParams = new HashMap<>();
			    
			    uriParams.put("clientId", clientId);
			    uriParams.put("uriList", uriList);
				
				authDAO.setClientUri(uriParams);	
			}
		}
	}
	
	@Transactional
	public int getClientCheck(String clientId) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("clientId", clientId);
		
		int total = authDAO.getClientCheck(keys);
		
		return total;
	}
	
	@Transactional
	public List<HashMap<String, Object>> getApiList(PagingVo paging, String apiId) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		
		int pStart = paging.getStart();
		int pLast = paging.getLast();
		
		keys.put("pStart", pStart);
		keys.put("pEnd", pLast);
		
		if(CheckUtil.isNotEmpty(apiId)) {
			keys.put("apiId", apiId);
		}
		
		List<HashMap<String, Object>> apiList = authDAO.getApiList(keys);
		
		return apiList;
	}
	
	@Transactional
	public int getApiTotalList(String apiId) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
				
		if(CheckUtil.isNotEmpty(apiId)) {
			keys.put("apiId", apiId);
		}
		
		int total = authDAO.getApiTotalList(keys);
		
		return total;
	}
	
	@Transactional
	public Map<String, Object> apiDetail(String param) throws Exception {
		
		Map<String, Object> returnMap = new HashMap<>();
		
		returnMap = authDAO.getApiInfoByApiId(param);
		
		return returnMap;
	}
	
	@Transactional
	public void modifyApi(HashMap<String, Object> params) throws Exception {
		
		String apiSecret = (String)params.get("apiSecret");
		
		if(CheckUtil.isNotEmpty(apiSecret)){
			// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
			int apiPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
			
			if(apiPwdType == SHA256) {
				params.replace("apiSecret", crypto.bytesToHex((crypto.digest(apiSecret, "SHA-256"))));
			}
		}
		
	    authDAO.updateApiInfo(params);
	    		
	}
	
	@Transactional
	public int deleteApi(String apiId) throws Exception {
				
	    int rs = authDAO.deleteApi(apiId);
	    logger.debug("API 삭제 ( " + apiId +" ) : " + rs + "건");
	    
	    return rs;
	}
	
	@Transactional
	public void addApi(HashMap<String, Object> params) throws Exception {
	
		String apiSecret = (String)params.get("apiSecret");
		
		// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
		int apiPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
		
		if(apiPwdType == SHA256) {
			params.replace("apiSecret", crypto.bytesToHex((crypto.digest(apiSecret, "SHA-256"))));
		}
		
	    authDAO.setApi(params);

	}
	
	@Transactional
	public int getApiCheck(String apiId) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("apiId", apiId);
		
		int total = authDAO.getApiCheck(keys);
		
		return total;
	}
	
	@Transactional
	public List<HashMap<String, Object>> getApiLog(String clientId, String startDate, String endDate) throws Exception {
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("clientId", clientId);
		keys.put("startDate", startDate);
		keys.put("endDate", endDate);
		
		List<HashMap<String, Object>> clientLogList = authDAO.getApiLog(keys);
		
		return clientLogList;
	}
}
