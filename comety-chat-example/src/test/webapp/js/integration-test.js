var waitFor = function (done, process) {
	if (!done) throw new Error("done arguments is required.");
	if (!process) throw new Error("process arguments is required.");
	
	if (waitFor.stop) return;
		
	var success = process();
	if (success) {
		done();
	} else {
		setTimeout(function () {
			waitFor(done, process);
		});
	}
};

describe("Integration Test", function () {
	
	before(function (done) {
		this.chatA = $("#chatA").contents();
		this.chatB = $("#chatB").contents();
		$.get("jaxrs/util/kill/all").done(function () {
			done();
		});
	});
	
	after(function (){
		waitFor.stop = true;
	});
		
	describe("シナリオA: ユーザーAとユーザーBで対話ができる", function () {
		
		describe("ユーザーAが接続を行う", function () {
			
			it("ユーザーAが名前欄に「ユーザーA」と入力する", function () {
				this.chatA.find("#name").val("ユーザーA");
			});
			
			it("ユーザーAが開始ボタンをクリックする", function () {
				this.chatA.find("#connect").click();
			});
			
			it("ユーザーAが「ChatSystem:ユーザーAが接続しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(0).text();
					return text == "ChatSystem:ユーザーAが接続しました。";
				}.bind(this));
			});
			
			it("ユーザーAが「ChatSystem:あなた以外に0人接続しています。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(1).text();
					return text == "ChatSystem:あなた以外に0人接続しています。";
				}.bind(this));
			});
		});
		
		describe("ユーザーBが接続を行う", function () {
			
			it("ユーザーBが名前欄に「ユーザーB」と入力する", function () {
				this.chatB.find("#name").val("ユーザーB");
			});
			
			it("ユーザーBが開始ボタンをクリックする", function () {
				this.chatB.find("#connect").click();
			});
			
			it("ユーザーBが「ChatSystem:ユーザーBが接続しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(0).text();
					return text == "ChatSystem:ユーザーBが接続しました。";
				}.bind(this));
			});
			
			it("ユーザーBが「ChatSystem:あなた以外に1人接続しています。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(1).text();
					return text == "ChatSystem:あなた以外に1人接続しています。";
				}.bind(this));
			});
			
			it("ユーザーBが「ChatSystem:ユーザーAが接続しています。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(2).text();
					return text == "ChatSystem:ユーザーAが接続しています。";
				}.bind(this));
			});
			
			it("ユーザーAが「ChatSystem:ユーザーBが接続しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(2).text();
					return text == "ChatSystem:ユーザーBが接続しました。";
				}.bind(this));
			});
		});
		
		describe("ユーザーAがメッセージの送信を行う", function () {
			
			it("ユーザーAがメッセージ「こんにちは。ユーザーBさん。」を入力する", function () {
				this.chatA.find("#message").val("こんにちは。ユーザーBさん。");
			});
			
			it("ユーザーAが送信ボタンをクリックする", function () {
				this.chatA.find("#send").click();
			});
			
			it("ユーザーAが「ユーザーA:こんにちは。ユーザーBさん。。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(3).text();
					return text == "ユーザーA:こんにちは。ユーザーBさん。";
				}.bind(this));
			});
			
			it("ユーザーBが「ユーザーA:こんにちは。ユーザーBさん。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(3).text();
					return text == "ユーザーA:こんにちは。ユーザーBさん。";
				}.bind(this));
			});
		});
		
		describe("ユーザーBがメッセージの送信を行う", function () {
			
			it("ユーザーBがメッセージ「よろしく。ユーザーAさん。」を入力する", function () {
				this.chatB.find("#message").val("よろしく。ユーザーAさん。");
			});
			
			it("ユーザーAが送信ボタンをクリックする", function () {
				this.chatB.find("#send").click();
			});
			
			it("ユーザーAが「ユーザーB:よろしく。ユーザーAさん。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(4).text();
					return text == "ユーザーB:よろしく。ユーザーAさん。";
				}.bind(this));
			});
			
			it("ユーザーBが「ユーザーB:よろしく。ユーザーAさん。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(4).text();
					return text == "ユーザーB:よろしく。ユーザーAさん。";
				}.bind(this));
			});
			
		});
		
		describe("ユーザーAが切断を行う", function () {
			
			it("ユーザーAが切断ボタンをクリックする", function () {
				this.chatA.find("#close").click();
			});
			
			it("ユーザーAが「ChatSystem:ユーザーAが切断しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(5).text();
					return text == "ChatSystem:ユーザーAが切断しました。";
				}.bind(this));
			});
			
			it("ユーザーBが「ChatSystem:ユーザーAが切断しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(5).text();
					return text == "ChatSystem:ユーザーAが切断しました。";
				}.bind(this));
			});
			
		});
		
		describe("ユーザーBが切断を行う", function () {
			
			it("ユーザーBが切断ボタンをクリックする", function () {
				this.chatB.find("#close").click();
			});
			
			it("ユーザーBが「ChatSystem:ユーザーBが切断しました。」メッセージを受け取る", function (done) {
				waitFor(done, function () {
					var text = this.chatB.find("#result").children().eq(6).text();
					return text == "ChatSystem:ユーザーBが切断しました。";
				}.bind(this));
			});
			
			it("ユーザーAは次のメッセージを受け取らない", function (done) {
				waitFor(done, function () {
					var text = this.chatA.find("#result").children().eq(6).text();
					return text == "";
				}.bind(this));
			});
			
		});
		
	});
			
	
});