// Import statements
import java.io.Serializable;

/**
 * Prj 5- These classes work together to serve as a chat server
 * This class makes the cm object that the clients will send to the server 
 *
 * @author Kartik Uppalapati, L05
 * @version 4,27,2020
 */


final class ChatMessage implements Serializable 
{
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
    
    // Create fields
    private String message;
    private int type;
    
    
    // Create constructor
    public ChatMessage(String message, int type)
    {
    	this.message = message;
    	this.type = type;
    }
   
    
    // Create methods
    public String getMessage()
    {
    	return this.message;
    }
    public int getType()
    {
    	return this.type;
    }
 
    
    
}
