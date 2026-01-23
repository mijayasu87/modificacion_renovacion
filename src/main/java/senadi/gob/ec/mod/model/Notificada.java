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
@Table(name = "notificada")
public class Notificada implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo_solicitante")
    private String tipoSolicitante;

    @Column(name = "solicitud_senadi")
    private String solicitud;

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

    @Column(name = "notificacion")
    private Integer notificacion;

    @Column(name = "fecha_certificado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCertificado;

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

    @Column(name = "domicilio_titular_ac")
    private String domicilioTitularAc;

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

    @Column(name = "providencia")
    private String providencia;

    @Column(name = "fecha_providencia")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaProvidencia;

    @Column(name = "fecha_notifica_pro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificaPro;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "r1")
    private String r1;

    @Column(name = "r2")
    private String r2;

    @Column(name = "r3")
    private String r3;

    @Column(name = "r4")
    private String r4;

    @Column(name = "r5")
    private String r5;

    @Column(name = "r6")
    private String r6;

    @Column(name = "identificacion")
    private String identificacion;

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

    @Column(name = "solicitante")
    private String solicitante;

    @Column(name = "tipo_abandono")
    private String tipoAbandono;

    @Column(name = "fecha_puesta_abandono")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaPuestaAbandono;

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
     * @return the tipoSolicitante
     */
    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    /**
     * @param tipoSolicitante the tipoSolicitante to set
     */
    public void setTipoSolicitante(String tipoSolicitante) {
        this.tipoSolicitante = tipoSolicitante;
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
     * @return the domicilioTitularAc
     */
    public String getDomicilioTitularAc() {
        return domicilioTitularAc;
    }

    /**
     * @param domicilioTitularAc the domicilioTitularAc to set
     */
    public void setDomicilioTitularAc(String domicilioTitularAc) {
        this.domicilioTitularAc = domicilioTitularAc;
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
     * @return the providencia
     */
    public String getProvidencia() {
        return providencia;
    }

    /**
     * @param providencia the providencia to set
     */
    public void setProvidencia(String providencia) {
        this.providencia = providencia;
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
     * @return the fechaNotificaPro
     */
    public Date getFechaNotificaPro() {
        return fechaNotificaPro;
    }

    /**
     * @param fechaNotificaPro the fechaNotificaPro to set
     */
    public void setFechaNotificaPro(Date fechaNotificaPro) {
        this.fechaNotificaPro = fechaNotificaPro;
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

    @Override
    public String toString() {
        return getSolicitud() + " - " + Operaciones.formatDate(getFechaPresentacion());
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
     * @return the r2
     */
    public String getR2() {
        return r2;
    }

    /**
     * @param r2 the r2 to set
     */
    public void setR2(String r2) {
        this.r2 = r2;
    }

    /**
     * @return the r3
     */
    public String getR3() {
        return r3;
    }

    /**
     * @param r3 the r3 to set
     */
    public void setR3(String r3) {
        this.r3 = r3;
    }

    /**
     * @return the r4
     */
    public String getR4() {
        return r4;
    }

    /**
     * @param r4 the r4 to set
     */
    public void setR4(String r4) {
        this.r4 = r4;
    }

    /**
     * @return the r5
     */
    public String getR5() {
        return r5;
    }

    /**
     * @param r5 the r5 to set
     */
    public void setR5(String r5) {
        this.r5 = r5;
    }

    /**
     * @return the r6
     */
    public String getR6() {
        return r6;
    }

    /**
     * @param r6 the r6 to set
     */
    public void setR6(String r6) {
        this.r6 = r6;
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
        return "https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/" + getIdRenewalForm() + "/";
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
     * @return the fechaPuestaAbandono
     */
    public Date getFechaPuestaAbandono() {
        return fechaPuestaAbandono;
    }

    /**
     * @param fechaPuestaAbandono the fechaPuestaAbandono to set
     */
    public void setFechaPuestaAbandono(Date fechaPuestaAbandono) {
        this.fechaPuestaAbandono = fechaPuestaAbandono;
    }
}
