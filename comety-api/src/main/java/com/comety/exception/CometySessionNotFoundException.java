package com.comety.exception;

/**
 * コメットのセッションが存在しない場合に発生する例外クラス
 */
public class CometySessionNotFoundException extends CometyException {

	/** SID */
	private static final long serialVersionUID = 1L;

	/**
	 * このクラスをインスタンス化する
	 * 
	 * @param sessionId セッションID
	 */
	public CometySessionNotFoundException(String sessionId) {
		super("sessionId [" + sessionId + "] is not found.");
	}

}
