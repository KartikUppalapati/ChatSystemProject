import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Prj 5- These classes work together to serve as a chat server
 * This is the client class that the user uses
 *
 * @author Kartik Uppalapati, L05
 * @version 4,27,2020
 */

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        // Create your input and output streams
    	// After starting, send the clients username to the server.
        try 
        {
        	socket = new Socket(server, port);
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sOutput.writeObject(username);
        } 
        catch (IOException e) 
        {
            System.out.println("Can't Connect!");
            return false;
        }

        // Check that username is not same
        try 
        {
			String msg = (String) sInput.readObject();
			if (msg.contentEquals("Same Username"))
			{
				throw new Exception();
			}
			else
			{
				System.out.println(msg);
				String bannedwords = (String) sInput.readObject();
				System.out.println("Please don't use these words:");
				System.out.println(bannedwords);
			}
		} 
        catch (Throwable e) 
        {
        	System.out.println("Username already being used!");
        	System.out.println("Try Again!");
        	return false;
		}
        
        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start(); 
        
        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) 
    {
        try 
        {
            sOutput.writeObject(msg);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // Get proper arguments and override defaults
    	Scanner scan = new Scanner(System.in);
    	String tempusername = "";
    	String portnum = "";
    	String serveraddy = "";
    	switch (args.length)
    	{
    	case 0: tempusername = "Anonymous";
    			portnum = "1500";
    			serveraddy = "localhost";
    			break;
    	case 1: tempusername = args[0];
				portnum = "1500";
				serveraddy = "localhost";
				break;
    	case 2: tempusername = args[0];
				portnum = args[1];
				serveraddy = "localhost";
				break;
    	case 3: tempusername = args[0];
				portnum = args[1];
				serveraddy = args[2];
				break;
    		
    	}
    	
        // Create your client and start it
        ChatClient client = new ChatClient(serveraddy, Integer.valueOf(portnum), tempusername);
        if (client.start() == false)
        {
        	return;
        }

        // Ask client for messages to send
        //TimeUnit.SECONDS.sleep(1);
        System.out.println("Type messages to send then press Enter:");
        while (true)
        {
        	String msg = scan.nextLine();
        	// If client logs out
        	if (msg.toLowerCase().contentEquals("/logout"))
        	{
        		client.sendMessage(new ChatMessage(msg, 1));
        		client.sInput.close();
        		client.sOutput.close();
        		client.socket.close();
        		break;
        	}
        	// If client wants to see client list
        	else if (msg.contentEquals("/list"))
        	{
        		System.out.println("Connected Clients:");
        		client.sendMessage(new ChatMessage(msg, 0));
        	}
        	// If client wants to slide in the dms
        	else if (msg.contains("/msg"))
        	{
        		client.sendMessage(new ChatMessage(msg, 2));
        	}
        	// Default
        	else
        	{
        		client.sendMessage(new ChatMessage(msg, 0));
        	}
        }
        
       
    }


   /**
    * This is a private class inside of the ChatClient
    * It will be responsible for listening for messages from the ChatServer.
    * ie: When other clients send messages, the server will relay it to the client.
    *
    * @author your name and section
    * @version date
    */
    private final class ListenFromServer implements Runnable {
        public void run() 
        {
        	while (true)
        	{
        		try 
                {
            		String msg = (String) sInput.readObject();
            		System.out.print(msg);
            	}
                catch (Throwable e)
                {
                    System.out.println("You have logged out!");
                    break;
                }
        	}
        }
    
    
    
    
    
    
    
    
    
    
    
    
    }
}
