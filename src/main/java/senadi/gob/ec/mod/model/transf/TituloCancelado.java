/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.model.transf;

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
@Table(name = "titulo_cancelado")
public class TituloCancelado implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "numero_titulo")
    private String numeroTitulo;
    
    @Column(name = "numero_tramite")
    private String numeroTramite;
    
    @Column(name = "expediente")
    private String expediente;
    
    @Column(name = "fecha_expediente")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaExpediente;
    
    @Column(name = "denominacion")   
    private String denominacion;
    
    @Column(name = "tipo_cancelacion")
    private String tipoCancelacion;
    
    @Column(name = "fecha_cancelacion")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCancelacion;
    
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
     * @return the numeroTitulo
     */
    public String getNumeroTitulo() {
        return numeroTitulo;
    }

    /**
     * @param numeroTitulo the numeroTitulo to set
     */
    public void setNumeroTitulo(String numeroTitulo) {
        this.numeroTitulo = numeroTitulo;
    }

    /**
     * @return the expediente
     */
    public String getExpediente() {
        return expediente;
    }

    /**
     * @param expediente the expediente to set
     */
    public void setExpediente(String expediente) {
        this.expediente = expediente;
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
     * @return the tipoCancelacion
     */
    public String getTipoCancelacion() {
        return tipoCancelacion;
    }

    /**
     * @param tipoCancelacion the tipoCancelacion to set
     */
    public void setTipoCancelacion(String tipoCancelacion) {
        this.tipoCancelacion = tipoCancelacion;
    }

    /**
     * @return the fechaCancelacion
     */
    public Date getFechaCancelacion() {
        return fechaCancelacion;
    }

    /**
     * @param fechaCancelacion the fechaCancelacion to set
     */
    public void setFechaCancelacion(Date fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
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
    
    @Override
    public String toString(){
        return getNumeroTitulo()+", "+getExpediente()+", "+getUsuario();
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
    
}
