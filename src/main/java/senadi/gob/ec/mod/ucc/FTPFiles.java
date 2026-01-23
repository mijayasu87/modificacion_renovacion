/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package senadi.gob.ec.mod.ucc;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author micharesp
 */
public class FTPFiles {

//    private static final String REMOTE_HOST = "10.0.20.130";
//    private static final String USERNAME = "root";
//    private static final String PASSWORD = "B7GJuaxu3Y";
//    private static final int REMOTE_PORT = 22;
//    private static final int SESSION_TIMEOUT = 10000;
//    private static final int CHANNEL_TIMEOUT = 30000;
    
    private Object[] parametros;

    public FTPFiles(int servidor) {
        if (servidor == 130) {
            parametros = parametrosSolicitudes();
        } else if (servidor == 131) {
            parametros = parametrosAdministracion();
        }
    }
    
    private Object[] parametrosSolicitudes() {
        String HOST = "10.0.20.130";
        String usuario = "root";
        String pass = "B7GJuaxu3Y";
        int puerto = 22;
        int ses_timeout = 10000;
        int chan_timeout = 30000;

        Object[] algo = {HOST, usuario, pass, puerto, ses_timeout, chan_timeout};
        return algo;
    }

    private Object[] parametrosAdministracion() {
        String HOST = "10.0.20.131";
        String usuario = "root";
        String pass = "Uio2016gdt&Iepi";
        int puerto = 22;
        int ses_timeout = 10000;
        int chan_timeout = 30000;

        Object[] algo = {HOST, usuario, pass, puerto, ses_timeout, chan_timeout};
        return algo;
    }

    public boolean validateFolderExists(String ruta) throws JSchException, IllegalAccessException, IOException {
        SFTPValidateSourceExists sft = new SFTPValidateSourceExists();
        sft.connect((String) parametros[1], (String) parametros[2], (String) parametros[0], (int) parametros[3]);
        //sft.connect(USERNAME, PASSWORD, REMOTE_HOST, REMOTE_PORT);
        //String result = sft.executeCommand("test -d /var/www/html/solicitudes/media/files/hallmark_forms/1234 && echo 'Directory Exists'");
        String result = sft.executeCommand("test -d " + ruta + " && echo 'Directory Exists'");
        sft.disconnect();
        if (result.equals("Directory Exists")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doCopyFromLocalToRemote(String localFile, String remotePath) {

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts("/home/micharesp/.ssh/known_hosts");

            //jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
            jschSession = jsch.getSession((String) parametros[1], (String) parametros[0], (int) parametros[3]);

            // authenticate using private key
            // jsch.addIdentity("/home/mkyong/.ssh/id_rsa");
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // authenticate using password
            jschSession.setPassword((String) parametros[2]);

            // 10 seconds session timeout
            jschSession.connect((int) parametros[4]);

//            jschSession.setTimeout(30000);
            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect((int) parametros[5]);
            //sftp.connect(CHANNEL_TIMEOUT);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
            channelSftp.put(localFile, remotePath);

            // download file from remote server to local
            // channelSftp.get(remoteFile, localFile);
            channelSftp.exit();
            return true;
        } catch (JSchException | SftpException e) {
            System.err.println("Error aquí: " + e);
            e.printStackTrace();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
        System.out.println("Done");
        return false;
    }
//    public boolean exeComando(String rutar, String rutac) {

    /**
     * ejecuta el comando enviado por el parametro 'comando'
     *
     * @param comando
     * @return true si el comando se ejecutó correctamente
     */
    public boolean exeComando(String comando) {
        try {
            JSch jsch = new JSch();
            //Session session = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
            Session session = jsch.getSession((String) parametros[1], (String) parametros[0], (int) parametros[3]);
            //session.setPassword(PASSWORD);
            session.setPassword((String) parametros[2]);
            session.setConfig("StrictHostKeyChecking", "no");
            //session.connect(SESSION_TIMEOUT);
            session.connect((int) parametros[4]);
            Channel channel = session.openChannel("exec");
            //((ChannelExec) channel).setCommand("mv " + rutar + " " + rutac + " && echo 'movido'");
            ((ChannelExec) channel).setCommand(comando);
            channel.setInputStream(null);

            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public List<String> listarDirectorio(String ruta) {
//        String SFTPHOST = "10.0.20.130";
//        int SFTPPORT = 22;
//        String SFTPUSER = "root";
//        String SFTPPASS = "B7GJuaxu3Y";
//        String SFTPWORKINGDIR = "/var/www/html/solicitudes/media/files/hallmark_forms/185551/";
        String SFTPWORKINGDIR = ruta;
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            //session = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
            session = jsch.getSession((String) parametros[1], (String) parametros[0], (int) parametros[3]);
            //session.setPassword(PASSWORD);
            session.setPassword((String) parametros[2]);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);

            Vector filelist = channelSftp.ls(SFTPWORKINGDIR);
            List<String> lista = new ArrayList<>();
            for (int i = 0; i < filelist.size(); i++) {
                String[] aux = filelist.get(i).toString().split(" ");

                String name = aux[aux.length - 1].trim();
                if (name.charAt(0) != '.') {
//                    System.out.println("------------------------------");
//                    System.out.println(name);
                    lista.add(name);
                }

            }
            channelSftp.exit();
            channelSftp.disconnect();
            session.disconnect();
            return lista;
        } catch (JSchException | SftpException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * @return the parametros
     */
    public Object[] getParametros() {
        return parametros;
    }

    /**
     * @param parametros the parametros to set
     */
    public void setParametros(Object[] parametros) {
        this.parametros = parametros;
    }

}
