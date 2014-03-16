package app;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.comety.filter.CometyEncodingFilter;

/**
 * チャットシステムのエンコーディングフィルタ
 */
@WebFilter(
        asyncSupported = true,
        initParams = {
            @WebInitParam(name = "encoding", value = "utf-8")
        },
        urlPatterns = {
            "/*"
        }
)
public class ChatEncodingFilter extends CometyEncodingFilter {

}
