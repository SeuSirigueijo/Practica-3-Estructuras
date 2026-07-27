import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementa un árbol B+ de claves enteras y datos de texto.
 */
public class ArbolBMas {

    private final int orden;
    private NodoArbolBMas raiz;

    /**
     * Crea un árbol vacío con el orden indicado.
     */
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol debe ser al menos 3.");
        }
        this.orden = orden;
        this.raiz = new NodoArbolBMas(true);
    }

    /**
     * Conserva la operación original usando un dato generado.
     */
    public void insertar(int clave) {
        insertar(clave, "Dato " + clave);
    }

    /**
     * Inserta una clave y su dato si la clave todavía no existe.
     */
    public boolean insertar(int clave, String dato) {
        if (dato == null || dato.isBlank()) {
            throw new IllegalArgumentException("El dato no puede estar vacío.");
        }
        if (buscar(clave)) {
            return false;
        }

        insertarRecursivo(raiz, clave, dato);

        if (estaDesbordado(raiz)) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(raiz);
            dividirHijo(nuevaRaiz, 0);
            raiz = nuevaRaiz;
        }
        return true;
    }

    /**
     * Inserta en la hoja correcta y divide los nodos que se desbordan.
     */
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        if (nodo.esHoja()) {
            int posicion = primerIndiceMayorOIgual(nodo.getClaves(), clave);
            nodo.getClaves().add(posicion, clave);
            nodo.getDatos().add(posicion, dato);
            return;
        }

        int indiceHijo = buscarIndiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(indiceHijo);
        insertarRecursivo(hijo, clave, dato);

        if (estaDesbordado(hijo)) {
            dividirHijo(nodo, indiceHijo);
        } else {
            reconstruirClaves(nodo);
        }
    }

    /**
     * Divide un hijo lleno y agrega el nuevo nodo al padre.
     */
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());

        if (nodoLleno.esHoja()) {
            int corte = nodoLleno.getClaves().size() / 2;

            nuevoNodo.setClaves(new ArrayList<>(
                    nodoLleno.getClaves().subList(corte, nodoLleno.getClaves().size())));
            nuevoNodo.setDatos(new ArrayList<>(
                    nodoLleno.getDatos().subList(corte, nodoLleno.getDatos().size())));

            nodoLleno.setClaves(new ArrayList<>(
                    nodoLleno.getClaves().subList(0, corte)));
            nodoLleno.setDatos(new ArrayList<>(
                    nodoLleno.getDatos().subList(0, corte)));

            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);
        } else {
            int corte = (nodoLleno.getHijos().size() + 1) / 2;

            nuevoNodo.setHijos(new ArrayList<>(
                    nodoLleno.getHijos().subList(corte, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(
                    nodoLleno.getHijos().subList(0, corte)));

            reconstruirClaves(nodoLleno);
            reconstruirClaves(nuevoNodo);
        }

        padre.getHijos().add(indice + 1, nuevoNodo);
        reconstruirClaves(padre);
    }

    /**
     * Indica si un nodo excedió su capacidad.
     */
    private boolean estaDesbordado(NodoArbolBMas nodo) {
        if (nodo.esHoja()) {
            return nodo.getClaves().size() >= orden;
        }
        return nodo.getHijos().size() > orden;
    }

    /**
     * Busca una clave únicamente en las hojas.
     */
    public boolean buscar(int clave) {
        return buscarDato(clave) != null;
    }

    /**
     * Devuelve el dato asociado con una clave o null si no existe.
     */
    public String buscarDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int posicion = Collections.binarySearch(hoja.getClaves(), clave);
        return posicion >= 0 ? hoja.getDatos().get(posicion) : null;
    }

    /**
     * Elimina una clave, su dato y corrige cualquier desbalance.
     */
    public boolean eliminar(int clave) {
        List<NodoArbolBMas> padres = new ArrayList<>();
        NodoArbolBMas hoja = raiz;

        while (!hoja.esHoja()) {
            padres.add(hoja);
            hoja = hoja.getHijos().get(buscarIndiceHijo(hoja, clave));
        }

        int posicion = Collections.binarySearch(hoja.getClaves(), clave);
        if (posicion < 0) {
            return false;
        }

        hoja.getClaves().remove(posicion);
        hoja.getDatos().remove(posicion);

        if (hoja != raiz) {
            rebalancearDespuesDeEliminar(hoja, padres);
        }

        for (int i = padres.size() - 1; i >= 0; i--) {
            reconstruirClaves(padres.get(i));
        }

        while (!raiz.esHoja() && raiz.getHijos().size() == 1) {
            raiz = raiz.getHijos().get(0);
        }

        if (!raiz.esHoja()) {
            reconstruirClaves(raiz);
        }
        return true;
    }

    /**
     * Repara un nodo con ocupación insuficiente mediante préstamo o fusión.
     */
    private void rebalancearDespuesDeEliminar(
            NodoArbolBMas nodo, List<NodoArbolBMas> padres) {

        NodoArbolBMas actual = nodo;
        int nivel = padres.size() - 1;

        while (actual != raiz && nivel >= 0) {
            int minimo = actual.esHoja()
                    ? minimoClavesHoja()
                    : minimoHijosInterno();

            int ocupacion = actual.esHoja()
                    ? actual.getClaves().size()
                    : actual.getHijos().size();

            if (ocupacion >= minimo) {
                break;
            }

            NodoArbolBMas padre = padres.get(nivel);
            int indice = padre.getHijos().indexOf(actual);
            NodoArbolBMas izquierdo = indice > 0
                    ? padre.getHijos().get(indice - 1)
                    : null;
            NodoArbolBMas derecho = indice + 1 < padre.getHijos().size()
                    ? padre.getHijos().get(indice + 1)
                    : null;

            if (actual.esHoja()) {
                if (prestarAHoja(actual, izquierdo, derecho, padre)) {
                    break;
                }
                fusionarHojas(actual, izquierdo, derecho, padre, indice);
            } else {
                if (prestarAInterno(actual, izquierdo, derecho, padre)) {
                    break;
                }
                fusionarInternos(actual, izquierdo, derecho, padre, indice);
            }

            actual = padre;
            nivel--;
        }
    }

    /**
     * Intenta prestar una entrada de una hoja vecina.
     */
    private boolean prestarAHoja(
            NodoArbolBMas actual,
            NodoArbolBMas izquierdo,
            NodoArbolBMas derecho,
            NodoArbolBMas padre) {

        int minimo = minimoClavesHoja();

        if (izquierdo != null && izquierdo.getClaves().size() > minimo) {
            int ultima = izquierdo.getClaves().size() - 1;
            actual.getClaves().add(0, izquierdo.getClaves().remove(ultima));
            actual.getDatos().add(0, izquierdo.getDatos().remove(ultima));
            reconstruirClaves(padre);
            return true;
        }

        if (derecho != null && derecho.getClaves().size() > minimo) {
            actual.getClaves().add(derecho.getClaves().remove(0));
            actual.getDatos().add(derecho.getDatos().remove(0));
            reconstruirClaves(padre);
            return true;
        }

        return false;
    }

    /**
     * Fusiona una hoja con uno de sus vecinos.
     */
    private void fusionarHojas(
            NodoArbolBMas actual,
            NodoArbolBMas izquierdo,
            NodoArbolBMas derecho,
            NodoArbolBMas padre,
            int indice) {

        if (izquierdo != null) {
            izquierdo.getClaves().addAll(actual.getClaves());
            izquierdo.getDatos().addAll(actual.getDatos());
            izquierdo.setSiguiente(actual.getSiguiente());
            padre.getHijos().remove(indice);
        } else if (derecho != null) {
            actual.getClaves().addAll(derecho.getClaves());
            actual.getDatos().addAll(derecho.getDatos());
            actual.setSiguiente(derecho.getSiguiente());
            padre.getHijos().remove(indice + 1);
        }

        reconstruirClaves(padre);
    }

    /**
     * Intenta prestar un hijo de un nodo interno vecino.
     */
    private boolean prestarAInterno(
            NodoArbolBMas actual,
            NodoArbolBMas izquierdo,
            NodoArbolBMas derecho,
            NodoArbolBMas padre) {

        int minimo = minimoHijosInterno();

        if (izquierdo != null && izquierdo.getHijos().size() > minimo) {
            int ultimo = izquierdo.getHijos().size() - 1;
            actual.getHijos().add(0, izquierdo.getHijos().remove(ultimo));
            reconstruirClaves(izquierdo);
            reconstruirClaves(actual);
            reconstruirClaves(padre);
            return true;
        }

        if (derecho != null && derecho.getHijos().size() > minimo) {
            actual.getHijos().add(derecho.getHijos().remove(0));
            reconstruirClaves(derecho);
            reconstruirClaves(actual);
            reconstruirClaves(padre);
            return true;
        }

        return false;
    }

    /**
     * Fusiona un nodo interno con uno de sus vecinos.
     */
    private void fusionarInternos(
            NodoArbolBMas actual,
            NodoArbolBMas izquierdo,
            NodoArbolBMas derecho,
            NodoArbolBMas padre,
            int indice) {

        if (izquierdo != null) {
            izquierdo.getHijos().addAll(actual.getHijos());
            padre.getHijos().remove(indice);
            reconstruirClaves(izquierdo);
        } else if (derecho != null) {
            actual.getHijos().addAll(derecho.getHijos());
            padre.getHijos().remove(indice + 1);
            reconstruirClaves(actual);
        }

        reconstruirClaves(padre);
    }

    /**
     * Devuelve hasta n registros desde la primera clave mayor o igual a la indicada.
     */
    public List<String> obtenerRango(int claveInicial, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        List<String> resultado = new ArrayList<>();
        NodoArbolBMas hoja = buscarHoja(claveInicial);
        int posicion = primerIndiceMayorOIgual(hoja.getClaves(), claveInicial);

        while (hoja != null && resultado.size() < cantidad) {
            while (posicion < hoja.getClaves().size()
                    && resultado.size() < cantidad) {
                resultado.add(hoja.getClaves().get(posicion)
                        + " -> " + hoja.getDatos().get(posicion));
                posicion++;
            }
            hoja = hoja.getSiguiente();
            posicion = 0;
        }

        return resultado;
    }

    /**
     * Imprime los registros recuperados por una búsqueda de rango.
     */
    public void recorrerRango(int claveInicial, int cantidad) {
        List<String> resultado = obtenerRango(claveInicial, cantidad);

        if (resultado.isEmpty()) {
            System.out.println("No hay elementos desde la clave indicada.");
            return;
        }

        System.out.println("Elementos encontrados:");
        for (String registro : resultado) {
            System.out.println("  " + registro);
        }
    }

    /**
     * Muestra la estructura completa del árbol.
     */
    public void imprimirArbol() {
        if (raiz.esHoja() && raiz.getClaves().isEmpty()) {
            System.out.println("El árbol está vacío.");
            return;
        }
        imprimirNodo(raiz, "", true);
    }

    /**
     * Imprime recursivamente un nodo y sus hijos.
     */
    private void imprimirNodo(
            NodoArbolBMas nodo, String indentacion, boolean esUltimo) {

        String contenido = nodo.esHoja()
                ? formatearHoja(nodo)
                : nodo.getClaves().toString();

        System.out.println(indentacion
                + (esUltimo ? "+- " : "|- ")
                + (nodo.esHoja() ? "Hoja " : "Interno ")
                + contenido);

        String nuevaIndentacion = indentacion + (esUltimo ? "   " : "|  ");
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(
                    nodo.getHijos().get(i),
                    nuevaIndentacion,
                    i == nodo.getHijos().size() - 1);
        }
    }

    /**
     * Presenta las claves y datos de una hoja.
     */
    private String formatearHoja(NodoArbolBMas hoja) {
        List<String> registros = new ArrayList<>();
        for (int i = 0; i < hoja.getClaves().size(); i++) {
            registros.add(hoja.getClaves().get(i)
                    + "=" + hoja.getDatos().get(i));
        }
        return registros.toString();
    }

    /**
     * Localiza la hoja que puede contener una clave.
     */
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas nodo = raiz;
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().get(buscarIndiceHijo(nodo, clave));
        }
        return nodo;
    }

    /**
     * Selecciona el hijo correspondiente según las claves separadoras.
     */
    private int buscarIndiceHijo(NodoArbolBMas nodo, int clave) {
        int indice = 0;
        while (indice < nodo.getClaves().size()
                && clave >= nodo.getClaves().get(indice)) {
            indice++;
        }
        return indice;
    }

    /**
     * Encuentra la posición de inserción mediante búsqueda binaria.
     */
    private int primerIndiceMayorOIgual(List<Integer> claves, int clave) {
        int izquierda = 0;
        int derecha = claves.size();

        while (izquierda < derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            if (claves.get(medio) < clave) {
                izquierda = medio + 1;
            } else {
                derecha = medio;
            }
        }
        return izquierda;
    }

    /**
     * Actualiza las claves separadoras a partir de los hijos.
     */
    private void reconstruirClaves(NodoArbolBMas nodo) {
        if (nodo.esHoja()) {
            return;
        }

        nodo.getClaves().clear();
        for (int i = 1; i < nodo.getHijos().size(); i++) {
            nodo.getClaves().add(obtenerPrimeraClave(nodo.getHijos().get(i)));
        }
    }

    /**
     * Obtiene la primera clave almacenada en un subárbol.
     */
    private int obtenerPrimeraClave(NodoArbolBMas nodo) {
        NodoArbolBMas actual = nodo;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(0);
        }
        return actual.getClaves().get(0);
    }

    private int minimoClavesHoja() {
        return orden / 2;
    }

    private int minimoHijosInterno() {
        return (orden + 1) / 2;
    }
}
