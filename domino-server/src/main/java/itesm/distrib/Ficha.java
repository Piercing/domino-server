package itesm.distrib;

class Ficha {

    private int izquierda;
    private int derecha;

    public int getIzquierda() {
        return izquierda;
    }

    public int getDerecha() {
        return derecha;
    }

    private void setIzquierda(int value) {
        izquierda = value;
    }

    private void setDerecha(int value) {
        derecha = value;
    }

    public Ficha(int izq, int der) {
        setIzquierda(izq);
        setDerecha(der);
    }

    public Ficha(String cadena) {

        String[] parametros = cadena.split("-");
        int izq = Integer.parseInt(parametros[0]);
        int der = Integer.parseInt(parametros[1]);
        setIzquierda(izq);
        setDerecha(der);
    }

    public void voltear() {
        int t = getIzquierda();
        setIzquierda(getDerecha());
        setDerecha(t);
    }

    @Override
    public String toString() {
        return String.format("%d-%d", getIzquierda(), getDerecha());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (!(other instanceof Ficha)) {
            return false;
        } else {
            Ficha f = (Ficha) other;
            return this.getIzquierda() == f.getIzquierda() && this.getDerecha() == f.getDerecha()
                    || this.getDerecha() == f.getIzquierda() && this.getIzquierda() == f.getDerecha();
        }
    }    
    
    public int getPuntos(){
        return getIzquierda() + getDerecha();
    }    
}
