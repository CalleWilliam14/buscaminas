package com.buscaminas.juego;

import java.util.ArrayList;
import java.util.Arrays;

public class Juego {
	private int numeroFilas;
	private int numeroColumnas;
	private int cantidadMinas;
	private int cantidadBanderas;
	private int totalBloquesAVisitar;
	private int contadorVisitados;
	private boolean juegoPerdido;
	private int[][] campo;
	private int[][] bloquesDescubiertos;
	private int[][] estados; // 0 <- sin visitar, 1 <- visitado, 2 <- con bandera, 3 <- bandera en posicion equivocada, -1 <- mina descubierta
	private ArrayList<Par> posicionMinas;
	private ArrayList<Par> posicionBanderas;
	
	public Juego() {
	    posicionMinas = new ArrayList<>();
	    posicionBanderas = new ArrayList<>();
	}

	public void ajustar(int numeroFilas, int numeroColumnas, int cantidadMinas) {
		this.numeroFilas = numeroFilas;
		this.numeroColumnas = numeroColumnas;
		this.cantidadMinas = cantidadMinas;
		cantidadBanderas = cantidadMinas;
		totalBloquesAVisitar = numeroFilas * numeroColumnas - cantidadMinas;
		contadorVisitados = 0;
		juegoPerdido = false;
		campo = new int[numeroFilas][numeroColumnas];
		bloquesDescubiertos = new int[numeroFilas][numeroColumnas];
		estados = new int[numeroFilas][numeroColumnas];
		posicionMinas.clear();
		posicionBanderas.clear();
		ubicarMinasEnCampo();
	}
	
	private void ubicarMinasEnCampo() {
	    int n, m, contador;
	    contador = cantidadMinas;
	    while (contador > 0) {
	        n = (int) (Math.random() * numeroFilas);
	        m = (int) (Math.random() * numeroColumnas);
	        if (campo[n][m] != -1) {
	            campo[n][m] = -1;
	            posicionMinas.add(new Par(n, m));
	            actualizarValoresAlrededorDeLaMina(n, m);
	            contador--;
	        }
	    }
	}
	
	private void actualizarValoresAlrededorDeLaMina(int n, int m) {
	    if (esPosicionValida(n, m - 1) && campo[n][m - 1] != -1) campo[n][m - 1]++;
	    if (esPosicionValida(n - 1, m - 1) && campo[n - 1][m - 1] != -1) campo[n - 1][m - 1]++;
	    if (esPosicionValida(n - 1, m) && campo[n - 1][m] != -1) campo[n - 1][m]++;
	    if (esPosicionValida(n - 1, m + 1) && campo[n - 1][m + 1] != -1) campo[n - 1][m + 1]++;
	    if (esPosicionValida(n, m + 1) && campo[n][m + 1] != -1) campo[n][m + 1]++;
	    if (esPosicionValida(n + 1, m + 1) && campo[n + 1][m + 1] != -1) campo[n + 1][m + 1]++;
	    if (esPosicionValida(n + 1, m) && campo[n + 1][m] != -1) campo[n + 1][m]++;
	    if (esPosicionValida(n + 1, m - 1) && campo[n + 1][m - 1] != -1) campo[n + 1][m - 1]++;
	}
	
	private boolean esPosicionValida(int n, int m) {
	    return n >= 0 && n < numeroFilas && m >= 0 && m < numeroColumnas;
	}
	
	public void reiniciar() {
	    cantidadBanderas = cantidadMinas;
	    contadorVisitados = 0;
	    juegoPerdido = false;
	    for (int i = 0; i < numeroFilas; i++) {
	        Arrays.fill(campo[i], 0);
	        Arrays.fill(bloquesDescubiertos[i], 0);
	        Arrays.fill(estados[i], 0);
	    }
	    posicionMinas.clear();
	    posicionBanderas.clear();
	    ubicarMinasEnCampo();
	}
	
	public boolean marcarPosicion(int n, int m) {
	    if (esPosicionValida(n, m) && estados[n][m] == 0 && cantidadBanderas > 0) {
	        estados[n][m] = 2;
	        cantidadBanderas--;
	        posicionBanderas.add(new Par(n, m));
	        return true;
	    }
	    return false;
	}
	
