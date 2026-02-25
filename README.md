# DOCUMENTAZIONE COMPLETA DEL PROGETTO “UNIVERSITÀ”

---

# 1. SITUAZIONE INIZIALE DEL PROGETTO

Il progetto nasce come applicazione web Java (Servlet + JSP + MySQL) per la gestione universitaria, con due soli ruoli:

* Studente  
* Professore  

L’architettura iniziale era basilare, con logica SQL spesso presente direttamente nelle JSP e una separazione MVC non rigorosa.

---

## 1.1 Account Studente (Versione Iniziale)

Credenziali:

* stud1 / stud1  
* stud2 / stud2  

Flusso iniziale:

* index.jsp → form di login  
* Connessione.java → gestione connessione DB  
* Login.java → autenticazione e visualizzazione esami  
* Prenotazione.java → servlet per prenotare esami  
* Prenota.java → conferma prenotazione  
* studente.jsp → visualizzazione dati  
* logout.jsp → uscita  

Problemi principali:

* Logica SQL nelle JSP  
* Nessun controllo su duplicati  
* Nessuna validazione robusta  
* Nessuna sicurezza reale  

---

## 1.2 Account Professore (Versione Iniziale)

Credenziali:

* prof1 / prof1  
* prof2 / prof2  

Flusso iniziale:

* index.jsp  
* Connessione.java  
* Login.java  
* StampaStudenti.java  
* professore.jsp  
* logout.jsp  

Problemi principali:

* Nessun controllo su ID appello inesistente  
* Gestione sessione fragile  
* Nessuna separazione reale della logica  

---

# 2. EVOLUZIONE DEL PROGETTO

Durante lo sviluppo il progetto è stato completamente ristrutturato e ampliato.

---

## 2.1 Revisione Autenticazione

### index.jsp

* Aggiunta selezione ruolo (Studente / Professore / Segreteria)  
* Validazione server-side  
* Eliminazione controlli ridondanti  

### Login.java

Problemi iniziali:

* Doppi cicli  
* Query su più tabelle  
* Nessun controllo input  

Migliorie:

* Switch-case sul ruolo  
* Query mirate sulla tabella corretta  
* Variabile di controllo per uscita ciclo  
* Eliminazione logica SQL dalle JSP  

Risultato: login più veloce, pulito e manutenibile.

---

## 2.2 Introduzione del Modulo Segreteria

La segreteria non esisteva nel progetto iniziale.  
È stata progettata completamente da zero.

Account:

* admin1 / admin1  
* admin2 / admin2  

Funzionalità implementate:

* Aggiungi/Rimuovi Studente  
* Aggiungi/Rimuovi Professore  
* Aggiungi/Rimuovi Corso  
* Visualizza utenti  
* Gestione corsi  
* Approvazione nuove registrazioni  

Controlli introdotti:

* Verifica chiavi esterne  
* Blocco eliminazione docente con corsi assegnati  
* Validazione dati  

La segreteria è ora un modulo completo e indipendente.

---

## 2.3 Migliorie Studente

Nuove funzionalità:

* Visualizzazione appelli disponibili  
* Prenotazione con controlli:  
  * esistenza appello  
  * duplicati  
  * validità data  
* Cancellazione prenotazione con controlli:  
  * verifica iscrizione reale  
  * blocco nel giorno dell’esame  

Bug eliminati:

* Prenotazioni multiple  
* Messaggi incoerenti  
* Errori di parsing su parametri null  

---

## 2.4 Migliorie Professore

Nuove funzionalità:

* Visualizzazione corsi assegnati  
* Aggiunta appello con controlli:  
  * corso esistente  
  * titolarità del corso  
  * data valida  
  * no duplicati  
* Cancellazione appello con gestione vincoli FK  
* Visualizzazione studenti iscritti  

---

# 3. INTRODUZIONE DEL MODULO VOTI

Il modulo Voti è stato progettato e integrato in modo completo, coinvolgendo tutti i ruoli.

---

## 3.1 Funzionalità per il Professore

Il docente può:

