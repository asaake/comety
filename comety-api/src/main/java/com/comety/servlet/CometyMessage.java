package com.comety.servlet;

/**
 * コメットのメセージクラス
 */
public class CometyMessage {

	/** ステータス */
	protected String status;
	
	/** メッセージ */
	protected String message;
	
	/**
	 * コメットメッセージを生成する
	 * 
	 * @param status ステータス
	 * @param message メッセージ
	 */
	public CometyMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	/**
	 * ステータスを取得する
	 * 
	 * @return ステータス
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * メッセージを取得する
	 * 
	 * @return メッセージ
	 */
	public String getMessage() {
		return message;
	}
	
}
