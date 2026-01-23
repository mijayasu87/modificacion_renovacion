/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Michael Yanangómez
 */
@Entity
@Table(name = "historial")
public class Historial implements Serializable{
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "solicitud_senadi")
    private String solicitudSenadi;
    
    @Column(name = "estado_actual")
    private String estadoActual;
    
    @Column(name = "estado_anterior")
    private String estadoAnterior;
    
    @Column(name = "accion")
    private String accion;
    
    @Column(name = "fecha_modificacion")
    private String fechaModificacion;
    
    @Column(name = "autenticacion_usu_id")    
    private Integer userId;
    
    @Column(name = "usuario")    
    private String usuario;

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
     * @return the estadoActual
     */
    public String getEstadoActual() {
        return estadoActual;
    }

    /**
     * @param estadoActual the estadoActual to set
     */
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * @return the estadoAnterior
     */
    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    /**
     * @param estadoAnterior the estadoAnterior to set
     */
    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    /**
     * @return the fechaModificacion
     */
    public String getFechaModificacion() {
        return fechaModificacion;
    }

    /**
     * @param fechaModificacion the fechaModificacion to set
     */
    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString(){
        return "- F. Modificación: "+getFechaModificacion().substring(0,getFechaModificacion().length()-2)+
                ", Estado anterior: "+getEstadoAnterior()+", Acción: "+getAccion()+", Estado actual: "+getEstadoActual()+". ["+getUsuario()+"]";
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
     * @return the accion
     */
    public String getAccion() {
        return accion;
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }
}
