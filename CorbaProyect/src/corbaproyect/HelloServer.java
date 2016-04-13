/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corbaproyect;

/**
 *
 * @author Ruben
 */
// HelloServer.java
// Copyright and License 
// HelloServer.java
// Copyright and License 
import commun.*;
import static corbaproyect.HelloClient.helloImpl;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class HelloImpl extends CORBA_InterfacePOA {

    private ORB orb;
    private String[] argsServer, argsClient;

    public HelloImpl(String[] argss, String[] argsc) {
        this.argsServer = argss;
        this.argsClient = argsc;
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public String sayHello() {
        return "CORBA Server connection established...[OK]";
    }

    public String selectAll() {

        String returnedQuery = "";

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices");

            while (rs.next()) {
                returnedQuery += rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5);
                //System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3) + " " + rs.getInt(4));
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnedQuery;
    }

    //Only local computing.
    public int selectRow(String id, String otherDate) {
        // 0 = no existe 1 = si existe y hacer cambio 2= si existe y no hacer cambio
        int returnedQuery = 0;
        String theDate = "";

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();
            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("select * from devices where id_bluetooth = '" + id + "'");

            while (rs.next()) {
                theDate = rs.getString(5);
                returnedQuery = compareDates(theDate, otherDate);
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnedQuery;
    }

    public int compareDates(String theDate, String otherDate) {
        int result = -1;
        Timestamp timestamp2 = null;
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = dateFormat.parse(theDate);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());

            Date parsedDate2 = dateFormat.parse(otherDate);
            timestamp2 = new java.sql.Timestamp(parsedDate2.getTime());

            if (timestamp.before(timestamp2)) {
                result = 1;
            } else {
                result = 2;
            }

        } catch (Exception e) {//this generic but you can control another types of exception
            System.out.println(e.toString());
        }
        return result;
    }

    public String insertRow(String ibt, String name, String lugar, String datetime, String pass) {

        String returnedQuery = "Cosa";

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            stmt.executeUpdate("INSERT INTO `locator`.`devices` (`id_bluetooth`, `name`, `lugar`, `datetime`, `password`) VALUES ('" + ibt + "', '" + name + "', '" + lugar + "', '" + datetime + "','" + pass + "')");

            System.out.println("All right");

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnedQuery;
    }

    public String updateRow(String ibt, String lugar, String datetime) {

        String returnedQuery = "";

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            stmt.executeUpdate("UPDATE `devices` SET `lugar`='" + lugar + "',`datetime`='" + datetime + "'  WHERE id_bluetooth='" + ibt + "'");

            System.out.println("All right");

//            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnedQuery;
    }

    public void recoveryBD(String ibt, String name, String lugar, String datetime, String pass) {
        int result = exists_idBT(ibt, datetime);
        if (result == 1) {
            updateRow(ibt, lugar, datetime);
        } else if (result == 0) {
            insertRow(ibt, name, lugar, datetime, pass);
        }
    }

    //Only local computing.
    public int exists_idBT(String id, String otherDate) {
        int s = selectRow(id, otherDate);
        return s;
    }

    //Only local computing.
    public void testBDConnection() {
        ConnectBD cbd = new ConnectBD();
        Connection con = null;

        con = cbd.connectBD();
        while (con == null) {
            con = cbd.connectBD();
        }
    }

    public boolean isEmpty() {
        boolean _isEmpty = false;

        ConnectBD cbd = new ConnectBD();
        Connection con = null;
        try {
            con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            ResultSet rs = stmt.executeQuery("SELECT * FROM `devices`");

            if (!rs.isBeforeFirst()) {
                System.out.println("No data");
                _isEmpty = true;
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);

        }

        return _isEmpty;
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

        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
        return helloImpl;
    }

    public void giveMeYourBD() {

        ResultSet rs = null;

        ConnectBD cbd = new ConnectBD();
        try {
            Connection con = cbd.connectBD();

            //stmt is the statement's object. It's used to create statements or queries.
            Statement stmt = con.createStatement();

            //devices is the table's name.
            rs = stmt.executeQuery("select * from devices");

            helloImpl = connectToServer(argsClient);
            while (rs.next()) {
                helloImpl.recoveryBD(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(HelloImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class HelloServer extends Thread {

    String[] argsServer = {};
    String[] argsClient = {};

    public HelloServer(String[] argsServer, String[] argsClient) {
        this.argsServer = argsServer;
        this.argsClient = argsClient;

        this.start();
    }

    public void run() {
        startServer();
    }

    public void startServer() {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(argsServer, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            HelloImpl helloImpl = new HelloImpl(argsServer, argsClient);
            helloImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(helloImpl);
            CORBA_Interface href = CORBA_InterfaceHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef
                    = orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = "CORBA_Project";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);

            //Test DB and connect.
            helloImpl.testBDConnection();

            System.out.println("HelloServer ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");
    }
}
