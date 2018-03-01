import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {

	public static final int sizeof_slowstart_packet = 16 * 1024; //Optimal Window Size for initial connections (1 Mbps - 100 Mbps): 16384 bytes
  	public static final int sizeof_datapacket = 5 * 1024 * 1024; //Data size to check bandwidth 

	public static void main(String[] args) throws Exception {

		InetAddress server_ip = InetAddress.getByName(args[0]);
		int server_port = Integer.parseInt(args[1]);

      	Socket clientSocket = new Socket(server_ip, server_port);
		System.out.println("Client connected to the server!");
		  	
      	InputStream inFromServer = clientSocket.getInputStream();
		OutputStream outToServer = clientSocket.getOutputStream();

		slowstart(inFromServer, outToServer);
	long downloadspeed = testDownloadSpeed(inFromServer, outToServer);	
	long uploadspeed = testUploadSpeed(inFromServer, outToServer);
				
		System.out.println("Summary:\n Download Speed is " + downloadspeed + " Mbps\n Upload Speed is " + uploadspeed + " Mbps\n");
      
		outToServer.close();
		inFromServer.close();
		clientSocket.close();				
	}


	public static void slowstart(InputStream input, OutputStream output) throws IOException {
		
      	byte[] slowstartpacket = new byte[sizeof_slowstart_packet];
		new Random().nextBytes(slowstartpacket);

		output.write(slowstartpacket);
		
      	//Read Packet recieved from Server
		int bytesRead, offset = 0;
		bytesRead = input.read(slowstartpacket, offset, slowstartpacket.length) ;
		while (bytesRead > 0) {
			offset = offset + bytesRead;
			bytesRead = input.read(slowstartpacket, offset, slowstartpacket.length - offset);
		}
	}


	public static long testDownloadSpeed(InputStream input, OutputStream output) throws IOException {
      
		int offset = 0;
      		byte[] downloadpacket = new byte[sizeof_datapacket];
		
      		long start = System.currentTimeMillis();
		int bytesRead = input.read(downloadpacket, offset,downloadpacket.length );
		long total_received = bytesRead;
		
      while (bytesRead > 0) {
			offset = offset + bytesRead;
			bytesRead = input.read(downloadpacket, offset, sizeof_datapacket - offset);
			total_received = total_received + bytesRead;
		}
		
      	long end = System.currentTimeMillis();		
	if ((end - start) == 0) {
			end = start + 1;
		}		
	System.out.println("Download time: " + (end - start));
      	long downloadspeed = ((total_received) *1000 * 8) / ((end - start) * 1024 * 1024);	
      	 
	return downloadspeed;

	}

	public static long testUploadSpeed(InputStream input, OutputStream output) throws IOException {
		
		byte[] uploadpacket = new byte[sizeof_datapacket];
		new Random().nextBytes(uploadpacket);
		
		long start = System.currentTimeMillis();
		output.write(uploadpacket);
		long end = System.currentTimeMillis();

		System.out.println("Upload time:" + (end - start));
		long uploadspeed = ((long)(uploadpacket.length) *1000* 8) / ((end - start) * 1024 * 1024);
      	 
		return uploadspeed;
	}

}
