package com.comety.exception;

import java.io.IOException;

/**
 * IOExceptionをRuntimeExceptionでラップしたクラス
 */
public class CometyIOException extends CometyException {

	/** SID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * このクラスをインスタンス化する
	 * 
	 * @param e IOException
	 */
	public CometyIOException(IOException e) {
		super(e);
	}

}
