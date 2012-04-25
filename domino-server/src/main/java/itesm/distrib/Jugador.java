package itesm.distrib;

import java.io.*;
import java.net.*;
import java.util.*;

public class Jugador extends Thread {

    private Host _host;
    private Socket _socket;
    private PrintStream salida = null;
    private BufferedReader entrada = null;
    private Listener listener = null;
    private int _numero;
    private ArrayList<Ficha> fichas;
    private Tren tren;

    public Host getServidor() {
        return _host;
    }

    public int getNumero() {
        return _numero;
    }

    public Socket getSocket() {
        return _socket;
    }

    public void setHost(Host value) {
        _host = value;
    }

    public void setNumero(int value) {
        _numero = value;
    }

    public void setSocket(Socket value) {
        _socket = value;
    }

    public Tren getTren() {
        return tren;
    }

    public void tomarFicha(Ficha ficha) {
        fichas.add(ficha);
    }

    public void tomarFichas(Collection<Ficha> listaFichas) {
        Iterator<Ficha> iterator = listaFichas.iterator();
        while (iterator.hasNext()) {
            this.tomarFicha(iterator.next());
        }
    }

    void quitarFicha(Ficha ficha) {
        Iterator<Ficha> i = fichas.iterator();
        while (i.hasNext()) {
            Ficha f = i.next();
            if (f.equals(ficha)) {
                fichas.remove(f);
                break;
            }
        }
    }

    public Jugador(Host host, Socket socket) {
        setHost(host);
        setSocket(socket);
        fichas = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            entrada = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            salida = new PrintStream(_socket.getOutputStream());
            listener = new Listener(this, entrada);
            tren = new Tren(this);
            this.enviarNumero();
            Thread t = new Thread(listener);
            t.start();
        } catch (IOException ex) {
            System.out.println("Error de acceso a E/S:" + ex.getMessage());
        }

    }

    public void interpretarComando(String mensaje) {
        String numeroJugador = String.valueOf(this.getNumero());

        String[] args = mensaje.split(":");
        if (args.length >= 1) {
            String comando = args[0];
            Ficha ficha = null;
            switch (comando) {
                case "Comer":
                    System.out.println("Jugador " + numeroJugador + " come");
                    ficha = _host.getFicha();
                    enviarFicha(ficha);
                    break;
                case "Poner":
                    //Poner Ficha
                    String[] trenArgs = args[1].split(",");
                    int numeroTren = Integer.parseInt(trenArgs[0]);
                    ficha = new Ficha(trenArgs[1]);
                    if (_host.ponerFichaTren(this.getNumero(), numeroTren, ficha)) {
                        System.out.println("Jugador " + numeroJugador + " puso " + trenArgs[0] + " en tren " + trenArgs[1]);
                        quitarFicha(ficha);
                    }
                    break;
                case "Pasar":
                    System.out.println("Jugador " + numeroJugador + "paso su turno");
                    this.tren.setMarcado(true);
                    _host.enviarTrenes();
                    break;
            }
        }
    }

    void enviarFicha(Ficha ficha) {
        this.enviarMensaje("Ficha:" + ficha.toString());
    }

    void enviarFichas() {
        StringBuilder sb = new StringBuilder();
        String coma = "";
        Iterator<Ficha> f = fichas.iterator();
        sb.append("Fichas:");
        while (f.hasNext()) {
            Ficha ficha = f.next();
            sb.append(coma);
            sb.append(ficha.toString());
            coma = ",";
        }
        this.enviarMensaje(sb.toString());
    }

    void enviarTren(Tren tren) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tren:");
        sb.append(tren.toString());
        this.enviarMensaje(sb.toString());
    }

    public void enviarNumero() {
        this.enviarMensaje("Num:" + String.valueOf(this.getNumero()));
    }

    void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }
}
