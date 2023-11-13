/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author alumno
 */
public class AnalizadorLexico {

    private String nombreArchivo;
    private String cadena;
    private Terminal simbolo;
    private char caracter;
    private FileReader archivo;
    private ArrayList<String> palabrasReservadas;
    private int linea;

    public AnalizadorLexico(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        linea = 0;
        simbolo = Terminal.NUL;
        caracter = ' ';
        try {
            archivo = new FileReader(nombreArchivo);
            palabrasReservadas = new ArrayList<>();
            cargarPalabrasReservadas();
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado");
        }
    }

    public void escanear() throws IOException {
        analizarPalabra();
    }

    private void concatenar() {
        cadena = cadena.concat(String.valueOf(caracter));
    }

    public void leerCaracter() throws IOException {
        int aux = archivo.read();
        if (aux != -1) {
            caracter = (char) aux;
        } else {
            caracter = (char) 26;
        }
    }

    public void limpiarPalabra() {
        cadena = "";
    }

    private void analizarPalabra() throws IOException {
        limpiarPalabra();
        if (caracterEstaVacio()) {
            leerCaracter();
            concatenar();
        } else {
            concatenar();
        }
        if (Character.isAlphabetic(caracter)) {
            leerCaracter();
            while (Character.isLetterOrDigit(caracter)) {
                concatenar();
                leerCaracter();
            }
            if (esPalabraReservada()) {
                simbolo = chequearPalabraReservada();
            } else {
                simbolo = Terminal.IDENTIFICADOR;
            }
        } else if (Character.isDigit(caracter)) {
            leerCaracter();
            while (Character.isDigit(caracter)) {
                concatenar();
                leerCaracter();
            }
            simbolo = Terminal.NUMERO;
        } else if (caracter == ';') {
            simbolo = Terminal.PUNTO_Y_COMA;
            leerCaracter();
        } else if (caracter == ':') {
            leerCaracter();
            concatenar();
            if (caracter == '=') {
                simbolo = Terminal.ASIGNACION_DE_VARIABLE;
                leerCaracter();
            } else {
                simbolo = Terminal.NUL;
            }
        } else if (caracter == '<') {
            leerCaracter();
            concatenar();
            if (caracter == '=') {
                simbolo = Terminal.MENOR_O_IGUAL;
                leerCaracter();
            } else if (caracter == '>') {
                simbolo = Terminal.DISTINTO;
                leerCaracter();
            } else {
                simbolo = Terminal.MENOR;
                leerCaracter();
            }
        } else if (caracter == '>') {
            leerCaracter();
            concatenar();
            if (caracter == '=') {
                simbolo = Terminal.MAYOR_O_IGUAL;
                leerCaracter();
            } else {
                simbolo = Terminal.MAYOR;
                leerCaracter();
            }
        } else if (caracter == '=') {
            simbolo = Terminal.IGUAL;
            leerCaracter();
        } else if (caracter == '.') {
            simbolo = Terminal.PUNTO;
            leerCaracter();
        } else if (caracter == ',') {
            simbolo = Terminal.COMA;
            leerCaracter();
        } else if (caracter == '(') {
            simbolo = Terminal.APERTURA_PARENTESIS;
            leerCaracter();
        } else if (caracter == ')') {
            simbolo = Terminal.CIERRE_PARENTESIS;
            leerCaracter();
        } else if (caracter == '+') {
            simbolo = Terminal.OPERADOR_SUMA;
            leerCaracter();
        } else if (caracter == '-') {
            simbolo = Terminal.OPERADOR_RESTA;
            leerCaracter();
        } else if (caracter == '*') {
            simbolo = Terminal.OPERADOR_MULTIPLICACION;
            leerCaracter();
        } else if (caracter == '/') {
            simbolo = Terminal.OPERADOR_DIVISION;
            leerCaracter();
        } else if ((int) caracter == 39) {
            leerCaracter();
            while ((int) caracter != 39) {
                concatenar();
                leerCaracter();
            }
            concatenar();
            simbolo = Terminal.CADENA_LITERAL;
            leerCaracter();
        } else if ((int) caracter == 26) {
            simbolo = Terminal.EOF;
        }
        //System.out.println((int)caracter);
    }

    public String getCadena() {
        return cadena;
    }

    public Terminal getSimbolo() {
        return simbolo;
    }

    private boolean esPalabraReservada() {
        return palabrasReservadas.contains(cadena.toLowerCase());
    }

    private void cargarPalabrasReservadas() {
        palabrasReservadas.add("const");
        palabrasReservadas.add("var");
        palabrasReservadas.add("procedure");
        palabrasReservadas.add("call");
        palabrasReservadas.add("begin");
        palabrasReservadas.add("end");
        palabrasReservadas.add("if");
        palabrasReservadas.add("then");
        palabrasReservadas.add("while");
        palabrasReservadas.add("do");
        palabrasReservadas.add("odd");
        palabrasReservadas.add("readln");
        palabrasReservadas.add("writeln");
        palabrasReservadas.add("write");
        palabrasReservadas.add("not");
        palabrasReservadas.add("halt");
        palabrasReservadas.add("sqr");
        palabrasReservadas.add("for");
        palabrasReservadas.add("to");

    }

    private boolean caracterEstaVacio() {
        return (caracter == ' ' || (int) caracter == 10 || (int) caracter == 13);
    }

    private boolean cadenaEstaVacia() {
        return cadena.equals(" ") || cadena.equals("\n") || cadena.equals("\r");
    }

    public void mostrar() {
        //condición para que no printee lineas de símbolos sin cadena.
        if (!cadenaEstaVacia()) {
            System.out.println(simbolo + " " + cadena);
        }
    }

    private Terminal chequearPalabraReservada() {
        Terminal terminalAux = Terminal.NUL;

        if (cadena.equalsIgnoreCase(palabrasReservadas.get(0))) {
            terminalAux = Terminal.CONST;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(1))) {
            terminalAux = Terminal.VAR;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(2))) {
            terminalAux = Terminal.PROCEDURE;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(3))) {
            terminalAux = Terminal.CALL;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(4))) {
            terminalAux = Terminal.BEGIN;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(5))) {
            terminalAux = Terminal.END;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(6))) {
            terminalAux = Terminal.IF;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(7))) {
            terminalAux = Terminal.THEN;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(8))) {
            terminalAux = Terminal.WHILE;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(9))) {
            terminalAux = Terminal.DO;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(10))) {
            terminalAux = Terminal.ODD;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(11))) {
            terminalAux = Terminal.READLN;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(12))) {
            terminalAux = Terminal.WRITELN;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(13))) {
            terminalAux = Terminal.WRITE;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(14))) {
            terminalAux = Terminal.NOT;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(15))) {
            terminalAux = Terminal.HALT;
        } else if (cadena.equalsIgnoreCase(palabrasReservadas.get(16))) {
            terminalAux = Terminal.SQR;
        }

        return terminalAux;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

}
