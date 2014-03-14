package com.comety.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.comety.exception.CometySessionNotFoundException;

/**
 * コメットのセッションクラス
 */
public class CometySession {
	
	/** ロガー */
	protected static final Log log = LogFactory.getLog(CometySession.class);
	
	/** セッション格納 */
	protected static final Map<String, CometySession> sessions = new HashMap<String, CometySession>();
	
	/**
	 * セッションを作成する
	 * 
	 * @param sessionId セッションID
	 * @throws Exception 例外
	 */
	public static CometySession createSession(CometyService cometService, String sessionId, Map<String, List<String>> parameters) {
		CometySession session = new CometySession(cometService, sessionId, parameters);
		sessions.put(sessionId, session);
		log.debug(
			"create sessionId: " + sessionId
			+ " : sessions: " + CometySession.getSessions().keySet().toString()
		);
		return session;
	}
	
	/**
	 * セッションを削除する
	 * @param session セッション
	 */
	public static void removeSession(CometySession session) {
		removeSession(session.getSessionId());
	}
	
	/**
	 * セッションを削除する
	 * @param sessionId セッションID
	 */
	public static void removeSession(String sessionId) {
		sessions.remove(sessionId);
		log.debug(
			"remove sessionId: " + sessionId
			+ " : sessions: " + CometySession.getSessions().keySet().toString()
		);
	}
	
	/**
	 * セッションを取得する
	 * 存在しない場合はCometSessionNotFoundExceptionを発生させる
	 * 
	 * @param sessionId セッションID
	 * @return コメットセッションを取得する
	 * @throws CometySessionNotFoundException sessionIdが存在しない場合
	 */
	public static CometySession getSession(String sessionId) {
		CometySession session = sessions.get(sessionId);
		if (session == null) {
			throw new CometySessionNotFoundException(sessionId);
		}
		return session;
	}
	
	/**
	 * すべてのセッションを取得する
	 * @return すべてのセッション
	 */
	public static Map<String, CometySession> getSessions() {
		return new HashMap<String, CometySession>(sessions);
	}
	
	/** コメットサービス */
	protected CometyService cometService;
	
	/** セッションID */
	protected String sessionId;
	
	/** パラメータ一覧 */
	protected Map<String, List<String>> parameters;
	
	/** コメット非同期コンテキスト */
	protected CometyAsyncContext asyncContext;
	
	/** コメットポーリングを監視するクラス */
	protected CometyPollingWaitWatcher pollingWaitWatcher;
	
	/** メッセージをためるキュー */
	protected Queue<CometyMessage> queue = new LinkedList<CometyMessage>();
	
	protected boolean isOpen = false;
	
	/**
	 * コメットのセッションを作成します
	 * 
	 * @param cometServiceInfo コメットサービスの設定情報
	 * @param sessionId セッションID
	 */
	public CometySession(CometyService cometService, String sessionId, Map<String, List<String>> parameters) {
		this.cometService = cometService;
		this.sessionId = sessionId;
		this.parameters = parameters;
		this.isOpen = true;
	}
	
	/**
	 * コメットサービスを取得する
	 * 
	 * @return コメットサービス
	 */
	public CometyService getCometService() {
		return cometService;
	}
	
	/**
	 * セッションIDを取得する
	 * 
	 * @return セッションID
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * パラメータを取得する
	 * 
	 * @return パラメータ
	 */
	public String getParameter(String name) {
		if (parameters.containsKey(name)) {
			return parameters.get(name).get(0);
		}
		return null;
	}
	
	/**
	 * パラメータの一覧を取得する
	 * 
	 * @return パラメータの一覧
	 */
	public Map<String, List<String>> getParameters() {
		return parameters;
	}
	
	/**
	 * このセッションが開かれているかどうかを取得する
	 * 
	 * @return このセッションが開かれているかどうか
	 */
	public boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * このセッションを閉じます
	 */
	public void close() {
		isOpen = false;
		CometySession.removeSession(sessionId);
	}
	
	/**
	 * このセッションでポーリングが開かれているかどうかを取得する
	 * 
	 * @return このセッションが開かれているかどうか
	 */
	public boolean isOpenPolling() {
		if (asyncContext != null && !asyncContext.isComplete()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * ポーリングを開始する
	 * 
	 * @param request リクエスト
	 */
	public synchronized void startPolling(HttpServletRequest request) {
		CometyAsyncContext asyncContext = new CometyAsyncContext(this, request.startAsync());
		asyncContext.setTimeout(cometService.getCometyServiceInfo().getTimeout());
		asyncContext.addListener(new CometyAsyncListener(this));
		this.asyncContext = asyncContext;
		stopPollingWaitWatcher();
		deriver();
	}
	
	/**
	 * ポーリングを停止する
	 */
	public synchronized void stopPolling() {
		this.asyncContext = null;
		startPollingWaitWatcher();
	}
	
	/**
	 * ポーリング待ち状態を監視する
	 */
	public void startPollingWaitWatcher() {
		pollingWaitWatcher = new CometyPollingWaitWatcher(this);
		pollingWaitWatcher.watch();
	}
	
	/**
	 * ポーリング待ち状態の監視を解除する
	 */
	public void stopPollingWaitWatcher() {
		if (pollingWaitWatcher != null) {
			pollingWaitWatcher.unwatch();
		}
		pollingWaitWatcher = null;
	}
	
	/**
	 * オープンメッセージを送信する
	 * 
	 * @param message メッセージ
	 */
	public void sendOpenMessage(String message) {
		sendMessage(CometyService.OPEN, message);
	}
	
	/**
	 * クローズメッセージを送信する
	 * 
	 * @param message メッセージ
	 */
	public void sendCloseMessage(String message) {
		sendMessage(CometyService.CLOSE, message);
	}
	
	/**
	 * タイムアウトメッセージを送信する
	 */
	public void sendTimeoutMessage(String message) {
		sendMessage(CometyService.TIMEOUT, message);
	}
	
	/**
	 * メッセージを送信する
	 * 
	 * @param message メッセージ
	 */
	public void sendMessage(String message) {
		sendMessage(CometyService.MESSAGE, message);
	}
	
	/**
	 * メッセージを送信する
	 * @param status ステータス
	 * @param message メッセージ
	 */
	public void sendMessage(String status, String message) {
		queue.add(new CometyMessage(status, message));
		deriver();
	}
	
	/**
	 * キューにメッセージがある場合はメッセージをレスポンスに書き出す
	 */
	public void deriver() {
		synchronized (this) {
			if (isOpenPolling() && !queue.isEmpty()) {
				CometyMessage message = queue.poll();
				asyncContext.sendMessage(message.getStatus(), message.getMessage());
				if (CometyService.CLOSE.equals(message.getStatus())) {
					this.close();
				}
				if (!CometyService.TIMEOUT.equals(message.getStatus())) {
					log.debug("send queue message:" + message.getStatus() + ":" + message.getMessage());
					log.debug("wait queue size: " + queue.size());
				}
			}
		}
	}

}
