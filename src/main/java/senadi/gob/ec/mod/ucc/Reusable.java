/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.ucc;

import java.util.ArrayList;
import java.util.List;
import senadi.gob.ec.mod.model.Documento;


/**
 *
 * @author micharesp
 */
public class Reusable {
    
    String rutaMaestro = "/var/www/html/solicitudes/media/files/renewal_forms/";
    
    public List<Documento> getRutasDeExpedienteRenewal(int idRenewal, String applicationNumber){
        List<Documento> documentos = new ArrayList<>();

        FTPFiles files = new FTPFiles(130);
        List<String> archivos = files.listarDirectorio(rutaMaestro + idRenewal);
        for (int i = 0; i < archivos.size(); i++) {
            String file = archivos.get(i);
            Documento doc = new Documento();
            doc.setId(idRenewal);
            doc.setDocumento(file);
            if(doc.getDocumento().contains("pdf_renewalfrm_"+idRenewal)){
                doc.setNombre("FORMULARIO_"+applicationNumber);
            }else if(doc.getDocumento().contains("pdf_voucher_renewalfrm_"+idRenewal)){
                doc.setNombre("COMPROBANTE_INGRESO");
            }else{
                doc.setNombre(doc.getDocumento());
            }
            doc.setUrl("https://registro.propiedadintelectual.gob.ec/solicitudes/media/files/renewal_forms/");
            documentos.add(doc);
        }
        
        return documentos;
    }
}
