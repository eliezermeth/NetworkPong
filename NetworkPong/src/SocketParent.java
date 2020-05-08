import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 05.03.2020
 * Content extracted from duplicate Server and Client classes to make parent class
 */

public class SocketParent extends JFrame
{
    protected JTextField enterField; // inputs message from user
    protected JTextArea displayArea; // display information to user
    protected ObjectOutputStream output;
    protected ObjectInputStream input;
    protected Socket connection; // socket for server/client connection

    PongWindow gamePanel = null;

    SocketParent(String title)
    {
        setTitle(title);

        // TODO enter Pong code here
        gamePanel = new PongWindow();
        // Game code
        // add(gamePanel, BorderLayout.CENTER);

        add(gamePanel);

        setSize( 500, 400 );
        setVisible( false );

        gamePanel.requestFocus();
    }

    protected void processConnection() throws IOException
    {
        PongPacket packet = null;
        sendData(gamePanel.getPacket());

        do // process messages sent from server/client
        {
            try // read message and display it
            {
                packet = (PongPacket) input.readObject();
                gamePanel.setFromPacket(packet);
                displayMessage( "\n" + packet );
            }
            catch ( ClassNotFoundException classNotFoundException )
            {
                displayMessage( "\nUnknown object type received" );
                // TODO update ball location (if needed)
                // repaint if needed
            }

        } while ( packet != null && packet.delta.x != 0 );
    }

    // manipulates displayArea in the event-dispatch thread
    protected void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater( // call on the EDT - Event Dispatch Thread -- only thread safe to update GUI because Swing is not thread safe
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        displayArea.append( messageToDisplay );
                    }
                }
        );
    }

    // TODO connect this to pong
    protected void sendData( PongPacket packet )
    {
        try // send object to server/client
        {
            output.writeObject( packet );
            output.flush();
            displayMessage( "\n" + getTitle() + packet );
        }
        catch ( IOException ioException )
        {
            displayArea.append( "\nError writing object" );
        }
    }

    protected void getStreams() throws IOException
    {
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush();

        input = new ObjectInputStream( connection.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    }

    protected void closeConnection(String message)
    {
        displayMessage( "\n" + message + "\n" );
        setTextFieldEditable( false );

        try
        {
            output.close();
            input.close();
            connection.close();
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable( final boolean editable )
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // sets enterField's editability
                    {
                        enterField.setEditable( editable );
                    }
                }
        );
    }
}
