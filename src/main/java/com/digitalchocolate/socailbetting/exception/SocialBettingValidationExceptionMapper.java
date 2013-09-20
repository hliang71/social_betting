package com.digitalchocolate.socailbetting.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.log4j.Logger;

public class SocialBettingValidationExceptionMapper implements ExceptionMapper<SocialBettingValidationException> {
    private static final Logger log = Logger.getLogger(SocialBettingValidationExceptionMapper.class);
    public Response toResponse(SocialBettingValidationException ex) {
    	log.info(new StringBuilder("validation error: error code:").append(ex.getErrCode()).append(" error message:").append(ex.getErrMsg()), ex);
        String errCode = ex.getErrCode();
        String errMsg = ex.getErrMsg();
        ExceptionBody body = new ExceptionBody();
        body.setSuccess(false);
        body.setErrorCode(errCode);
        body.setErrorMsg(errMsg);
        ResponseBuilder rb = Response.status(Response.Status.OK);
        rb.type(MediaType.APPLICATION_JSON_TYPE);
        rb.entity(body);    
        Response r = rb.build();
        return r;
    }
}
