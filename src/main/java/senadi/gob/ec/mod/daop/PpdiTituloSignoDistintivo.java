/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.daop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "ppdi_titulo_signo_distintivo", schema = "iepi_procesos")
public class PpdiTituloSignoDistintivo implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "codigo_titulo_signo_distintivo")
    private Integer codigoTituloSignoDistintivo;

    @Column(name = "codigo_solicitud_signo")
    private Integer codigoSolicitudSigno;

    @Column(name = "numero_titulo")
    private String numeroTitulo;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_emision_documento")
    private Date fechaEmisionDocumento;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "fecha_vencimiento_titulo")
    private Date fechaVencimientoTitulo;

    @Column(name = "usuario_elabora")
    private String usuarioElabora;

    @Column(name = "titular")
    private String titular;

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
     * @return the codigoTituloSignoDistintivo
     */
    public Integer getCodigoTituloSignoDistintivo() {
        return codigoTituloSignoDistintivo;
    }

    /**
     * @param codigoTituloSignoDistintivo the codigoTituloSignoDistintivo to set
     */
    public void setCodigoTituloSignoDistintivo(Integer codigoTituloSignoDistintivo) {
        this.codigoTituloSignoDistintivo = codigoTituloSignoDistintivo;
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
     * @return the fechaEmisionDocumento
     */
    public Date getFechaEmisionDocumento() {
        return fechaEmisionDocumento;
    }

    /**
     * @param fechaEmisionDocumento the fechaEmisionDocumento to set
     */
    public void setFechaEmisionDocumento(Date fechaEmisionDocumento) {
        this.fechaEmisionDocumento = fechaEmisionDocumento;
    }

    /**
     * @return the fechaVencimientoTitulo
     */
    public Date getFechaVencimientoTitulo() {
        return fechaVencimientoTitulo;
    }

    /**
     * @param fechaVencimientoTitulo the fechaVencimientoTitulo to set
     */
    public void setFechaVencimientoTitulo(Date fechaVencimientoTitulo) {
        this.fechaVencimientoTitulo = fechaVencimientoTitulo;
    }

    /**
     * @return the usuarioElabora
     */
    public String getUsuarioElabora() {
        return usuarioElabora;
    }

    /**
     * @param usuarioElabora the usuarioElabora to set
     */
    public void setUsuarioElabora(String usuarioElabora) {
        this.usuarioElabora = usuarioElabora;
    }

    /**
     * @return the titular
     */
    public String getTitular() {
        return titular;
    }

    /**
     * @param titular the titular to set
     */
    public void setTitular(String titular) {
        this.titular = titular;
    }

    @Override
    public String toString() {
        return getNumeroTitulo() + ", " + getTitular() + ", " + Operaciones.formatDate(getFechaEmisionDocumento());
    }
}
