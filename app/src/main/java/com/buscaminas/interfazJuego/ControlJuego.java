package com.buscaminas.interfazJuego;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.buscaminas.juego.Juego;

public class ControlJuego extends JPanel implements MouseHandler {
	private int numeroFilas;
	private int numeroColumnas;
	private int longitudBloque;
	private String nivelActual;
	private Font tipoLetra;
	private Color[] coloresNumeros = 
	{
		new Color(0, 0, 205), new Color(50, 205, 50), new Color(220, 20, 60), new Color(148, 0, 211),
		new Color(255, 165, 0), new Color(0, 255, 255), new Color(0, 0, 0), new Color(128, 128, 128)
		
	};
	private Color[] coloresBloques = 
	{
		new Color(173, 225, 47), new Color(154, 210, 50),
		new Color(255, 239, 213), new Color(255, 218, 185)
	};
	private Color[] coloresMinas = 
	{
		new Color(255, 215, 0), new Color(255, 140, 0), new Color(0, 255, 255), new Color(255, 0, 0),
		new Color(148, 0, 211), new Color(228, 130, 238), new Color(0, 0, 205), new Color(0, 100, 0)
	};
	private Juego juego;
	private ArrayList<Rectangle> rectangulos;
	
	public ControlJuego() {
	    addMouseListener(this);
	    nivelActual = "Sin nivel";
		juego = new Juego();
		rectangulos = new ArrayList<>();
	}

	public void definirNivel(String nivel) {
		if (nivelActual.equals(nivel)) reiniciar();
		else {
			switch (nivel) {
				case "Dificil":
					ajustar(25, 15, 18, 75);
					break;
				case "Medio":
					ajustar(18, 11, 25, 35);
					break;
				default:
					ajustar(11, 6, 34, 10);
			}
			nivelActual = nivel;
		}
	}

	public void ajustar(int numeroColumnas, int numeroFilas, int longitudBloque, int cantidadMinas) {
		this.numeroColumnas = numeroColumnas;
		this.numeroFilas = numeroFilas;
		this.longitudBloque = longitudBloque;
		tipoLetra = new Font(Font.MONOSPACED, Font.BOLD, longitudBloque);
		juego.ajustar(numeroFilas, numeroColumnas, cantidadMinas);
		rectangulos.clear();
		for (int i = 0; i < numeroFilas; i++)
		    for (int j = 0; j < numeroColumnas; j++)
		        rectangulos.add(new Rectangle(j * longitudBloque, i * longitudBloque, longitudBloque, longitudBloque));
	    setPreferredSize(new Dimension(numeroColumnas * longitudBloque, numeroFilas * longitudBloque));
	}
	
	public void reiniciar() {
		juego.reiniciar();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (!nivelActual.equals("Sin nivel")) pintarTablero(g);
	}
	
	private void pintarTablero(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		int estado, x, y, delta, gama, radio, xym;
		int[][] bloquesDescubiertos, estados;
		Color colorBloque, colorSimbolo;
		boolean haySimbolo, turno, sinCambioDeColor;
		String simbolo;
		
		turno = false;
		colorSimbolo = null;
		sinCambioDeColor = numeroColumnas % 2 == 0;
		simbolo = "";
		bloquesDescubiertos = juego.getBloquesDescubiertos();
		estados = juego.getEstados();
		g2d.setFont(tipoLetra);
		FontMetrics fm = g2d.getFontMetrics(tipoLetra);
		delta = (longitudBloque - fm.charWidth('0')) / 2;
		gama = fm.getAscent() / 2 + fm.getDescent();
		radio = (longitudBloque / 2);
		xym = radio / 2;
		
		for (int i = 0; i < numeroFilas; i++) {
			for (int j = 0; j < numeroColumnas; j++) {
				estado = estados[i][j];
				switch (estado) {
					case 1:
						if (turno) colorBloque = coloresBloques[2];
						else colorBloque = coloresBloques[3];
						break;
					case -1:
						colorBloque = coloresMinas[(int) (Math.random() * 8)];
						break;
					default:
						if (turno) colorBloque = coloresBloques[0];
						else colorBloque = coloresBloques[1];
				}
				switch (estado) {
					case 1:
						if (bloquesDescubiertos[i][j] > 0) {
							haySimbolo = true;
							colorSimbolo = coloresNumeros[bloquesDescubiertos[i][j] - 1];
							simbolo = String.valueOf(bloquesDescubiertos[i][j]);
						} else haySimbolo = false;
						break;
					case 2:
						haySimbolo = true;
						colorSimbolo = Color.RED;
						simbolo = "?";
						break;
					case 3:
						haySimbolo = true;
						colorSimbolo = Color.RED;
						simbolo = "X";
						break;
					default:
						haySimbolo = false;
				}
				x = j * longitudBloque;
				y = i * longitudBloque;
				g2d.setColor(colorBloque);
				g2d.fillRect(x, y, longitudBloque, longitudBloque);
				if (haySimbolo) {
				    g2d.setColor(colorSimbolo);
					g2d.drawString(simbolo, x + delta, y + gama);
				} else if (estado == -1) {
					g2d.setColor(Color.BLACK);
					g2d.fillOval(x + xym, y + xym, radio, radio);
				}
				turno = !turno;
			}
			if (sinCambioDeColor) turno = !turno;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    int posX, posY, contador;
	    boolean bandera;
	    if (!juego.juegoTerminado()) {
		    posX = e.getX();
		    posY = e.getY();
		    contador = 0;
		    bandera = false;
		    for (int i = 0; i < numeroFilas && !bandera; i++)
		        for (int j = 0; j < numeroColumnas && !bandera; j++)
		            if (rectangulos.get(contador).intersects(new Rectangle(posX, posY, 1, 1))) {
		                posX = i;
		                posY = j;
		                bandera = true;
		            } else contador++;
		    if (e.isMetaDown()) {
		    	if (juego.marcarPosicion(posX, posY)) repaint();
		    	else if (juego.desmarcarPosicion(posX, posY)) repaint();
		    } else if (juego.jugar(posX, posY)) {
                repaint();

                if (juego.juegoTerminado()) {
                    String mensaje;

                    if (juego.juegoGanado())
                        mensaje = "¡Has Ganado!";
                    else
                        mensaje = "¡Has Perdido!";

                    JOptionPane.showMessageDialog(this, mensaje);
                } 
            }
        }
    }
}
