describe("CometyServiceTest", function() {

  beforeEach(function() {
    this.server = sinon.fakeServer.create();
    this.cometyService = new CometyService({
      rootPath: "jaxrs"
    });
  });

  afterEach(function() {
    this.server.restore();
  });

  it("CometyService作成時にjQueryがない場合はエラー", function() {
    window.jQuery = null;
    expect(CometyService).to.throwException("jQuery is required.");
    window.jQuery = $;
  });

  it("CometyService作成時に設定オブジェクトがない場合はエラー", function() {
    expect(CometyService)
        .to.throwException("options object is required.");
  });

  it("CometyService作成時にrootPathの設定がな場合はエラー", function() {
    expect(CometyService).withArgs({})
        .to.throwException("options.rootPath is requierd.");
  });

  it("クローズの呼び出し時にセッションIDがない場合はエラー", function() {
    expect(this.cometyService.close.bind(this.cometyService))
        .to.throwException("not connect comety service.");
  });

  it("ポーリングの呼び出し時にセッションIDがない場合はエラー", function() {
    expect(this.cometyService.polling.bind(this.cometyService))
        .to.throwException("not connect comety service.");
  });

  it("メッセージ送信の呼び出し時にセッションIDがない場合はエラー", function() {
    expect(this.cometyService.sendMessage.bind(this.cometyService))
        .to.throwException("not connect comety service.");
  });

  it("パスの作成ができる", function() {
    var path = this.cometyService.path("test");
    expect(path).to.eql("jaxrs/test");
  });

  it("サーバーと接続ができる", function() {
    this.server.respondWith(
        "POST",
        "jaxrs/connect",
        [
          200,
          {"Content-Type": "application/json"},
          "1"
        ]
    );
    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json"
          },
          ""
        ]
    );
    this.server.respondWith(
        "POST",
        "jaxrs/open",
        [
          200,
          {"Content-Type": "application/json"},
          ""
        ]
    );
    window.server = this.server;
    this.cometyService.connect();
    this.server.respond();
    this.server.respond();

    var connectRequest = this.server.requests[0];
    expect(connectRequest.status).to.eql(200);
    expect(connectRequest.url).to.eql("jaxrs/connect");

    var pollingRequest = this.server.requests[1];
    expect(pollingRequest.status).to.eql(200);
    expect(pollingRequest.url).to.eql("jaxrs/polling");

    var openRequest = this.server.requests[2];
    expect(openRequest.status).to.eql(200);
    expect(openRequest.url).to.eql("jaxrs/open");

  });

  it("セッションを閉じることができる", function() {
    this.server.respondWith(
        "POST",
        "jaxrs/close",
        [
          200,
          {"Content-Type": "application/json"},
          ""
        ]
    );
    this.cometyService.sessionId = "1";
    this.cometyService.close();
    this.server.respond();
    var request = this.server.requests[0];
    expect(request.status).to.eql(200);
    expect(request.url).to.eql("jaxrs/close");
  });

  it("pollingでオープンメッセージが返却された場合にonOpenが呼び出され、その後pollingを再開することを確認する", function() {

    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json",
            "comety-status": "open"
          },
          JSON.stringify({"message": ""})
        ]
    );

    this.cometyService.sessionId = "1";
    this.cometyService.polling();
    var called = [];
    this.cometyService.onOpen = function() {
      called.push("onOpen");
    };
    this.cometyService.polling = function() {
      called.push("polling");
    };
    this.server.respond();
    expect(called[0]).to.eql("onOpen");
    expect(called[1]).to.eql("polling");
  });

  it("pollingでクローズメッセージが返却された場合にonCloseが呼び出され、その後sessionIdをnullにすることを確認する", function() {

    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json",
            "comety-status": "close"
          },
          JSON.stringify({"message": ""})
        ]
    );

    this.cometyService.sessionId = "1";
    this.cometyService.polling();
    var called = [];
    this.cometyService.onClose = function() {
      called.push("onOpen");
    };
    this.server.respond();
    expect(called[0]).to.eql("onOpen");
    expect(this.cometyService.sessionId).to.eql(null);
  });

  it("pollingでメッセージが返却された場合にonMessageが呼び出され、その後pollingを再開することを確認する", function() {

    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json",
            "comety-status": "message"
          },
          JSON.stringify({"message": ""})
        ]
    );

    this.cometyService.sessionId = "1";
    this.cometyService.polling();
    var called = [];
    this.cometyService.onMessage = function() {
      called.push("onMessage");
    };
    this.cometyService.polling = function() {
      called.push("polling");
    };
    this.server.respond();
    expect(called[0]).to.eql("onMessage");
    expect(called[1]).to.eql("polling");
  });

  it("pollingでタイムアウトが返却された場合にpollingを再開することを確認する", function() {

    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json",
            "comety-status": "timeout"
          },
          JSON.stringify({"message": ""})
        ]
    );

    this.cometyService.sessionId = "1";
    this.cometyService.polling();
    var called = [];
    this.cometyService.polling = function() {
      called.push("polling");
    };
    this.server.respond();
    expect(called[0]).to.eql("polling");
  });

  it("pollingで未知のステータスが返却された場合はコンソールに警告を出力する", function() {

    this.server.respondWith(
        "POST",
        "jaxrs/polling",
        [
          200,
          {
            "Content-Type": "application/json",
            "comety-status": "none"
          },
          JSON.stringify({"message": ""})
        ]
    );

    var capture = [];
    var oldWarn = console.warn;
    console.warn = function(msg) {
      capture.push(msg);
    };

    this.cometyService.sessionId = "1";
    this.cometyService.polling();
    this.server.respond();
    console.warn = oldWarn;
    expect(capture[0]).to.eql("CometyService: [none] is not supoorted status.");
  });

  it("メッセージを送信することができる", function() {
    this.server.respondWith(
        "POST",
        "jaxrs/message",
        [200, {"Content-Type": "application/json"}, ""]
    );

    this.cometyService.sessionId = "1";
    this.cometyService.sendMessage("test-message");
    this.server.respond();
    var request = this.server.requests[0];
    expect(request.status).to.eql(200);
    expect(request.url).to.eql("jaxrs/message");
  });

});