package com.dreamsecurity.auth.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.springframework.stereotype.Component;

@Component
public class ReplaceFileContents {

	static String proFName = "oauthConfig.properties";
	
	public boolean replace(Map<String, String> params) {
		
		PropertiesConfiguration config = new PropertiesConfiguration();
		PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
		
		try {

			File f = new File(this.getClass().getClassLoader().getResource(proFName).getFile());
			layout.load(new InputStreamReader(new FileInputStream(f)));

			if (!params.isEmpty()) {
				for (String key : params.keySet()) {
					if (config.containsKey(key)) {
						config.setProperty(key, params.get(key));
					}
				}
			}
			
			layout.save(new FileWriter(f));
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
