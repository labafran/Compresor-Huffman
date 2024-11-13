# Compresor Huffman
Trabajo Práctico Final grupal para la materia Algoritmos y Estructuras de Datos

## Descripción
Este proyecto tiene como objetivo desarrollar un compresor de datos utilizando el algoritmo de compresión de Huffman. Inspirado en compresores como WinRAR y 7Zip, el programa comprime y descomprime archivos, aplicando un enfoque que combina técnicas de estructura de datos y algoritmos.

## Funcionamiento
- Análisis de Frecuencia: El programa analiza el archivo de entrada para calcular la frecuencia de cada carácter, generando una lista de los caracteres con sus frecuencias.

- Construcción del Árbol de Huffman: Con la lista de frecuencias, se construye un árbol binario donde los caracteres de mayor frecuencia tienen una menor profundidad, optimizando el almacenamiento.

- Codificación y Compresión: Utilizando el árbol de Huffman, se transforma el archivo de entrada en una secuencia de bits que representa la versión comprimida del contenido.

- Descompresión: El archivo comprimido puede ser leído y descifrado por el mismo programa, reconstruyendo el archivo original a partir de la secuencia de bits y el árbol de Huffman.

## Tecnologías Utilizadas
- Lenguajes de Programación: Java.

- Algoritmo de Compresión: Árbol de Huffman.

## Equipo de Trabajo
- Gerardi, Santino 
- Labayen, Franco
- Molina, Rodrigo
- Miño, Bruno
