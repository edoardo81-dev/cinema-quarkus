# Cinema Quarkus

Backend REST sviluppato con **Quarkus** per la gestione di un sistema cinema, con funzionalità di autenticazione JWT, gestione di film, sale, fasce orarie, programmazioni e prenotazioni, supporto a **Server-Sent Events (SSE)** per gli eventi di prenotazione e deploy tramite **Docker Compose + Nginx + PostgreSQL**.

## Panoramica del progetto

`cinema-quarkus` è un backend pensato per simulare la parte server di una piattaforma cinema.  
L’applicazione consente di:

- consultare il catalogo dei film
- visualizzare sale e fasce orarie
- consultare le programmazioni
- creare prenotazioni lato pubblico
- gestire le risorse amministrative tramite autenticazione JWT
- ricevere eventi live relativi alle prenotazioni tramite SSE

Il progetto è organizzato con una struttura pulita a layer, usando:

- **Resource** per gli endpoint REST
- **Service** per la logica applicativa
- **Repository** per l’accesso ai dati
- **DTO** per richieste e risposte
- **Mapper** per la conversione tra entità e DTO

---

## Tecnologie utilizzate

- **Java 17**
- **Quarkus 3**
- **Maven**
- **REST**
- **Jackson**
- **Hibernate Validator**
- **JWT (SmallRye JWT)**
- **Ebean ORM**
- **H2** per ambiente di sviluppo
- **PostgreSQL** per ambiente production
- **Docker / Docker Compose**
- **Nginx**
- **Server-Sent Events (SSE)**

---

## Funzionalità principali

### Area pubblica
- visualizzazione di tutti i film
- dettaglio singolo film
- visualizzazione sale
- visualizzazione fasce orarie
- visualizzazione programmazioni
- filtro programmazioni per sala, film e fascia oraria
- creazione prenotazioni
- stream eventi prenotazioni via SSE

### Area amministrativa
- login admin con JWT
- CRUD completo su:
  - film
  - sale
  - fasce orarie
  - programmazioni
- visualizzazione prenotazioni
- ricerca prenotazioni per programmazione
- annullamento prenotazioni
- eliminazione prenotazioni

---

## Struttura del progetto

