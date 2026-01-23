/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author Michael Yanangómez
 */
public class NotificadaDAO extends DAOAbstract<Notificada>{
    public NotificadaDAO(Notificada n){
        super(n);
    }
    
    @Override
    public List<Notificada> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n From Notificada n ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();
    }
    
    public List<String> getNotificadasNotificacionEmitido() {
        System.out.println("---------------------Empezando el reconocimiento notificadas------------------");
        String sql = "Select DISTINCT(r.solicitud_senadi) from "
                + "notificada as r "
                + "left join notificacion_casillero as n on r.solicitud_senadi = n.solicitud "
                + "where n.estado_notificacion = 1 and n.documento like '%notifi%' ";

        Query query = this.getEntityManager().createNativeQuery(sql);
        List<String> notificadas = query.getResultList();

        System.out.println("--------------------Terminado el reconocimiento notificadas-----------------");
        System.out.println("Trámites encontrados notificadas: " + notificadas.size());
        return notificadas;
    }    
    
    public List<Notificada> getNotificadasCriteria(String text){
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.solicitud LIKE '%"+text+"%' or n.denominacion LIKE '%"+text+"%' or n.titularActual LIKE '%"+text+"%' ORDER BY n.id DESC" );
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.setMaxResults(300).getResultList();                
    }
    
    /****Esperando a ver como es la funcionalidad**************/
    public int getNextNumeroNotificacion(Date fechaElaboraNotificacion){
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.id = (Select MAX(n1.id) from Notificada n1)");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        
        Notificada n = (Notificada) query.getSingleResult();
        
        int yearElNot = fechaElaboraNotificacion.getYear() + 1900;
        int yearNot = n.getFechaElaboraNotificacion().getYear() + 1900;
        
        if(yearElNot == yearNot){
            return n.getNotificacion()+1;
        }else if(yearElNot > yearNot){
            return 1;
        }else{
            return -1;
        }       
    }

    public List<Notificada> getNotificadasBySolSenadi(String sol_senadi) {
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.solicitud = '"+sol_senadi+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public Notificada getNotificadaBySolSenadi(String senadi) {
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.solicitud = '"+senadi+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        List<Notificada> notificadas = query.getResultList();
        if(notificadas.isEmpty()){
            return new Notificada();
        }else{
            return notificadas.get(0);
        }
    }
    
    public boolean validarExistenciaNotificada(String solicitudSenadi){
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.solicitud = '"+solicitudSenadi+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean validarExistenciaNot(Notificada not){
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.solicitud = '"+not.getSolicitud()+"' and n.id != "+not.getId());
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        if(query.getResultList().isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public List<Notificada> getNotificadasByDate(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.fechaPresentacion BETWEEN '"+start+"' and '"+end+"' "
                + "or n.fechaElaboraNotificacion BETWEEN '"+start+"' and '"+end+"'");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
    
    public List<Notificada> getNotificadasByFechaNotificacion(Date ini, Date fin) {
        String start = Operaciones.formatDate(ini);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Notificada n where n.fechaElaboraNotificacion BETWEEN '"+start+"' and '"+end+"' order by n.fechaElaboraNotificacion desc");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }
}
