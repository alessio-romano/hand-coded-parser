README
======

Scelte di progettazione
-----------------------
1. Abbiamo modificato la classe Token dell'esercitazione 1 in modo che il name sia un intero piuttosto che una
   stringa in modo da agevolare i controlli avendo un riferimento fisso.
2. È stata modificata la classe Lexer in modo da restituire il Token `EOF` a fine file per permettere al Parser
   di terminare la sua esecuzione.
3. Il Parser a discesa ricorsiva è stato progettato in modo che i **token** al Lexer siano **ottenuti su richesta**. <br/>
   Non abbiamo quindi usato un array come nell'esempio fornito.
4. A seguito dei risultati dei test preliminari, si è deciso che le _keywords_ debbano essere in **minuscolo**. <br/>
   (Si può far riferimento al test sul `file_source8` in cui non tutte le keywords rispettano questa scelta)

Testing
-------
Per i file di test, siccome quelli forniti davano tutti syntax error
in quanto non rispettavano la grammatica di partenza, abbiamo deciso,
quindi, di modificarne qualcuno in modo che la grammatica venga rispettata.

Ad, esempio, il `file_source1` di partenza (a sinistra) è stato modificato come segue (a destra): 
```
if id < 2.0 then                            |        if id < 2 then 
          do                                |             do 
              if y < id then                |                    if y < id then
                  while id do x <-- id;     |                        do x <-- id while id 
              else                          |                    else
                  x <-- 3;                  |                        x <-- 3 
          while id                          |              while id
      else                                  |        else 
          prova <-- 3                       |            prova <-- 3 
```

Per il file di test `file_source4` e `file_source6`, sono state applicate modifiche analoghe.

I risultati ottenuti a seguito dei test effettuati sono quelli riportati nella seguente tabella:

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