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
@Table(name = "usuario_rol")
public class UsuarioRol implements Serializable{
    private static long serialVersionUID = 1L;

    @Id
    @Column(name = "usrol_id")
    private Integer id;
    
    @Column(name = "usrol_idusuario")
    private Integer usrol_idusuario;
    
    @Column(name = "usrol_idrol")
    private Integer usrol_idrol;

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
     * @return the usrol_idusuario
     */
    public Integer getUsrol_idusuario() {
        return usrol_idusuario;
    }

    /**
     * @param usrol_idusuario the usrol_idusuario to set
     */
    public void setUsrol_idusuario(Integer usrol_idusuario) {
        this.usrol_idusuario = usrol_idusuario;
    }

    /**
     * @return the usrol_idrol
     */
    public Integer getUsrol_idrol() {
        return usrol_idrol;
    }

    /**
     * @param usrol_idrol the usrol_idrol to set
     */
    public void setUsrol_idrol(Integer usrol_idrol) {
        this.usrol_idrol = usrol_idrol;
    }
}
