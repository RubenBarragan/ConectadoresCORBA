/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corbaproyect;

import commun.CORBA_Interface;
import commun.CORBA_InterfaceHelper;
import static corbaproyect.HelloClient.helloImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 *
 * @author Pedro
 */
public class Server_Thread extends Thread {

    private Socket theClient;
    private DataOutputStream toClient;
    private DataInputStream fromClient;
    private CORBA_Interface helloImpl;
    private Connection conn;
    private String serverIP;
    private Server_Socket PreviusClass;
    private String[] args;

    public Server_Thread(Socket _theClient, CORBA_Interface _helloImpl, Connection _conn, String _address, Server_Socket a, String[] _args) {
        this.theClient = _theClient;
        this.helloImpl = _helloImpl;
        this.conn = _conn;
        this.serverIP = _address;
        this.PreviusClass = a;
        this.args = _args;

        try {
            toClient = new DataOutputStream(_theClient.getOutputStream());
            fromClient = new DataInputStream(_theClient.getInputStream());
        } catch (IOException ex) {
            System.out.println("Constructor Server thread");
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CORBA_Interface connectToServer(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is 
            // part of the Interoperable naming Service.  
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "CORBA_Project";
            helloImpl = CORBA_InterfaceHelper.narrow(ncRef.resolve_str(name));

            System.out.println("RMI Connection established...[OK]");
            PreviusClass.helloImpl = helloImpl;

            if (!PreviusClass.sendBD) {
                Statement stmt2 = PreviusClass.conn.createStatement();
                ResultSet rs = stmt2.executeQuery("select * from devices");

                while (rs.next()) {
                    stub.recoveryBD(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                }
                PreviusClass.sendBD = true;
                stub.giveMeYourBD();
            }

        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
        return helloImpl;
    }

    public void disconnectClient() {
        try {
            System.out.println("closed socket: " + theClient.toString());
            theClient.close();
        } catch (IOException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String insertPerson(String ibt, String name, String password) {

        String returnedQuery = "Cosa";

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            stmt.executeUpdate("INSERT INTO `locator`.`devices` (`id_bluetooth`, `name`, `password`) VALUES ('" + ibt + "', '" + name + "', '" + password + "')");

            System.out.println("All right");

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnedQuery;
    }

    public boolean checkID(String idBT, String name, String password) {
        boolean trueID = false;

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices where id_bluetooth = '" + idBT + "'");

            while (rs.next()) {
                if (rs.getString(2).equals(idBT) && rs.getString(3).equals(name) && rs.getString(6).equals(password)) {
                    trueID = true;
                }
            }
            con.close();

            return trueID;
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trueID;
    }

    public String buscarPersona(String nombre) {
        String datosPersona = "";
        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices where name = '" + nombre + "'");

            while (rs.next()) {
                datosPersona = rs.getString(3) + "#" + rs.getString(4) + "#" + rs.getString(5);
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return datosPersona;
    }

    public String buscarTodos() {
        String datosPersonas = "";
        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices");
            int i = 0;
            while (rs.next()) {
                datosPersonas = datosPersonas + rs.getString(3) + "#" + rs.getString(4) + "#" + rs.getString(5);
                i++;
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return datosPersonas;
    }

    public boolean checkIDexist(String idBT, String name, String password) {
        boolean trueID = false;

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices where id_bluetooth = '" + idBT + "'");

            if (rs.next()) {
                trueID = true;
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return trueID;
    }

    @Override
    public void run() {
        String msg = "";

        try {

            //Receive msg from the client
            msg = fromClient.readUTF();

            String[] argsw = msg.split("#");

            if (argsw[0].equals("ingresar")) {
                if (checkID(argsw[1], argsw[2], argsw[3])) {
                    toClient.writeUTF("Ingresa");
                } else {
                    toClient.writeUTF("noIngresa");
                }
            } else if (argsw[0].equals("registrar")) {
                if (!checkIDexist(argsw[1], argsw[2], argsw[3])) {
                    insertPerson(argsw[1], argsw[2], argsw[3]);
                    toClient.writeUTF("Registrado");
                } else {
                    toClient.writeUTF("yaExiste");
                }
            } else if (argsw[0].equals("buscarPersona")) {
                String persona = buscarPersona(argsw[2]);
                toClient.writeUTF(persona);
            } else if (argsw[0].equals("buscarTodos")) {
                String Todos = buscarTodos();
                toClient.writeUTF(Todos);
            } else {

                System.out.println("Message Recived: " + msg);
                String datetime = "2016-04-01 23:55:20";
                String ibt = "bt123456789";
                //Create the query to the local database.
                if (PreviusClass.conn != null) {
                    Statement stmt = PreviusClass.conn.createStatement(); //stmt is the object to create statements.
                    stmt.executeUpdate("UPDATE `devices` SET `lugar`='" + msg + "',`datetime`='" + datetime + "' WHERE id_bluetooth='" + ibt + "'");
                    System.out.println("Local query performed...[OK].");
                    toClient.writeUTF("Acknowledge");
                } else {
                    Connection conni = PreviusClass.cbd.connectBD();
                    PreviusClass.conn = conni;
                    toClient.writeUTF("noDB");
                }

                if (helloImpl != null) {
                    helloImpl.updateRow(ibt, msg, datetime);
                    System.out.println("External query performed...[OK]");
                } else {
                    PreviusClass.sendBD = false;
                    connectToServer(this.args);
                }
            }

        } catch (IOException ex) {
            //Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("External query performed...[FAILED]");

            try {
                helloImpl.sayHello(); // Function to test RMI connection.
            } catch (Exception ex1) {
                PreviusClass.sendBD = false;
                connectToServer(args);
            }

        } catch (SQLException ex) {
            //no DB connection
            try {
                System.out.println("DB connection ... [FAILED]");
                toClient.writeUTF("noBD");

                Connection conn = PreviusClass.cbd.connectBD();
                PreviusClass.conn = conn;

            } catch (IOException ex1) {
                System.out.println("No contestavion al cliente ");
                Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
        disconnectClient();
    }
}
