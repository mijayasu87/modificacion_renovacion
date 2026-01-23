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
 * @author Michael
 */
@WebServlet(name = "ServletNewInforme", urlPatterns = {"/newinforme"})
public class NewInforme extends HttpServlet {

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

        String nombre = "Certificado Renovación";

        Controlador c = new Controlador();
        Delegado delegadoO = c.getDelegadoActivo();
        Delegacion delegacionO = c.getDelegacionActiva();
        Resolucion resolucionO = c.getResolucionActiva("renovacion");
        Resolucion resolucionN = c.getResolucionActiva("notificacion");
        
        Secretario secreN = c.getSecretarioActivo();

//        String delegado = "Abg. Franklin R. Jara";
//        String delegacion = "DELEGADO DE LA DIRECTORA NACIONAL DE PROPIEDAD INDUSTRIAL";
//        String resolucionData = "No. 005-2022-DNPI-SENADI, de 8 de Febrero de 2022";
        String delegado = delegadoO.getNombre();
        String delegacion = delegacionO.getNombre();
        String resolucionData = resolucionO.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionO.getFecha());
        String resolucionNot = resolucionN.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionN.getFecha());
                
        RazonCorreccion razon = null;
        if (lb.getRazon() != null) {
            razon = lb.getRazon();
        }        

//        System.out.println("aquiiiiiiiiiiiiiiiiiiii: "+resolucionData);
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
                        System.out.println("----------> raux "+(i+1)+": "+raux.getSigno());
                        if (lb.isNewreport()) {
                            if (raux.getSigno().trim().toUpperCase().equals("LC")) {
                                is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionLemaReport.jrxml");
                            } else {
                                is = getServletContext().getResourceAsStream("/WEB-INF/report/NewRenovacionReport.jrxml");
                            }

                        } else {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionReport.jrxml");
                        }
                        String apartado = "";
                        if (raux.getSigno().trim().toUpperCase().equals("NC")) {
                            apartado = "en la Decisión 486 de la Comisión de la Comunidad Andina, establece en sus artículos 196; y, 198 inciso segundo "
                                    + "<style pdfFontName='Helvetica-Oblique'>“(…) el registro se efectuará en los mismos términos del registro original.”</style>, "
                                    + "en concordancia con el artículo 420";
                        } else {
                            apartado = "en los artículos 152 y 153 de la Decisión 486 de la Comisión de la Comunidad Andina, "
                                    + "en concordancia con los artículos, 365, 366";
                        }

                        Report report = new Report();
                        byte[] arb = report.pdfRenovada(is, path, raux, raux.getSolicitudSenadi().trim() + "_"+raux.getSigno().trim().replace(" ", "_").toLowerCase()+"_certificado.pdf", delegado, delegacion, resolucionData, apartado);
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
                        
                        
                        
//                        System.out.println("LLega por aquí: "+raux.getSolicitudSenadi() +"_certificado_r.pdf");
//                        File fileTemp = new File(raux.getSolicitudSenadi().trim() + "_"+raux.getSigno().trim().replace(" ", "_").toLowerCase()+"_certificado_r.pdf");
//                        FileOutputStream outs = new FileOutputStream(fileTemp);
//                        outs.write(arb);
//                        outs.close();
//                        files.add(fileTemp);
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
                    for (int i = 0; i < lb.getNotificadasFlotantes().size(); i++) {
                        Notificada naux = lb.getNotificadasFlotantes().get(i);
//                        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/report/SecondPageN.jrxml");
                        InputStream is = null;
                        if (naux.getSigno().trim().toUpperCase().equals("NC")) {
                            System.out.println("");
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNC.jrxml");
                        } else {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNormal.jrxml");
                        }
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

//                response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;

                if (lb.getRenovacionFlotante() != null) {
                    Renovacion raux = lb.getRenovacionFlotante();
                    response.setHeader("Content-disposition", "inline; filename=" + raux.getSolicitudSenadi().trim() + "_"+raux.getSigno().trim().replace(" ", "_").toLowerCase()+"_renovacion.pdf");
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
                        if (raux.getSigno().trim().toUpperCase().equals("LC")) {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionLemaReport.jrxml");
                        } else {
                            is = getServletContext().getResourceAsStream("/WEB-INF/report/NewRenovacionReport.jrxml");
                        }
                        in = report.viewRenovada(path, is, raux, "archivo.xls", delegado, delegacion, resolucionData, apartado);
                    } else {
//                        System.out.println("Llega por aquí b: "+nombrePersona);
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/RenovacionReport.jrxml");
                        in = report.viewRenovada(path, is, raux, "archivo.xls", delegado, delegacion, resolucionData, apartado);
                    }

                } else if (lb.getNotificadaFlotante() != null) {
                    Notificada naux = lb.getNotificadaFlotante();
                    response.setHeader("Content-disposition", "inline; filename=" + naux.getSolicitud().trim() + "_" + naux.getSigno().trim().replace(" ", "_").toLowerCase() + "_notificacion.pdf");

                    if (naux.getSigno().equals("NC")) {
                        System.out.println("");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNC.jrxml");
                    } else {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNormal.jrxml");
                    }
//                    is = getServletContext().getResourceAsStream("/WEB-INF/report/NotificacionNormal.jrxml");
                    in = report.viewNotificada(path, is, lb.getNotificadaFlotante(), "archivo.xls", resolucionData, delegado, delegacion, resolucionNot, secreN);
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
