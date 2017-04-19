package com.abc.dubbo.demo.api.extension;

import com.alibaba.dubbo.rpc.RpcContext;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author lishen
 */
public class CustomExceptionMapper implements ExceptionMapper<NotFoundException> {

    public Response toResponse(NotFoundException e) {
        System.out.println("Exception mapper successfully got an exception: " + e + ":" + e.getMessage());
        System.out.println("Client IP is " + RpcContext.getContext().getRemoteAddressString());
        return Response.status(Response.Status.NOT_FOUND).entity("Oops! the requested resource is not found!").type("text/plain").build();
    }
}
