/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senadi.gob.ec.mod.ucc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author michael
 */
public class IepiFormDep {
    //Producción    
    public static String USER = "iepi-solicitudes";
    public static String PASSWORD = "5ad0d5c3fced39d5048f";
    public static String iepi_formularios = "jdbc:mysql://10.0.20.130:3306/iepi_formularios";   
    public static String iepi_depurar = "jdbc:mysql://10.0.20.130:3306/iepi_depurar";
    public static String iepi_casilleros = "jdbc:mysql://10.0.20.130:3306/iepi_casilleros";
    
    
    public static Connection doConnectionToFormularios() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(iepi_formularios, IepiFormDep.USER, IepiFormDep.PASSWORD);
        return con;
    }
    
    public static Connection doConnectionToDepurar() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(iepi_depurar, IepiFormDep.USER, IepiFormDep.PASSWORD);
        return con;
    }
    
    public static Connection doConnectionToCasilleros() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(iepi_casilleros, IepiFormDep.USER, IepiFormDep.PASSWORD);
        return con;
    }
}
