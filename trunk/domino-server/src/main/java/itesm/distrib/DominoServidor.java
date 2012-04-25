package itesm.distrib;

import java.io.IOException;
import java.net.*;

public class DominoServidor {

    static boolean juegoIniciado;
    private static int rondaActual;
    private Host host;
    private ServerSocket server;
    private int puerto;

    /**
     * Crea una nueva instancia del servidor para jugar dominó.
     * @param puerto Puerto de escucha
     * @param numeroJugadores Jugadores que pueden y son necesarios para iniciar
     * la partida.
     */
    public DominoServidor(int puerto, int numeroJugadores) {
        this.host = new Host(rondaActual > 0 ? rondaActual-- : 12);
        this.puerto = puerto;
        host.setNumeroJugadores(numeroJugadores);
    }

    /**
     * Inicia la aplicación recibiendo como primer parámetro el puerto de escucha
     * del servidor y el número de jugadores que pueden ingresar en la partida.
     * @param args Primer parámetro el puerto de escucha y segundo parámetro número
     * de jugadores soportados.
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                int puerto = Integer.parseInt(args[0]);
                int numJugadores = Integer.parseInt(args[1]);
                DominoServidor dominoServidor = new DominoServidor(puerto, numJugadores);
                dominoServidor.esperaJugadores();
            } catch (NumberFormatException ex) {
                System.out.println("Parámetro incorrecto <puerto>; debe ser un número de puerto.");
            }
        } else {
            System.out.println("Uso: DominoServidor <puerto> jugadores");
        }
    }

    /**
     * Inicia el servidor. Éste método se bloquea hasta que se han conectado el número
     * mínimo de jugadores para poder comenzar la partida.
     */
    private void esperaJugadores() {
        try {
            this.server = new ServerSocket(puerto);
            while (!host.comenzado) {
                Socket socket = server.accept();
                Jugador nuevo = new Jugador(host, socket);
                host.agregarJugador(nuevo);
            }
        } catch (IOException ex) {
            System.out.println("Error de socket: " + ex.getMessage());
        }
    }
}
