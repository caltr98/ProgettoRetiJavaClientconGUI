import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;

public class LoginRegistrationPage extends JFrame  implements ActionListener, MouseListener, EventListener,WindowListener {
    private Client client;
    private JButton regButton;
    private JButton logiButton;
    private JPanel centralPanel,northPanel,buttons;
    public static String regexNospace="([a-zA-Z0-9.~!$%^&*_=+_%\\[\\]()\"{}\\\\:;,^?-]+)"; //costante usata in tutto il programma
    //matching di stringa alfanumerica con simboli senza spazi
    public static String regexWIRTHspace="([a-zA-Z0-9.~!$%^&*_=+_%\\[\\]()\"{}\\\\:;,^?-]+)( [a-zA-Z0-9.~!$%^&*_=+_%\\[\\]()\"{}\\\\:;,^?-]+)*"; //costante usata quando voglio
    //match di una stringa e usa serie di stringhe alfanumeriche con simboli spaziate fra loro
    private JTextField username;
    private JTextField password;
    private GridBagConstraints gbc;
    int serverPort,registryPort;//numero di porta del Server
    public LoginRegistrationPage(int serverPort,int registryPort){
        this.serverPort=serverPort;
        this.registryPort=registryPort;
        this.setTitle("WORTHAccess");
        regButton=new JButton("Registrazione");
        logiButton=new JButton("Login");
        regButton.setBackground(Color.LIGHT_GRAY);
        logiButton.setBackground(Color.LIGHT_GRAY);

        regButton.addActionListener(this);
        logiButton.addActionListener(this);

        username=new JTextField();
        username.addMouseListener(this);
        password=new JTextField();
        password.addMouseListener(this);
        username.setText("username");
        password.setText("password");


        this.setLayout(new BorderLayout(0,0));

        gbc=new GridBagConstraints();
        gbc.gridwidth=GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets=new Insets(10,10,10,10);
        centralPanel=new JPanel();

        centralPanel.setLayout(new GridBagLayout());
        centralPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        northPanel=new JPanel(new GridLayout(2,2,10,10));
        northPanel.add(new JLabel("Nome Utente"));
        northPanel.add(username);
        northPanel.add(new JLabel("Password"));
        northPanel.add(password);

        northPanel.setPreferredSize(new Dimension(200,50));

        //inserimento dei pulsanti al centro del frame
        buttons = new JPanel(new GridBagLayout());
        buttons.add(logiButton,gbc);
        buttons.add(regButton,gbc);
        //fine inserimento dei pulsanti al centro del frame
        centralPanel.add(northPanel,gbc);
        centralPanel.add(buttons,gbc);

        this.add(centralPanel,BorderLayout.CENTER);
        this.pack();
        this.addWindowListener(this);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usrname;
        String password;
        JOptionPane jOptionPane;
        String result;
        if(e.getSource()==regButton){
            usrname=this.username.getText();
            password=this.password.getText();
            if(usrname.equals("")){
                JOptionPane.showMessageDialog(null,"Nessun nome utente Inserito","campo nome utente vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore
            }
            if(password.equals("")){
                JOptionPane.showMessageDialog(null,"Nessuna Password Inserita","campo password vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore

            }
            else if(usrname.matches("^"+regexNospace)&& password.matches("^"+regexNospace)){
                client=new Client(usrname,password,serverPort,registryPort);//creo oggetto
                result=client.Registration();
                if(result==null){
                    JOptionPane.showMessageDialog(null,"Errore nella comunicazione col server","ComunicationFailure",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                }
                else if(result.equals("Username già in utilizzo")){
                    JOptionPane.showMessageDialog(null,result,"Registrazione fallita",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                }
                else{
                    JOptionPane.showMessageDialog(null,"Successo","Registrazione effettuata",JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"Lettere,numeri,simboli e no spazi in username e password","Formati non rispettati",JOptionPane.ERROR_MESSAGE);//messaggio di errore
            }
        }
        if(e.getSource()==logiButton){
            usrname=this.username.getText();
            password=this.password.getText();
            if(usrname.equals("")){
                JOptionPane.showMessageDialog(null,"Nessun nome utente Inserito","campo nome utente vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore
            }
            if(password.equals("")){
                JOptionPane.showMessageDialog(null,"Nessun nome utente Inserito","campo nome utente vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore
            }
            else if(usrname.matches("^"+regexNospace)&& password.matches("^"+regexNospace)){
                //necessario che username e password venga rispettato, lettere,numeri,simboli,no spazi
                if(client==null){
                    client=new Client(usrname,password,serverPort,registryPort);
                }else{
                    client.setMyPassword(password);
                    client.setMyUsrName(usrname);
                }
                //avvio procedura di login
                result=client.Login();
                if(result==null){
                    JOptionPane.showMessageDialog(null,"Errore nella comunicazione col server",
                            "ComunicationFailure",JOptionPane.WARNING_MESSAGE);//messaggio di erroreù
                    client.CloseConnection();
                    client=null;
                }
                else  if(result.equals("LOGGED")){
                    JOptionPane.showMessageDialog(null,"Successo","Login effettuato"
                            ,JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
                    this.removeWindowListener(this);
                    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//aperto il nuovo frame alla chiusura di questo l'app resta attiva
                    new MainPage(client);
                    this.dispose();//chiusura di questa pagina
                }
                else {
                    JOptionPane.showMessageDialog(null,result,"Login fallito",JOptionPane.ERROR_MESSAGE);//messaggio di errore
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"Lettere,numeri,simboli e no spazi in username e password","Formati non rispettati",JOptionPane.ERROR_MESSAGE);//messaggio di errore
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        username.setText("");
        password.setText("");
        username.removeMouseListener(this);
        password.removeMouseListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(client!=null)
            client.CloseConnection();//chiudi la socket se stai uscendo dal programma
        System.exit(0);

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
