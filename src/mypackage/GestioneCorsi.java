package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/GestioneCorsi")
public class GestioneCorsi extends HttpServlet {

    //stessa metodologia delle altre servlet
    //ma in aggiunta c'è setMsg che setta se il messaggio è di successo o errore cosi da far cambiare colore
    //e ForwardByRole che appunto reindirizza in base al ruolo dato che questa servlet è usata da più utenti con ruoli
    //diversi

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("ruolo") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String ruolo = (String) session.getAttribute("ruolo");

        ruolo = ruolo.trim().toLowerCase();

        if (!ruolo.equals("segreteria") && !ruolo.equals("studente")) {
            response.sendRedirect("index.jsp");
            return;
        }


        String action = request.getParameter("action");

        if (action == null) {
            setMsg(request, false, "Nessuna azione specificata");
            forwardByRole(request, response, ruolo);
            return;
        }

        switch (action) {
            case "aggiungi":
                if (!"segreteria".equals(ruolo)) { response.sendRedirect("index.jsp"); return; }
                aggiungiCorso(request, response);
                break;

            case "cancella":
                if (!"segreteria".equals(ruolo)) { response.sendRedirect("index.jsp"); return; }
                cancellaCorso(request, response);
                break;

            case "visualizza":
                visualizzaCorsi(request, response, ruolo);
                break;

            default:
                setMsg(request, false, "Azione non riconosciuta");
                forwardByRole(request, response, ruolo);
        }
    }


    // metodo solo per la segreteria
    // metodo aggiungiCorso
    private void aggiungiCorso(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        String materia = request.getParameter("materia");
        String idProfParam = request.getParameter("idProf");

        // Controllo campi vuoti
        if (materia == null || materia.trim().isEmpty() ||
                idProfParam == null || idProfParam.trim().isEmpty()) {

            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
            return;
        }

        int idProf;
        try {
            idProf = Integer.parseInt(idProfParam);
        } catch (NumberFormatException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "ID professore non valido");
            request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // Controlliamos se il prof esiste e controlliamo se sia stato accettato prima di dargli un corso
            String checkProf = "SELECT stato FROM professore WHERE idProfessore = ?";
            String stato = null;

            try (PreparedStatement ps = con.prepareStatement(checkProf)) {
                ps.setInt(1, idProf);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    request.setAttribute("success", false);
                    request.setAttribute("message", "Errore: professore inesistente");
                    request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
                    return;
                }

                stato = rs.getString("stato");
            }

            if (!"approvato".equalsIgnoreCase(stato)) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Il professore non può essere assegnato a un corso: stato = " + stato);
                request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
                return;
            }

            //controlliamo che il corso non esisti già
            String checkMateria = "SELECT COUNT(*) FROM corso WHERE Materia = ?";
            try (PreparedStatement ps = con.prepareStatement(checkMateria)) {
                ps.setString(1, materia);
                ResultSet rs = ps.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    request.setAttribute("success", false);
                    request.setAttribute("message", "Errore: materia già presente");
                    request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
                    return;
                }
            }

            // infine possiamo aggiungerlo
            String sql = "INSERT INTO corso (Materia, Cattedra) VALUES (?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setString(1, materia);
                stmt.setInt(2, idProf);

                int righe = stmt.executeUpdate();

                if (righe > 0) {
                    request.setAttribute("success", true);
                    request.setAttribute("message", "Corso inserito correttamente");
                } else {
                    request.setAttribute("success", false);
                    request.setAttribute("message", "Errore: nessun corso inserito");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
        }

        request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
    }

    // metodo solo per la segreteria
    // metodo cancellaCorso
    private void cancellaCorso(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        //prendiamo dalla form idCorso poi lo castiamo a int per lavorarci col db
        String idParam = request.getParameter("idcorso");

        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
            return;
        }

        int idCorso;

        try {
            idCorso = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "L'ID corso deve essere un numero intero");
            request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // check se il corso esiste
            String checkSql = "SELECT COUNT(*) FROM corso WHERE idcorso = ?";
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, idCorso);
                ResultSet rs = ps.executeQuery();
                rs.next();

                if (rs.getInt(1) == 0) {
                    request.setAttribute("success", false);
                    request.setAttribute("message", "Nessun corso trovato con questo ID");
                    request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
                    return;
                }
            }

            // cancellazione
            String sql = "DELETE FROM corso WHERE idcorso = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setInt(1, idCorso);
                stmt.executeUpdate();

                request.setAttribute("success", true);
                request.setAttribute("message", "Corso cancellato correttamente");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore Database: " + e.getMessage());
        }

        request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
    }

    // metodo per tutti gli utenti
    // metodo per visualizzare tutti i corsi
    private void visualizzaCorsi(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String ruolo)
            throws ServletException, IOException {

        List<Map<String,Object>> corsi = new ArrayList<>();

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement("SELECT idcorso, Materia FROM corso");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("idcorso", rs.getInt("idcorso"));
                m.put("materia", rs.getString("Materia"));
                corsi.add(m);
            }

            request.setAttribute("corsi", corsi);
            setMsg(request, true, "Corsi caricati correttamente");

        } catch (SQLException e) {
            setMsg(request, false, "Errore caricamento corsi: " + e.getMessage());
        }

        // reindirizzamento automatico in base al ruolo
        forwardByRole(request, response, ruolo);
    }

    // metodi di supporto per reindirizzare e settare messaggio
    private void forwardByRole(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException {

        switch (ruolo) {
            case "studente":
                request.getRequestDispatcher("studentePrenotazione.jsp").forward(request, response);
                break;
            case "professore":
                request.getRequestDispatcher("professoreCorsi.jsp").forward(request, response);
                break;
            case "segreteria":
                request.getRequestDispatcher("segreteriaCorsi.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }

    private void setMsg(HttpServletRequest req, boolean ok, String msg) {
        req.setAttribute("success", ok);
        req.setAttribute("message", msg);
    }


}