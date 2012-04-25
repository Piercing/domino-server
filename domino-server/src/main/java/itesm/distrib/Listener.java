package itesm.distrib;

import java.io.*;
import java.net.*;

class Listener implements Runnable {

    BufferedReader b;
    Jugador j;

    public BufferedReader getBuffer() {
        return b;
    }

    public Jugador getJugador() {
        return j;
    }

    public void setBuffer(BufferedReader value) {
        b = value;
    }

    public void setJugador(Jugador value) {
        j = value;
    }

    public Listener(Jugador jugador, BufferedReader buffer) {
        setJugador(jugador);
        setBuffer(buffer);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String comando = b.readLine();
                j.interpretarComando(comando);
            } catch (Exception ex) {
                System.out.println("Error comando " + ex.getMessage());
            }
        }
    }
}
