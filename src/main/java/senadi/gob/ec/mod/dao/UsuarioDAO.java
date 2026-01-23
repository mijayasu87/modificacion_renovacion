/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Usuario;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
public class UsuarioDAO extends DAOAbstractAut<Usuario>{
    public UsuarioDAO(Usuario u){
        super(u);
    }

    @Override
    public List<Usuario> buscarTodos() {
        Query query = this.getEntityManager().createQuery("SELECT u FROM Usuario u");
        return query.getResultList();
    }
    
    public boolean existsUser(String login, String clave){
        Query query = this.getEntityManager().createQuery("Select u from Usuario u where u.login = '"+login+"' and u.password = '"+Operaciones.md5(clave)+"'");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }                
    }
    
    public Usuario getUsuario(String login, String clave){
        Query query = this.getEntityManager().createQuery("Select u from Usuario u where u.login = '"+login+"' and u.password = '"+Operaciones.md5(clave)+"'");
        return (Usuario) query.getSingleResult();
    }
    
    public boolean getUsuarioRol(int idRol, int idUsuario){
        Query query = this.getEntityManager().createQuery("Select u from UsuarioRol u where u.usrol_idrol = "+idRol+" and u.usrol_idusuario = "+idUsuario);
        if(query.getResultList().isEmpty()){
            return false;            
        }else{
            return true;
        }
    }
}
