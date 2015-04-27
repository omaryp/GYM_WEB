/**
 * 
 */
package pe.com.gym.login;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import pe.com.gym.entidades.Empleado;
import pe.com.gym.entidades.Menu;
import pe.com.gym.entidades.Perfil;


/**
 * @author Omar Yarleque 
 *
 */
public class Usuario extends User implements Serializable {

	private static final long serialVersionUID = 1L;
	private pe.com.gym.entidades.Usuario usuario;
	private Empleado empleado;
    private Perfil perfil;
    private List<Menu> opciones;
    
    public Usuario(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
   
    public Usuario(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

	public pe.com.gym.entidades.Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(pe.com.gym.entidades.Usuario usuario) {
		this.usuario = usuario;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public List<Menu> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<Menu> opciones) {
		this.opciones = opciones;
	}
    
}
