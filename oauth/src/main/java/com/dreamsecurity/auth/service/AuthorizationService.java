package com.dreamsecurity.auth.service;

import java.util.HashMap;
import java.util.Map;

import com.dreamsecurity.auth.exception.AuthorizationException;

public interface AuthorizationService {

	public Map<String, Object> getAuthorizationCode(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getAccessTokenByCode(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getAccessTokenByImplicit(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getAccessTokenByPassword(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getAccessTokenByclientCredentials(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getAccessTokenByRefreshToken(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> tokenValidation(Map<String, Object> params) throws AuthorizationException, Exception;
	public Map<String, Object> getOauthConfig();
	public boolean setOauthConfig(HashMap<String, Object> params);
}