```text
cinema-quarkus
├── docker
│   └── postgres
│       └── init
├── nginx
│   ├── certs
│   └── conf.d
├── src
│   ├── main
│   │   ├── docker
│   │   ├── java/com/example
│   │   │   ├── config
│   │   │   ├── dto
│   │   │   ├── exception
│   │   │   ├── mapper
│   │   │   ├── model
│   │   │   ├── repository
│   │   │   ├── resource
│   │   │   └── service
│   │   └── resources
│   └── test
└── docker-compose.yml
Package principali:

config
Configurazione applicativa e inizializzazione dati.

dto
Oggetti usati per input/output delle API.

exception
Gestione centralizzata degli errori applicativi.

mapper
Conversione tra entità e DTO.

model
Entità principali del dominio.

repository
Accesso ai dati tramite Ebean.

resource
Endpoint REST esposti dal backend.

service
Logica di business.
Modello dominio

Le entità principali del progetto sono:

AdminUser
Film
Sala
FasciaOraria
Programmazione
Prenotazione
Descrizione sintetica
Film: titolo, genere, anno, durata, immagine
Sala: nome e capienza
FasciaOraria: nome e prezzo
Programmazione: collega film, sala e fascia oraria, con periodo di validità e orario di inizio
Prenotazione: dati cliente, numero posti, data spettacolo, stato, codice prenotazione
AdminUser: utente amministratore per accesso all’area protetta
Sicurezza e autenticazione

Il progetto usa JWT per proteggere gli endpoint amministrativi.

Endpoint pubblico di login
POST /api/auth/login
Permessi configurati
/api/public/** → accesso libero
/api/auth/* → accesso libero
/api/admin/** → accesso autenticato
Ruolo previsto
ADMIN

Il token JWT contiene il gruppo/ruolo dell’utente autenticato.

Credenziali iniziali demo

All’avvio, il DataInitializer crea un utente amministratore se non presente:

username: admin
password: admin123

Queste credenziali sono utili per testare rapidamente le API protette.

Dati iniziali caricati automaticamente

Il progetto inizializza automaticamente:

Sale
Sala 1
Sala 2
Sala 3
Fasce orarie
POMERIGGIO
SERA
NOTTE
Film

Tra i film precaricati:

Lo Squalo
The Blues Brothers
Il Padrino
2001 Odissea nello spazio
Arancia meccanica
Febbre da cavallo
L'Evocazione - The Conjuring
The Shining
Scarface
Programmazioni

Vengono create programmazioni iniziali con:

sala
fascia oraria
orario di inizio
data inizio programmazione
data fine programmazione
Profili applicativi
Dev

In ambiente di sviluppo il progetto usa H2 in-memory:

database: H2
url: jdbc:h2:mem:cinema;DB_CLOSE_DELAY=-1
Prod

In ambiente production usa PostgreSQL con parametri configurabili via variabili ambiente:

DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
API principali
Autenticazione
Login

POST /api/auth/login

Esempio body:

{
  "username": "admin",
  "password": "admin123"
}
Film
Pubblico
GET /api/public/films
GET /api/public/films/{id}
Admin
POST /api/admin/films
PUT /api/admin/films/{id}
DELETE /api/admin/films/{id}
Sale
Pubblico
GET /api/public/sale
GET /api/public/sale/{id}
Admin
POST /api/admin/sale
PUT /api/admin/sale/{id}
DELETE /api/admin/sale/{id}
Fasce orarie
Pubblico
GET /api/public/fasce-orarie
GET /api/public/fasce-orarie/{id}
Admin
POST /api/admin/fasce-orarie
PUT /api/admin/fasce-orarie/{id}
DELETE /api/admin/fasce-orarie/{id}
Programmazioni
Pubblico
GET /api/public/programmazioni
GET /api/public/programmazioni/{id}
GET /api/public/programmazioni/sala/{salaId}
GET /api/public/programmazioni/film/{filmId}
GET /api/public/programmazioni/fascia-oraria/{fasciaOrariaId}
Admin
POST /api/admin/programmazioni
PUT /api/admin/programmazioni/{id}
DELETE /api/admin/programmazioni/{id}
Prenotazioni
Pubblico
POST /api/public/prenotazioni
Admin
GET /api/admin/prenotazioni
GET /api/admin/prenotazioni/{id}
GET /api/admin/prenotazioni/programmazione/{programmazioneId}
PUT /api/admin/prenotazioni/{id}/annulla
DELETE /api/admin/prenotazioni/{id}
Eventi live prenotazioni
SSE
GET /api/public/events/prenotazioni

Questo endpoint espone un flusso di eventi Server-Sent Events utile per aggiornamenti real-time lato frontend.

Avvio del progetto
1. Clonare il repository
git clone https://github.com/edoardo81-dev/cinema-quarkus.git
cd cinema-quarkus
2. Avvio in sviluppo

Per eseguire il progetto in modalità sviluppo con H2:

./mvnw quarkus:dev

Su Windows:

mvnw.cmd quarkus:dev

L’applicazione partirà di default sulla porta:

http://localhost:8080
3. Build del progetto
./mvnw clean package
4. Avvio con Docker Compose

Il file docker-compose.yml orchestra:

PostgreSQL
cinema-app
Nginx

Per avviare i container:

docker compose up -d

Per fermarli:

docker compose down
Architettura container
PostgreSQL

Container database con:

DB: cinema
user: cinema
password: cinema
cinema-app

Container applicazione Quarkus con profilo prod.

Nginx

Reverse proxy davanti all’applicazione, esposto su:

80
443
Configurazione

La configurazione principale si trova in:

src/main/resources/application.properties

Tra i parametri più rilevanti:

porta HTTP Quarkus
configurazione JWT
policy di accesso pubblico/admin
datasource dev/prod
configurazione proxy per Nginx
Gestione errori

Il progetto include una gestione centralizzata degli errori tramite:

ApiError
ConflictException
ResourceNotFoundException
GlobalExceptionHandler

Questo permette di restituire risposte coerenti e leggibili in caso di errore applicativo.

Testing

Per eseguire i test:

./mvnw test

Nel progetto è presente una struttura iniziale di test sotto:

src/test/java/com/example
Punti di forza del progetto
architettura chiara e modulare
distinzione netta tra area pubblica e area admin
autenticazione JWT
supporto SSE per eventi live
inizializzazione automatica dati demo
doppio profilo dev/prod
PostgreSQL containerizzato
reverse proxy Nginx
backend facilmente estendibile per un frontend Angular o React
Possibili sviluppi futuri
integrazione completa con frontend cinema
documentazione API con Swagger / OpenAPI
gestione posti disponibili per sala e spettacolo
storicizzazione prenotazioni
pagamenti online
pannello admin web completo
deploy cloud su Render / Railway / VPS
Autore

Progetto sviluppato da Edoardo come esercitazione pratica full backend con Quarkus, JWT, Ebean, PostgreSQL, Docker e Nginx.
