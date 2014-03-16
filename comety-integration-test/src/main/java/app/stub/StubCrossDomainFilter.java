package app.stub;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.comety.filter.CometyCrossDomainFilter;
import com.comety.servlet.CometyService;

/**
 * スタブのクロスドメインフィルター
 */
@WebFilter(
        asyncSupported = true,
        initParams = {
            @WebInitParam(name = "cors.supportedHeaders", value = CometyService.HEADER),
            @WebInitParam(name = "cors.exposedHeaders", value = CometyService.HEADER)
        },
        urlPatterns = {
            "/*"
        }
)
public class StubCrossDomainFilter extends CometyCrossDomainFilter {

}
