# Árbol B+ en Java

Implementación de un árbol B+ de claves enteras y datos de texto. El proyecto parte
del código base proporcionado para la segunda práctica de Estructuras de Datos.

## Funcionalidades

- Inserción de claves y datos sin permitir claves duplicadas.
- División automática de hojas y nodos internos.
- Búsqueda de datos por clave.
- Eliminación con préstamo, fusión y reducción de la raíz.
- Hojas enlazadas para realizar recorridos por rango.
- Menú de consola con validación de entradas.
- Impresión de la estructura del árbol.
- Pruebas automáticas para árboles de orden 3 a 8.

## Decisiones de implementación

- El orden `m` representa el máximo de hijos de un nodo interno.
- Un nodo puede almacenar como máximo `m - 1` claves.
- Las claves internas se utilizan únicamente como separadores.
- Las claves y los datos reales permanecen en las hojas.
- Cada clave es única.
- Un recorrido comienza en la primera clave mayor o igual a la clave inicial.

## Estructura

```text
ArbolBMas/
├── src/
│   ├── ArbolBMas.java
│   ├── Main.java
│   └── NodoArbolBMas.java
├── test/
│   └── ArbolBMasTest.java
├── .gitignore
└── README.md
```

## Requisitos

- Java 17 o una versión posterior.

## Compilación y ejecución

Desde la carpeta principal del proyecto:

```text
javac -d out src/NodoArbolBMas.java src/ArbolBMas.java src/Main.java
java -cp out Main
```

## Ejecución de las pruebas

```text
javac -d out src/NodoArbolBMas.java src/ArbolBMas.java test/ArbolBMasTest.java
java -cp out ArbolBMasTest
```

Las pruebas insertan, buscan, recorren y eliminan 120 elementos en árboles de
distintos órdenes. Después de cada eliminación comprueban que los datos restantes
continúen ordenados y accesibles.
