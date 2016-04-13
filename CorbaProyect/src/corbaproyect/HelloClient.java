package corbaproyect;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ruben
 */
import commun.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class HelloClient extends Thread {

    public String[] args = {};
    static CORBA_Interface helloImpl;
    
    public HelloClient(String[] args){
        this.args = args;
        
        this.start();
    }
    
    public void run(){
        connectToServer();
    }

    public void connectToServer() {
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

            //System.out.println("Obtained a handle on server object: " + helloImpl);
            System.out.println(helloImpl.sayHello());

        } catch (Exception e) {
            System.out.println("CORBA Server connecting...[FAILED]");
            //e.printStackTrace(System.out);
        }
        
        //Instance to connect with clients.
        Server_Socket s1 = new Server_Socket(4050, helloImpl, args);
        
        //Instance to connect with cliends.
        //Server_Socket s2 = new Server_Socket(4051, helloImpl, args);
        
    }
}
