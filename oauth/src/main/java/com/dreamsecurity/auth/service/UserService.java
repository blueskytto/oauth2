package com.dreamsecurity.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dreamsecurity.auth.common.vo.PagingVo;
import com.dreamsecurity.auth.exception.AuthorizationException;

public interface UserService {
	
	public Map<String, Object> login(Map<String, Object> params) throws AuthorizationException, Exception;
	public List<HashMap<String, Object>> clientList(PagingVo paging, String clientOwner) throws Exception;
	public List<HashMap<String, Object>> clientList(PagingVo paging) throws Exception;
	public Map<String, Object> clientDetail(String param) throws Exception;
	public List<HashMap<String, Object>> scopesList() throws Exception;
	public List<HashMap<String, Object>> grantTypesList() throws Exception;
	public List<HashMap<String, Object>> getClientUriList(String clientId) throws Exception;
	public void modifyClient(HashMap<String, Object> params, List<String> uriList) throws Exception;
	public int getClientTotalList(String clientOwner) throws Exception;
	public int getClientTotalList() throws Exception;
	public int deleteClient(String clientId) throws Exception;
	public void addClient(HashMap<String, Object> params, List<String> uriList) throws Exception;
	public int getClientCheck(String clientId) throws Exception;
	public List<HashMap<String, Object>> getApiList(PagingVo paging, String apiList) throws Exception;
	
	public int getApiTotalList(String apiList) throws Exception;
	public Map<String, Object> apiDetail(String param) throws Exception;
	public void modifyApi(HashMap<String, Object> params) throws Exception;
	public int deleteApi(String apiId) throws Exception;
	public void addApi(HashMap<String, Object> params) throws Exception;
	public int getApiCheck(String apiId) throws Exception;
	public List<HashMap<String, Object>> getApiLog(String clientId, String startDate, String endDate) throws Exception;
}
