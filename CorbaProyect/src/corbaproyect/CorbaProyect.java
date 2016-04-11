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
public class CorbaProyect {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String[] argsServer = {"-ORBInitialPort","1050"};
        String[] argsClient = {"-ORBInitialHost","192.168.1.70","-ORBInitialPort","1050"};
        
        //Create an instance of the Server.
        HelloServer hs = new HelloServer(argsServer);
        
        //Create an instance of the client.
        HelloClient hc = new HelloClient(argsClient);
    }
}