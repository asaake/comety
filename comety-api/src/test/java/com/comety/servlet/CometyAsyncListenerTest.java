package com.comety.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

public class CometyAsyncListenerTest {
	
	@Mocked
	@Injectable
	CometySession session;
	
	@Mocked
	AsyncEvent event;
	
	@Tested
	CometyAsyncListener listerner;
	
	@Test
	public void セッションが開かれていてonCompleteを呼び出した場合にstopPollingが呼び出されることを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = true;
		}};
		
		listerner.onComplete(event);
		
		new Verifications() {{
			session.stopPolling(); times = 1;
		}};
	}
	
	@Test
	public void セッションが閉じられていてonCompleteを呼び出した場合にstopPollingが呼び出されないことを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = false;
		}};
		
		listerner.onComplete(event);
		
		new Verifications() {{
			session.stopPolling(); times = 0;
		}};
	}
	
	@Test
	public void セッションが開かれていてonTimeoutを呼び出した場合にタイムアウトメッセージが送信されることを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = true;
		}};
		
		listerner.onTimeout(event);
		
		new Verifications() {{
			session.sendTimeoutMessage(""); times = 1;
		}};
	}
	
	@Test
	public void セッションが閉じられていてonTimeoutを呼び出した場合にsendTimeoutMessageが送信されないことを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = false;
		}};
		
		listerner.onTimeout(event);
		
		new Verifications() {{
			session.sendTimeoutMessage(""); times = 0;
		}};
	}
	
	@Test
	public void セッションが開かれていてonErrorを呼び出した場合にstopPollingが呼び出されることを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = true;
		}};
		
		listerner.onError(event);
		
		new Verifications() {{
			session.stopPolling(); times = 1;
		}};
	}
	
	@Test
	public void セッションが閉じられていてonErrorを呼び出した場合にstopPollingが呼び出されないことを確認する() throws IOException {
		
		new NonStrictExpectations() {{
			session.isOpen(); result = false;
		}};
		
		listerner.onError(event);
		
		new Verifications() {{
			session.stopPolling(); times = 0;
		}};
	}

}
