package com.comety.exception;

/**
 * 接続パラメータの検証が失敗した場合の例外クラス
 */
public class CometyInValidConnectParametersException  extends CometyException {
	
	/** SID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * このクラスをインスタンス化する
	 * 
	 * @param message メッセージ
	 */
	public CometyInValidConnectParametersException(String message) {
		super(message);
	}

}
