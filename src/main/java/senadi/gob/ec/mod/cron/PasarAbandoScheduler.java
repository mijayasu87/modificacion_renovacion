/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.cron;

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.transaction.Transactional;
import java.time.LocalDate;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Prorroga;
import senadi.gob.ec.mod.ucc.Controlador;
import senadi.gob.ec.mod.ucc.Operaciones;

/**
 *
 * @author michael
 */
@Singleton
@Startup // para que se inicie con el servidor
public class PasarAbandoScheduler {

    @Schedule(hour = "1", minute = "1", second = "0", persistent = false)
    @Transactional
    public void moverNotificacionesVencidas() {
        System.out.println("🕒 Scheduler renovaciones ejecutado: " + Operaciones.getCurrentTimeStamp());

        int erjafe = 61;
        int reglamento = 10;
        int coa = 10;

        createAbandonosRenovacion(erjafe, reglamento, coa);
        createProrrogasRenovacion();
    }

    public void createProrrogasRenovacion() {
        Controlador c = new Controlador();
        List<Notificada> candidatas = c.getProrrogasCandidatas();
        System.out.println("notificaciones candidatas a prórroga encontradas: " + candidatas.size());
        for (Notificada notaux : candidatas) {
            if (notaux.getFechaPuestaProrroga() == null || notaux.getDiasProrroga() == null) {
                continue;
            }
            LocalDate limite = Operaciones.calcularFechaLimiteExcluyendoFinesSemana(notaux.getFechaPuestaProrroga(), notaux.getDiasProrroga());
            if (!LocalDate.now().isBefore(limite)) { // hoy >= límite (días hábiles)
                Prorroga p = new Prorroga();
                p.setTipoSolicitante(notaux.getTipoSolicitante());
                p.setSolicitud(notaux.getSolicitud());
                p.setFechaPresentacion(notaux.getFechaPresentacion());
                p.setNoComprobantePresentSolic(notaux.getNoComprobantePresentSolic());
                p.setNoComprobanteEmisionCert(notaux.getNoComprobanteEmisionCert());
                p.setTotalFoliosExpediente(notaux.getTotalFoliosExpediente());
                p.setNotificacion(notaux.getNotificacion());
                p.setFechaCertificado(notaux.getFechaCertificado());
                p.setTituloResolucion(notaux.getTituloResolucion());
                p.setRegistroNo(notaux.getRegistroNo());
                p.setFechaRegistro(notaux.getFechaRegistro());
                p.setFechaVenceRegistro(notaux.getFechaVenceRegistro());
                p.setDenominacion(notaux.getDenominacion());
                p.setLema(notaux.getLema());
                p.setSigno(notaux.getSigno());
                p.setClase(notaux.getClase());
                p.setProtege(notaux.getProtege());
                p.setTitularActual(notaux.getTitularActual());
                p.setTacNJ(notaux.getTacNJ());
                p.setNacTitularAc(notaux.getNacTitularAc());
                p.setDomicilioTitularAc(notaux.getDomicilioTitularAc());
                p.setAr(notaux.getAr());
                p.setNj(notaux.getNj());
                p.setTitApodRepre(notaux.getTitApodRepre());
                p.setApeApodRepre(notaux.getApeApodRepre());
                p.setNomApodRepre(notaux.getNomApodRepre());
                p.setFechaElaboraNotificacion(notaux.getFechaElaboraNotificacion());
                p.setFechaNotifica(notaux.getFechaNotifica());
                p.setCasilleroSenadi(notaux.getCasilleroSenadi());
                p.setCasilleroJudicial(notaux.getCasilleroJudicial());
                p.setRo(notaux.getRo());
                p.setProvidencia(notaux.getProvidencia());
                p.setFechaProvidencia(notaux.getFechaProvidencia());
                p.setFechaNotificaPro(notaux.getFechaNotificaPro());
                p.setResponsable(notaux.getResponsable());
                p.setIdentificacion(notaux.getIdentificacion());
                p.setNotificacionEmitida(notaux.isNotificacionEmitida());
                p.setCertificadoEmitido(notaux.isCertificadoEmitido());
                p.setCancelado(notaux.getCancelado());
                p.setSolicitante(notaux.getSolicitante());
                // Datos propios de prórroga
                p.setFechaPuestaProrroga(notaux.getFechaPuestaProrroga());
                p.setDiasProrroga(notaux.getDiasProrroga());
                p.setNumeroAlcance(notaux.getNumeroAlcance());
                p.setFechaAlcance(notaux.getFechaAlcance());
                p.setFechaProrroga(new Date());
                p.setNumeroProrroga(c.getNextNumeroProrroga(new Date()));
                if (c.saveProrroga(p) && c.removeNotificacion(notaux)) {
                    c.saveHistorial("PRORROGA", "NOTIFICADAS", p.getSolicitud(), "PASADO A", 0, "modificaciones");
                    System.out.println("Se pasó a prórroga el trámite " + p.getSolicitud());
                } else {
                    System.out.println("No se pudo pasar a prórroga el trámite " + notaux.getSolicitud());
                }
            }
        }
    }

//    @PostConstruct
//    public void alIniciar() {
//        moverNotificacionesVencidas();
//    }

