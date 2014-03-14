package com.comety.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.comety.exception.CometyException;

/**
 * コメットサービスのデフォルトの例外マッパー
 */
abstract public class CometyExceptionMapper implements ExceptionMapper<CometyException> {

	@Override
	public Response toResponse(CometyException exception) {
		return Response
				.status(500)
				.type(MediaType.TEXT_PLAIN)
				.entity(exception.getMessage())
				.build();
	}

}
