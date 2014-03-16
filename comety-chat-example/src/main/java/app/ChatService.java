package app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.comety.exception.CometyInValidConnectParametersException;
import com.comety.servlet.CometyService;
import com.comety.servlet.CometySession;
import java.io.IOException;

/**
 * チャットシステム
 */
@Path("/")
public class ChatService extends CometyService {

    /**
     * このシステムのセッションID
     */
    public static final String SYSTEM_SESSION_ID = "ChatSystem";

    /**
     * このシステムの名前
     */
    public static final String SYSTEM_NAME = "ChatSystem";

    /**
     * ロガー
     */
    protected static final Log log = LogFactory.getLog(ChatService.class);

    /**
     * JSONマッパー
     */
    protected static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void validateConnectParameters(Map<String, List<String>> parameters)
            throws CometyInValidConnectParametersException {
        List<String> param = parameters.get("name");
        if (param != null && param.get(0) != null && param.get(0).length() > 0) {
            return;
        }
        throw new CometyInValidConnectParametersException("name is required.");

    }

    @Override
    public void onOpen(CometySession session) {
        // 管理者メッセージを全員に送信する
        log.debug("onOpen: " + session.getSessionId());
        Map<String, String> data = createMessage(session.getParameter("name") + "が接続しました。");
        sendBloadCastMessage(data);

        // 接続者リストを自身に送信する
        Map<String, CometySession> sessions = CometySession.getSessions();
        sessions.remove(session.getSessionId());
        sendMessage(session, createMessage("あなた以外に" + sessions.size() + "人接続しています。"));
        for (CometySession connectedSession : sessions.values()) {
            String message = connectedSession.getParameter("name") + "が接続しています。";
            sendMessage(session, createMessage(message));
        }
    }

    @Override
    public void onClose(CometySession session, boolean isForceClose) {
        // 管理者メッセージを全員に送信する
        log.debug("onClose: " + session.getSessionId());
        Map<String, String> data = createMessage(session.getParameter("name") + "が切断しました。");
        sendBloadCastMessage(data);
    }

    @Override
    public void onMessage(CometySession session, String message) {
        // ユーザーのメッセージを全員に送信する
        log.debug("onMessage: " + session.getSessionId());
        Map<String, String> data = createMessage(session, message);
        sendBloadCastMessage(data);
    }

    /**
     * 管理者メッセージを作成する
     *
     * @param message メッセージ
     * @return 送信できる形式のメッセージ
     */
    protected Map<String, String> createMessage(String message) {
        Map<String, String> data = new HashMap<>();
        data.put("sessionId", SYSTEM_SESSION_ID);
        data.put("name", SYSTEM_NAME);
        data.put("message", message);
        return data;
    }

    /**
     * ユーザーのメッセージを作成する
     *
     * @param session セッション
     * @param message メッセージ
     * @return 送信できる形式のメッセージ
     */
    protected Map<String, String> createMessage(CometySession session, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("sessionId", session.getSessionId());
        data.put("name", session.getParameter("name"));
        data.put("message", message);
        return data;
    }

    /**
     * 対象のユーザーにメッセージを送信する
     *
     * @param session セッション
     * @param data データ
     */
    protected void sendMessage(CometySession session, Map<String, String> data) {
        String message = writeValueAsString(data);
        session.sendMessage(message);
    }

    /**
     * 接続ユーザー全員へメッセージを送信する
     *
     * @param data 送信できる形式のメッセージ
     */
    protected void sendBloadCastMessage(Map<String, String> data) {
        String message = writeValueAsString(data);
        for (CometySession session : CometySession.getSessions().values()) {
            session.sendMessage(message);
        }
    }

    /**
     * JSON文字列をオブジェクトにマッピングする
     *
     * @param <E> 変換クラス
     * @param src JSON文字列
     * @param clazz クラス
     * @return オブジェクト
     */
    protected <E> E readValue(String src, Class<E> clazz) {
        try {
            return mapper.readValue(src, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * オブジェクトをJSON文字列に変換する
     *
     * @param o オブジェクト
     * @return JSON文字列
     */
    protected String writeValueAsString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
