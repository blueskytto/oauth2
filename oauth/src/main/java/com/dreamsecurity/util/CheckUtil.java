package com.dreamsecurity.util;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckUtil {

	/**
	  * Object type 변수가 비어있는지 체크
	  * 
	  * @param obj 
	  * @return Boolean : true / false
	  */
	public static Boolean isEmpty(Object obj) {
		if (obj instanceof String) return obj == null || "".equals(obj.toString().trim());
		else if (obj instanceof List) return obj == null || ((List<?>) obj).isEmpty();
		else if (obj instanceof Map) return obj == null || ((Map<?,?>) obj).isEmpty();
		else if (obj instanceof Object[]) return obj == null || Array.getLength(obj) == 0;
		else return obj == null;
	}
	 
	public static boolean isNotEmpty(String s){
		return !isEmpty(s);
	}
	
	/**
	 * 
	 * @param domain (ex : https://sp1.dev.com:8080/test?name=jke )
	 * @return normalizeUri (ex : https://sp1.dev.com:8080 )
	 */
	public static Map<String, Object> uriNormalize(String domain) {
		
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("result", false);
		
		try {
			URL url = new URL(domain);
			String rsUrl = url.getProtocol() + "://" + url.getAuthority();
			returnMap.clear();
			returnMap.put("result", true);
			returnMap.put("url", rsUrl);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return returnMap;
		}
		return returnMap;
	}
	
}
