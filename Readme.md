comety
======

CometをWebSocketのように扱うライブリです。  
Arquillianを使用したサンプルプロジェクトとしても有用です。


プロジェクト一覧
----
|プロジェクト名|説明|
|-------------------|----|
|comety-bom             |cometyの親pom|
|comety-resources       |cometyで使用しているリソースを集めたもの|
|comety-api             |cometyのAPIを提供するプロジェクト|
|comety-integration-test|cometyの統合テストをするプロジェクト|
|comety-chat-example    |cometyを使用した参考プロジェクト|


コード例
----
```javascript
var cometyService = new CometyService({
  rootPath: "/myservice/jaxrs"
});

cometyService.onOpen = function (data, status, xhr) {
  console.log("onOpen");
};
cometyService.onClose = function (data, status, xhr) {
  console.log("onClose");
};
cometyService.onMessage = function (data, status, xhr) {
  console.log("onMessage: " + data);
}
 
cometyService.connect(); // サービスを開始する
cometyService.sendMessage("Hello World."); // メッセージを送信する
cometyService.close(); // サービスを終了する
```

```java
@Path("/")
public class ExampleService extends CometyService {

	@Override
	public void onOpen(CometySession session) throws Exception {
		session.sendMessage("onOpen");
	}

	@Override
	public void onClose(CometySession session, boolean isForceClose) throws Exception {
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
```







