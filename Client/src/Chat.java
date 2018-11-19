import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;
 

public class Chat extends JFrame 
{ 
    private JPanel contentPane; 
    private  static JTextArea txtarea_messages;  
    private String[] listOfUsers;
    private DefaultListModel<String> model =null; //this line is used in the JList
    private String nameOfChat="";
    
    //CONSTRUCTOR
    public Chat(String nameIn)
    { 
        setTitle(nameIn+" Chat");
        createWindow();
        nameOfChat=nameIn;
    } 

    public void createWindow() 
    { 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(1192,680); 
        setLocationRelativeTo(null); 
        setResizable(false);
        contentPane = new JPanel(); 
        contentPane.setBackground(new Color(51, 153, 204)); 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); 
        setContentPane(contentPane); 
        contentPane.setLayout(null); 
         
        txtarea_messages = new JTextArea(); 
        txtarea_messages.setBackground(Color.WHITE); 
        txtarea_messages.setLineWrap(true); 
        txtarea_messages.setEditable(false);                //<<<<this txtarea is where all the users messages appear >>>>>>>>>>>>
        txtarea_messages.setBounds(10, 11, 913, 517); 
        txtarea_messages.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,17));
        //contentPane.add(txtarea_messages); 
        txtarea_messages.setText("successfuly connected..."); 
        
        JScrollPane scrollPane = new JScrollPane(txtarea_messages);
        contentPane.add(scrollPane); 						
        scrollPane.setVisible(true); 							//<<<< scrollbar for txtarea_messages >>>>>>>>>>>>
        scrollPane.setBounds(10, 11, 913, 517); 
             
        JTextArea txtarea_userMessage = new JTextArea();
        txtarea_userMessage.setBackground(Color.WHITE);   //<<<< this txtarea is where the user writes messages in the global chat>>>>>>
        txtarea_userMessage.setLineWrap(true); 
        txtarea_userMessage.requestFocusInWindow(); 
        txtarea_userMessage.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,16));
        
        JScrollPane scrollPaneUser = new JScrollPane(txtarea_userMessage);      
        scrollPaneUser.setBounds(10, 544, 913, 86); 	//<<<< scrollbar for txtarea_userMessage >>>>>>>>>>>>
        contentPane.add(scrollPaneUser); 
        
        //------------------------------------------------------------------- <<<< This bit is for the JList that hodls the users connected
        model = new DefaultListModel<>();
        JList<String> userList = new JList<>( model );
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        userList.setLayoutOrientation(JList.VERTICAL); 
        userList.setVisibleRowCount(-1); // invoking setVisibleRowCount(-1) makes the list display the maximum number of items possible in the available space onscreen 
        userList.setFont(new Font("Arial",Font.BOLD,20));
        
        JScrollPane scrollPaneUsers = new JScrollPane(userList); 
        contentPane.add(scrollPaneUsers); 
        scrollPane.setVisible(true); 
        scrollPaneUsers.setBounds(933, 11, 241, 517); 

        userList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e) 
            {
            	if (e.getValueIsAdjusting()==false) //until de user has released the click
            	{
            		 if (userList.getSelectedIndex() != -1)// -1 is where is not selection
            		 {
            			 String userSelected = userList.getSelectedValue().toString();  //we get the name of selected item and compare it with our name
            			 boolean checkIfChatIsActive; //to check if a window for that person is open already
            			 
            			 if(!userSelected.equals(ClientMain.nameOfThisClient)) //if we did not click in our own name
	                     {
            				 checkIfChatIsActive = ClientMain.checkIfChatIsActive(userSelected);//we check if the conversation is already active
            				 if(checkIfChatIsActive == false) //if not, we open a new window
            				 {
            					 userSelected = userList.getSelectedValue().toString(); //we get the name clicked in the List of users
            					 PrivateChatGUI newPrivateChat = new PrivateChatGUI(userSelected);
            					 ClientMain.addNewChatToList(newPrivateChat);
            				 }
	                     }	                     	
            		 }           		
                }
            }
        });
        //-----------------------------------------------------------------
        
        JButton btnSend = new JButton("Send"); 
        btnSend.setForeground(new Color(0, 0, 0)); 
        btnSend.setBackground(UIManager.getColor("Button.light"));        //<<<<< THIS BUTTON IS TO SEND THE ACTUAL MESSAGE  >>>>>>
        btnSend.setBounds(1000, 564, 122, 44); 
        contentPane.add(btnSend);        
        btnSend.addActionListener(new ActionListener()              
        { 
            public void actionPerformed(ActionEvent arg0)
            { 
                if(!txtarea_userMessage.getText().equals("") )  //if is not empty the txtarea, we prepare and send the message
                {
                	Message messageToSend = new Message();
                	messageToSend.setSender(ClientMain.nameOfThisClient);
                	messageToSend.setType("Broadcast");
                	messageToSend.setContent(txtarea_userMessage.getText().trim());
                	
                	boolean confirmationDeliveryMessage = ClientMain.sendMessageToServer(messageToSend);//we send the message to the server 
                    
                	if(confirmationDeliveryMessage == false)
                	{
                		txtarea_messages.append("\n\r                         MESSAGE COULD NOT BE SENT"); 
                		txtarea_messages.setCaretPosition(txtarea_messages.getDocument().getLength()); //this will set the focus of the window at the end
                	}
                }
                
                txtarea_userMessage.setText(""); 	//we set the txtarea empty again
                txtarea_userMessage.requestFocusInWindow();  
            } 
        }); 

        setVisible(true); 
    } 


    public void updateFromServer(Message messageIn) //we receive messages from the server
    {
    	 if(messageIn.getType().equals("Broadcast")) 
         { 
             if(messageIn.getListUsers() == null) 
             { 
            	 if(messageIn.getSender().equals(nameOfChat)) //this display the word "YOU" in the text area if the broadcast message is sent by myself
            	 {
            		 txtarea_messages.append("\n\r"+"You"+": "+messageIn.getContent()); 
                     txtarea_messages.setCaretPosition(txtarea_messages.getDocument().getLength());
                     //after writing something in the txtarea of the chat, we make a focus in the last line written in the Chat 
            	 }else 
            	 {
            		 txtarea_messages.append("\n\r"+messageIn.getSender()+": "+messageIn.getContent()); 
	                 txtarea_messages.setCaretPosition(txtarea_messages.getDocument().getLength());
	                 //after writing something in the txtarea of the chat, we make a focus in the last line written in the Chat
            	 }
             } 
             else 
             { 
                 listOfUsers = messageIn.getListUsers();
                 model.clear(); //this empties the list of users display
                 for(int i=0; i < listOfUsers.length; i++)
                 {                	
                 	model.addElement(listOfUsers[i]); //this fill the list of users display
                 }
             } 
         }
    	 else if(messageIn.getType().equals("PrivateChat"))
    	 {
    		 ClientMain.sendMessageToPrivateChat(messageIn.getSender(),messageIn.getContent());
    	 }
    }
    
 
    

} 

