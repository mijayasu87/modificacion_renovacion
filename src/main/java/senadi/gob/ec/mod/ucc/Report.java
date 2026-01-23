/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import senadi.gob.ec.mod.model.Abandono;
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.RazonCorreccion;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author michael
 */
public class Report implements Serializable {

    private Connection conn;

    public Report() {
        try {
            //localhost
//            String user = "root";
//            String pass = "MichaRoot6*";
//            String basd = "mod_renovacion";
//            String host = "localhost";

            //producción
            String user = "root";
            String pass = "B8GJuaxu4Y:2020";
            String basd = "mod_renovacion";
            String host = "10.0.20.140";

            Class.forName("com.mysql.jdbc.Driver"); //se carga el driver
            String url = "jdbc:mysql://" + host + "/" + basd + "?autoReconnect=true&useSSL=false";
            conn = DriverManager.getConnection(url, user, pass);

        } catch (Exception ex) {
            System.out.println("Error conexion: " + ex);
            ex.printStackTrace();
        }
    }

    /*Cierra la conexión a la base de datos mysql*/
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewErjafeMasterBytes(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Secretario secretaria, String tipoTramite, Resolucion resolucion) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tablabd", "notificada");
            parametro.put("tipotramite", tipoTramite);
            parametro.put("resolucionnot", resolucion.getResolucion());
            parametro.put("id", idCaducada);

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            
            System.out.println("ID param: " + parametro.get("id"));
System.out.println("JasperReport compiled: " + jasperReport.getName());
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print caducada " + tipoTramite + " separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewErjafe(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Secretario secretaria, String tipoTramite, Resolucion resolucion) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tablabd", "caducada");
            parametro.put("tablabd", "notificada");
            parametro.put("tipotramite", tipoTramite);
            parametro.put("resolucionnot", resolucion.getResolucion());
            parametro.put("id", idCaducada);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print erjafe: " + ex);
            return null;
        }
    }

    public FileInputStream viewCaducada(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada, String rutapdf,
            String delegado, String delegacion, Secretario secretaria, String tipoTramite) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            String tercero;
            String cuarto;
            String resuelve;
            Date date = new Date(2016 - 1900, 11, 9);
            System.out.println("date: " + date);
            if (fechaPresentacion.before(date)) {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada "
                        + "de documentación para su análisis dentro del procedimiento administrativo correspondiente y, realizado el examen "
                        + "preliminar de la documentación aportada, se procedió a verificar el cumplimiento de los requisitos formales y "
                        + "sustanciales establecidos en la Ley de Propiedad Intelectual y su Reglamento General.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, no se "
                        + "ha verificado el cumplimiento de lo dispuesto en la Ley de Propiedad Intelectual "
                        + "ni en su Reglamento, lo cual impide que la administración corrobore el cumplimiento "
                        + "de los requisitos necesarios para la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos establecidos en el Art. 357 "
                        + "de la Ley de Propiedad Intelectual; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo "
                        + "Contencioso Administrativo.";
            } else {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada de documentación "
                        + "para su análisis dentro del procedimiento administrativo correspondiente y, realizado "
                        + "el examen preliminar de la documentación aportada, se procedió a verificar el cumplimiento "
                        + "de los requisitos formales y sustanciales establecidos en el Código Orgánico de la "
                        + "Economía Social de los Conocimientos, Creatividad e Innovación.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, "
                        + "no se ha verificado el cumplimiento de lo dispuesto en la Código Orgánico de "
                        + "la Economía Social de los Conocimientos, Creatividad e Innovación, lo cual impide "
                        + "que la administración corrobore el cumplimiento de los requisitos necesarios para "
                        + "la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos correspondientes "
                        + "de conformidad con lo establecido en el Art. 597 del Código Orgánico de la Economía Social de los "
                        + "Conocimientos, Creatividad e Innovación y en el Art. 488 del Reglamento de Gestión de los "
                        + "Conocimientos; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo Contencioso "
                        + "Administrativo.";
            }

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("tercero", tercero);
            parametro.put("cuarto", cuarto);
            parametro.put("resuelve", resuelve);
            parametro.put("tablabd", "caducada");
            parametro.put("tipotramite", "la " + tipoTramite);

            parametro.put("id", idCaducada);

            System.out.println("que onda");
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutapdf + ".pdf");

            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print caducada renovación: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewCaducada(String path, InputStream rutaJrxml, Caducada cad, String rutaArchivoXLS,
            String delegado, String delegacion, String resData, Secretario secre, String resNot) {

        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", cad.getId());
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);
            parametro.put("secretaria", secre.getNombre());
            parametro.put("denominacion", secre.getDenominacion());
            parametro.put("resolucionNot", resNot);

            String venc = Operaciones.formatDate(cad.getFechaVencimiento());

            Date fechaDada = Operaciones.convertStringToDate(venc);

            LocalDate localDate = fechaDada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate antes = Operaciones.getFechaAjustada(localDate, -6);
            LocalDate despues = Operaciones.getFechaAjustada(localDate, 6);

            parametro.put("before", Operaciones.formatDateToLarge(Date.from(antes.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            parametro.put("after", Operaciones.formatDateToLarge(Date.from(despues.atStartOfDay(ZoneId.systemDefault()).toInstant())));

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print renovación: " + ex);
            return null;
        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewRenovada(String path, InputStream rutaJrxml, Renovacion r, String rutaArchivoXLS,
            String delegado, String delegacion, String resData, String apartado) {

        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE RENOVACIÓN No. " + r.getCertificadoNo() + " - SENADI");
            parametro.put("fecha_certificado", Operaciones.formatDateToLarge(r.getFechaCertificado()));
            parametro.put("fecha_presentacion", Operaciones.formatDateToLarge(r.getFechaPresentacion()));
            parametro.put("fecha_registro", Operaciones.formatDateToLarge(r.getFechaRegistro()));
            parametro.put("fecha_vence_reg", Operaciones.formatDateToLarge(r.getFechaVenceRegistro()));
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idRenovacion", r.getId());
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);
            parametro.put("apartado", apartado);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);

                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                exporter.exportReport();

            }
            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print renovación: " + ex);
            return null;
        }
    }

    public byte[] pdfRenovada(InputStream rutaJrxml, String path, Renovacion r, String nombre, String delegado, String delegacion, String resData, String apartado) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("title", "CERTIFICADO DE RENOVACIÓN No. " + r.getCertificadoNo() + " - SENADI");
            parametro.put("fecha_certificado", Operaciones.formatDateToLarge(r.getFechaCertificado()));
            parametro.put("fecha_presentacion", Operaciones.formatDateToLarge(r.getFechaPresentacion()));
            parametro.put("fecha_registro", Operaciones.formatDateToLarge(r.getFechaRegistro()));
            parametro.put("fecha_vence_reg", Operaciones.formatDateToLarge(r.getFechaVenceRegistro()));
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("idRenovacion", r.getId());
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);
            parametro.put("apartado", apartado);
            //se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (JRException ex) {
            System.err.println("Error iReport: " + ex.getMessage());
            return null;
        }
    }

    public byte[] pdfUnidoRenovacion(InputStream rutaJrxml, String path, RenovacionTableModel rtm, String nombre, String delegado, String delegacion, String resData) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);

            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            JRTableModelDataSource datos = new JRTableModelDataSource(rtm);

            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, datos);
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;

        } catch (JRException ex) {
            System.err.println("Error pdfunidorenovación: " + ex.getMessage() + " - " + ex);
            return null;

        }
    }

    public byte[] pdfUnidoNotificada(InputStream rutaJrxml, String path, NotificadaTableModel ntm, String nombre) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");

            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            JRTableModelDataSource datos = new JRTableModelDataSource(ntm);

            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, datos);

            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;

        } catch (JRException ex) {
            System.err.println("Error pdfunidonotificada: " + ex.getMessage() + " - " + ex);
            return null;

        }
    }

    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public FileInputStream viewNotificada(String path, InputStream rutaJrxml, Notificada n, String rutaArchivoXLS, String resolucionData,
            String delegado, String delegacion, String resolucionNot, Secretario secre) {

        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", n.getId());
            parametro.put("resolucionData", resolucionData);
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionNot", resolucionNot);
            parametro.put("casilla", n.getCasilleroSenadi() != null && !n.getCasilleroSenadi().trim().isEmpty() ? n.getCasilleroSenadi() : n.getCasilleroJudicial());
            parametro.put("secretario", secre.getNombre());
            parametro.put("denominacion", secre.getDenominacion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                return null;
            }

            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            OutputStream out = new FileOutputStream(rutaArchivoXLS + ".pdf");
            JRPdfExporter exporter = new JRPdfExporter();

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            ExporterInput inp = new SimpleExporterInput(jasperPrint);
            configuration.setCreatingBatchModeBookmarks(true);
            configuration.set128BitKey(Boolean.TRUE);

            exporter.setConfiguration(configuration);
            exporter.setExporterInput(inp);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            exporter.exportReport();

            entrada = new FileInputStream(rutaArchivoXLS + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print notificada: " + ex);
            return null;
        }
    }

    public byte[] pdfNotificada(InputStream rutaJrxml, String path, Notificada n, String nombre, String resolucionData,
            String delegado, String delegacion, String resolucionNot, Secretario secre) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
