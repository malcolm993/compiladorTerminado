/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

import java.io.IOException;

/**
 *
 * @author Yair
 */
public class AnalizadorSintactico {
//One ring to rule them all

    private AnalizadorLexico alex;
    private IndicadorError indError;
    private AnalizadorSemantico aSem;
    private GeneradorDeCodigo genCod;
    private int cantidadDeVariables;

    public AnalizadorSintactico(AnalizadorLexico alex, IndicadorError indError, AnalizadorSemantico aSem, GeneradorDeCodigo genCod) {
        this.alex = alex;
        this.indError = indError;
        this.aSem = aSem;
        this.genCod = genCod;
        cantidadDeVariables = 0;
    }

    public void analizar() throws IOException {
        intentoMaravilla();
        programa();
        if (alex.getSimbolo() == Terminal.EOF) {
            indError.mostrarError(0, alex.getCadena());
        } else {
            indError.mostrarError(1, alex.getCadena());
        }
    }

    private void programa() throws IOException {
        genCod.cargarByte(0xBF); // MOV EDI, 00 00 00 00 
        genCod.cargarInt(0);       // MOV EDI, 00 00 00 00

        bloque(0);
        if (alex.getSimbolo() == Terminal.PUNTO) {
            genCod.finalizarMemoria(cantidadDeVariables, alex.getNombreArchivo());
            intentoMaravilla();
        } else {
            indError.mostrarError(2, alex.getCadena());
        }
    }

    private void bloque(int base) throws IOException {
        genCod.cargarByte(0xE9); // E9 00 00 00 00
        genCod.cargarInt(0);       // E9 00 00 00 00 
        int origenBloque = genCod.getTopeMemoria();
        int desplazamiento = 0;
        int indice;
        if (alex.getSimbolo() == Terminal.CONST) {

            IdentificadorBean beanConstante = new IdentificadorBean();
            beanConstante.setTipo(alex.getSimbolo());
            intentoMaravilla();

            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                beanConstante.setNombre(alex.getCadena());
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }

            if (alex.getSimbolo() == Terminal.IGUAL || alex.getSimbolo() == Terminal.ASIGNACION_DE_VARIABLE) {
                intentoMaravilla();
            } else {
                indError.mostrarError(4, alex.getCadena());
            }

            if (alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.NUMERO) {
                    beanConstante.setValor(Integer.parseInt(alex.getCadena()) * (-1));

                    indice = aSem.buscarBean(base + desplazamiento - 1, base, beanConstante.getNombre());
                    if (indice == -1) {
                        aSem.agregarBean(base + desplazamiento, beanConstante);
                        desplazamiento++;
                    } else {
                        indError.mostrarError(23, alex.getCadena());
                    }
                    intentoMaravilla();
                } else {
                    indError.mostrarError(5, alex.getCadena());
                }
            } else if (alex.getSimbolo() == Terminal.NUMERO) {
                beanConstante.setValor(Integer.parseInt(alex.getCadena()));

                indice = aSem.buscarBean(base + desplazamiento - 1, base, beanConstante.getNombre());
                if (indice == -1) {
                    aSem.agregarBean(base + desplazamiento, beanConstante);
                    desplazamiento++;
                } else {
                    indError.mostrarError(23, alex.getCadena());
                }

                intentoMaravilla();
            } else {
                indError.mostrarError(5, alex.getCadena());
            }
            while (alex.getSimbolo() == Terminal.COMA) {
                IdentificadorBean beanConstanteWhile = new IdentificadorBean();
                beanConstanteWhile.setTipo(Terminal.CONST);
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    beanConstanteWhile.setNombre(alex.getCadena());
                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                if (alex.getSimbolo() == Terminal.IGUAL || alex.getSimbolo() == Terminal.ASIGNACION_DE_VARIABLE) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(4, alex.getCadena());
                }

