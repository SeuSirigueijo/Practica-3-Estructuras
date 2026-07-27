import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Ejecuta pruebas automáticas sin bibliotecas externas.
 */
public class ArbolBMasTest {

    public static void main(String[] args) {
        probarValidaciones();

        for (int orden = 3; orden <= 8; orden++) {
            probarOperacionesCompletas(orden);
        }

        System.out.println("Todas las pruebas finalizaron correctamente.");
    }

    /**
     * Prueba inserciones y eliminaciones en árboles de distintos órdenes.
     */
    private static void probarOperacionesCompletas(int orden) {
        ArbolBMas arbol = new ArbolBMas(orden);
        TreeMap<Integer, String> esperados = new TreeMap<>();
        List<Integer> claves = new ArrayList<>();

        for (int clave = 0; clave < 120; clave++) {
            claves.add(clave);
        }

        Collections.shuffle(claves, new Random(1000L + orden));
        for (int clave : claves) {
            String dato = "Dato-" + clave;
            verificar(arbol.insertar(clave, dato),
                    "No se pudo insertar la clave " + clave);
            esperados.put(clave, dato);
        }

        verificar(!arbol.insertar(50, "Duplicado"),
                "El árbol permitió una clave duplicada.");
        validarContenido(arbol, esperados);

        List<String> rango = arbol.obtenerRango(57, 8);
        List<String> rangoEsperado = new ArrayList<>();
        for (int clave = 57; clave < 65; clave++) {
            rangoEsperado.add(clave + " -> Dato-" + clave);
        }
        verificarIguales(rangoEsperado, rango,
                "El recorrido no cruzó correctamente las hojas.");

        Collections.shuffle(claves, new Random(2000L + orden));
        for (int clave : claves) {
            verificar(arbol.eliminar(clave),
                    "No se pudo eliminar la clave " + clave);
            esperados.remove(clave);
            verificar(!arbol.buscar(clave),
                    "La clave eliminada todavía aparece en el árbol.");
            validarContenido(arbol, esperados);
        }

        verificar(!arbol.eliminar(999),
                "Se reportó la eliminación de una clave inexistente.");
        verificar(arbol.obtenerRango(Integer.MIN_VALUE, 10).isEmpty(),
                "El árbol debería estar vacío.");

        verificar(arbol.insertar(25, "Reinserción"),
                "El árbol no aceptó una reinserción después de vaciarse.");
        verificarIguales("Reinserción", arbol.buscarDato(25),
                "No se recuperó el dato reinsertado.");
    }

    /**
     * Comprueba que las búsquedas y el enlace de hojas mantengan el orden.
     */
    private static void validarContenido(
            ArbolBMas arbol, TreeMap<Integer, String> esperados) {

        for (Map.Entry<Integer, String> entrada : esperados.entrySet()) {
            verificarIguales(
                    entrada.getValue(),
                    arbol.buscarDato(entrada.getKey()),
                    "La búsqueda devolvió un dato incorrecto.");
        }

        List<String> listadoEsperado = new ArrayList<>();
        for (Map.Entry<Integer, String> entrada : esperados.entrySet()) {
            listadoEsperado.add(
                    entrada.getKey() + " -> " + entrada.getValue());
        }

        int cantidad = Math.max(1, esperados.size() + 5);
        verificarIguales(
                listadoEsperado,
                arbol.obtenerRango(Integer.MIN_VALUE, cantidad),
                "El recorrido completo quedó desordenado o incompleto.");
    }

    /**
     * Prueba los argumentos que deben ser rechazados.
     */
    private static void probarValidaciones() {
        verificarExcepcion(
                () -> new ArbolBMas(2),
                "Se aceptó un orden menor que 3.");

        ArbolBMas arbol = new ArbolBMas(4);
        verificarExcepcion(
                () -> arbol.insertar(1, " "),
                "Se aceptó un dato vacío.");
        verificarExcepcion(
                () -> arbol.obtenerRango(0, 0),
                "Se aceptó un rango sin elementos.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void verificarIguales(
            Object esperado, Object obtenido, String mensaje) {

        if (!esperado.equals(obtenido)) {
            throw new AssertionError(
                    mensaje + " Esperado: " + esperado
                            + "; obtenido: " + obtenido);
        }
    }

    private static void verificarExcepcion(
            Runnable operacion, String mensaje) {

        try {
            operacion.run();
            throw new AssertionError(mensaje);
        } catch (IllegalArgumentException excepcionEsperada) {
            // La validación funcionó.
        }
    }
}
