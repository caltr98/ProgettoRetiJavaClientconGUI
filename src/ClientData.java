import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ClientData {
    //Classe che contiene le informazioni che il Client ha su se stesso e la lista degli Utenti
    //Lista Utenti  + Lista Utenti Online
    private ArrayList<String> UsersList;//Lista degli utenti ,verrà aggiornata dal server tramite CallBack
    private ArrayList<String> OnlineUsersList;//Aggiornata dal server mediante CallBack
    private ClientCallBack ServerListContainer;//Conterrà le liste aggiornate dal server con callback
    private ClientNotifyInterface callbackService;
    ServerCallbackRegistration callbackRegistration;//stub per invocare il metodo register e unregister alle callback
    boolean logged;
    int registryPort;
    public ClientData(int registryPort){
        this.registryPort=registryPort;
        logged=false;
        /*this.ServerListContainer=ServerListContainer;
        UsersList= null;//null fino al login nel server ServerListContainer.getListUsers();
        OnlineUsersList=null//null fino al login nel server  ServerListContainer.getListUsers();
        */

    }
    public String Registration(String usrname,String password) throws RemoteException, NotBoundException {
        //Registrazione mediante RMI
        if(!logged) {
            RegistrationInterface regService;
            Registry registry = LocateRegistry.getRegistry(registryPort);//Registry trovato in una posizione concordata
            //ricerca nel registry dell'oggeto remoto per la registrazione
            regService = (RegistrationInterface)  registry.lookup("Server-Utenti");
            //invocazione metodo remoto di registrazione
            try {
                regService.registration(usrname, password);
            }catch (IllegalArgumentException e){
                return e.getMessage();
            }
            return "Success Registration";
        }
        else{
            System.out.println("Non puoi registrare un nuovo utente se sei loggato ad un account");
            return "Fail Registration";
        }

    }
    public void Login(String usrname,int portnum) throws RemoteException, NotBoundException {
        //Login fase RMI, client si registra per le callback
        if(!logged) {
            this.ServerListContainer=new ClientCallBack();//oggetto che verrà aggiornato dal server remoto tramite callback
            Registry registry = LocateRegistry.getRegistry(registryPort);//Registry trovato in una posizione concordata
            this.callbackRegistration = (ServerCallbackRegistration)  registry.lookup("Server-Callback");
            this.callbackService = (ClientNotifyInterface) UnicastRemoteObject.exportObject(this.ServerListContainer, portnum);
            this.callbackRegistration.callbackregistration(this.callbackService,usrname);
            logged=true;
        }
    }
    public void Logout() throws RemoteException {
        if(logged){
            this.logged=false;
            //this.callbackRegistration.unregistercallback(this.callbackService);
        }
    }
    public String[] printAllOnlineUsers(){
        System.out.println("print di tutti gli utenti ONLINE");
        OnlineUsersList=ServerListContainer.getListOnlineUsers();//Aggiorno all'ultima versione della lista

        for (String s:
             OnlineUsersList) {
            System.out.println("utente online "+s);
        }
        return OnlineUsersList.toArray(new String[0]);
    }
    public String[] printAllUsers(){
        if(logged) {
            UsersList = ServerListContainer.getListUsers();//Aggiorno all'ultima versione della lista
            for (String s :
                    UsersList) {
                System.out.println("utente :"+s);
            }
        }
        return UsersList.toArray(new String[0]);
    }
}
