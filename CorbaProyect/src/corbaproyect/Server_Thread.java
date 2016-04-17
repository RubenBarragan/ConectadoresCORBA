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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private Server_Socket PreviusClass;
    private String[] args;

    public Server_Thread(Socket _theClient, CORBA_Interface _helloImpl, Connection _conn, String _address, Server_Socket a, String[] _args) {
        this.theClient = _theClient;
        this.helloImpl = _helloImpl;
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
                    helloImpl.recoveryBD(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
                }
                PreviusClass.sendBD = true;
                helloImpl.giveMeYourBD();
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
                datosPersonas += rs.getString(3) + "#" + rs.getString(4) + "#" + rs.getString(5) + "#";
                i++;
            }
            datosPersonas = deleteLastChar(datosPersonas);
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

    public String getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //System.out.println(sdf.format(cal.getTime()));
        return sdf.format(cal.getTime());
    }

    public String deleteLastChar(String s) {
        if (!s.isEmpty()) {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    public String searchArea(String area) {
        String data = "";
        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices where lugar = '" + area + "'");

            while (rs.next()) {
                data += rs.getString(2) + "#" + rs.getString(3) + "#" + rs.getString(4) + "#" + rs.getString(5) + "#";
            }
            data = deleteLastChar(data);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data; //id_bluetooth#name#lugar#date#id_bluetooth#name#lugar#date...
    }

    @Override
    public void run() {
        String msg = "";

        try {
            //Receive msg from the client
            msg = fromClient.readUTF();

            String[] dataSet = msg.split("#"); //ingresar#id_bluetooth#nombre#password

            if (dataSet[0].equals("ingresar")) { //ingresar#id_bluetooth#nombre#password
                //Check if the user and password match.
                if (checkID(dataSet[1], dataSet[2], dataSet[3])) {
                    toClient.writeUTF("ingresa");
                } else {
                    toClient.writeUTF("noIngresa");
                }
            } else if (dataSet[0].equals("registrar")) { //registras#id_bluetooth#nombre#password
                //Check if the person already exists.
                if (!checkIDexist(dataSet[1], dataSet[2], dataSet[3])) {
                    insertPerson(dataSet[1], dataSet[2], dataSet[3]);
                    helloImpl.insertRow(dataSet[1], dataSet[2], "", "2010-04-13 06:50:44", dataSet[3]);
                    toClient.writeUTF("signUp");
                } else {
                    toClient.writeUTF("userExists");
                }
            } else if (dataSet[0].equals("searchPerson")) {  //searchPerson#name
                String persona = buscarPersona(dataSet[1]);
                toClient.writeUTF(persona); //notFound if user doesn't exist and id_bluetooth#name#lugar#date
            } else if (dataSet[0].equals("searchAll")) { //searchAll
                String Todos = buscarTodos();
                toClient.writeUTF(Todos); //id_bluetooth#name#lugar#date#id_bluetooth#name#lugar#date...
            } else if (dataSet[0].equals("searchArea")) { //searchArea#location
                String userInArea = searchArea(dataSet[1]);
                toClient.writeUTF(userInArea); //id_bluetooth#name#lugar#date#id_bluetooth#name#lugar#date...
            } else if (dataSet[0].equals("updateLocation")) { //updateLocation#id_bluetooth#location
                //System.out.println("Message Recived: " + msg);
                String ibt = dataSet[1];
                String lugar = dataSet[2];
                String datetime = getDate();
                //Create the query to the local database.
                if (PreviusClass.conn != null) {
                    Statement stmt = PreviusClass.conn.createStatement(); //stmt is the object to create statements.
                    stmt.executeUpdate("UPDATE `devices` SET `lugar`='" + lugar + "',`datetime`='" + datetime + "' WHERE id_bluetooth='" + ibt + "'");
                    System.out.println("Local query performed...[OK].");
                    toClient.writeUTF("Acknowledge");
                } else {
                    Connection conni = PreviusClass.cbd.connectBD();
                    PreviusClass.conn = conni;
                    toClient.writeUTF("noDB");
                }
                if (helloImpl != null) {
                    try {
                        helloImpl.updateRow(ibt, lugar, datetime);
                        System.out.println("External query performed...[OK]");
                    } catch (Exception exx) {
                        PreviusClass.sendBD = false;
                        connectToServer(args);
                    }
                } else {
                    PreviusClass.sendBD = false;
                    connectToServer(this.args);
                }
            }

        } catch (IOException ex) {
            //Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("External query performed...[FAILED]");
            try {
                helloImpl.sayHello(); // Function to test CORBA connection.
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
                System.out.println("No contestación al cliente");
                Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
        disconnectClient();
    }
}
