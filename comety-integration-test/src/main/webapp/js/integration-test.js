describe("Integration Test", function () {
	
	before(function () {
		this.delay = 10;
	});
	
	after(function (){
		delete this.delay;
	});
		
	describe("シナリオA: ユーザーAから自分にメッセージを送信する", function () {
		before(function () {
			var crossDomain = window.Testem != null;
			this.cometyService = new CometyService({
				"rootPath": "http://localhost:8080/comety-integration-test/jaxrs",
				"crossDomain": crossDomain
			});
			
			this.calledOnOpen = false;
			this.cometyService.onOpen = function () {
				this.calledOnOpen = true;
			}.bind(this);
			
			this.onMessages = [];
			this.cometyService.onMessage = function (message) {
				this.onMessages.push(message);
			}.bind(this);
			
			this.calledOnClose = false;
			this.cometyService.onClose = function () {
				this.calledOnClose = true;
			}.bind(this);
		});
		
		after(function () {
			delete this.cometyService;
			delete this.calledOnOpen;
			delete this.onMessages;
			delete this.calledOnClose;
		});
		
		it("ユーザーAが接続を行う", function (done) {
			this.cometyService.connect({name: "A"}).done(function (sessionId) {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAがオープンメッセージを受け取る", function () {
			expect(this.calledOnOpen).to.eql(true);
		});
		
		it("ユーザーAからユーザーAへメッセージを送信する", function (done) {
			this.cometyService.sendMessage(JSON.stringify({
				"toSessionId": this.cometyService.sessionId,
				"message": "hello world"
			})).done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAが送信したメッセージを受け取る", function () {
			expect(this.onMessages[0]).to.eql("hello world");
		});
		
		it("ユーザーAが切断を行う", function (done) {
			this.cometyService.close().done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAがクローズメッセージを受け取る", function () {
			expect(this.calledOnClose).to.eql(true);
		});
	});
	
	describe("シナリオB: ユーザーAとユーザーBがメッセージを送信し合う", function () {
		
		before(function () {
			
			var crossDomain = window.Testem != null;
			
			// ユーザーAの設定
			this.cometyServiceA = new CometyService({
				"rootPath": "http://localhost:8080/comety-integration-test/jaxrs",
				"crossDomain": crossDomain
			});
			
			// onOpen
			this.calledOnOpenA = false;
			this.cometyServiceA.onOpen = function () {
				this.calledOnOpenA = true;
			}.bind(this);
			
			// onMessage
			this.onMessagesA = [];
			this.cometyServiceA.onMessage = function (message) {
				this.onMessagesA.push(message);
			}.bind(this);
			
			// onClose
			this.calledOnCloseA = false;
			this.cometyServiceA.onClose = function () {
				this.calledOnCloseA = true;
			}.bind(this);
			
			// ユーザーBの設定
			this.cometyServiceB = new CometyService({
				"rootPath": "http://localhost:8080/comety-integration-test/jaxrs",
				"crossDomain": crossDomain
			});
			
			// onOpen
			this.calledOnOpenB = false;
			this.cometyServiceB.onOpen = function () {
				this.calledOnOpenB = true;
			}.bind(this);
			
			// onMessage
			this.onMessagesB = [];
			this.cometyServiceB.onMessage = function (message) {
				this.onMessagesB.push(message);
			}.bind(this);
			
			// onClose
			this.calledOnCloseB = false;
			this.cometyServiceB.onClose = function () {
				this.calledOnCloseB = true;
			}.bind(this);
		});
		
		after(function () {
			delete this.cometyServiceA;
			delete this.calledOnOpenA;
			delete this.onMessagesA;
			delete this.calledOnCloseA;
			
			delete this.cometyServiceB;
			delete this.calledOnOpenB;
			delete this.onMessagesB;
			delete this.calledOnCloseB;
		});
		
		it("ユーザーAが接続を行う", function (done) {
			this.cometyServiceA.connect({name: "A"}).done(function (sessionId) {
				expect(sessionId).not.empty();
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAがオープンメッセージを受け取る", function () {
			expect(this.calledOnOpenA).to.eql(true);
		});
		
		it("ユーザーBが接続を行う", function (done) {
			this.cometyServiceB.connect({name: "B"}).done(function (sessionId) {
				expect(sessionId).not.empty();
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーBがオープンメッセージを受け取る", function () {
			expect(this.calledOnOpenB).to.eql(true);
		});
		
		it("ユーザーAからユーザーBにメッセージを送信する", function (done) {
			this.cometyServiceA.sendMessage(JSON.stringify({
				toSessionId: this.cometyServiceB.sessionId,
				message: "fromA to B"
			})).done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーBがメッセージを受け取る", function () {
			expect(this.onMessagesB[0]).to.eql("fromA to B");
		});
		
		it("ユーザーBからユーザーAにメッセージを送信する", function (done) {
			this.cometyServiceB.sendMessage(JSON.stringify({
				toSessionId: this.cometyServiceA.sessionId,
				message: "fromB to A"
			})).done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAがメッセージを受け取る", function () {
			expect(this.onMessagesA[0]).to.eql("fromB to A");
		});
		
		
		it("ユーザーAが切断を行う", function (done) {
			this.cometyServiceA.close().done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーAがクローズメッセージを受け取る", function () {
			expect(this.calledOnCloseA).to.eql(true);
		});
		
		it("ユーザーBが切断を行う", function (done) {
			this.cometyServiceB.close().done(function () {
				setTimeout(done, this.delay);
			}.bind(this));
		});
		
		it("ユーザーBがクローズメッセージを受け取る", function () {
			expect(this.calledOnCloseB).to.eql(true);
		});
		
	});
	
});