                if (alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.NUMERO) {
                        beanConstanteWhile.setValor(Integer.parseInt(alex.getCadena()) * (-1));
                        indice = aSem.buscarBean(base + desplazamiento - 1, base, beanConstanteWhile.getNombre());
                        if (indice == -1) {
                            aSem.agregarBean(base + desplazamiento, beanConstanteWhile);
                            desplazamiento++;
                        } else {
                            indError.mostrarError(23, alex.getCadena());
                        }
                        intentoMaravilla();
                    } else {
                        indError.mostrarError(5, alex.getCadena());
                    }
                } else if (alex.getSimbolo() == Terminal.NUMERO) {
                    beanConstanteWhile.setValor(Integer.parseInt(alex.getCadena()));
                    indice = aSem.buscarBean(base + desplazamiento - 1, base, beanConstanteWhile.getNombre());
                    if (indice == -1) {
                        aSem.agregarBean(base + desplazamiento, beanConstanteWhile);
                        desplazamiento++;
                    } else {
                        indError.mostrarError(23, alex.getCadena());
                    }
                    intentoMaravilla();
                } else {
                    indError.mostrarError(5, alex.getCadena());
                }
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        if (alex.getSimbolo() == Terminal.VAR) {
            IdentificadorBean beanVariable = new IdentificadorBean();
            beanVariable.setTipo(alex.getSimbolo());
            intentoMaravilla();
            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                beanVariable.setNombre(alex.getCadena());
                beanVariable.setValor(cantidadDeVariables * 4);
                indice = aSem.buscarBean(base + desplazamiento - 1, base, beanVariable.getNombre());
                if (indice == -1) {
                    aSem.agregarBean(base + desplazamiento, beanVariable);
                    desplazamiento++;
                    cantidadDeVariables++;
                } else {
                    indError.mostrarError(23, alex.getCadena());
                }
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }
            while (alex.getSimbolo() == Terminal.COMA) {
                IdentificadorBean beanVariableWhile = new IdentificadorBean();
                beanVariableWhile.setTipo(Terminal.VAR);
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    beanVariableWhile.setNombre(alex.getCadena());
                    beanVariableWhile.setValor(cantidadDeVariables * 4);
                    indice = aSem.buscarBean(base + desplazamiento - 1, base, beanVariableWhile.getNombre());
                    if (indice == -1) {
                        aSem.agregarBean(base + desplazamiento, beanVariableWhile);
                        desplazamiento++;
                        cantidadDeVariables++;
                    } else {
                        indError.mostrarError(23, alex.getCadena());
                    }

                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        while (alex.getSimbolo() == Terminal.PROCEDURE) {
            IdentificadorBean procedureBean = new IdentificadorBean();
            procedureBean.setTipo(Terminal.PROCEDURE);
            intentoMaravilla();
            if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                procedureBean.setNombre(alex.getCadena());
                procedureBean.setValor(genCod.getTopeMemoria());
                indice = aSem.buscarBean(base + desplazamiento - 1, base, procedureBean.getNombre());
                if (indice == -1) {
                    aSem.agregarBean(base + desplazamiento, procedureBean);
                    desplazamiento++;
                } else {
                    indError.mostrarError(23, alex.getCadena());
                }
                intentoMaravilla();
            } else {
                indError.mostrarError(3, alex.getCadena());
            }
            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
            bloque(base + desplazamiento);
            genCod.cargarByte(0xC3); // esto es un RET, genial

            if (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                intentoMaravilla();
            } else {
                indError.mostrarError(6, alex.getCadena());
            }
        }
        //ACA HAY QUE SALTAR
        int destinoBloque = genCod.getTopeMemoria();
        genCod.cargarIntEn(destinoBloque - origenBloque, origenBloque - 4);
        proposicion(base, desplazamiento);
    }

    private void proposicion(int base, int desplazamiento) throws IOException {
        int indice;
        switch (alex.getSimbolo()) {
            case IDENTIFICADOR:
                indice = aSem.buscarBean(base + desplazamiento - 1, 0, alex.getCadena());

                IdentificadorBean ident = aSem.obtenerBean(indice);
                if (indice == -1) {
                    indError.mostrarError(24, alex.getCadena());
                }
                intentoMaravilla();

                if (alex.getSimbolo() == Terminal.ASIGNACION_DE_VARIABLE || alex.getSimbolo() == Terminal.IGUAL) {
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); //OBTENGO EL RESULTADO DE EXPRESION QUE ESTA EN LA PILA, SE LO MANDO A EAX
                    genCod.cargarByte(0x89); // MOV EDI+0000, EAX
                    genCod.cargarByte(0x87); // MOV EDI+0000, EAX
                    genCod.cargarInt(ident.getValor());// LE MANDAMOS A EDI+000X EL VALOR QUE SACAMOS DE LA PILA EN EAX
                } else {
                    indError.mostrarError(7, alex.getCadena());
                }
                break;
            case CALL:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    indice = aSem.buscarBean(base + desplazamiento - 1, 0, alex.getCadena());

                    if (indice == -1) {
                        indError.mostrarError(24, alex.getCadena());
                    }

                    IdentificadorBean beanProc = aSem.obtenerBean(indice);
                    if (beanProc.getTipo() != Terminal.PROCEDURE) {
                        indError.mostrarError(26, alex.getCadena());
                    }

                    int aux12 = beanProc.getValor() - (genCod.getTopeMemoria() + 5);
                    genCod.cargarByte(0xE8); // CALL EL PROCEDURE
                    genCod.cargarInt(aux12);   // CALL LA POSICION DEL PROCEDURE

                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                break;
            case BEGIN:
                intentoMaravilla();
                proposicion(base, desplazamiento);
                while (alex.getSimbolo() == Terminal.PUNTO_Y_COMA) {
                    intentoMaravilla();
                    proposicion(base, desplazamiento);
                }
                if (alex.getSimbolo() == Terminal.END) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(8, alex.getCadena());
                }
                break;
            case IF:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.NOT) {
                    intentoMaravilla();
                    condicionNOT(base, desplazamiento);
                } else {
                    condicion(base, desplazamiento);
                }
                int posicion = genCod.getTopeMemoria();

                if (alex.getSimbolo() == Terminal.THEN) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(9, alex.getCadena());
                }
                proposicion(base, desplazamiento);
                int destino = genCod.getTopeMemoria();

                genCod.cargarIntEn(destino - posicion, posicion - 4); // FIX UP
                // CHE, VOLVE A DESPUES DEL E9 Y CAMBIALE LA POSICION DEL SALTO
                //FIX UP A LA CONDICION, SI LA CONDICION SE CUMPLE, SE REALIZA EL THEN. SALTANDOSE EL SALTO PARA SALIR DEL IF
                // CASO CONTRARIO, SI LA CONDICION NO SE CUMPLE SALTA AL FINAL DEL IF. ACÁ SE REALIZA EL FIX UP PARA DECIRLE A DONDE SALIR
                break;
            case WHILE:
                intentoMaravilla();

                int destinoWhile = genCod.getTopeMemoria(); // POSICION ANTES DE LA CONDICION

                condicion(base, desplazamiento);

                int posicionWhile = genCod.getTopeMemoria(); // TIENE EL E9 00 00 00 00
                if (alex.getSimbolo() == Terminal.DO) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(10, alex.getCadena());
                }
                proposicion(base, desplazamiento);
                int despuesProposicion = genCod.getTopeMemoria(); // FIN DEL WHILE

                int distancia = destinoWhile - (despuesProposicion + 5); //salto hacia atras para reevaluar la condicion 

                genCod.cargarByte(0xE9);  // SALTO A VOLVER A EVALUAR LA CONDICION 
                genCod.cargarInt(distancia);// 

                genCod.cargarIntEn((despuesProposicion + 5) - posicionWhile, posicionWhile - 4); // FIX UP E9 
                //genCod.cargarIntEn(despuesProposicion - posicionWhile, posicionWhile-4);
                break;
            case READLN:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(11, alex.getCadena());
                }
                if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                    indice = aSem.buscarBean(base + desplazamiento - 1, 0, alex.getCadena());
                    IdentificadorBean readlnBean = aSem.obtenerBean(indice);
                    if (indice == -1) {
                        indError.mostrarError(24, alex.getCadena());
                    } else if (readlnBean.getTipo() == Terminal.PROCEDURE) {
                        indError.mostrarError(25, alex.getCadena());
                    }

                    genCod.cargarByte(0xE8); // CALL 
                    int distanciaEntradaEntero = 1424 - (genCod.getTopeMemoria() + 4); // SE HACE EL CALCULO DE LA DISTANCIA A LA SUBRUTINA
                    genCod.cargarInt(distanciaEntradaEntero); // CALL SUBRUTINA

                    genCod.cargarByte(0x89); // MOV EDI+0000, EAX
                    genCod.cargarByte(0x87); // MOV EDI+0000, EAX
                    genCod.cargarInt(readlnBean.getValor()); // MOV EDI+GETVALOR, EAX

                    intentoMaravilla();
                } else {
                    indError.mostrarError(3, alex.getCadena());
                }
                while (alex.getSimbolo() == Terminal.COMA) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.IDENTIFICADOR) {
                        indice = aSem.buscarBean(base + desplazamiento - 1, 0, alex.getCadena());
                        IdentificadorBean readlnWhileBean = aSem.obtenerBean(indice);

                        if (indice == -1) {
                            indError.mostrarError(24, alex.getCadena());
                        } else if (readlnWhileBean.getTipo() == Terminal.PROCEDURE) {
                            indError.mostrarError(25, alex.getCadena());
                        }

                        genCod.cargarByte(0xE8);
                        int distanciaEntradaEnteroWhile = 1424 - (genCod.getTopeMemoria() + 4);
                        genCod.cargarInt(distanciaEntradaEnteroWhile);
                        genCod.cargarByte(0x89);
                        genCod.cargarByte(0x87);
                        genCod.cargarInt(readlnWhileBean.getValor());

                        intentoMaravilla();
                    } else {
                        indError.mostrarError(3, alex.getCadena());
                    }
                }
                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(12, alex.getCadena());
                }
                break;
            case WRITELN:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                        int ubicacionCadena = genCod.descargarIntDe(204) + genCod.descargarIntDe(212)
                                - genCod.getTAMANO_HEADER() + genCod.getTopeMemoria() + 15;

                        genCod.cargarByte(0xB8); //MOV EAX , XXXXX
                        genCod.cargarInt(ubicacionCadena);//UBICACION  

                        int rutinaMostrarCadena = 992 - (genCod.getTopeMemoria() + 5);//UBICACION RUTNA
                        genCod.cargarByte(0xE8);//2.CALL A LA RUTINA
                        genCod.cargarInt(rutinaMostrarCadena);// PASO UBICACION DE LA RUTINA
                        genCod.cargarByte(0xE9);//3. SE GENERA SALTO INCONDICIONAL
                        genCod.cargarInt(0);

                        // 4. SE GENERAN LOS BYTES DE LA CADENA 
                        String cadena = alex.getCadena().substring(1, alex.getCadena().length() - 1);
                        int inicioCadena = genCod.getTopeMemoria();

                        for (char caracter : cadena.toCharArray()) {
                            genCod.cargarByte((byte) caracter);
                        }

                        genCod.cargarByte(0x00);
                        int finalCadena = genCod.getTopeMemoria();

                        //5. SE GENERA UN FIX UP DEL PASO 3
                        intentoMaravilla();
                        int saltosParaHacer = finalCadena - inicioCadena;
                        genCod.cargarIntEn(saltosParaHacer, inicioCadena - 4);

                    } else {
                        expresion(base, desplazamiento);
                        genCod.cargarByte(0x58);
                        int mostrarEntero = 1056 - genCod.getTopeMemoria() - 5;
                        genCod.cargarByte(0xE8);
                        genCod.cargarInt(mostrarEntero);//mostramos el valor por pantalla del entero ingresado

                    }
                    while (alex.getSimbolo() == Terminal.COMA) {
                        intentoMaravilla();
                        if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                            int ubicacionCadena = genCod.descargarIntDe(204) + genCod.descargarIntDe(212)
                                    - genCod.getTAMANO_HEADER() + genCod.getTopeMemoria() + 15;

                            genCod.cargarByte(0xB8); //MOV EAX , XXXXX
                            genCod.cargarInt(ubicacionCadena);//UBICACION  

                            int rutinaMostrarCadena = 992 - (genCod.getTopeMemoria() + 5);//UBICACION RUTNA
                            genCod.cargarByte(0xE8);//2.CALL A LA RUTINA
                            genCod.cargarInt(rutinaMostrarCadena);// PASO UBICACION DE LA RUTINA
                            genCod.cargarByte(0xE9);//3. SE GENERA SALTO INCONDICIONAL
                            genCod.cargarInt(0);

                            // 4. SE GENERAN LOS BYTES DE LA CADENA 
                            String cadena = alex.getCadena().substring(1, alex.getCadena().length() - 1);
                            int inicioCadena = genCod.getTopeMemoria();

                            for (char caracter : cadena.toCharArray()) {
                                genCod.cargarByte((byte) caracter);
                            }

                            genCod.cargarByte(0x00);
                            int finalCadena = genCod.getTopeMemoria();

                            //5. SE GENERA UN FIX UP DEL PASO 3
                            intentoMaravilla();
                            int saltosParaHacer = finalCadena - inicioCadena;
                            genCod.cargarIntEn(saltosParaHacer, inicioCadena - 4);

                        } else {
                            expresion(base, desplazamiento);
                            genCod.cargarByte(0x58); // POP EAX
                            int mostrarEntero = 1056 - genCod.getTopeMemoria() - 5;
                            genCod.cargarByte(0xE8);
                            genCod.cargarInt(mostrarEntero);//mostramos el valor por pantalla del entero ingresado
                        }
                    }
                    if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                        intentoMaravilla();
                    } else {
                        indError.mostrarError(12, alex.getCadena());
                    }
                }
                int distanciaSubrutina = genCod.getTopeMemoria() + 5;
                genCod.cargarByte(0xE8);
                genCod.cargarInt(1040 - distanciaSubrutina);
                break;
            case WRITE:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                }
                if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                    //1. se inicializa EAX con la posicion absoluta de la cadena
                    int ubicacionCadena = genCod.descargarIntDe(204) + genCod.descargarIntDe(212)
                            - genCod.getTAMANO_HEADER() + genCod.getTopeMemoria() + 15;
                    genCod.cargarByte(0xB8); //MOV EAX , XXXXX
                    genCod.cargarInt(ubicacionCadena);//UBICACION +15 POR EJEMPLO 

                    int rutinaMostrarCadena = 992 - (genCod.getTopeMemoria() + 5);//UBICACION RUTNA

                    genCod.cargarByte(0xE8);//2.CALL A LA RUTINA
                    genCod.cargarInt(rutinaMostrarCadena);// PASO UBICACION DE LA RUTINA
                    genCod.cargarByte(0xE9);//3. SE GENERA SALTO INCONDICIONAL
                    genCod.cargarInt(0);

                    // 4. SE GENERAN LOS BYTES DE LA CADENA 
                    String cadena = alex.getCadena().substring(1, alex.getCadena().length() - 1);
                    int inicioCadena = genCod.getTopeMemoria();

                    for (char caracter : cadena.toCharArray()) {
                        genCod.cargarByte((byte) caracter);
                    }

                    genCod.cargarByte(0x00);
                    int finalCadena = genCod.getTopeMemoria();

                    //5. SE GENERA UN FIX UP DEL PASO 3
                    intentoMaravilla();
                    int saltosParaHacer = finalCadena - inicioCadena;
                    genCod.cargarIntEn(saltosParaHacer, inicioCadena - 4);

                } else {
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    int mostrarEntero = 1056 - genCod.getTopeMemoria() - 5;
                    genCod.cargarByte(0xE8);
                    genCod.cargarInt(mostrarEntero);//mostramos el valor por pantalla del entero ingresado
                }

                while (alex.getSimbolo() == Terminal.COMA) {
                    intentoMaravilla();
                    if (alex.getSimbolo() == Terminal.CADENA_LITERAL) {
                        //1. se inicializa EAX con la posicion absoluta de la cadena
                        int ubicacionCadena = genCod.descargarIntDe(204) + genCod.descargarIntDe(212)
                                - genCod.getTAMANO_HEADER() + genCod.getTopeMemoria() + 15;
                        genCod.cargarByte(0xB8); //MOV EAX , XXXXX
                        genCod.cargarInt(ubicacionCadena);//UBICACION +15 POR EJEMPLO 

                        int rutinaMostrarCadena = 992 - (genCod.getTopeMemoria() + 5);//UBICACION RUTNA

                        genCod.cargarByte(0xE8);//2.CALL A LA RUTINA
                        genCod.cargarInt(rutinaMostrarCadena);// PASO UBICACION DE LA RUTINA
                        genCod.cargarByte(0xE9);//3. SE GENERA SALTO INCONDICIONAL
                        genCod.cargarInt(0);

                        // 4. SE GENERAN LOS BYTES DE LA CADENA 
                        String cadena = alex.getCadena().substring(1, alex.getCadena().length() - 1);
                        int inicioCadena = genCod.getTopeMemoria();

                        for (char caracter : cadena.toCharArray()) {
                            genCod.cargarByte((byte) caracter);
                        }

                        genCod.cargarByte(0x00);
                        int finalCadena = genCod.getTopeMemoria();

                        //5. SE GENERA UN FIX UP DEL PASO 3
                        intentoMaravilla();
                        int saltosParaHacer = finalCadena - inicioCadena;
                        genCod.cargarIntEn(saltosParaHacer, inicioCadena - 4);
                    } else {
                        expresion(base, desplazamiento);
                        genCod.cargarByte(0x58);
                        int mostrarEntero = 1056 - genCod.getTopeMemoria() - 5;
                        genCod.cargarByte(0xE8);
                        genCod.cargarInt(mostrarEntero);//mostramos el valor por pantalla del entero ingresado
                    }
                }
                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(12, alex.getCadena());
                }
                break;
            case HALT:
                int posicionHalt = 1416 - (genCod.getTopeMemoria() + 5); // +5 PORQUE ES DESPUES DEL E9
                genCod.cargarByte(0xE9);
                genCod.cargarInt(posicionHalt);
                //System.out.println("Se codifico correctamente el halt");
                intentoMaravilla();
                break;
        }
    }

    private void expresion(int base, int desplazamiento) throws IOException {
        boolean esPositivo = true;
        if (alex.getSimbolo() == Terminal.OPERADOR_SUMA) {
            esPositivo = true;
            intentoMaravilla();
        } else if (alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
            esPositivo = false;
            intentoMaravilla();
        }
        termino(base, desplazamiento);
        if (!esPositivo) {
            genCod.cargarByte(0x58); // POP EAX
            genCod.cargarByte(0xF7); // NEG EAX
            genCod.cargarByte(0xD8); // NEG EAX
            genCod.cargarByte(0x50); // PUSH EAX
        }

        while (alex.getSimbolo() == Terminal.OPERADOR_SUMA || alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
            boolean operadorFlag = false;
            if (alex.getSimbolo() == Terminal.OPERADOR_SUMA) {
                operadorFlag = true;
            } else if (alex.getSimbolo() == Terminal.OPERADOR_RESTA) {
                operadorFlag = false;
            }
            intentoMaravilla();
            termino(base, desplazamiento);

            if (operadorFlag) {
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0x01); // ADD EAX, EBX
                genCod.cargarByte(0xD8); // ADD EAX, EBX
                genCod.cargarByte(0x50); // PUSH EAX
            } else {
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0x93); // XCHG EAX, EBX
                genCod.cargarByte(0x29); // SUB EAX, EBX
                genCod.cargarByte(0xD8); // SUB EAX, EBX
                genCod.cargarByte(0x50); // PUSH EAX
            }
        }
    }

    private void condicion(int base, int desplazamiento) throws IOException {
        if (alex.getSimbolo() == Terminal.ODD) {
            intentoMaravilla();
            expresion(base, desplazamiento);
            //58 A8 01 7B 05 E9 00 00 00 00.
            genCod.cargarByte(0x58); // POP EAX 
            genCod.cargarByte(0xA8); // TEST AL, 01
            genCod.cargarByte(0x01); // TEST AL, 01 
            genCod.cargarByte(0x7B); // JPO SALTAS EL SALTO DEL IF
            genCod.cargarByte(0x05); // JPO SALTAS EL SALTO DEL IF
            genCod.cargarByte(0xE9); // JMP FUERA DEL IF
            genCod.cargarInt(0);
        } else {
            expresion(base, desplazamiento);
            switch (alex.getSimbolo()) {
                case IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX 
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x74); // JE 
                    genCod.cargarByte(0x05); // JE
                    break;
                case DISTINTO:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58);
                    genCod.cargarByte(0x5B);
                    genCod.cargarByte(0x39);
                    genCod.cargarByte(0xC3);
                    genCod.cargarByte(0x75);
                    genCod.cargarByte(0x05);
                    break;
                case MENOR:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58);
                    genCod.cargarByte(0x5B);
                    genCod.cargarByte(0x39);
                    genCod.cargarByte(0xC3);
                    genCod.cargarByte(0x7C);
                    genCod.cargarByte(0x05);
                    break;
                case MENOR_O_IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58);
                    genCod.cargarByte(0x5B);
                    genCod.cargarByte(0x39);
                    genCod.cargarByte(0xC3);
                    genCod.cargarByte(0x7E);
                    genCod.cargarByte(0x05);
                    break;
                case MAYOR:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58);
                    genCod.cargarByte(0x5B);
                    genCod.cargarByte(0x39);
                    genCod.cargarByte(0xC3);
                    genCod.cargarByte(0x7F);
                    genCod.cargarByte(0x05);
                    break;
                case MAYOR_O_IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58);
                    genCod.cargarByte(0x5B);
                    genCod.cargarByte(0x39);
                    genCod.cargarByte(0xC3);
                    genCod.cargarByte(0x7D);
                    genCod.cargarByte(0x05);
                    break;
                default:
                    indError.mostrarError(19, alex.getCadena());
            }
            genCod.cargarByte(0xE9); // E9 00 00 00 00
            genCod.cargarInt(0);       // E9 00 00 00 00
        }
    }

    private void condicionNOT(int base, int desplazamiento) throws IOException {
        if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
            intentoMaravilla();
        } else {
            indError.mostrarError(11, alex.getCadena());
        }
        if (alex.getSimbolo() == Terminal.ODD) {
            intentoMaravilla();
            expresion(base, desplazamiento);
            //58 A8 01 7B 05 E9 00 00 00 00.
            genCod.cargarByte(0x58); // POP EAX 
            genCod.cargarByte(0xA8); // TEST AL, 00
            genCod.cargarByte(0x00); // TEST AL, 00 
            genCod.cargarByte(0x7B); // JPO SALTAS EL SALTO DEL IF
            genCod.cargarByte(0x05); // JPO SALTAS EL SALTO DEL IF
            genCod.cargarByte(0xE9); // JMP FUERA DEL IF
            genCod.cargarInt(0);
        } else {
            expresion(base, desplazamiento);
            switch (alex.getSimbolo()) {
                case IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX 
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x75); // JE 
                    genCod.cargarByte(0x05); // JE
                    break;
                case DISTINTO:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX 
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x74);
                    genCod.cargarByte(0x05);
                    break;
                case MENOR:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x7D);
                    genCod.cargarByte(0x05);
                    break;
                case MENOR_O_IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x7F);
                    genCod.cargarByte(0x05);
                    break;
                case MAYOR:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x7E);
                    genCod.cargarByte(0x05);
                    break;
                case MAYOR_O_IGUAL:
                    intentoMaravilla();
                    expresion(base, desplazamiento);
                    genCod.cargarByte(0x58); // POP EAX
                    genCod.cargarByte(0x5B); // POP EBX
                    genCod.cargarByte(0x39); // CMP EBX, EAX
                    genCod.cargarByte(0xC3); // CMP EBX, EAX
                    genCod.cargarByte(0x7C);
                    genCod.cargarByte(0x05);
                    break;
                default:
                    indError.mostrarError(19, alex.getCadena());
            }

            if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                intentoMaravilla();
            } else {
                indError.mostrarError(22, alex.getCadena());
            }
            genCod.cargarByte(0xE9); // E9 00 00 00 00
            genCod.cargarInt(0);       // E9 00 00 00 00
        }
    }

    private void termino(int base, int desplazamiento) throws IOException {
        factor(base, desplazamiento);

        while (alex.getSimbolo() == Terminal.OPERADOR_MULTIPLICACION || alex.getSimbolo() == Terminal.OPERADOR_DIVISION) {
            boolean esMultiplicacion = true;

            if (alex.getSimbolo() == Terminal.OPERADOR_MULTIPLICACION) {
                esMultiplicacion = true;
            } else if (alex.getSimbolo() == Terminal.OPERADOR_DIVISION) {
                esMultiplicacion = false;
            }
            intentoMaravilla();
            factor(base, desplazamiento);
            if (esMultiplicacion) {
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0xF7); // IMUL EBX
                genCod.cargarByte(0xEB); // IMUL EBX
                genCod.cargarByte(0x50); // PUSH EAX
            } else if (!esMultiplicacion) {
                genCod.cargarByte(0x58); // POP EAX
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0x93); // XCHNG 
                genCod.cargarByte(0x99); // CDQ
                genCod.cargarByte(0xF7); // IDIV EBX
                genCod.cargarByte(0xFB); // IDIV EBX
                genCod.cargarByte(0x50); // PUSH EAX
            }
        }
    }

    private void factor(int base, int desplazamiento) throws IOException {
        switch (alex.getSimbolo()) {
            case IDENTIFICADOR:
                IdentificadorBean beanFactor;
                int indice = aSem.buscarBean(base + desplazamiento - 1, 0, alex.getCadena());
                //System.out.println(alex.getCadena());
                beanFactor = aSem.obtenerBean(indice);

                if (beanFactor == null) {
                    indError.mostrarError(24, alex.getCadena());
                }

                if (beanFactor.getTipo().equals(Terminal.VAR)) {
                    // SI ES VAR HACERLO DE ESTA FORMA
                    genCod.cargarByte(0x8B); // MOV EAX
                    genCod.cargarByte(0x87); // MOV EAX, EDI+
                    genCod.cargarInt(beanFactor.getValor()); // MOV EAX, EDI+0000
//                    System.out.println(beanFactor.getValor());
                    genCod.cargarByte(0x50); // PUSH EAX
                } else if (beanFactor.getTipo().equals(Terminal.CONST)) {
                    // SI ES CONST HACERLO DE LA OTRA FORMA
                    genCod.cargarByte(0xB8); // MOV EAX, UN VALOR
                    genCod.cargarInt(beanFactor.getValor()); // MOV EAX, VALOR CONST
//                    System.out.println(beanFactor.getValor());
                    genCod.cargarByte(0x50); // PUSH EAX
                } else {
                    indError.mostrarError(25, alex.getCadena());
                }
                intentoMaravilla();
                break;
            case NUMERO:
                genCod.cargarByte(0xB8); // MOV EAX, UN VALOR
                genCod.cargarInt(Integer.parseInt(alex.getCadena())); // MOV EAX, VALOR LITERAL
                genCod.cargarByte(0x50); // PUSH EAX
                intentoMaravilla();
                break;
            case APERTURA_PARENTESIS:
                intentoMaravilla();
                expresion(base, desplazamiento);
                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(22, alex.getCadena());
                }
                break;
            case SQR:
                intentoMaravilla();
                if (alex.getSimbolo() == Terminal.APERTURA_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(11, alex.getCadena());
                }
                expresion(base, desplazamiento); // dejas en el tope de la pila el valor del registro EAX

                if (alex.getSimbolo() == Terminal.CIERRE_PARENTESIS) {
                    intentoMaravilla();
                } else {
                    indError.mostrarError(12, alex.getCadena());
                }

                // LEVANTAMOS EL VALOR DE LA PILA
                // QUEREMOS UN NUMERO AL CUADRADO
                // ESO SIGNIFICA N X N, 2X2
                // ENTONCES QUEREMOS MULTIPLICAR EL MISMO VALOR POR EL MISMO VALOR
                // PARA ELLO PODEMOS USAR IMUL QUE MULTIPLICA EAX POR EBX
                // PARA ELLO NECESITAMOS TENER EL MISMO VALOR EN EAX Y EN EBX
                //genCod.cargarByte(0x58); // POP EAX
                //genCod.cargarByte(0x93); // XCHG EAX, EBX así pasamos el valor levantado de la pila y lo ponemos en ebx
                genCod.cargarByte(0x5B); // POP EBX
                genCod.cargarByte(0xB8); // YA CON EL VALOR EN EBX FORZAMOS A EAX A QUE SEA 0
                genCod.cargarInt(0);       // B8 00 00 00 00 es MOV EAX, 00 00 00 00  
                genCod.cargarByte(0x01); // 01 D8 SUMA EAX Y EBX Y LO PONE EN EL PRIMER OPERANDO 
                genCod.cargarByte(0xD8); // ENTONCES EAX + EBX = 0 + UN VALOR = PONER UN VALOR EN EAX
                genCod.cargarByte(0xF7); // IMUL EAX, EBX 
                genCod.cargarByte(0xE8); // IMUL EAX, EBX
                genCod.cargarByte(0x50); // PONEMOS EL VALOR EN LA PILA CON PUSH EAX
                break;
            default:
                indError.mostrarError(20, alex.getCadena());
        }
    }

    private void intentoMaravilla() throws IOException {
        do {
            alex.escanear();
        } while (alex.getCadena().isBlank());
    }
}
