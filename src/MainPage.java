
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;

import static java.awt.Color.black;

public class MainPage extends JFrame implements ItemListener, ActionListener, WindowListener {
    //La finestra principale
    private Client client;
    JPanel cards, card1, card2;//contenitore e card del CardLayout
    GridBagConstraints gbc;
    JButton logoutButton, lisUsButton, lisOnUsButton;
    JButton listProjectsB,createProjectB,openProjectB
            ,addMemberB,cancelProjectB;
    JPanel comboBoxPane,currentPanel;
    JComboBox jComboBox;//Combo Box per scegliere fra operazioni generali e operazioni sul progetto
    JComboBox principalJcomboBox;
    JButton backButton;
    public MainPage(Client client) {
        this.client = client;
        card1 = new JPanel();
        card2 = new JPanel();

        jComboBox = new JComboBox(new String[]{"Generale", "Progetti"});

        principalJcomboBox= new JComboBox(new String[]{"firstpage","listallusers"});

        comboBoxPane=new JPanel();

        cards=new JPanel(); // pannello che cambierà fra pulsanti op generali e op sul progetto

        jComboBox.setEditable(false);

        jComboBox.addItemListener(this);

        comboBoxPane.add(jComboBox);

        backButton=new JButton("Back to Main");//Button per tornare alla pagina principale dopo un'operazione
        backButton.addActionListener(this);
        backButton.setIcon(new ImageIcon("./icons8-back-arrow-50.png"));
        backButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        backButton.setContentAreaFilled(false);

        this.setTitle("WORTH"+" "+client.getMyUsrName());
        this.setLayout(new FlowLayout());




        //setting dei gbc
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.ipadx=50;
        gbc.ipady=50;

        //impostazione della card1, operazioni Logout, Mostra lista utenti e lista utenti Online
        card1.setLayout(new GridBagLayout());
        card1.setSize(new Dimension(1000,1000));
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(218, 75, 75));
        lisUsButton = new JButton("Lista Utenti");
        lisUsButton.setBackground(new Color(35, 99, 104, 255));

        lisOnUsButton = new JButton("Lista Utenti Online");
        lisOnUsButton.setBackground(new Color(89, 148, 91));


        lisUsButton.addActionListener(this);
        logoutButton.addActionListener(this);
        lisOnUsButton.addActionListener(this);
        card1.add(logoutButton,gbc);
        card1.add(lisOnUsButton,gbc);
        card1.add(lisUsButton,gbc);


        //fine impostazioni card1
        cards.setLayout(new CardLayout(2,2));//aggiunta dei pannelli da selezionare
        cards.add(card1, "Generale");
        cards.add(card2, "Progetti");

        //impostazione card2 listaProgetti,creaprogetto,OperazioniSuCards
        card2.setLayout(new GridBagLayout());
        card2.setSize(new Dimension(1000,1000));
        createProjectB = new JButton("Crea Progetto");
        createProjectB.setBackground(new Color(136, 179, 104));
        listProjectsB = new JButton("Lista miei Progetti");
        listProjectsB.setBackground(new Color(191, 171, 110, 255));

        cancelProjectB = new JButton("Cancella Progetto");
        cancelProjectB.setBackground(new Color(148, 106, 89));


        addMemberB=new JButton("Aggiungi membro a Progetto");
        addMemberB.setBackground(new Color(170, 116, 194, 104));


        openProjectB=new JButton("Apri Progetto");
        openProjectB.setBackground(new Color(203, 194, 129, 161));

        createProjectB.addActionListener(this);
        listProjectsB.addActionListener(this);
        createProjectB.addActionListener(this);
        openProjectB.addActionListener(this);

        card2.add(openProjectB,gbc);
        card2.add(createProjectB,gbc);
        card2.add(listProjectsB,gbc);



