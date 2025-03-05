package com.buscaminas;

import com.buscaminas.interfazJuego.JuegoIU;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JuegoIU();
			}
		});
    }
}
