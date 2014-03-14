package app.stub;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.comety.filter.CometyEncodingFilter;

@WebFilter(
		asyncSupported=true,
		initParams={
			@WebInitParam(name="encoding", value="utf-8")	
		},
		urlPatterns={
			"/*"
		}
	)
public class StubEncodingFilter extends CometyEncodingFilter {

}
