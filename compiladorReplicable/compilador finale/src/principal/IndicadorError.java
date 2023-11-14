/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

/**
 *
 * @author Yair
 */
public class IndicadorError {

    //Solo D10S y NOS sabemos lo que hace éste co:digo.
    //Esta clase es un switch de distintos casos de error.
    public void mostrarError(int codigo, String cadena) {
        switch (codigo) {
            case 0:
                System.out.println("compilacion con exito");
                break;
            case 1:
                System.out.println("El simbolo " + cadena + " esta de mas.");
                break;
            case 2:
                System.out.println("Se esperaba un punto y se encontro: " + cadena);
                break;
            case 3:
                System.out.println("Se esperaba un identificador y se encontro: " + cadena);
                break;
            case 4:
                System.out.println("Se esperaba un IGUAL y se encontro: " + cadena);
                break;
            case 5:
                System.out.println("Se esperaba un NUMERO y se encontro: " + cadena);
                break;
            case 6:
                System.out.println("Se esperaba un PUNTO_Y_COMA y se encontro: " + cadena);
                break;
            case 7:
                System.out.println("Se esperaba ASIGNACION_DE_VARIABLE y se encontro: " + cadena);
                break;
            case 8:
                System.out.println("Se esperaba END y se encontro: " + cadena);
                break;
            case 9:
                System.out.println("Se esperaba THEN y se encontro: " + cadena);
                break;
            case 10:
                System.out.println("Se esperaba DO y se encontro: " + cadena);
                break;
            case 11:
                System.out.println("Se esperaba APERTURA_PARENTESIS y se encontro: " + cadena);
                break;
            case 12:
                System.out.println("Se esperaba CIERRE_PARENTESIS y se encontro: " + cadena);
                break;
            case 13:
                System.out.println("Se esperaba un identificador y se encontro: " + cadena);
                break;
            case 14:
                System.out.println("Se esperaba un IGUAL y se encontro: " + cadena);
                break;
            /*case 15:
                System.out.println("Se esperaba un NUMERO y se encontro: " + cadena);
                break;*/
 /*case 16:
                System.out.println("Se esperaba un PUNTO_Y_COMA y se encontro: " + cadena);
                break;*/
            case 17:
                System.out.println("Se esperaba ASIGNACION_DE_VARIABLE y se encontro: " + cadena);
                break;
            case 18:
                System.out.println("Se esperaba END y se encontro: " + cadena);
                break;
            case 19:
                System.out.println("Se esperaba un operador logico y se encontro: " + cadena);
                break;
            case 20:
                System.out.println("Se esperaba IDENTIFICADOR, NUMERO, o APERTURA_PARENTESIS pero se encontro: " + cadena);
                break;
            case 21:
                System.out.println("Hay un error en la expresion ");
                break;
            case 22:
                System.out.println("Se esperaba un PARENTESIS DE CIERRE pero se encontro " + cadena);
                break;
            case 23:
                System.out.println("Tiene dos identificadores iguales: " + cadena);
                break;
            case 24:
                System.out.println("El identificador: '" + cadena + "' no fue definido.");
                break;
            case 25:
                System.out.println("El identificador '" + cadena + "' no es un CONST o INT.");
                break;
            case 26:
                System.out.println("El identificador '" + cadena + "' no es un PROCEDURE.");
                break;
        }
        System.exit(0);
    }

}
