<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%
	String context = request.getContextPath();
%>
<title>チャット</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=context%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=context%>/js/comety-service.js"></script>
<script type="text/javascript">  
$(function(){
	
	window.onMessage = function (msg) {
		alert(msg);
	};
	
	// 切断状態のフォーム
	var closedForm = function () {
		$("#connect").removeAttr("disabled");
		$("#name").removeAttr("disabled");
		$("#close").attr("disabled", "disabled");
		$("#send").attr("disabled", "disabled");
		$("#message").attr("disabled", "disabled");
	};
	
	// 接続状態のフォーム
	var connectedForm = function () {
		$("#connect").attr("disabled", "disabled");
		$("#name").attr("disabled", "disabled");
		$("#close").removeAttr("disabled");
		$("#send").removeAttr("disabled");
		$("#message").removeAttr("disabled");
	};
	
	// 色をランダムで返す
	var createRandamColor = function () {
		var color = "";
		color += Math.floor(Math.random() * 255).toString(16);
		color += Math.floor(Math.random() * 255).toString(16);
		color += Math.floor(Math.random() * 255).toString(16);
		return color;
	};
	
	// セッションIDに応じた名前の色を取得する
	var userColors = {};
	var getUserColor = function (sessionId) {
		if (!userColors[sessionId]) {
			userColors[sessionId] = createRandamColor();
		}
		return userColors[sessionId];
	};
	
	// 画面にメッセージを追加する
	var addMessage = function (obj) {
		var ntag = $("<span>").text(obj.name);
		ntag.css("color", "#" + getUserColor(obj.sessionId));
		var stag = $("<span>").text(":");
		stag.css("color", "glay");
		var mtag = $("<span>").text(obj.message);
		var t = $("<div>")
			.append(ntag)
			.append(stag)
			.append(mtag);
		$("#result").append(t);
	};
	
	var cometyService = null;
	
	// メッセージ
	$("#message").keypress(function (e) {
		// Enterを押した場合は送信ボタンをクリックしたことにする
		if (e.keyCode == 13) {
			$("#send").click();
		}
	});
	
	// メッセージ送信
	$("#send").click(function () {
		
		// 空白の場合は何もしない
		if ($("#message").val() == "") {
			return;
		}
		
		// cometyServiceに接続している場合はメッセージを送信する
		if (cometyService) {
			cometyService.sendMessage($("#message").val());
			$("#message").val("");
		}
	});
	
	// 名前
	$("#name").keypress(function (e) {
		// Enterを押した場合は接続ボタンをクリックしたことにする
		if (e.keyCode == 13) {
			$("#connect").click();
		}
	});
	
	// 接続
	$("#connect").click(function () {
		
		// 名前の必須入力
		if ($("#name").val() == "") {
			alert("名前を入力してください");
			return;
		}
		
		// cometyServiceの初期化
		cometyService = new CometyService({
			rootPath: "<%=context%>/jaxrs"
		});
		
		// 接続時のイベントハンドラ
		cometyService.onOpen = function(data, status, xhr) {
			console.log("onOpen: sessionId=" + data);
		};
		
		// 切断時のイベントハンドラ
		cometyService.onClose = function(data, status, xhr) {
			console.log("onClose: sessionId=" + data);
		};
		
		// メッセージ取得時のイベントハンドラ
		cometyService.onMessage = function(data, status, xhr) {
			var obj = JSON.parse(data);
			console.log("onMessage: "
				+ [
					"sessionId=" + obj.sessionId,
					"name=" + obj.name,
					"message=" + obj.message
				].join(", ")
			);
			addMessage(obj);
		};
		
		// cometyServiceを起動
		cometyService.connect({
			name : $("#name").val()
		});
		connectedForm();
		$("#message").focus();
	});
	
	// 切断
	$("#close").click(function() {
		// cometyServiceに接続している場合は切断する
		if (cometyService) {
			cometyService.close();
			cometyService = null;
			closedForm();
		}
	});
	
	// フォームの初期化
	closedForm();
});
</script>
</head>
<body>
	<h1>comety chat</h1>
	<button id="connect">開始</button>
	<button id="close">切断</button>
	名前:
	<input type="text" id="name" />
	<br /> メッセージ：
	<input type="text" id="message" />
	<button id="send">送信</button>
	<div id="result"></div>
</body>
</html>