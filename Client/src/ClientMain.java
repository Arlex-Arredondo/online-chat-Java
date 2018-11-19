import java.net.*;
import java.util.ArrayList;
import java.util.Properties;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane; 

public class ClientMain implements Runnable
{  
    private static Socket socketForServer = null;   
    private static ObjectInputStream in = null;   
    private static ObjectOutputStream out = null;   
    private static String host = "";   
    private static int port = 0; 
    
    private static Chat chat =null;
	static String nameOfThisClient=null;
	protected static ArrayList<PrivateChatGUI> listOfPrivateChat = new ArrayList<>(); //to hold all private chats opened
    
    public static void main(String[] args)  //TO INITIALIZE. ENTER 2 ARGUMENTS:   IP and PORT  i.e.   127.0.0.1  10007
    {   
    	if (args.length != 2 )    
        {    
            System.out.println("please insert IP of host and its port number");    
        }    
        else    
        {  
            host = args[0];
            port = Integer.parseInt(args[1]);  
            new LoginGUI();   
        }   
    }   
    
    public static Message register(Message messageIn)   
    {      	
    	Message confirmationFromServer = new Message(); //this variable is the one to be returned 
        try   
        {   
            socketForServer = new Socket(host, port);   
            in = new ObjectInputStream(socketForServer.getInputStream());   
            out = new ObjectOutputStream(socketForServer.getOutputStream());  
            out.writeObject(messageIn);   //sent request for register
            out.flush();                
            
            try   
            { 
            	confirmationFromServer = (Message) in.readObject();  //receives confirmation
            }   
            catch (Exception e)   
            {   
                JOptionPane.showMessageDialog(null, "error receiving Confirmation from server to register!", "Information",JOptionPane.INFORMATION_MESSAGE);   
            }   
            out.close();   
            in.close();   
        }   
        catch (UnknownHostException e)   
        {   
            JOptionPane.showMessageDialog(null, "Host Unreachable!" ,"Information", JOptionPane.INFORMATION_MESSAGE);   
        }   
        catch (IOException e)   
        {   
            JOptionPane.showMessageDialog(null,"error sending information for registration to server" ,"Information" ,JOptionPane.INFORMATION_MESSAGE);  
            confirmationFromServer.setContent("anything"); 
            //this will do anything, but will avoid a program error.confirmationFromServer is return to, LoginGUI. and only accepts "Success","Fail0","Fail2","Fail3"    
        }   
        finally   
        {   
            try   
            {   
                if (socketForServer != null)   
                {   
                    socketForServer.close();   
                }   
            }   
            catch (IOException e)   
            {  
                JOptionPane.showMessageDialog(null, "Error closing the socket while registering" ,"Information",JOptionPane.INFORMATION_MESSAGE);   
            }   
        }  
        
        return confirmationFromServer;
    }   

    
    public static Message login(Message messageIn)   
    {   
    	Message confirmationFromServer = new Message();
    	
        try
        {
            socketForServer = new Socket(host, port);   
            in = new ObjectInputStream(socketForServer.getInputStream());   
            out = new ObjectOutputStream(socketForServer.getOutputStream());  
            out.writeObject(messageIn);   //sends petition to log in
            out.flush();
            nameOfThisClient = messageIn.getSender();
            
            try   
            {   
            	confirmationFromServer = (Message) in.readObject();  //receives confirmation back  
            	
                if (confirmationFromServer.getContent().equals("Success"))   
                {   
                	chat = new Chat(nameOfThisClient);
                	
                	Message firstUpdateOfUserList = (Message) in.readObject(); //we received a message to update the list of users for first time                	
                	chat.updateFromServer(firstUpdateOfUserList);
                	
                	ClientMain thisClass = new ClientMain();
                	Thread t1 = new Thread(thisClass); 		//we create a thread of this class to be able to communicate always with server
                	t1.start();               	
                } 
            }   
            catch (IOException e)   
            {   
                JOptionPane.showMessageDialog(null,"Information", "Error receiving first message from server!",JOptionPane.INFORMATION_MESSAGE);   
            }   
            catch (ClassNotFoundException e)
            {
            	e.getStackTrace();
            }   
        }   
        catch (UnknownHostException e)   
        {   
            JOptionPane.showMessageDialog(null, "Information","Host Unreachable!", JOptionPane.INFORMATION_MESSAGE);   
        }   
        catch (IOException e)   
        {   
            JOptionPane.showMessageDialog(null, "Log in petition could not be sent!","Information",JOptionPane.INFORMATION_MESSAGE);  
            confirmationFromServer.setContent("Fail"); //this will do nothing but evoid a program error. confirmationFromServer is return to, LoginGUI. and only acceps "Success"           
        } 
        
        return confirmationFromServer;  
    } 
    
 
    public static boolean sendMessageToServer(Message fromGUI)  
    {  
    	boolean confirmationDeliveryMessage = true;
        try   
        {      
            out.writeObject(fromGUI);;   
            out.flush();              
        }          
        catch (IOException e)   
        {  
            JOptionPane.showMessageDialog(null, "could not be sent message to server", "Information",JOptionPane.INFORMATION_MESSAGE); 
            confirmationDeliveryMessage =false;
        }  
        
        return confirmationDeliveryMessage;
    }   
    