//            parametro.put("title", "RENOVACIÓN");
            parametro.put("SUBREPORT_DIR", path + "/");
//            parametro.put("idNotificada", n.getId());
//            parametro.put("fecha_el_not", Operaciones.formatDateToLarge(n.getFechaElaboraNotificacion()));
            parametro.put("id", n.getId());
            parametro.put("resolucionData", resolucionData);
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionNot", resolucionNot);
            parametro.put("casilla", n.getCasilleroSenadi() != null && !n.getCasilleroSenadi().trim().isEmpty() ? n.getCasilleroSenadi() : n.getCasilleroJudicial());
            parametro.put("secretario", secre.getNombre());
            parametro.put("denominacion", secre.getDenominacion());
            //se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (JRException ex) {
            System.err.println("Error iReport: " + ex.getMessage());
            return null;
        }
    }

    public byte[] pdfCaducada(String path, InputStream is, Caducada cad, String archivoxls, String delegado, String delegacion,
            String resData, Secretario secre, String resNot) {

        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", cad.getId());
            parametro.put("delegado", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("resolucionData", resData);
            parametro.put("secretaria", secre.getNombre());
            parametro.put("denominacion", secre.getDenominacion());
            parametro.put("resolucionNot", resNot);

            String venc = Operaciones.formatDate(cad.getFechaVencimiento());

            Date fechaDada = Operaciones.convertStringToDate(venc);
            LocalDate localDate = fechaDada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate antes = Operaciones.getFechaAjustada(localDate, -6);
            LocalDate despues = Operaciones.getFechaAjustada(localDate, 6);

            parametro.put("before", Operaciones.formatDateToLarge(Date.from(antes.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            parametro.put("after", Operaciones.formatDateToLarge(Date.from(despues.atStartOfDay(ZoneId.systemDefault()).toInstant())));

            //se carga el reporte
            jasperReport = JasperCompileManager.compileReport(is);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (JRException ex) {
            System.err.println("Error iReport: " + ex.getMessage());
            return null;
        }
    }

    public byte[] viewRazonCorreccion(String path, InputStream ruta, RazonCorreccion razon) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();
            parametro.put("suscriptor", razon.getSuscriptor());
            parametro.put("denominacion", razon.getDenominacion());
            parametro.put("razon", razon.getRazon());
            parametro.put("idCambioCasillero", 1);
            parametro.put("SUBREPORT_DIR", path + "/");

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(ruta);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print renovacion separado: " + ex);
            return null;
        }
    }


    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewCaducadaMasterBytes(String path, InputStream rutaJrxml, Date fechaPresentacion, Integer idCaducada,
            String delegado, String delegacion, Secretario secretaria, String tipoTramite) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {

            String tercero;
            String cuarto;
            String resuelve;
            Date date = new Date(2018 - 1900, 6, 6); //6 julio 2018
            if (fechaPresentacion.before(date)) {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada "
                        + "de documentación para su análisis dentro del procedimiento administrativo correspondiente y, realizado el examen "
                        + "preliminar de la documentación aportada, se procedió a verificar el cumplimiento de los requisitos formales y "
                        + "sustanciales establecidos en la Ley de Propiedad Intelectual y su Reglamento General.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, no se "
                        + "ha verificado el cumplimiento de lo dispuesto en la Ley de Propiedad Intelectual "
                        + "ni en su Reglamento, lo cual impide que la administración corrobore el cumplimiento "
                        + "de los requisitos necesarios para la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos establecidos en el Art. 357 "
                        + "de la Ley de Propiedad Intelectual; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo "
                        + "Contencioso Administrativo.";
            } else {
                tercero = "La solicitud fue ingresada por la parte interesada, acompañada de documentación "
                        + "para su análisis dentro del procedimiento administrativo correspondiente y, realizado "
                        + "el examen preliminar de la documentación aportada, se procedió a verificar el cumplimiento "
                        + "de los requisitos formales y sustanciales establecidos en el Código Orgánico de la "
                        + "Economía Social de los Conocimientos, Creatividad e Innovación.";
                cuarto = "En el presente caso, de la revisión efectuada a la documentación ingresada, "
                        + "no se ha verificado el cumplimiento de lo dispuesto en la Código Orgánico de "
                        + "la Economía Social de los Conocimientos, Creatividad e Innovación, lo cual impide "
                        + "que la administración corrobore el cumplimiento de los requisitos necesarios para "
                        + "la continuación o admisión del trámite.";
                resuelve = "El presente acto administrativo es susceptible de los recursos administrativos correspondientes "
                        + "de conformidad con lo establecido en el Art. 597 del Código Orgánico de la Economía Social de los "
                        + "Conocimientos, Creatividad e Innovación y en el Art. 488 del Reglamento de Gestión de los "
                        + "Conocimientos; o por vía jurisdiccional ante uno de los Tribunales Distritales de lo Contencioso "
                        + "Administrativo.";
            }

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado);
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("tercero", tercero);
            parametro.put("cuarto", cuarto);
            parametro.put("resuelve", resuelve);
            String el_la = "";
            if (tipoTramite.equals("TRANSFERENCIA")) {
                parametro.put("tablabd", "caducada");
                el_la = "la ";
            } else if (tipoTramite.equals("CAMBIO DE DOMICILIO")) {
                parametro.put("tablabd", "cambio_domicilio");
                el_la = "el ";
            } else if (tipoTramite.equals("CAMBIO DE NOMBRE")) {
                parametro.put("tablabd", "cambio_nombre");
                el_la = "el ";
            } else if (tipoTramite.equals("LICENCIA DE USO")) {
                parametro.put("tablabd", "licencia_uso");
                el_la = "la ";
            } else if (tipoTramite.equals("PRENDA COMERCIAL")) {
                parametro.put("tablabd", "prenda_comercial");
                el_la = "la ";
            }
            parametro.put("tipotramite", el_la + tipoTramite);
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", idCaducada);

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print caducada " + tipoTramite + " separado: " + ex);
            return null;
        }
    }

    public FileInputStream viewAbandono(String path, InputStream rutaJrxml, Abandono abandono, String rutapdf,
            Delegado delegado, String delegacion, Secretario secretaria) {
        try {
            FileInputStream entrada;
            JasperReport reportePrincipal = JasperCompileManager.compileReport(rutaJrxml);

            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado.getNombre());
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", abandono.getId());
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportePrincipal, parametro, conn);
            if (jasperPrint.getPages().isEmpty()) {
                System.out.println("Hay un error con el jasperprint");
                return null;
            }
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();

            try (OutputStream out = new FileOutputStream(rutapdf + ".pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                ExporterInput inp = new SimpleExporterInput(jasperPrint);
                configuration.setCreatingBatchModeBookmarks(true);
                configuration.set128BitKey(Boolean.TRUE);
                exporter.setConfiguration(configuration);
                exporter.setExporterInput(inp);
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            }
            entrada = new FileInputStream(rutapdf + ".pdf");
            return entrada;
        } catch (Exception ex) {
            System.out.println("Error print abandono: " + ex);
            return null;
        }
    }
    
    /*Dibuja (arma) el reporte, para que esté listo para ser mostrado en pantalla*/
    public byte[] viewAbandonoMasterBytes(String path, InputStream rutaJrxml, Abandono abandono,
            Delegado delegado, String delegacion, Secretario secretaria) {
        JasperReport jasperReport;
        JasperPrint jasperPrint;
        try {
            Map parametro = new HashMap();

            parametro.put("nombrePersona", delegado.getNombre());
            parametro.put("delegacion", delegacion);
            parametro.put("secretaria", secretaria.getNombre());
            parametro.put("denosecre", secretaria.getDenominacion());
            parametro.put("SUBREPORT_DIR", path + "/");
            parametro.put("id", abandono.getId());

//se carga el reporte
            jasperReport = JasperCompileManager.compileReport(rutaJrxml);
            //se procesa el archivo jasper
            jasperPrint = JasperFillManager.fillReport(jasperReport, parametro, conn);
            //se crea el archivo PDF            
            byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
            return output;
        } catch (Exception ex) {
            System.out.println("Error print abandono separado: " + ex);
            return null;
        }
    }
}
