package com.comety.servlet;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

public class CometyServiceTest {
	
	@Tested
	CometyService service;
	
	@Test
	public void デフォルトのサービス設定が作成されていることを確認する() {
		CometyServiceInfo expectInfo = service.createCometyServiceInfo();
		CometyServiceInfo actualInfo = service.getCometyServiceInfo();
		assertEquals(expectInfo.getContentType(), actualInfo.getContentType());
		assertEquals(expectInfo.getEncoding(), actualInfo.getEncoding());
		assertEquals(expectInfo.getTimeout(), actualInfo.getTimeout());
	}
	
	@Test
	public void デフォルトのセッションID作成メソッドがセッションIDを連番で作成することを確認する() {
		Integer sessionId = CometyService.seqSessionId;
		assertEquals((sessionId++).toString(), service.createSessionId());
		assertEquals((sessionId++).toString(), service.createSessionId());
	}
	
	@Test
	public void connectを呼び出すとセッションを作成することを確認する() {
		
		String sessionId = service.connect(new MultivaluedHashMap<String, String>());
		CometySession session = CometySession.getSession(sessionId);
		assertEquals(sessionId, session.getSessionId());
	}
	
	@Test
	public void openメソッドを呼び出すとconnectで作成したセッションを使用してonOpenを呼び出すことを確認する() {
		
		String sessionId = service.connect(new MultivaluedHashMap<String, String>());
		service.open(sessionId);
		
		final CometySession session = CometySession.getSession(sessionId);
		assertEquals(sessionId, session.getSessionId());
		new Verifications() {{
			service.onOpen(session);
		}};
	}
	
	@Test
	public void closeメソッドを呼び出すとconnectで作成したセッションを使用してonCloseを呼び出しクローズメッセージを送信することを確認する() {
		
		String sessionId = service.connect(new MultivaluedHashMap<String, String>());
		final CometySession session = CometySession.getSession(sessionId);
		service.close(sessionId, false);
		
		CometyMessage message = session.queue.poll();
		assertEquals(CometyService.CLOSE, message.getStatus());
		new Verifications() {{
			service.onClose(session, false);
		}};
	}
	
	@Test
	public void sendMessageメソッドを呼び出すと指定したセッションでonMessageを呼び出すことを確認する() {
		
		String sessionId = service.connect(new MultivaluedHashMap<String, String>());
		final CometySession session = CometySession.getSession(sessionId);
		final String message = "test-message";
		service.sendMessage(sessionId, message);
		
		new Verifications() {{
			service.onMessage(session, message);
		}};
	}
	
	@Test
	public void pollingメソッドを呼び出すとセッションのstartPollingを呼び出すことを確認する(@Mocked HttpServletRequest request) {
		new MockUp<CometySession>() {
			@Mock(invocations=1)
			public void startPolling(HttpServletRequest request) {
			}
		};
		
		String sessionId = service.connect(new MultivaluedHashMap<String, String>());
		service.polling(request, sessionId);
	}

}
