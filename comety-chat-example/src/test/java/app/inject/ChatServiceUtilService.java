package app.inject;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import app.ChatService;

import com.comety.servlet.CometySession;

@Path("/util")
public class ChatServiceUtilService {
	
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
	
	@GET
	@Path("/kill/{sessionId}")
	public String kill(@PathParam("sessionId") String sessionId) {
		ChatService chatService = new ChatService();
		chatService.close(sessionId, true);
		return "kill: " + sessionId;
	}

}
