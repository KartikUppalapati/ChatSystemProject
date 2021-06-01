import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Prj 5- These classes work together to serve as a chat server
 * This is the server class that the client class connects to
 *
 * @author Kartik Uppalapati, L05
 * @version 4,27,2020
 */

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final List<String> usernames = new ArrayList<>();
    private final int port;
    private final ChatFilter badwordsfilter;
    public static final Object obj = new Object();


    private ChatServer(int port, String badwordsfile) {
        this.port = port;
        badwordsfilter = new ChatFilter(badwordsfile);
        }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try 
        {
        	ServerSocket serverSocket = new ServerSocket(port);
        	System.out.println("Server Starting...");
        	System.out.println("Fitered Words:");
        	System.out.println(badwordsfilter.showListOfBadWords());
        	System.out.println("Server Running!");
        	while (true)
        	{
		        Socket socket = serverSocket.accept();
		        Runnable r = new ClientThread(socket, uniqueId++);
		        Thread t = new Thread(r);
		        clients.add((ClientThread) r);
		        t.start();
        	}
        } 
        catch (IOException e) 
        {
            System.out.println("Port is being Used!");
            System.out.println("Try a different One!");
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
    	// Initialize variables from args provided
    	String portnum = "";
    	String badwordsfile = "";
    	switch (args.length)
    	{
    	case 0: portnum = "1500";
    			badwordsfile = "badwords.txt";
    			break;
    	case 1: portnum = args[0];
    			badwordsfile = "badwords.txt";
    			break;
    	case 2: portnum = args[0];
    			badwordsfile = args[1];
				break;
    		
    	}
		ChatServer server = new ChatServer(Integer.parseInt(portnum), badwordsfile);
        server.start();
    }


   /**
    * This is a private class inside of the ChatServer
    * A new thread will be created to run this every time a new client connects.
    *
    * @author your name and section
    * @version date
    */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private ClientThread(Socket socket, int id) 
        {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() 
        {
            try 
            {            
                // Check that username not same
                if (usernames.contains(username))
                {
                	writeMessage("Same Username");
            		System.out.println("Someone tried to log on with existing username: " + username);
                    close();
            		remove(this.id);
                }
                // Add id to Anonymous usernames
                else if (username.contentEquals("Anonymous"))
                {
                	username += String.valueOf(this.id);
                }
                // Add normal usernames to array list
                else
                {
                	usernames.add(username);
                }
                
                // Welcome client and show banned words
                sOutput.writeObject("Welcome " + username + "\n");
                sOutput.writeObject(badwordsfilter.showListOfBadWords());
                System.out.println(username + " has logged on!");
                
                while (true)
                {
            		ChatMessage msg = (ChatMessage) sInput.readObject();
                	// If user enters /logout
                	if (msg.getType() == 1)
                	{
                		String tempuser = clients.get(this.id).username;
                		//usernames.remove(tempuser);
                		close();
                		remove(this.id);
                		System.out.println(tempuser + " has logged out!");
                		Broadcast(tempuser + " has logged out!");
                		break;
                	}
                	// Else if user enters /list
                	else if (msg.getMessage().contentEquals("/list"))
                	{
                		String returnclients = "";
                		for (int i = 0; i < clients.size(); i++)
                		{
                			if (clients.get(i).id != this.id)
                			{
                				returnclients += clients.get(i).username + "\n";
                			}
                		}
                		writeMessage(returnclients);
                	}
                	// Else if sliding in the dms
                	else if (msg.getType() == 2)
                	{
                		// Can't dm yourself
                		String[] list = msg.getMessage().split(" ", 3);
                		if (list[1].contentEquals(username))
                		{
                			writeMessage("Can't DM Yourself!");
                		}
                		else 
                		{
	                		for (int i = 0; i < clients.size(); i++)
	                		{
	                			// If username exists send filtered text
	                			if (clients.get(i).username.contentEquals(list[1]))
	                			{
	                				synchronized (obj)
	                				{
	                					String filtereddm = badwordsfilter.filter(list[2]);
		                				writeMessage("Direct Message Sent!");
		                				System.out.println(username + " direct messaged " + clients.get(i).username + ": " + filtereddm);
		                				clients.get(i).writeMessage(username + " DM: " + filtereddm);
		                				break;
	                				}
	                			}
	                			// Else username doesn't exist
	                			else if (clients.get(i).username.contentEquals(list[1]) == false && i == clients.size() - 1)
	                			{
	                				writeMessage("Client " + list[1] + " doesn't exist!");
	                			}
	                		}
                		}
                	}
                	// Else perform normal chat functions
                	else
                	{
                		String messagetosend = badwordsfilter.filter(msg.getMessage());
                		System.out.print(username + ": " + messagetosend + "\n");
                		Broadcast(username + ": " + messagetosend);
                	}
                }
            } 
            // If client force quits ctrl+c 
            catch (SocketException e)
            {
            	close();
        		try 
        		{
					remove(this.id);
				} 
        		catch (IOException e1) 
        		{
					e1.printStackTrace();
				}
        		System.out.println(username + " force quit!");
        		Broadcast(username + " force quit!");
            }
            catch (IOException | ClassNotFoundException ex)
            {
            	ex.printStackTrace();
            }
        }
    
    
       
        private void Broadcast(String message)
        {
        	synchronized (obj)
        	{
        		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
    		    Date date = new Date();
        		for (int i = 0; i < clients.size(); i++)
            	{
            		clients.get(i).writeMessage(message  + " " + formatter.format(date));
            	}
        	}
        }
    
    
        private boolean writeMessage(String message)
        {
        	try
        	{
        		if (this.socket.isClosed() == true)
            	{
            		return false;
            	}
            	else
            	{
            		sOutput.writeObject(message);
            		sOutput.writeObject("\n");
            		return true;
            	}
        	}
        	catch (IOException e)
        	{
        		
        	}
        	return true;
        }
    
    
        private void remove (int id) throws IOException
        {
        	synchronized (obj)
        	{
        		clients.remove(id);
        	}
        	
        }
        
        private void close()
        {
    		try 
    		{
    			clients.get(id).sInput.close();
        		clients.get(id).sOutput.close();
        		clients.get(id).socket.close();
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
        }
        
    }
}
