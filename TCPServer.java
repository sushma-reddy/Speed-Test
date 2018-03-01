import java.io.*;
import java.net.*;
import java.util.*;

class TCPServer {
  
  public static final int sizeof_slowstart_packet = 16 * 1024; //Optimal Window Size for initial connections (1 Mbps - 100 Mbps): 16384 bytes
  public static final int sizeof_datapacket = 5 * 1024 * 1024; //Data size to check bandwidth: 5MB 

  public static void slowstart(InputStream input, OutputStream output) throws IOException {
		byte[] slowstartpacket = new byte[sizeof_slowstart_packet];
		int bytesRead,offset = 0;
		bytesRead = input.read(slowstartpacket, offset, slowstartpacket.length);
		while (bytesRead > 0) {
			offset = offset + bytesRead;
			bytesRead = input.read(slowstartpacket, offset, slowstartpacket.length - offset);
		}
		output.write(slowstartpacket);
	}


  public static int testDownloadSpeed(InputStream input, OutputStream output) throws IOException {
	
		byte[] downloadpacket = new byte[sizeof_datapacket];
		new Random().nextBytes(downloadpacket);
      	
      		output.write(downloadpacket);
		System.out.println("Traffic sent to Client");
		return 0;
	}

  public static void testUploadSpeed(InputStream input, OutputStream output) throws IOException {

		int offset = 0;
        byte[] uploadpacket = new byte[sizeof_datapacket]; 
      	int bytesRead = input.read(uploadpacket, offset, uploadpacket.length);
		int total_received = bytesRead;
		while ( bytesRead > 0) {
			
			offset = offset + bytesRead;
			bytesRead = input.read(uploadpacket, offset, uploadpacket.length - offset);
			total_received = total_received + bytesRead;
		}
	}
	
  public static void main(String[] args) throws Exception {
      
		InetAddress server_ip = InetAddress.getByName(args[0]);
		int server_port = Integer.parseInt(args[1]);
      	
		ServerSocket welcomeSocket = new ServerSocket(server_port, 0, server_ip);
		System.out.println("Server listening on port:" + welcomeSocket.getLocalPort());

		while (true) {
			
          	Socket connectionSocket = welcomeSocket.accept();
			System.out.println("Connected to: " + connectionSocket.getRemoteSocketAddress());
			
          	OutputStream outToClient = connectionSocket.getOutputStream();
		InputStream inFromClient = connectionSocket.getInputStream();
		
		slowstart(inFromClient, outToClient);
          	System.out.println("TCP Slow Start done!!");
          
			System.out.println("Download Test Started!");
			testDownloadSpeed(inFromClient, outToClient);
			
			System.out.println("Upload Test Started!");          
			testUploadSpeed(inFromClient, outToClient);          
          
			outToClient.close();
			inFromClient.close();
			connectionSocket.close();
          
          	System.out.println("Speed test done!!");
		}
	}

}
