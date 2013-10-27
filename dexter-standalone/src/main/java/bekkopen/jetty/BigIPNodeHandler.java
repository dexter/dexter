package bekkopen.jetty;

import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static bekkopen.jetty.H.GET;
import static bekkopen.jetty.H.POST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Example Node handler - i.e. if you run your apps behind a F5 BigIP hardware load-balancer, and wish
 * to be able to sign in/out the nodes individually when redeploying.
 */
public class BigIPNodeHandler extends AbstractHandler {

    // The location the BigIP load balancer will check.
    // GET /node.txt /HTTP 1.0
    // Assuming the BigIP is configured to
    // * report the node down if it doesn't get a response from the path.
    // * report the node as turning off gracefully if it sees 'offline' (stop new sessions)
    // * report the node as healthy if it sees 'online' (accept new sessions)
    public static final String NODE_HANDLER_PATH = "/node.txt";

    // Respond with 226 if down. See RFC 3229#10.4.1 226 IM Used.
    // Our BigIP load-balancer requires a 2xx service code, and this is the most applicable to let the server know something is changed
    private static final int IM_USED = 226;

    private final String secretToken;

    private boolean available = true;

    public BigIPNodeHandler(String secretToken) {
        this.secretToken = secretToken;
    }

    public void handle(String target, Request serverRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!NODE_HANDLER_PATH.equals(target)) return;

        if("POST".equals(request.getMethod()) && secretToken.equals(request.getParameter("secret"))) {
          available = !("offline".equals(request.getParameter("state")));
        }

        response.setContentType("text/plain");
        response.setStatus(available ? SC_OK : IM_USED);

        PrintWriter out = response.getWriter();

        out.println(available ? "online" : "offline");
        out.close();

        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }

    public static String check(int port) {
      return GET("http://localhost:" + port + NODE_HANDLER_PATH).body;
    }

    public static String online(int port, String secretToken) {
        return POST("http://localhost:" + port + NODE_HANDLER_PATH + "?secret=" + secretToken + "&state=online").body;
    }

    public static String offline(int port, String secretToken) {
      return POST("http://localhost:" + port + NODE_HANDLER_PATH + "?secret=" + secretToken + "&state=offline").body;
    }

}
