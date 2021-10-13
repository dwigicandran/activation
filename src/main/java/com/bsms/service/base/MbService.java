package com.bsms.service.base;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

public interface MbService {

	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception;
	
}
