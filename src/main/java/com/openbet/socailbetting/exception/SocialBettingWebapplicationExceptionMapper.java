package com.openbet.socailbetting.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.openbet.socailbetting.utils.ErrCode;

public class SocialBettingWebapplicationExceptionMapper implements ExceptionMapper<Exception>
{
	private static final Logger log = Logger.getLogger(SocialBettingWebapplicationExceptionMapper.class);
	@Override
	public Response toResponse(Exception ex) {
		StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
		String err = sw.toString();
		log.info("WebApplicationException:", ex);
		
        String errCode = ErrCode.INTERNAL_SERVER_ERROR.value();
        String errMsg = ex.getMessage();
        ExceptionBody body = new ExceptionBody();
        body.setSuccess(false);
        body.setErrorCode(errCode);
        if(StringUtils.isBlank(errMsg))
        {
        	errMsg="Not Supported Operation, make sure HTTP Method: POST and conent type: application/json.";
        }
        body.setErrorMsg(errMsg);
        ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.type(MediaType.APPLICATION_JSON_TYPE);
        rb.entity(body);    
        Response r = rb.build();
        return r;
	}

}
