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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Michael Yanangómez
 */
public class Operaciones {

    public static String getCurrentTimeStamp() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    /**
     * Pasa un String Ej: '2020-05-15' a java.util.Date
     *
     * @param fecha en String
     */
    public static Date convertStringToDate(String fecha) {
        String an = fecha.substring(0, fecha.indexOf("-"));
        String aux = fecha.substring(fecha.indexOf("-") + 1);

        int a = Integer.parseInt(an);
        int m = Integer.parseInt(aux.substring(0, aux.indexOf("-")));
        int d = Integer.parseInt(aux.substring(aux.indexOf("-") + 1));

        return new Date(a - 1900, m - 1, d);
    }

    /**
     * Da formato a la fecha recibida en el siguiente orden 'yyyy-mm-dd'
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        int dia = date.getDate();
        int mes = date.getMonth() + 1;
        int año = date.getYear() + 1900;
        String d = dia + "";
        String m = mes + "";
        if (dia < 10) {
            d = "0" + dia;
        }
        if (mes < 10) {
            m = "0" + mes;
        }

        String fecha = año + "-" + m + "-" + d;
        return fecha;
    }

    /**
     * Da formato a una fecha recibida, en el siguiente orden 'Dddddd dd de
     * Mmmmm de yyyy'
     */
    public static String formatDateToLarge(Date fecha) {
        int dia = fecha.getDate();
        int mes = fecha.getMonth();
        int año = fecha.getYear() + 1900;
//        int diasem = fecha.getDay();

        String fec = "";
        if (dia < 10) {
            fec = "0" + dia + " de " + getMes(mes) + " de " + año;  //getDia(diasem) + " " + 
        } else {
            fec = dia + " de " + getMes(mes) + " de " + año;  //getDia(diasem) + " " + 
        }

        return fec.toLowerCase();
    }

    public static String getDia(int dia) {
        if (dia == 1) {
            return "Lunes";
        } else if (dia == 2) {
            return "Martes";
        } else if (dia == 3) {
            return "Miércoles";
        } else if (dia == 4) {
            return "Jueves";
        } else if (dia == 5) {
            return "Viernes";
        } else if (dia == 6) {
            return "Sábado";
        } else if (dia == 0) {
            return "Domingo";
        } else {
            return "Error";
        }
    }

    public static String getMes(int mes) {
        if (mes == 0) {
            return "Enero";
        } else if (mes == 1) {
            return "Febrero";
        } else if (mes == 2) {
            return "Marzo";
        } else if (mes == 3) {
            return "Abril";
        } else if (mes == 4) {
            return "Mayo";
        } else if (mes == 5) {
            return "Junio";
        } else if (mes == 6) {
            return "Julio";
        } else if (mes == 7) {
            return "Agosto";
        } else if (mes == 8) {
            return "Septiembre";
        } else if (mes == 9) {
            return "Octubre";
        } else if (mes == 10) {
            return "Noviembre";
        } else if (mes == 11) {
            return "Diciembre";
        } else {
            return "Error";
        }
    }

    /* Retorna un hash MD5 a partir de un texto */
    public static String md5(String txt) {
        return getHash(txt, "MD5");
    }

    /* Retorna un hash a partir de un tipo y un texto */
    public static String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getTramiteFromPdfName(String pdfName) {
        String tramite = "";
        for (int i = 0; i < pdfName.length(); i++) {
            if (pdfName.charAt(i) == '_') {
                tramite = pdfName.substring(0, i);
                break;
            }
        }
        return tramite;
    }

    public static boolean copyFile(String fileName, InputStream stream, String rutaCarpeta) {
        try {
            // write the inputStream to a FileOutputStream
            String nombreDoc = fileName;
            String rutaCompleta = rutaCarpeta + nombreDoc;
            removeSimilarFiles(nombreDoc, rutaCarpeta);
//            System.out.println("rutacompleta: "+rutaCarpeta);
            FileOutputStream fichero = new FileOutputStream(rutaCompleta);
            // Lectura de la url de la web y escritura en fichero local
            byte[] buffer = new byte[1024]; // buffer temporal de lectura.
            int readed = stream.read(buffer);
            while (readed > 0) {
                fichero.write(buffer, 0, readed);
                readed = stream.read(buffer);
            }
            // cierre de conexion y fichero.
            stream.close();
            fichero.close();
            return true;
//            System.out.println("New file uploaded: " + (rutaCarpeta + fileName));
        } catch (IOException e) {
            System.out.println("Error al guardar documento: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeSimilarFiles(String logo, String rutaCarpeta) {
        String[] extensiones = {".pdf"};

        boolean aviso = false;
        for (String extension : extensiones) {
            File file = new File(rutaCarpeta + logo + extension);
            if (file.exists()) {
                file.delete();
                aviso = true;
            }
        }

        return aviso;
    }

    public static File zip(List<File> files, String filename) {
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

    public static int esCertificado(String url, String textoCertificado) {
        PDDocument pdDocument = null;

        try {
            URL ur = new URL(url);

            pdDocument = PDDocument.load(ur.openStream());

            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            String parsedText = pdfStripper.getText(pdDocument);

//            System.out.println("Texto:\n\n\n" + parsedText);
            if (parsedText.contains(textoCertificado)) {
                return 1;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("a: Error leyendo documento: " + e);
            return -1;
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    System.out.println("b: Error leyendo documento: " + e);
                    return -1;
                }
            }
        }
        return 0;
    }

    public static LocalDate getFechaAjustada(LocalDate fecha, int meses) {
        // Añadir o restar los meses
        LocalDate fechaAjustada = fecha.plusMonths(meses);

        // Comprobar si la fecha ajustada cae en el último día del mes
//        if (fechaAjustada.getMonth() != fecha.getMonth()) {
//            fechaAjustada = fechaAjustada.with(TemporalAdjusters.lastDayOfMonth());
//        }

        return fechaAjustada;
    }

    public static boolean validarFecha(Date fecha) {
        try {
            fecha.toString();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

        public static LocalDate calcularFechaLimiteExcluyendoFinesSemana(Date fechaInicio, int diasHabiles) {
        LocalDate fecha = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int agregados = 0;
        while (agregados < diasHabiles) {
            fecha = fecha.plusDays(1);
            DayOfWeek dia = fecha.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                agregados++;
            }
        }
        return fecha;
    }        

    public static LocalDate calcularFechaLimiteExcluyendoFinesSemana(int diasHabiles) {
        LocalDate fecha = LocalDate.now();
        int cont = 0;

        while (cont < diasHabiles) {
            fecha = fecha.minusDays(1);
            DayOfWeek diaSemana = fecha.getDayOfWeek();
            if (diaSemana != DayOfWeek.SATURDAY && diaSemana != DayOfWeek.SUNDAY) {
                cont++;
            }
        }

        return fecha;
    }

}
