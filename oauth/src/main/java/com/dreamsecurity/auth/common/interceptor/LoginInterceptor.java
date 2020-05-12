package com.dreamsecurity.auth.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("USER_ID");
		
		String uri = request.getQueryString();
		
		if (uri == null) {
			uri = request.getRequestURL().toString();
		} else {
			uri = request.getRequestURL().append("?").append(request.getQueryString()).toString();
			session.setAttribute("SAVED_REQUEST", uri);
		}
		
		logger.debug("요청온 URI :" + uri);

		if (userId == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		request.getSession().removeAttribute("SAVED_REQUEST");
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}
}
