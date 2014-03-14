/**
 * Cometのサービスを扱うクラス
 * 
 * 例：
 *   var cometyService = new CometyService({
 *     rootPath: "/myservice/jaxrs"
 *   });
 *   
 *   cometyService.onOpen = function (data, status, xhr) {
 *     console.log("onOpen");
 *   };
 *   cometyService.onClose = function (data, status, xhr) {
 *     console.log("onClose");
 *   };
 *   cometyService.onMessage = function (data, status, xhr) {
 *     console.log("onMessage: " + data);
 *   }
 *   
 *   cometyService.connect(); // サービスを開始する
 *   cometyService.sendMessage("Hello World."); // メッセージを送信する
 *   cometyService.close(); // サービスを終了する
 */
var CometyService = (function() {
	
	/**
	 * 初期化時に設定を読み込む
	 * 
	 * @param options 設定
	 */
	function CometyService(options) {
		if (!jQuery) throw new Error("jQuery is required.");
		if (!options) throw new Error("options object is required.");
		if (!options.rootPath) throw new Error("options.rootPath is requierd.");
		if (!options.crossDomain) options.crossDomain = false;
		this.sessionId = null;
		this.rootPath = options.rootPath;
		this.crossDomain = options.crossDomain;
		
		console.log("CometyService Settings ->");
		console.log("rootPath: " + this.rootPath);
		console.log("crossDomain: " + this.crossDomain);
		console.log("<- CometyService Settings");
	};
	
	CometyService.POLLING_PATH = "polling";
	CometyService.CONNECT_PATH = "connect";
	CometyService.OPEN_PATH = "open";
	CometyService.CLOSE_PATH = "close";
	CometyService.MESSAGE_PATH = "message";
	
	CometyService.HEADER_STATUS = "comety-status";
	CometyService.OPEN_STATUS = "open";
	CometyService.CLOSE_STATUS = "close";
	CometyService.MESSAGE_STATUS = "message";
	CometyService.TIMEOUT_STATUS = "timeout";
	
	/**
	 * パスを作成する
	 */
	CometyService.prototype.path = function () {
		var args = Array.prototype.slice.call(arguments);
		args.unshift(this.rootPath);
		return args.join("/");
	};
	
	/**
	 * 現在のセッションを閉じる
	 * 
	 * @return Deferred
	 */
	CometyService.prototype.close = function () {
		if (!this.sessionId) {
			throw new Error("not connect comety service.");
		}
		
		return this.post(
			this.path(CometyService.CLOSE_PATH),
			{
				sessionId: this.sessionId
			}
		);
	};
	
	/**
	 * セッションを接続する
	 * 
	 * @return Deferred
	 */
	CometyService.prototype.connect = function (data) {
		if (!data) data = {};
		return this.post(
			this.path(CometyService.CONNECT_PATH),
			data
		).pipe(function (data, status, xhr) {
			this.sessionId = data;
			this.polling();
			return this.post(
				this.path(CometyService.OPEN_PATH),
				{
					sessionId: this.sessionId
				}
			).pipe(function () {
				return $.Deferred().resolve(data, status, xhr);
			});
		}.bind(this));
	};
	
	/**
	 * セッションの接続待機を行う
	 * ライブラリが自動で呼び出すメソッド
	 * 
	 * @return Deferred
	 */
	CometyService.prototype.polling = function (){
		if (!this.sessionId) {
			throw new Error("not connect comety service.");
		}
		
        return this.post(
        	this.path(CometyService.POLLING_PATH),
        	{
        		sessionId: this.sessionId
        	}
        ).pipe(function (data, status, xhr) {
        	var cometyStatus = xhr.getResponseHeader(CometyService.HEADER_STATUS);
        	switch (cometyStatus) {
        		case CometyService.OPEN_STATUS:
        			this.onOpen(data, status, xhr);
        			this.polling();
        			break;
        		case CometyService.CLOSE_STATUS:
        			this.onClose(data, status, xhr);
        			this.sessionId = null;
        			break;
        		case CometyService.MESSAGE_STATUS:
        			this.onMessage(data, status, xhr);
        			this.polling();
        			break;
        		case CometyService.TIMEOUT_STATUS:
        			this.polling();
        			break;
        		default:
        			console.warn("CometyService: [" + cometyStatus + "] is not supoorted status.");
        	}
        }.bind(this));
    };
    
    /**
     * POSTで値を送信する
     */
    CometyService.prototype.post = function (url, data) {
    	return $.ajax({
    		url: url,
    		type:"POST",
    		data: data,
    		contentType: "application/x-www-form-urlencoded",
    		crossDomain: this.crossDomain
    	});
    };
    
    /**
     * 接続中のセッションにメッセージを送信する
     * 
     * @param message メッセージ
     * @return Deferred
     */
    CometyService.prototype.sendMessage = function (message) {
    	if (!this.sessionId) {
			throw new Error("not connect comety service.");
		}
    	
    	return this.post(
    		this.path(CometyService.MESSAGE_PATH),
    		{
    			sessionId: this.sessionId,
    			message: message
    		}
    	);
    };
    
    /**
     * オープン時に呼び出されるイベントハンドラ
     * 
     * @param data データ
     * @param status ステータス
     * @param xhr xhrオブジェクト
     */
    CometyService.prototype.onOpen = function (data, status, xhr) {
    };
    
    /**
     * クローズ時に呼び出されるイベントハンドラ
     * 
     * @param data データ
     * @param status ステータス
     * @param xhr xhrオブジェクト
     */
    CometyService.prototype.onClose = function (data, status, xhr) {
    };
    
    /**
     * メッセージを受け取った時に呼び出されるイベントハンドラ
     * 
     * @param data データ
     * @param status ステータス
     * @param xhr xhrオブジェクト
     */
    CometyService.prototype.onMessage = function (data, status, xhr) {
    };
    
	return CometyService;
})();