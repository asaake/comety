package com.comety.servlet;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

public class CometyPollingWaitWatcherTest {
	
	@Mocked
	@Injectable
	CometySession session;
	
	@Tested
	CometyPollingWaitWatcher watcher;
	
	@Test
	public void 監視を開始後タイムアウトの３倍の時間経過後にserviceのcloseが呼び出されることを確認する(final @Mocked CometyService service) throws InterruptedException {
		
		final CometyServiceInfo info = new CometyServiceInfo();
		info.setContentType("testContentType");
		info.setEncoding("testEncoding");
		info.setTimeout(1000);
		
		new NonStrictExpectations() {{
			session.getSessionId(); result = "1";
			session.getCometService(); result = service;
			service.getCometyServiceInfo(); result = info;
		}};
		
		watcher.watch();
		Thread.sleep(info.getTimeout() * 4);
		
		new Verifications() {{
			service.close("1", true); times = 1;
		}};
	}
	
	@Test
	public void 監視を開始後強制中断された場合にserviceのcloseが呼び出されることを確認する(final @Mocked CometyService service) throws InterruptedException {
		
		final CometyServiceInfo info = new CometyServiceInfo();
		info.setContentType("testContentType");
		info.setEncoding("testEncoding");
		info.setTimeout(500);
		
		new NonStrictExpectations() {{
			session.getSessionId(); result = "1";
			session.getCometService(); result = service;
			service.getCometyServiceInfo(); result = info;
		}};
		
		watcher.watch();
		watcher.interrupt();
		Thread.sleep(1);
		
		new Verifications() {{
			service.close("1", true); times = 1;
		}};
	}
	
	@Test
	public void 監視が中断されることを確認する(final @Mocked CometyService service) throws InterruptedException {
		final CometyServiceInfo info = new CometyServiceInfo();
		info.setContentType("testContentType");
		info.setEncoding("testEncoding");
		info.setTimeout(500);
		
		new NonStrictExpectations() {{
			session.getSessionId(); result = "1";
			session.getCometService(); result = service;
			service.getCometyServiceInfo(); result = info;
		}};
		
		watcher.watch();
		watcher.unwatch();
		Thread.sleep(info.getTimeout() * 4);
		
		new Verifications() {{
			service.close("1", false); times = 0;
		}};
	}

}
