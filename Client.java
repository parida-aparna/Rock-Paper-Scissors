import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.event.*;
import java.io.InterruptedIOException;
import javax.swing.*;

/**This is the Program that handles the Client Side
 *
 * @author Aparna Parida & Khadeeja
 */
public class Client extends Frame implements ActionListener{
    private static String player_nick;//String class field storing the nickname of the player
    private static String opponent_nick;//String class field storing the nickname of the opponent
    private static PrintWriter pw;//Field of the printwriter class, which will be used to send string objects to the server
    private static BufferedReader br, brKeyboard;//Bufferedreader fields for loading String type
    private static Socket clientSocket;//Client socket to connect to the server
    private static String txt2;//String class field that will store the text entered by the player
    private static int p1;
    private static int p2;

    private Button b;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Label l1;
    private Label l2;
    private Label l3;
    private Label l4;
    private Label l5;
    private Label l6;
    private static Label l7;
    private static Label l8;
    private static Label l9;
    private static Label l10;
    private static TextField tf;
    private static TextField tf2;
    private static TextArea ta;
    private static ActionEvent th;

   
    public Client(Frame f){
        //f.setLayout(new FlowLayout());//set layout
  		f.setBackground(Color.orange);//set background color of the Frame
  		b=new Button("Click To Start Server Connection");//Send Button
        /**
        //This Part was added to add Icons, instead of text, but for some reason, it didn't work as it was shown in TutorialsPoint.
  		Icon paper = new ImageIcon("/home/ankitaparna/Documents/Final/1.PNG");
        Icon scissors = new ImageIcon("/home/ankitaparna/Documents/Final/2.PNG");
        Icon rock = new ImageIcon("/home/ankitaparna/Documents/Final/3.PNG");

  		b1=new Button(rock);
 		b2=new Button(paper);
  		b3=new Button(scissors);
        */
  		b1=new Button("Rock");
 		b2=new Button("Paper");
  		b3=new Button("Scissors");
  		b4=new Button("Quit Server Connection");
  		//Adding ActionListener to the buttons, so that OnClick does some function.
  		b.addActionListener(this);//Add action listener to send button.
        	b1.addActionListener(this);
        	b2.addActionListener(this);
        	b3.addActionListener(this);
        	b4.addActionListener(this);
        	//Adding ActionCommand to the buttons, so that in ActionListener, actions can be implemented based on respective button click.
  		b.setActionCommand("SEND");
  		b1.setActionCommand("ROCK");
        	b2.setActionCommand("PAPER");
        	b3.setActionCommand("SCISSORS");
        	b4.setActionCommand("QUIT");
        	//Setting Background Color fpor button, just for Fun.
        	b.setBackground(Color.red);
        	b1.setBackground(Color.pink);
        	b2.setBackground(Color.pink);
        	b3.setBackground(Color.pink);
        	b4.setBackground(Color.lightGray);
        	//This WindowListener is the reason the tab closes when clicked on top.
  		f.addWindowListener(new W1());//add Window Listener to the Frame
        	//l1=new Label("Rock-Paper-Scissors");   //Label removed, well cause Frame has a Title
        	l1=new Label("      ");
  		l2=new Label("By- Aparna & Khadeeja");
  		l3=new Label("Your Name");
  		l4=new Label("Your Opponent's Name");
  		l5=new Label("Your Choice");
  		l6=new Label("Client Progress...");
                l7=new Label("-");
                l8=new Label("-");
                l9=new Label("-");
                l10=new Label("-");
  		tf=new TextField(15);
		tf2=new TextField(20);
  		ta=new TextArea(20,20);
  		ta.setBackground(Color.cyan);
  		//TextArea is being set to Not Editable, so that user can't write anything into it.
  		ta.setEditable(false);
  		//At first, the Rock, Paper and Scissors Button have been set to Not-Clickable.
        	b1.setEnabled(false);
        	b2.setEnabled(false);
        	b3.setEnabled(false);
        	//At first, a simple layout was maintained, for the sake of first building the layout. After that step was done,
        	//Box Layout was used on top of the frame to bundle certain components together, and showcasing them.
 		Box box1 = Box.createHorizontalBox();//Define Author
 		Box box2 = Box.createHorizontalBox();//Name Label and TextField
 		Box box3 = Box.createHorizontalBox();//Opponent Name Label and TextField
 		Box box4 = Box.createHorizontalBox();//Starting the Connection button
 		Box box5 = Box.createHorizontalBox();//Choice Buttons
 		Box box6 = Box.createVerticalBox();//Label, TextArea and Quit Button
		Box box7 = Box.createVerticalBox();
                Box box8 = Box.createHorizontalBox();
                Box box9 = Box.createHorizontalBox();
		box1.add(l1);
 		box1.add(l2);
 		box2.add(l3);
		box2.add(tf);
 		box3.add(l4);
 		box3.add(tf2);
 		box4.add(b);
 		box5.add(b1);
 		box5.add(b2);
 		box5.add(b3);
 		box6.add(l6);
 		box6.add(ta);
 		box6.add(b4);
                box8.add(l7);
                box8.add(l8);
                box9.add(l9);
                box9.add(l10);
		box7.add(box1);box7.add(box2);box7.add(box3);box7.add(box4);box7.add(box5);box7.add(box6);box7.add(box8);box7.add(box9);
       		f.add(box7);
		setFont(new Font("Arial",Font.BOLD,20));
        	f.setSize(300,650);//set the size
        	f.setVisible(true);//Showing the frame
        	f.setLocation(150,50);//set the location
        	f.validate();
    	}

