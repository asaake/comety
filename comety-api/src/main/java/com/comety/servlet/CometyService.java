package com.comety.servlet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import com.comety.exception.CometyInValidConnectParametersException;

/**
 * Cometのサービスを利用するクラス<br>
 * <br>
 * 独自のサービス設定が必要場場合は、createCometyServiceInfoメソッドをオーバーライドする。<br>
 * 独自のセッションIDを発行する場合は、createSessionIdメソッドをオーバーライドする。<br>
 * <br>
 * CometyService.pollingSessionsに接続中の全てのセッションが保持されています。<br>
 * <br>
 * ポーリング中のリクエストが、どの場面でレスポンスを返したか<br>
 * comety-status:<br>
 *   send: メッセージの送信<br>
 *   timeout: ポーリングのタイムアウト<br>
 *   open: Cometの開始<br>
 *   close: Cometの終了<br>
 * <br>
 * ライフサイクル<br>
 *   1. /connect:　セッションIdを発行します<br>
 *   2. /open: onOpenメソッドを実行します<br>
 *   3. /polling: ポーリングを開始し、onPollingメソッドを呼び出します。その後待機します<br>
 *   4. /message: onMessageメソッドを実行します<br>
 *   5. /close: onCloseメソッドを実行します、その後セッションIdを閉じます<br>
 * <br>
 * 例<br>
 * <pre>
@Path("/")
public class ExampleService extends CometyService {

	@Override
	public void onOpen(CometySession session) throws Exception {
		session.sendMessage("onOpen");
	}

	@Override
	public void onClose(CometySession session) throws Exception {
		session.sendMessage("onClose");
	}

	@Override
	public void onMessage(CometyAsyncContext asyncContext, String message) throws Exception {
		Map<String, CometySessions> sessions = CometySession.getSessions();
		synchronized(sessions) {
			for (CometySession session : sessions.values()) {
				session.sendMessage(message);
			}
		}
	}
	
}
 </pre>
 */
public abstract class CometyService {

    /**
     * セッションIDのシーケンス
     */
    protected static int seqSessionId = 1;

    /**
     * メッセージステータス
     */
    public static final String HEADER = "comety-status";

    /**
     * メッセージステータス: メッセージ送信
     */
    public static final String MESSAGE = "message";

    /**
     * メッセージステータス: タイムアウト
     */
    public static final String TIMEOUT = "timeout";

    /**
     * メッセージステータス: オープン
     */
    public static final String OPEN = "open";

    /**
     * メッセージステータス : クローズ
     */
    public static final String CLOSE = "close";

    /**
     * メッセージのデフォルトエンコーディング
     */
    public static final String DEFAULT_ENCODING = "utf-8";

    /**
     * メッセージのデフォルトコンテンツタイプ
     */
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    /**
     * メッセージのデフォルトタイムアウト
     */
    public static final long DEFAULT_TIMEOUT = 5000;

    /**
     * コメットサービスの設定
     */
    protected static CometyServiceInfo cometyServiceInfo;

    /**
     * このクラスをインスタンス化する
     */
    public CometyService() {
        if (cometyServiceInfo == null) {
            cometyServiceInfo = createCometyServiceInfo();
        }
    }

    /**
     * コメットサービスの設定を取得します
     *
     * @return コメットサービスの設定
     */
    public CometyServiceInfo getCometyServiceInfo() {
        return cometyServiceInfo;
    }

    /**
     * コメットサービスの設定を作成します 独自の設定が必要な場合はこのメソッドをオーバーライドする
     * 
     * @return コメットサービスの設定
     */
    public CometyServiceInfo createCometyServiceInfo() {
        CometyServiceInfo info = new CometyServiceInfo();
        info.setEncoding(DEFAULT_ENCODING);
        info.setContentType(DEFAULT_CONTENT_TYPE);
        info.setTimeout(DEFAULT_TIMEOUT);
        return info;
    }

    /**
     * コメットサービスのセッションIDを作成します 独自のセッションIDを発行する場合はこのメソッドをオーバライドする
     *
     * @return セッションID
     */
    public synchronized String createSessionId() {
        return String.valueOf(seqSessionId++);
    }

    /**
     * 接続時のパラメータを検証する
     *
     * @param parameters パラメータの一覧
     * @throws CometyInValidConnectParametersException 検証が失敗した場合の例外
     */
    abstract public void validateConnectParameters(Map<String, List<String>> parameters) throws CometyInValidConnectParametersException;

    /**
     * オープン時に呼び出されるイベントハンドラ
     *
     * @param session セッション
     */
    abstract public void onOpen(CometySession session);

    /**
     * クローズ時に呼び出されるイベントハンドラ ※このメソッド終了後にセッションは閉じられます
     *
     * @param session セッション
     * @param isForceClose 強制切断かどうか
     */
    abstract public void onClose(CometySession session, boolean isForceClose);

    /**
     * メッセージを受け取った時にに呼び出されるイベントハンドラ
     *
     * @param session セッション
     * @param message メッセージ
     */
    abstract public void onMessage(CometySession session, String message);

    /**
     * 初回の接続時にセッションIDを発行するメソッド
     *
     * @param parameters フォームパラメータ
     * @return セッションID
     */
    @POST
    @Path("/connect")
    public String connect(MultivaluedMap<String, String> parameters) {
        validateConnectParameters(parameters);
        String sessionId = createSessionId();
        CometySession session = CometySession.createSession(this, sessionId, parameters);
        return session.getSessionId();
    }

    /**
     * オープン時に呼び出されるメソッド
     *
     * @param sessionId セッションID
     */
    @POST
    @Path("/open")
    public void open(@FormParam("sessionId") String sessionId) {
        CometySession session = CometySession.getSession(sessionId);
        session.sendOpenMessage(session.getSessionId());
        onOpen(session);
    }

    /**
     * クローズ時に呼び出されるメソッド
     *
     * @param sessionId セッションID
     */
    @POST
    @Path("/close")
    public void close(@FormParam("sessionId") String sessionId) {
        close(sessionId, false);
    }

    /**
     * クローズ時に呼び出されるメソッド
     *
     * @param sessionId セッションID
     * @param isForceClose 強制切断かどうか
     */
    public void close(@FormParam("sessionId") String sessionId, boolean isForceClose) {
        CometySession session = CometySession.getSession(sessionId);
        onClose(session, isForceClose);
        if (isForceClose) {
            session.close();
        } else {
            session.sendCloseMessage(session.getSessionId());
        }
    }

    /**
     * クライアントからメッセージを送信するために呼び出されるメソッド
     *
     * @param sessionId セッションID
     * @param message メッセージ
     */
    @POST
    @Path("/message")
    public void sendMessage(@FormParam("sessionId") String sessionId,
            @FormParam("message") String message) {
        CometySession session = CometySession.getSession(sessionId);
        onMessage(session, message);
    }

    /**
     * ポーリングを維持するメソッド
     *
     * @param request　リクエスト
     * @param sessionId セッションID
     */
    @POST
    @Path("/polling")
    public void polling(@Context HttpServletRequest request, @FormParam("sessionId") String sessionId) {
        final CometySession session = CometySession.getSession(sessionId);
        session.startPolling(request);
    }

}
