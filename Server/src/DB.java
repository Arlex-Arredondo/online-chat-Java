 

import java.sql.*; 

public class DB 
{ 
	public static Connection conn=null;
    public  void DBM(String usernameIn, String passwordIn) //this method is called when we want to make a new Registration
    { 
        try  
        { 
            String USERNAME = usernameIn; 
            String PASSWORD = passwordIn; 
             
            Class.forName("org.h2.Driver"); 
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa"); 
            Statement st = conn.createStatement(); 
 
            String query; 
            int n; //when a query is made it returns a number
            
            // if(table != exists) 
            // { 
            // query = "CREATE TABLE DBUSERS (USERNAME VARCHAR(255) PRIMARY KEY, 
            // PASSWORD VARCHAR(255));"; 
            // n = st.executeUpdate(query); 
            // System.out.println(n); 
            // } 
            
            String salt = BCrypt.gensalt(); 
            PASSWORD = BCrypt.hashpw(PASSWORD, salt); //we stores the password of the new user encrypted
           
            query = "INSERT INTO DBUSERS VALUES ('" + USERNAME + "','" + PASSWORD + "' );"; 
            n = st.executeUpdate(query);  //we send the query to the DB
            
            // We have successfuly created a DB 
            // SELECT * FROM CREDENTIALS; 
            st.close(); 
            conn.close(); 
        }  
        catch (ClassNotFoundException e) 
        { 
            e.printStackTrace(); 
        } 
        catch (SQLException e) 
        { 
            e.printStackTrace(); 
        } 
    }
    

    public int userExists(String usernameIn,String passwordIn) 
    { 
        boolean userExists = false; 
        boolean passExists = false; 
        int n; //variable to be returned
        
        try 
        { 
            String query;          
            
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa"); 
            Statement st = conn.createStatement(); 
            query = "SELECT * FROM DBUSERS;"; 
            ResultSet rs = st.executeQuery(query);  //we received row by row from the db
             
            while (rs.next())  //read the row
            { 
                String username = rs.getString("USERNAME"); 
                String password = rs.getString("PASSWORD"); 
                
                if (username.equals(usernameIn)) //we compare the data in the database (username and password) with the data to be compared (usernameIn and passwordIn)
                { 
                    if(BCrypt.checkpw(passwordIn, password)) //as the password stored is encrypted we use checkpw (data to be compared, and encrypted data)
                    { 
                        userExists =true; 
                        passExists =true; 
                        break; 
                    } 
                    else 
                    { 
                        userExists =true;  //password was wrong
                    } 
                } 
            } 

            rs.close(); 
            st.close(); 
            conn.close(); 
        } 
        catch (SQLException e) 
        { 
            e.printStackTrace(); 
        } 

        if(userExists && passExists) 
        { 
            n = 1; //exist in the DB 
        } 
        else if(userExists && !passExists) 
        { 
            n = 2; //user ok password wrong 
        } 
        else 
        { 
            n = 0; //user and pass wrong/does not exist in DB 
        } 
             
        return n;
    } 

  public static void checkIfUserTableExists(String path, String user, String password) {
	  try
	  {
		  Class.forName("org.h2.Driver"); 
          conn = DriverManager.getConnection(path, user, password); 
          Statement st = conn.createStatement(); 
		  DatabaseMetaData dbm = conn.getMetaData();
		  ResultSet tables = dbm.getTables(null, null, "DBUSERS", null);
		  if (!tables.next()) 
		  {
			 
			  String query = "CREATE TABLE DBUSERS (USERNAME VARCHAR(255) PRIMARY KEY, PASSWORD VARCHAR(255));"; 
		      st.executeUpdate(query); 
		  }
		  st.close(); 
	      conn.close();
		
		
	  } 
      catch (SQLException e) 
      { 
          e.printStackTrace(); 
      } catch (ClassNotFoundException e) 
      { 
          e.printStackTrace(); 
      } 
	  
  }
    
} 