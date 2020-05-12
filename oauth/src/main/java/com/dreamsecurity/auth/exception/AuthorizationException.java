package com.dreamsecurity.auth.exception;

public class AuthorizationException extends Exception {
	
	private static final long serialVersionUID = 1L;
	protected int errCode = 0000;

	public AuthorizationException() {
		super();
	}
	
	public AuthorizationException(String msg){
		super(msg);
	}
	
	public AuthorizationException(int errCode, String msg){
		super(msg);
		this.errCode = errCode;
	}
	
	public int getErrorCode()
	{
		return errCode;
	}
}
