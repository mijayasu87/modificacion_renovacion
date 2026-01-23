/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author micharesp
 */
public class SecretarioDAO extends DAOAbstract<Secretario>{
    
    public SecretarioDAO(Secretario r){
        super(r);
    }

    @Override
    public List<Secretario> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select r from Secretario r");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }   

    public boolean validarSecretarioActiva() {
        Query query = this.getEntityManager().createQuery("Select d from Secretario d where d.estado = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public Secretario getSecretarioActiva(){
        Query query = this.getEntityManager().createQuery("Select d from Secretario d where d.estado = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return (Secretario) query.getSingleResult();
    }
    
    public List<Secretario> getSecretarios(){
        Query query = this.getEntityManager().createQuery("Select s from Secretario s");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
}
