package proxy;

import burp.IHttpRequestResponse;
import burp.IHttpService;
import burp.IInterceptedProxyMessage;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ProxyMessageContainerTest {

    @Mock
    private IInterceptedProxyMessage message;
    @Mock
    private IHttpRequestResponse iHttpRequestResponse;
    @Mock
    private IHttpService iHttpService;

    byte[] request1 = ("POST /suite/passport/v3/login_type?_r50470=1631191165187&_signature=_02B4Z6wo00f01AJhHqAAAIDCSUVO0X6XJbgCQRoAAGGo09 HTTP/1.1\r\n" +
            "Host: passport.larksuite.com\r\n" +
            "Cookie: __tea__ug__uid=7005201506236597766; _ga=GA1.2.322131158.1631025588; passport_web_did=7005210448710598662; locale=en-US; trust_browser_id=4f30a47b-68b8-4570-8345-17299d357eef; s_v_web_id=verify_kta7ray4_WOSIoSoe_aOSg_4kBu_BJ1j_GNW1cOjDP5wR; fid=55b9e1c8-274f-46d9-99e2-79eb0273ec86; is_anonymous_session=; _csrf_token=c151a485df937541b22d61e9849b1fd35bed19f9-1631028525; lang=en; csrf_token=8c2efa1c-edc2-4d43-b089-3165772f104b; m_65f68ea2=38633265666131632d656463322d346434332d623038392d33313635373732663130346246111c051538e0cc44e1d1f309c607eaed4c1984060d7ebff8b389a1905e6d7d; landing_url=https://passport.larksuite.com/suite/passport/page/login/?query_scope=all&app_id=2&should_pass_through=1&utm_from=organic_ccm_share_web&redirect_uri=https%3A%2F%2Fqgofkq606f.larksuite.com%2Fwiki%2FwikusXZNmOWmSGobB4dCfjezPnf; swp_csrf_token=6cd82a98-f3e5-41d6-a890-3ed94eb79a43; t_beda37=8ef483c4f85b3418b17835bf7108f82ddb55f0f8b4fedff55d0a8697eafdfc5f; MONITOR_WEB_ID=d0115479-0ea5-4d9a-b0e5-e8cc9755ac80\r\n" +
            "Content-Length: 883\r\n" +
            "Pragma: no-cache\r\n" +
            "Accept-Encoding: gzip, deflate\r\n" +
            "Accept-Language: en-GB,en-US;q=0.9,en;q=0.8\r\n" +
            "Connection: close\r\n" +
            "\r\n" +
            "{\"query_scope\":\"all\",\"app_id\":2,\"pattern\":\"\",\"reg_params\":\"{").getBytes();

    String host1 = "seedbits.dev";

    void mock1() {
        MockitoAnnotations.initMocks(this);
        when(this.iHttpService.getHost()).thenReturn(this.host1);
        when(this.iHttpRequestResponse.getRequest()).thenReturn(this.request1);
        when(this.iHttpRequestResponse.getHttpService()).thenReturn(this.iHttpService);
        when(this.message.getMessageInfo()).thenReturn(this.iHttpRequestResponse);
    }

    @Test
    void getPath() {
        mock1();
        ProxyMessageContainer pmc = new ProxyMessageContainer(this.message);
        System.out.println(pmc.getHostname());
        System.out.println(pmc.getPath());
        System.out.println(new String(pmc.getRequest()));
        System.out.println(pmc.getMethod());
        assert true;
    }
}