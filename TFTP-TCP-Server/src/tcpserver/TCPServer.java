package tcpserver;

import java.net.*;
import java.io.*; // imports all classes from the java.io package
import java.util.Arrays;
import java.util.Scanner;

public class TCPServer 
{
    protected DatagramSocket socket = null;
    private int dataSize;//TID: B
    private final int portNumber ;
    private short rrqOpp; 
    private short wrqOpp; 
    private short dataOpp;
    private short errorOpp;
    private String mode;
    private String filename;
    private Scanner arguments;
    private String input;
    private DatagramPacket packet;
    
    public TCPServer()
    {
        this.portNumber = 10000;
        this.arguments = new Scanner(System.in); // used to read inputs from the user
    }

    public void sendAck() throws IOException
    {
        short blockNumber = 1; 
                                                                                               int len = dataSize;
        byte[] buf = new byte[dataSize];
        
        socket = new DatagramSocket(portNumber);

        InetAddress address = InetAddress.getByName("127.0.0.1");

        packet = new DatagramPacket(buf, blockNumber); //creates an acknowledgement packet with a block numbeber 
        packet.setAddress(address);
        packet.setPort(9000);

        socket.send(packet);
        socket.close();
        
    }
    
    public void sendData() throws IOException
    {
        short blockNumber = 1;
        int len = blockNumber;
        byte[] buf = new byte[dataSize];

        socket = new DatagramSocket(portNumber);

        InetAddress address = InetAddress.getByName("127.0.0.1");

        packet = new DatagramPacket(buf, len); //creates a data packet that is sent to host B 
        packet.setAddress(address);
        packet.setPort(9000);

        socket.send(packet);
        System.out.println("Data : " + Arrays.toString(packet.getData()) + " has been sent. ");
        socket.close();
    }    
    
    public void readRRQ(String filename) throws IOException
    {
        System.out.println("\nRead request in process....");
        int len = dataSize;
        byte[] buf = new byte[dataSize];

        socket = new DatagramSocket(portNumber);

        InetAddress address = InetAddress.getByName("127.0.0.1");

        packet = new DatagramPacket(buf, len);
        packet.setAddress(address);
        packet.setPort(9000);

        socket.send(packet);
        socket.receive(packet);
        socket.setSoTimeout(10);
        socket.close(); 
        sendAck();
        
    }

    public void writeWRQ(String filename) throws IOException
    {
        System.out.println("\nWrite request in process....");

        int len = dataSize;
        byte[] buf = new byte[dataSize];

        socket = new DatagramSocket(4000);

        InetAddress address = InetAddress.getByName("127.0.0.1");

        packet = new DatagramPacket(buf, len);
        packet.setAddress(address);
        packet.setPort(9000);

        socket.send(packet);
        socket.receive(packet);
        socket.setSoTimeout(10);
        sendData();
        socket.close();  
    }

    public void starts() throws IOException
    {
        System.out.println("Press 1 to Read a file/Press 2 to Write a file: ");
        input = arguments.next();
        userInput();
    }
    
    public static void main(String[] args) throws IOException {
        
        int portNumber = 10000;
        ServerSocket masterSocket;
        Socket slaveSocket;

        masterSocket = new ServerSocket(portNumber);
        

        System.out.println("Server Started...");
        
        while (true) 
        {
            slaveSocket = masterSocket.accept();
            
            System.out.println("Accepted TCP connection from: " + slaveSocket.getInetAddress() + ", " + slaveSocket.getPort() + "...");
            TCPServer tcps = new TCPServer();
            tcps.starts();            

        }
    }
        
    public void userInput() throws IOException
    {
        switch(input)
        {
            case "1": readRRQ(filename); break; // case where the user inputs a 1 into the commnad line 
            case "2": writeWRQ(filename); break; //listens to request and invokes writeWRQ method
            
            default: 
            { 
              System.out.println("Error: You can only enter 1 or 2.\n"); // informs users if they enter a number that is not 1 or 2
              starts(); 
            }
        }
        socket.close();
    }
}
