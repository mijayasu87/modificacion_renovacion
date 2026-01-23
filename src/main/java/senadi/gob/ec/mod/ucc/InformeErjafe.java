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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import senadi.gob.ec.mod.bean.LoginBean;
import senadi.gob.ec.mod.model.Notificada;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author michael
 */
@WebServlet(name = "ServletErjafe", urlPatterns = {"/erjreport"})
public class InformeErjafe extends HttpServlet {

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

        Controlador c = new Controlador();

        Secretario secretaria = c.getSecretarioActivo();

        String delegado = c.getDelegadoActivo().getNombre();
        String delegacion = c.getDelegacionActiva().getNombre();
        Resolucion resnot = c.getResolucionActiva("notificacion");

        try {

            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious()) {

                response.setContentType("application/x-download");
                List<File> files = new ArrayList<>();
                

                String carp = "not_e_tr_" + Operaciones.formatDate(new Date());
                response.setHeader("Content-disposition", "inline; filename=" + carp + ".zip");
                List<Notificada> notificadas = lb.getNotificadasFlotantes();

                for (int i = 0; i < notificadas.size(); i++) {
                    Notificada notificacion = notificadas.get(i);
                    InputStream is = null;

                    is = getServletContext().getResourceAsStream("/WEB-INF/report/ErjafeReporte.jrxml");
                    String nombre = notificacion.getSolicitud() + "_cad_not_" + notificacion.getNotificacion();

                    //Es necesario poner report aquí apra que se abra la sesión de la conexión
                    Report report = new Report();
                    byte[] arb = report.viewErjafeMasterBytes(path, is, notificacion.getFechaPresentacion(), notificacion.getId(), "archivo.xls", delegado, delegacion, secretaria, "RENOVACIÓN", resnot);
                    File fileTemp = new File(nombre.trim().replace(" ", "_") + ".pdf");
                    FileOutputStream outs = new FileOutputStream(fileTemp);
                    outs.write(arb);
                    outs.close();

                    files.add(fileTemp);

                    report.closeConnection();
                    is.close();
                }

                File all = zip(files, "micharoto");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } else {
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;

                Notificada notificacion = lb.getNotificadaFlotante();
                if (notificacion != null && notificacion.getId() != null) {

                    System.out.println("print erjafe transferencia " + notificacion.getSolicitud());
                    String nombre = notificacion.getSolicitud() + "_not_ren_e_" + notificacion.getNotificacion();

                    nombre = nombre.trim().replace(" ", "_");
                    response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                    is = getServletContext().getResourceAsStream("/WEB-INF/report/ErjafeReporte.jrxml");
                    in = report.viewErjafe(path, is, notificacion.getFechaPresentacion(), notificacion.getId(), "archivo.xls", delegado, delegacion, secretaria, "RENOVACIÓN", resnot);
                } else {
                    System.err.println("No se cargó correctamente la negada transferencia");
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
            System.out.println("error notificada erjafe : " + e.toString());
        } finally {
            out.close();
        }
    }

    public File zip(List<File> files, String filename) {
        File zipfile = new File(filename);
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        try {
            // create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            // compress the files
            for (int i = 0; i < files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).getCanonicalFile());
                // add ZIP entry to output stream
                out.putNextEntry(new ZipEntry(files.get(i).getName()));
                // transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // complete the entry
                out.closeEntry();
                in.close();
            }
            // complete the ZIP file
            out.close();
            return zipfile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }
}
