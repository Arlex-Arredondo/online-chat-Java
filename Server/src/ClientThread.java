import java.io.*;
import java.net.*;
 
public class ClientThread extends Thread  //this thread is a the server communicating with an specific client.										
{
	Socket client;
	String nameOfThisThread;		//when the user logs in, we give his name to this thread, so we can identify this thread when sending and receiving private chats
	ObjectOutputStream messageOut = null;
	ObjectInputStream messageIn = null;
	
	public ClientThread(Socket s)
	{		
		client = s;
		
		try 
		{ 
			messageOut = new ObjectOutputStream(client.getOutputStream());
			messageIn = new ObjectInputStream(client.getInputStream());
		} 
		catch (IOException e) 
		{
			e.getStackTrace(); System.out.println("error initializing streams in constructor");
		} 
		
		initializeThisClientConnection();
	}
	
	public void initializeThisClientConnection()
	{	
        Message theMessage = new Message(); 
        try     
        {    
        	//here we receive a message  from the client type Login or Register
            theMessage = (Message) messageIn.readObject(); 
        }     
        catch (ClassNotFoundException e)     
        {      
        	System.out.println("error reading login/register type from client. ");e.printStackTrace();    
        } 
        catch (IOException e)
        {
			System.out.println("error reading from client");
			e.printStackTrace();
		}                 
        
        if (theMessage.getType().equals("Login"))
        { 
        	nameOfThisThread = theMessage.getSender();//stores the name of the person who wants to login, in case is successfull we give that name to the thread
        	boolean checkIfLoggedIn = false;
        	
        	if(ServerMain.listOfThreads.size() >0 )//if there is already someone logged in
        	{
        		checkIfLoggedIn = ServerMain.checkIfAlreadyLoggedIn(nameOfThisThread);
    			if(checkIfLoggedIn) //the user is already logged in
    			{
    				theMessage.setType("Confirmation");
    				theMessage.setContent("Fail3"); //this error  tells user he is logged in already
    				try
    				{
    					messageOut.writeObject(theMessage);  
    					messageOut.flush();
    				}
    				catch(IOException e)
    				{
    					System.out.println("error sending Fail3 message");e.getStackTrace();
    				}
    				
    				//as he is logged In we close this socket
    				try 
    				{
						messageOut.close();
						messageIn.close();
	    				client.close();
					} catch (IOException e) {
						System.out.println("error--closing socket when is logged in. ");e.printStackTrace();
					}
    				
    			}       		         		
        	}
        	
        	if(checkIfLoggedIn == false)
        	{
                theMessage = Login(theMessage);    //we send that message to the login method (it sends back a message with type confirmation)
                
                if (theMessage.getContent().equals("Success")) //if the content of the message we receive from Login method is success
                {    
	                try 
	                {
	                	messageOut.writeObject(theMessage);//send confirmation of successfull login to client 
	                	messageOut.flush();
					} 
	                catch (IOException e) 
	                {
						System.out.println("error sending login in success"); e.printStackTrace();
					}  
	                
                    ServerMain.startThread = true; //this will start this thread, so the servers knows
                   
                }    
                else ////if the content of the message we receive from Login method is Fail0 or Fail2
                {    
                	try
                	{
                		messageOut.writeObject(theMessage);
                		messageOut.flush();
                	}
                	catch (IOException e)
                	{
                		System.out.println("error sending fail to login"); e.getStackTrace();
                	}
                }    
        	} 
        }   
        else if (theMessage.getType().equals("Register"))
        {    
            theMessage = Register(theMessage);    //we send that message to the login method (it sends back a message with type confirmation)
            try
            {
            	messageOut.writeObject(theMessage); //sends a confirmation to client if successfull registration or not (Fail or Success) 
            	messageOut.flush();
            }
            catch(IOException e)
            {
            	System.out.println("error sending confirmation of Register"); e.getStackTrace();
            }
        }   
	}
		

    public Message Login(Message messageIn)    
    {    
    	//messageIn in " String content ", comes with  a username and password separated by a coma
        // we save in this array in [0] the username and in [1] the password    
        String[] userAndPass = messageIn.getContent().split(",");    
        DB database = new DB();  

        Message messageToSend = new Message(); //we create the object type message in which we will send the confimation back to the client    
        int answerFromDB = database.userExists(userAndPass[0],userAndPass[1]); //we check if the user who tries to login exist, sends back a number

        if (answerFromDB == 1)   //it is 1 if user and password are OK 
        {    
            messageToSend.setType("Confirmation");    //we set the message variables
            messageToSend.setContent("Success");    
        }    
        else  
            { 
                if (answerFromDB == 2)    //it is 2 if user OK  but password NOT OK
                {    
                    messageToSend.setType("Confirmation");    
                    messageToSend.setContent("Fail2");   
                } 
                else  
                { 
                    if(answerFromDB == 0)   //it is 0 if user and password NOT OK
                    { 
                        messageToSend.setType("Confirmation");    
                        messageToSend.setContent("Fail0");  
                    } 
                }    
            }           

        return messageToSend;    
    }    

     
    public Message Register(Message messageIn)    
    {    
    	//the Login message in the String content, comes with  a username and password separeted by a coma
        // we save in this array in [0] the username and in [1] the password    
        String[] userAndPass = messageIn.getContent().split(",");   
        DB database = new DB(); 
        
        Message messageToSend = new Message();   //we create the object type message in which we will send the confimation back to the client         
        int answerFromDB = database.userExists(userAndPass[0],userAndPass[1]); // check if the user exists in DB (it will send back a number)

        if (answerFromDB == 1)    //if the answer of the checking is 1 user exist in the DB
        {    
            messageToSend.setType("Confirmation");    
            messageToSend.setContent("Fail");              
        }    
        else if(answerFromDB == 0) //if the answer of the checking is 0, the user does not exist in the DB, we can procceed to register that user
        {    
            messageToSend.setType("Confirmation");    
            messageToSend.setContent("Success");    
            database.DBM(userAndPass[0], userAndPass[1]);  //we register that user in the DB
        }    
        
        return messageToSend;    
    }    
    
    
	public String getThisThreadname()
	{
		return nameOfThisThread;
	}

	
	public  void sendMessageToClient(Message message)    
	{ 
		try 
		{
			messageOut.writeObject(message);
			messageOut.flush();
		} 
		catch (IOException e)
		{
			System.out.println("in thread. sending"); e.printStackTrace();
		}		 
	}

	
	public void run()	
	{	 		
		try
		{
			while(true) 
			{
				Message theMessage = (Message) messageIn.readObject();			
				ServerMain.messageFromClient(theMessage);
			}
		}
		catch (IOException e)
		{			
			System.out.println("connection lost with client. "+e); 
			ServerMain.LogOut(nameOfThisThread);  //when the connection is lost with a client we have to take him out of the list
			try 
			{
				messageOut.close();
				messageIn.close();
				client.close();
			} 
			catch (IOException e1) 
			{				
				System.out.println("error closing client connection at thread. ");e1.printStackTrace();
			}
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
}
