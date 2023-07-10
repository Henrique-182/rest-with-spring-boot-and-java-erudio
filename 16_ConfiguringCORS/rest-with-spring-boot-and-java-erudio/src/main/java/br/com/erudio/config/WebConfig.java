package br.com.erudio.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.erudio.serialization.converter.YamlJackson2HttpMessageConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new YamlJackson2HttpMessageConverter());
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		/*
		configurer.favorParameter(true);
		configurer.parameterName("mediaType");
		configurer.ignoreAcceptHeader(true); 
		configurer.useRegisteredExtensionsOnly(false);
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
		configurer.mediaType("json", MediaType.APPLICATION_JSON);
		configurer.mediaType("xml", MediaType.APPLICATION_XML);
		*/
		
		configurer.favorParameter(false);
		configurer.ignoreAcceptHeader(false); 
		configurer.useRegisteredExtensionsOnly(false);
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
		configurer.mediaType("json", MediaType.APPLICATION_JSON);
		configurer.mediaType("xml", MediaType.APPLICATION_XML);
		configurer.mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML);
	}

}
