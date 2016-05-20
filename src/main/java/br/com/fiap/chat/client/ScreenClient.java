package br.com.fiap.chat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import layout.TableLayout;

public class ScreenClient extends JFrame {

	private static final long serialVersionUID = 1L;

	private final double[][] size = {
			{20, 30, 50, 150, 110, 110, TableLayout.FILL}, 
			{10, 30, 250, 50, TableLayout.PREFERRED, 50, 50, 20, TableLayout.FILL, 10}
			};
	
	protected JPanel painelGeral;
	protected JPanel painelEdicao;
	
	protected JTextField jtfDestinatario;
	protected JTextField jtfMensagem;
	
	protected JLabel lblIP;
	protected JLabel lblNumIP;
	protected JLabel lblDestinatario;
	protected JLabel lblMensagem;
	
	protected JButton btnEnviar;
	protected JButton btnLimpar;
	protected JButton btnSair;
	
	protected JTextArea jtaDialogo;
	
	protected JScrollPane dialogoScroll;
	
	private ScreenClientAction action;
	
	private Client clientThread;
	
	/**
	 * Construtor 
	 */
	public ScreenClient() {		
		clientThread = new Client(this);
		//só cria a tela do cliente se o server já estiver sido executado
		if (clientThread.makeConnection()) {
			this.inicializacao();
			this.montaPainelGeral();
			this.adicionaListeners();
			
			this.setTitle(" Chat 9SCJ !!!");
			this.setSize(500, 500);
			this.setResizable(false);		
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setVisible(true);	
		}
	}
	
	/**
	 * Inicializa todas as varíaves da tela
	 */
	private void inicializacao() {
		painelGeral = new JPanel(new TableLayout(size));
		painelGeral.setBackground(new Color(250,240,230));
		
		painelEdicao = new JPanel(new BorderLayout());
		painelEdicao.setBorder(BorderFactory.createEtchedBorder());
		painelEdicao.setOpaque(false);
		
		lblIP = new JLabel("IP : ");
		lblIP.setForeground(new Color(25,25,112));
		
		//arrumar para receber o valor do ip do usuario
		lblNumIP = new JLabel(clientThread.getConnection().getLocalAddress().getHostAddress());
		lblNumIP.setForeground(new Color(25,25,112));
		
		jtaDialogo = new JTextArea();
		jtaDialogo.setEditable(false);
		jtaDialogo.setLineWrap(true);
		
		dialogoScroll = new JScrollPane(jtaDialogo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		lblDestinatario = new JLabel("Enviar Para: ");
		lblDestinatario.setForeground(new Color(25,25,112));
		
		jtfDestinatario = new JTextField();
		jtfDestinatario.setPreferredSize(new Dimension(100,20));
		jtfDestinatario.setBorder(BorderFactory.createLoweredBevelBorder());
		
		lblMensagem = new JLabel("Mensagem: ");
		lblMensagem.setForeground(new Color(25,25,112));
		
		jtfMensagem = new JTextField();
		jtfMensagem.setPreferredSize(new Dimension(100,20));
		jtfMensagem.setBorder(BorderFactory.createLoweredBevelBorder());
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setPreferredSize(new Dimension(100, 25));
		
		btnLimpar = new JButton("Limpar");
		btnLimpar.setPreferredSize(new Dimension(100, 25));
		
		btnSair = new JButton("Sair");
		btnSair.setPreferredSize(new Dimension(100, 25));	
		
		action = new ScreenClientAction();
		
	}
	
	/**
	 * Monta tela 
	 */
	private void montaPainelGeral() {
		painelGeral.add(lblIP, "1,1,f,c");
		painelGeral.add(lblNumIP, "2,1,3,1,f,c");
		painelGeral.add(lblDestinatario, "1,3,2,3,f,c");
		painelGeral.add(jtfDestinatario, "3,3,f,c");
		painelGeral.add(lblMensagem, "1,4,2,4,c,c");
		painelGeral.add(jtfMensagem, "3,4,5,4,f,c");
		painelGeral.add(btnEnviar, "4,5,c,c");
		painelGeral.add(btnLimpar, "5,5,r,c");
		painelGeral.add(btnSair, "5,6,r,c");
		
		painelEdicao.add(dialogoScroll);
		painelGeral.add(painelEdicao, "1,2,5,2,c,c");
		
		this.getContentPane().add(painelGeral);
	}
	
	/**
	 * Adiciona os listeners a cada botão. 
	 */
	private void adicionaListeners() {		
		btnEnviar.addActionListener(action);
		btnLimpar.addActionListener(action);
		btnSair.addActionListener(action);
	}
	
	class ScreenClientAction implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			if(event.getSource().equals(btnEnviar)) {
				String para = jtfDestinatario.getText();
				String linha = jtfMensagem.getText();
				StringBuffer msg = new StringBuffer();
				if(para != null && !para.equals("")) {
					msg.append("Para ");
					msg.append(para);
					msg.append(": ");
				}
				msg.append(linha);
				
				clientThread.serverOut.println(msg.toString());	
				clientThread.serverOut.flush();
				jtfMensagem.setText("");
				jtfDestinatario.setText("");
				
			} else if(event.getSource().equals(btnLimpar)) {
				jtaDialogo.setText("");
				
			} else if(event.getSource().equals(btnSair)) {
				clientThread.closeConnection();
				System.exit(0);
			}
		}
	}
	
	/** MAIN
	 * @param args
	 */
	public static void main(final String[] args) {
		new ScreenClient();
	}	
}