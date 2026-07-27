import java.util.Scanner;

/**
 * Proporciona la interfaz de consola para utilizar el árbol B+.
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            mostrarEncabezado();
            int orden = leerEnteroMinimo(
                    scanner, "Indique el orden del árbol (mínimo 3): ", 3);
            ArbolBMas arbol = new ArbolBMas(orden);
            menu(scanner, arbol);
        }
    }

    /**
     * Mantiene activo el menú hasta que el usuario decida salir.
     */
    private static void menu(Scanner scanner, ArbolBMas arbol) {
        int opcion;

        do {
            mostrarOpciones();
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> insertar(scanner, arbol);
                case 2 -> buscar(scanner, arbol);
                case 3 -> eliminar(scanner, arbol);
                case 4 -> recorrerRango(scanner, arbol);
                case 5 -> arbol.imprimirArbol();
                case 0 -> System.out.println("Gracias por utilizar el árbol B+.");
                default -> System.out.println("La opción indicada no existe.");
            }
        } while (opcion != 0);
    }

    /**
     * Solicita los datos necesarios para una inserción.
     */
    private static void insertar(Scanner scanner, ArbolBMas arbol) {
        int clave = leerEntero(scanner, "Clave entera: ");
        String dato = leerTexto(scanner, "Dato asociado: ");

        if (arbol.insertar(clave, dato)) {
            System.out.println("El elemento se insertó correctamente.");
        } else {
            System.out.println("La clave ya existe; no se realizó la inserción.");
        }
    }

    /**
     * Busca una clave y muestra su dato.
     */
    private static void buscar(Scanner scanner, ArbolBMas arbol) {
        int clave = leerEntero(scanner, "Clave por buscar: ");
        String dato = arbol.buscarDato(clave);

        if (dato == null) {
            System.out.println("La clave no se encuentra en el árbol.");
        } else {
            System.out.println("Dato encontrado: " + dato);
        }
    }

    /**
     * Solicita la clave que se desea eliminar.
     */
    private static void eliminar(Scanner scanner, ArbolBMas arbol) {
        int clave = leerEntero(scanner, "Clave por eliminar: ");

        if (arbol.eliminar(clave)) {
            System.out.println("El elemento se eliminó correctamente.");
        } else {
            System.out.println("La clave no se encuentra en el árbol.");
        }
    }

    /**
     * Solicita el inicio y el tamaño de un recorrido por rango.
     */
    private static void recorrerRango(Scanner scanner, ArbolBMas arbol) {
        int claveInicial = leerEntero(scanner, "Clave inicial: ");
        int cantidad = leerEnteroMinimo(
                scanner, "Cantidad de elementos por mostrar: ", 1);
        arbol.recorrerRango(claveInicial, cantidad);
    }

    /**
     * Lee un número entero y repite la solicitud si es inválido.
     */
    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException excepcion) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    /**
     * Lee un entero que debe respetar un valor mínimo.
     */
    private static int leerEnteroMinimo(
            Scanner scanner, String mensaje, int minimo) {

        while (true) {
            int valor = leerEntero(scanner, mensaje);
            if (valor >= minimo) {
                return valor;
            }
            System.out.println("El valor mínimo permitido es " + minimo + ".");
        }
    }

    /**
     * Lee un texto que no puede quedar vacío.
     */
    private static String leerTexto(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El dato no puede quedar vacío.");
        }
    }

    private static void mostrarEncabezado() {
        System.out.println("================================");
        System.out.println("       ÁRBOL B+ INTERACTIVO");
        System.out.println("================================");
    }

    private static void mostrarOpciones() {
        System.out.println();
        System.out.println("--------------- MENÚ ---------------");
        System.out.println("1. Insertar un elemento");
        System.out.println("2. Buscar un elemento");
        System.out.println("3. Eliminar un elemento");
        System.out.println("4. Recorrer un rango");
        System.out.println("5. Mostrar la estructura del árbol");
        System.out.println("0. Salir");
        System.out.println("------------------------------------");
    }
}
