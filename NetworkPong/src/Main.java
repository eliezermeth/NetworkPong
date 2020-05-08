import javax.swing.JFrame;

public class Main
{
   public static void main (String args[])
   {
       // Server
       Server server = new Server(); // create server
       server.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
       server.runServer(); // run server application

       // Client
       Client client; // declare client application
       // if no command line args
       if ( args.length == 0 )
           client = new Client( "localhost" ); // connect to localhost
       else
           client = new Client( args[ 0 ] ); // use args to connect

       client.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
       client.runClient(); // run client application
   }
}
