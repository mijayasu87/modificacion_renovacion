/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.daop;

import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author michael
 */
public class PpdiTituloDAO extends DAOAbstractP<PpdiTituloSignoDistintivo>{
    public PpdiTituloDAO(PpdiTituloSignoDistintivo p){
        super(p);
    }

    @Override
    public List<PpdiTituloSignoDistintivo> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p");
        return query.getResultList();
    }
    
    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByNumeroTitulo(String numeroTitulo){
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.numeroTitulo = '"+numeroTitulo+"'");
        if(query.getResultList().isEmpty()){
            return new PpdiTituloSignoDistintivo();
        }else{
            return (PpdiTituloSignoDistintivo) query.getResultList().get(0);
        }        
    }
    
    public PpdiTituloSignoDistintivo getPpdiTituloSignoDistintivoByCodigoSolicitudSigno(Integer codigoSolicitudSigno){
        Query query = this.getEntityManager().createQuery("Select p from PpdiTituloSignoDistintivo p where p.codigoSolicitudSigno = "+codigoSolicitudSigno);
        if(query.getResultList().isEmpty()){
            return new PpdiTituloSignoDistintivo();
        }else{
            return (PpdiTituloSignoDistintivo) query.getResultList().get(0);
        }
    }
    
    
}
