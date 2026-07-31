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
import senadi.gob.ec.mod.model.Delegado;
import senadi.gob.ec.mod.model.Prorroga;
import senadi.gob.ec.mod.model.Resolucion;
import senadi.gob.ec.mod.model.Secretario;

/**
 *
 * @author Michael Y.
 */
@WebServlet(name = "ServletProrroga", urlPatterns = {"/prorrogareport"})
public class InformeProrroga extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession mises = request.getSession();

        LoginBean lb = (LoginBean) mises.getValue("loginBean");
        ServletOutputStream out = response.getOutputStream();

        ServletContext context = request.getServletContext();
        String path = context.getRealPath("/WEB-INF/report/");

        Controlador c = new Controlador();

        Secretario secretaria = c.getSecretarioActivo();
        Delegado delegado = c.getDelegadoActivo();
        String delegacion = c.getDelegacionActiva().getNombre();
        
        Resolucion resnot = c.getResolucionActiva("notificacion");

        try {
            response.setHeader("Cache-Control", "max-age=18");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);

            if (lb.isVarious()) {
                List<Prorroga> prorrogas = lb.getProrrogas();

                String carp = "prorrogas_" + Operaciones.formatDate(new Date());
                response.setHeader("Content-disposition", "inline; filename=" + carp + ".zip");
                response.setContentType("application/x-download");
                List<File> files = new ArrayList<>();

                for (int i = 0; i < prorrogas.size(); i++) {
                    Prorroga prorroga = prorrogas.get(i);
                    InputStream is = getServletContext().getResourceAsStream("/WEB-INF/report/ProrrogaReport.jrxml");
                    Report report = new Report();
                    String nombre = prorroga.getSolicitud() + "_prorroga_ren_" + prorroga.getNumeroProrroga();
                    byte[] arb = report.viewProrrogaMasterBytes(path, is, prorroga, delegado, delegacion, secretaria, resnot);
                    File fileTemp = new File(nombre.trim().replace(" ", "_") + ".pdf");
                    try (FileOutputStream outs = new FileOutputStream(fileTemp)) {
                        outs.write(arb);
                    }
                    files.add(fileTemp);
                    report.closeConnection();
                    is.close();
                }

                File all = zip(files, "prorrogas");

                byte[] content = Files.readAllBytes(all.toPath());

                response.getOutputStream().write(content);
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } else {
                Prorroga prorroga = lb.getProrroga();
                response.setContentType("application/pdf");
                Report report = new Report();
                FileInputStream in = null;
                InputStream is = null;

                if (prorroga != null && prorroga.getId() != null) {
                    String nombre = prorroga.getSolicitud() + "_prorroga_ren_" + prorroga.getNumeroProrroga();
                    nombre = nombre.trim().replace(" ", "_");
                    response.setHeader("Content-disposition", "inline; filename=" + nombre + ".pdf");
                    is = getServletContext().getResourceAsStream("/WEB-INF/report/ProrrogaReport.jrxml");
                    in = report.viewProrroga(path, is, prorroga, "archivo.xls", delegado, delegacion, secretaria, resnot);
                }
                int bit = 256;
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
            System.out.println("error prórroga : " + e.toString());
        } finally {
            out.close();
        }
    }

    public File zip(List<File> files, String filename) {
        File zipfile = new File(filename);
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < files.size(); i++) {
                FileInputStream in = new FileInputStream(files.get(i).getCanonicalFile());
                out.putNextEntry(new ZipEntry(files.get(i).getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            return zipfile;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para generar el PDF de prórrogas";
    }
}
