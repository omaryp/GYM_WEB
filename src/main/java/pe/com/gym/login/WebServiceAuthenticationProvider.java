package pe.com.gym.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pe.com.gym.delegate.Gym;
import pe.com.gym.entidades.Empleado;
import pe.com.gym.entidades.Menu;
import pe.com.gym.entidades.Perfil;

/**
 * @author Omar Yarleque
 *
 */
public class WebServiceAuthenticationProvider implements AuthenticationProvider {

    public WebServiceAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	int codigoEmpleado = 0;
        String user = authentication.getName();
        Empleado empleado = null;
        Perfil perfil = null;
        pe.com.gym.entidades.Usuario usuario = null;
        try {
        	if(!Gym.INSTANCE.validaUsuario(user, authentication.getCredentials().toString())){
        		throw new BadCredentialsException("Usuario o Contraseña invalidos");
        	}
        	usuario = Gym.INSTANCE.getUsuario(user);
        	codigoEmpleado = usuario.getId().getCodemp();
        	empleado = Gym.INSTANCE.getEmpleado(codigoEmpleado);
        	if(empleado == null){
        		throw new BadCredentialsException("Usuario no enlazado a un empleado");
        	}
        	perfil = Gym.INSTANCE.getPerfil(codigoEmpleado);
        	if(perfil == null){
        		throw new BadCredentialsException("Empleado no tiene asociado perfil");
        	}
        } catch (NullPointerException exception) {
            throw new ProviderNotFoundException("No se encontro el Servidor.");
        }
        List<Menu> opciones = Gym.INSTANCE.getMenus(codigoEmpleado);
        if (opciones == null || opciones.isEmpty()) {
            throw new CustomAuthenticationException("Usted no tiene autorización para la aplicación; consulte con su administrador");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        GrantedAuthority ga = new SimpleGrantedAuthority("PERFIL_TEST");
        authorities.add(ga);
        Usuario sesion = new Usuario(user, authentication.getCredentials().toString(),authorities);
        sesion.setEmpleado(empleado);
        sesion.setPerfil(perfil);
        sesion.setUsuario(usuario);
        sesion.setOpciones(opciones);
        
        return new UsernamePasswordAuthenticationToken(sesion, authentication.getCredentials(), authorities);
    }
    
    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
