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
        fichas = new ArrayList<Ficha>();
    }

    @Override
    public void run() {

        tren = new Tren(this);
        try {
            entrada = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            salida = new PrintStream(_socket.getOutputStream());
            listener = new Listener(this, entrada);
            this.enviarNumero();
            Thread t = new Thread(listener);
            t.start();
        } catch (IOException ex) {
            System.out.println("Error de acceso a E/S:" + ex.getMessage());
        }

    }

    public void interpretarComando(String mensaje) {
        String strj = String.valueOf(this.getNumero());

        String[] args = mensaje.split(":");
        if (args.length >= 1) {
            String comando = args[0];
            if (comando.equals("Comer")) {
                System.out.println("Jugador " + strj + " come");
                Ficha ficha = _host.getFicha();
                enviarFicha(ficha);
            } else if (comando.startsWith("Poner")) { //Poner Ficha
                if (args.length == 2) {
                    Ficha ficha = new Ficha(args[1]);
                    if (_host.ponerFichaTren(this.getNumero(), ficha)) {
                        System.out.println("Jugador " + strj + " puso " + args[1]);
                        quitarFicha(ficha);
                    }
                }
            }
        }
    }

    void enviarFicha(Ficha ficha) {
        this.enviarMensaje("Ficha:" + ficha.toString());
    }

    void enviarFichas() {
        StringBuffer sb = new StringBuffer();
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
