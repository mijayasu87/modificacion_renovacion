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

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "notificacion_casillero")
public class UploadNotificacion implements Serializable{
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "renewal_form_id")
    private Integer renewalFormId;
    
    @Column(name = "documento")
    private String documento;
    
    @Column(name = "solicitud")
    private String solicitud;
        
    @Column(name = "casillero")
    private Integer casillero;
    
    @Column(name = "estado_notificacion")
    private boolean estado;
    
    @Column(name = "fecha_notificacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaNotificacion;
    
    @Column(name = "observacion")
    private String observacion;
    
    @Column(name = "usuario")
    private String usuario;
    
    @Column(name = "activo")
    private boolean activo;
    
    @Column(name = "notifications_id")
    private Integer notificationsId;

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the casillero
     */
    public Integer getCasillero() {
        return casillero;
    }

    /**
     * @param casillero the casillero to set
     */
    public void setCasillero(Integer casillero) {
        this.casillero = casillero;
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
     * @return the renewalFormId
     */
    public Integer getRenewalFormId() {
        return renewalFormId;
    }

    /**
     * @param renewalFormId the renewalFormId to set
     */
    public void setRenewalFormId(Integer renewalFormId) {
        this.renewalFormId = renewalFormId;
    }

    /**
     * @return the estado
     */
    public boolean isEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(boolean estado) {
        this.estado = estado;
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
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return the notificationsId
     */
    public Integer getNotificationsId() {
        return notificationsId;
    }

    /**
     * @param notificationsId the notificationsId to set
     */
    public void setNotificationsId(Integer notificationsId) {
        this.notificationsId = notificationsId;
    }

}
