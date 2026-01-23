/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import senadi.gob.ec.mod.model.Caducada;
import senadi.gob.ec.mod.model.Delegacion;
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author micharesp
 */
@WebServlet(name = "ServletCaducadaInforme", urlPatterns = {"/cadinforme"})
public class CaducadaInforme extends HttpServlet {

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

//        String nombre = "Resolución Renovación";
        Controlador c = new Controlador();
        Delegado delegadoO = c.getDelegadoActivo();
        Delegacion delegacionO = c.getDelegacionActiva();
        Resolucion resolucionO = c.getResolucionActiva("renovacion");
        Resolucion resolucionN = c.getResolucionActiva("notificacion");

        Secretario secre = c.getSecretarioActivo();

//        String delegado = "Abg. Franklin R. Jara";
//        String delegacion = "DELEGADO DE LA DIRECTORA NACIONAL DE PROPIEDAD INDUSTRIAL";
//        String resolucionData = "No. 005-2022-DNPI-SENADI, de 8 de Febrero de 2022";
        String delegado = delegadoO.getNombre();
        String delegacion = delegacionO.getNombre();
        String resolucionData = resolucionO.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionO.getFecha());
        String resolucionNot = resolucionN.getResolucion() + ", de " + Operaciones.formatDateToLarge(resolucionN.getFecha());
        boolean other = lb.isOther();
        try {

            response.setHeader("Cache-Control", "max-age=30");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);
            if (lb.isVarious() && !lb.getCaducadas().isEmpty()) {

                response.setContentType("application/x-download");
                response.addHeader("Content-disposition", "attachment; filename=Cad-Neg_" + Operaciones.formatDate(new Date()) + ".zip");
                List<File> files = new ArrayList<File>();
                for (int i = 0; i < lb.getCaducadas().size(); i++) {
                    Caducada caux = lb.getCaducadas().get(i);
                    InputStream is = null;

                    String filename = caux.getSolicitudSenadi().trim() + "_" + caux.getSigno().trim().replace(" ", "_").toLowerCase();
                    if (other) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaOtherReport.jrxml");
                        filename += "_cad-neg_ro.pdf";
                    } else if (caux.getAntDes().equals("DESPUÉS")) {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaDespuesReport.jrxml");
                        filename += "_cad-neg_des_r.pdf";
                    } else {
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaReport.jrxml");
                        filename += "_cad-neg_r.pdf";
                    }

//
                    Report report = new Report();
                    byte[] arb = report.pdfCaducada(path, is, caux, "archivo.xls", delegado, delegacion, resolucionData, secre, resolucionNot);
                    report.closeConnection();
                    is.close();
//                        System.out.println("LLega por aquí: "+raux.getSolicitudSenadi() +"_certificado_r.pdf");
                    File fileTemp = new File(filename);
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();
                    files.add(fileTemp);
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

                if (lb.getCaducada() != null) {
                    Caducada caux = lb.getCaducada();                    
                    if (other) {                        
                        response.setHeader("Content-disposition", "inline; filename=" + caux.getSolicitudSenadi().trim() + "_" + caux.getSigno().trim().replace(" ", "_").toLowerCase() + "_cad-neg_ro.pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaOtherReport.jrxml");                        
                    } else if (caux.getAntDes().equals("DESPUÉS")) {
                        response.setHeader("Content-disposition", "inline; filename=" + caux.getSolicitudSenadi().trim() + "_" + caux.getSigno().trim().replace(" ", "_").toLowerCase() + "_cad-neg_des_r.pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaDespuesReport.jrxml");
                    } else {
                        response.setHeader("Content-disposition", "inline; filename=" + caux.getSolicitudSenadi().trim() + "_" + caux.getSigno().trim().replace(" ", "_").toLowerCase() + "_cad-neg_r.pdf");
                        is = getServletContext().getResourceAsStream("/WEB-INF/report/CaducadaReport.jrxml");
                    }

                    in = report.viewCaducada(path, is, caux, "archivo.xls", delegado, delegacion, resolucionData, secre, resolucionNot);
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
}
