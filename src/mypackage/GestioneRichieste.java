package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/GestioneRichiesta")
public class GestioneRichieste extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        //metodo utilizzabile solo dalla segreteria per accettare o rifiutare richieste
        //proveninti da nuovi account prof o stud
        HttpSession session = request.getSession(false);
        if (session == null || !"segreteria".equals(session.getAttribute("ruolo"))) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");   // Approva / Rifiuta
        String ruolo  = request.getParameter("ruolo");    // Studente / Professore
        String id     = request.getParameter("id");       // Matricola o idProfessore

        if (action == null || ruolo == null || id == null ||
                action.isEmpty() || ruolo.isEmpty() || id.isEmpty()) {

            request.setAttribute("success", false);
            request.setAttribute("message", "Parametri mancanti");

            if ("Studente".equalsIgnoreCase(ruolo)) {
                request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
            } else if ("Professore".equalsIgnoreCase(ruolo)) {
                request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("segreteria.jsp").forward(request, response);
            }
            return;
        }

        switch (action) {
            case "Approva":
                gestisciRichiesta(request, response, ruolo, id, "APPROVATO");
                break;

            case "Rifiuta":
                gestisciRichiesta(request, response, ruolo, id, "RIFIUTATO");
                break;

            default:
                request.setAttribute("success", false);
                request.setAttribute("message", "Azione non riconosciuta");
                request.getRequestDispatcher("segreteria.jsp").forward(request, response);
        }
    }

    // metodo unico per accettare e rifiutare le richieste
    private void gestisciRichiesta(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String ruolo,
                                   String id,
                                   String nuovoStato)
            throws ServletException, IOException {

        String query = null;

        // Determino tabella e colonna ID
        if ("Studente".equalsIgnoreCase(ruolo)) {
            query = "UPDATE studente SET stato=? WHERE Matricola=?";
        } else if ("Professore".equalsIgnoreCase(ruolo)) {
            query = "UPDATE professore SET stato=? WHERE idProfessore=?";
        } else {
            request.setAttribute("success", false);
            request.setAttribute("message", "Ruolo non valido");
            request.getRequestDispatcher("segreteria.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nuovoStato);
            ps.setInt(2, Integer.parseInt(id));

            int righe = ps.executeUpdate();

            if (righe > 0) {
                request.setAttribute("success", true);
                request.setAttribute("message",
                        "Richiesta " + ruolo.toLowerCase() + " " + nuovoStato.toLowerCase() + " correttamente");
            } else {
                request.setAttribute("success", false);
                request.setAttribute("message", "Nessun record aggiornato");
            }

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore Database: " + e.getMessage());
        }

        if ("Studente".equalsIgnoreCase(ruolo)) {
            request.getRequestDispatcher("segreteriaStudente.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
        }
    }
}
