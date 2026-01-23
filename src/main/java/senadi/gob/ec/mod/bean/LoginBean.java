package senadi.gob.ec.mod.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.RazonCorreccion;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Usuario;
import senadi.gob.ec.mod.ucc.LDAPIngreso;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private String login;
    private String clave;
    private boolean logeado = false;
    private int idUser;

    private Usuario usuario;

    private boolean shake;

    private Renovacion renovacionFlotante;
    private Notificada notificadaFlotante;
    private boolean various;
    private List<Renovacion> renovacionesFlotantes;
    private List<Notificada> notificadasFlotantes;
    private boolean usuarioConsulta;

    private List<Caducada> caducadas;
    private Caducada caducada;
    private boolean other;

    private boolean allInOne;

    private boolean newreport;

    private boolean subrogante;

    private String nombre;
    
    private RazonCorreccion razon;
    
    private List<Abandono> abandonos;
    private Abandono abandono;

    public LoginBean() {
        shake = true;
        usuarioConsulta = false;
    }

    public boolean estaLogeado() {
        return logeado;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void login(ActionEvent actionEvent) {
//        RequestContext context = RequestContext.getCurrentInstance();

        FacesMessage msg = null;

        LDAPIngreso ldap = new LDAPIngreso();
        int n = ldap.validarIngresoLDAP(login, clave);
//        int n = -1;
        if (n == 1) {
            shake = false;
            logeado = true;
            usuarioConsulta = false;

            PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);

            if (logeado) {

                PrimeFaces.current().ajax().addCallbackParam("view", "renovaciones.xhtml");
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", login);

        } else if (n == -1) {
            boolean validar = ldap.validarIngresoLDAPSinrestrinccion(login, clave);
            if (validar) {
                usuarioConsulta = true;
                shake = false;

                logeado = true;

                PrimeFaces.current().ajax().addCallbackParam("estaLogeado", logeado);
                if (logeado) {

                    PrimeFaces.current().ajax().addCallbackParam("view", "renovaciones.xhtml");
                }
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "LECTURA-Bienvenid@", login);
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Credenciales Incorrectas");
            }

        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Credenciales Incorrectas");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        logeado = false;
        shake = false;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    /**
     * @return the shake
     */
    public boolean isShake() {
        return shake;
    }

    /**
     * @param shake the shake to set
     */
    public void setShake(boolean shake) {
        this.shake = shake;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUser(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the renovacionFlotante
     */
    public Renovacion getRenovacionFlotante() {
        return renovacionFlotante;
    }

    /**
     * @param renovacionFlotante the renovacionFlotante to set
     */
    public void setRenovacionFlotante(Renovacion renovacionFlotante) {
        this.renovacionFlotante = renovacionFlotante;
    }

    /**
     * @return the notificadaFlotante
     */
    public Notificada getNotificadaFlotante() {
        return notificadaFlotante;
    }

    /**
     * @param notificadaFlotante the notificadaFlotante to set
     */
    public void setNotificadaFlotante(Notificada notificadaFlotante) {
        this.notificadaFlotante = notificadaFlotante;
    }

    /**
     * @return the various
     */
    public boolean isVarious() {
        return various;
    }

    /**
     * @param various the various to set
     */
    public void setVarious(boolean various) {
        this.various = various;
    }

    /**
     * @return the renovacionesFlotantes
     */
    public List<Renovacion> getRenovacionesFlotantes() {
        return renovacionesFlotantes;
    }

    /**
     * @param renovacionesFlotantes the renovacionesFlotantes to set
     */
    public void setRenovacionesFlotantes(List<Renovacion> renovacionesFlotantes) {
        this.renovacionesFlotantes = renovacionesFlotantes;
    }

    /**
     * @return the notificadasFlotantes
     */
    public List<Notificada> getNotificadasFlotantes() {
        return notificadasFlotantes;
    }

    /**
     * @param notificadasFlotantes the notificadasFlotantes to set
     */
    public void setNotificadasFlotantes(List<Notificada> notificadasFlotantes) {
        this.notificadasFlotantes = notificadasFlotantes;
    }

    /**
     * @return the allInOne
     */
    public boolean isAllInOne() {
        return allInOne;
    }

    /**
     * @param allInOne the allInOne to set
     */
    public void setAllInOne(boolean allInOne) {
        this.allInOne = allInOne;
    }

    /**
     * @return the usuarioConsulta
     */
    public boolean isUsuarioConsulta() {
        return usuarioConsulta;
    }

    /**
     * @param usuarioConsulta the usuarioConsulta to set
     */
    public void setUsuarioConsulta(boolean usuarioConsulta) {
        this.usuarioConsulta = usuarioConsulta;
    }

    /**
     * @return the newreport
     */
    public boolean isNewreport() {
        return newreport;
    }

    /**
     * @param newreport the newreport to set
     */
    public void setNewreport(boolean newreport) {
        this.newreport = newreport;
    }

    /**
     * @return the subrogante
     */
    public boolean isSubrogante() {
        return subrogante;
    }

    /**
     * @param subrogante the subrogante to set
     */
    public void setSubrogante(boolean subrogante) {
        this.subrogante = subrogante;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the caducadas
     */
    public List<Caducada> getCaducadas() {
        return caducadas;
    }

    /**
     * @param caducadas the caducadas to set
     */
    public void setCaducadas(List<Caducada> caducadas) {
        this.caducadas = caducadas;
    }

    /**
     * @return the caducada
     */
    public Caducada getCaducada() {
        return caducada;
    }

    /**
     * @param caducada the caducada to set
     */
    public void setCaducada(Caducada caducada) {
        this.caducada = caducada;
    }

    /**
     * @return the other
     */
    public boolean isOther() {
        return other;
    }

    /**
     * @param other the other to set
     */
    public void setOther(boolean other) {
        this.other = other;
    }

    /**
     * @return the razon
     */
    public RazonCorreccion getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(RazonCorreccion razon) {
        this.razon = razon;
    }

    /**
     * @return the abandonos
     */
    public List<Abandono> getAbandonos() {
        return abandonos;
    }

    /**
     * @param abandonos the abandonos to set
     */
    public void setAbandonos(List<Abandono> abandonos) {
        this.abandonos = abandonos;
    }

    /**
     * @return the abandono
     */
    public Abandono getAbandono() {
        return abandono;
    }

    /**
     * @param abandono the abandono to set
     */
    public void setAbandono(Abandono abandono) {
        this.abandono = abandono;
    }
}
