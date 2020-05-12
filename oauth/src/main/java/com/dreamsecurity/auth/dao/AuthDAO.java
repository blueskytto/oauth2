package com.dreamsecurity.auth.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface AuthDAO  {
	
	public String getNow();
    public HashMap<String, Object> getResourceUserInfoByID(HashMap<String, Object> key) throws SQLException;
    public HashMap<String, Object> getAuthorCode(HashMap<String, Object> key) throws SQLException;
	public int setAuthorCode(HashMap<String, Object> key) throws SQLException;
	public int updateAuthorCodeStatus(HashMap<String, Object> key) throws SQLException;
	public HashMap<String, Object> getClientInfoByClientId(String key) throws SQLException;
	public List<HashMap<String, Object>> getClientRedirectUriByClientId(String key) throws SQLException;
	public int setAccessToken(HashMap<String, Object> key) throws SQLException;
	public HashMap<String, Object> getUserInfo(String key) throws SQLException;
	public HashMap<String, Object> getAccessToken(String key) throws SQLException;
	public HashMap<String, Object> getAccessTokenByRefreshToken(String key) throws SQLException;
	public int updateAccessToken(HashMap<String, Object> key) throws SQLException;
	public List<HashMap<String, Object>> getClientList(HashMap<String, Object> key) throws SQLException;
	public List<HashMap<String, Object>> getScopesList() throws SQLException;
	public List<HashMap<String, Object>> getGrantTypesList() throws SQLException;
	public int updateClientInfo(HashMap<String, Object> key) throws SQLException;
	public int deleteClientUriList(String key) throws SQLException;
	public int setClientUri(HashMap<String, Object> key) throws SQLException;
	public int getClientTotalList(HashMap<String, Object> key) throws SQLException;
	public int deleteClient(String key) throws SQLException;
	public int setClient(HashMap<String, Object> key) throws SQLException;
	public int getClientCheck(HashMap<String, Object> key) throws SQLException;
	
	public List<HashMap<String, Object>> getApiList(HashMap<String, Object> key) throws SQLException;
	public int getApiTotalList(HashMap<String, Object> key) throws SQLException;
	public HashMap<String, Object> getApiInfoByApiId(String key) throws SQLException;
	public int updateApiInfo(HashMap<String, Object> key) throws SQLException;
	public int deleteApi(String key) throws SQLException;
	public int setApi(HashMap<String, Object> key) throws SQLException;
	public int getApiCheck(HashMap<String, Object> key) throws SQLException;
	public int setApiLog(HashMap<String, Object> key) throws SQLException;
	public List<HashMap<String, Object>> getApiLog(HashMap<String, Object> key) throws SQLException;
	
}
