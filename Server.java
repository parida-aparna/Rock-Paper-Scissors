import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    
import java.awt.Desktop; 

/**This is the Program that handles the Server Side
*/
public class Server extends Frame implements ActionListener{
    private Vector<String> player = new Vector<String>();//Container storing player names
    private Vector<Integer> pscore = new Vector<Integer>();//Container storing player scores
    private Vector<PlayerService> client = new Vector<PlayerService>();//Container holding objects of the class
    private static TextArea ta;
    private Button b1;
    private Button b2;
    private int num;
    static String fileName;
    Frame f;

    public static void main(String... args) { 
        new Server().createServer();//Create a server object
    }

    private void createServer() {
        num = 0;
        f = new Frame("Server Side Progress");
        f.setBackground(Color.orange);//set background color of the Frame
        b1=new Button("Quit");//Send Button
        b2=new Button("GamePlay");
        b1.setBackground(Color.pink);
        b2.setBackground(Color.orange);
        b1.addActionListener(this);//Add action listener to send button.
        b2.addActionListener(this);
        b1.setActionCommand("QUIT");
        b2.setActionCommand("SHOW");
        ta=new TextArea(12,20);
        ta.setEditable(false);
        ta.setBackground(Color.cyan);
        f.addWindowListener(new W1());//add Window Listener to the Frame
        f.add(b2,BorderLayout.NORTH);
        f.add(b1,BorderLayout.SOUTH);//Add send Button to the frame
        f.add(ta,BorderLayout.CENTER);
        setFont(new Font("Arial",Font.BOLD,20));
        f.setSize(400,400);//set the size
        f.setLocation(100,300);//set the location
        f.setVisible(true);
        f.validate();
        fileName = "RockPaperScissors_Data.txt"; 
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
       try { 
            BufferedWriter out = new BufferedWriter( 
                          new FileWriter(fileName, true)); 
            out.write("\n\nThe Below is all the Data\nOf Date And Time\n" + dtf.format(now) + "\n"); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("Exception Occurred" + e); 
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1803); //create the client socket. the connection is via port 1413. If the port is busy, you can enter a different value. IMPORTANT: The port number must be the same as in the client's socket.
        } catch (IOException ex) {
            System.out.println("Problem with creating server socket: " + ex.getMessage());
        }
        Appendta("Server is online");
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept(); //Connect the client socket to the server socket
            } catch (IOException ex) {
                Appendta("Problem with connection to Client socket: " + ex.getMessage());
            }
            PlayerService c = null;
            try {
                c = new PlayerService(clientSocket);
            } catch (Exception ex) {
                Appendta("Problem with creating object to handle the player: " + ex.getMessage());
            }
            client.add(c);//Add a new player to the list of clients
            pscore.add(num, 0);
            Appendta("Added: " + c.player_nick);
            Appendta("Available players: ");
            for (String s : player) {//Print the list of logged players
                Appendta(s);
            }
        }
    }

    private class W1 extends WindowAdapter
 	{
            public void windowClosing(WindowEvent we)
            {
  		 System.exit(0);
            }
 	}

    public void actionPerformed(ActionEvent ae)
 	{
            String cmd = ae.getActionCommand();
            switch (cmd) {
            case "QUIT"://disconnecting with the Client
                    System.exit(0);
                    break;
            //THe rest of the button.
            case "SHOW":
                    if (Desktop.isDesktopSupported()) {
                    try {
                        File myFile = new File("/home/aparna/RockPaperScissors_Data.txt");
                        Desktop.getDesktop().open(myFile);
                    } catch (IOException ex) {
                        Appendta("Cannot Find the File");
    }
}
            }
        }
        
     public static void appendtofile(String fileName, 
                                       String str) 
        { 
        try { 
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( 
                   new FileWriter(fileName, true)); 
            out.write(str); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
        } 

    //Duel of a Player with AI
    private void duel(String player_nick) {
        PlayerService player_service = null;
        for (PlayerService c : client) {//Finding a player among clients
            if (c.playerReturns().equals(player_nick)) {
                player_service = c;
            }
        }
        String choiceAI = drawServerSelection();//Random AI selection (paper, scissors or stone)
		player_service.sendMessage(player_nick + " chose " + player_service.playerSelection + ", AI " + choiceAI);//provide information to the player about who has chosen

        String winner = playerWinner(player_service, choiceAI);//designate the winner based on the choices made
        if ("Tie".equals(winner)) {
            player_service.sendMessage("Tie");//Sending result information to the player
//            increasePS(findplay(winner));
        } else if (winner == null) {
            player_service.sendMessage("Illegal move!");// send information to the player
        } else {
            player_service.sendMessage(winner);//Sending draw information to the player
        }
        player_service.playerSelection = null;//Reset the earlier selection
        player_service.sendMessage("W");//Sending information about resetting earlier elections
    }

    //Duel of a Player with Another Player
    private void duel(String player_nick, String opponent_nick) {
        PlayerService player_service = null;
        PlayerService opponent_service = null;
        for (PlayerService c : client) {//Finding player and opponent
            if (c.playerReturns().equals(player_nick)) {
                player_service = c;
            } else if (c.playerReturns().equals(opponent_nick)) {
                opponent_service = c;
            }
        }
        player_service.sendMessage(player_nick + " chose " + player_service.playerSelection + ", " + opponent_nick + " " + opponent_service.playerSelection);//sending messages to players about the choices made
        opponent_service.sendMessage(opponent_nick + " chose " + opponent_service.playerSelection + ", " + player_nick + " " + player_service.playerSelection);//sending a message to players about the choices made
        String winner = playerWinner(player_service, opponent_service);//designate the winner
        if ("Tie".equals(winner)) {
            player_service.sendMessage("Tie");
            opponent_service.sendMessage("Tie");
        } else if (winner == null) {
            player_service.sendMessage("Illegal move!");
            opponent_service.sendMessage("Illegal move!");
        } else {
            player_service.sendMessage(winner);
            opponent_service.sendMessage(winner);
        }
        player_service.playerSelection = null;//reset the elections
        player_service.sendMessage("W");//provide information about zeroing to the player
        opponent_service.playerSelection = null;//reset the elections
        opponent_service.sendMessage("W");//providing information about zeroing to the opponent
    }

    //Winner between Player and AI
    private static String playerWinner(PlayerService player_service, String choiceAI) {
        String result = null;
        if ("Paper".equals(player_service.playerSelection)) {
            if ("Paper".equals(choiceAI)) {
                result = "Tie";
            } else if ("Scissors".equals(choiceAI)) {
                result = "AI";
            } else if ("Rock".equals(choiceAI)) {
                result = player_service.player_nick;
            }
        } else if ("Scissors".equals(player_service.playerSelection)) {
            if ("Paper".equals(choiceAI)) {
                result = player_service.player_nick;
            } else if ("Scissors".equals(choiceAI)) {
                result = "Tie";
            } else if ("Rock".equals(choiceAI)) {
                result = "AI";
            }
        } else if ("Rock".equals(player_service.playerSelection)) {
            if ("Paper".equals(choiceAI)) {
                result = "AI";
            } else if ("Scissors".equals(choiceAI)) {
                result = player_service.player_nick;
            } else if ("Rock".equals(choiceAI)) {
                result = "Tie";
            }
        }
	if(!"Tie".equals(result))
        Appendta("The winner is " + result);
	else if("Tie".equals(result))
	Appendta("There's a tie");
        return result;
    }

    //Appending to TextArea
    public static void Appendta(String txt) {
    		ta.append(txt + "\n");
    	}

    //Winner between two players
    private static String playerWinner(PlayerService player_service, PlayerService opponent_service) {
        String result = null;
        if ("Paper".equals(player_service.playerSelection)) {
            if ("Paper".equals(opponent_service.playerSelection)) {
                result = "Tie";
            } else if ("Scissors".equals(opponent_service.playerSelection)) {
                result = opponent_service.player_nick;
            } else if ("Rock".equals(opponent_service.playerSelection)) {
                result = player_service.player_nick;
            }
        } else if ("Scissors".equals(player_service.playerSelection)) {
            if ("Paper".equals(opponent_service.playerSelection)) {
                result = player_service.player_nick;
            } else if ("Scissors".equals(opponent_service.playerSelection)) {
                result = "Tie";
            } else if ("Rock".equals(opponent_service.playerSelection)) {
                result = opponent_service.player_nick;
            }
        } else if ("Rock".equals(player_service.playerSelection)) {
            if ("Paper".equals(opponent_service.playerSelection)) {
                result = opponent_service.player_nick;
            } else if ("Scissors".equals(opponent_service.playerSelection)) {
                result = player_service.player_nick;
            } else if ("Rock".equals(opponent_service.playerSelection)) {
                result = "Tie";
            }
        }
	if(!"Tie".equals(result))
        Appendta("The winner is " + result);
	else 
	Appendta("There's a tie");
        return result;
    }

    //Selecting Random choice by AI
    private static String drawServerSelection() {
        Random random = new Random();
        int number = random.nextInt(3);//find a random integer between 0-2
        String score = null;
        if (number == 0) {
            score = "Paper";
        } else if (number == 1) {
            score = "Scissors";
        } else if (number == 2) {
            score = "Rock";
        }
        Appendta("Server chose: " + score);
        return score;
    }

    class PlayerService implements Runnable {
        private Thread thread;
        private String player_nick;
        private String opponent_nick;
        private String playerSelection;// a field storing the gesture selected by the player
        private String playerSelection2;
        private String playerSelection3;
        private BufferedReader br;//a field to support loading an input stream from a client
        private PrintWriter pw;//field to support sending the output stream to the client

        private PlayerService(Socket client) {
            try {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException ex) {
                Appendta("Problem with creating the BufferedReader class object for loading stream in: " + ex.getMessage());
            }
            try {
                pw = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException ex) {
                Appendta("Problem with creating PrintWriter object for sending output stream: " + ex.getMessage());
            }
            try {
                player_nick = br.readLine();// load information about the player's nickname
            } catch (IOException ex) {
                Appendta("Problem loading player nickname: " + ex.getMessage());
            }
            player.add(player_nick);//adding the player's nickname to the list of player's nicknames
            try {
                opponent_nick = br.readLine();//load information about the opponent's nickname
            } catch (IOException ex) {
                Appendta("Problem loading player nickname: " + ex.getMessage());
            }
            playerSelection = null;
            playerSelection2 = null;
            playerSelection3 = null;
            thread = new Thread(this, player_nick);//create a new thread
            thread.start();//start a new thread. continues in the run () method
        }
        
        

        //Sending Message to Client
        private void sendMessage(String txt) {
            pw.println(txt);
        }

        //Returning Opponent Name for each thread, for the sake of duel
        private String playerReturns() {
            return player_nick;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    playerSelection = br.readLine();//Read player selection
                } catch (IOException ex) {
                    Appendta("Problem loading the player's selection (gesture): " + ex.getMessage());
                }
                if ("Quit".equals(playerSelection)) {//the player has chosen to end
                    try {
                        playerSelection2 = br.readLine();//Read player selection
                         appendtofile(fileName, 
                                       playerSelection2);
                    } catch (IOException ex) {
                    Appendta("Problem appending into the file" + ex.getMessage());
                    }
                    client.remove(this);//remove the player from the client list
                    player.remove(player_nick);//remove the player's nickname from the player's nicklist
                    Appendta("Removed: " + player_nick);
                    Appendta("Available players: ");
                    for (String s : player) {//list the logged in players
                        Appendta(s);
                    }
                    break;
                } else if ("AI".equals(opponent_nick)) {//the player decided to play with AI
                    duel(player_nick);//start the duel
                } else {//the player decided to play with another player
                    Appendta("Read player choice: " + player_nick);
                    for (PlayerService c : client) {
                        if (c.playerReturns().equals(opponent_nick) && c.playerSelection != null) {//the other player has already responded, you can go to comparison
                            Appendta("The match has started");
                            duel(player_nick, opponent_nick);//compare the choices of both players (start the duel)
                        } 
                    }
                }
            }
        }
    }
}
