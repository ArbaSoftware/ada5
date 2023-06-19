package nl.arba.ada.server.cmis;

import nl.arba.ada.server.cmis.services.Cache;
import nl.arba.ada.server.cmis.services.browser.BrowserService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class App {
    public static void main(String[] args) {
        try {
            Server server = new Server(8080);

            Cache cache = new Cache("http://192.168.2.74:9601/ada");

            ServletHandler handler = new ServletHandler();
            BrowserService browser = new BrowserService("http://192.168.2.74:9601/ada", cache);
            handler.addServletWithMapping(new ServletHolder(browser), "/browser");
            server.setHandler(handler);

            server.start();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
