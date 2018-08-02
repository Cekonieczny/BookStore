package pl.jstk.controller;


import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import pl.jstk.constants.ViewNames;

@ControllerAdvice
public class ControllerExceptionAdvice {

		
		@ExceptionHandler(AccessDeniedException.class)
		public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("path", request.getRequestURI());
			modelAndView.addObject("message", ex.getMessage());
			modelAndView.setViewName(ViewNames.ERROR_403);
			return modelAndView;
		}
		
}
