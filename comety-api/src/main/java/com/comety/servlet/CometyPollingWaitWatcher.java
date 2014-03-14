package com.comety.servlet;

/**
 * ポーリングの処理で通信完了後、通信再開までセッションを維持または破棄するクラス
 */
public class CometyPollingWaitWatcher extends Thread {
	
	/** セッション */
	protected CometySession session;
	
	/** 監視中かどうか */
	protected boolean isWatch;
	
	/**
	 * 監視するセッションを指定してこのクラスをインスタンス化する
	 * 
	 * @param session セッション
	 */
	public CometyPollingWaitWatcher(CometySession session) {
		this.session = session;
	}
	
	/**
	 * 監視を終了する
	 */
	public void unwatch() {
		isWatch = false;
	}
	
	/**
	 * 監視を開始する
	 */
	public void watch() {
		isWatch = true;
		start();
	}

	@Override
	public void run() {
		CometyService service = session.getCometService();
		CometyServiceInfo info = service.getCometyServiceInfo();
		try {
			Thread.sleep(info.getTimeout() * 3);
			if (isWatch) {
				isWatch = false;
				service.close(session.getSessionId(), true);
			}
		} catch (InterruptedException e) {
			isWatch = false;
			service.close(session.getSessionId(), true);
		}
	}
	
}