	public boolean desmarcarPosicion(int n, int m) {
	    if (esPosicionValida(n, m) && estados[n][m] == 2 && cantidadBanderas < cantidadMinas) {
	        estados[n][m] = 0;
	        cantidadBanderas++;
	        posicionBanderas.remove(new Par(n, m));
	        return true;
	    }
	    return false;
	}
	
	public boolean jugar(int n, int m) {
		boolean jugado;
		if (hayBandera(n, m)) jugado = false;
		else {
			if (estaVisitado(n, m) && contarBanderasAlrededor(n, m) == campo[n][m]) {
				jugado = false;
				if (noEstaVisitado(n, m - 1)) jugado |= jugar(n, m - 1);
				if (noEstaVisitado(n - 1, m - 1)) jugado |= jugar(n - 1, m - 1);
				if (noEstaVisitado(n - 1, m)) jugado |= jugar(n - 1, m);
				if (noEstaVisitado(n - 1, m + 1)) jugado |= jugar(n - 1, m + 1);
				if (noEstaVisitado(n, m + 1)) jugado |= jugar(n, m + 1);
				if (noEstaVisitado(n + 1, m + 1)) jugado |= jugar(n + 1, m + 1);
				if (noEstaVisitado(n + 1, m)) jugado |= jugar(n + 1, m);
				if (noEstaVisitado(n + 1, m - 1)) jugado |= jugar(n + 1, m - 1);
			} else {
				if (hayMina(n, m)) {
					descubrirMinas();
					descubrirBanderasEnPosicionesEquivocadas();
					juegoPerdido = true;
				} else descubrir(n, m);
				jugado = true;
			}
		}
		return jugado;
	}
	
	private void descubrir(int n, int m) {
		if (noEstaVisitado(n, m)) {
			if (campo[n][m] > 0) {
				estados[n][m] = 1;
				bloquesDescubiertos[n][m] = campo[n][m];
				contadorVisitados++;
			} else if (campo[n][m] == 0) { // verificar luego
				estados[n][m] = 1;
				bloquesDescubiertos[n][m] = campo[n][m];
				contadorVisitados++;
				descubrir(n, m - 1);
				descubrir(n - 1, m - 1);
				descubrir(n - 1, m);
				descubrir(n - 1, m + 1);
				descubrir(n, m + 1);
				descubrir(n + 1, m + 1);
				descubrir(n + 1, m);
				descubrir(n + 1, m - 1);
			}
		}
	}
	
	private void descubrirMinas() {
		int n, m;
		for (Par par : posicionMinas) {
			n = par.getX();
			m = par.getY();
			if (estados[n][m] == 0) estados[n][m] = -1;
		}
	}
	
	private void descubrirBanderasEnPosicionesEquivocadas() {
		int n, m;
		for (Par par : posicionBanderas) {
			n = par.getX();
			m = par.getY();
			if (campo[n][m] >= 0) estados[n][m] = 3;
		}
	}
	
	private int contarBanderasAlrededor(int n, int m) {
		int cantidadBanderas = 0;
		if (posicionBanderas.contains(new Par(n, m - 1))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n - 1, m - 1))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n - 1, m))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n - 1, m + 1))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n, m + 1))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n + 1, m + 1))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n + 1, m))) cantidadBanderas++;
		if (posicionBanderas.contains(new Par(n + 1, m - 1))) cantidadBanderas++;
		return cantidadBanderas;
	}
	
	public boolean hayBandera(int n, int m) {
		return esPosicionValida(n, m) && estados[n][m] == 2;
	}
	
	public boolean estaVisitado(int n, int m) {
		return esPosicionValida(n, m) && estados[n][m] == 1;
	}
	
	public boolean noEstaVisitado(int n, int m) {
		return esPosicionValida(n, m) && estados[n][m] == 0;
	}
	
	private boolean hayMina(int n, int m) {
		return esPosicionValida(n, m) && campo[n][m] == -1;
	}

	public int[][] getBloquesDescubiertos() {
		return bloquesDescubiertos;
	}

	public int[][] getEstados() {
		return estados;
	}
	
	public boolean juegoGanado() {
		return contadorVisitados == totalBloquesAVisitar;
	}
	
	public boolean juegoPerdido() {
		return juegoPerdido;
	}
	
	public boolean juegoTerminado() {
		return juegoGanado() || juegoPerdido();
	}
}
