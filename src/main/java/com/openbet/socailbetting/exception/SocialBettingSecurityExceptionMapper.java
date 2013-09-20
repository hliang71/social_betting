package com.openbet.socailbetting.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.log4j.Logger;

public class SocialBettingSecurityExceptionMapper implements ExceptionMapper<SocialBettingSecurityException> {
    private static final Logger log = Logger.getLogger(SocialBettingSecurityExceptionMapper.class);
    public Response toResponse(SocialBettingSecurityException ex) {
    	log.info(new StringBuilder("security exception: error code:").append(ex.getErrCode()).append(" error message:").append(ex.getErrMsg()));

        String errCode = ex.getErrCode();
        String errMsg = ex.getErrMsg();
        ExceptionBody body = new ExceptionBody();
        body.setSuccess(false);
        body.setErrorCode(errCode);
        body.setErrorMsg(errMsg);
        ResponseBuilder rb = Response.status(Response.Status.FORBIDDEN);
        rb.type(MediaType.APPLICATION_JSON_TYPE);
        rb.entity(body);    
        Response r = rb.build();
        return r;
    }
}
