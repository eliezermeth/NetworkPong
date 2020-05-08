// Fig. 27.7: Client.java
// Client portion of a stream-socket connection between client and server.
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends SocketParent
{
    private String chatServer; // host server for this application

    // initialize chatServer and set up GUI
    public Client( String host )
    {
        super( "Client" );
    }

    public void runClient()
    {
        try
        {
            connectToServer();
            getStreams();
            processConnection();
        }
        catch ( EOFException eofException )
        {
            displayMessage( "\nClient terminated connection" );
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
        finally
        {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException
    {
        displayMessage( "Attempting connection\n" );

        // create Socket to make connection to server
        connection = new Socket( InetAddress.getByName( chatServer ), 12345 );

        displayMessage( "Connected to: " +
                connection.getInetAddress().getHostName() );
    }

    private void closeConnection()
    {
        super.closeConnection("Closing connection");
    }
}
