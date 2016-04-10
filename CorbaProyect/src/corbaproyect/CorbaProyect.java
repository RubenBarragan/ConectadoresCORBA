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
        String[] argsClient = {"-ORBInitialHost","nameserverhost","-ORBInitialPort","1050"};
        
        HelloServer hs = new HelloServer();
        hs.startServer(argsServer);
        
        HelloClient hc = new HelloClient();
        hc.connectToServer(argsClient);
    }
}
