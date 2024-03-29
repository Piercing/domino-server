package itesm.distrib;

import java.util.*;

public class Host {

    boolean comenzado;
    private int numeroJugadores;
    private HashMap<Integer, Jugador> jugadores = new HashMap<>();
    private HashMap<Integer, Ficha> fichas = new HashMap<>();
    private Tren trenPrincipal;
    private Ficha motor;
    private int rondaActual;

    public int getConectados() {
        return jugadores.size();
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public void setNumeroJugadores(int value) {
        numeroJugadores = value;
    }

    public Collection<Jugador> getJugadores() {
        return jugadores.values();
    }

    public Host(int rondaActual) {
        comenzado = false;
        jugadores = new HashMap<>();
        trenPrincipal = new Tren();
        trenPrincipal.setEsPrincipal(true);
        this.rondaActual = rondaActual;
        iniciarFichas(rondaActual);        
    }

    //Inicializa el Hashmap que incluye todas las fichas excluyendo la ficha con
    //la que se inicia la ronda actual.
    private void iniciarFichas(int rondaActual) {

        fichas = new HashMap<>();
        int k = 1;
        
        /*
        motor = new Ficha(12,12);
        trenPrincipal.agregarFicha(motor);        
        for(int i = 0; i < 15; i++){
            fichas.put(k++, new Ficha(11,12));
        }
        */
        
        
        for (int i = 1; i <= 12; i++) {
            for (int j = i; j <= 12; j++) {
                //Verificamos si la ficha que estamos por agregar es una mula
                //que coincide con la ronda actual. De ser así la colocamos como
                //ficha inicial en el tren principal y no la añadimos al hash de fichas.
                if (rondaActual == i && rondaActual == j) {
                    motor = new Ficha(i, j);
                    trenPrincipal.agregarFicha(motor);
                } else {
                    fichas.put(k++, new Ficha(i, j));
                }
            }
        }       
    }

    synchronized void agregarJugador(Jugador jugador) {
        if (!comenzado) {
            if (this.getConectados() <= this.getNumeroJugadores()) {
                int numero = jugadores.size() + 1;
                jugador.setNumero(numero);
                jugadores.put(numero, jugador);
                jugador.start();
                System.out.println("Nuevo jugador");
                /*
                if (this.getConectados() == this.getNumeroJugadores()) {
                    comenzar();
                }
                */
            } else {
                jugador.enviarMensaje("Máximo de jugadores alcanzado");
            }
        } else {
            jugador.enviarMensaje("Juego ya iniciado");
        }
    }

    synchronized void comenzar() {
        if(!comenzado){            
            comenzado = true;
            System.out.println("Inicia el juego");
            broadcast("Inicia el juego.");
            reiniciarJugadores();            
            repartirFichas();      
            for(Jugador jugador : getJugadores()){                
                jugador.enviarFichas();
                if(jugadorInicia(jugador)){
                    jugador.tomarTurno(jugador);
                }
            }
            enviarMotor();
            enviarTrenes();
        }
        //Tomamos el
    }

    /**
     * Reparte las fichas en juego de forma aleatoria entre los distintos jugadores.
     */
    private void repartirFichas() {
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
    
    private void reiniciarJugadores(){
        for(Jugador jugador : getJugadores()){
            jugador.reiniciar();
        }
    }

    public synchronized Ficha getFicha() {

        Ficha result = null;
        Random r = new Random();
        Set<Integer> setLlaves = fichas.keySet();
        ArrayList<Integer> listLlaves = new ArrayList<>();
        listLlaves.addAll(setLlaves);

        int posicion = r.nextInt(listLlaves.size());
        int fichaId = listLlaves.get(posicion);
        result = fichas.get(fichaId);
        fichas.remove(fichaId);

        return result;
    }

    public synchronized boolean ponerFichaTren(int numeroJugador, int numeroTren, Ficha ficha) {
        Jugador jugador = jugadores.get(numeroJugador);
        Tren tren;
        if(numeroTren == -1){
            tren = trenPrincipal; }
        else if (numeroJugador == numeroTren ) { 
            tren = jugador.getTren();}
        else { 
            tren = jugadores.get(numeroTren).getTren(); }

        boolean agregar = false;

        if (tren.EsVacio()) {
            if(motor.getDerecha() == ficha.getIzquierda()){
                agregar = true;
            }
        } else {
            Ficha ultima = tren.getUltimaFicha();
            if (ultima.getDerecha() == ficha.getIzquierda()) {
                agregar = true;
            }
        }
        if (agregar) {
            tren.agregarFicha(ficha);         
            jugador.quitarFicha(ficha);
            if(jugador.numeroFichas()== 0){
                FinJuego();                
                this.broadcast(jugador.toString() + " ha ganado el juego.!");
            }            
        }
        return agregar;
    }

    void FinJuego(){
        this.comenzado = false;
        Iterator<Jugador> i = getJugadores().iterator();
        while(i.hasNext()){
            Jugador j = i.next();
            enviarPuntos(j);
        }
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
                
        //return 3;
        
    }

    private void broadcast(String mensaje) {
        Iterator<Jugador> i = getJugadores().iterator();
        while (i.hasNext()) {
            Jugador j = i.next();
            j.enviarMensaje(mensaje);
        }
    }

    /**
     * Envía a todos los jugadores los trenes en juego.
     * Se enviarán tantos trenes como Jugadores existan más el tren central.
     */
    public void enviarTrenes() {
        //Enviamos el tren principal.
        this.EnviarTren(trenPrincipal);
        Iterator<Jugador> i = getJugadores().iterator();
        //Enviamos el tren de cada uno de los jugadores
        while (i.hasNext()) {
            Jugador j = i.next();
            this.EnviarTren(j.getTren());
        }        
    }    
    
    public void EnviarTren(Tren t){
        this.broadcast("Tren:" + t.toString());
    }    
    
    private void enviarMotor(){
        this.broadcast("Motor:"+motor.toString());                
    }

    private boolean jugadorInicia(Jugador jugador) {
        int numeroJugadorAIniciar = (rondaActual % getConectados()) + 1;
        if(jugador.getNumero() == numeroJugadorAIniciar){
            return true;
        }
        return false;
    }
    
    private void enviarPuntos(Jugador j){
        Integer puntos = j.getPuntos();
        this.broadcast("Puntos:" + j.toString() + ";" + puntos.toString());
    }
}
