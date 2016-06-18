package br.com.fiap.chat.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

public class Server implements Runnable {
	
	private static Logger log4j = Logger.getLogger(Server.class);
	
	private static final String CHAT_SERVER_MAX_CONNECTIONS = "chat.server.maxConnections";
	private static final String CHAT_SERVER_PORT = "chat.server.port";
	
	private ServerSocket serverSocket;
	private Socket socket;
	private Integer maxConnections;
	private Integer numConnections;
	private Integer port;
	private Vector<Connection> conVector;
	private boolean isConnected = false;
	
	private ScreenServer server;
	
	public Server(ScreenServer server) {
		try {
			FileInputStream inputStream = new FileInputStream(this.getClass().getResource("/chat.properties").getFile());		
			Properties props = new Properties();
			
			props.load(inputStream);
			
			port = Integer.parseInt((String) props.get(CHAT_SERVER_PORT));
			maxConnections = Integer.parseInt((String)props.getProperty(CHAT_SERVER_MAX_CONNECTIONS));
			numConnections = 0;
			conVector = new Vector<Connection>();
			this.server = server;
		} catch (FileNotFoundException e) {
			log4j.error(e);
		} catch (IOException e) {
			log4j.error(e);
		}
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			log4j.error(e1);
		}
		
		isConnected = true;
		while(true) {
			try {
				socket = serverSocket.accept();	
			} catch (IOException e) {
				continue;
			}
			if(!isConnected) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					break;
				}
				break;
			}
			handleConnection(socket);
		}
	}

	private void handleConnection(Socket socket) {
		synchronized (this) {
			while(numConnections == maxConnections) {
				try {
					wait();
				} catch (InterruptedException e) {
					log4j.error(e);
				}
			}
		}
		numConnections++;
		Connection conn = new Connection(socket);
		Thread thread = new Thread(conn);
		thread.start();
		conVector.add(conn);
		server.jtaLog.append("Host " + socket.getInetAddress() + " acabou de conectar\n");
		log4j.info("Host " + socket.getInetAddress() + " acabou de conectar");
	}
	
	public void sendToAllClients(String message) {
		for (Connection connection : conVector) {
			connection.sendMessage(message);
		}
	}
	
	public synchronized void connectionClosed(Connection connection, String user) {
		conVector.remove(connection);
		numConnections--;
		notify();
		server.jtaLog.append("Host " + socket.getInetAddress() + " desconectou\n");
		log4j.info("Host " + socket.getInetAddress() + " desconectou");
	}
	
	public synchronized void close() {
		isConnected = false;
		try {
			new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
		} catch (IOException e) {
			log4j.error(e);
		}
	}
	
	class Connection implements Runnable {
		private Socket commSocket;
		private OutputStreamWriter out;
		private BufferedReader in;
		
		public Connection(Socket s) {
			commSocket = s;
		}

		public void run() {
			OutputStream socketOutput = null;
			InputStream socketInput = null;
			try {
				socketOutput = commSocket.getOutputStream();
				out = new OutputStreamWriter(socketOutput);
				socketInput = commSocket.getInputStream();
				in = new BufferedReader(new InputStreamReader(socketInput));

				String inputMsg = null;
				while((inputMsg = in.readLine()) != null) {
					if(inputMsg.equals("CLOSE")) {
						synchronized (this) {
							this.sendMessage("CLOSE");
							try {
								wait();
							} catch (InterruptedException e) {
								log4j.error(e);
							}
						}
						break;
					}
					Server.this.sendToAllClients(inputMsg + "\n");
				}
			} catch (IOException e) {
				log4j.error(e);
			} finally {
				InetAddress address = this.commSocket.getInetAddress();
				Server.this.connectionClosed(this, address.toString());
			}
		}
		
		public void sendMessage(String message) {
			try {
				StringBuffer sbLog = new StringBuffer();
				sbLog.append(message);
				
				out.write(sbLog.toString());
				out.flush();
				if("CLOSE".equals(message)) {
					notify();
				}
			} catch (Exception e) {
				log4j.error(e);
			}
		}	
	
	}	

}
