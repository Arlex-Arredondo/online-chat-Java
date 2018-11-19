import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class PrivateChatGUI extends JFrame 
{
	private JPanel contentPane;
	private String nameOfThisPrivateChat;
	protected JTextArea txtarea_messages;

	
	public  PrivateChatGUI(String usernameIn) 
	{
		nameOfThisPrivateChat=usernameIn;			
		createWindow2();
	}
	
	public String getThisPrivateChatName()
	{
		return nameOfThisPrivateChat;
	}
	
	public void createWindow2() 
	{
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //instead of closing the window directly, we use window listener.
		setSize(720,523);
		setResizable(false);
		//setLocationRelativeTo(null);
		setLocation(8, 50);
		setTitle("Private chat with "+nameOfThisPrivateChat );	
		addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            ClientMain.deleteChatFromList(nameOfThisPrivateChat);
	            dispose();
	        }
	    });
		//distancia desde izq, distancia desde arriba, ancho, largo
		contentPane = new JPanel();
		contentPane.setBackground(new Color(51, 153, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtarea_messages = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(txtarea_messages);//this pane contains the txtarea_messages text area
		contentPane.add(scrollPane);
		scrollPane.setVisible(true);
		scrollPane.setBounds(10, 11, 680, 362);
		DefaultCaret caret = (DefaultCaret) txtarea_messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);//this makes the text area to display always the bottom part, so everytime someone sends a mesage, is visible
		
		txtarea_messages.setLineWrap(true);
		txtarea_messages.setEditable(false);
		txtarea_messages.setBounds(10, 11, 913, 517);
		txtarea_messages.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,17));
		//contentPane.add(txtarea_messages);	
		txtarea_messages.setText(" You are now chatting with "+nameOfThisPrivateChat+"...");
		
		JTextArea txtarea_userMessage = new JTextArea();
		txtarea_userMessage.setFont(new Font("Arial",Font.LAYOUT_LEFT_TO_RIGHT,16));
		JScrollPane scrollPaneUser = new JScrollPane(txtarea_userMessage);
		txtarea_userMessage.setLineWrap(true);
		scrollPaneUser.setBounds(10, 381, 550, 86);
		
		contentPane.add(scrollPaneUser);
		setVisible(true);
		txtarea_userMessage.requestFocusInWindow(); 
	
		JButton btnSend = new JButton("Send");		
		btnSend.setBounds(570, 401, 115, 44);
		contentPane.add(btnSend);
		btnSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if(ClientMain.checkIfChatIsActive(nameOfThisPrivateChat))
				{
					if(!txtarea_userMessage.getText().equals(""))  //if is not empty the txtarea, we prepare and send the message
	                {
						Message messageToSend = new Message();
	                	messageToSend.setSender(ClientMain.nameOfThisClient);
	                	messageToSend.setReceiver(nameOfThisPrivateChat);
	                	messageToSend.setType("PrivateChat");
	                	messageToSend.setContent(txtarea_userMessage.getText().trim());
	                	
	                	boolean confirmationDeliveryMessage = ClientMain.sendMessageToServer(messageToSend);//call server sending message   
	                    
	                	if(confirmationDeliveryMessage == false)
	                	{
	                		txtarea_messages.append("\n\r                         MESSAGE COULD NOT BE SENT"); 
	                		txtarea_messages.setCaretPosition(txtarea_messages.getDocument().getLength());//this will set the focus of the window at the end
	                	}
	                	else
	                	{
	                		txtarea_messages.append("\n\rYou: "+txtarea_userMessage.getText());
	                	}
	                }
					txtarea_userMessage.setText("");
					txtarea_userMessage.requestFocusInWindow(); 
				}
				else
				{
					txtarea_messages.append("\n\r          USER NOT CONNECTED");
				}
			}
		});
	}
}
