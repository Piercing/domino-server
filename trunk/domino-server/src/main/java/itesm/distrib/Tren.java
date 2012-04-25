package itesm.distrib;

import java.util.*;

class Tren {

    private int numero;
    private ArrayList<Ficha> fichas;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int value) {
        numero = value;
    }

    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
    }

    public ArrayList<Ficha> getFichas() {
        return fichas;
    }

    public boolean EsVacio() {
        return fichas.size() == 0;
    }

    public Ficha getUltimaFicha() {
        return fichas.get(fichas.size() - 1);
    }

    public Tren() {
        fichas = new ArrayList<Ficha>();
    }

    public Tren(Jugador j) {
        this();
        setNumero(j.getNumero());
    }

    public Tren(String str) {
        this();
        String[] a = str.split(";");
        if (a.length >= 1) {
            setNumero(Integer.parseInt(a[0]));
            if (a.length == 2) {
                String[] strFichas = a[1].split(",");
                for (int i = 0; i < strFichas.length; i++) {
                    Ficha f = new Ficha(strFichas[i]);
                    this.agregarFicha(f);
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(numero));
        sb.append(";");
        String coma = "";
        Iterator<Ficha> i = fichas.iterator();
        while (i.hasNext()) {
            Ficha f = i.next();
            sb.append(coma);
            sb.append(f.toString());
            coma = ",";
        }
        return sb.toString();
    }
}