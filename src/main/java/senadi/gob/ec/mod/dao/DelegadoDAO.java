/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Delegado;

/**
 *
 * @author micharesp
 */
public class DelegadoDAO extends DAOAbstract<Delegado>{
    
    public DelegadoDAO(Delegado d){
        super(d);
    }

    @Override
    public List<Delegado> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select d from Delegado d");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public boolean validarDelegadoActivo(){
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.estado = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public Delegado getDelegadoActivo(){
        Query query = this.getEntityManager().createQuery("Select d from Delegado d where d.estado = true");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return (Delegado) query.getSingleResult();
    }
    
}
