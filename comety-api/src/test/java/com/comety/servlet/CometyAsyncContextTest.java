package com.comety.servlet;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

import com.comety.exception.CometyCallOnlyOnceException;

/**
 * CometyAsyncContextのテストクラス
 */
public class CometyAsyncContextTest {

    @Injectable
    @Mocked
    CometySession session;

    @Injectable
    @Mocked
    AsyncContext asyncContext;

    @Mocked
    CometyService cometyService;

    @Tested
    CometyAsyncContext context;

    @Test
    public void completeメソッドが呼ばれるとisCompleteがtrueになることを確認する() {
        new NonStrictExpectations() {{
                asyncContext.complete();
        }};

        context.complete();

        assertEquals(true, context.isComplete());
        new Verifications() {{
            asyncContext.complete(); times = 1;
        }};
    }

    @Test(expected = CometyCallOnlyOnceException.class)
    public void completeメソッドを二度呼び出すと例外が発生することを確認する() {
        new NonStrictExpectations() {{
            asyncContext.complete();
        }};

        context.complete();
        context.complete();
    }

    @Test
    public void sendMessageでPrintWriterに指定したメッセージが書き込まれることを確認する(final @Mocked HttpServletResponse response, final @Mocked PrintWriter writer) throws Exception {
        final CometyServiceInfo info = new CometyServiceInfo();
        info.setContentType("testContentType");
        info.setEncoding("testEncoding");
        info.setTimeout(-1);

        final String cometStatus = "test-status";
        final int httpStatus = 200;
        final String message = "test-message";

        new NonStrictExpectations() {{

            // session
            session.getCometService(); result = cometyService;

            // cometService
            cometyService.getCometyServiceInfo(); result = info;

            // asyncContext
            asyncContext.getResponse(); result = response;
            asyncContext.complete();

            // response
            response.setCharacterEncoding(info.getEncoding());
            response.setContentType(info.getContentType());
            response.addHeader(CometyService.HEADER, cometStatus);
            response.setStatus(httpStatus);
            response.getWriter(); result = writer;

        }};

        context.sendMessage(cometStatus, message);

        new Verifications() {{
            response.setCharacterEncoding(info.getEncoding()); times = 1;
            response.setContentType(info.getContentType()); times = 1;
            response.addHeader(CometyService.HEADER, cometStatus); times = 1;
            response.setStatus(httpStatus); times = 1;
            writer.write(message); times = 1;
        }};
    }

}