        this.add(comboBoxPane, BorderLayout.PAGE_START);
        this.add(cards, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);//se viene chiusa la scheda si effettuano delle operazioni di pulizia prima
        this.setVisible(true);


    }
    private class ProjectPanel extends JPanel implements ListSelectionListener,ActionListener {
        //Panel che appare quando si APRE un Progetto specifico

        JList jList;//lista dei progetti apribili
        JButton backToProjects,backToProject,showMembersB,addMemberB,showCardsB,
        showCardB,addCardB,moveCardB,getCardHistoryB,chatButtonB,submitNewCard;
        JScrollPane jScrollPane;
        JPanel MainPanel;
        String projectName,cardTOmove;//ProjectName ottenuto all'apertura del Pannello, Card specificata da muovere
        JRadioButton TODOButton,INPROGRESSButton, TOBEREVISEDButton, DONEButton;

        JList jListCards,jListCardsH,jListCardsM,jListMembers;//1 cards in lista e mostra card, 2 Card Per history,3 Card per Move
        JPanel showCardsPanel,singleCardPanel,addCardPanel,moveCardPanel,showMembersPanel;
        JTextField titoloToInsert;
        JTextArea DescrizioneToInsert;

        int activepanel;//1 showCardsPanel,2 singleCardPanel,3 addCardPanel,4 moveCardPanel,5 showMembersPanel
        //per trovare il Panel da rimuovere in caso di backToProject Button premuto
        public ProjectPanel(){
            MainPanel=new JPanel();
            currentPanel=MainPanel;
            String resultino[]=client.listProjects().split("\u2407");//Il pannello viene aperto sulla scelta del
            //progetto da aprire

            if(resultino[0].equals("NO PROJECTS")){//Se non ci sono progetti si torna indietro
                JOptionPane.showMessageDialog(null,"NessunProgetto",resultino[0],JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
                backButton.doClick();

            }
            jList=new JList(resultino);//lista dei progetti da scegliere
            jList.setFont(jList.getFont().deriveFont(22.0f));
            jScrollPane= new JScrollPane(jList);
            MainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            jScrollPane.setPreferredSize(new Dimension(500,500));
            MainPanel.add(jScrollPane);
            jList.addListSelectionListener(this);//permettere scelta del progetto da aprire
            MainPanel.add(backButton);//permette di tornare indietro al CardLayout precedente prima della scelta del project
            this.add(MainPanel);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) { //Listener della selezione nella lista
            String result;
            if(!e.getValueIsAdjusting()) {//Solo se Scelta effettuata
                if (e.getSource() ==jList) {//Prima scelta:Scegliere il Progetto da aprire

                    result = jList.getSelectedValue().toString();
                    projectName = result;//risultato ottenuto

                    MainPanel.remove(jList);//Cambio di pannelli da mostrare sulla finestra
                    MainPanel.remove(jScrollPane);
                    MainPanel.remove(backButton);

                    //inizializzazione dei Button per tornare Indietro
                    backToProjects = new JButton("Back to Projects");//indietro alla scelta dei Progetti
                    backToProjects.setIcon(new ImageIcon("./icons8-back-arrow-50.png"));
                    backToProjects.addActionListener(this);
                    backToProjects.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                    backToProjects.setContentAreaFilled(false);

                    backToProject = new JButton("Back to "+projectName+" Project");//Indietro al Pannello del progetto
                    backToProject.setIcon(new ImageIcon("./icons8-back-arrow-50.png"));
                    backToProject.addActionListener(this);
                    backToProject.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                    backToProject.setContentAreaFilled(false);

                    MainPanel.setBorder(BorderFactory.createTitledBorder(result));
                    MainPanel.setLayout(new GridBagLayout());
                    projectOperations();//Inizializzazione degli altri Button per le operazioni sulle Card
                    //si trova dopo il metodo ValueChanged
                }
                if(e.getSource()==jListCards){//attendo selezione nella lista delle cards
                    //si arriva qui dopo la scelta di Mostra singola Card
                    this.remove(showCardsPanel);
                    String title,description,status;
                    String arrived[];
                    singleCardPanel=new JPanel();
                    singleCardPanel.setBorder(BorderFactory.createTitledBorder("NomeCard-- Descrizione --Lista Corrente"));
                    String CardName=jListCards.getSelectedValue().toString();
                    arrived=client.showCard(projectName,CardName).split("\u2407");
                    title=arrived[0];
                    description=arrived[1];
                    status=arrived[2];
                    JTextPane jTextPane=new JTextPane();
                    //scrittura delle informazioni della card su jTextPane
                    StyleContext sc = StyleContext.getDefaultStyleContext();
                    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.ORANGE);
                    aset = sc.addAttribute(aset, StyleConstants.FontFamily, "TimesRoman");
                    aset=sc.addAttribute(aset,StyleConstants.FontSize,25);
                    jTextPane.setCaretPosition(  jTextPane.getDocument().getLength());
                    jTextPane.setCharacterAttributes(aset, false);
                    jTextPane.replaceSelection("CardTitle: "+title);//scrittura del titolo

                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, black);//scrittura della descrizione
                    jTextPane.setCharacterAttributes(aset, false);
                    jTextPane.replaceSelection("\n\nDescription:\n"+description);

                    aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground,Color.BLUE);//scrittura della descrizione
                    jTextPane.setCharacterAttributes(aset, false);
                    jTextPane.replaceSelection("\n\nStatus:"+status);//scrittura dello status


                    JScrollPane jScrollPane= new JScrollPane(jTextPane,
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    singleCardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    jScrollPane.setPreferredSize(new Dimension(400,450));
                    //Titolo-Descrizione-Stato scritti in colori diversi per differenziarli
                    singleCardPanel.add(jScrollPane);
                    singleCardPanel.add(backToProject);
                    singleCardPanel.add(backToProjects);
                    singleCardPanel.add(backButton);
                    activepanel=2;
                    this.add(singleCardPanel);
                    MainPage.this.pack();

                }
                if(e.getSource()==jListCardsH){//Scelta della Card da cui ottenere la History
                    this.remove(showCardsPanel);
                    singleCardPanel=new JPanel();
                    String CardName=jListCardsH.getSelectedValue().toString();
                    JTextArea testo=new JTextArea(2,0);
                    testo.setText(CardName+": "+client.getCardHistory(projectName,CardName));
                    testo.setEditable(false);
                    testo.setFont(new Font("TimesRoman", Font.BOLD ,20));
                    JScrollPane jScrollPane= new JScrollPane(testo,
                            JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                    //si visualizza la History come restituita dal server con stati separati da |, il tutto
                    //scrollando orizzontalmente
                    singleCardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    jScrollPane.setPreferredSize(new Dimension(500,100));
                    singleCardPanel.add(jScrollPane);
                    singleCardPanel.add(backToProject);
                    singleCardPanel.add(backToProjects);
                    singleCardPanel.add(backButton);
                    activepanel=2;
                    this.add(singleCardPanel);
                    MainPage.this.pack();

                }
                if(e.getSource()==jListCardsM){//scelta nella Card da spostare di lista
                    this.remove(showCardsPanel);
                    cardTOmove=jListCardsM.getSelectedValue().toString();
                    new MoveCardPanel();//Nuovo Pannello, innerclass di questa innerclass
                    MainPage.this.pack();

                }



            }
        }

        public void projectOperations(){
            //inserimento dei button in un layout simil griglia/tabella con due colonne
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx=0;
            gbc.gridy=0;

            showMembersB=new JButton("Mostra membri Progetto");
            showMembersB.setBackground(new Color(123, 90, 132, 104));

            addMemberB=new JButton("Aggiungi membro a Progetto");
            addMemberB.setBackground(new Color(170, 116, 194, 173));

            showCardB=new JButton("Mostra singola Card");
            showCardsB=new JButton("Mostra le Cards");
            addCardB=new JButton("Aggiungi Card");
            moveCardB=new JButton("Muovi Card");
            getCardHistoryB=new JButton("Ottieni Card History");
            chatButtonB=new JButton("CHAT!");



            showCardB.setBackground(new Color(92, 180, 155, 180));
            showCardsB.setBackground(new Color(92, 180, 155, 180));
            addCardB.setBackground(new Color(92, 180, 155, 180));
            moveCardB.setBackground(new Color(92, 180, 155, 180));
            getCardHistoryB.setBackground(new Color(92, 180, 155, 180));
            chatButtonB.setBackground(new Color(63, 187, 29));


            showCardB.addActionListener(this);
            showCardsB.addActionListener(this);
            addCardB.addActionListener(this);
            moveCardB.addActionListener(this);
            getCardHistoryB.addActionListener(this);
            cancelProjectB.addActionListener(this);
            addMemberB.addActionListener(this);
            showMembersB.addActionListener(this);
            chatButtonB.addActionListener(this);

            MainPanel.add(backButton,gbc);
            gbc.gridx++;
            MainPanel.add(backToProjects,gbc);
            gbc.weightx = 1;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.ipadx=50;
            gbc.ipady=50;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx--;
            gbc.gridy++;
            MainPanel.add(showCardB,gbc);
            gbc.gridx++;
            MainPanel.add(showCardsB,gbc);
            gbc.gridy++;
            gbc.gridx--;
            MainPanel.add(addCardB,gbc);
            gbc.gridx++;
            MainPanel.add(moveCardB,gbc);
            gbc.gridy++;
            gbc.gridx--;
            MainPanel.add(getCardHistoryB,gbc);
            gbc.gridx++;
            MainPanel.add(cancelProjectB,gbc);
            gbc.gridy++;
            gbc.gridx--;
            MainPanel.add(showMembersB,gbc);
            gbc.gridx++;
            MainPanel.add(addMemberB,gbc);

            //gbc = new GridBagConstraints();
            gbc.gridx=0;
            gbc.gridy+=3;
            gbc.gridwidth = GridBagConstraints.LAST_LINE_END;
            gbc.anchor = GridBagConstraints.SOUTH;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(10, 0, 0, 0);
            MainPanel.add(chatButtonB,gbc);//chat button occupa un'intera riga

            MainPage.this.pack();

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==backToProjects){
                MainPage.this.remove(this);//necessario rimuovere questo pannello
                ProjectPanel newPane=new ProjectPanel();//creato un Nuovo ProjectPanel che porterà alla scelta del project
                MainPage.this.add(newPane);
                MainPage.this.currentPanel=newPane;
                MainPage.this.pack();
            }
            if(e.getSource()==backToProject){//ritorno al menu delle operazioni su un Progetto
                GridBagConstraints gbc1=new GridBagConstraints();//Necessario reinserire i bottono backButton e
                gbc1.gridx=0;                                    //backToProjects, scompaiono al ritorno nella MainPage
                gbc1.gridy=0;
                if(activepanel==1){//necessario trovare quale pannello è attivo al momento per rimuoverlo
                    this.remove(showCardsPanel);
                    MainPanel.add(backButton,gbc1);
                    gbc1.gridx++;
                    MainPanel.add(backToProjects,gbc1);
                    MainPanel.setVisible(true);
                    MainPage.this.pack();
                }
                if(activepanel==2){
                    this.remove(singleCardPanel);
                    MainPanel.add(backButton,gbc1);
                    gbc1.gridx++;
                    MainPanel.add(backToProjects,gbc1);

                    MainPanel.setVisible(true);
                    MainPage.this.pack();
                }
                if(activepanel==3){
                    this.remove(addCardPanel);
                    MainPanel.add(backButton,gbc1);
                    gbc1.gridx++;
                    MainPanel.add(backToProjects,gbc1);

                    MainPanel.setVisible(true);
                    //projectOperations();
                    MainPage.this.pack();
                }
                if(activepanel==4){
                    this.remove(moveCardPanel);
                    MainPanel.add(backButton,gbc1);
                    gbc1.gridx++;
                    MainPanel.add(backToProjects,gbc1);

                    MainPanel.setVisible(true);
                    MainPage.this.pack();
                }
                if(activepanel==5){
                    this.remove(showMembersPanel);
                    MainPanel.add(backButton,gbc1);
                    gbc1.gridx++;
                    MainPanel.add(backToProjects,gbc1);

                    MainPanel.setVisible(true);
                    MainPage.this.pack();
                }



            }
            if(e.getSource()==addCardB){//dal menu delle operazioni sulle card, permette di inserire titolo e descrizione
                addCardPanel=new JPanel();
                submitNewCard=new JButton("Submit Card");
                MainPanel.setVisible(false);
                this.add(addCardPanel);

                GridBagConstraints gbc=new GridBagConstraints();
                gbc.gridwidth=GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets=new Insets(5,5,5,5);
                addCardPanel.setLayout(new GridBagLayout());
                addCardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                 titoloToInsert=new JTextField();
                 DescrizioneToInsert=new JTextArea();

                 DescrizioneToInsert.setWrapStyleWord(true);  //evitare SCROLLBAR ORIZZIONALE
                 DescrizioneToInsert.setLineWrap(true);

                 jScrollPane=new JScrollPane(DescrizioneToInsert,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                 DescrizioneToInsert.setSize(new Dimension(250,250));
                 titoloToInsert.setSize(new Dimension(100,100));

                addCardPanel.add(new JLabel("Titolo Card"),gbc);
                addCardPanel.add(titoloToInsert,gbc);
                gbc.ipadx=50;
                gbc.ipady=50;
                addCardPanel.add(new JLabel("Descrizione Card"),gbc);
                addCardPanel.add(jScrollPane,gbc);

                gbc.ipadx=0;
                gbc.ipady=0;
                addCardPanel.add(submitNewCard,gbc);
                addCardPanel.add(backToProject,gbc);
                addCardPanel.add(backToProjects,gbc);
                addCardPanel.add(backButton,gbc);

                submitNewCard.addActionListener(this);//Necessario premere Button Submit per aggiungere card
                activepanel=3;
                MainPage.this.pack();
            }
            if(e.getSource()==chatButtonB){//apertura del nuovo frame/vecchio frame della chat
                client.readChat(projectName);
            }
            if(e.getSource()==showCardB){//mostrare una singola card, apre una lista da cui scegliere quale
                showCardsPanel=new JPanel();
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                this.add(showCardsPanel);
                String response[]=client.showCards(projectName).split("\u2407");
                if (response[0].equals("Project non esiste")){
                    JOptionPane.showMessageDialog(null,response[0],"No Project",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                    backButton.doClick();
                }
                jListCards=new JList(response);
                jListCards.setFont(jListCards.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane= new JScrollPane(jListCards);
                showCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500,500));
                showCardsPanel.add(jScrollPane);
                showCardsPanel.add(backToProject);
                showCardsPanel.add(backToProjects);
                showCardsPanel.add(backButton);
                jListCards.addListSelectionListener(this);
                activepanel=1;
                MainPage.this.pack();
            }
            if(e.getSource()==showCardsB){//apre la lista delle cards
                //in questo caso la lista non ammette selezioni
                showCardsPanel=new JPanel();
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                this.add(showCardsPanel);
                String response[]=client.showCards(projectName).split("\u2407");
                if (response[0].equals("Project non esiste")){
                    JOptionPane.showMessageDialog(null,response[0],"No Project",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                    backButton.doClick();
                }
                JList jListCardsThrowAway=new JList(response);
                jListCardsThrowAway.setFont(jListCardsThrowAway.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane= new JScrollPane(jListCardsThrowAway);
                showCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500,500));
                showCardsPanel.add(jScrollPane);
                showCardsPanel.add(backToProject);

                showCardsPanel.add(backToProjects);
                showCardsPanel.add(backButton);
                activepanel=1;
                MainPage.this.pack();
            }
            if(e.getSource()==getCardHistoryB){//apre la lista delle cards da cui ottenere history
                showCardsPanel=new JPanel();
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                this.add(showCardsPanel);
                String response[]=client.showCards(projectName).split("\u2407");
                if (response[0].equals("Project non esiste")){
                    JOptionPane.showMessageDialog(null,response[0],"No Project",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                    backButton.doClick();
                }

                jListCardsH=new JList(response);
                jListCardsH.setFont(jListCardsH.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane= new JScrollPane(jListCardsH);
                showCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500,500));
                showCardsPanel.add(jScrollPane);
                showCardsPanel.add(backToProject);
                showCardsPanel.add(backToProjects);
                showCardsPanel.add(backButton);
                jListCardsH.addListSelectionListener(this);
                activepanel=1;
                MainPage.this.pack();
            }
            if(e.getSource()==cancelProjectB){//permette di cancellare il project
                String response;
                JOptionPane jOptionPane=new JOptionPane();//apertura di JOptionPane per decidere eliminazioen
                int risp=jOptionPane.showConfirmDialog(null,"Sei sicuro/a?",
                        "Eliminazione Progetto "+projectName, JOptionPane.YES_NO_OPTION);
                if (risp == JOptionPane.YES_OPTION) {
                    jOptionPane.getRootFrame().dispose();
                    MainPage.this.remove(jOptionPane);//il confirmDialog riappariva anche dopo aver fatto la scelta senza
                                                        //questo comando
                    jOptionPane.setVisible(false);    //comandi extra che potrebbero risolvere il bug di sopra
                    jOptionPane.setFocusable(false);
                    jOptionPane.setEnabled(false);
                    response=client.cancelProject(projectName);
                    if(response==null){
                        JOptionPane.showMessageDialog(null,"Errore nella comunicazione col server",
                                "ComunicationFailure",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                        System.exit(0);
                    }
                    else if(response.equals("OK")) {
                        jOptionPane.getRootFrame().dispose();

                        JOptionPane.showMessageDialog(null, "Cancellato");

                        backButton.doClick();//triggero azioni di backbutton
                    }else{
                        jOptionPane.getRootFrame().dispose();

                        JOptionPane.showMessageDialog(null,"Impossibile cancellare Project",
                                response,JOptionPane.ERROR_MESSAGE);//messaggio di errore
                    }
                }
                if(risp==JOptionPane.NO_OPTION){
                    jOptionPane.getRootFrame().dispose();

                    MainPage.this.remove(jOptionPane);
                    jOptionPane.setVisible(false);
                    jOptionPane.setFocusable(false);
                    jOptionPane.setEnabled(false);
                    JOptionPane.showMessageDialog(null, "Non Cancellato");
                }
                cancelProjectB=new JButton("Cancella Progetto");//ricrearlo elimina bug che fa rispuntare
                //l'optionPane più vol
                cancelProjectB.addActionListener(this);
                cancelProjectB.setBackground(new Color(148, 106, 89));



            }
            if(e.getSource()==submitNewCard){//inserimento nuova Card dopo aver scritto Titolo e Descrizione
                String title;
                String description;
                title=this.titoloToInsert.getText();
                description=this.DescrizioneToInsert.getText();
                String result;
                if(title.equals("")){
                    JOptionPane.showMessageDialog(null,"Nessun titolo Inserito","campo nome utente vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                }
                else if(description.equals("")){
                    JOptionPane.showMessageDialog(null,"Nessuna descrizione Inserita","campo descrizione  vuoto",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                }
                else{
                    if( !( title.matches("^"+(LoginRegistrationPage.regexWIRTHspace+"$") ) ) ){//testo (spazi) testo possibile
                        JOptionPane.showMessageDialog(null,"Testo spazio altro testo è ammesso","Formato Titolo errato",JOptionPane.WARNING_MESSAGE);//messaggio di errore

                    }
                    else {
                        result = client.addCard(projectName, title, description);
                        if (result == null) {
                            JOptionPane.showMessageDialog(null, "Errore nella comunicazione col server", "ComunicationFailure", JOptionPane.WARNING_MESSAGE);//messaggio di errore
                            System.exit(0);
                        } else if (result.equals("OK")) {
                            JOptionPane.showMessageDialog(null, "Successo", "Inserimento effettuato", JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
                            backToProject.doClick();

                        } else if (!result.equals("Project non esiste")) {
                            JOptionPane.showMessageDialog(null, result, "Inserimento fallito", JOptionPane.WARNING_MESSAGE);//messaggio di errore
                            backButton.doClick();
                        }
                         else if (!result.equals("Project contiene cardName")) {
                        JOptionPane.showMessageDialog(null, "Card già esistente", "Inserimento fallito", JOptionPane.WARNING_MESSAGE);//messaggio di errore
                        backButton.doClick();
                        }
                        else {
                            JOptionPane.showMessageDialog(null, result, "Inserimento fallito", JOptionPane.WARNING_MESSAGE);//messaggio di errore
                            backToProject.doClick();

                        }
                    }
                }


            }
            if(e.getSource()==moveCardB) {//Apre Lista per decidere che Card Spostare
                showCardsPanel=new JPanel();
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                this.add(showCardsPanel);
                //ottengo lista card per selezionare quale muovere
                String response[]=client.showCards(projectName).split("\u2407");
                if (response[0].equals("Project non esiste")){
                    JOptionPane.showMessageDialog(null,response[0],"No Project",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                    backButton.doClick();
                }
                jListCardsM=new JList(response);
                jListCardsM.setFont(jListCardsM.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane= new JScrollPane(jListCardsM);
                showCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500,500));
                showCardsPanel.add(jScrollPane);
                showCardsPanel.add(backToProject);
                showCardsPanel.add(backToProjects);
                showCardsPanel.add(backButton);
                jListCardsM.addListSelectionListener(this);
                activepanel=1;
                MainPage.this.pack();

            }
            if(e.getSource()==addMemberB){//aggiunta membro al progetto

                String response;
                JOptionPane jOptionPane=new JOptionPane();//jOPtionPane per inserire nome utente
                String toAdd=jOptionPane.showInputDialog(null,"Inserire Username",
                        "Aggiunta Utente "+projectName, JOptionPane.PLAIN_MESSAGE);

                if (toAdd!=null && toAdd.matches("^"+(LoginRegistrationPage.regexNospace))) {
                    MainPage.this.remove(jOptionPane);//il confirmDialog a volte riappare anche dopo la scelta/chiusura
                    jOptionPane.setVisible(false);    //comandi extra che potrebbero risolvere il bug di sopra
                    jOptionPane.setFocusable(false);
                    jOptionPane.setEnabled(false);

                    response=client.addMember(projectName,toAdd);
                    if(response==null){
                        JOptionPane.showMessageDialog(null,"Errore nella comunicazione col server",
                                "ComunicationFailure",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                        System.exit(0);
                    }
                    else if(response.equals("OK")) {
                        JOptionPane.showMessageDialog(null, "Membro aggiunto");

                    }else{
                        JOptionPane.showMessageDialog(null,"Impossibile aggiungere membro a Project",
                                response,JOptionPane.ERROR_MESSAGE);//messaggio di errore

                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Inserire username nel formato lettere,simboli,no spazi",
                            "Invalid Username",JOptionPane.ERROR_MESSAGE);//messaggio di errore
                }


            }
            if(e.getSource()==showMembersB){//mostra i membri del progetto in una lista
                showMembersPanel=new JPanel();
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                this.add(showMembersPanel);
                String response[]=client.showMembers(projectName).split("\u2407");
                if (response[0].equals("Project non esiste")){
                    JOptionPane.showMessageDialog(null,response[0],"No Project",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                    backButton.doClick();
                }

                jListMembers=new JList(response);
                jListMembers.setFont(jListMembers.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane= new JScrollPane(jListMembers);
                showMembersPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500,500));
                showMembersPanel.add(jScrollPane);
                showMembersPanel.add(backToProject);
                showMembersPanel.add(backToProjects);
                showMembersPanel.add(backButton);
                activepanel=5;
                MainPage.this.pack();



            }


            //metodi "USATI" DA innerclass MoveCardPanel, come action listener quindi è usata questa classe ProjectPanel
            if(e.getSource()==TODOButton){//radio Buttons fra cui scegliere
                moveCard("TODO");
            }
            if(e.getSource()==INPROGRESSButton){
                moveCard("INPROGRESS");
            }
            if(e.getSource()==TOBEREVISEDButton){
                moveCard("TOBEREVISED");
            }
            if(e.getSource()==DONEButton){
                moveCard("DONE");
            }
        }
        public void moveCard(String dst){//DOPO la scelta della Destinazione l'utente DEVE identificare la lista
            //di partenza
            String res;
            String options[]=new String[]{"TODO","INPROGRESS","TOBEREVISED","DONE"};
            int i;
            JOptionPane jOptionPane=new JOptionPane();//pannello in cui identificare la lista di origine della card
            i=jOptionPane.showOptionDialog(
                    null,"Seleziona lista Partenza","Lista Partenza",
                    JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null, options
                    , options[0]);
            if(i==-1){
                i=2;
            }
            if(i==3){
                i=3;
            }
            res=client.moveCard(projectName,cardTOmove,options[i],dst);
            if(res==null){
                MainPage.this.remove(jOptionPane);//il confirmDialog riappare a volte anche dopo scelta/chiusura
                jOptionPane.setVisible(false);    //comandi extra che potrebbero risolvere il bug di sopra
                jOptionPane.setFocusable(false);
                jOptionPane.setEnabled(false);

                JOptionPane.showMessageDialog(null,"Errore nella comunicazione col server",
                        "ComunicationFailure",JOptionPane.WARNING_MESSAGE);//messaggio di errore
                System.exit(0);
            }

            else if(!res.equals("Operazione Successo: Card spostata in posizione desiderata")){
                JOptionPane.showMessageDialog(null,res,"Spostamento Fallito",JOptionPane.WARNING_MESSAGE);//messaggio di errore

                //backToProject.doClick();
            }
            else{
                JOptionPane.showMessageDialog(null,res,"Spostamento effettuato",JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
            }

        }
        private class  MoveCardPanel extends JPanel{
            public MoveCardPanel(){
                moveCardPanel=this;
                this.setBorder(BorderFactory.createTitledBorder("Inserire Destinazione Card:"+ cardTOmove));
                MainPanel.setVisible(false);
                //MainPanel.removeAll();
                ProjectPanel.this.add(moveCardPanel);
                TODOButton=new JRadioButton("TODO");
                INPROGRESSButton=new JRadioButton("INPROGRESS");
                TOBEREVISEDButton=new JRadioButton("TOBEREVISED");
                DONEButton=new JRadioButton("DONE");
                TODOButton.addActionListener(ProjectPanel.this);
                INPROGRESSButton.addActionListener(ProjectPanel.this);
                TOBEREVISEDButton.addActionListener(ProjectPanel.this);
                DONEButton.addActionListener(ProjectPanel.this);


                ButtonGroup positions=new ButtonGroup();//Una sola scelta fra tutti i Button
                positions.add(TODOButton);
                positions.add(INPROGRESSButton);
                positions.add(TOBEREVISEDButton);
                positions.add(DONEButton);

                moveCardPanel.setLayout(new GridBagLayout());
                JLabel test=new JLabel("test");
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor=GridBagConstraints.CENTER;
                gbc.fill=GridBagConstraints.BOTH;
                gbc.weightx=1;
                gbc.gridx=50;
                gbc.gridy=50;
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.fill = GridBagConstraints.BOTH;
                moveCardPanel.add(new JLabel(new ImageIcon(
                        (new ImageIcon("./to-do-icon-18.jpg").getImage().
                                getScaledInstance(100, 100, Image.SCALE_DEFAULT)
                        ))),gbc);//un'icona per ogni lista
                gbc.gridy++;
                moveCardPanel.add(TODOButton,gbc);
                gbc.gridy--;
                gbc.gridx++;
                moveCardPanel.add(new JLabel( new ImageIcon
                        (new ImageIcon("clipart2655834.png").getImage().
                                getScaledInstance(200, 200, Image.SCALE_DEFAULT)
                        )),gbc);
                gbc.gridy++;
                moveCardPanel.add(INPROGRESSButton,gbc);
                gbc.gridy--;
                gbc.gridx++;
                moveCardPanel.add(new JLabel( new ImageIcon
                        (new ImageIcon("revise-icon-9.jpg").getImage().
                                getScaledInstance(200, 200, Image.SCALE_DEFAULT)
                        )),gbc);
                gbc.gridy++;
                moveCardPanel.add(TOBEREVISEDButton,gbc);
                gbc.gridy--;
                gbc.gridx++;
                moveCardPanel.add(new JLabel(new ImageIcon(
                        (new ImageIcon("clipart3011314.png").getImage().
                                getScaledInstance(200, 200, Image.SCALE_DEFAULT)
                        ))),gbc);
                gbc.gridy++;
                moveCardPanel.add(DONEButton,gbc);

                gbc.gridy++;
                gbc.gridy++;
                moveCardPanel.add(backToProject,gbc);
                gbc.gridy++;

                moveCardPanel.add(backToProjects,gbc);
                gbc.gridy++;

                moveCardPanel.add(backButton,gbc);

                activepanel=4;

                MainPage.this.pack();

            }
        }
    }

    public void itemStateChanged(ItemEvent evt) {//scelta del Pannello nel Menù di MainPage
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, (String) evt.getItem());//scelta fra Generale o Progetti
    }

    @Override
    public void actionPerformed(ActionEvent e) {//Bottoni premibili nel Menù principale della classe MainPage
        if(e.getSource()==lisOnUsButton){//lista utenti Online
            cards.setVisible(false);
            comboBoxPane.setVisible(false);
            JPanel list=new JPanel();
            JList jList=new JList(client.listOnlineusers());
            jList.setFont(jList.getFont().deriveFont(22.0f));
            JScrollPane jScrollPane= new JScrollPane(jList);
            list.setLayout(new FlowLayout(FlowLayout.CENTER));
            jScrollPane.setPreferredSize(new Dimension(500,500));
            list.add(jScrollPane);
            currentPanel=list;
           //this.remove(currentPanel); eliminazione
            list.add(backButton);
            this.add(list);
            this.pack();

        }
        if(e.getSource()==lisUsButton){//lista utenti
            cards.setVisible(false);
            comboBoxPane.setVisible(false);
            JPanel list=new JPanel();
            JList jList=new JList(client.listUsers());
            jList.setFont(jList.getFont().deriveFont(22.0f));
            JScrollPane jScrollPane= new JScrollPane(jList);
            list.setLayout(new FlowLayout(FlowLayout.CENTER));
            jScrollPane.setPreferredSize(new Dimension(500,500));
            list.add(jScrollPane);
            currentPanel=list;
            //this.remove(currentPanel); eliminazione
            list.add(backButton);
            this.add(list);
            this.pack();

        }
        if(e.getSource()==logoutButton){//logout
            client.Logout();//LOGOUT E CHIUSURA DELLE CHAT(+terminazione dei Thread della chat)
            client.CloseConnection();//chiusura della socket con il server in caso di logout
            new LoginRegistrationPage(client.getServerPort(),client.getRegistryPort());//ritorniamo a schermata di registrazione
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.removeWindowListener(this);
            this.dispose();
        }
        if(e.getSource()==backButton){//ritorno al menù principale
            currentPanel.setVisible(false);
            this.remove(currentPanel);
            cards.setVisible(true);
            comboBoxPane.setVisible(true);
            this.pack();
        }
        if(e.getSource()==openProjectB){//apertura progetto
            cards.setVisible(false);
            comboBoxPane.setVisible(false);
            ProjectPanel projectPanel=new ProjectPanel();//creazione ProjectPanel, si apre una lista di progetti
            currentPanel=projectPanel;
            projectPanel.setVisible(true);
            this.add(currentPanel);
            this.pack();
        }
        if(e.getSource()==listProjectsB){//lista dei progetti di cui l'utente è membro
            String result[]=client.listProjects().split("\u2407");
            if(result[0].equals("NO PROJECTS")){
                JOptionPane.showMessageDialog(null,"Nessun Progetto",result[0],JOptionPane.INFORMATION_MESSAGE);//messaggio di errore
            }
            else {
                cards.setVisible(false);
                comboBoxPane.setVisible(false);
                JPanel list = new JPanel();

                JList jList = new JList(result);
                jList.setFont(jList.getFont().deriveFont(22.0f));
                JScrollPane jScrollPane = new JScrollPane(jList);
                list.setLayout(new FlowLayout(FlowLayout.CENTER));
                jScrollPane.setPreferredSize(new Dimension(500, 500));
                list.add(jScrollPane);
                currentPanel = list;
                //this.remove(currentPanel); eliminazione
                list.add(backButton);
                this.add(list);
                this.pack();
            }
        }
        if(e.getSource()==createProjectB){//creazione Progetto
            String response;
            JOptionPane jOptionPane=new JOptionPane();//pannello per inserire ProjectName
            String toAdd=jOptionPane.showInputDialog(null,"Inserire Nome nuovo Progetto",
                    "Aggiunta Progetto", JOptionPane.PLAIN_MESSAGE);

            if (toAdd!=null && toAdd!="") {
                MainPage.this.remove(jOptionPane);//il confirmDialog riappariva anche dopo aver fatto la scelta senza
                //questo comando
                jOptionPane.setVisible(false);    //comandi extra che potrebbero risolvere il bug di sopra
                jOptionPane.setFocusable(false);
                jOptionPane.setEnabled(false);
                if (!(toAdd.matches("^"+(LoginRegistrationPage.regexWIRTHspace)))) {//testo (spazi) testo possibile
                    JOptionPane.showMessageDialog(null, "Testo spazio altro testo è ammesso", "Formato Project Name errato",
                            JOptionPane.WARNING_MESSAGE);//messaggio di errore
                } else {
                    response = client.createProject(toAdd);

                    if (response == null) {
                        JOptionPane.showMessageDialog(null, "Errore nella comunicazione col server",
                                "ComunicationFailure", JOptionPane.WARNING_MESSAGE);//messaggio di errore
                        System.exit(0);
                    } else if (response.equals("OK")) {
                        JOptionPane.showMessageDialog(null, "Progetto Creato", null, JOptionPane.PLAIN_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Impossibile creare Progetto",
                                response, JOptionPane.ERROR_MESSAGE);//messaggio di errore

                    }
                }
            }

            MainPage.this.remove(jOptionPane);//il confirmDialog riappariva anche dopo aver fatto la scelta
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {//chiusura del programma dalla X, chiusura SAFE con Logout
        client.Logout();//LOGOUT E CHIUSURA DELLE CHAT(+terminazione dei Thread della chat)
        client.CloseConnection();//chiusura della socket con il server in caso di logout

        //Nel vero caso in cui il Client termina in modo unsafe
        //il server può notare che il client si è disconnesso e "sloggarlo"
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
