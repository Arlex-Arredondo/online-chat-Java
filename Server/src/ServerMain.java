 
import java.io.*;    
import java.net.*;
import java.util.*;
import java.util.Properties;

public class ServerMain    
{   
	protected static ArrayList<ClientThread> listOfThreads = new ArrayList<>(); //to hold all clientes connected
	protected static boolean startThread =false;
	public static Properties prop= null;
	
    public static void main(String[] args)     
    {
    	
//    	try
//    	{
//    		prop= new Properties();//this creates an object of the class Properties
//    		FileInputStream propIn= new FileInputStream("properties.prop");//This stream allows us to read the properties file
//    		prop.load(propIn);//this loads the properties in memory
//    		propIn.close();//this closes the FileInputStream, properties are still loaded in memory and can be used
//    	}catch (IOException e) 
//    	{
//    		e.printStackTrace();
//    	}
    	
    	if (args.length !=1)
    	{
    		System.out.println("Server usage, insert port");
    	}
    	else
    	{
    		
	    	while (true)
	    	{
	    	DB.checkIfUserTableExists("jdbc:h2:~/test","sa","sa");//this checks if the table "CBUseres" exists, if not, this method will create it.
	           
	            while (true)    
	            {    
	                startService(Integer.parseInt(args[0]));    
	            }
	    	}
    	}
    }    
       
  
    public static void startService(int port)    
    {    
        ServerSocket serverSocket = null;    //socket to create the server
        Socket clientConnection = null;    	//socket to receive a client connection
         
        try    
        {                  
            System.out.println("Starting server at port " + port);    
            serverSocket = new ServerSocket(port);  // we start the server and wait for connections   
            System.out.println("Waiting...");    
  
            while (true)   //loop to listn for connections infinitely, it is never false 
            {             	
                clientConnection = serverSocket.accept();  //start listening for connections  
                System.out.println("Accepted from " + clientConnection.getInetAddress());               
                
                ClientThread newThread = new ClientThread(clientConnection); //we create a thread and pass this connection               
                if(startThread==true)//after the previos thread has confirmed that is ready to logIn, we start it.
                {
                	startThread = false; 
                	newThread.start();                	
                	listOfThreads.add(newThread);             
                	setAndSendListOfUsers(); //we inmediately send and update of user list
                }
            } //END WHILE   
        }     //end try
        catch (IOException e)     
        {    
            System.out.println("error starting the server ");e.getStackTrace();
        }              
        try    
        {    
            serverSocket.close();    
        }    
        catch (IOException e)    
        {    
            System.out.println("error closing server");    
        }    
    }      
     
    //all this server threads use this method to send private chats or confirmations.  that is why it is syncronized
    public synchronized static void messageFromClient(Message messageIn)    
    {  
    	if(messageIn.getType().equals("PrivateChat"))
    	{
    		for(int i=0;i<listOfThreads.size();i++)		//loop thorugh all the users connected
    		{
    			if(listOfThreads.get(i).getThisThreadname().equals(messageIn.getReceiver())) //find the user to whom the message was sent 
    			{
    				listOfThreads.get(i).sendMessageToClient(messageIn);
    				break;
    			}
    		}
    	}
    	else if(messageIn.getType().equals("Broadcast"))
    	{
    		//when we get the outputStream of every thread in the arrayList, we store it here
    		for(int i=0;i<listOfThreads.size();i++)
    		{
    			listOfThreads.get(i).sendMessageToClient(messageIn);
    		}
    	}        
    }
    
    
    public static void LogOut(String nameIn) //we receive the name of the thread/client who is loginOut
    {
    	 for(int i=0;i<listOfThreads.size();i++)
    	 {
    		 if(listOfThreads.get(i).getThisThreadname().equals(nameIn))
    		 {
    			 listOfThreads.remove(i);
    			 break;
    		 }
    	 }
    	 
    	 setAndSendListOfUsers(); //update the list of users as someone left
    }
    
    public static void setAndSendListOfUsers()
    {
    	 Message theMessage =new Message(); 
         String[] userNamesConnected = new String[listOfThreads.size()];
         
         for(int i=0;i<listOfThreads.size();i++) 
         {
         	userNamesConnected[i] = listOfThreads.get(i).getThisThreadname();
         	//we loop through all the threads and get its name and add it to the array 
         }
         
        //after we added all the names in the array we create a message and send it
     	theMessage.setListUsers(userNamesConnected);
     	theMessage.setType("Broadcast");
     	
     	for(int i=0;i<listOfThreads.size();i++)
     	{
     		listOfThreads.get(i).sendMessageToClient(theMessage);
     	}
    }
    
    //if a person tries to loggIn, this checks its name in the array of users
    public static boolean checkIfAlreadyLoggedIn(String nameIn)
    {
    	for(int i=0;i<listOfThreads.size();i++)
		{
			if(listOfThreads.get(i).getThisThreadname().equals(nameIn)) //the user is already logged in
			{				
				return true;
			}
		}    
    	return false;
    }
}  

  