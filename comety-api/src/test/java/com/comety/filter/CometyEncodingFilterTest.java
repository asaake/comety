package com.comety.filter;

import static org.junit.Assert.assertEquals;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

/**
 * CometyEncodingFilterのテストクラス
 */
public class CometyEncodingFilterTest {

    @Mocked
    FilterConfig config;

    @Mocked
    HttpServletRequest request;

    @Mocked
    HttpServletResponse response;

    @Mocked
    FilterChain chain;

    @Tested
    CometyEncodingFilter filter;

    @Test
    public void testInit() throws Exception {
        new Expectations() {{
            config.getInitParameter("encoding");
            result = "utf-8";
        }};
        filter.init(config);
        assertEquals("utf-8", Deencapsulation.getField(filter, "encoding"));
    }

    @Test
    public void testDoFilter() throws Exception {
        new NonStrictExpectations() {{
            config.getInitParameter("encoding");
            result = "utf-8";
            request.setCharacterEncoding("utf-8");
        }};

        filter.init(config);
        filter.doFilter(request, response, chain);

        new Verifications() {{
            config.getInitParameter("encoding");
            times = 1;
            request.setCharacterEncoding("utf-8");
        }};
    }
}
