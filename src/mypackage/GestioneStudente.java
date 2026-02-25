package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

//libreria per List e Map
import java.util.*;

@WebServlet("/GestioneStudente")
public class GestioneStudente extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        //prendiamo la sessione se eventualmente non esiste non la creiamo
        //controlliamo se il ruolo salvata nella sessione è segreteria
        HttpSession session = request.getSession(false);
        if (session == null || !"segreteria".equals(session.getAttribute("ruolo"))) {
            response.sendRedirect("index.jsp");
            return;
        }

        //il post semplicemente ha uno switch che in bse all'azione scelta agisce
        //ha tre metodi aggiungi ,cancella e visualizza utilizzabili solo dalla segreteria
        //reindirizza tutto alla jsp apposita

        String action = request.getParameter("action");

        if (action == null) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Nessuna azione specificata");
            request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "aggiungi": aggiungiStudente(request, response); break;
            case "cancella": cancellaStudente(request, response); break;
            case "visualizza": visualizzaStudenti(request, response); break;
            default:
                request.setAttribute("success", false);
                request.setAttribute("message", "Azione non riconosciuta");
                request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
        }
    }

    //  metodo aggiungiStudente
    private void aggiungiStudente(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        //parametri presi dalla form in segreteraStudente.jsp
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");

        //check sui parametri
        if (user == null || password == null || nome == null || cognome == null ||
                user.trim().isEmpty() || password.trim().isEmpty() ||
                nome.trim().isEmpty() || cognome.trim().isEmpty()) {

            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            //controllo per evitare username duplicati
            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM studente WHERE username=?"
            );
            check.setString(1, user);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Errore: username già esistente");
                request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
                return;
            }

            // inserimento
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO studente (username, password, nome, cognome) VALUES (?, ?, ?, ?)"
            );

            //indichiamo a che ? passare il parametro , nella query
            stmt.setString(1, user);
            stmt.setString(2, password);
            stmt.setString(3, nome);
            stmt.setString(4, cognome);

            //eseguiamo l'update e verifichiamo se tutto è andati correttamente
            int righe = stmt.executeUpdate();

            if (righe > 0) {
                request.setAttribute("success", true);
                request.setAttribute("message", "Studente inserito correttamente");
            } else {
                request.setAttribute("success", false);
                request.setAttribute("message", "Errore: nessuno studente inserito");
            }

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
        }

        request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
    }

   // metodo cancellaStudente
    private void cancellaStudente(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        //passaggio della matricola dello studente da cancellare
        String matricolaParam = request.getParameter("matricola");

        if (matricolaParam == null || matricolaParam.trim().isEmpty()) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
            return;
        }

        //cast da String a int dato che nel DB matricola è int
        int matricola;

        try {
            matricola = Integer.parseInt(matricolaParam);
        } catch (NumberFormatException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "La matricola deve essere un numero intero");
            request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // controlliamo se lo studente esista realmente
            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM studente WHERE Matricola=?"
            );
            check.setInt(1, matricola);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Nessuno studente trovato con questa matricola");
                request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
                return;
            }

            // cancellazione
            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM studente WHERE Matricola=?"
            );
            stmt.setInt(1, matricola);
            stmt.executeUpdate();

            request.setAttribute("success", true);
            request.setAttribute("message", "Studente cancellato correttamente");

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore Database: " + e.getMessage());
        }

        request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
    }

   // metodo visualizzaStudenti(permette di visualizzarli tutti)
    private void visualizzaStudenti(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Matricola, nome, cognome, stato FROM studente"
             )) {

            //passiamo il risulato ad un ResultSet
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> studenti = new ArrayList<>();

            //per evitare problemi di aggiornamento si utilizza un' HashMap che conterrà tutti i dati degli studenti
            //che gli verrano passati dal ResultSet
            //cosi si sono risolti problemi di aggiornamento dato dal solo utilizzo di ResultSet senza Map
            while (rs.next()) {
                Map<String, Object> s = new HashMap<>();
                s.put("matricola", rs.getInt("Matricola"));
                s.put("nome", rs.getString("nome"));
                s.put("cognome", rs.getString("cognome"));
                s.put("stato", rs.getString("stato"));
                studenti.add(s);
            }

            //passiamo questo elenco di studenti alla tabella che poi li farà visualizzare
            request.setAttribute("elenco_studenti", studenti);
            request.setAttribute("success", true);
            request.setAttribute("message", "Studenti caricati correttamente");

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
        }

        request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
    }

}
