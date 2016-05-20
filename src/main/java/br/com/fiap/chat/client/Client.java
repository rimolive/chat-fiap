package br.com.fiap.chat.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Client {
	
	private static Logger log4j = Logger.getLogger(Client.class);
	
	private static final String CHAT_CLIENT_PORT = "chat.client.port";
	private static final String CHAT_CLIENT_SERVER_IP = "chat.client.serverIp";
	
	private Socket connection;
	private String serverIp;
	private int serverPort;
	private BufferedReader serverIn;
	protected PrintStream serverOut;
	private ScreenClient client;
	private RemoteReader reader;
	
	public Client(ScreenClient client) {
		try {
			FileInputStream inputStream = new FileInputStream("chat.properties");		
			Properties props = new Properties();
			
			props.load(inputStream);
			serverIp = (String) props.get(CHAT_CLIENT_SERVER_IP);
			serverPort = Integer.parseInt((String) props.get(CHAT_CLIENT_PORT));
			this.client = client;
		} catch (FileNotFoundException e) {
			log4j.error(e);
		} catch (IOException e) {
			log4j.error(e);
		}
	}

	public boolean makeConnection() {
		boolean status = false;
		try {
			setConnection(new Socket(serverIp, serverPort));
			
			serverIn = new BufferedReader(new InputStreamReader(getConnection().getInputStream()));
			serverOut = new PrintStream(getConnection().getOutputStream());
			
			reader = new RemoteReader();
			Thread t = new Thread(reader);
			t.start();
			
			status =  true;			
		} catch (Exception e) {
			log4j.error(e + " - Incapaz de conectar com o servidor!");
		}
		return status;
	}
	
	private class RemoteReader implements Runnable {

		private boolean keepListening = true;

		public void run() {
			while (keepListening == true) {
				try {
					String nextLine = serverIn.readLine();
					if("CLOSE".equals(nextLine)) {
						break;
					}
					client.jtaDialogo.append(nextLine + "\n");
				} catch (Exception e) {
					keepListening = false;
					log4j.error(e + " - Erro enquanto lia o servidor.");
				}
			}
		}
	
	}
	
	public void closeConnection() {
		try {
			reader.keepListening = false;
			serverOut.println("CLOSE");
			getConnection().close();
		} catch (IOException e) {
			log4j.error(e);
		}
	}

	private void setConnection(Socket connection) {
		this.connection = connection;
	}

	public Socket getConnection() {
		return connection;
	}

}
