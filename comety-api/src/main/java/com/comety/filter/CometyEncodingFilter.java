package com.comety.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * エンコードを行うフィルター
 *
 * 例<br>
 * <code>
 * @WebFilter(
 *   asyncSupported=true,
 *   initParams={
 *     @WebInitParam(name="encoding", value="utf-8")
 *   },
 *   urlPatterns={ "/*" }
 * )
 * public class ExampleEncodingFilter extends CometyEncodingFilter {
 *
 * }
 * </code>
 */
public class CometyEncodingFilter implements Filter {

    /**
     * エンコーディング
     */
    protected String encoding;

    /**
     * 設定からエンコーディングを取り出す
     *
     * @param config 設定
     * @throws ServletException サーブレット例外
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
    }

    /**
     * リクエストのエンコーディングに設定のエンコーディングを設定する
     *
     * @param request リクエスト
     * @param response レスポンス
     * @param chain フィルター呼び出し
     * @throws ServletException サーブレット例外
     * @throws IOException IO例外
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
