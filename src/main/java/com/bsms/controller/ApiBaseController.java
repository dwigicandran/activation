package com.bsms.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bsms.service.base.MbService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Path("/activation")
public class ApiBaseController {

	@Autowired
	private ApplicationContext context;
	
	@Context
    private ContainerRequestContext requestContext;
	
	@Context
    private HttpHeaders header;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@POST @Path("/{serviceName}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public MbApiResp service(@PathParam("serviceName") String serviceName, String jsonRequest) {
		
		MbApiReq request = null;
		MbApiResp response = null;
		
		try {
			request = objectMapper.readValue(jsonRequest, MbApiReq.class);
			MbService service = (MbService) context.getBean(serviceName);
			response = service.process(header, requestContext, request);
		
        } catch (Exception e) {

            System.out.print(e);
        }
		
		return response;
		
	}
	
}
