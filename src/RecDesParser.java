/*
* Implementazione di un Parser a Discesa Ricorsiva
* secondo la seguente Grammatica G = (N, T, S, P)
*
* N = {Program, Stmt, Expr, Program1, Expr1, Expr2, Relop}
* T = {ID, IF, THEN, ELSE, LT, GT, LE, GE, NE, EQ, NUMBER, ;, ASSIGN, WHILE, DO, EOF}
* S
* P = {
*       S -> Program EOF
*
*       Program -> Stmt Program1
*       Program1 -> ; Stmt Program1 | ɛ
*
*       Stmt -> IF Expr THEN Stmt ELSE Stmt | ID ASSIGN Expr | DO Stmt WHILE Expr
*
*       Expr -> Expr2 Expr1
*       Expr1 -> Relop Expr2 Expr1 | ɛ
*       Expr2 -> ID | NUMBER
*
*       Relop -> LT | GT | LE | GE | NE | EQ
*     }
* */

public class RecDesParser {

    private static final boolean DEBUG = false;
    private Lexer lexer; //istanza dell'analizzatore lessicale
    private Token currentToken; //il token corrente che si sta analizzando
    private int operators[] = {sym.LE, sym.LT, sym.GE, sym.GT, sym.EQ, sym.NE};

    public RecDesParser(String filePath){
        lexer = new Lexer();
        lexer.initialize(filePath);
    }

    public void printStringTable(){
        lexer.printStringTable();
    }

