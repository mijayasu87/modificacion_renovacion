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
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
@Entity
@Table(name = "desistida")
public class Desistida implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "solicitud_senadi")
    private String solicitudSenadi;

    @Column(name = "iepi")
    private String iepi;

    @Column(name = "solicitud_no")
    private String solicitudNo;

    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;

    @Transient
    private String fechaPres;

    @Column(name = "total_folios_expediente")
    private String totalFoliosExpediente;

    @Column(name = "certificado_no")
    private Integer certificadoNo;

    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;

    @Column(name = "no_registro")
    private String registroNo;

    @Column(name = "fecha_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaRegistro;

    @Transient
    private String fechaRegPres;

    @Column(name = "fecha_vence_registro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVenceRegistro;

    @Transient
    private String fechaVenRegPres;

    @Column(name = "denominacion")
    private String denominacion;

    @Column(name = "signo")
    private String signo;

    @Column(name = "protege")
    private String protege;

    @Column(name = "titular_actual")
    private String titularActual;

    @Column(name = "ar")
    private String ar;

    @Column(name = "nj")
    private String nj;

    @Column(name = "tit_apod_repre")
    private String titApodRepre;

    @Column(name = "ape_apod_repre")
    private String apeApodRepre;

    @Column(name = "nom_apod_repre")
    private String nomApodRepre;

    @Column(name = "fecha_elabora_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaElaboraNotificacion;

    @Column(name = "fecha_notifica")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotifica;

    @Column(name = "casillero_senadi")
    private String casilleroSenadi;

    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "ro")
    private String ro;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "fecha_desistida")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaDesistida;

    @Transient
    private String fechaDesistPres;

    @Column(name = "identificacion")
    private String Identificacion;

    @Transient
    private String rutaExpediente;

    @Transient
    private Integer idRenewalForm;
    
    @Column(name = "cancelado")
    private String cancelado;

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
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
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
     * @return the iepi
     */
    public String getIepi() {
        return iepi;
    }

    /**
     * @param iepi the iepi to set
     */
    public void setIepi(String iepi) {
        this.iepi = iepi;
    }

    /**
     * @return the solicitudNo
     */
    public String getSolicitudNo() {
        return solicitudNo;
    }

    /**
     * @param solicitudNo the solicitudNo to set
     */
    public void setSolicitudNo(String solicitudNo) {
        this.solicitudNo = solicitudNo;
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
     * @return the totalFoliosExpediente
     */
    public String getTotalFoliosExpediente() {
        return totalFoliosExpediente;
    }

    /**
     * @param totalFoliosExpediente the totalFoliosExpediente to set
     */
    public void setTotalFoliosExpediente(String totalFoliosExpediente) {
        this.totalFoliosExpediente = totalFoliosExpediente;
    }

    /**
     * @return the certificadoNo
     */
    public Integer getCertificadoNo() {
        return certificadoNo;
    }

    /**
     * @param certificadoNo the certificadoNo to set
     */
    public void setCertificadoNo(Integer certificadoNo) {
        this.certificadoNo = certificadoNo;
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
     * @return the fechaVenceRegistro
     */
    public Date getFechaVenceRegistro() {
        return fechaVenceRegistro;
    }

    /**
     * @param fechaVenceRegistro the fechaVenceRegistro to set
     */
    public void setFechaVenceRegistro(Date fechaVenceRegistro) {
        this.fechaVenceRegistro = fechaVenceRegistro;
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
     * @return the protege
     */
    public String getProtege() {
        return protege;
    }

    /**
     * @param protege the protege to set
     */
    public void setProtege(String protege) {
        this.protege = protege;
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
     * @return the ar
     */
    public String getAr() {
        return ar;
    }

    /**
     * @param ar the ar to set
     */
    public void setAr(String ar) {
        this.ar = ar;
    }

    /**
     * @return the nj
     */
    public String getNj() {
        return nj;
    }

    /**
     * @param nj the nj to set
     */
    public void setNj(String nj) {
        this.nj = nj;
    }

    /**
     * @return the titApodRepre
     */
    public String getTitApodRepre() {
        return titApodRepre;
    }

    /**
     * @param titApodRepre the titApodRepre to set
     */
    public void setTitApodRepre(String titApodRepre) {
        this.titApodRepre = titApodRepre;
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
     * @return the nomApodRepre
     */
    public String getNomApodRepre() {
        return nomApodRepre;
    }

    /**
     * @param nomApodRepre the nomApodRepre to set
     */
    public void setNomApodRepre(String nomApodRepre) {
        this.nomApodRepre = nomApodRepre;
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
     * @return the fechaNotifica
     */
    public Date getFechaNotifica() {
        return fechaNotifica;
    }

    /**
     * @param fechaNotifica the fechaNotifica to set
     */
    public void setFechaNotifica(Date fechaNotifica) {
        this.fechaNotifica = fechaNotifica;
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
     * @return the fechaDesistida
     */
    public Date getFechaDesistida() {
        return fechaDesistida;
    }

    /**
     * @param fechaDesistida the fechaDesistida to set
     */
    public void setFechaDesistida(Date fechaDesistida) {
        this.fechaDesistida = fechaDesistida;
    }

    /**
     * @return the fechaPres
     */
    public String getFechaPres() {
        if (getFechaPresentacion() != null) {
            return Operaciones.formatDate(getFechaPresentacion());
        } else {
            return "--";
        }

    }

    /**
     * @param fechaPres the fechaPres to set
     */
    public void setFechaPres(String fechaPres) {
        this.fechaPres = fechaPres;
    }

    /**
     * @return the fechaRegPres
     */
    public String getFechaRegPres() {
        if (getFechaRegistro() != null) {
            return Operaciones.formatDate(getFechaRegistro());
        } else {
            return "--";
        }

    }

    /**
     * @param fechaRegPres the fechaRegPres to set
     */
    public void setFechaRegPres(String fechaRegPres) {
        this.fechaRegPres = fechaRegPres;
    }

    /**
     * @return the fechaVenRegPres
     */
    public String getFechaVenRegPres() {
        if (getFechaVenceRegistro() != null) {
            return Operaciones.formatDate(getFechaVenceRegistro());
        } else {
            return "--";
        }

    }

    /**
     * @param fechaVenRegPres the fechaVenRegPres to set
     */
    public void setFechaVenRegPres(String fechaVenRegPres) {
        this.fechaVenRegPres = fechaVenRegPres;
    }

    /**
     * @return the fechaDesistPres
     */
    public String getFechaDesistPres() {
        if (getFechaDesistida() != null) {
            return Operaciones.formatDate(getFechaDesistida());
        } else {
            return "--";
        }

    }

    /**
     * @param fechaDesistPres the fechaDesistPres to set
     */
    public void setFechaDesistPres(String fechaDesistPres) {
        this.fechaDesistPres = fechaDesistPres;
    }

    /**
     * @return the Identificacion
     */
    public String getIdentificacion() {
        return Identificacion;
    }

    /**
     * @param Identificacion the Identificacion to set
     */
    public void setIdentificacion(String Identificacion) {
        this.Identificacion = Identificacion;
    }

    /**
     * @return the rutaExpediente
     */
    public String getRutaExpediente() {
        return "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/"+getIdRenewalForm()+"/pdf_renewalfrm_"+getIdRenewalForm()+".pdf";
    }

    /**
     * @param rutaExpediente the rutaExpediente to set
     */
    public void setRutaExpediente(String rutaExpediente) {
        this.rutaExpediente = rutaExpediente;
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
}
