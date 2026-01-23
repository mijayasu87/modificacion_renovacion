/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Delegacion;

/**
 *
 * @author micharesp
 */
public class DelegacionDAO extends DAOAbstract<Delegacion> {

    public DelegacionDAO(Delegacion d) {
        super(d);
    }

    @Override
    public List<Delegacion> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select d from Delegacion d");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public boolean validarDelegacionActiva(){
        Query query = this.getEntityManager().createQuery("Select d from Delegacion d where d.activo = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public Delegacion getDelegacionActiva(){
        Query query = this.getEntityManager().createQuery("Select d from Delegacion d where d.activo = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return (Delegacion) query.getSingleResult();
    }
}
