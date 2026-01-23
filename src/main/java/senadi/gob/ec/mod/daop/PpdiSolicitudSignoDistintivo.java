/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.daop;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import senadi.gob.ec.mod.ucc.Operaciones;


/**
 *
 * @author Michael Yanangómez
 */
@Entity
@Table(name = "ppdi_solicitud_signo_distintivo", schema = "iepi_procesos")
public class PpdiSolicitudSignoDistintivo implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_solicitud_signo")
    private Integer codigoSolicitudSigno;

    @Column(name = "numero_expediente")
    private String numeroExpediente;

    @Column(name = "numero_tramite")
    private String numeroTramite;

    @Column(name = "denominacion_signo")
    private String denominacionSigno;

    @Column(name = "descripcion_signo")
    private String descripcionSigno;

    @Column(name = "codigo_tipo_derecho")
    private Integer codigoTipoDerecho;

    @Column(name = "codigo_pais")
    private Integer codigoPais;   

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_presentacion")
    private Date fechaPresentacion;

    @Column(name = "casillero_iepi")
    private String casilleroIepi;

    @Column(name = "estado")
    private String estado;

    @Temporal(TemporalType.DATE)
    @Column(name = "prio_fecha")
    private Date prioFecha;

    @Column(name = "clasificacion_internacional")
    private String clasificacionInternacional;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_expediente")
    private Date fechaExpediente;

    @Column(name = "bonita")
    private String bonita;

    @Column(name = "numero_gaceta")
    private String gaceta;

    @Column(name = "codigo_clasificacion_niza")
    private Integer clasificacion_niza;

    @Column(name = "codigo_catalogo_viena")
    private Integer codigoCatalogoViena;

    @Column(name = "codigo_tasa")
    private Integer codigoTasa;

    @Column(name = "estado_gaceta")
    private String estadoGaceta;

    @Column(name = "codigo_naturaleza_signo")
    private Integer codigoNaturalezaSigno;

    @Column(name = "observacion")
    private String observacion;
    
    @Transient
    private String observacionText;

    @Column(name = "codigo_unidad")
    private Integer codigoUnidad;

    @Column(name = "id_comprobante_deposito")
    private Integer idComprobanteDeposito;

    @Column(name = "draft")
    private String draft;

    @Column(name = "zona")
    private String zona;

    @Column(name = "etapa_tramite")
    private Integer etapaTramite;

    @Column(name = "fecha_terminacion_gaceta")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaTerminacionGaceta;

    @Transient
    private String fechaPresentacionText;

    @Transient
    private String fechaTerminacionGacetaText;

    @Transient
    private String webscript;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the codigoSolicitudSigno
     */
    public Integer getCodigoSolicitudSigno() {
        return codigoSolicitudSigno;
    }

    /**
     * @param codigoSolicitudSigno the codigoSolicitudSigno to set
     */
    public void setCodigoSolicitudSigno(Integer codigoSolicitudSigno) {
        this.codigoSolicitudSigno = codigoSolicitudSigno;
    }

    /**
     * @return the numeroExpediente
     */
    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    /**
     * @param numeroExpediente the numeroExpediente to set
     */
    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    /**
     * @return the numeroTramite
     */
    public String getNumeroTramite() {
        return numeroTramite;
    }

    /**
     * @param numeroTramite the numeroTramite to set
     */
    public void setNumeroTramite(String numeroTramite) {
        this.numeroTramite = numeroTramite;
    }

    /**
     * @return the denominacionSigno
     */
    public String getDenominacionSigno() {
        return denominacionSigno;
    }

    /**
     * @param denominacionSigno the denominacionSigno to set
     */
    public void setDenominacionSigno(String denominacionSigno) {
        this.denominacionSigno = denominacionSigno;
    }

    /**
     * @return the descripcionSigno
     */
    public String getDescripcionSigno() {
        return descripcionSigno;
    }

    /**
     * @param descripcionSigno the descripcionSigno to set
     */
    public void setDescripcionSigno(String descripcionSigno) {
        this.descripcionSigno = descripcionSigno;
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
     * @return the casilleroIepi
     */
    public String getCasilleroIepi() {
        return casilleroIepi;
    }

    /**
     * @param casilleroIepi the casilleroIepi to set
     */
    public void setCasilleroIepi(String casilleroIepi) {
        this.casilleroIepi = casilleroIepi;
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
     * @return the prioFecha
     */
    public Date getPrioFecha() {
        return prioFecha;
    }

    /**
     * @param prioFecha the prioFecha to set
     */
    public void setPrioFecha(Date prioFecha) {
        this.prioFecha = prioFecha;
    }

    /**
     * @return the clasificacionInternacional
     */
    public String getClasificacionInternacional() {
        return clasificacionInternacional;
    }

    /**
     * @param clasificacionInternacional the clasificacionInternacional to set
     */
    public void setClasificacionInternacional(String clasificacionInternacional) {
        this.clasificacionInternacional = clasificacionInternacional;
    }

    /**
     * @return the fechaExpediente
     */
    public Date getFechaExpediente() {
        return fechaExpediente;
    }

    /**
     * @param fechaExpediente the fechaExpediente to set
     */
    public void setFechaExpediente(Date fechaExpediente) {
        this.fechaExpediente = fechaExpediente;
    }

    @Override
    public String toString() {
        return getNumeroTramite() + ", " + getDenominacionSigno() + ", " + getFechaPresentacion().toString() + "\n";
    }

    /**
     * @return the bonita
     */
    public String getBonita() {
        return bonita;
    }

    /**
     * @param bonita the bonita to set
     */
    public void setBonita(String bonita) {
        this.bonita = bonita;
    }

    /**
     * @return the fechaPresentacionText
     */
    public String getFechaPresentacionText() {
        return getFechaPresentacion() != null ? Operaciones.formatDate(getFechaPresentacion()) : "Sin fecha";
    }

    /**
     * @param fechaPresentacionText the fechaPresentacionText to set
     */
    public void setFechaPresentacionText(String fechaPresentacionText) {
        this.fechaPresentacionText = fechaPresentacionText;
    }

    /**
     * @return the gaceta
     */
    public String getGaceta() {
        return gaceta;
    }

    /**
     * @param gaceta the gaceta to set
     */
    public void setGaceta(String gaceta) {
        this.gaceta = gaceta;
    }

    /**
     * @return the clasificacion_niza
     */
    public Integer getClasificacion_niza() {
        return clasificacion_niza;
    }

    /**
     * @param clasificacion_niza the clasificacion_niza to set
     */
    public void setClasificacion_niza(Integer clasificacion_niza) {
        this.clasificacion_niza = clasificacion_niza;
    }

    /**
     * @return the fechaTerminacionGaceta
     */
    public Date getFechaTerminacionGaceta() {
        return fechaTerminacionGaceta;
    }

    /**
     * @param fechaTerminacionGaceta the fechaTerminacionGaceta to set
     */
    public void setFechaTerminacionGaceta(Date fechaTerminacionGaceta) {
        this.fechaTerminacionGaceta = fechaTerminacionGaceta;
    }

    /**
     * @return the fechaTerminacionGacetaText
     */
    public String getFechaTerminacionGacetaText() {
        return getFechaTerminacionGaceta() != null ? Operaciones.formatDate(getFechaTerminacionGaceta()) : "Sin fecha";
    }

    /**
     * @param fechaTerminacionGacetaText the fechaTerminacionGacetaText to set
     */
    public void setFechaTerminacionGacetaText(String fechaTerminacionGacetaText) {
        this.fechaTerminacionGacetaText = fechaTerminacionGacetaText;
    }

    /**
     * @return the codigoTipoDerecho
     */
    public Integer getCodigoTipoDerecho() {
        return codigoTipoDerecho;
    }

    /**
     * @param codigoTipoDerecho the codigoTipoDerecho to set
     */
    public void setCodigoTipoDerecho(Integer codigoTipoDerecho) {
        this.codigoTipoDerecho = codigoTipoDerecho;
    }

    /**
     * @return the codigoCatalogoViena
     */
    public Integer getCodigoCatalogoViena() {
        return codigoCatalogoViena;
    }

    /**
     * @param codigoCatalogoViena the codigoCatalogoViena to set
     */
    public void setCodigoCatalogoViena(Integer codigoCatalogoViena) {
        this.codigoCatalogoViena = codigoCatalogoViena;
    }

    /**
     * @return the codigoTasa
     */
    public Integer getCodigoTasa() {
        return codigoTasa;
    }

    /**
     * @param codigoTasa the codigoTasa to set
     */
    public void setCodigoTasa(Integer codigoTasa) {
        this.codigoTasa = codigoTasa;
    }

    /**
     * @return the estadoGaceta
     */
    public String getEstadoGaceta() {
        return estadoGaceta;
    }

    /**
     * @param estadoGaceta the estadoGaceta to set
     */
    public void setEstadoGaceta(String estadoGaceta) {
        this.estadoGaceta = estadoGaceta;
    }

    /**
     * @return the codigoNaturalezaSigno
     */
    public Integer getCodigoNaturalezaSigno() {
        return codigoNaturalezaSigno;
    }

    /**
     * @param codigoNaturalezaSigno the codigoNaturalezaSigno to set
     */
    public void setCodigoNaturalezaSigno(Integer codigoNaturalezaSigno) {
        this.codigoNaturalezaSigno = codigoNaturalezaSigno;
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

    /**
     * @return the codigoUnidad
     */
    public Integer getCodigoUnidad() {
        return codigoUnidad;
    }

    /**
     * @param codigoUnidad the codigoUnidad to set
     */
    public void setCodigoUnidad(Integer codigoUnidad) {
        this.codigoUnidad = codigoUnidad;
    }

    /**
     * @return the idComprobanteDeposito
     */
    public Integer getIdComprobanteDeposito() {
        return idComprobanteDeposito;
    }

    /**
     * @param idComprobanteDeposito the idComprobanteDeposito to set
     */
    public void setIdComprobanteDeposito(Integer idComprobanteDeposito) {
        this.idComprobanteDeposito = idComprobanteDeposito;
    }

    /**
     * @return the draft
     */
    public String getDraft() {
        return draft;
    }

    /**
     * @param draft the draft to set
     */
    public void setDraft(String draft) {
        this.draft = draft;
    }

    /**
     * @return the zona
     */
    public String getZona() {
        return zona;
    }

    /**
     * @param zona the zona to set
     */
    public void setZona(String zona) {
        this.zona = zona;
    }

    /**
     * @return the etapaTramite
     */
    public Integer getEtapaTramite() {
        return etapaTramite;
    }

    /**
     * @param etapaTramite the etapaTramite to set
     */
    public void setEtapaTramite(Integer etapaTramite) {
        this.etapaTramite = etapaTramite;
    }

    /**
     * @return the codigoPais
     */
    public Integer getCodigoPais() {
        return codigoPais;
    }

    /**
     * @param codigoPais the codigoPais to set
     */
    public void setCodigoPais(Integer codigoPais) {
        this.codigoPais = codigoPais;
    }

    /**
     * @return the webscript
     */
    public String getWebscript() {
        //produccion
        //return "http://balanceo.iepi.gov.ec/alfresco/s/slingshot/zipContents?expediente=" + getNumeroExpediente();
        //intra
        return "http://10.0.20.57/alfresco/s/slingshot/zipContents?expediente=" + getNumeroExpediente();
    }

    /**
     * @param webscript the webscript to set
     */
    public void setWebscript(String webscript) {
        this.webscript = webscript;
    }

    /**
     * @return the observacionText
     */
    public String getObservacionText() {
        return getObservacion().substring("(usuario;".length(), getObservacion().indexOf(";"));
    }

    /**
     * @param observacionText the observacionText to set
     */
    public void setObservacionText(String observacionText) {
        this.observacionText = observacionText;
    }

}