* Inserire un voto per uno studente iscritto a un appello  
* Modificare un voto già inserito  
* Eliminare un voto  
* Visualizzare i voti:  
  * per corso  
  * per appello  

Controlli:

* Lo studente deve essere iscritto all’appello  
* Non è possibile inserire due voti per la stessa prenotazione  
* Validazione del range voto  
* Controllo titolarità del corso  

---

## 3.2 Funzionalità per la Segreteria

La segreteria può:

* Visualizzare tutti i voti per corso  
* Visualizzare tutti i voti per appello  
* Controllare eventuali anomalie o duplicati  

---

## 3.3 Funzionalità per lo Studente

Lo studente può:

* Visualizzare tutti i suoi voti  
* Accettare o rifiutare un voto  
* Visualizzare la media dei suoi voti  
* Visualizzare la media per corso  

Controlli:

* Un voto rifiutato non viene conteggiato nella media  
* Un voto accettato diventa definitivo  

---

# 4. SICUREZZA

La sicurezza è stata migliorata in modo significativo.

### Controllo sessione in ogni servlet  
Non è stato utilizzato un filtro esterno.  
Ogni servlet verifica manualmente:

* se la sessione esiste  
* se contiene il ruolo corretto  
* se l’utente ha i permessi per l’operazione  

Questo approccio garantisce:

* nessun accesso non autorizzato  
* nessuna JSP raggiungibile senza login  
* controllo preciso per ruolo  

### Hash delle password  
Le password non sono più salvate in chiaro.  
Vengono memorizzate tramite hashing sicuro.

---

# 5. PROBLEMI RISCONTRATI E RISOLTI

| Problema                               | Soluzione                          |
| -------------------------------------- | ---------------------------------- |
| FK su cancellazione appello            | Blocco se esistono prenotazioni    |
| Prenotazione multipla                  | Controllo preventivo duplicati     |
| Connessione null                       | Fix gestione connessione           |
| Messaggi incoerenti                    | Riscrittura sistema messaggi       |
| Sessione professore instabile          | Controllo sessione in ogni servlet |
| Errori date appelli                    | Validazione completa               |
| Cancellazione prenotazione errore null | Controlli su parametri             |
| Controlli mancanti corso/prof          | Inseriti controlli di appartenenza |
| Duplicati voti                         | Controllo su prenotazione          |
| Media errata                           | Calcolo corretto lato server       |

---

# 6. ARCHITETTURA FINALE

Il progetto ora segue una struttura chiara e organizzata.

### View

* JSP pulite  
* Nessun SQL  
* Solo visualizzazione dati  

### Controller

* Servlet dedicate  
* Gestione sessione  
* Validazioni  
* Routing ordinato  

### Model

* Classi per Studente, Professore, Corso, Appello, Prenotazione, Voto  
* Connessione centralizzata  

Struttura modulare per ruoli:

* Studente  
* Professore  
* Segreteria  

---

# 7. RISULTATO FINALE

Il progetto è ora:

## Completamente funzionante

* Studente stabile  
* Professore completo  
* Segreteria autonoma  
* Modulo voti pienamente operativo  

## Sicuro

* Hash password  
* Controllo sessione in ogni servlet  

## Robusto

* Nessuna pagina bianca  
* Nessun errore SQL  
* Nessuna prenotazione duplicata  
* Nessun accesso non autorizzato  
* Nessun voto duplicato  

## Scalabile

* Codice modulare  
* Struttura chiara  
* Facile estensione futura  

## Professionale

* Separazione logica chiara  
* Validazioni complete  
* Gestione ruoli strutturata  
* Architettura ordinata  

---

# CONCLUSIONE

Il progetto è passato da una struttura basilare con logica mista e controlli limitati, a un sistema universitario completo, sicuro e strutturalmente professionale.

L’evoluzione ha riguardato:

* riorganizzazione architetturale  
* miglioramento sicurezza  
* ampliamento funzionalità  
* introduzione del modulo voti  
* eliminazione bug  
* controlli avanzati su ogni operazione  

Il risultato finale rappresenta un’applicazione web universitaria completa, stabile e pronta per ulteriori estensioni.
