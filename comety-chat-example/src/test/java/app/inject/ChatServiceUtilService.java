package app.inject;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import app.ChatService;

import com.comety.servlet.CometySession;

/**
 * テスト時専用のユーティリティクラス
 */
@Path("/util")
public class ChatServiceUtilService {

    /**
     * すべてのセッションを削除する
     *
     * @return 削除したセッションIDの配列
     */
    @GET
    @Path("/kill/all")
    public String killAll() {
        ChatService chatService = new ChatService();
        Set<String> sessionIds = CometySession.getSessions().keySet();
        for (String sessionId : sessionIds) {
            chatService.close(sessionId, true);
        }
        return sessionIds.toString();
    }

    /**
     * セッションを削除する
     *
     * @param sessionId
     * @return 削除したセッションID
     */
    @GET
    @Path("/kill/{sessionId}")
    public String kill(@PathParam("sessionId") String sessionId) {
        ChatService chatService = new ChatService();
        chatService.close(sessionId, true);
        return sessionId;
    }

}
