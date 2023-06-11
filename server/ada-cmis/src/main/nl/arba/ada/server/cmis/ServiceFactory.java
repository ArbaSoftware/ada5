package nl.arba.ada.server.cmis;

import org.apache.chemistry.opencmis.commons.exceptions.CmisProxyAuthenticationException;
import org.apache.chemistry.opencmis.commons.impl.Base64;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class ServiceFactory extends AbstractServiceFactory {
    @Override
    public void init(Map<String, String> parameters) {

    }

    @Override
    public CmisService getService(CallContext callContext) {
        HttpServletRequest request = (HttpServletRequest) callContext.get(CallContext.HTTP_SERVLET_REQUEST);
        String authHeader = request.getHeader("Authorization");
        AdaService aService =  null;
        if (authHeader.startsWith("Basic ")) {
            try {
                String decoded = new String(Base64.decode(authHeader.substring("Basic ".length())));
                String user = decoded.split(Pattern.quote(":"))[0];
                String password = decoded.split(Pattern.quote(":"))[1];
                aService = new AdaService("http://192.168.2.74:9601/ada", user, password);
                return aService;
            }
            catch (java.io.IOException io) {
                io.printStackTrace();
                throw new CmisProxyAuthenticationException();
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
        else
            throw new CmisProxyAuthenticationException();

        return aService;
    }

    public void destroy() {
    }
}