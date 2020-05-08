// Fig. 27.5: Server.java
// Server portion of a client/server stream-socket connection.
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends SocketParent
{
    private ServerSocket server; // server socket
    private int counter = 1; // counter of number of connections

    public Server()
    {
        super( "Server" );
    }

    public void runServer()
    {
        try
        {
            server = new ServerSocket( 12345, 100 ); // create ServerSocket

            while ( true )  // infinite loop
            {
                try
                {
                    waitForConnection();
                    getStreams();
                    processConnection();
                }
                catch ( EOFException eofException )
                {
                    displayMessage( "\nServer terminated connection" );
                }
                finally
                {
                    closeConnection();
                    ++counter;
                }
            }
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException
    {
        displayMessage( "Waiting for connection\n" );
        // blocking call (synchronous call.... not asynchronous (= on demand...where your code hets called back like an event handler)
        connection = server.accept();
        displayMessage( "Connection " + counter + " received from: " +
                connection.getInetAddress().getHostName() );
    }

    private void closeConnection()
    {
        super.closeConnection("Terminating connection");
    }
}
