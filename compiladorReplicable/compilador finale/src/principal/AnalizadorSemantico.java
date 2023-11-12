/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

public class AnalizadorSemantico {

    private IdentificadorBean[] identificadores;

    public AnalizadorSemantico() {
        identificadores = new IdentificadorBean[64];
    }

    public void agregarBean(int posicion, IdentificadorBean ident) {
        try {
            identificadores[posicion] = ident;
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Se ha llegado al tope de carga de variables!");
            System.out.println("Para expandir su capacidad de variables en el sistema, \npor favor contactese al: 0800-555-GRUPOTAURO.");
            System.exit(0);
        }
    }

    public int buscarBean(int desde, int hasta, String ident) {
        int i = desde;
        ident = ident.toUpperCase();
        //System.out.println("======= ESTAMOS EN EL BUSCAR BEAN =======");
        while (i >= hasta) {
            //System.out.println(identificadores[i].getNombre().toUpperCase());
            //System.out.println(ident);
            if ((identificadores[i].getNombre().toUpperCase().equals(ident))) {
                return i;
            }
            i--;
        }

        return -1;
    }

    public IdentificadorBean obtenerBean(int indice) {
        return identificadores[indice];
    }

}