    public void createAbandonosRenovacion(int erjafe, int reglamento, int coa) {
        Controlador c = new Controlador();
        List<Notificada> notificaciones = c.getAbandonosErjafeVencidos(erjafe);
        createAbandonos(notificaciones, "ERJAFE");
        notificaciones = c.getAbandonosSinFinesSemana(coa, "COA");
        System.out.println("notificaciones coa encontrados: " + notificaciones.size());
        createAbandonos(notificaciones, "COA");
        notificaciones = c.getAbandonosSinFinesSemana(reglamento, "REGLAMENTO");
        createAbandonos(notificaciones, "REGLAMENTO");
    }

    public void createAbandonos(List<Notificada> notificaciones, String type) {
        Controlador c = new Controlador();
        int n = 0;
        for (int i = 0; i < notificaciones.size(); i++) {
            Notificada notaux = notificaciones.get(i);
            Abandono abandono = new Abandono();
            abandono.setSolicitud(notaux.getSolicitud().toUpperCase());
            abandono.setFechaPresentacion(notaux.getFechaPresentacion());
            abandono.setFechaAbandono(new Date());
            abandono.setNumeroAbandono(c.getNextNumeroAbandono(abandono.getFechaAbandono()));

            abandono.setFechaElaboraNotificacion(notaux.getFechaNotifica());
            abandono.setNotificacion(notaux.getNotificacion());
            abandono.setFechaNotificacion(notaux.getFechaNotifica());
            abandono.setRegistro(notaux.getRegistroNo());
            abandono.setFechaRegistro(notaux.getFechaRegistro());
            abandono.setDenominacion(notaux.getDenominacion());
            abandono.setSigno(notaux.getSigno());
            //abandono.setTitularAnterior(notaux.getTitApodRepre());
            abandono.setTitularActual(notaux.getTitularActual());
            abandono.setApeApodRepre(notaux.getApeApodRepre());
            abandono.setRo(notaux.getRo());
            abandono.setCasilleroSenadi(notaux.getCasilleroSenadi());
            abandono.setCasilleroJudicial(notaux.getCasilleroJudicial());
            abandono.setResponsable(notaux.getResponsable());
            abandono.setIdentificacion(notaux.getIdentificacion());
            if(notaux.getNoComprobanteEmisionCert() != null && !notaux.getNoComprobanteEmisionCert().trim().isEmpty()){
                abandono.setCertificado(Integer.valueOf(notaux.getNoComprobanteEmisionCert()));
            }
            
            abandono.setFechaCertificado(notaux.getFechaCertificado());
            //abandono.setDomicilioTitularActual(notaux.getDomicilioTitularAc());
            abandono.setComprobante(notaux.getNoComprobantePresentSolic());
            abandono.setCertificadoEmitido(notaux.isCertificadoEmitido());
            abandono.setNotificacionEmitida(notaux.isNotificacionEmitida());
            abandono.setSolicitante(notaux.getSolicitante());
            abandono.setCancelado(notaux.getCancelado());

            abandono.setTipoAbandono(type);
            if (!c.saveAbandono(abandono)) {
                System.out.println("No se pudo pasar la notificación transf " + abandono.getSolicitud() + " (" + type + ") a abandono");
                return;
            } else {
                if (c.removeNotificacion(notaux)) {
                    c.saveHistorial("ABANDONO", "NOTIFICADAS", abandono.getSolicitud(), "PASADO A", 0, "modificaciones");
                    n++;
                }
            }
        }
    }
}