    	@Override
    	public void actionPerformed(ActionEvent e) {
        //Object src = e.getSource(); //This can be used to obtain the src, and then using the same to choose the
        //action as per button name, rather the command associated. In that case, ActionCommand wouldn't have used.
	th = e;//The same ActionEvent is being used in the MessageThread
        String cmd = e.getActionCommand();
        switch (cmd)
	{
           	case "SEND"://Establishing Connection with the Server
                if("".equals(tf.getText()) || "".equals(tf2.getText()) || " ".equals(tf.getText()) || " ".equals(tf2.getText()))
		{
                     Appendta("User hasn't inputted into the fields provided.");
                }
		else
		{
			b.setEnabled(false);
                	b1.setEnabled(true);
                	b2.setEnabled(true);
                	b3.setEnabled(true);
                	String[] output = new String[2];
                	Appendta("Remember, your opponent's name must be either of these: (Enter 'AI' to play with PC)");
                	output[0] = tf.getText();//load the player nick from the keyboard
                	output[1] = tf2.getText();//load the second player's nickname from the keyboard
                	l7.setText(tf.getText());
                	l8.setText(tf2.getText());
                
                	Appendta(output[0] + " chose " + output[1] + " as their opponent.");//display the menu and download from the player his nickname and opponent's nickname
                	//     b2.setEnabled(true);
                	String servername = "localhost";//determining the server name
                	try 
			{
               		    	new Client(output[0], output[1], servername);// create a objectClient
               	 	} 
			catch (Exception ex) 
			{
                    		Appendta("Problem with creating object Client:" + ex.getMessage());
                	}
                }
                break;
            //THe rest of the buttons.
            case "ROCK":
            case "PAPER":
            case "SCISSORS":
                new MessagesThread().start();//start a new thread. The run () method is started
            break;
            case "QUIT":
                try
		{
        	        pw.println("Quit");// sending information about the player's choice to the server
        	        pw.println(player_nick + " Won " + p1 + " matches.\n");
        	        System.exit(0);
                }
		catch (Exception ex) 
		{
                    Appendta("Connection To Server has yet not been Done." + ex.getMessage());
                }
                break;
        }
 }

//Class That closes the application at the top Cross
	class W1 extends WindowAdapter
 	{
	        @Override
  		public void windowClosing(WindowEvent we)
  		{
  			 System.exit(0);
  		}
	}

