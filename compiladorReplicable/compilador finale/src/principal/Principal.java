package principal;

import java.io.IOException;

public class Principal {

    public static void main(String[] args) throws IOException {
        IndicadorError indError = new IndicadorError();
        AnalizadorLexico alex = new AnalizadorLexico("recu-28-11.PL0");
        AnalizadorSemantico anSem = new AnalizadorSemantico();
        GeneradorDeCodigo genCod = new GeneradorDeCodigo();
        AnalizadorSintactico aSint = new AnalizadorSintactico(alex, indError, anSem, genCod);

        aSint.analizar();
    }
}
