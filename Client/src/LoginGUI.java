import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;  
import java.awt.*;   

public class LoginGUI extends JFrame 
{   
    JDialog d1;  //this variable is to create the register window, to overlap the login one you sees it when executed
    private JFrame frame;   
    private JTextField textField;   
    private JPasswordField passwordField;   
    private JTextField reTextField;   
    private JPasswordField rePasswordField ;   
    private JPasswordField rePasswordField_1; 
    
 
    public LoginGUI()  
    {   
        initialize();   
    } 
     
    private void initialize() 
    {   
        frame = new JFrame();   
        frame.setTitle("  Welcome to Fantabulous CHAT");
        frame.getContentPane().setBackground(new Color(102, 153, 204));   
        frame.setBackground(SystemColor.activeCaptionBorder);   
        frame.setBounds(100, 100, 450, 300);   
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        frame.getContentPane().setLayout(null);   
        
        JLabel lblUsername = new JLabel("Username");   
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 15));  
        lblUsername.setForeground(new Color(255, 255, 255));   
        lblUsername.setBounds(46, 97, 80, 14);   
        frame.getContentPane().add(lblUsername);   

        textField = new JTextField();   
        textField.setBounds(130, 94, 223, 20);   
        frame.getContentPane().add(textField);   
        textField.setColumns(10);   
        
        passwordField = new JPasswordField();   
        passwordField.setBounds(130, 118, 223, 20);   
        frame.getContentPane().add(passwordField);   

        JLabel lblNotRegisteredClick = new JLabel("Not registered? click ");  
        lblNotRegisteredClick.setForeground(Color.WHITE);  
        lblNotRegisteredClick.setFont(new Font("Tahoma", Font.PLAIN, 14));  
        lblNotRegisteredClick.setBounds(190, 228, 135, 19);  
        frame.getContentPane().add(lblNotRegisteredClick); 
        
        JLabel lblPassword = new JLabel("Password");   
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));  
        lblPassword.setForeground(new Color(255, 255, 255));   
        lblPassword.setBounds(46, 121, 80, 14);   
        frame.getContentPane().add(lblPassword);   

        JButton btnLogin = new JButton("Login");  
        btnLogin.setBounds(190, 149, 104, 23);   
        frame.getContentPane().add(btnLogin);   
        btnLogin.addActionListener(new ActionListener()  
        {   
            public void actionPerformed(ActionEvent e)  
            { 
                if(textField.getText().equals("")) { 
                    JOptionPane.showMessageDialog(null, "Please enter username!"); 		//
                } 
                else if(passwordField.getText().equals(""))								//  <<<  checks if the fields are empty
                { 
                    JOptionPane.showMessageDialog(null, "Please enter the password!");  //
                } 
                else 
                {  
                	Message loginPetition = new Message(); 
                	loginPetition.setType("Login");   				//prepare and send a message to clientMain.login which will send it to server
                	loginPetition.setSender(textField.getText());
                	loginPetition.setContent(textField.getText() + "," + String.valueOf(passwordField.getPassword()));
                	
                	loginPetition  = ClientMain.login(loginPetition);  //we received the confirmation from server
                	
                    if (loginPetition.getContent().equals("Success")) 
                    { 
                        frame.dispose();  //close the window
                    } 
                    else if(loginPetition.getContent().equals("Fail2")) 
                    {   
                        JOptionPane.showMessageDialog(null, "The username and password don't match, please try again"); 
                    } 
                    else if(loginPetition.getContent().equals("Fail0")) 
                    { 
                        JOptionPane.showMessageDialog(null, "User name does not exists, please register first!"); 
                        textField.setText(""); 			//set fields empty
                        passwordField.setText(""); 
                    } 
                    else if(loginPetition.getContent().equals("Fail3"))
                    {
                    	JOptionPane.showMessageDialog(null, "You are already logged In"); 
                    }
                } 
            }   
        });   

        //when the user click this button, the register GUI opens
        JButton btnHere = new JButton("here");  
        btnHere.setBounds(321, 229, 82, 20);  
        frame.getContentPane().add(btnHere);       
        btnHere.addActionListener(new ActionListener()
        {  
            public void actionPerformed(ActionEvent e)
            {  
                //the code for register gui is here               
                jDialog();  
            }  
        });  

        frame.setVisible(true);   
    }  
    
    public void jDialog() 
    {  
        d1 = new JDialog(this,"	Register form	",true);  
        d1.getContentPane().setBackground(new Color(102, 153, 204));  
        d1.setBackground(SystemColor.activeCaptionBorder);   
        d1.setBounds(100, 100, 450, 300);  
        d1.setResizable(false);
        d1.getContentPane().setLayout(null);   

        JLabel lblUsername = new JLabel("Username");   
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 15));  
        lblUsername.setForeground(new Color(255, 255, 255));   
        lblUsername.setBounds(99, 99, 67, 14);          
        d1.getContentPane().add(lblUsername);  

        reTextField = new JTextField();   
        reTextField.setBounds(170, 93, 223, 20);   
        d1.getContentPane().add(reTextField);   
        reTextField.setColumns(10);   

        rePasswordField = new JPasswordField();   
        rePasswordField.setBounds(170, 118, 223, 20);   
        d1.getContentPane().add(rePasswordField);   

        JLabel lblConfirmPassword = new JLabel("Confirm password");  
        lblConfirmPassword.setForeground(Color.WHITE);  
        lblConfirmPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));  
        lblConfirmPassword.setBounds(50, 150, 116, 14);  
        d1.getContentPane().add(lblConfirmPassword);  

        rePasswordField_1 = new JPasswordField();  
        rePasswordField_1.setBounds(170, 144, 223, 20);  
        d1.getContentPane().add(rePasswordField_1); 

        JLabel lblPassword = new JLabel("Password");   
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));  
        lblPassword.setForeground(new Color(255, 255, 255));   
        lblPassword.setBounds(105, 124, 61, 14);   
        d1.getContentPane().add(lblPassword);  

        JButton btnRegister = new JButton("Register");  
        btnRegister.setBounds(180, 186, 99, 23);   
        d1.getContentPane().add(btnRegister); 
        btnRegister.addActionListener(new ActionListener() 
        {   
            public void actionPerformed(ActionEvent e)  
            {    
                if(reTextField.getText().equals("")|| rePasswordField.getText().equals("")|| rePasswordField_1.getText().equals("")) 
                { 																				//
                    JOptionPane.showMessageDialog(null, "Please fill all the fields");			//
                } 																				//  checks if the fields are empty and its size
                else if((rePasswordField.getText().length())<=5) 								//
                { 
                    JOptionPane.showMessageDialog(null, "Password must be at lest 6 characters long"); 
                } 
                else 
                { 
                    if (rePasswordField.getText().equals(rePasswordField_1.getText()))  //checks if password and the repetition are equal
                    {  
                    	Message registerPetition = new Message();
                    	registerPetition.setType("Register");
                    	registerPetition.setContent(reTextField.getText()+","+String.valueOf(rePasswordField.getPassword()));
                    	
                        registerPetition = ClientMain.register(registerPetition);  //we receive confirmation from server
                        
                        if(registerPetition.getContent().equals("Fail")) 
                        { 
                            JOptionPane.showMessageDialog(null, "Username already exists, please try a differen username"); 
                        } 
                        else if(registerPetition.getContent().equals("Success"))
                        { 
                            JOptionPane.showMessageDialog(null, "Congratulations, you are now registered!"); 
                            d1.dispose();	//this closes the window
                            textField.setText(null); 
                            passwordField.setText(null); 
                        } 
                    } 
                    else 
                    {  
                           JOptionPane.showMessageDialog(null, "Please make sure your passwords match", "Warning",JOptionPane.ERROR_MESSAGE);  
                    }  
                } 
            }             
        });  
 
        d1.setVisible(true);  
    }  
   
    
    public void actionPerformed(ActionEvent e) 
    {   

    }   
    


}   

  

   

 