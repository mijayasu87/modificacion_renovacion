/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
public class AbandonoDAO extends DAOAbstract<Abandono> {

    public AbandonoDAO(Abandono r) {
        super(r);
    }

    @Override
    public List<Abandono> buscarTodos() {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n ORDER BY n.id DESC");
        return query.getResultList();
    }

    public List<Abandono> getAbandonoByCriteria(String text) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud LIKE '%" + text + "%' or n.denominacion LIKE '%" + text + "%' "
                + "or n.titularActual LIKE '%" + text + "%' ORDER BY n.id DESC");
        return query.getResultList();
    }

    public List<Abandono> getAbandonoByFecha(Date inicio, Date fin) {
        String start = Operaciones.formatDate(inicio);
        String end = Operaciones.formatDate(fin);
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.fechaPresentacion between '" + start + "' and '" + end + "' "
                + "or n.fechaCertificado between '" + start + "' and '" + end + "' or n.fechaRegistro between '" + start + "' and '" + end + "' ORDER BY n.id DESC");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public int getNextNumeroAbandono(Date fechaAbandono) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.id = (Select MAX(n1.id) from Abandono n1)");

        List<Abandono> abandonos = query.getResultList();
        if (abandonos.isEmpty()) {
            return 1;
        } else {
            Abandono abandono = abandonos.get(0);

            int yearElNot = fechaAbandono.getYear() + 1900;
            int yearNot = abandono.getFechaAbandono().getYear() + 1900;

            if (yearElNot == yearNot) {

                int nextabandono = abandono.getNumeroAbandono() + 1;
                System.out.println("asignación de número de abandono " + nextabandono);
                return nextabandono;
            } else if (yearElNot > yearNot) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    public Abandono getAbandonoBySolicitud(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select r from Abandono r where r.solicitud = :solicitud");
        query.setParameter("solicitud", solicitud);
        if (!query.getResultList().isEmpty()) {
            return (Abandono) query.getResultList().get(0);
        } else {
            return new Abandono();
        }
    }
    
    public List<Abandono> getAbandonosBySolSenadi(String solicitud) {
        Query query = this.getEntityManager().createQuery("Select c from Abandono c where c.solicitud = :solicitud");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        query.setParameter("solicitud", solicitud);
        return query.getResultList();
    }

    public boolean validarExistenciaAbandono(String solicitudSenadi) {
        Query query = this.getEntityManager().createQuery("Select c from Abandono c where c.solicitud = :solicitud");
        query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        query.setParameter("solicitud", solicitudSenadi);
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validarExistenciaAbandono(Abandono a) {
        Query query = this.getEntityManager().createQuery("Select n from Abandono n where n.solicitud = :solicitud and n.id != :id");
        query.setParameter("solicitud", a.getSolicitud());
        query.setParameter("id", a.getId());
        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public List<Notificada> getAbandonosErjafeVencidos(int dias) {

        // Calcula la fecha límite en Java
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery("SELECT n FROM Notificada n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite");
        query.setParameter("tipo", "ERJAFE");
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }
    
    /**
     * Funciona para coa y reglamento
     * @param diasHabiles: número de días a calcular
     * @param type: tipo de abandono (COA ó REGLAMENTO)
     * @return devuelve el listado de notificaciones que están vencidas
     */
    public List<Notificada> getAbandonosSinFinesSemana(int diasHabiles, String type) {
        LocalDate fechaLimite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(diasHabiles);
        Date fechaLimiteDate = java.sql.Date.valueOf(fechaLimite);

        Query query = this.getEntityManager().createQuery(
                "SELECT n FROM Notificada n WHERE n.tipoAbandono = :tipo AND n.fechaPuestaAbandono <= :fechaLimite"
        );
        query.setParameter("tipo", type);
        query.setParameter("fechaLimite", fechaLimiteDate);
        return query.getResultList();
    }

}
