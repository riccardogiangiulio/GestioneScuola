# GestioneScuola

## Obiettivo
GestioneScuola è un sistema informatico progettato per la gestione completa di una scuola. Il sistema permette di amministrare corsi, classi, studenti, insegnanti, esami e presenze, offrendo una piattaforma centralizzata per tutte le attività amministrative e didattiche di un istituto scolastico.

## Tecnologie
- **Backend**: Spring Boot 3.4.4, Java 21
- **Database**: PostgreSQL 16
- **Sicurezza**: Spring Security con autenticazione JWT
- **Documentazione API**: Swagger/SpringFox
- **Persistenza dati**: Spring Data JPA, Hibernate
- **Containerizzazione**: Docker, Docker Compose
- **Generazione report**: iText PDF, Apache POI (Excel), Apache Commona (CSV)
- **Validazione**: Bean Validation (Jakarta Validation)
- **Gestione dipendenze**: Maven

## Architettura
Il progetto è strutturato secondo il pattern architetturale MVC (Model-View-Controller) con una chiara separazione dei ruoli:

- **Model**: Le entità di dominio (User, Course, SchoolClass, ecc.) mappate al database tramite JPA
- **Controller**: Endpoint REST che gestiscono le richieste HTTP
- **Service**: Logica di business centralizzata con transazioni
- **Repository**: Interfacce per l'accesso ai dati con Spring Data JPA
- **DTO**: Oggetti di trasferimento dati per separare la rappresentazione esterna dalle entità interne
- **Exception Handling**: Sistema centralizzato di gestione delle eccezioni
- **JWT**: Logica di autenticazione per generare e verificare Bearer token
- **Util**: DataLoaderRunner per generare dati all'avvio del progetto

L'applicazione è progettata per essere eseguita in container Docker, facilitando il deployment e garantendo l'uniformità tra ambienti diversi.

## Sicurezza e autenticazione
- Implementazione di autenticazione basata su token JWT
- Gestione dei ruoli (Studente, Insegnante, Amministratore)
- Protezione degli endpoint con annotazioni di Spring Security
- Password criptate con BCrypt
- Controllo granulare degli accessi basato sui ruoli
- Token con scadenza configurabile (attualmente 24 ore)

## Funzionalità principali
- **Gestione Utenti**: Registrazione e gestione di studenti, insegnanti e amministratori
- **Gestione Corsi**: Creazione e amministrazione di corsi con relative materie
- **Gestione Classi**: Creazione di classi con assegnazione di studenti e insegnanti
- **Sistema di Iscrizioni**: Gestione delle iscrizioni ai corsi con relative approvazioni
- **Gestione Lezioni**: Pianificazione di lezioni con assegnazione di aule e insegnanti
- **Rilevamento Presenze**: Tracciamento delle presenze degli studenti alle lezioni
- **Gestione Esami**: Creazione e valutazione di esami con registrazione dei risultati
- **Esportazione Dati**: Generazione di report in formato PDF ed Excel

## Funzionalità aggiuntive
- Validazione approfondita dei dati in ingresso
- Gestione delle eccezioni personalizzate
- Logging dettagliato delle operazioni
- Data loader per inizializzare il sistema con dati di esempio
- API RESTful completamente documentate con Swagger

## Testing
Il progetto include test unitari e di integrazione utilizzando:
- JUnit 5
- Spring Boot Test
- Spring Security Test

## Avvio del progetto
1. Clonare il repository
2. Assicurarsi di avere installato:
   - Java 21
   - Maven
   - PostgreSQL 16 (se si avvia senza Docker)

### Opzione 1: Avvio con Docker (consigliato)
3. Eseguire il build del progetto: `mvn clean package`
4. Avviare i container Docker: `docker-compose up --build`
5. L'applicazione sarà disponibile su http://localhost:8081

### Opzione 2: Avvio diretto senza Docker
3. Assicurarsi che PostgreSQL sia in esecuzione sulla porta 5433
4. Configurare le credenziali del database in src/main/resources/application.properties se necessario
5. Eseguire: `mvn spring-boot:run` oppure avviare direttamente la classe GestioneScuolaApplication
6. L'applicazione sarà disponibile su http://localhost:8080

## Dataset iniziale
All'avvio, l'applicazione carica un dataset dimostrativo che include:
- Utenti con diversi ruoli (amministratori, insegnanti, studenti)
- Corsi didattici con relative materie
- Classi con insegnanti assegnati
- Iscrizioni di studenti
- Lezioni programmate
- Esami con relativi risultati
- Dati di presenza alle lezioni

## Design Pattern utilizzati
- **Singleton**: Utilizzato nei bean Spring gestiti con `@Service` e `@Component`
- **Repository**: Implementato tramite Spring Data JPA per l'accesso ai dati
- **DTO (Data Transfer Object)**: Utilizzato per separare la rappresentazione esterna dalle entità interne
- **Factory**: Implementato nei mapper per la conversione tra entità e DTO
- **Dependency Injection**: Utilizzato estensivamente tramite Spring per l'inversione di controllo
- **Builder**: Utilizzato in alcune classi di utility
- **MVC**: Modello architetturale generale dell'applicazione

## Note finali
Questo progetto è stato sviluppato come sistema dimostrativo per la gestione completa di un istituto scolastico. L'enfasi è stata posta sulla scalabilità, la sicurezza e la manutenibilità del codice, seguendo le migliori pratiche di sviluppo software.

Il sistema è progettato per essere facilmente estendibile con nuove funzionalità e personalizzabile per adattarsi alle esigenze specifiche di diversi tipi di istituzioni educative.