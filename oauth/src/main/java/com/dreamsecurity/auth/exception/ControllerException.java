package com.dreamsecurity.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerException {
	
	private static final Logger logger = LoggerFactory.getLogger(ControllerException.class);

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public void handleMissingParams(MissingServletRequestParameterException ex) {
		
	    String name = ex.getParameterName();
	    logger.error(name + " parameter is missing");
	    // Actual exception handling
	}
}
