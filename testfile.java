import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class testfile {

	public static void main(String[] args) {

		
	
	ArrayList<String> sentence = new ArrayList<>();
	
	sentence.add("hey");
	sentence.add("what up");
	sentence.add("damn");
	
	sentence.remove(sentence.get(1));
	
	System.out.println(sentence);
	
	
	
	int num1 = 10;
	int num2 = 20;
	
	int result = (num1 > num2) ? (num1 + num2) : (num1 - num2);
	
	System.out.println(result);
	
	
		
		
	}

}
