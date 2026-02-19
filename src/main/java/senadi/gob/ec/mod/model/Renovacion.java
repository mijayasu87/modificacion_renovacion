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
@Table(name = "renovacion")
public class Renovacion implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "solicitud_senadi")
    private String solicitudSenadi;

    @Column(name = "fecha_presentacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPresentacion;

    @Transient
    private String fechaPres;

    @Column(name = "no_comprobante_present_solic")
    private String noComprobantePresentSolic;

    @Column(name = "no_comprobante_emision_cert")
    private String noComprobanteEmisionCert;

    @Column(name = "total_folios_expediente")
    private String totalFoliosExpediente;

    @Column(name = "certificado_no")
    private Integer certificadoNo;

    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;

    @Transient
    private String fechaCertPres;

    @Column(name = "titulo_resolucion")
    private String tituloResolucion;

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

    @Column(name = "lema")
    private String lema;

    @Column(name = "signo")
    private String signo;

    @Column(name = "clase")
    private String clase;

    @Column(name = "protege")
    private String protege;

    @Column(name = "titular_actual")
    private String titularActual;

    @Column(name = "tacnj")
    private String tacNJ;

    @Column(name = "nac_titular_ac")
    private String nacTitularAc;

    @Column(name = "abogado_patrocinador_ape_apo_repre")
    private String abogadoPatrocinadorApeApoRepre;

    @Column(name = "casillero_senadi")
    private String casilleroSenadi;

    @Column(name = "casillero_judicial")
    private String casilleroJudicial;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "identificacion")
    private String identificacion;

    @Column(name = "cargado")
    private boolean cargado;
    
    @Column(name = "observacion")
    private String observacion;

    @Transient
    private String rutaExpediente;

    @Transient
    private Integer idRenewalForm;

    @Column(name = "notificacion_emitida")
    private boolean notificacionEmitida;

    @Column(name = "certificado_emitido")
    private boolean certificadoEmitido;
    
    @Column(name = "cancelado")
    private String cancelado;

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
     * @return the noComprobantePresentSolic
     */
    public String getNoComprobantePresentSolic() {
        return noComprobantePresentSolic;
    }

    /**
     * @param noComprobantePresentSolic the noComprobantePresentSolic to set
     */
    public void setNoComprobantePresentSolic(String noComprobantePresentSolic) {
        this.noComprobantePresentSolic = noComprobantePresentSolic;
    }

    /**
     * @return the noComprobanteEmisionCert
     */
    public String getNoComprobanteEmisionCert() {
        return noComprobanteEmisionCert;
    }

    /**
     * @param noComprobanteEmisionCert the noComprobanteEmisionCert to set
     */
    public void setNoComprobanteEmisionCert(String noComprobanteEmisionCert) {
        this.noComprobanteEmisionCert = noComprobanteEmisionCert;
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
     * @return the lema
     */
    public String getLema() {
        return lema;
    }

    /**
     * @param lema the lema to set
     */
    public void setLema(String lema) {
        this.lema = lema;
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
     * @return the clase
     */
    public String getClase() {
        return clase;
    }

    /**
     * @param clase the clase to set
     */
    public void setClase(String clase) {
        this.clase = clase;
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
     * @return the tacNJ
     */
    public String getTacNJ() {
        return tacNJ;
    }

    /**
     * @param tacNJ the tacNJ to set
     */
    public void setTacNJ(String tacNJ) {
        this.tacNJ = tacNJ;
    }

    /**
     * @return the nacTitularAc
     */
    public String getNacTitularAc() {
        return nacTitularAc;
    }

    /**
     * @param nacTitularAc the nacTitularAc to set
     */
    public void setNacTitularAc(String nacTitularAc) {
        this.nacTitularAc = nacTitularAc;
    }

    /**
     * @return the abogadoPatrocinadorApeApoRepre
     */
    public String getAbogadoPatrocinadorApeApoRepre() {
        return abogadoPatrocinadorApeApoRepre;
    }

    /**
     * @param abogadoPatrocinadorApeApoRepre the abogadoPatrocinadorApeApoRepre
     * to set
     */
    public void setAbogadoPatrocinadorApeApoRepre(String abogadoPatrocinadorApeApoRepre) {
        this.abogadoPatrocinadorApeApoRepre = abogadoPatrocinadorApeApoRepre;
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

    @Override
    public String toString() {
        return "Renovación: " + getSolicitudSenadi() + ", " + Operaciones.formatDate(getFechaPresentacion()) + "\n";
    }

    /**
     * @return the tituloResolucion
     */
    public String getTituloResolucion() {
        return tituloResolucion;
    }

    /**
     * @param tituloResolucion the tituloResolucion to set
     */
    public void setTituloResolucion(String tituloResolucion) {
        this.tituloResolucion = tituloResolucion;
    }

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
     * @return the fechaCertPres
     */
    public String getFechaCertPres() {
        if (getFechaCertificado() != null) {
            return Operaciones.formatDate(getFechaCertificado());
        } else {
            return "--";
        }

    }

    /**
     * @param fechaCertPres the fechaCertPres to set
     */
    public void setFechaCertPres(String fechaCertPres) {
        this.fechaCertPres = fechaCertPres;
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
     * @return the rutaExpediente
     */
    public String getRutaExpediente() {
        return "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/" + getIdRenewalForm() + "/pdf_renewalfrm_" + getIdRenewalForm() + ".pdf";
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
     * @return the cargado
     */
    public boolean isCargado() {
        return cargado;
    }

    /**
     * @param cargado the cargado to set
     */
    public void setCargado(boolean cargado) {
        this.cargado = cargado;
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
