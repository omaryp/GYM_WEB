/**
 * 
 */
package pe.com.gym.login;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Omar Yarleque
 *
 */
public class CustomAuthenticationException extends AuthenticationException {
    
	private static final long serialVersionUID = 1L;

	public CustomAuthenticationException(String msg) {
        super(msg);
    }
}