package com.comety.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.comety.exception.CometyIOException;
import com.comety.exception.CometyCallOnlyOnceException;

/**
 * AsyncContextをComety用に拡張したクラス
 */
public class CometyAsyncContext implements AsyncContext {
	
	/** レスポンス書き込み完了フラグ */
	protected boolean complete = false;
	
	/** 拡張元となるAsyncContext */
	protected AsyncContext asyncContext;
	
	/** セッション */
	protected CometySession session;
	
	/**
	 * AsyncContextを元としてこのインスタンスを作成する
	 * 
	 * @param session セッション
	 * @param asyncContext 元となるAsyncContext
	 */
	public CometyAsyncContext(CometySession session, AsyncContext asyncContext) {
		this.session = session;
		this.asyncContext = asyncContext;
	}

	@Override
	public ServletRequest getRequest() {
		return asyncContext.getRequest();
	}

	@Override
	public ServletResponse getResponse() {
		return asyncContext.getResponse();
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return asyncContext.hasOriginalRequestAndResponse();
	}

	@Override
	public void dispatch() {
		asyncContext.dispatch();
	}

	@Override
	public void dispatch(String path) {
		asyncContext.dispatch(path);
	}

	@Override
	public void dispatch(ServletContext context, String path) {
		asyncContext.dispatch(context, path);
	}

	@Override
	public void complete() {
		if (complete) {
			throw new CometyCallOnlyOnceException("complete");
		} else {
			complete = true;
			asyncContext.complete();
		}
	}

	@Override
	public void start(Runnable run) {
		asyncContext.start(run);
	}

	@Override
	public void addListener(AsyncListener listener) {
		asyncContext.addListener(listener);
	}

	@Override
	public void addListener(AsyncListener listener,
			ServletRequest servletRequest, ServletResponse servletResponse) {
		asyncContext.addListener(listener, servletRequest, servletResponse);
	}

	@Override
	public <T extends AsyncListener> T createListener(Class<T> clazz)
			throws ServletException {
		return asyncContext.createListener(clazz);
	}

	@Override
	public void setTimeout(long timeout) {
		asyncContext.setTimeout(timeout);
	}

	@Override
	public long getTimeout() {
		return asyncContext.getTimeout();
	}
	
	/**
	 * このレスポンスが完了しているかどうかを取得する
	 * 
	 * @return このレスポンスが完了しているかどうか
	 */
	public boolean isComplete() {
		return complete;
	}
	
	/**
	 * メッセージを送信する
	 * メッセージタイプは[CometService.SEND_MESSAGE]を使用する
	 * 
	 * @param message メッセージ
	 * @throws CometyIOException 入力または出力の例外が発生した場合 
	 */
	public void sendMessage(String message) throws CometyIOException {
		sendMessage(CometyService.MESSAGE, message);
	}
	
	/**
	 * メッセージを送信する。
	 * 
	 * @param status メッセージのタイプ
	 * @param message メッセージ
	 * @throws CometyIOException 入力または出力の例外が発生した場合 
	 */
	public void sendMessage(String status, String message) throws CometyIOException {
		CometyServiceInfo info = session.getCometService().getCometyServiceInfo();
		HttpServletResponse response = (HttpServletResponse) getResponse();
		response.setCharacterEncoding(info.getEncoding());
		response.setContentType(info.getContentType());
		response.addHeader(CometyService.HEADER, status);
		response.setStatus(200);
		try {
			response.getWriter().write(message);
		} catch (IOException e) {
			throw new CometyIOException(e);
		}
		complete();
	}

}
