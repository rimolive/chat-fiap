package br.com.fiap.chat.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import layout.TableLayout;

public class ScreenServer extends JFrame {
	
	private static Logger log4j = Logger.getLogger(Server.class);
	
	private static final long serialVersionUID = 1L;

	private final double[][] size = {
			{10, 120, 50, 160, 50, 160, TableLayout.FILL, 10},
			{10, 30, 40, 70, 160, 30, TableLayout.FILL, 10}
			};
	
	protected JPanel painelGeral;
	protected JPanel painelEdicao;
	
	protected JLabel lblTitulo;
	protected JLabel lblInformacao;
	
	protected JButton btnConectar;
	protected JButton btnDesconectar;
	protected JButton btnSair;
	protected JButton btnAbrirLog;
	
	protected JTextArea jtaLog;
	
	protected JScrollPane logScroll;
	
	private ScreenServerAction action;
	
	private Server server;
	
	private Thread threadServer;
	
	/**
	 * Construtor 
	 */
	public ScreenServer() {
		this.inicializacao();
		this.montaPainelGeral();
		this.adicionaListeners();
		
		this.setTitle(" Chat 9SCJ !!!");
		this.setSize(560, 400);
		this.setResizable(false);		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setVisible(true);		
	}
	
	/**
	 * Inicializa todas as varíaves da tela
	 */
	private void inicializacao() {
		painelGeral = new JPanel(new TableLayout(size));
		painelGeral.setBackground(new Color(250,240,230));
		
		painelEdicao = new JPanel(new BorderLayout());
		painelEdicao.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "  Log do Servidor:  "));
		painelEdicao.setOpaque(false);
		
		lblTitulo = new JLabel("Bem Vindo ao Chat 9SCJ ! ");
		lblTitulo.setForeground(new Color(25,25,112));
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
		
		lblInformacao = new JLabel("Para iniciar o chat clique no botão Iniciar!");
		lblInformacao.setForeground(new Color(25,25,112));
		
		btnConectar = new JButton("Iniciar");
		btnConectar.setPreferredSize(new Dimension(100, 25));
		
		btnDesconectar = new JButton("Finalizar");
		btnDesconectar.setPreferredSize(new Dimension(150, 25));
		btnDesconectar.setEnabled(false);
		
		btnSair = new JButton("Sair");
		btnSair.setPreferredSize(new Dimension(100, 25));	
		
		jtaLog = new JTextArea(10, 100);
		jtaLog.setEditable(false);
		jtaLog.setLineWrap(true);
		
		logScroll = new JScrollPane(jtaLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		btnAbrirLog = new JButton("Exibir Log");
		btnAbrirLog.setPreferredSize(new Dimension(100,25));
		
		action = new ScreenServerAction();
		server = new Server(this);
	}
	
	/**
	 * Monta tela 
	 */
	private void montaPainelGeral() {
		painelGeral.add(lblTitulo, "1,1,6,1,f,c");
		painelGeral.add(lblInformacao, "1,2,6,2,c,c");
		painelGeral.add(btnConectar, "1,3,c,c");
		painelGeral.add(btnDesconectar, "3,3,c,c");
		painelGeral.add(btnSair, "5,3,c,c");
		painelGeral.add(btnAbrirLog, "5,5,c,c");
		
		painelEdicao.add(logScroll);
		painelGeral.add(painelEdicao, "1,4,5,4");
		
		this.getContentPane().add(painelGeral);
	}
	
	/**
	 * Adiciona os listeners a cada botão. 
	 */
	private void adicionaListeners() {		
		btnConectar.addActionListener(action);
		btnDesconectar.addActionListener(action);
		btnSair.addActionListener(action);
		btnAbrirLog.addActionListener(action);
	}
	
	class ScreenServerAction implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			if(event.getSource().equals(btnConectar)) {
				btnConectar.setEnabled(false);
				btnDesconectar.setEnabled(true);
				
				threadServer = new Thread(server);
				threadServer.start();
				
				jtaLog.append("Servidor Conectado! \n");
				log4j.info("Servidor Conectado!");
				
			} else if(event.getSource().equals(btnDesconectar)) {
				btnDesconectar.setEnabled(false);
				btnConectar.setEnabled(true);
				server.close();
				jtaLog.append("Servidor Desconectado! \n");
				log4j.info("Servidor Desconectado!");
				
			} else if(event.getSource().equals(btnSair)) {
				log4j.info("Saiu do Servidor!");
				System.exit(0);
				
			} else if(event.getSource().equals(btnAbrirLog)) {
				jtaLog.append("Abrindo arquivo de log! \n");
				try {
					FileReader fileReader = new FileReader("C:\\LOG_CHAT.log");
					BufferedReader arquivoLido = new BufferedReader(fileReader);
					
					int numLinha = 1;
					String linha = arquivoLido.readLine();
					while (linha != null) {
						jtaLog.append(linha + "\n");
						numLinha ++;
						linha = arquivoLido.readLine();
					}
					log4j.info("Arquivo de log foi aberto!");
				} catch (IOException e) {
					log4j.error(e);
				}
			}
		}
	}
	
	/** 
	 * MAIN
	 * @param args
	 */
	public static void main(final String[] args) {
		new ScreenServer();
	}	
}

