package app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Test;

import com.comety.exception.CometyInValidConnectParametersException;
import com.comety.servlet.CometySession;

public class ChatServiceTest {

    @Tested
    ChatService chatService;

    @Test(expected = CometyInValidConnectParametersException.class)
    public void 接続必須パラメータチェックで必須パラメータがない場合に例外が発生する() {
        Map<String, List<String>> parameters = new HashMap<>();
        chatService.validateConnectParameters(parameters);
    }

    @Test
    public void 接続必須パラメータチェックでに必須パラメータがある場合はなにもしない() {
        Map<String, List<String>> parameters = new HashMap<>();
        List<String> param = new ArrayList<>();
        param.add("test");
        parameters.put("name", param);
        chatService.validateConnectParameters(parameters);
    }

    @Test
    public void 管理者メッセージが作成できる() {
        String message = "testMessage";
        Map<String, String> data = chatService.createMessage("testMessage");
        assertEquals(ChatService.SYSTEM_SESSION_ID, data.get("sessionId"));
        assertEquals(ChatService.SYSTEM_NAME, data.get("name"));
        assertEquals(message, data.get("message"));
    }

    @Test
    public void ユーザーメッセージが作成できる(final @Mocked CometySession session) {

        final String sessionId = "1";
        final String name = "testuser";
        final String message = "testMessage";

        new NonStrictExpectations() {{
            session.getSessionId(); result = sessionId;
            session.getParameter("name"); result = name;
        }};

        Map<String, String> data = chatService.createMessage(session, message);
        assertEquals(sessionId, data.get("sessionId"));
        assertEquals(name, data.get("name"));
        assertEquals(message, data.get("message"));
    }

    @Test
    public void 接続ユーザー全員にメッセージを送信できる() {

        // モック作成
        final List<String> sendedSessionIds = new ArrayList<>();
        new MockUp<CometySession>() {
            @Mock
            public void sendMessage(Invocation invocation, String message) {
                CometySession session = invocation.getInvokedInstance();
                if (sendedSessionIds.contains(session.getSessionId())) {
                    fail("２重送信されている");
                } else {
                    sendedSessionIds.add(session.getSessionId());
                }
            }
        };

        // セッション作成
        String[] sessionIds = new String[]{"1", "2", "3", "4", "5"};
        Map<String, List<String>> parameters = new HashMap<>();
        for (String sessionId : sessionIds) {
            CometySession.createSession(chatService, sessionId, parameters);
        }

        // メッセージ送信
        Map<String, String> data = chatService.createMessage("testMessage");
        chatService.sendBloadCastMessage(data);

        // 検証
        for (String sessionId : sessionIds) {
            assertTrue(sendedSessionIds.contains(sessionId));
        }

    }

}
