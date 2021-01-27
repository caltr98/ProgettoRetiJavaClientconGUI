import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;

public class ClientCallBack extends RemoteObject implements ClientNotifyInterface {
    private ArrayList<String> Users;
    private ArrayList<String> onlineUsers;
    public ClientCallBack()  throws RemoteException{
        super();
        Users=new ArrayList<>();
        onlineUsers=new ArrayList<>();
    }

    @Override
    public void updateUsers(ArrayList<String> Users) throws RemoteException{
        synchronized (this.Users) {
            this.Users = Users;
        }
    }

    @Override
    public void updateOnlineUsers(ArrayList<String> onlineUsers) throws RemoteException {
        synchronized (this.onlineUsers) {
            this.onlineUsers = onlineUsers;
        }
    }
    public ArrayList<String> getListUsers(){
        synchronized (Users) {
            return Users;
        }
    }
    public ArrayList<String> getListOnlineUsers(){
        synchronized (onlineUsers){
            return onlineUsers;
        }
    }
}
