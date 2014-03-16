package com.comety.exception;

/**
 * Cometyの例外元クラス
 */
abstract public class CometyException extends RuntimeException {

    /**
     * SID
     */
    private static final long serialVersionUID = 1L;

    /**
     * このクラスをインスタンス化する
     */
    public CometyException() {
        super();
    }

    /**
     * このクラスをインスタンス化する
     *
     * @param message メッセージ
     */
    public CometyException(String message) {
        super(message);
    }

    /**
     * このクラスをインスタンス化する
     *
     * @param message メッセージ
     * @param cause 原因
     */
    public CometyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * このクラスをインスタンス化する
     *
     * @param cause 原因
     */
    public CometyException(Throwable cause) {
        super(cause);
    }

    /**
     * このクラスをインスタンス化する
     *
     * @param message メッセージ
     * @param cause 原因
     * @param enableSuppression 抑制の有効化または無効化
     * @param writableStackTrace 書き込み可能スタックトレースの有効化または無効化
     */
    protected CometyException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
