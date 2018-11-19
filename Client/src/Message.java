
import java.io.Serializable;  

public class Message implements Serializable  
{  
	private String type;  
	private String sender;  
	private String receiver;  
	private String content; 
	private String[] listUsers;  //we save here the list of the users connected. it is check by the client if it is empty or not when a message is type Broadcast
  
    public void setListUsers(String[] listIn) 
    { 
        this.listUsers = listIn; 
    } 
     
    public void setType(String typeIn)  
    {  
        this.type = typeIn;  
    }  
  
    public void setSender(String senderIn) 
    {  
        this.sender = senderIn;  
    }  
  
    public void setReceiver(String receiverIn)  
    {  
        this.receiver = receiverIn;  
    }  
   
    public void setContent(String contentIn) 
    {  
        this.content = contentIn;  
    }  
  
    public String[] getListUsers() 
    { 
        return listUsers; 
    } 
     
    public String getType()  
    {  
        return type;  
    }  
   
    public String getSender()  
    {  
        return sender;  
    }  
   
    public String getReceiver()  
    {  
        return receiver;  
    }  
  
    public String getContent()  
    {  
        return content;  
    }  
  
} 

 