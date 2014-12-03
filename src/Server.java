import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

//adapted from http://zgking.com:8080/home/donghui/teaching/multithread_socket/
//by Donghui Zhang 
//Accessed and Modified by Andy Yao Dec 01,2014

public class Server {
	public static Map<String, String> idKey;
	
    public static void main(String args[]) {
		int port = 16000;
		idKey = new HashMap<String, String>();
		idKey.put("73BB35BEBBB23", "User1");
		idKey.put("35A9BDACDFEAB", "User2");
		idKey.put("A772597247FAA", "User3");
		Server server = new Server( port );
		server.startServer();
    }

    // declare a server socket and a client socket for the server;
    // declare the number of connections

    ServerSocket echoServer = null;
    Socket clientSocket = null;
    int numConnections = 0;
    int port;
	
    public Server( int port ) {
    	this.port = port;
    }

    public void stopServer() {
		System.out.println( "Server cleaning up." );
		System.exit(0);
    }

    public void startServer() {
	// Try to open a server socket on the given port
	// Note that we can't choose a port less than 1024 if we are not
	// privileged users (root)
	
        try {
	    echoServer = new ServerSocket(port);
        }
        catch (IOException e) {
	    System.out.println(e);
        }   
	
		System.out.println( "Server is started and is waiting for connections." );

	// Whenever a connection is received, start a new thread to process the connection
	// and wait for the next connection.
	
		while ( true ) {
		    try {
			clientSocket = echoServer.accept();
			numConnections ++;
			ServerConnection con = new ServerConnection(clientSocket, numConnections, this, idKey);
			new Thread(con).start();
		    }   
		    catch (IOException e) {
			System.out.println(e);
		    }
		}
    }
}

class ServerConnection implements Runnable {
    BufferedReader is;
    PrintStream os;
    Socket clientSocket;
    int id;
    Server server;
    Map<String, String> idKey;

    public ServerConnection(Socket clientSocket, int id, Server server, Map<String, String> idKey) {
		this.clientSocket = clientSocket;
		this.id = id;
		this.server = server;
		this.idKey = idKey;
		System.out.println( "Connection " + id + " established with: " + clientSocket );
		try {
		    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    os = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
		    System.out.println(e);
		}
    }

    public void run() {
        String line;
        boolean auth = false;
        long[] k = new long[4];
		long[] v = new long[2];
		TEA t = new TEA();
		try {
			while(!auth) {
				line = is.readLine();
				//System.out.println( "Received " + line + " from Connection " + id + "." );
				for (Entry<String, String> entry : idKey.entrySet()) {
					k[0] = Long.parseLong(entry.getKey().substring(0, 3), 36);
					k[1] = Long.parseLong(entry.getKey().substring(3, 6), 36);
					k[2] = Long.parseLong(entry.getKey().substring(6, 9), 36);
					k[3] = Long.parseLong(entry.getKey().substring(9), 36);
					v = t.getDecrypted(line, k);
					if ((Long.toString(v[0], 36) + Long.toString(v[1], 36)).equalsIgnoreCase(entry.getValue())) {
						v[0] = Long.parseLong("ac", 36);
						v[1] = Long.parseLong("kn", 36);
						//System.out.println(k[0] +" " + k[1]);
						os.println(t.getEncrypted(v, k));
						auth = true;
						break;
					}
				}
				if (!auth) {
					v[0] = Long.parseLong("den", 36);
					v[1] = Long.parseLong("ied", 36);
					os.println(t.getEncrypted(v, k));
				}
			}
			
			while (true) {
				line = is.readLine();
				//System.out.println( "Received " + line + " from Connection " + id + "." );
				v = t.getDecrypted(line, k);
				//System.out.println((Long.toString(v[0], 36) + Long.toString(v[1], 36)));
				//System.out.println(k[0] +" " + k[1]);
				String res = Long.toString(v[0], 36) + Long.toString(v[1], 36);
				if (res.equalsIgnoreCase("99")) {
					break;
				}
				
				res = res.replaceAll("dot", "\\.");
				FileEncrypt file = new FileEncrypt(res);
				if (!file.exists){
					v[0] = Long.parseLong("notf", 36);
					v[1] = Long.parseLong("ound", 36);
					os.println(t.getEncrypted(v, k));
				} else {
					v[0] = Long.parseLong("filef", 36);
					v[1] = Long.parseLong("ound", 36);
					os.println(t.getEncrypted(v, k));
					for (String s: file.getLines()) {
						s = s.replaceAll("\\s", "space");
						v[0] = Long.parseLong(s.substring(0, s.length()/2), 36);
						v[1] = Long.parseLong(s.substring(s.length()/2), 36);
						os.println(t.getEncrypted(v, k));
					}
					v[0] = Long.parseLong("eo", 36);
					v[1] = Long.parseLong("f", 36);
					os.println(t.getEncrypted(v, k));
				}
				
			}
	
		    System.out.println( "Connection " + id + " closed." );
	            is.close();
	            os.close();
	            clientSocket.close();
		} catch (IOException e) {
		    System.out.println(e);
		}
    }
}
