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
 * <pre>
@WebFilter(
	asyncSupported=true,
	initParams={
		@WebInitParam(name="encoding", value="utf-8")	
	},
	urlPatterns={
		"/*"
	}
)
public class ExampleEncodingFilter extends CometyEncodingFilter {

}
 * </pre>
 */
public class CometyEncodingFilter implements Filter {
    
	/** エンコーディング */
	protected String encoding;

	/**
	 * 設定からエンコーディングを取り出す
	 */
	@Override
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
    }

	/**
	 * リクエストのエンコーディングに設定のエンコーディングを設定する
	 */
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

	@Override
    public void destroy() {}
}
