package com.buscaminas.interfazJuego;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JuegoIU {
	private final int BASE = 490;
	private final int ALTURA = 380;
	private JFrame ventana;
	private JPanel contenedor;
	private JPanel panelCampo;
	private String[] niveles = {"Facil", "Medio", "Dificil"};
	private Color colorBarra = new Color(0, 100, 0);
	private Color colorCampo = new Color(0, 128, 0);
	private ControlJuego control;

	public JuegoIU() {
		ventana = new JFrame("Buscaminas");
		ventana.setSize(BASE, ALTURA);
		ventana.setLocationRelativeTo(null);
		contenedor = (JPanel) ventana.getContentPane();
		control = new ControlJuego();
		hacerBarraMenu();
		hacerAreaJuego();
		ventana.setResizable(false);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setVisible(true);
		seleccionarNivel();
	}

	private void seleccionarNivel() {
		int indice = JOptionPane.showOptionDialog(ventana, "Selecciona el nivel", "Nivel", JOptionPane.CLOSED_OPTION, JOptionPane.QUESTION_MESSAGE, null, niveles, niveles[0]);
		if (indice != -1) {
			control.definirNivel(niveles[indice]);
			panelCampo.revalidate();
		}
	}

	private void hacerAreaJuego() {
		hacerBarraJuego();

		panelCampo = new JPanel();
		
		panelCampo.setBackground(colorCampo);
		panelCampo.setLayout(new FlowLayout());
		panelCampo.add(control);
		contenedor.add(panelCampo, BorderLayout.CENTER);
	}

	private void hacerBarraJuego() {
		JPanel panelBotones;
		JButton btnReiniciar;
		Image imgReiniciar;
		ImageIcon iconoReiniciar;
		
		contenedor.setLayout(new BorderLayout());
		
		panelBotones = new JPanel(new FlowLayout());
		panelBotones.setBackground(colorBarra);
		contenedor.add(panelBotones, BorderLayout.NORTH);
		
		imgReiniciar = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/images/imgReinicio.png"));
		imgReiniciar = imgReiniciar.getScaledInstance(25, 25, Image.SCALE_DEFAULT);
		iconoReiniciar = new ImageIcon(imgReiniciar);
		btnReiniciar = new JButton(iconoReiniciar);
		btnReiniciar.setMargin(new Insets(0, 0, 0, 0));
		btnReiniciar.addActionListener(e -> control.reiniciar());
		panelBotones.add(btnReiniciar);
	}

	private void hacerBarraMenu() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem seleccionar, salir;

		menuBar = new JMenuBar();
		ventana.setJMenuBar(menuBar);
		
		menu = new JMenu("Opciones");
		menuBar.add(menu);

		seleccionar = new JMenuItem("Nuevo nivel");
		seleccionar.addActionListener(e -> seleccionarNivel());
		menu.add(seleccionar);

		salir = new JMenuItem("Salir");
		salir.addActionListener(e -> System.exit(0));
		menu.add(salir);
	}
}