    //when we received a message(messageContent) from another client(nameIn). we either open a new window for that client or send that message to thes existing one.
    public static void sendMessageToPrivateChat(String nameIn, String messageContent)
    {
    	if(checkIfChatIsActive(nameIn)) //if the conversation is active we look for it in the list and bring it to the front of the screen 
    	{    		
	    	for(int i=0;i<listOfPrivateChat.size();i++) 
			{	
	    		if(listOfPrivateChat.get(i).getThisPrivateChatName().equals(nameIn))
	    		{
	    			listOfPrivateChat.get(i).toFront();
	    			listOfPrivateChat.get(i).txtarea_messages.append("\n\r"+nameIn+": "+messageContent);
					break;
	    		}
			}
		}
    	else //if is not active, we create a new window chat
    	{			
    		PrivateChatGUI newPrivateChat = new PrivateChatGUI(nameIn);
    		newPrivateChat.toFront(); //this will bring the window to the front of the screen
			newPrivateChat.txtarea_messages.append("\n\r"+nameIn+": "+messageContent);
			addNewChatToList(newPrivateChat);
	
    	}
    }
    
    public static boolean checkIfChatIsActive(String nameIn)
    {
    	if(listOfPrivateChat.size() >0 )
    	{
	    	for(int i=0;i<listOfPrivateChat.size();i++)
			{
				if(listOfPrivateChat.get(i).getThisPrivateChatName().equals(nameIn)) //the chat is already active/open
				{			
					listOfPrivateChat.get(i).toFront(); //this will bring the windows active to the front of the screen
					listOfPrivateChat.get(i).setState(JFrame.NORMAL);
					return true;
				}
			}    
    	}
    	return false;
    }
    
    public static void addNewChatToList(PrivateChatGUI PrivateChatIn) //when a user open a new privateChat we add to the list to know the active conversations
    {	
    	listOfPrivateChat.add(PrivateChatIn);
    }
    
    public static void deleteChatFromList(String nameIn) //when a chat window is closed we take the name of that window, search it and delete from the list
    {
    	for(int i=0;i<listOfPrivateChat.size();i++)
		{
			if(listOfPrivateChat.get(i).getThisPrivateChatName().equals(nameIn)) //if the name of the chat is equal to the one we are looking for, we delete that chat window
			{			
				listOfPrivateChat.remove(i);
				break;
			}
		}
    }
    
    public void run()  
    {    	
        try 
        { 
        	while (true)
        	{
	        	Message message = (Message) in.readObject();        
	            chat.updateFromServer(message);
        	}
        } 
        catch(IOException e) 
        { 
        	JOptionPane.showMessageDialog(null, "error reading message from server", "Information",JOptionPane.INFORMATION_MESSAGE); 
        }  
        catch (ClassNotFoundException e) 
        { 
            e.printStackTrace(); 
        } 
    } 
     
    
}   
