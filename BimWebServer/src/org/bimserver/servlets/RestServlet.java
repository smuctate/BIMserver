package org.bimserver.servlets;

import javax.servlet.ServletConfig;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.bimserver.webservices.RestAuthentication;

public class RestServlet extends CXFNonSpringJaxrsServlet {
	private static final long serialVersionUID = 6288864278630843847L;
	
	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		getBus().getInInterceptors().add(new RestAuthentication());
	}
}