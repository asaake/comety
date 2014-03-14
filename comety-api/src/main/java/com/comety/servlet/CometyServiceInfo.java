package com.comety.servlet;

/**
 * コメットサービスの設定情報クラス
 */
public class CometyServiceInfo {
	
	/** メッセージのエンコーディング */
	protected String encoding;
	
	/** メッセージのコンテンツタイプ */
	protected String contentType;
	
	/** ポーリングのタイムアウト */
	protected long timeout;
	
	/**
	 * メッセージのエンコーディングを取得します
	 * @return メッセージのエンコーディング
	 */
	public String getEncoding() {
		return encoding;
	}
	
	/**
	 * メッセージのエンコーディングを設定します
	 * @param encoding メッセージのエンコーディング
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * メッセージのコンテンツタイプを取得します
	 * @return メッセージのコンテンツタイプ
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * メッセージのコンテンツタイプを設定します
	 * @param contentType メッセージのコンテンツタイプ
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * ポーリングのタイムアウトを取得します
	 * @return ポーリングのタイムアウト
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * ポーリングのタイムアウトを設定します
	 * @param timeout ポーリングのタイムアウト
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
