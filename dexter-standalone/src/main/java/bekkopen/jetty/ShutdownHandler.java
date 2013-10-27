package bekkopen.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static bekkopen.jetty.H.POST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Inspiration from blogpost by Johannes Brodwall.
 */
public class ShutdownHandler extends AbstractHandler {

    private final Server server;
    private final WebAppContext context;
    private final String secret;

    public ShutdownHandler(Server server, WebAppContext context, String secret) {
        this.server = server;
        this.context = context;
        this.secret = secret;
    }

    public void handle(String target, Request serverRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!target.equals("/shutdown") || !request.getMethod().equals("POST") || !secret.equals(request.getParameter("secret")))
            return;

        try {
            // You should probably do something clever with this if you handle a lot of transactions etc
            // BigIP fortunately handles sessions to my nodes, so I haven't put much effort into this.
            response.setStatus(SC_OK);
            PrintWriter out = response.getWriter();
            out.println("Shutting down");
            try { out.close();    } catch (Exception ignored) {}
            try { context.stop(); } catch (Exception ignored) {}
            try { server.stop();  } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        System.exit(0);
    }

    public static String shutdown(int port, String shutdownSecret) {
      return POST("http://127.0.0.1:" + port + "/shutdown?secret=" + shutdownSecret).body;
    }
}
