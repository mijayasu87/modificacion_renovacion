/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Michael Yanangómez
 */
@Entity
@Table(name = "caducada")
public class Caducada implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "resolucion")
    private String resolucion;
    
    @Column(name = "solicitud_senadi")
    private String solicitudSenadi;
    
    @Column(name = "fecha_solicitud")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaSolicitud;
    
//    @Transient
//    private String fechaSolPres;
    
    @Column(name = "denominacion")
    private String denominacion;
    
    @Column(name = "no_registro")
    private String registroNo;
    
    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;
    
//    @Transient
//    private String fechaRegPres; 
    
    @Column(name = "solicitante")
    private String solicitante;
    
    @Column(name = "fecha_providencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProvidencia;
    
//    @Transient
//    private String fechaProPres;
    
    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "abogado_patrocinador")
    private String abogadoPatrocinador;
    
    @Column(name = "casillero_no")
    private String casilleroNo;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "requisito_notificado")
    private String requisitoNotificado;
    
    @Column(name = "certificado_no")
    private Integer certificaNo;
    
    @Column(name = "fecha_otorgada")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaOtorgada;
    
//    @Transient
//    private String fechaOtoPres;
    
    @Column(name = "solicitud")
    private String solicitud;
    
    @Column(name = "fecha_de_solicitud")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaDeSolicitud;
    
    @Column(name = "responsable")
    private String responsable;
    
    @Column(name = "signo")
    private String signo;
    
    @Column(name = "fecha_vencimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencimiento;
    
//    @Transient
//    private String fechaVencPres;
    
    @Column(name = "identificacion")
    private String identificacion;
    
    @Column(name = "ant_des")
    private String antDes;
    
    @Column(name = "cancelado")
    private String cancelado;
    
    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;
    
    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;
    
    @Column(name = "caducada_emitida")
    private boolean caducadaEmitida;
    
    @Column(name = "antecedente")
    private String antecedente;
    
    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "observacion")
    private String observacion;

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
     * @return the resolucion
     */
    public String getResolucion() {
        return resolucion;
    }

    /**
     * @param resolucion the resolucion to set
     */
    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    /**
     * @return the solicitudSenadi
     */
    public String getSolicitudSenadi() {
        return solicitudSenadi;
    }

    /**
     * @param solicitudSenadi the solicitudSenadi to set
     */
    public void setSolicitudSenadi(String solicitudSenadi) {
        this.solicitudSenadi = solicitudSenadi;
    }

    /**
     * @return the fechaSolicitud
     */
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * @param fechaSolicitud the fechaSolicitud to set
     */
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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
     * @return the registroNo
     */
    public String getRegistroNo() {
        return registroNo;
    }

    /**
     * @param registroNo the registroNo to set
     */
    public void setRegistroNo(String registroNo) {
        this.registroNo = registroNo;
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
     * @return the fechaProvidencia
     */
    public Date getFechaProvidencia() {
        return fechaProvidencia;
    }

    /**
     * @param fechaProvidencia the fechaProvidencia to set
     */
    public void setFechaProvidencia(Date fechaProvidencia) {
        this.fechaProvidencia = fechaProvidencia;
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
     * @return the abogadoPatrocinador
     */
    public String getAbogadoPatrocinador() {
        return abogadoPatrocinador;
    }

    /**
     * @param abogadoPatrocinador the abogadoPatrocinador to set
     */
    public void setAbogadoPatrocinador(String abogadoPatrocinador) {
        this.abogadoPatrocinador = abogadoPatrocinador;
    }

    /**
     * @return the casilleroNo
     */
    public String getCasilleroNo() {
        return casilleroNo;
    }

    /**
     * @param casilleroNo the casilleroNo to set
     */
    public void setCasilleroNo(String casilleroNo) {
        this.casilleroNo = casilleroNo;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the requisitoNotificado
     */
    public String getRequisitoNotificado() {
        return requisitoNotificado;
    }

    /**
     * @param requisitoNotificado the requisitoNotificado to set
     */
    public void setRequisitoNotificado(String requisitoNotificado) {
        this.requisitoNotificado = requisitoNotificado;
    }

    /**
     * @return the certificaNo
     */
    public Integer getCertificaNo() {
        return certificaNo;
    }

    /**
     * @param certificaNo the certificaNo to set
     */
    public void setCertificaNo(Integer certificaNo) {
        this.certificaNo = certificaNo;
    }

    /**
     * @return the fechaOtorgada
     */
    public Date getFechaOtorgada() {
        return fechaOtorgada;
    }

    /**
     * @param fechaOtorgada the fechaOtorgada to set
     */
    public void setFechaOtorgada(Date fechaOtorgada) {
        this.fechaOtorgada = fechaOtorgada;
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
     * @return the fechaDeSolicitud
     */
    public Date getFechaDeSolicitud() {
        return fechaDeSolicitud;
    }

    /**
     * @param fechaDeSolicitud the fechaDeSolicitud to set
     */
    public void setFechaDeSolicitud(Date fechaDeSolicitud) {
        this.fechaDeSolicitud = fechaDeSolicitud;
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
     * @return the antDes
     */
    public String getAntDes() {
        return antDes;
    }

    /**
     * @param antDes the antDes to set
     */
    public void setAntDes(String antDes) {
        this.antDes = antDes;
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
     * @return the caducadaEmitida
     */
    public boolean isCaducadaEmitida() {
        return caducadaEmitida;
    }

    /**
     * @param caducadaEmitida the caducadaEmitida to set
     */
    public void setCaducadaEmitida(boolean caducadaEmitida) {
        this.caducadaEmitida = caducadaEmitida;
    }

    /**
     * @return the antecedente
     */
    public String getAntecedente() {
        return antecedente;
    }

    /**
     * @param antecedente the antecedente to set
     */
    public void setAntecedente(String antecedente) {
        this.antecedente = antecedente;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
}