    public boolean S(){
        if(DEBUG) System.out.println("sono in S");
        currentToken = lexer.nextToken();

        if(!Program()){
            return false;
        } else {
            if(!EOF()){
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean Program(){
        if(DEBUG) System.out.println("sono in program");

        if(!Stmt()){
            return false;
        } else {//non avanzo al prossimo token perché lo ha già fatto Stmt()
            if(!Program1()){
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean Program1(){
        if(DEBUG) System.out.println("sono in program1 e il current token è: " + currentToken);
        if(!currentToken.sameTokenType(sym.SEMICOLON)){
            //se non ho letto il ";" allora potrebbe essere la produzione Program1 -> ɛ
            return true;
        } else { //è stato letto ";"
            if(DEBUG) System.out.println("Program1: Ho letto SEMICOLON");
            currentToken = lexer.nextToken();
            if(DEBUG) System.out.println("Program1: Dopo SEMICOLON il current token è: " + currentToken);
            if(!Stmt()){
                if(DEBUG) System.out.println("Non ho letto uno Stmt() dopo \";\"");
                return false;
            } else {//ho letto uno Stmt()
                if(!Program1()){ //Program1 -> ; Stmt Program1
                    return false;
                }
                return true;
            }
        }
    }

    public boolean Stmt(){
        if(DEBUG) System.out.println("sono in statement e il current token è: " + currentToken);

        if(!currentToken.sameTokenType(sym.IF)){
            //se non è IF, potrebbe essere la produzione Stmt -> ID ASSIGN Expr
            if(!currentToken.sameTokenType(sym.ID)){
                //se non è ID, potrebbe essere la produzione Stmt -> DO Stmt WHILE Expr
                if(!currentToken.sameTokenType(sym.DO)){
                    return false;
                } else {//ho letto do
                    if(DEBUG) System.out.println("Stmt: DO");
                    currentToken = lexer.nextToken();
                    if(!Stmt()){
                        return false;
                    } else { //ho letto Stmt() ma non avanzo al prossimo token perché se ne occupa un altro caso
                        if(!currentToken.sameTokenType(sym.WHILE)){
                            return false;
                        } else {//ho letto while
                            if(DEBUG) System.out.println("Stmt: WHILE");
                            currentToken = lexer.nextToken();
                            if(!Expr()){
                                return false;
                            } else {//ho letto Expr() e non avanzo al prossimo token perché lo ha già fatto Expr()
                                return true;
                            }
                        }
                    }
                } //fine della produzione Stmt -> DO Stmt WHILE Expr

            } else { //Stmt -> ID ASSIGN Expr
                if(DEBUG) System.out.println("Stmt: ID");
                currentToken = lexer.nextToken();
                if(!currentToken.sameTokenType(sym.ASSIGN)){
                    return false;
                } else { //ho letto ASSIGN (ossia <-- )
                    if(DEBUG) System.out.println("Stmt: ASSIGN e il current token è: " + currentToken);
                    currentToken = lexer.nextToken();
                    if(DEBUG) System.out.println("token corrente dopo che il caso ASSIGN ha richiesto il next: " + currentToken);
                    if(!Expr()){
                        return false;
                    } else {//ho letto Expr() e non faccio avanzare al prossimo token perché lo ha già fatto Expr()
                        return true;
                    }
                }
            } //fine della Produzione Stmt -> ID ASSIGN Expr

        } else { //ho letto IF e quindi vado al prossimo token Stmt -> IF Expr THEN Stmt ELSE Stmt
            if(DEBUG) System.out.println("Stmt: IF");
            currentToken = lexer.nextToken();
            if(!Expr()){
                if(DEBUG) System.out.println("Stmt: NON ho letto Expr() dopo IF");
                return false;
            } else {//ho letto Expr() ma non avanzo al prossimo token perché l'ha già fatto Expr()
                if(DEBUG) System.out.println("Stmt: Ho letto Expr() dopo IF");
                if(!currentToken.sameTokenType(sym.THEN)){
                    if(DEBUG) System.out.println("Token Corrente: " + currentToken);
                    if(DEBUG) System.out.println("Stmt: NON ho letto THEN dopo IF");
                    return false;
                } else {//ho letto then
                    if(DEBUG) System.out.println("Stmt: THEN");
                    currentToken = lexer.nextToken();
                    if(!Stmt()){
                        return false;
                    } else {//ho letto Stmt() ma non faccio avanzare il token perché se ne occupa un altro caso
                        if(!currentToken.sameTokenType(sym.ELSE)){
                            return false;
                        } else {//ho letto else
                            if(DEBUG) System.out.println("Stmt: ELSE");
                            currentToken = lexer.nextToken();
                            if(!Stmt()){
                                return false;
                            } else { //ho letto Stmt() ma non faccio avanzare il token perché se ne occupa un altro caso
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean Expr(){
        if(DEBUG) System.out.println("sono in expr e il current token è: " + currentToken);

        if(!Expr2()) {
            return false;
        }
        else{// non avanzo al prossimo token perché lo ha già fatto Expr2()
            if(!Expr1()){
                return false;
            }
            else{
                return true;
            }
        }

    }

    public boolean Expr1(){
        if(DEBUG) System.out.println("sono in expr1 - currenttoken: "+currentToken);
        if(!Relop()){
            //Non ho letto RELOP ma potrebbe essere la Produzione Expr1 -> ɛ
            if(DEBUG) System.out.println("Non ho letto relop");
            return true;
        } else { //abbiamo trovato un operatore di confronto ma non avanziamo al prossimo token perché lo ha già fatto Relop()
            if(DEBUG) System.out.println("Ho letto relop");
            if(!Expr2()){
                return false;
            } else { //abbiamo trovato ID o NUMBER
                if(!Expr1()){ //Expr1 -> ɛ
                    currentToken = lexer.nextToken();
                    return true;
                } //sono nel caso di Expr -> ɛ
                return true;
            }
        }
    }

    public boolean Expr2(){
        if(DEBUG) System.out.println("sono in expr2 e il current token è: " + currentToken);
        if(!currentToken.sameTokenType(sym.ID)){
            //non ho letto un ID ma potrebbe essere la produzione Expr2 -> NUMBER
            if(!currentToken.sameTokenType(sym.NUMBER)){
                return false;
            } else { //ho letto un NUMBER e quindi avanzo al prossimo token
                if(DEBUG) System.out.println("Expr2: NUMBER");
                currentToken = lexer.nextToken();
                return true;
            }
        } else { //ho letto un ID e avanzo al prossimo token
            if(DEBUG) System.out.println("Expr2: ID");
            currentToken = lexer.nextToken();
            return true;
        }
    }

    public boolean Relop(){
        if(DEBUG) System.out.println("sono in relop");
        if(DEBUG) System.out.println(currentToken);
        for(int x : operators){
            if(currentToken.sameTokenType(x)) {
                currentToken = lexer.nextToken();
                return true;
            }
        }
        return false;
    }

    public boolean EOF(){
        if(DEBUG) System.out.println("sono in eof");
        if(!currentToken.sameTokenType(sym.EOF)){
            return false;
        }
        return true;
    }

}
