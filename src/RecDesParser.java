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

    public RecDesParser(String filePath){
        lexer = new Lexer();
        lexer.initialize(filePath);
    }

    public boolean S(){
        currentToken = lexer.nextToken();
        return Program() && EOF();
    }

    public boolean Program(){
        boolean result = Stmt() && Program1();
        if(!result){
            return false;
        }
        currentToken = lexer.nextToken();
        return true;
    }

    public boolean Program1(){
        if(!currentToken.sameTokenType(sym.SEMICOLON)){
            return false;
        } else { //è stato letto ";"
            if(!Stmt()){
                return false;
            } else {
                if(!Program1()){ //Program1 -> ɛ
                    currentToken = lexer.nextToken();
                    return true;
                }
                return true;
            }
        }
    }

    public boolean Stmt(){

    }

    public boolean Expr(){
        boolean result = Expr1() && Expr2();
        if(!result){
            return false;
        }
        currentToken = lexer.nextToken();
        return true;
    }

    public boolean Expr1(){
        if(!Relop()){
            return false;
        } else { //abbiamo trovato un operatore di confronto
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

        if(!currentToken.sameTokenType(sym.ID)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.NUMBER)){
            return false;
        }

        currentToken = lexer.nextToken();
        return true;
    }

    public boolean Relop(){

        if(!currentToken.sameTokenType(sym.LT)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.GT)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.LE)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.GE)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.NE)){
            return false;
        }

        if(!currentToken.sameTokenType(sym.EQ)){
            return false;
        }

        currentToken = lexer.nextToken();
        return true;
    }

    public boolean EOF(){

    }

}
