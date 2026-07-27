import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nodo interno o una hoja del árbol B+.
 */
public class NodoArbolBMas {

    private final boolean esHoja;
    private List<Integer> claves;
    private List<NodoArbolBMas> hijos;
    private List<String> datos;
    private NodoArbolBMas siguiente;

    /**
     * Crea un nodo con las colecciones que corresponden a su tipo.
     */
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public void setDatos(List<String> datos) {
        if (!esHoja && datos != null) {
            throw new IllegalStateException("Los nodos internos no almacenan datos.");
        }
        this.datos = datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoArbolBMas siguiente) {
        if (!esHoja && siguiente != null) {
            throw new IllegalStateException("Los nodos internos no enlazan hojas.");
        }
        this.siguiente = siguiente;
    }
}
