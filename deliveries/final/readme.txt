Server:

    Comando di avvio: java -Dfile.encoding=utf-8 -jar Adrenalina-Server.jar

    Le impostazioni di gioco sono contenute nel file match_settings.properties all'interno della cartella settings
    Le impostazioni di connessione del server sono contenute nel file serverSettings.xml nella cartella settings

###########################################################################################################################################################
###########################################################################################################################################################

Client:

    Comando di avvio: java -Dfile.encoding=utf-8 -jar Adrenalina.jar

    Senza paramatri il client si avvierà automaticamente in modalità CLI.
    Aggiungendo il parametro 'start-gui' verrà invece avviata l'interfaccia grafica.
    L'interfaccia grafica può anche essere avviata direttamente eseguendo il file jar tramite doppio click

###########################################################################################################################################################
###########################################################################################################################################################

Nota:

    Se la modalità di connessione RMI non dovesse funzionare correttamente inserire l'ip del computer nel file ip.txt nella stessa cartella del jar (sia client sia server)

###########################################################################################################################################################
###########################################################################################################################################################

Funzionalità Implementate:

    - Regole complete
    - GUI
    - CLI
    - Socket
    - RMI
    - 1 Funzionalità aggiuntiva (Riconnessione giocatore alla partita in corso)