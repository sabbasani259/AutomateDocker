//CR406 : 20231117 : Dhiraj K : CORS Handling

package remote.wise.service.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.JwTokenUtil;

 
public class RestCorsFilter implements Filter{
	
	static Logger ilogger = InfoLoggerClass.logger;
	static Logger flogger = FatalLoggerClass.logger;

	private static final String[] IP_HEADERS = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"

			// you can add more matching headers here ...
	};

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain arg2)
			throws IOException, ServletException {
		List<String> validRefererList = new ArrayList<String>(
				Arrays.asList("http://localhost", "http://10.210.196.206","https://llieng.jcbdigital.in","http://10.210.196.51","https://13.126.159.18","http://10.210.196.39","https://customerui.jcblivelink.in","https://llistaging.jcbdigital.in"));

		String jwtToken = null;

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String referer = ((HttpServletRequest) request).getHeader("Referer");
			String requestIP = getRequestIP(httpServletRequest);
			ilogger.info(
					"referer:" + referer + ", requestIP:" + requestIP + ", URI:" + httpServletRequest.getRequestURI());
			boolean authorized = false;
			boolean validReferer = false; // Authorize (allow) all domains to consume the content
			//HttpServletRequest  request   = (HttpServletRequest)servletRequest;
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers", ((HttpServletRequest)request).getHeader("Access-Control-Request-Headers"));
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

			// check referer
			if (referer != null) {
				for (String ref : validRefererList) {
					if (referer.indexOf(ref) == 0) {
						validReferer = true;
						break;
					}
				}
			}else {
				if (httpServletRequest.getRequestURI().contains("UserTokenService/validateUserToken")
						|| httpServletRequest.getRequestURI().contains("project/getProjectMachines")
						|| httpServletRequest.getRequestURI().contains("project/getSiteMachines")
						|| httpServletRequest.getRequestURI().contains("MachineGroupAsset/getMachineGroupAsset")
						|| httpServletRequest.getRequestURI().contains("VinNoFromGroupIdRESTService/VinNoFromGroupId")
						|| httpServletRequest.getRequestURI().contains("MachineGroupAsset/getMachineGroupIds")
						|| httpServletRequest.getRequestURI().contains("MADashBoardEmailSubscribersRestService/MADashBoardDetails")
						|| httpServletRequest.getRequestURI().contains("PdfBatchGeneratorService/generatePdf")
						|| httpServletRequest.getRequestURI().contains("PdfBatchGeneratorService/sendPdf")) {
					//no referer check required, as invoked from MDA Reports layer
					validReferer = true;
				}
			}
			if (validReferer) {
				if (httpServletRequest.getRequestURI().contains("UserAuthentication/authenticate")
						|| httpServletRequest.getRequestURI().contains("UserTokenService/validateUserToken")
						|| httpServletRequest.getRequestURI().contains("ForgotPasswordRESTService/getForgottenPassword")
						|| httpServletRequest.getRequestURI().contains("ForgotLoginIdRESTService/getForgottenLoginID")
						|| httpServletRequest.getRequestURI().contains("ForgotLoginIdRESTService/authenicateLoginIDOrMobileNo")
						|| httpServletRequest.getRequestURI().contains("LoginRegistrationRESTService/getSecretQuestions")
						|| httpServletRequest.getRequestURI().contains("TestServiceV2/testMethod")
						|| httpServletRequest.getRequestURI().contains("TestServiceV3/testMethod")
						|| httpServletRequest.getRequestURI().contains("project/getProjectMachines")
						|| httpServletRequest.getRequestURI().contains("project/getSiteMachines")
						|| httpServletRequest.getRequestURI().contains("MachineGroupAsset/getMachineGroupAsset")
						|| httpServletRequest.getRequestURI().contains("VinNoFromGroupIdRESTService/VinNoFromGroupId")
						|| httpServletRequest.getRequestURI().contains("MachineGroupAsset/getMachineGroupIds")
						|| httpServletRequest.getRequestURI().contains("MADashBoardEmailSubscribersRestService/MADashBoardDetails")
						|| httpServletRequest.getRequestURI().contains("PdfBatchGeneratorService/generatePdf")
						|| httpServletRequest.getRequestURI().contains("PdfBatchGeneratorService/sendPdf")
						
						) {
					    ilogger.info("no check for "+httpServletRequest.getRequestURI());
					    authorized = true;
				}else {
					// check jwt token present or not
					jwtToken = httpServletRequest.getHeader("TokenId");
					String tokenId = null;
					if (jwtToken != null) {
						tokenId = JwTokenUtil.validateJwt(jwtToken).get("loginId");
						ilogger.info("loginId from JWT ::loginId::"+tokenId);
						if (tokenId!=null) {
							authorized = true;
						} else {
							authorized = false;
						}
					}
				}
			} else {
				ilogger.info(referer + " not a valid  referer !!!!!!!!!!!!!!!!!!!!!!!!!");
			}

			if (authorized) {
				try {
					arg2.doFilter(request, response);
				}catch (Exception e) {
					handleException((HttpServletRequest) request, (HttpServletResponse) response, e);
				}
			} else {
				if (request instanceof HttpServletRequest) {
					if (((HttpServletRequest) request).getMethod().equals("POST")) {
						RequestDispatcher dispatcher = request.getRequestDispatcher("/unauthorized/postmethod");
						dispatcher.forward(request, response);
					} else if (((HttpServletRequest) request).getMethod().equals("GET")) {
						RequestDispatcher dispatcher = request.getRequestDispatcher("/unauthorized/getmethod");
						dispatcher.forward(request, response);
					}
				}
			}
		}
	}

		private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
	            throws IOException {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"Exception Occurred\"}");
	    }

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	public static String getRequestIP(HttpServletRequest request) {
		String[] parts = null;
		String IP = "";
		for (String header : IP_HEADERS) {
			String value = request.getHeader(header);
			// System.out.println("header:"+header + "-value:"+value);
			if (value != null && !value.isEmpty()) {
				parts = value.split("\\s*,\\s*");
				IP = parts[0];
				// System.out.println("IP got:"+IP);
			}
		}
		return IP;
	}

	
	
	
 /*
	   @Override
	    public void doFilter(ServletRequest servletRequest, 
	    		ServletResponse servletResponse,
	            FilterChain filterChain
	            ) throws IOException, ServletException {

	        HttpServletRequest  request   = (HttpServletRequest)servletRequest;
	        HttpServletResponse response  = (HttpServletResponse)servletResponse;
	        response.addHeader("Access-Control-Allow-Origin", "*");
	        response.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,PATCH,OPTIONS");
	        response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));     
	        // For HTTP OPTIONS verb reply with ACCEPTED status code, for CORS handshake
	        if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
	            return;
	        }
	        filterChain.doFilter(servletRequest, servletResponse);
	    }
	    @Override
	    public void destroy() {
	        // TODO Auto-generated method stub
	    }
	    @Override
	    public void init(FilterConfig arg0) throws ServletException {
	        // TODO Auto-generated method stub       
	    }
 */
}