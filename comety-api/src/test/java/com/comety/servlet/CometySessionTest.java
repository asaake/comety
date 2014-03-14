package com.comety.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

import com.comety.exception.CometySessionNotFoundException;

public class CometySessionTest {
	
	@Tested
	CometySession session;
	
	@Mocked
	CometyService cometyService;
	
	String sessionId = "1";
	
	@Mocked
	HttpServletRequest request;
	
	@Mocked
	HttpServletResponse response;
	
	@Mocked
	AsyncContext asyncContext;
	
	@Before
	public void setup() {
		session = new CometySession(cometyService, sessionId, new HashMap<String, List<String>>());
	}
	
	public CometyServiceInfo getInfo() {
		CometyServiceInfo info = new CometyServiceInfo();
		info.setContentType("testContentType");
		info.setEncoding("testEncoding");
		info.setTimeout(1000);
		return info;
	}
	
	@Test
	public void セッションが作成されることを確認する() {
		CometySession session = CometySession.createSession(cometyService, sessionId, new HashMap<String, List<String>>());
		Map<String, CometySession> sessions = CometySession.getSessions();
		assertNotNull(sessions.get(session.getSessionId()));
		assertTrue(session.isOpen());
	}
	
	@Test
	public void セッションが削除されることを確認する() {
		CometySession session = CometySession.createSession(cometyService, sessionId, new HashMap<String, List<String>>());
		CometySession.removeSession(session);
		Map<String, CometySession> sessions = CometySession.getSessions();
		assertNull(sessions.get(session.getSessionId()));
	}
	
	@Test
	public void セッションが取得できることを確認する() {
		CometySession session = CometySession.createSession(cometyService, sessionId, new HashMap<String, List<String>>());
		assertEquals(session.getSessionId(), CometySession.getSession(sessionId).getSessionId());
	}
	
	@Test(expected=CometySessionNotFoundException.class)
	public void セッションがない場合は例外が発生することを確認する() {
		CometySession.getSession(sessionId);
	}
	
	@Test
	public void ポーリングを開始できることを確認する() {
		
		new NonStrictExpectations() {{
			request.startAsync(); result = asyncContext;
			cometyService.getCometyServiceInfo(); result = getInfo();
		}};
		
		assertFalse(session.isOpenPolling());
		session.startPolling(request);
		assertTrue(session.isOpenPolling());
		
	}
	
	@Test
	public void メッセージを送信後ポーリングを開始すると保存されているメッセージを配信することを確認する(final @Mocked PrintWriter writer) throws IOException {
		
		final String message = "test-message";
		new NonStrictExpectations() {{
			request.startAsync(); result = asyncContext;
			cometyService.getCometyServiceInfo(); result = getInfo();
			asyncContext.getResponse(); result = response;
			response.getWriter(); result = writer;
		}};
		
		assertFalse(session.isOpenPolling());
		session.sendMessage(message);
		session.startPolling(request);
		assertFalse(session.isOpenPolling());
	}
	
	@Test
	public void ポーリング停止の監視が開始されることを確認する() throws InterruptedException {
		new NonStrictExpectations() {{
			request.startAsync(); result = asyncContext;
			cometyService.getCometyServiceInfo(); result = getInfo();
		}};
		
		session.stopPolling();
		Thread.sleep(1);
		CometyPollingWaitWatcher pollingWaitWatcher = session.pollingWaitWatcher;
		assertNotNull(pollingWaitWatcher);
		assertTrue(pollingWaitWatcher.isWatch);
	}
	
	@Test
	public void ポーリング停止後ポーリングを再開するとポーリング停止の監視が解除されることを確認する() throws InterruptedException {
		new NonStrictExpectations() {{
			request.startAsync(); result = asyncContext;
			cometyService.getCometyServiceInfo(); result = getInfo();
		}};
		
		session.stopPolling();
		Thread.sleep(1);
		CometyPollingWaitWatcher pollingWaitWatcher = session.pollingWaitWatcher;
		session.startPolling(request);
		assertNull(session.pollingWaitWatcher);
		assertFalse(pollingWaitWatcher.isWatch);
	}
	
	@Test
	public void メッセージを送信するとキューにメッセージが格納されることを確認する() {
		
		String message = "test-message";
		session.sendMessage(message);
		assertEquals(1, session.queue.size());
		
		CometyMessage cometMessage = session.queue.poll();
		assertEquals(CometyService.MESSAGE, cometMessage.getStatus());
		assertEquals(message, cometMessage.getMessage());
	}
	
	@Test
	public void クローズメッセージを送信した後にセッションが閉じられ削除されることを確認する(final @Mocked PrintWriter writer) throws IOException {
		new NonStrictExpectations() {{
			request.startAsync(); result = asyncContext;
			cometyService.getCometyServiceInfo(); result = getInfo();
			asyncContext.getResponse(); result = response;
			response.getWriter(); result = writer;
		}};
		
		session.sendCloseMessage("");
		session.startPolling(request);
		assertFalse(session.isOpen());
		assertNull(CometySession.sessions.get(session.getSessionId()));
		
	}

}
