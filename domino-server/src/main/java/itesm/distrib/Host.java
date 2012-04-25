package itesm.distrib;

import java.util.*;

public class Host {

    boolean comenzado;
    private int numeroJugadores;
    private Ficha fichaInicio;
    private HashMap<Integer, Jugador> jugadores = new HashMap<Integer, Jugador>();
    private HashMap<Integer, Ficha> fichas = new HashMap<Integer, Ficha>();

    public int getConectados() {
        return jugadores.size();
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public Ficha getFichaInicio() {
        return fichaInicio;
    }

    public void setNumeroJugadores(int value) {
        numeroJugadores = value;
    }

    public void setFichaInicio(Ficha ficha) {
        fichaInicio = ficha;
    }

    public Collection<Jugador> getJugadores() {
        return jugadores.values();
    }

    public Host() {
        jugadores = new HashMap<Integer, Jugador>();
        iniciarFichas();
    }

    private void iniciarFichas() {

        fichas = new HashMap<Integer, Ficha>();
        int k = 1;
        for (int i = 1; i <= 12; i++) {
            for (int j = i; j <= 12; j++) {
                fichas.put(k++, new Ficha(i, j));
            }
        }
    }

    synchronized void agregarJugador(Jugador jugador) {
        if (!comenzado) {
            if (this.getConectados() <= this.getNumeroJugadores()) {
                int numero = jugadores.size() + 1;
                jugador.setNumero(numero);
                jugadores.put(numero, jugador);
                jugador.run();

                System.out.println("Nuevo jugador");
                if (this.getConectados() == this.getNumeroJugadores()) {
                    comenzar();
                }
            } else {
                jugador.enviarMensaje("Máximo de jugadores alcanzado");
            }
        } else {
            jugador.enviarMensaje("Juego ya iniciado");
        }
    }

    void comenzar() {
        comenzado = true;
        System.out.println("Inicia el juego");
        repartirFichas();
        Iterator<Jugador> i = getJugadores().iterator();
        while (i.hasNext()) {
            Jugador j = i.next();
            j.enviarFichas();
        }
        enviarTrenes();
    }

    private void repartirFichas() {
        Random r = new Random();
        int numeroFichas = 0;
        if ((numeroFichas = getNumeroFichas()) != 0) {

            for (int i = 1; i <= numeroFichas; i++) {

                Iterator<Jugador> it = getJugadores().iterator();
                while (it.hasNext()) {

                    Jugador jugador = it.next();
                    Ficha ficha = getFicha();
                    jugador.tomarFicha(ficha);
                }
            }
        }
    }

    public synchronized Ficha getFicha() {

        Ficha result = null;
        Random r = new Random();
        Set<Integer> setLlaves = fichas.keySet();
        ArrayList<Integer> listLlaves = new ArrayList<Integer>();
        listLlaves.addAll(setLlaves);

        int posicion = r.nextInt(listLlaves.size());
        int fichaId = listLlaves.get(posicion);
        result = fichas.get(fichaId);
        fichas.remove(fichaId);

        return result;
    }

    public synchronized boolean ponerFichaTren(int numero, Ficha ficha) {
        Jugador jugador = jugadores.get(numero);
        Tren tren = jugador.getTren();
        boolean agregar = false;

        if (tren.EsVacio()) {
            agregar = true;
        } else {
            Ficha ultima = tren.getUltimaFicha();
            if (ultima.getDerecha() == ficha.getIzquierda()) {
                agregar = true;
            }
        }
        if (agregar) {
            jugador.getTren().agregarFicha(ficha);
            enviarTrenes();
        }
        return agregar;
    }

    private int getNumeroFichas() {

        int j = jugadores.size();
        if (j >= 1 && j <= 3) {
            return 16;
        } else if (j == 4) {
            return 15;
        } else if (j == 5) {
            return 14;
        } else if (j == 6) {
            return 12;
        } else if (j == 7) {
            return 10;
        } else if (j == 8) {
            return 9;
        } else {
            return 0;
        }
    }

    private void broadcast(String mensaje) {
        Iterator<Jugador> i = getJugadores().iterator();
        while (i.hasNext()) {
            Jugador j = i.next();
            j.enviarMensaje(mensaje);
        }
    }

    private void enviarTrenes() {
        Iterator<Jugador> i = getJugadores().iterator();
        while (i.hasNext()) {
            Jugador j = i.next();
            this.broadcast("Tren:" + j.getTren().toString());
        }
    }
}
