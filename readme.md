README
======

La grammatica utilizzata per lo sviluppo del parser a discesa ricorsiva è la seguente:

```
  G = (N, T, S, P)
  N = {Program, Stmt, Expr, Program1, Expr1, Expr2, Relop}
  T = {ID, IF, THEN, ELSE, LT, GT, LE, GE, NE, EQ, NUMBER, ;, ASSIGN, WHILE, DO, EOF}
  S
  P = {
        S -> Program EOF
        Program -> Stmt Program1
        Program1 -> ; Stmt Program1 | ɛ
        Stmt -> IF Expr THEN Stmt ELSE Stmt | ID ASSIGN Expr | DO Stmt WHILE Expr
        Expr -> Expr2 Expr1
        Expr1 -> Relop Expr2 Expr1 | ɛ
        Expr2 -> ID | NUMBER
        Relop -> LT | GT | LE | GE | NE | EQ
      }
```

Testing
-------

Sono stati forniti dei file di test per verificare il corretto funzionamento del parser in specifici casi.
I risultati ottenuti a seguito dei test effettuati sono riportati nella seguente tabella:

|File di Test| Output del Parser |
|-----|:---------:|
|file_source1 | Valido |
|file_source2 | Non Valido |
|file_source3 | Non Valido |
|file_source4 | Valido |
|file_source5 | Non Valido |
|file_source6 | Valido |
|file_source7 | Non Valido |
|file_source8 | Non Valido |
|file_source9 | Valido |
|file_source10 | Valido |
