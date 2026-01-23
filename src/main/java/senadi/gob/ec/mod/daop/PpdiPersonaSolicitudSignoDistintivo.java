/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.daop;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author micharesp
 */
@Entity
@Table(name = "ppdi_persona_solicitud_signo_distintivo", schema = "iepi_procesos")
public class PpdiPersonaSolicitudSignoDistintivo implements Serializable{
    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_solicitud_signo")
    private Integer codigoSolicitudSigno;

    @Column(name = "codigo_persona")
    private Integer codigoPersona;

    @Column(name = "tipo_persona")
    private String tipoPersona;

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
     * @return the codigoPersona
     */
    public Integer getCodigoPersona() {
        return codigoPersona;
    }

    /**
     * @param codigoPersona the codigoPersona to set
     */
    public void setCodigoPersona(Integer codigoPersona) {
        this.codigoPersona = codigoPersona;
    }

    /**
     * @return the tipoPersona
     */
    public String getTipoPersona() {
        return tipoPersona;
    }

    /**
     * @param tipoPersona the tipoPersona to set
     */
    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

}
