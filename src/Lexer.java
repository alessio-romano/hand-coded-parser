import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class Lexer {

    private static final boolean DEBUG = false;
    private int idSymTab;
    private SymbolTable symbolTable;
    private String buffer = "";
    private int beginLexem;
    private int forward;
    private File input;
    private int state;
    private FileReader fileReader;
    private boolean flag = true;

    public Lexer(){
        beginLexem = 0;
        state = 0;
        idSymTab = 0;
    }

    public Boolean initialize(String filePath) {
        //Verifica se il file di input esiste
        //In caso positivo, inizializza e riempie il buffer a partire dal file di input
        try {
            input = new File(filePath);
            fileReader = new FileReader(input);
            int i;
            while ((i = fileReader.read()) != -1) {
                buffer += (char) i;
            }
            buffer += "\0";
            if (DEBUG) System.out.println(buffer);

            //creo la Symbol Table
            symbolTable = new SymbolTable();

            // Inserimento delle parole chiave nella stringTable. Questa scelta è stata fatta
            // al fine di non costruire un diagramma di transizione per ogni parola chiave.
            // Le parole chiave verranno "catturate" dal diagramma di transizione e gestite e di conseguenza.
            int keywords[] = {sym.IF, sym.THEN, sym.ELSE, sym.DO, sym.WHILE, sym.INT, sym.FLOAT};

            for(int x: keywords) {
                symbolTable.add(idSymTab++, sym.tokens[x], new Token(x));
            }
            return true;
        }
        catch(IOException e){
            //il file non esiste
            return false;
        }
    }

    public Token nextToken(){
        //Ad ogni chiamata del lexer (nextToken()) si resettano tutte le variabili utilizzate
        int zeroCounter = 0; //E' utilizzato per contare gli zeri consecutivi dopo la virgola in modo
        //da assicurarsi che i numeri decimali non terminino con più zeri
        forward = beginLexem;

        if(DEBUG){//Stampe di controllo
            System.out.println("Dim. buffer: " + buffer.length());
            System.out.println("Valore forward: " + forward);
        }

        state = 0;
        String lessema = ""; //Corrisponde al lessema riconosciuto
        char c; //Carattere letto ad ogni iterazione

        //Utilizziamo una flag il cui scopo è quello di indicare l'eventuale raggiungimento
        //della fine del file, corrispondente al carattere '\0' all'interno della stringa
        while(flag){
            c = buffer.charAt(forward);
            if(c == '\0') {
                flag = false; //flag = false indica la fine del file
                if (DEBUG) System.out.println("Lexer: Ho raggiunto la fine del file");
            }
            forward++;

            if (DEBUG) { //stampe di controllo
                System.out.println("-----------------------");
                System.out.println("Carattere Letto: " + c);
                System.out.println("State: " + state);
                System.out.println("Forward: " + forward);
                System.out.println("Zerocounter:" + zeroCounter);
                System.out.println("-----------------------");
            }

            switch (state) {
                case 0:
                    //RELOP
                    if (c == '<') {
                        state = 1;
                    } else if (c == '=') {
                        state = 5;
                        beginLexem = forward;
                        return new Token(sym.EQ);
                    } else if (c == '>') {
                        state = 6;
                        //ID
                    } else if (Character.isLetter(c)) { //ex stato 9
                        lessema += c;
                        state = 10;
                        //WHITESPACE
                    } else if (Character.isWhitespace(c)) { //ex stato 22
                        state = 23;
                        //NUMBERS
                    } else if (Character.isDigit(c)) { //ex stato 12
                        if(c == '0'){
                            state = 12;
                        } else {
                            state = 13;
                        }
                        lessema += c;
                    }
                    //SEPARATORS
                    else if (c == ';') {
                        beginLexem = forward;
                        return new Token(sym.SEMICOLON);
                    } else if (c == ',') {
                        beginLexem = forward;
                        return new Token(sym.COMMA);
                    } else if (c == '(') {
                        beginLexem = forward;
                        return new Token(sym.LPAR);
                    } else if (c == ')') {
                        beginLexem = forward;
                        return new Token(sym.RPAR);
                    } else if (c == '{') {
                        beginLexem = forward;
                        return new Token(sym.LBRAC);
                    } else if (c == '}') {
                        beginLexem = forward;
                        return new Token(sym.RBRAC);
                    }
                    else if(c != '\0'){
                        beginLexem = forward;
                        lessema+=c;
                        return new Token(sym.ERROR,lessema);
                    }
                    break; //end case 0
                case 1:
                    if (c == '=') {
                        state = 2;
                        beginLexem = forward;
                        return new Token(sym.LE);
                    } else if (c == '>') {
                        state = 3;
                        beginLexem = forward;
                        return new Token(sym.NE);
                    } else if (c == '-') {
                        state = 25;
                    } else {
                        state = 4;
                        retrack();
                        return new Token(sym.LT);
                    } //end case 1
                    break;
                case 25:
                    if (c == '-') {
                        state = 26;
                        beginLexem = forward;
                        return new Token(sym.ASSIGN);
                    }
                case 6:
                    if (c == '=') {
                        state = 7;
                        beginLexem = forward;
                        return new Token(sym.GE);
                    } else {
                        state = 8;
                        retrack();
                        return new Token(sym.GT);
                    }
                    //ID
                case 10:
                    if (Character.isLetterOrDigit(c)) {
                        lessema += c;
                    } else {
                        state = 11;
                        retrack();
                        return installID(lessema);
                    }
                    break;
                //UNSIGNED NUMBERS
                case 12:
                    if(c == '.'){
                        state = 14;
                        lessema += c;
                    } else {
                        state = 20;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    }
                    break;
                case 13:
                    if (c == '.') {
                        state = 14;
                        lessema += c;
                    } else if(c == 'E' || c == 'e'){
                        state = 16;
                        lessema += c;
                    } else if (!Character.isDigit(c)) {
                        state = 20;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    } else { //stiamo ancora leggendo un numero
                        lessema += c;
                        //lo stato è sempre 13 quindi non va modificato
                    }
                    break;
                case 14:
                    if (Character.isDigit(c)) {
                        if(c != '0') {
                            state = 15;
                        }
                        else{
                            zeroCounter++;
                            state = 30;
                        }
                        lessema += c;
                    } else {
                        retrack(); //Viene utilizzato due volte il metodo retrack perché dopo il punto
                        retrack(); //ci sono zeri successivi seguiti da un carattere diverso da un digit
                        //ad esempio "50.00 "

                        //Avendo letto un carattere che non rispetta il pattern di alcun token,
                        //restituiamo l'ultimo token corretto prima del carattere "."
                        String x = lessema.substring(0,lessema.length()-1);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) {
                        if(c == '0') {
                            state = 31;
                            zeroCounter++;
                        }
                        lessema += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 16;
                        lessema += c;
                    } else {
                        state = 21;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    } else if (c == '+' || c == '-') {
                        state = 17;
                        lessema += c;
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    }
                    break;
                case 18:
                    if (!Character.isDigit(c)) {
                        state = 19;
                        retrack();
                        return new Token(sym.NUMBER, lessema);
                    } else { //sto leggendo ancora un numero
                        lessema += c;
                    }
                    break;
                case 30:
                    if(Character.isDigit(c)){
                        if(c != '0') {
                            state = 15;
                            zeroCounter = 0; //azzero perchè altrimenti considero anche zeri precedenti
                        }
                        else{
                            zeroCounter++;
                        }
                        lessema+=c;
                    }
                    else{
                        forward -= zeroCounter;
                        retrack(); //Viene utilizzato due volte il metodo retrack perché dopo il punto
                        retrack(); //viene letto un carattere diverso da un digit
                        String x = lessema.substring(0,lessema.length()-zeroCounter-1);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 31:
                    if(Character.isDigit(c)){
                        if(c != '0') {
                            state = 15;
                            zeroCounter = 0; //azzero perchè altrimenti considero anche zeri precedenti
                        }
                        else{
                            zeroCounter++;
                        }
                        lessema+=c;
                    }
                    else{
                        forward -= zeroCounter;
                        retrack();
                        String x = lessema.substring(0,lessema.length()-zeroCounter);
                        return new Token(sym.NUMBER, x);
                    }
                    break;
                case 23:
                    if (!Character.isWhitespace(c)) {
                        state = 0;
                        retrack();
                    }
                    //else {sto leggendo ancora un ws e quindi resto nello stato 23}
                    break;
                default:
                    break;
            } //end switch
        }//end while
        return new Token(sym.EOF);
        //return null;
    }//end method

    private Token installID(String lessema){
        Token token;
        //Verifico se il lessema riconosciuto è una parola chiave oppure un id già dichiarato
        token = symbolTable.contain(lessema);
        if(token != null){
            return token;
        } else { //se non lo è, allora è un nuovo ID
            token =  new Token(sym.ID, String.valueOf(idSymTab));
            symbolTable.add(idSymTab, lessema, token);
            idSymTab++;
            return token;
        }
    }

    private void retrack(){
        //fa il retract nel file di un carattere
        forward--;
        beginLexem = forward;
    }

    public void printStringTable(){
        System.out.println("\nSTRING TABLE");
        System.out.println("-----------------------------------------------");
        System.out.println(symbolTable);

    }
}// end class