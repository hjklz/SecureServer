import java.io.*;
import java.net.*;

//adapted from http://zgking.com:8080/home/donghui/teaching/multithread_socket/
//by Donghui Zhang 
//Accessed and Modified by Andy Yao Dec 01,2014

public class Client {
    public static void main(String[] args) {
	
	String hostname = "localhost";
	int port = 16000;

	// declaration section:
	// clientSocket: our client socket
	// os: output stream
	// is: input stream
	
        Socket clientSocket = null;  
        DataOutputStream os = null;
        BufferedReader is = null;
	
	// Initialization section:
	// Try to open a socket on the given port
	// Try to open input and output streams
	
        try {
            clientSocket = new Socket(hostname, port);
            os = new DataOutputStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + hostname);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + hostname);
        }
	
	// If everything has been initialized then we want to write some data
	// to the socket we have opened a connection to on the given port
	
	if (clientSocket == null || os == null || is == null) {
	    System.err.println( "Something is wrong. One variable is null." );
	    return;
	}

	try {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String keyboardInput, responseLine, res;
		long v[] = new long[2];
		long k[] = new long[4];
		TEA t = new TEA();
		while (true) {
			System.out.print( "Enter your user ID: " );
			keyboardInput = br.readLine();
			v[0] = Long.parseLong(keyboardInput.substring(0, keyboardInput.length()/2), 36);
			v[1] = Long.parseLong(keyboardInput.substring(keyboardInput.length()/2), 36);
			
			while (true) {
				System.out.print( "Enter your 128 bit encryption key in Hex(XXXXXXXXXXXXX): ");
				keyboardInput = br.readLine();
				
				if (keyboardInput.length() != 13) {
					System.out.println("Invalid key length!");
				} else {
					k[0] = Long.parseLong(keyboardInput.substring(0, 3), 36);
					k[1] = Long.parseLong(keyboardInput.substring(3, 6), 36);
					k[2] = Long.parseLong(keyboardInput.substring(6, 9), 36);
					k[3] = Long.parseLong(keyboardInput.substring(9), 36);
					break;
				}
			}
			
			os.writeBytes(t.getEncrypted(v, k) + "\n" );
			
			responseLine = is.readLine();
			//System.out.println(responseLine);

			v = t.getDecrypted(responseLine, k);

			//System.out.println(Long.toString(v[0], 36) + Long.toString(v[1], 36));
			
			if ((Long.toString(v[0], 36) + Long.toString(v[1], 36)).equalsIgnoreCase("ackn")) {
				System.out.println("Authentication successsful!");
				break;
			} else {
				System.out.println("Invalid User ID / Key!");
			}
		}
		
	    while (true) {
			System.out.print("Enter filename (99 to quit): " );
			keyboardInput = br.readLine();
			keyboardInput = keyboardInput.replaceAll("\\.", "dot");
			v[0] = Long.parseLong(keyboardInput.substring(0, keyboardInput.length()/2), 36);
			v[1] = Long.parseLong(keyboardInput.substring(keyboardInput.length()/2), 36);
			
			if (keyboardInput.equalsIgnoreCase("99")) {
				os.writeBytes(t.getEncrypted(v, k) + "\n" );
				break;
			}
			
			os.writeBytes(t.getEncrypted(v, k) + "\n" );
		
			responseLine = is.readLine();
			v = t.getDecrypted(responseLine, k);
			res = Long.toString(v[0], 36) + Long.toString(v[1], 36);
			
			if (res.equalsIgnoreCase("notfound")){
				System.out.println("File not found!");
			} else {
				System.out.println("File found!");
				responseLine = is.readLine();
				v = t.getDecrypted(responseLine, k);
				res = Long.toString(v[0], 36) + Long.toString(v[1], 36);
				while(!res.equalsIgnoreCase("eof")) {
					res = res.replaceAll("space", " ");
					System.out.println(res);
					responseLine = is.readLine();
					v = t.getDecrypted(responseLine, k);
					res = Long.toString(v[0], 36) + Long.toString(v[1], 36);
				}
			}
	    }
	    
	    // clean up:
	    // close the output stream
	    // close the input stream
	    // close the socket
	    
	    os.close();
	    is.close();
	    clientSocket.close();   
	} catch (UnknownHostException e) {
	    System.err.println("Trying to connect to unknown host: " + e);
	} catch (IOException e) {
	    System.err.println("IOException:  " + e);
	}
    }           
}
