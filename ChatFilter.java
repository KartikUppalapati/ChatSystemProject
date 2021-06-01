import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Prj 5- These classes work together to serve as a chat server
 * This is the filter class that filters out bad words based on text file given
 *
 * @author Kartik Uppalapati, L05
 * @version 4,27,2020
 */

public class ChatFilter {

	private ArrayList<String> badwords = new ArrayList<String>();
	
    public ChatFilter(String badWordsFileName) 
    {
    	File f = new File(badWordsFileName);
    	FileReader fr;
    	BufferedReader bfr;
		try 
		{
			fr = new FileReader(f);
			bfr = new BufferedReader(fr);
			while (true)
			{
				String line = bfr.readLine();
				if (line == null)
				{
					break;
				}
				badwords.add(line);
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
    }

    public String filter(String msg) 
    {
    	String replace = "";
    	String stars = "";
    	for (int i = 0; i < badwords.size(); i++)
    	{
    		if (msg.contains(badwords.get(i)))
    		{
    			for (int j = 0; j < badwords.get(i).length(); j++)
    			{
    				stars += "*";
    			}
    			replace = msg.replace(badwords.get(i), stars);
    			break;
    		} else
    		{
    			replace = msg;
    		}
    	}
        return replace;
    }
    
    public String showListOfBadWords()
    {
    	String badwordsstring = "";
    	for (int i = 0; i < badwords.size(); i++)
    	{
    		badwordsstring += badwords.get(i) + "\n";
    	}
    	
    	return badwordsstring;
    }
}
