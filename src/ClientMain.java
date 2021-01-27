import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class ClientMain {
    public static void main(String args[]) {
        int serverPort,registryPort;//invece porta su cui esportare oggetto ClientCallBack viene trovata automaticamente
        if (!(args.length <1)) {//se c'è almeno un argomento
            //Se si inserisce da linea di comando anche n.porta del Server
            //controllo che porta sia   un numero
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("ERR -arg 1");
                return;
            }
            if (args.length == 2) {
                try {
                    registryPort = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("ERR -arg 2");
                    return;
                }
            } else {
                System.out.println("Too many arguments");
                return;
            }
        }
        else {
            serverPort = 6364;//numero di porta di Default del Server in questo Progetto
            registryPort=7897;//numero di porta di Default del Registry da cui ottenere metodi RMI Registrazione e Login
        }
        SocketChannel socketChannel = null;
        Writer stringWriterToServer = null;
        BufferedReader stringReaderFromServer=null;
        Scanner sc = new Scanner(System.in);
        String  s2, s3;
        int i;
        byte[] tmpTOString;
        byte[] bytearray;
        new LoginRegistrationPage(serverPort,registryPort);
        /*  senza GUI si potrebberò inviare comando direttamente con un oggetto Client
            Client me=new Client("Eren","Yeager",6364);
            System.out.println(me.Registration());
            System.out.println(me.Login());
            System.out.println(me.Login());
            System.out.println(me.listProjects());

            System.out.println(me.createProject("Viaggio"));
            System.out.println(me.addCard("Viaggio","Passaporto","Da richiedere"));
            System.out.println(me.moveCard("Viaggio","Passaporto","TODO","INPROGRESS"));
            System.out.println(me.getCardHistory("Viaggio","Passaporto"));
            System.out.println(me.showCard("Viaggio","Passaporto"));
            System.out.println(me.cancelProject("Viaggio"));
            System.out.println(me.moveCard("Viaggio","Passaporto","INPROGRESS","DONE"));
            System.out.println(me.listProjects());
            System.out.println(me.getCardHistory("Viaggio","Passaporto"));
            System.out.println(me.cancelProject("Viaggio"));
            me.listUsers();
            me.listOnlineusers();
            System.out.println(me.listProjects());
            .............................................................
            */
    }
}
