package fr.harmonieMutuelle.bpm.lib.jms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("file:C:/Users/meharzi-a/Documents/BONITA_BUNDLE/server/application.properties")
@ConfigurationProperties("jms")
public class JmsConfig {
	
	private String server_url;
	
	private String user_name;
	
	private String password;

	private String default_destination_name;
	
	private String reject_destination_name;
	
	private String error_destination_name;
	
	public String getError_destination_name() {
		return error_destination_name;
	}

	public void setError_destination_name(String error_destination_name) {
		this.error_destination_name = error_destination_name;
	}

	public String getReject_destination_name() {
		return reject_destination_name;
	}

	public void setReject_destination_name(String reject_destination_name) {
		this.reject_destination_name = reject_destination_name;
	}

	public String getDefault_destination_name() {
		return default_destination_name;
	}

	public void setDefault_destination_name(String default_destination_name) {
		this.default_destination_name = default_destination_name;
	}

	public String getServer_url() {
		return server_url;
	}

	public void setServer_url(String server_url) {
		this.server_url = server_url;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "JmsConfig [server_url=" + server_url + ", user_name=" + user_name + ", password=" + password + "]";
	}
	
	 @Bean
	    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }

}
