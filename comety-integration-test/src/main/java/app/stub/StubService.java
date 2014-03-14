package app.stub;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.comety.exception.CometyInValidConnectParametersException;
import com.comety.servlet.CometyService;
import com.comety.servlet.CometySession;

@Path("/")
public class StubService extends CometyService {

	@Override
	public void validateConnectParameters(Map<String, List<String>> parameters)
			throws CometyInValidConnectParametersException {
		List<String> param = parameters.get("name");
		if (param != null && param.get(0) != null && param.get(0).length() > 0) {
			return;
		} else {
			throw new CometyInValidConnectParametersException("name is required.");
		}
	}

	@Override
	public void onOpen(CometySession session) {
	}

	@Override
	public void onClose(CometySession session, boolean isForceClose) {
	}

	@Override
	public void onMessage(CometySession session, String message) {
		Map<String, String> map = readValue(message);
		CometySession toSession = CometySession.getSession(map.get("toSessionId"));
		toSession.sendMessage(map.get("message"));
	}
	
	protected Map<String, String> readValue(String src) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(src, new TypeReference<Map<String, String>>(){});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String writeValueAsString(Object o) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
