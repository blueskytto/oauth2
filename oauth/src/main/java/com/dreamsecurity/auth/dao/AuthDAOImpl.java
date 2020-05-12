package com.dreamsecurity.auth.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
public class AuthDAOImpl implements AuthDAO {
	
	@Inject
	private SqlSessionTemplate sqlSession;
	
	private static final String namespace = "oauth";
	
	@Override
	public String getNow() {
		return sqlSession.selectOne(namespace + ".getNow");
	}
	
	@Override
	public HashMap<String, Object> getResourceUserInfoByID(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getResourceUserInfoByID", key);
	}
	
	@Override
	public int setAuthorCode(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setAuthorCode", key);
	}
	
	@Override
	public HashMap<String, Object> getAuthorCode(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getAuthorCode", key);
	}
	
	@Override
	public int updateAuthorCodeStatus(HashMap<String, Object> key) throws SQLException {
		return sqlSession.update(namespace + ".updateAuthorCodeStatus", key);
	}
	
	@Override
	public HashMap<String, Object> getClientInfoByClientId(String key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getClientInfoByClientId", key);
	}
	
	@Override
	public List<HashMap<String, Object>> getClientRedirectUriByClientId(String key) throws SQLException {
		return sqlSession.selectList(namespace + ".getClientRedirectUriByClientId", key);
	}
	
	@Override
	public int setAccessToken(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setAccessToken", key);
	}
	
	@Override
	public HashMap<String, Object> getUserInfo(String key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getUserInfo", key);
	}
	
	@Override
	public HashMap<String, Object> getAccessToken(String key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getAccessToken", key);
	}
	
	@Override
	public HashMap<String, Object> getAccessTokenByRefreshToken(String key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getAccessTokenByRefreshToken", key);
	}
	
	@Override
	public int updateAccessToken(HashMap<String, Object> key) throws SQLException {
		return sqlSession.update(namespace + ".updateAccessToken", key);
	}
	
	@Override
	public List<HashMap<String, Object>> getClientList(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectList(namespace + ".getClientList", key);
	}
	
	@Override
	public List<HashMap<String, Object>> getScopesList() throws SQLException {
		return sqlSession.selectList(namespace + ".getScopesList");
	}
	
	@Override
	public List<HashMap<String, Object>> getGrantTypesList() throws SQLException {
		return sqlSession.selectList(namespace + ".getGrantTypesList");
	}
	
	@Override
	public int updateClientInfo(HashMap<String, Object> key) throws SQLException {
		return sqlSession.update(namespace + ".updateClientInfo", key);
	}
	
	@Override
	public int deleteClientUriList(String key) throws SQLException {
		return sqlSession.delete(namespace + ".deleteClientUriList", key);
	}
	
	@Override
	public int setClientUri(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setClientUri", key);
	}
	
	@Override
	public int getClientTotalList(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getClientTotalList", key);
	}
	
	@Override
	public int deleteClient(String key) throws SQLException {
		return sqlSession.delete(namespace + ".deleteClient", key);
	}
	
	@Override
	public int setClient(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setClient", key);
	}
	
	@Override
	public int getClientCheck(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getClientCheck", key);
	}
	
	@Override
	public List<HashMap<String, Object>> getApiList(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectList(namespace + ".getApiList", key);
	}
	
	@Override
	public int getApiTotalList(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getApiTotalList", key);
	}
	
	@Override
	public HashMap<String, Object> getApiInfoByApiId(String key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getApiInfoByApiId", key);
	}
	
	@Override
	public int updateApiInfo(HashMap<String, Object> key) throws SQLException {
		return sqlSession.update(namespace + ".updateApiInfo", key);
	}
	
	@Override
	public int deleteApi(String key) throws SQLException {
		return sqlSession.delete(namespace + ".deleteApi", key);
	}
	
	@Override
	public int setApi(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setApi", key);
	}
	
	@Override
	public int getApiCheck(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectOne(namespace + ".getApiCheck", key);
	}
	
	@Override
	public int setApiLog(HashMap<String, Object> key) throws SQLException {
		return sqlSession.insert(namespace + ".setApiLog", key);
	}
	
	@Override
	public List<HashMap<String, Object>> getApiLog(HashMap<String, Object> key) throws SQLException {
		return sqlSession.selectList(namespace + ".getApiLog", key);
	}
	
}
