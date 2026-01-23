/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import senadi.gob.ec.mod.bean.LoginBean;
import senadi.gob.ec.mod.model.Delegacion;
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.RazonCorreccion;
import senadi.gob.ec.mod.model.Renovacion;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletInforme", urlPatterns = {"/reportes"})
public class Informe extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession mises = (HttpSession) request.getSession();

        LoginBean lb = (LoginBean) mises.getValue("loginBean");
        ServletOutputStream out = response.getOutputStream();

        ServletContext context = request.getServletContext();
        String path = context.getRealPath("/WEB-INF/report/");

//        String nombre = "Certificado Renovación";
        Controlador c = new Controlador();
        Delegado delegadoO = c.getDelegadoActivo();
        Delegacion delegacionO = c.getDelegacionActiva();
        Resolucion resolucionO = c.getResolucionActiva("renovacion");
        Resolucion resolucionN = c.getResolucionActiva("notificacion");
        Secretario secreN = c.getSecretarioActivo();

        String delegado = delegadoO.getNombre();
        String delegacion = delegacionO.getNombre();
        String resolucionData = resolucionO.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionO.getFecha());
        String resolucionNot = resolucionN.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionN.getFecha());

        RazonCorreccion razon = null;
        if (lb.getRazon() != null) {
            razon = lb.getRazon();
        }

        if (lb.isSubrogante()) {
            delegado = "Ab. Pablo Xavier Montenegro Rubio";
            delegacion = "DELEGADO DE LA DIRECTORA NACIONAL DE PROPIEDAD INDUSTRIAL (S)";
            resolucionData = "No. 003-2021-DNPI-SENADI, de 4 de Octubre de 2021";
        }
        try {

            response.setHeader("Cache-Control", "max-age=30");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);
            if (lb.isVarious() && !lb.getRenovacionesFlotantes().isEmpty()) {
                response.setContentType("application/x-download");
                response.addHeader("Content-disposition", "attachment; filename=Renovaciones_" + Operaciones.formatDate(new Date()) + ".zip");
                List<File> files = new ArrayList<File>();
                if (lb.isAllInOne()) {
                    InputStream is = null;
                    if (lb.isNewreport()) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NewTodasRenReport.jrxml");
                    } else {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/TodasRenReport.jrxml");
                    }

                    Report report = new Report();
                    String certName = "renovacion_" + Operaciones.formatDate(new Date()) + ".pdf";

                    byte[] arb = report.pdfUnidoRenovacion(is, path, new RenovacionTableModel(lb.getRenovacionesFlotantes()), certName, delegado, delegacion, resolucionData);
                    report.closeConnection();
                    is.close();
                    File fileTemp = new File(certName);
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();
                    files.add(fileTemp);
                } else {
                    for (int i = 0; i < lb.getRenovacionesFlotantes().size(); i++) {
                        Renovacion raux = lb.getRenovacionesFlotantes().get(i);
                        InputStream is = null;
                        if (lb.isNewreport()) {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NewRenovacionReport.jrxml");
                        } else {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionReport.jrxml");
                        }
                        String apartado = "";
                        if (raux.getSigno().equals("NC")) {
                            apartado = "en la Decisión 486 de la Comisión de la Comunidad Andina, establece en sus artículos 196; y, 198 inciso segundo "
                                    + "<style pdfFontName='Helvetica-Oblique'>“(…) el registro se efectuará en los mismos términos del registro original.”</style>, "
                                    + "en concordancia con el artículo 420";
                        } else {
                            apartado = "en los artículos 152 y 153 de la Decisión 486 de la Comisión de la Comunidad Andina, "
                                    + "en concordancia con los artículos, 365, 366";
                        }
                        Report report = new Report();
                        byte[] arb = report.pdfRenovada(is, path, raux, raux.getSolicitudSenadi().trim() + "_certificado.pdf", delegado, delegacion, resolucionData, apartado);
                        report.closeConnection();
                        is.close();

                        File fileTemp = null;
                        if (razon != null) {
                            List<Object[]> estos = new ArrayList<>();
                            Report rep = new Report();
                            InputStream isaux = getServletContext().getResourceAsStream("/WEB-INF/report/RazonCorreccion.jrxml");
                            byte[] rac = rep.viewRazonCorreccion(path, isaux, razon);
                            rep.closeConnection();
                            isaux.close();
                            
                            estos.add(new Object[]{arb, "PÁGINA CERTIFICADO " + raux.getSolicitudSenadi()});
                            estos.add(new Object[]{rac, "PÁGINA RAZÓN " + raux.getSolicitudSenadi()});
                           
                            fileTemp = c.concatenarPdfDoFile(estos, raux.getSolicitudSenadi().trim().replace(" ", "_")+ "_ren_cert_" + raux.getCertificadoNo()+ "_raz.pdf");                            
                        } else {
                            fileTemp = new File(raux.getSolicitudSenadi().trim().replace(" ", "_") + "_"+raux.getCertificadoNo()+"_certificado.pdf");
                            FileOutputStream outs = new FileOutputStream(fileTemp);
                            outs.write(arb);
                            outs.close();
                        }
                        files.add(fileTemp);
                    }
                }
                File all = Operaciones.zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } else if (lb.isVarious() && !lb.getNotificadasFlotantes().isEmpty()) {
                response.setContentType("application/x-download");
                response.addHeader("Content-disposition", "attachment; filename=Notificadas_" + Operaciones.formatDate(new Date()) + ".zip");

                List<File> files = new ArrayList<File>();
                if (lb.isAllInOne()) {
//                    System.out.println("Entro si all in one");
                    InputStream is = getServletContext().getResourceAsStream("/WEB-INF/report/TodasNotReportBoth.jrxml");
                    Report report = new Report();
                    String certName = "notificada_" + Operaciones.formatDate(new Date()) + ".pdf";
                    byte[] arb = report.pdfUnidoNotificada(is, path, new NotificadaTableModel(lb.getNotificadasFlotantes()), certName);
                    report.closeConnection();
                    is.close();
                    File fileTemp = new File(certName);
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();
                    files.add(fileTemp);
                } else {
//                    System.out.println("Entro no all in one");
                    for (int i = 0; i < lb.getNotificadasFlotantes().size(); i++) {
                        Notificada naux = lb.getNotificadasFlotantes().get(i);

                        InputStream is = null;
                        if (naux.getSigno().equals("NC")) {
                            System.out.println("");
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNC.jrxml");
                        } else {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNormal.jrxml");
                        }

//                        is = getServletContext().getResourceAsStream("/WEB-INF/report/SecondPageN.jrxml");
//                    System.out.println("pdf_" + (i + 1));
                        Report report = new Report();
                        byte[] arb = report.pdfNotificada(is, path, naux, naux.getSolicitud().trim() + "_" + naux.getSigno().trim().replace(" ", "_").toLowerCase() + "_notificacion.pdf", resolucionData,
                                delegado, delegacion, resolucionNot, secreN);
                        report.closeConnection();
                        is.close();

                        File fileTemp = new File(naux.getSolicitud().trim() + "_" + naux.getSigno().trim().replace(" ", "_").toLowerCase() + "_notificacion.pdf");
                        FileOutputStream outs = new FileOutputStream(fileTemp);
                        outs.write(arb);
                        outs.close();
                        files.add(fileTemp);

                    }
                }

                File all = Operaciones.zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } else {

                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;

                if (lb.getRenovacionFlotante() != null) {
                    response.setHeader("Content-disposition", "inline; filename=" + lb.getRenovacionFlotante().getSolicitudSenadi().trim() + "_renovacion.pdf");
                    Renovacion raux = lb.getRenovacionFlotante();
                    String apartado = "";
                    if (raux.getSigno().equals("NC")) {
                        apartado = "en la Decisión 486 de la Comisión de la Comunidad Andina, establece en sus artículos 196; y, 198 inciso segundo "
                                + "<style pdfFontName='Helvetica-Oblique'>“(…) el registro se efectuará en los mismos términos del registro original.”</style>, "
                                + "en concordancia con el artículo 420";
                    } else {
                        apartado = "en los artículos 152 y 153 de la Decisión 486 de la Comisión de la Comunidad Andina, "
                                + "en concordancia con los artículos, 365, 366";
                    }
                    if (lb.isNewreport()) {
//                        System.out.println("Llega por aquí a: "+nombrePersona);
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NewRenovacionReport.jrxml");
                        in = report.viewRenovada(path, is, lb.getRenovacionFlotante(), "archivo.xls", delegado, delegacion, resolucionData, apartado);
                    } else {
//                        System.out.println("Llega por aquí b: "+nombrePersona);
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionReport.jrxml");
                        in = report.viewRenovada(path, is, lb.getRenovacionFlotante(), "archivo.xls", delegado, delegacion, resolucionData, apartado);
                    }

                } else if (lb.getNotificadaFlotante() != null) {
                    Notificada naux = lb.getNotificadaFlotante();

                    response.setHeader("Content-disposition", "inline; filename=" + lb.getNotificadaFlotante().getSolicitud().trim() + "_"
                            + naux.getSigno().trim().replace(" ", "_").toLowerCase() + "_notificacion.pdf");

                    if (naux.getSigno().equals("NC")) {
//                        System.out.println("");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNC.jrxml");
                    } else {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNormal.jrxml");
                    }
                    in = report.viewNotificada(path, is, naux, "archivo.xls", resolucionData, delegado, delegacion, resolucionNot, secreN);
                }

                int bit;
                bit = 256;
                while ((bit) >= 0) {
                    bit = in.read();
                    out.write(bit);
                }

                out.flush();
                out.close();
                report.closeConnection();
                is.close();
            }

        } catch (Exception e) {
            System.out.println("errorinf: " + e.toString());
        } finally {
            out.close();
        }
    }

//    public File zip(List<File> files, String filename) {
//        File zipfile = new File(filename);
//        // Create a buffer for reading the files
//        byte[] buf = new byte[1024];
//        try {
//            // create the ZIP file
//            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
//            // compress the files
//            for (int i = 0; i < files.size(); i++) {
//                FileInputStream in = new FileInputStream(files.get(i).getCanonicalFile());
//                // add ZIP entry to output stream
//                out.putNextEntry(new ZipEntry(files.get(i).getName()));
//                // transfer bytes from the file to the ZIP file
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//                // complete the entry
//                out.closeEntry();
//                in.close();
//            }
//            // complete the ZIP file
//            out.close();
//            return zipfile;
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//        }
//        return null;
//    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
