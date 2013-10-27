package bekkopen.jetty;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    private volatile int numberOfSessions;

    public void sessionCreated(HttpSessionEvent se) {
        synchronized (this) {
            numberOfSessions++;
        }
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        synchronized (this) {
            numberOfSessions--;
        }
    }

    public int getNumberOfSessions() {
        return numberOfSessions;
    }

}
