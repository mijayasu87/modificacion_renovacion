/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "abandono")
public class Abandono implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "solicitud")
    private String solicitud;

    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;

    @Column(name = "notificacion")
    private Integer notificacion;

    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;

    @Column(name = "no_registro")
    private String registro;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "denominacion")
    private String denominacion;

    @Column(name = "signo")
    private String signo;

    @Column(name = "titular_actual")
    private String titularActual;

    @Column(name = "ape_apod_repre")
    private String apeApodRepre;

    @Column(name = "fecha_elabora_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaElaboraNotificacion;

    @Column(name = "certificado")
    private String certificado;

    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;

    @Column(name = "casillero_senadi")
    private String casilleroSenadi;

    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "ro")
    private String ro;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "identificacion")
    private String identificacion;

    @Column(name = "r1")
    private String r1;

    @Column(name = "comprobante")
    private String comprobante;

    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;

    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;

    @Column(name = "cancelado")
    private String cancelado;

    @Column(name = "tipo_abandono")
    private String tipoAbandono;

    @Column(name = "solicitante")
    private String solicitante;

    @Column(name = "numero_abandono")
    private Integer numeroAbandono;

    @Column(name = "fecha_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAbandono;
    
    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "abandono_notificado")
    private boolean abadonoNotificado;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the solicitud
     */
    public String getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the fechaPresentacion
     */
    public Date getFechaPresentacion() {
        return fechaPresentacion;
    }

    /**
     * @param fechaPresentacion the fechaPresentacion to set
     */
    public void setFechaPresentacion(Date fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    /**
     * @return the notificacion
     */
    public Integer getNotificacion() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(Integer notificacion) {
        this.notificacion = notificacion;
    }

    /**
     * @return the fechaNotificacion
     */
    public Date getFechaNotificacion() {
        return fechaNotificacion;
    }

    /**
     * @param fechaNotificacion the fechaNotificacion to set
     */
    public void setFechaNotificacion(Date fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    /**
     * @return the registro
     */
    public String getRegistro() {
        return registro;
    }

    /**
     * @param registro the registro to set
     */
    public void setRegistro(String registro) {
        this.registro = registro;
    }

    /**
     * @return the fechaRegistro
     */
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * @param fechaRegistro the fechaRegistro to set
     */
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * @return the denominacion
     */
    public String getDenominacion() {
        return denominacion;
    }

    /**
     * @param denominacion the denominacion to set
     */
    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    /**
     * @return the signo
     */
    public String getSigno() {
        return signo;
    }

    /**
     * @param signo the signo to set
     */
    public void setSigno(String signo) {
        this.signo = signo;
    }

    /**
     * @return the titularActual
     */
    public String getTitularActual() {
        return titularActual;
    }

    /**
     * @param titularActual the titularActual to set
     */
    public void setTitularActual(String titularActual) {
        this.titularActual = titularActual;
    }

    /**
     * @return the apeApodRepre
     */
    public String getApeApodRepre() {
        return apeApodRepre;
    }

    /**
     * @param apeApodRepre the apeApodRepre to set
     */
    public void setApeApodRepre(String apeApodRepre) {
        this.apeApodRepre = apeApodRepre;
    }

    /**
     * @return the fechaElaboraNotificacion
     */
    public Date getFechaElaboraNotificacion() {
        return fechaElaboraNotificacion;
    }

    /**
     * @param fechaElaboraNotificacion the fechaElaboraNotificacion to set
     */
    public void setFechaElaboraNotificacion(Date fechaElaboraNotificacion) {
        this.fechaElaboraNotificacion = fechaElaboraNotificacion;
    }

    /**
     * @return the certificado
     */
    public String getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    /**
     * @return the fechaCertificado
     */
    public Date getFechaCertificado() {
        return fechaCertificado;
    }

    /**
     * @param fechaCertificado the fechaCertificado to set
     */
    public void setFechaCertificado(Date fechaCertificado) {
        this.fechaCertificado = fechaCertificado;
    }

    /**
     * @return the casilleroSenadi
     */
    public String getCasilleroSenadi() {
        return casilleroSenadi;
    }

    /**
     * @param casilleroSenadi the casilleroSenadi to set
     */
    public void setCasilleroSenadi(String casilleroSenadi) {
        this.casilleroSenadi = casilleroSenadi;
    }

    /**
     * @return the casilleroJudicial
     */
    public String getCasilleroJudicial() {
        return casilleroJudicial;
    }

    /**
     * @param casilleroJudicial the casilleroJudicial to set
     */
    public void setCasilleroJudicial(String casilleroJudicial) {
        this.casilleroJudicial = casilleroJudicial;
    }

    /**
     * @return the ro
     */
    public String getRo() {
        return ro;
    }

    /**
     * @param ro the ro to set
     */
    public void setRo(String ro) {
        this.ro = ro;
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the identificacion
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * @param identificacion the identificacion to set
     */
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    /**
     * @return the r1
     */
    public String getR1() {
        return r1;
    }

    /**
     * @param r1 the r1 to set
     */
    public void setR1(String r1) {
        this.r1 = r1;
    }

    /**
     * @return the comprobante
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     * @param comprobante the comprobante to set
     */
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    /**
     * @return the certificadoEmitido
     */
    public boolean isCertificadoEmitido() {
        return certificadoEmitido;
    }

    /**
     * @param certificadoEmitido the certificadoEmitido to set
     */
    public void setCertificadoEmitido(boolean certificadoEmitido) {
        this.certificadoEmitido = certificadoEmitido;
    }

    /**
     * @return the notificacionEmitida
     */
    public boolean isNotificacionEmitida() {
        return notificacionEmitida;
    }

    /**
     * @param notificacionEmitida the notificacionEmitida to set
     */
    public void setNotificacionEmitida(boolean notificacionEmitida) {
        this.notificacionEmitida = notificacionEmitida;
    }

    /**
     * @return the cancelado
     */
    public String getCancelado() {
        return cancelado;
    }

    /**
     * @param cancelado the cancelado to set
     */
    public void setCancelado(String cancelado) {
        this.cancelado = cancelado;
    }

    /**
     * @return the tipoAbandono
     */
    public String getTipoAbandono() {
        return tipoAbandono;
    }

    /**
     * @param tipoAbandono the tipoAbandono to set
     */
    public void setTipoAbandono(String tipoAbandono) {
        this.tipoAbandono = tipoAbandono;
    }

    /**
     * @return the solicitante
     */
    public String getSolicitante() {
        return solicitante;
    }

    /**
     * @param solicitante the solicitante to set
     */
    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    /**
     * @return the numeroAbandono
     */
    public Integer getNumeroAbandono() {
        return numeroAbandono;
    }

    /**
     * @param numeroAbandono the numeroAbandono to set
     */
    public void setNumeroAbandono(Integer numeroAbandono) {
        this.numeroAbandono = numeroAbandono;
    }

    /**
     * @return the fechaAbandono
     */
    public Date getFechaAbandono() {
        return fechaAbandono;
    }

    /**
     * @param fechaAbandono the fechaAbandono to set
     */
    public void setFechaAbandono(Date fechaAbandono) {
        this.fechaAbandono = fechaAbandono;
    }

    /**
     * @return the idRenewalForm
     */
    public Integer getIdRenewalForm() {
        return idRenewalForm;
    }

    /**
     * @param idRenewalForm the idRenewalForm to set
     */
    public void setIdRenewalForm(Integer idRenewalForm) {
        this.idRenewalForm = idRenewalForm;
    }

    /**
     * @return the fechaVencimiento
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * @param fechaVencimiento the fechaVencimiento to set
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * @return the abadonoNotificado
     */
    public boolean isAbadonoNotificado() {
        return abadonoNotificado;
    }

    /**
     * @param abadonoNotificado the abadonoNotificado to set
     */
    public void setAbadonoNotificado(boolean abadonoNotificado) {
        this.abadonoNotificado = abadonoNotificado;
    }

    
}
