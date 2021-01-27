import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import static java.awt.Color.black;

public class   ChatThread extends Thread {
    private MulticastSocket chatSocket;
    private String chatAddress;//ottenuto dal Server alla prima readChat;
    private int chatPort;
    private ChatFrame chatFrame;
    private String ProjectName;
    private String userName;
    boolean ChatThreadStopped;

    private int mexNum;//numera i messaggi inviati da questo utente in modo da accorgersi di messaggi arrivati out of order
    private InetSocketAddress adrr;//new InetSocketAddress(chatAddress,chatPort);

    public ChatThread(String chatAddress,String ProjectName,String userName){
        String[]addresValues=chatAddress.split("\u2407");//server ha restituito address+porta
        this.chatAddress=addresValues[0];
        this.chatPort= Integer.parseInt(addresValues[1]);//ottengo valore numero porta
        System.out.println("numero di porta tradotto"+chatPort);
        this.userName=userName;
        this.ProjectName=ProjectName;
        this.mexNum=1;
        ChatThreadStopped=false;
        try {
            adrr=new InetSocketAddress(InetAddress.getByName(this.chatAddress),chatPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            chatSocket=new MulticastSocket(chatPort);
            chatSocket.setTimeToLive(1);
            //interfaccia settata come lato server a localhost
            //chatSocket.joinGroup(adrr,NetworkInterface.getByInetAddress(InetAddress.getByName("localhost")));
            //chatSocket.joinGroup(adrr,NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));

            //joinGroup (se tutto va bene e non ci sono problemi con le network interface funziona
            chatSocket.joinGroup(adrr,null);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void DisplayChat(){
        this.chatFrame.displayMe();
        System.out.println("apertura della chat address "+chatAddress+ " di " +this.ProjectName );
    }
    public void run(){
        System.out.println("Avvio di ChatThread: "+ Thread.currentThread().getName() +" di "+this.ProjectName);
        this.chatFrame=new ChatFrame();//avvio di ChatFrame Swing che attiverà operazioni di invio e riceverà stringhe da leggere
        ChatConversation();//il Thread appena creato leggerà in questo metodo i messaggi in arrivo mediante receive bloccante
        return;
    }

    public class ChatFrame extends JFrame implements WindowListener, ActionListener {
        JTextField NewMessageField;
        JTextPane chatBoxPane;
        JButton sendMessageB;
        JPanel ChatPanel,sendPanel;
        HashMap<String,Color>colorHashMap;//Assegno un colore ad ogni utente
        public ChatFrame(){
            this.setTitle("Chat: "+userName+" "+ProjectName);//
            this.setIconImage(new ImageIcon("chat.png").getImage());//ringrazio flaticon.com
            colorHashMap=new HashMap<>();
            this.setVisible(true);
            this.getDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.addWindowListener(this);
            ChatPanel = new JPanel();
            ChatPanel.setLayout(new BorderLayout());

            sendPanel = new JPanel();
            sendPanel.setBackground(new Color(127, 70, 70));
            sendPanel.setLayout(new GridBagLayout());

            NewMessageField = new JTextField(25);
            NewMessageField.requestFocusInWindow();

            sendMessageB = new JButton("INVIA");
            sendMessageB.addActionListener(this) ;

            //chatBox = new JTextArea();
            chatBoxPane=new JTextPane();
            //chatBoxPane.setEditable(false);//solo visualizzazione
            chatBoxPane.setFont(new Font("TimesRoman", Font.PLAIN, 25));
            //chatBoxPane.setLineWrap(true);

            JScrollPane chatPane=new JScrollPane(chatBoxPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            chatPane.setPreferredSize(new Dimension(650,300));
            ChatPanel.add(chatPane, BorderLayout.CENTER);
            chatBoxPane.replaceSelection("Nome utente - Numero Messaggio - Messaggio");
            chatBoxPane.setEditable(false);

            GridBagConstraints left = new GridBagConstraints();
            left.anchor = GridBagConstraints.LINE_START;
            left.fill = GridBagConstraints.BOTH;

            GridBagConstraints right = new GridBagConstraints();
            right.insets = new Insets(0, 20, 0, 0);
            right.anchor = GridBagConstraints.LINE_END;
            right.fill = GridBagConstraints.BOTH;

            sendPanel.add(NewMessageField, left);
            sendPanel.add(sendMessageB, right);

            sendPanel.setPreferredSize(new Dimension(100,100));
            ChatPanel.add(BorderLayout.SOUTH, sendPanel);

            this.add(ChatPanel);
            this.pack();
        }
        public void WriteToTextBox(String h,String b){
            String sender,messagenum,onlymessage;
            chatBoxPane.setEditable(true);

            sender= h.substring(h.indexOf("SENDER:")+7,h.indexOf("\n"));//nome utente è tra SENDER: il primo \n
            messagenum=h.substring((h.indexOf("\n NUM")+6),h.indexOf("END\n"));
            onlymessage=b;//resto è il messaggio
            System.out.println("sender:"+sender+" "+"NUM: "+messagenum+"message: "+onlymessage);
            StyleContext sc = StyleContext.getDefaultStyleContext();
            Color userColor;
            if((userColor=colorHashMap.get(sender))==null){
                userColor=new Color(((int)(Math.random() * 0x1000000)));//creo un colore random
                colorHashMap.put(sender,userColor);
            }
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, userColor);

            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Impact");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

            int len = chatBoxPane.getDocument().getLength();
            chatBoxPane.setCaretPosition(len);
            chatBoxPane.setCharacterAttributes(aset, false);
            if(sender.equals(ChatThread.this.userName)){
                chatBoxPane.replaceSelection("\n"+"me "+messagenum+":>");
            }
            else {
                chatBoxPane.replaceSelection("\n" + sender + " " + messagenum + ":<");
            }

            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, black);
            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "TimesRoman");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

            len = chatBoxPane.getDocument().getLength();
            chatBoxPane.setCaretPosition(len);
            chatBoxPane.setCharacterAttributes(aset, false);
            chatBoxPane.replaceSelection(onlymessage);
            chatBoxPane.setEditable(false);


        }
        public void WriteToTextBoxServerMessage(String body) {
            chatBoxPane.setEditable(true);

            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(97, 97, 97));
            int len = chatBoxPane.getDocument().getLength();
            chatBoxPane.setCaretPosition(len);
            chatBoxPane.setCharacterAttributes(aset, false);
            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Impact");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);
            len = chatBoxPane.getDocument().getLength();
            chatBoxPane.setCaretPosition(len);
            chatBoxPane.setCharacterAttributes(aset, false);
            chatBoxPane.replaceSelection("\n\n"+"---"+body+"---"+"\n");
            chatBoxPane.setEditable(false);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==sendMessageB){
                String messageFromTextField=this.NewMessageField.getText();
                if(messageFromTextField.equals("")){
                    JOptionPane.showMessageDialog(null,"Nessun messaggio Inserito","Empty Message",JOptionPane.ERROR_MESSAGE);//messaggio di errore
                }
                else {
                    this.NewMessageField.setText("");//field ripulito
                    try {
                        ChatSendMessage(messageFromTextField,ChatThread.this.userName);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

        }
        private void getDefaultCloseOperation(int op) {
            super.setDefaultCloseOperation(op);
        }

        public void displayMe(){
            this.setVisible(true);
        }
        public void HideMe(){
            this.setVisible(false);
        }

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            HideMe();
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }

    }
    public void ChatConversation()  {
        //Metodo eseguito da un ChatThread-Utente per ottenere i messaggi dalla conversazione
        String receivedString;
        String header="";
        String body="";
        byte[]datagramBuffer=new byte[1024];//Possono arrivare messaggi di max 1024: caretteri + Dimensione Header
        DatagramPacket toReceive = new DatagramPacket(datagramBuffer, 1024);
        while (true) {
            try {
                chatSocket.receive(toReceive);//Attesa di un nuovo messaggio in Chat,bloccato qui il thread
            }catch (IOException e){//è stata chiusa la Socket dal Main-Thread, vuol dire che si sta eseguendo il Logout,allora termina Chat-Thread
                //oppure c'è davvero una IOException per altri motivi
                System.out.println(Thread.currentThread().getName()+"arrivata eccezzione");
                synchronized (ChatThread.this){

                    if(this.ChatThreadStopped){
                        return;//return dal metodo ChatConversation segna la fine del Thread
                    }
                    else e.printStackTrace();//è arrivata una eccezione per altro motivo
                }
            }
            System.out.println("Multicast DatagramPacket  ricevuto da "+toReceive.getAddress().toString() +" porta: "+ toReceive.getPort());
            receivedString = new String(toReceive.getData(),toReceive.getOffset(),toReceive.getLength());

            //Lettura messaggi chat in 3 fasi:1 Avviso nuovo messaggio, 2 lettura nuovo messaggio, 3 conferma fine mex
            if (receivedString.matches("^(NEW!) SENDER:"+LoginRegistrationPage.regexNospace+"(\n NUM:)[0-9]+ (END\n)(?is).*")) {//controllo che arrivi un messaggio con formato header-body-trailer//controllo che arrivi un messaggio con formato header-body-trailer
                //Conservo header per avere informazioni su chi ha inviato il messaggio e orario di invio
                header=receivedString.substring(0,receivedString.indexOf("END\n")+4);
                body=receivedString.substring(receivedString.indexOf("END\n")+4);//resto della stringa è il body
                this.chatFrame.WriteToTextBox(header,body);
            }
            else if (receivedString.equals("\nPROJECT DELETED\n")){//nel caso in cui un progetto viene
                //cancellato chiudiamo la chat,terminiamo il thread e eliminiamo il frame della chat
                //propaghiamo anche il messaggio Chat Closed, verrà inviato dal Server
                try {
                    ChatSendENDMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chatSocket.close();
                this.chatFrame.dispose();//eliminazione del chatFrame in caso di cancellazione progetto
                return;//fine della funzione, thread torna in run e infine termina
            }
            else if(receivedString.matches("^SERVER:"+LoginRegistrationPage.regexWIRTHspace+"\nEND\n(?is).*")){//Messagio dal server sul cambiamento
                //di lista di una Card
                body=receivedString.substring(receivedString.indexOf("END\n")+4);//resto della stringa è il body

                this.chatFrame.WriteToTextBoxServerMessage(body);

            }
            else{
                System.out.println(receivedString+" match message format errato");
            }
            toReceive.setLength(1024);//Rendo dispobile l'intero buffer per la ricezione*/
        }
    }
    public void ChatSendMessage(String message,String usrname) throws IOException {
        //sono limitato nella dimensione del messaggio, quindi se superò 1024 byte è necessario spezzettarlo
        String header="NEW!"+ " SENDER:"+usrname +"\n NUM:"+mexNum+" END\n";
        int headerdimension=header.length();
        int messageDim=message.length();
        if(messageDim+headerdimension>1024){
            //divido in due il messaggio
            ChatSendMessage(message.substring(0,messageDim/2),usrname);
            ChatSendMessage(message.substring((messageDim/2)+1,messageDim),usrname);
            return;
        }
        mexNum++;
        byte[] toSend=(header.concat(message)).getBytes();//Inviamo header e corpo(almeno in parte) insieme

        DatagramPacket datagramPacket = new DatagramPacket(toSend,toSend.length, InetAddress.getByName(this.chatAddress),chatPort);

        synchronized (ChatThread.this) {// se non è stato chiuso il Socket per logout invia
            if(!this.ChatThreadStopped) {
                chatSocket.send(datagramPacket);
                System.out.println(message+"inviato");
            }
        }
    }

    public void closeSocket() {//subito dopo fine del Thread forzata da client
        //a seguito di inizio Logout, usato per chiudere la socket
        synchronized (ChatThread.this) {//necessario sincronizzare nel caso in cui la socket venga usata per le SCRITTURE
            this.chatFrame.dispose();//eliminazione del chatFrame in caso di LogOut
            this.ChatThreadStopped=true;//Thread controllerà che la IOException sia arrivata proprio per chiusura esplicità qui
            //quando si sbloccherà dalla receive
            this.chatSocket.close();
        }
    }

    public void ChatSendENDMessage() throws IOException {//Chiamata dall'utente che propaga il messaggio
        //invio O propagazione di messaggio PROJECT DELETED
        byte[] toSend=("\nPROJECT DELETED\n").getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getByName(this.chatAddress), chatPort);
        synchronized (ChatThread.this) {// se non è stato chiuso il Socket per logout invia
            if(!this.ChatThreadStopped) {
                chatSocket.send(datagramPacket);
            }
        }
   }
}
