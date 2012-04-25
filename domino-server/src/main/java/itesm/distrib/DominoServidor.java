package itesm.distrib;

import java.net.*;
import java.io.*;

public class DominoServidor {

    static boolean juegoIniciado;

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                int puerto = Integer.parseInt(args[0]);
                int numJugadores = Integer.parseInt(args[1]);
                ServerSocket server;
                try {
                    int i = 0;
                    Host host = new Host();
                    host.setNumeroJugadores(numJugadores);
                    server = new ServerSocket(puerto);
                    while (true) {
                        if (!host.comenzado) {
                            Socket socket = server.accept();
                            Jugador nuevo = new Jugador(host, socket);
                            host.agregarJugador(nuevo);
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Error de socket: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                System.out.println("Parámetro incorrecto <puerto>; debe ser un número de puerto.");
            }
        } else {
            System.out.println("Uso: DominoServidor <puerto> jugadores");
        }
    }
}
