/**
 * 
 */
package pe.com.gym.login;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * @author Omar Yarleque
 *
 */
public class JsfRedirectInvalidSessionStrategy implements InvalidSessionStrategy {

    private static final Logger logger = Logger.getLogger(JsfRedirectInvalidSessionStrategy.class.getName());
    private final String destinationUrl;
    private RedirectStrategy redirectStrategy;
    private boolean createNewSession = true;

    public JsfRedirectInvalidSessionStrategy(String invalidSessionUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
        this.destinationUrl = invalidSessionUrl;
    }
    
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.log(Level.INFO, "Starting new session (if required) and redirecting to ''{0}''", destinationUrl);
        if (createNewSession) {
            request.getSession();
        }
        redirectStrategy.sendRedirect(request, response, destinationUrl);
    }
    
    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }    
}