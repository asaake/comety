package com.comety.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

/**
 * 以下の動作をするリスナークラス<br>
 * complete: セッションのポーリングを停止する<br>
 * error: セッションのポーリングを呈する<br>
 * timeout: タイムアウトメッセージを送信する<br>
 */
public class CometyAsyncListener implements AsyncListener {

    /**
     * セッション
     */
    protected CometySession session;

    /**
     * 指定された値でAsyncListenerを初期化する
     *
     * @param session セッション
     */
    public CometyAsyncListener(CometySession session) {
        this.session = session;
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    /**
     * コンプリート時にポーリングを停止する
     */
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        if (session.isOpen()) {
            session.stopPolling();
        }
    }

    /**
     * タイムアウト時にタイムアウトメッセージを送信する
     */
    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        if (session.isOpen()) {
            session.sendTimeoutMessage("");
        }
    }

    /**
     * エラー時にポーリングを停止する
     */
    @Override
    public void onError(AsyncEvent event) throws IOException {
        if (session.isOpen()) {
            session.stopPolling();
        }
    }

}
