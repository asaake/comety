package com.comety.exception;

/**
 * 一度だけ呼び出しを期待したところに二回以上呼び出しが来た場合の例外クラス
 */
public class CometyCallOnlyOnceException extends CometyException {

	/** SID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * このクラスをインスタンス化する
	 * 
	 * @param sessionId セッションID
	 */
	public CometyCallOnlyOnceException(String name) {
		super(name + " call only once.");
	}

}
