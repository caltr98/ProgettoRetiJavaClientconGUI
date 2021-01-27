import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class Client {
    //Oggetto che rappresenta un Client Loggato, è l'oggetto da cui la GUI chiama i metodi
    private Socket socket;
    private Writer stringWriterToServer;
    private BufferedReader stringReaderFromServer;
    private String myUsrName;
    private String myPassword;
    private boolean loggedIn;
    private ClientData RMIdataFromServer;//Lista Utenti,Lista Utenti Online
    private int registryPort,serverPort;
    private HashMap<String,ChatThread> chatThread;//associo ad ogni Progetto di cui ho aperto la chat
    //un Thread che si occupa di leggerne i messaggi
    public Client(String myUsrName, String myPassword, int serverPort,int registryPort) {
        this.myPassword=myPassword;
        this.myUsrName=myUsrName;
        this.serverPort=serverPort;
        this.registryPort=registryPort;
        RMIdataFromServer=new ClientData(registryPort);
        this.chatThread=new HashMap<>();
        socket=new Socket();
    }
    public String Registration() {
        String ris=null;
        try {
            ris = this.RMIdataFromServer.Registration(myUsrName, myPassword);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return ris;
    }
    private void Connect() throws IOException {
        //Avvio della connessione col server
        socket.connect(new InetSocketAddress(serverPort));
        stringWriterToServer=new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset());
        stringReaderFromServer=new BufferedReader(new InputStreamReader(socket.getInputStream(),Charset.defaultCharset()),248);
    }
    public String Login()  {
        String response;
        int portNumForCallBack;//numero di porta per oggetto che Server usera per la CallBack RMI
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                Connect();//connessione TCP avviata come prima operazione al primo Login
                //se eventualmente Login fallisce resta aperta per i tentativi successivi
            }
            sendStringToServer("0 login -\r"+myUsrName+" -\r"+myPassword);
            response = getStringFromServer();
            if (response.equals("LOGINSUCCESS")) {
                loggedIn = true;
                System.out.println("Loggato al server");
                portNumForCallBack=getFreePortnum();
                RMIdataFromServer.Login(myUsrName, portNumForCallBack);
                return "LOGGED";
            } else return response;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getFreePortnum() {//metodo che trova una porta libera da assegnare
        //al client per la callback RMI
        int port=-1;
        ServerSocket trySocket;
        try {trySocket=new  ServerSocket(0);//prova a creare una socket alla porta 0, allora ne crea una sulla prima porta
            //disponibile
             port=trySocket.getLocalPort();
             trySocket.close();
             return port;//se eseguite in concorrenza due operazioni del genere su processi differenti
            //potrebbe essere ritornata la stessa porta
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Non ci sono porte da assegnare");
        System.exit(0);
        return -1;
    }

    public String Logout(){
        if(!loggedIn){
            return "NOT LOGGED";
        }
        try {
            sendStringToServer("-1 logout");//Viene inviata richiesta di Logout al server con successo
        } catch (IOException e) {
            e.printStackTrace();
        }
        //non si attende nessuna risposta dal server
        loggedIn=false;
        for (ChatThread t:
             chatThread.values()) {
            t.closeSocket();//Chiusura della MulticastSocket
            try {
                System.out.println("wait on join ChatThread");
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        chatThread.clear();
        return "LOGOUT REQUESTED";
    }
    public void CloseConnection()  {
        if(loggedIn){
            Logout();//Se si decidere di chiudere la connessione è necessario il Logout
        }
        try {
            for (ChatThread t:
                    chatThread.values()) {//chiusura delle socket delle chat se sono state aperte e lo sono
                t.closeSocket();//Chiusura della MulticastSocket
                try {
                    System.out.println("wait on join ChatThread");
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            chatThread.clear();

            if(!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String listProjects (){
        String result=null ;
        if(!loggedIn){
            return "NOT LOGGED";
        }
        try {
            sendStringToServer("10 listprojects");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String createProject(String ProjectName)  {
        String result=null;
        try {
            sendStringToServer("1 createproject"+"-\r"+ProjectName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String addMember(String ProjectName,String nickUtente)  {
        if(!loggedIn){
            return "NOT LOGGED";
        }

        String result=null;
        try {
            sendStringToServer("2 addmember-\r"+ProjectName+"-\r"+nickUtente);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String showMembers(String projectName) {
        if(!loggedIn){
            return "NOT LOGGED";
        }

        String result=null;
        try {
            sendStringToServer("3 showmembers-\r"+projectName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String showCards(String projectName) {
        if(!loggedIn){
            return "NOT LOGGED";
        }

        String result=null;
        System.out.println("projectName di cui cerco cards: "+ projectName);
        try {
            sendStringToServer("4 showcards-\r"+projectName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String showCard(String projectName,String cardName) {
        if(!loggedIn){
            return "NOT LOGGED";
        }

        String result=null;
        try {
            System.out.println("CardRequested:"+cardName);
            sendStringToServer("5 showcard-\r"+projectName+"-\r"+cardName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public String readChat(String projectName) {
        if(!loggedIn){
            return "NOT LOGGED";
        }
        String result = null;
        if(!chatThread.containsKey(projectName)) {
            System.out.println("invio dir readChat");
            try {
                sendStringToServer("11 readchat-\r" + projectName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                result = getStringFromServer();
                chatThread.put(projectName,new ChatThread(result,projectName,myUsrName));
                chatThread.get(projectName).start();//avvio di Thread-Chat Parallelo al Main-Thread ed altri
                //Thread-Chat, fintanto che l'utente sarà loggato aggiornerà la chat
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            chatThread.get(projectName).DisplayChat();//frame della Chat Riappare
        }
        return result;
    }

    public String getCardHistory(String projectName,String cardName)  {
        if(!loggedIn){
            return "NOT LOGGED";
        }
        String result=null;
        try {
            sendStringToServer("6 getcardhistory-\r"+projectName+"-\r"+cardName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String cancelProject(String projectName)  {
        if(!loggedIn){
            return "NOT LOGGED";
        }
        String result=null;
        try {
            sendStringToServer("7 cancelproject-\r"+projectName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String addCard(String projectName, String cardName, String descrizione) {
        if(!loggedIn){
            return "NOT LOGGED";
        }
        String toDescritone=descrizione.replaceAll("\n"," ");
        String result=null;
        try {
            sendStringToServer("8 addcard-\r"+projectName+"-\r"+cardName+"-\r"+toDescritone);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String moveCard(String projectName,String cardName,String listaPartenza,String listaDestinazione)  {
        if(!loggedIn){
            return "NOT LOGGED";
        }
        String result=null;
        try {
            sendStringToServer("9 movecard-\r"+projectName+"-\r"+cardName+"-\r"+listaPartenza+"-\r"+listaDestinazione);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            result=getStringFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
    public String sendChatMsg(String projectName,String  messaggio){
        String result="a";
        return result;
    }


    private void sendStringToServer(String s) throws IOException {
        stringWriterToServer.write(s);
        stringWriterToServer.flush();
    }
    private String getStringFromServer() throws IOException {
        String ris;
        ris = stringReaderFromServer.readLine();
        return ris;
    }
    public String[] listUsers(){
        return this.RMIdataFromServer.printAllUsers();
    }
    public String[] listOnlineusers(){
        return this.RMIdataFromServer.printAllOnlineUsers();
    }

    public void setMyUsrName(String myUsrName) {
        this.myUsrName = myUsrName;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    public String getMyUsrName() {
        return myUsrName;
    }
}