    //The Function where the System Prints are appended into the TextArea.
  	public static void Appendta(String txt) 
	{
        	ta.append(txt + "\n");
    	}

    	public Client(String player_nick, String opponent_nick, String server_name) 
	{
	        this.player_nick = player_nick;//assign a name to the player
	        this.opponent_nick = opponent_nick;//assign an opponent's name
	        try {
	            clientSocket = new Socket(server_name, 1803);//creating the client socket. the connection is via port 1413. If the port is busy, you can enter a different value. IMPORTANT: The port 									number must be the same on the server side.
        	} catch (UnknownHostException ex) {
        	    Appendta("Problem with an Unknown Host " + ex.getMessage());
        	} catch (IOException ex) {
        	    Appendta("Problem with Connecting to Server " + ex.getMessage());
        	}
		try {
           	br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//create an object receiving the input stream from the server
        	} catch (IOException ex) {
        	Appendta("Problem with BufferedReader " + ex.getMessage());
        	}
        
        	try {
        	    pw = new PrintWriter(clientSocket.getOutputStream(), true);//creating an object sending string data to the server
        	} catch (IOException ex) {
        	    Appendta("Problem with PrintWriter " + ex.getMessage());
        	}
        	pw.println(this.player_nick);//sending the player's nickname to the server
        	pw.println(this.opponent_nick);//send the opponent's nickname to the server
        	l7.setText(this.player_nick);
        	l8.setText(this.opponent_nick);
        	l9.setText("0");
        	l10.setText("0");
        	p1=0; p2=0;
    	}	
    
    	public static void increp1() 
	{
	        p1++;
	}

	public static void increp2() 
	{        	
		p2++;
    	}


    	public static void main(String... args) 
	{
	        Frame f= new Frame("Rock Paper Scissors");//Initializing the frame
	        new Client(f);//Creating the Frame and Showing the GUI to the User
	        displayMenu(); //display the menu and then, performing the various actions based on the user's input and button Click
   	}

    	static class MessagesThread extends Thread
	{

        	@Override
        	public void run()
 		{
        	    try {
            		String txt = "Keep_Running";//Just for the sake of initializing, Keep running until the button press leads to changing you.
               		Appendta("We Hope you have chosen either of the following:\n'Paper', 'Scissors' or 'Rock'. or 'Quit' to end the game.");
                	do {
                		String cmd = th.getActionCommand();
        			switch (cmd) 
				{
           				 case "ROCK":
           				 	txt="Rock";break;
           				 case "PAPER":
           				 	txt="Paper";break;
           				 case "SCISSORS":
           				 	txt="Scissors";break;
                    		}
                	} while (!"Paper".equals(txt) && !"Scissors".equals(txt) && !"Rock".equals(txt) && !"Quit".equals(txt));//if the player types something non-compliant game will be asked to enter 																							again
	                pw.println(txt);// Send the selection to the server
                 
                        Appendta(br.readLine());//Information about the election of both players
                        txt2=br.readLine();
                     	if(!"Tie".equals(txt2))
                        	Appendta(txt2 + " is the winner!!!");//Winner information
                        else if("Tie".equals(txt2))
				Appendta("There's a tie");   
                           
                    
                          
                    if(player_nick.equals(txt2)){
                        increp1();
                        l9.setText("" + p1);
                    }else if(opponent_nick.equals(txt2)){
                        increp2();
                        l10.setText("" + p2);
                    }else if("Tie".equals(txt2)){
                        increp1();
                        increp2();
                        l9.setText("" + p1);
                        l10.setText("" + p2);
                    }
                    br.readLine();// Information about resetting previous elections on the server
            }catch (IOException ex) {
                Appendta("Problem with receiving data by Client: " + ex.getMessage());
            }
        }
    }

    private static void displayMenu() {
        Appendta("======$#%|DC-PROJECT|%#$=======");
        Appendta("              | Rock Paper Scissors |");
        Appendta("==========**<^>**===========");
        Appendta("Input Into the Fields Provided.\n\nWhen you are done, press the Button following, named Start Client Connection");
    }
}
