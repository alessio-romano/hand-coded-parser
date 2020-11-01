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

    private Lexer lexer; //istanza dell'analizzatore lessicale
    private Token currentToken; //il token corrente che si sta analizzando
    private int operators[] = {sym.LE, sym.LT, sym.GE, sym.GT, sym.EQ, sym.NE};

    public RecDesParser(String filePath){
        lexer = new Lexer();
        lexer.initialize(filePath);
    }

    public boolean S(){
        System.out.println("sono in S");
        currentToken = lexer.nextToken();
        return Program() && EOF();
    }

    public boolean Program(){
        System.out.println("sono in program");
        boolean result = Stmt() && Program1();
        if(!result){
            return false;
        }
        currentToken = lexer.nextToken();
        return true;
    }

    public boolean Program1(){
        System.out.println("sono in program1");
        if(!currentToken.sameTokenType(sym.SEMICOLON)){
            return false;
        } else { //è stato letto ";"
            currentToken = lexer.nextToken();
            if(!Stmt()){
                return false;
            } else {
                if(!Program1()){ //Program1 -> ɛ
                    return true;
                }
                return true;
            }
        }
    }

    public boolean Stmt(){
        System.out.println("sono in statement");
        if(!currentToken.sameTokenType(sym.IF)) {
            if(!currentToken.sameTokenType(sym.ID)) {
                if(!currentToken.sameTokenType(sym.DO)){
                    return false;
                }else {//il current token è DO
                    System.out.println("ho letto do");
                    currentToken = lexer.nextToken();
                    if (!Stmt()) {
                        return false;
                    }
                    else{
                        if(!currentToken.sameTokenType(sym.WHILE)){
                            return false;
                        }
                        else{
                            System.out.println("ho letto while");
                            currentToken = lexer.nextToken();
                            if(!Expr()){
                                return false;
                            }
                            else{
                                currentToken = lexer.nextToken();
                                return true;
                            }
                        }
                    }
                }
            }
            else{//il current token è ID
                System.out.println("ho letto id");
                currentToken = lexer.nextToken();
                if(!currentToken.sameTokenType(sym.ASSIGN)){
                    return false;
                }
                else{
                    System.out.println("ho letto assign");
                    currentToken = lexer.nextToken();
                    if(!Expr()){
                        return false;
                    }
                    else{
                        currentToken = lexer.nextToken();
                        return true;
                    }
                }
            }
        }
        else{ //il current token è IF
            System.out.println("ho letto if");
            currentToken = lexer.nextToken();
            if(!Expr()){
                return false;
            }
            else{
                currentToken = lexer.nextToken();
                if(!currentToken.sameTokenType(sym.THEN)){
                    return false;
                }
                else{
                    System.out.println("ho letto then");
                    currentToken = lexer.nextToken();
                    if(!Stmt()){
                        return false;
                    }
                    else{
                        currentToken = lexer.nextToken();
                        return true; //controllo ELSE->controllo STMT
                    }
                }
            }
        }
    }

    public boolean Expr(){
        System.out.println("sono in expr");

        if(!Expr2()) {
            return false;
        }
        else{
            currentToken = lexer.nextToken();
            if(!Expr1()){
                return false;
            }
            else{
                return true;
            }
        }

    }

    public boolean Expr1(){
        System.out.println("sono in expr1 - currenttoken:"+currentToken);
        if(!Relop()){
            System.out.println("Non ho letto relop");
            return true;
        } else { //abbiamo trovato un operatore di confronto
            System.out.println("Ho letto relop");
            if(!Expr2()){
                return false;
            } else { //abbiamo trovato ID o NUMBER
                if(!Expr1()){ //Expr1 -> ɛ
                    currentToken = lexer.nextToken();
                    return true;
                }
                return true;
            }
        }
    }

    public boolean Expr2(){
        System.out.println("sono in expr2");
        if(!(currentToken.sameTokenType(sym.ID) || currentToken.sameTokenType(sym.NUMBER))){
            return false;
        }
        System.out.println("Current token prima dell'aggiornamento in expr2 "+currentToken);
        currentToken = lexer.nextToken();
        System.out.println("Current token dopo dell'aggiornamento in expr2 \n"+currentToken);
        return true;
    }

    public boolean Relop(){
        System.out.println("sono in relop");
        System.out.println(currentToken);
        for(int x : operators){
            if(currentToken.sameTokenType(x)) {
                currentToken = lexer.nextToken();
                return true;
            }
        }
        return false;
    }

    public boolean EOF(){
        System.out.println("sono in eof");
        if(!currentToken.sameTokenType(sym.EOF)){
            return false;
        }
        return true;
    }

}
