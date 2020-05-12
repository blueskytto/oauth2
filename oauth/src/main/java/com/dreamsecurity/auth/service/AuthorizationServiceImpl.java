package com.dreamsecurity.auth.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dreamsecurity.auth.common.config.ReplaceFileContents;
import com.dreamsecurity.auth.dao.AuthDAO;
import com.dreamsecurity.auth.exception.AuthorizationException;
import com.dreamsecurity.auth.token.TokenGenerator;
import com.dreamsecurity.util.CheckUtil;
import com.dreamsecurity.util.Crypto;

@Service(value = "AuthorizationService")
public class AuthorizationServiceImpl implements AuthorizationService {
	
	@Inject
	private TokenGenerator tokenGenerator;
	
	@Inject
	private AuthDAO authDAO;
	
	@Inject
	private Crypto crypto;

	@Autowired
	private ReplaceFileContents replaceFileContents;
	
	@Autowired
	private ReloadableResourceBundleMessageSource messageSource;
		
	@Resource(name = "errorMsg")
	private Map<String, Map<String, Object>> errorMsg;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);
	
	private final int SHA256 = 1;
	
	@Transactional
	public Map<String, Object> getAuthorizationCode(Map<String, Object> params) throws AuthorizationException, Exception {
		
		logger.info("#### Authorization Code Grant Start ####");
				
		String clientId = (String)params.get("clientId");
		String redirectUri = (String)params.get("redirectUri");
		String scope = (String)params.get("scope");
		String userId = (String)params.get("userId");
		
		scope = scope.replace(" ", ",");
		
		logger.debug("clientId : " + clientId);
		logger.debug("redirectUri : " + redirectUri);
		logger.debug("scope : " + scope);
		logger.debug("userId : " + userId);
		
		// 설정 파일내 Code 유효시간 설정시간 읽기
		int codeValidTime = Integer.parseInt(messageSource.getMessage("oauth.code.vaildTime", null, Locale.getDefault()));
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> clientInfo = new HashMap<>();
			
		/*
		 * 1. clientId 조회 후 비교
		 */
		clientInfo = authDAO.getClientInfoByClientId(clientId);

		// ERROR : 등록된 클라이언트 아이디가 없을 경우
		if(CheckUtil.isEmpty(clientInfo)) {
			throw new AuthorizationException(4101, (String)errorMsg.get("4101").get("error_description"));
		}
		
		/*
		 *  2. RedirectUri 조회 후 비교
		 */
		List<HashMap<String, Object>> redirectList = authDAO.getClientRedirectUriByClientId(clientId);
		boolean rs = false;
		for(int i = 0; i < redirectList.size(); i++) {
			if(redirectList.get(i).get("REDIRECT_URI").equals(redirectUri)) {
				rs = true;
				break;
			}
		}
		
		// ERROR : 등록된 REDIRECT URI가 없을경우
		if(!rs) {
			throw new AuthorizationException(4106, (String)errorMsg.get("4106").get("error_description"));
		}
		
		/*
		 *  3. Scope 비교 ( Scope가 파라미터로 왔을 경우 )
		 */
		if(CheckUtil.isNotEmpty(scope)) {
			List<String> paramScopesList = Arrays.asList(scope.split(","));
			
			String clientScopes = (String)clientInfo.get("CLIENT_SCOPES");
			List<String> clientScopesList = Arrays.asList(clientScopes.split(","));
			
			logger.debug("paramScopesList : " + paramScopesList);
			logger.debug("clientScopesList : " + clientScopesList);
			
			// ERROR : 클라이언트 허용하는 scopes의 범위가 아닐 경우
			if(!clientScopesList.containsAll(paramScopesList)) {
				throw new AuthorizationException(4111, (String)errorMsg.get("4111").get("error_description"));
			}
		}
		
		/*
		 *  4. Code 발행
		 */
		TokenGenerator tokenGenerator = new TokenGenerator();
		String code = tokenGenerator.generatorToken(codeValidTime);
		
		/*
		 * 5. 유효기간 설정
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		Date date = new Date();			
		date = DateUtils.addMinutes(date, 10);
		String sDate = formatter.format(date);
		
		logger.debug("Code 유효 기간 :" + sDate);
		
		/*
		 * 6. DB 저장
		 */
		HashMap<String, Object> key = new HashMap<>();
		key.put("code", code);
		key.put("clientId", clientId);
		key.put("scope", scope);
		key.put("usableCode", "Y");
		key.put("userId", userId);
		key.put("codeExDate", sDate);
		
		int result = authDAO.setAuthorCode(key);
		
		if(result > 0) {
			returnMap.put("code", code);
		}

		return returnMap;
	}
		
	@Transactional
	public Map<String, Object> getAccessTokenByCode(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken issues by Authorization Code. Start ####");
		
		String code = (String)params.get("code");
		String redirectUri = (String)params.get("redirectUri");
		String clientId = (String)params.get("clientId");
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> authCodeMap = new HashMap<>();
		
		/*
		 * 1. clientId 와 코드 조회 후 비교
		 */
		HashMap<String, Object> clientkey = new HashMap<>();
		clientkey.put("code", code);
		clientkey.put("clientId", clientId);
		
		logger.debug("Code : " + code);
		logger.debug("clientId : " + clientId);
		
		authCodeMap = authDAO.getAuthorCode(clientkey);
		
		// ERROR : 요청온 코드와 클라이언트아이디로 조회가 안될경우. 발급된 코드가 다드거나 유효하지 않은 경우, 발급신청한 클라이언트가 아닐경우
		if(CheckUtil.isEmpty(authCodeMap)) {
			throw new AuthorizationException(4107, (String)errorMsg.get("4107").get("error_description"));
		}
		else if(!code.equals(authCodeMap.get("CODE")) || !"Y".equals(authCodeMap.get("CODE_USABLE"))) {
			throw new AuthorizationException(4108, (String)errorMsg.get("4108").get("error_description"));
		}
		else if(!clientId.equals(authCodeMap.get("CLIENT_ID"))) {
			throw new AuthorizationException(4109, (String)errorMsg.get("4109").get("error_description"));
		}
		
		/*
		 *  2. RedirectUri 조회 후 비교
		 */
		List<HashMap<String, Object>> redirectList = authDAO.getClientRedirectUriByClientId(clientId);
		
		logger.debug("등록된 Redirect URI 정보들 : " + redirectList);
		
		boolean rs = false;
		for(int i = 0; i < redirectList.size(); i++) {
			if(redirectList.get(i).get("REDIRECT_URI").equals(redirectUri)) {
				rs = true;
				break;
			}
		}
		// ERROR : 등록된 REDIRECT URI가 없을경우
		if(!rs) {
			throw new AuthorizationException(4106, (String)errorMsg.get("4106").get("error_description"));
		}
			
		/*
		 * 3. 코드발급시 저장된 시간과 요청왔을때 시간을 비교하여 지정된 시간이상 일경우 에러처리
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 만료시간
		String codeExpiryTime = (String)authCodeMap.get("CODE_EXPIRY_DATE"); 
		Date codeExDate = formatter.parse(codeExpiryTime);
		long codeExDateTime = codeExDate.getTime();
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
		long curDateTime = curDate.getTime();
		
		logger.debug("codeExDateTime - curDateTime : " + (codeExDateTime - curDateTime));
		
		//ERROR : 발급된 코드가 유효기간이 지났을 경우
		if(codeExDateTime - curDateTime < 0) {
			throw new AuthorizationException(4110, (String)errorMsg.get("4110").get("error_description"));
		}
		
		logger.debug("CODE 발행 시간 체크");
		logger.debug("현재 서버시간 : " + curDate);
		logger.debug("코드 만료시간 : " + codeExDate);
		
		/*
		 * 4. 사용된 코드를 유휴하지 않은 상태값으로 변경
		 */
		HashMap<String, Object> usableCodeKey = new HashMap<>();
		usableCodeKey.put("code", code);
		usableCodeKey.put("usableCode", "N");
		
		int resultStatus = authDAO.updateAuthorCodeStatus(usableCodeKey);
		
		if(resultStatus <= 0){
			throw new AuthorizationException(4104, "updateAuthorCodeStatus SQL ERROR");
		}
		
		/*
		 *  5. AccessToken 발급
		 */
		String tokenType = (String)authCodeMap.get("CLIENT_TOKEN_TYPE");
		
		String accessToken = null;
		String refreshToken = null;
		
		if(tokenType.equals("bearer")) {
			
			// 설정 파일내 토큰 길이 설정 읽기
			int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
			int refreshTokenLength = Integer.parseInt(messageSource.getMessage("oauth.refreshToken.length", null, Locale.getDefault()));
			
			accessToken = tokenGenerator.generatorToken(accessTokenLength);
			refreshToken = tokenGenerator.generatorToken(refreshTokenLength);
		}
		else if(tokenType.equals("brearer-jwt")) {
			
		}
		
		String scope = (String)authCodeMap.get("SCOPE");
		String userId = (String)authCodeMap.get("USER_ID");
				
		/*
		 * 6. 토큰 유효기간 설정
		 */
		int accessExpiresIn = Integer.parseInt((String)authCodeMap.get("CLIENT_TOKEN_EXPIRES"));
		int refreshExpiresIn = Integer.parseInt((String)authCodeMap.get("CLIENT_REFRESH_EXPIRES"));
		
		Date accessTokenExDate = curDate;
		Date refreshTokenExDate = curDate;
		
		accessTokenExDate = DateUtils.addSeconds(accessTokenExDate, accessExpiresIn);
		refreshTokenExDate = DateUtils.addSeconds(refreshTokenExDate, refreshExpiresIn);
		
		String sAccessTokenExDate = formatter.format(accessTokenExDate);
		String sRefreshTokenExDate = formatter.format(refreshTokenExDate);
		
		/*
		 * 6. 요청한 Uri를 발급자(issuer)로 저장하기 위한 기본형태 변환처리
		 */
		Map<String, Object> returnUrl = new HashMap<>();
		returnUrl = CheckUtil.uriNormalize(redirectUri);
		
		// ERROR : URI 파싱 실패일 경우
		if(!(Boolean)returnUrl.get("result")) {
			throw new AuthorizationException(4104, "MalformedURLException URL : URL 파싱 실패");
		}
		
		String issuer = (String) returnUrl.get("url");
		
		/*
		 *  7. 토큰 DB 저장
		 */
		HashMap<String, Object> accessTokenkey = new HashMap<>();
		accessTokenkey.put("clientId", clientId);
		accessTokenkey.put("accessToken", accessToken);
		accessTokenkey.put("refreshToken", refreshToken);
		accessTokenkey.put("scope", scope);
		accessTokenkey.put("issuer", issuer);
		accessTokenkey.put("userId", userId);
		accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
		accessTokenkey.put("refreshTokenExDate", sRefreshTokenExDate);
		
		logger.debug("accessTokenkey : " + accessTokenkey);
		
		int result = authDAO.setAccessToken(accessTokenkey);
		
		if(result > 0) {
			
			returnMap.put("access_token", accessToken);
			returnMap.put("token_type", tokenType);
			returnMap.put("expires_in", accessExpiresIn);
			returnMap.put("refresh_token", refreshToken);
		}
		
		return returnMap;
	}
	
	@Transactional
	public Map<String, Object> getAccessTokenByImplicit(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken issues by Implicit. Start ####");
		
		String redirectUri = (String)params.get("redirectUri");
		String clientId = (String)params.get("clientId");
		String scope = (String)params.get("scope");
		String userId = (String)params.get("userId");
		
		scope = scope.replace(" ", ",");
		
		logger.debug("clientId : " + clientId);
		logger.debug("redirectUri : " + redirectUri);
		logger.debug("scope : " + scope);
		logger.debug("userId : " + userId);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> clientInfo = new HashMap<>();
			
		/*
		 * 1. clientId 조회 후 비교
		 */
		clientInfo = authDAO.getClientInfoByClientId(clientId);

		// ERROR : 등록된 클라이언트 아이디가 없을 경우
		if(CheckUtil.isEmpty(clientInfo)) {
			throw new AuthorizationException(4101, (String)errorMsg.get("4101").get("error_description"));
		}
		
		/*
		 * 2. RedirectUri 조회 후 비교
		 */
		List<HashMap<String, Object>> redirectList = authDAO.getClientRedirectUriByClientId(clientId);
		
		logger.debug("등록된 Redirect URI 정보들 : " + redirectList);
		
		boolean rs = false;
		for(int i = 0; i < redirectList.size(); i++) {
			if(redirectList.get(i).get("REDIRECT_URI").equals(redirectUri)) {
				rs = true;
				break;
			}
		}
		
		// ERROR : 등록된 REDIRECT URI가 없을경우
		if(!rs) {
			throw new AuthorizationException(4106, (String)errorMsg.get("4106").get("error_description"));
		}
		
		/*
		 *  3. Scope 비교 ( Scope가 파라미터로 왔을 경우 )
		 */
		if(CheckUtil.isNotEmpty(scope)) {
			List<String> paramScopesList = Arrays.asList(scope.split(","));
			
			String clientScopes = (String)clientInfo.get("CLIENT_SCOPES");
			List<String> clientScopesList = Arrays.asList(clientScopes.split(","));
			
			logger.debug("paramScopesList : " + paramScopesList);
			logger.debug("clientScopesList : " + clientScopesList);
			
			// ERROR : 클라이언트 허용하는 scopes의 범위가 아닐 경우
			if(!clientScopesList.containsAll(paramScopesList)) {
				throw new AuthorizationException(4111, (String)errorMsg.get("4111").get("error_description"));
			}
		}
		
		/*
		 * 4. AccessToken 발급
		 */
		String tokenType = (String)clientInfo.get("CLIENT_TOKEN_TYPE");
		
		// 설정 파일내 토큰 길이 설정 읽기
		int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
		
		String accessToken = tokenGenerator.generatorToken(accessTokenLength);
		
		/*
		 * 5. 요청한 Uri를 발급자(issuer)로 저장하기 위한 기본형태 변환처리
		 */
		Map<String, Object> returnUrl = new HashMap<>();
		returnUrl = CheckUtil.uriNormalize(redirectUri);
		
		// ERROR : URI 파싱 실패일 경우
		if(!(Boolean)returnUrl.get("result")) {
			throw new AuthorizationException(4104, "MalformedURLException URL : URL 파싱 실패");
		}
		
		String issuer = (String) returnUrl.get("url");
		
		/*
		 * 6. 토큰 유효기간 설정
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
		
		int accessExpiresIn = Integer.parseInt((String)clientInfo.get("CLIENT_TOKEN_EXPIRES"));
		
		Date accessTokenExDate = curDate;
		accessTokenExDate = DateUtils.addSeconds(accessTokenExDate, accessExpiresIn);
		String sAccessTokenExDate = formatter.format(accessTokenExDate);
		
		/*
		 * 7. 토큰 DB 저장 
		 */
		HashMap<String, Object> accessTokenkey = new HashMap<>();
		accessTokenkey.put("clientId", clientId);
		accessTokenkey.put("accessToken", accessToken);
		accessTokenkey.put("scope", scope);
		accessTokenkey.put("issuer", issuer);
		accessTokenkey.put("userId", userId);
		accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
		
		int result = authDAO.setAccessToken(accessTokenkey);
		
		if(result > 0) {
			returnMap.put("access_token", accessToken);
			returnMap.put("token_type", tokenType);
			returnMap.put("expires_in", accessExpiresIn);
		}
	
		return returnMap;
	}
	
	@Transactional
	public Map<String, Object> getAccessTokenByPassword(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken issues by Password. Start ####");
		
		String userId = (String)params.get("username");
		String password = (String)params.get("password");
		String clientId = (String)params.get("clientId");
		String scope = (String)params.get("scope");
		
		logger.debug("clientId : " + clientId);
		logger.debug("username : " + userId);
		logger.debug("password : " + password);
		logger.debug("scope : " + scope);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> userInfo = new HashMap<>();
		Map<String, Object> clientInfo = new HashMap<>();
		
		// 설정 파일내 사용자비밀번호 암호화 설정 읽기
		int userPwdType = Integer.parseInt(messageSource.getMessage("oauth.user.password.algo", null, Locale.getDefault()));
		
		if(userPwdType == SHA256) {
			password = crypto.bytesToHex((crypto.digest(password, "SHA-256")));
		}
		
		/*
		 * 1. 사용자 정보 조회 후 패스워드 비교
		 */
		userInfo = authDAO.getUserInfo(userId);
		
		if(CheckUtil.isEmpty(userInfo)) {
			throw new AuthorizationException(4300, (String)errorMsg.get("4300").get("error_description"));
		}
		else if(!password.equals(userInfo.get("USER_PASSWORD"))) {
			throw new AuthorizationException(4301, (String)errorMsg.get("4301").get("error_description"));
		}
		
		/*
		 * 2. clientId 조회 후 비교
		 */
		clientInfo = authDAO.getClientInfoByClientId(clientId);

		// ERROR : 등록된 클라이언트 아이디가 없을 경우
		if(CheckUtil.isEmpty(clientInfo)) {
			throw new AuthorizationException(4101, (String)errorMsg.get("4101").get("error_description"));
		}
		
		/*
		 *  3. Scope 비교 ( Scope가 파라미터로 왔을 경우 )
		 */
		if(CheckUtil.isNotEmpty(scope)) {
			List<String> paramScopesList = Arrays.asList(scope.split(","));
			
			String clientScopes = (String)clientInfo.get("CLIENT_SCOPES");
			List<String> clientScopesList = Arrays.asList(clientScopes.split(","));
			
			logger.debug("paramScopesList : " + paramScopesList);
			logger.debug("clientScopesList : " + clientScopesList);
			
			// ERROR : 클라이언트 허용하는 scopes의 범위가 아닐 경우
			if(!clientScopesList.containsAll(paramScopesList)) {
				throw new AuthorizationException(4111, (String)errorMsg.get("4111").get("error_description"));
			}
		}
	
		/*
		 * 4. AccessToken 발급
		 */
		
		// 설정 파일내 토큰 길이 설정 읽기
		int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
		int refreshTokenLength = Integer.parseInt(messageSource.getMessage("oauth.refreshToken.length", null, Locale.getDefault()));
		
		String tokenType = (String)clientInfo.get("CLIENT_TOKEN_TYPE");
		
		String accessToken = tokenGenerator.generatorToken(accessTokenLength);
		String refreshToken = tokenGenerator.generatorToken(refreshTokenLength);
		
		/*
		 * 5. 토큰 유효기간 설정
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
				
		int accessExpiresIn = Integer.parseInt((String)clientInfo.get("CLIENT_TOKEN_EXPIRES"));
		int refreshExpiresIn = Integer.parseInt((String)clientInfo.get("CLIENT_REFRESH_EXPIRES"));
		
		Date accessTokenExDate = curDate;
		Date refreshTokenExDate = curDate;
		
		accessTokenExDate = DateUtils.addSeconds(accessTokenExDate, accessExpiresIn);
		refreshTokenExDate = DateUtils.addSeconds(refreshTokenExDate, refreshExpiresIn);
		
		String sAccessTokenExDate = formatter.format(accessTokenExDate);
		String sRefreshTokenExDate = formatter.format(refreshTokenExDate);
		
		/*
		 * 6. 토큰 DB 저장 
		 */
		HashMap<String, Object> accessTokenkey = new HashMap<>();
		accessTokenkey.put("clientId", clientId);
		accessTokenkey.put("accessToken", accessToken);
		accessTokenkey.put("refreshToken", refreshToken);
		accessTokenkey.put("scope", scope);
		accessTokenkey.put("userId", userId);
		accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
		accessTokenkey.put("refreshTokenExDate", sRefreshTokenExDate);
					
		int result = authDAO.setAccessToken(accessTokenkey);
		
		if(result > 0) {
			returnMap.put("access_token", accessToken);
			returnMap.put("refreshToken", refreshToken);
			returnMap.put("token_type", tokenType);
			returnMap.put("expires_in", accessExpiresIn);
		}
	
		return returnMap;
	}
	
	@Transactional
	public Map<String, Object> getAccessTokenByclientCredentials(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken issues by client credentials. Start ####");
		
		String clientId = (String)params.get("clientId");
		String clientSecret = (String)params.get("clientSecret");
		String scope = (String)params.get("scope");
		
		logger.debug("clientId : " + clientId);
		logger.debug("clientSecret : " + clientSecret);
		logger.debug("scope : " + scope);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> clientInfo = new HashMap<>();
		
		// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
		int clientPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
		
		if(clientPwdType == SHA256) {
			clientSecret = crypto.bytesToHex((crypto.digest(clientSecret, "SHA-256")));
		}
		
		/*
		 * 1. clientId 및 clientSecret 조회 후 비교
		 */
		clientInfo = authDAO.getClientInfoByClientId(clientId);

		// ERROR : 등록된 클라이언트 아이디가 없을 경우
		if(CheckUtil.isEmpty(clientInfo)) {
			throw new AuthorizationException(4101, (String)errorMsg.get("4101").get("error_description"));
		}
		else if(!clientSecret.equals(clientInfo.get("CLIENT_SECRET"))) {
			throw new AuthorizationException(4400, (String)errorMsg.get("4400").get("error_description"));
		}
		
		/*
		 *  2. Scope 비교 ( Scope가 파라미터로 왔을 경우 )
		 */
		if(CheckUtil.isNotEmpty(scope)) {
			List<String> paramScopesList = Arrays.asList(scope.split(","));
			
			String clientScopes = (String)clientInfo.get("CLIENT_SCOPES");
			List<String> clientScopesList = Arrays.asList(clientScopes.split(","));
			
			logger.debug("paramScopesList : " + paramScopesList);
			logger.debug("clientScopesList : " + clientScopesList);
			
			// ERROR : 클라이언트 허용하는 scopes의 범위가 아닐 경우
			if(!clientScopesList.containsAll(paramScopesList)) {
				throw new AuthorizationException(4111, (String)errorMsg.get("4111").get("error_description"));
			}
		}
	
		/*
		 * 3. AccessToken 발급
		 */
		// 설정 파일내 토큰 길이 설정 읽기
		int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
		
		String tokenType = (String)clientInfo.get("CLIENT_TOKEN_TYPE");
		
		String accessToken = tokenGenerator.generatorToken(accessTokenLength);
				
		/*
		 * 4. 토큰 유효기간 설정
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
				
		int accessExpiresIn = Integer.parseInt((String)clientInfo.get("CLIENT_TOKEN_EXPIRES"));
		
		Date accessTokenExDate = curDate;
		accessTokenExDate = DateUtils.addSeconds(accessTokenExDate, accessExpiresIn);
		String sAccessTokenExDate = formatter.format(accessTokenExDate);
		
		/*
		 * 5. 토큰 DB 저장 
		 */
		HashMap<String, Object> accessTokenkey = new HashMap<>();
		accessTokenkey.put("clientId", clientId);
		accessTokenkey.put("accessToken", accessToken);
		accessTokenkey.put("scope", scope);
		accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
		
		int result = authDAO.setAccessToken(accessTokenkey);
		
		if(result > 0) {
			returnMap.put("access_token", accessToken);
			returnMap.put("token_type", tokenType);
			returnMap.put("expires_in", accessExpiresIn);
		}
	
		return returnMap;
	}
	
	@Transactional
	public Map<String, Object> getAccessTokenByRefreshToken(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken Re-issues by Refresh Token. Start ####");
		
		String clientId = (String)params.get("clientId");
		String refreshToken = (String)params.get("refreshToken");
		
		logger.debug("clientId : " + clientId);
		logger.debug("refreshToken : " + refreshToken);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> tokenInfo = new HashMap<>();
		
		/*
		 * 1. Refresh Token, ClientId 조회 후 비교
		 */
		tokenInfo = authDAO.getAccessTokenByRefreshToken(refreshToken);

		// ERROR : 등록된 Refresh Token 없을 경우, 조회된 클라이언트 아이디가 다를 경우
		if(CheckUtil.isEmpty(tokenInfo)) {
			throw new AuthorizationException(6000, (String)errorMsg.get("6000").get("error_description"));
		}
		else if(!clientId.equals(tokenInfo.get("CLIENT_ID"))) {
			throw new AuthorizationException(4107, (String)errorMsg.get("4107").get("error_description"));
		}
		
		/*
		 * 2. Refresh 토큰 발급 유효시간 체크
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 만료시간
		String refreshTokenExpiryTime = (String)tokenInfo.get("RTOKEN_EXPIRY_DATE"); 
		Date refreshTokenExDate = formatter.parse(refreshTokenExpiryTime);
		long refreshTokenExDateTime = refreshTokenExDate.getTime();
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
		long curDateTime = curDate.getTime();
		
		logger.debug("refreshTokenExDateTime - curDateTime : " + (refreshTokenExDateTime - curDateTime));
		
		//ERROR : 발급된 RefreshToken의 유효기간이 지났을 경우
		if(refreshTokenExDateTime - curDateTime < 0) {
			throw new AuthorizationException(6001, (String)errorMsg.get("6001").get("error_description"));
		}
	
		/*
		 *  3. AccessToken 발급
		 */
		
		// 설정 파일내 토큰 길이 설정 읽기
		int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
		int refreshTokenLength = Integer.parseInt(messageSource.getMessage("oauth.refreshToken.length", null, Locale.getDefault()));
		
		String accessToken = (String)tokenInfo.get("ACCESS_TOKEN");
		String tokenType = (String)tokenInfo.get("CLIENT_TOKEN_TYPE");
		
		TokenGenerator tokenGenerator = new TokenGenerator();
		String newAccessToken = tokenGenerator.generatorToken(accessTokenLength);
		String newRefreshToken = tokenGenerator.generatorToken(refreshTokenLength);
		
		/*
		 * 4. 토큰 유효기간 설정
		 */
		int accessExpiresIn = Integer.parseInt((String)tokenInfo.get("CLIENT_TOKEN_EXPIRES"));
		int refreshExpiresIn = Integer.parseInt((String)tokenInfo.get("CLIENT_REFRESH_EXPIRES"));
		
		Date accessTokenExDate = curDate;
		Date newRefreshTokenExDate = curDate;
		
		accessTokenExDate = DateUtils.addSeconds(accessTokenExDate, accessExpiresIn);
		newRefreshTokenExDate = DateUtils.addSeconds(newRefreshTokenExDate, refreshExpiresIn);
		
		String sAccessTokenExDate = formatter.format(accessTokenExDate);
		String sRefreshTokenExDate = formatter.format(newRefreshTokenExDate);
		
		HashMap<String, Object> accessTokenkey = new HashMap<>();
		accessTokenkey.put("asisAccessToken", accessToken);
		accessTokenkey.put("clientId", clientId);
		accessTokenkey.put("accessToken", newAccessToken);
		accessTokenkey.put("refreshToken", newRefreshToken);
		accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
		accessTokenkey.put("refreshTokenExDate", sRefreshTokenExDate);
		
		int result = authDAO.updateAccessToken(accessTokenkey);
		
		if(result > 0) {
			returnMap.put("access_token", newAccessToken);
			returnMap.put("refreshToken", newRefreshToken);
			returnMap.put("token_type", tokenType);
			returnMap.put("expires_in", accessExpiresIn);
		}
		
		return returnMap;
	}
	
	@Transactional
	public Map<String, Object> tokenValidation(Map<String, Object> params) throws AuthorizationException, Exception{
		
		logger.info("#### AccessToken validation. Start ####");
				
		String authorization = (String)params.get("authorization");
		
		logger.debug("authorization : " + authorization);
		
		Map<String, Object> returnMap = new HashMap<>();
		Map<String, Object> tokenInfo = new HashMap<>();
		
		String activeValue = "false";
		String apiId = "";
			
		/*
		 * 1. Request Header 에 있던 authorization 유형별 분기
		 *  1) Basic - 등록된 API Id, API Secret 을 검증 후 AccessToken 유휴상태 검증
		 *  2) Bearer - 유효한 AccessToken(요청용)을 전달받아 다른 AccessToken(검증하고자 하는 토큰) 유휴상태 검증 ,
		 *  			이때 접속 로그는 요청용으로 사용된 토큰의 발급자 클라이언트 아이디로 API ID를 사용 
		 */
		if(authorization.indexOf("Basic") >= 0) {
			
			Map<String, Object> apiInfo = new HashMap<>();
			
			String encAuthStr = authorization.replaceAll("Basic ", "");
			
			Decoder decoder = Base64.getDecoder();
			byte[] decAuthBytes = decoder.decode(encAuthStr);
			String decAuthStr = new String(decAuthBytes, "UTF-8");
			
			String[] decAuthInfo = decAuthStr.split(":");
			apiId = decAuthInfo[0];
			String apiSecret = decAuthInfo[1];
			
			logger.debug("apiId : " + apiId);
			
			apiInfo = authDAO.getApiInfoByApiId(apiId);
			
			// 설정 파일내 클라이언트 비밀번호 암호화 설정 읽기
			int apiPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
			
			if(apiPwdType == SHA256) {
				apiSecret = crypto.bytesToHex((crypto.digest(apiSecret, "SHA-256")));
			}
			
			// ERROR : 등록된 API 아이디가 없을 경우, API가 사용 중지상태인지 확인, API 패스워드가 다를 경우
			if(CheckUtil.isEmpty(apiInfo)) {
				throw new AuthorizationException(4101, (String)errorMsg.get("4101").get("error_description"));
			}
			else if(!"Y".equalsIgnoreCase((String)apiInfo.get("API_USABLE"))){
				throw new AuthorizationException(4112, (String)errorMsg.get("4112").get("error_description"));
			}
			else if(!apiSecret.equals(apiInfo.get("API_SECRET"))) {
				throw new AuthorizationException(4400, (String)errorMsg.get("4400").get("error_description"));
			}
			
		}
		else if(authorization.indexOf("Bearer") >= 0) {
			
			Map<String, Object> authTokenInfo = new HashMap<>();
			
			String resourceAccessToken = authorization.replaceAll("Bearer ", "");
			
			authTokenInfo = authDAO.getAccessToken(resourceAccessToken);
			
			// ERROR : 등록된 인증요청 토큰이 없을 경우
			if(CheckUtil.isEmpty(authTokenInfo)) {
				throw new AuthorizationException(7001, (String)errorMsg.get("7001").get("error_description"));
			}
			
			apiId = (String) authTokenInfo.get("CLIENT_ID");
			
			// ==============================
			// AuthToken 검증은 로직 차후에 삽입 할 것
			// ==============================
			
		}
		// ERROR : 잘못된 authorization Type
		else {
			throw new AuthorizationException(4100, (String)errorMsg.get("4100").get("error_description"));
		}
		
		/*
		 * 2. Access Token 정보 조회
		 */
		String accessToken = (String) params.get("token");
		
		tokenInfo = authDAO.getAccessToken(accessToken);
		
		//ERROR : 등록된 AccessToken이 없을 경우
		if(CheckUtil.isEmpty(tokenInfo)) {
			logger.error("결과 : 조회되지 않는 토큰");
			throw new AuthorizationException(6002, (String)errorMsg.get("6002").get("error_description"));
		}
		
		/*
		 * 3. Access Token 유효기간 상태 확인
		 */
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		
		// 만료시간
		String accessTokenExpiryTime = (String)tokenInfo.get("ATOKEN_EXPIRY_DATE"); 
		Date accessTokenExDate = formatter.parse(accessTokenExpiryTime);
		long accessTokenExDateTime = accessTokenExDate.getTime();
		
		// 현재 서버 시간
		Date curDate = new Date();
		curDate = formatter.parse(formatter.format(curDate));
		long curDateTime = curDate.getTime();
		
		logger.debug("accessTokenExDateTime - curDateTime : (초) " + (accessTokenExDateTime - curDateTime)/1000);

		//ERROR : 발급된 AccessToken의 유효기간이 지났을 경우
		if(accessTokenExDateTime - curDateTime < 0) {
			logger.error("결과 : 만료된 토큰");
			throw new AuthorizationException(6001, (String)errorMsg.get("6001").get("error_description"));
		}

		/*
		 * 4. Access Token 검증 결과 저장
		 *  1) 자동연장 기능 있을 경우 : 토큰 유효기간을 연장하고 결과를 리턴한다.
		 *  2) 자동연장 기능 없을 경우 : 토큰 검증 결과만 리턴한다.
		 */
		activeValue = "true";
		String autoEx = (String)tokenInfo.get("CLIENT_TOKEN_AUTOEX");
		
		returnMap.put("active", activeValue);
		returnMap.put("client_id", tokenInfo.get("CLIENT_ID"));
		returnMap.put("username", tokenInfo.get("USER_ID"));
		
		String scopes = (String)tokenInfo.get("SCOPE");
		if(CheckUtil.isNotEmpty(scopes)) {
			scopes.replace(",", " ");
			returnMap.put("scope", scopes);
		}
		if(CheckUtil.isNotEmpty((String)tokenInfo.get("ISSUER"))) {
			returnMap.put("iss", tokenInfo.get("ISSUER"));
		}
		
		// 1) 자동 연장기능 되어있을 경우
		if(CheckUtil.isNotEmpty(autoEx) && autoEx.equals("y")) {
								
			int accessExpiresIn = Integer.parseInt((String)tokenInfo.get("CLIENT_TOKEN_EXPIRES"));
			int refreshExpiresIn = Integer.parseInt((String)tokenInfo.get("CLIENT_REFRESH_EXPIRES"));
			
			Date newAccessTokenExDate = curDate;
			Date newRefreshTokenExDate = curDate;
			
			newAccessTokenExDate = DateUtils.addSeconds(newAccessTokenExDate, accessExpiresIn);
			newRefreshTokenExDate = DateUtils.addSeconds(newRefreshTokenExDate, refreshExpiresIn);
			
			String sAccessTokenExDate = formatter.format(newAccessTokenExDate);
			String sRefreshTokenExDate = formatter.format(newRefreshTokenExDate);
			
			HashMap<String, Object> accessTokenkey = new HashMap<>();
			accessTokenkey.put("clientId", tokenInfo.get("CLIENT_ID"));
			accessTokenkey.put("accessToken", accessToken);
			accessTokenkey.put("refreshToken", tokenInfo.get("REFRESH_TOKEN"));
			accessTokenkey.put("asisAccessToken", accessToken);
			accessTokenkey.put("accessTokenExDate", sAccessTokenExDate);
			accessTokenkey.put("refreshTokenExDate", sRefreshTokenExDate);
			
			int result = authDAO.updateAccessToken(accessTokenkey);
			
			// Return value => ERROR : DB 업데이트 실패된 경우
			if(result <= 0) {
				throw new AuthorizationException(4101, (String)errorMsg.get("4104").get("error_description"));
			}
			
			returnMap.put("exp", (curDateTime / 1000) + accessExpiresIn);
			returnMap.put("iat", curDateTime / 1000);
		}
		// 2) 자동 연장기능이 없을 경우
		else {
			
			String accessTokenCreationTime = (String)tokenInfo.get("CREATION_DATE");
			Date accessTokenCtDate = formatter.parse(accessTokenCreationTime);
			long accessTokenCtDateTime = accessTokenCtDate.getTime();
			
			returnMap.put("exp", accessTokenExDateTime / 1000);
			returnMap.put("iat", accessTokenCtDateTime / 1000);
		}
		
		/*
		 * 5. API Accept Log 저장
		 */
		HashMap<String, Object> apiLogkeys = new HashMap<>();
		apiLogkeys.put("apiId", apiId);
		apiLogkeys.put("clientId", tokenInfo.get("CLIENT_ID"));
		apiLogkeys.put("userId", tokenInfo.get("USER_ID"));
		
		authDAO.setApiLog(apiLogkeys);
	
		return returnMap;
	}
	
	public Map<String, Object> getOauthConfig(){
		
		logger.info("#### getOauthConfig Start ####");
		
		Map<String, Object> returnMap = new HashMap<>();
		
		int codeValidTime = Integer.parseInt(messageSource.getMessage("oauth.code.vaildTime", null, Locale.getDefault()));
		int accessTokenLength = Integer.parseInt(messageSource.getMessage("oauth.accessToken.length", null, Locale.getDefault()));
		int refreshTokenLength = Integer.parseInt(messageSource.getMessage("oauth.refreshToken.length", null, Locale.getDefault()));
		int userPwdType = Integer.parseInt(messageSource.getMessage("oauth.user.password.algo", null, Locale.getDefault()));
		int clientPwdType = Integer.parseInt(messageSource.getMessage("oauth.client.password.algo", null, Locale.getDefault()));
		
		returnMap.put("codeValidTime", codeValidTime);
		returnMap.put("accessTokenLength", accessTokenLength);
		returnMap.put("refreshTokenLength", refreshTokenLength);
		returnMap.put("userPwdType", userPwdType);
		returnMap.put("clientPwdType", clientPwdType);
		
		return returnMap;
	}
	
	public boolean setOauthConfig(HashMap<String, Object> params){
		
		logger.info("#### setOauthConfig Start ####");
		
		boolean result = false;
		
		String codeVaildTime = (String)params.get("authCode");
		String accessTokenLength = (String)params.get("accessTokenLength");
		String refreshTokenLength = (String)params.get("refreshTokenLength");
		String userPwdType = (String)params.get("userPwdRadio");
		String clientPwdType = (String)params.get("clientPwdRadio");
		
		Map<String, String> configParams = new HashMap<String, String>();
		configParams.put("oauth.code.vaildTime", codeVaildTime);
		configParams.put("oauth.accessToken.length", accessTokenLength);
		configParams.put("oauth.refreshToken.length", refreshTokenLength);
		configParams.put("oauth.user.password.algo", userPwdType);
		configParams.put("oauth.client.password.algo", clientPwdType);
		
		result = replaceFileContents.replace(configParams);
		
		return result;
		
	}
	
}
