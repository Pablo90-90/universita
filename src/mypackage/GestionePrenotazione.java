package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/GestionePrenotazione")
public class GestionePrenotazione extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ruolo") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        System.out.println("DEBUG ruolo sessione = " + request.getSession().getAttribute("ruolo"));


        String ruolo = (String) session.getAttribute("ruolo");
        String action = request.getParameter("action");
        System.out.println("DEBUG action = " + request.getParameter("action"));


        if (action == null) {
            setMsg(request, false, "Azione non valida");
            forwardByRole(request, response, ruolo);
            return;
        }

        try {
            // Routing con controlli permessi inclusi
            switch (action) {
                case "aggiungi":
                    if (!"studente".equals(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                    try {
                        aggiungi(request, response, ruolo);
                    } catch (SQLException e) {
                        setMsg(request, false, "Errore DB: " + e.getMessage());
                        forwardByRole(request, response, ruolo);
                    }
                    break;

                case "cancella":
                    if (!"studente".equals(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                    try {
                        cancella(request, response, ruolo);
                    } catch (SQLException e) {
                        setMsg(request, false, "Errore DB: " + e.getMessage());
                        forwardByRole(request, response, ruolo);
                    }
                    break;

                case "visualizzaMie":
                    if (!"studente".equalsIgnoreCase(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                    visualizzaMie(request, response);
                    break;

                case "visualizzaPerProfessore":
                    if (!"professore".equals(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                    visualizzaPerProfessore(request, response);
                    break;

                case "visualizzaPerAppello":
                    if (!"professore".equals(ruolo) && !"segreteria".equals(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                case "visualizzaTutte":
                    if (!"segreteria".equals(ruolo)) {
                        response.sendRedirect("index.jsp");
                        return;
                    }
                    visualizzaTutte(request, response, ruolo);
                    break;

                default:
                    setMsg(request, false, "Azione non riconosciuta");
                    forwardByRole(request, response, ruolo);
            }
        } catch (SQLException e) {
            setMsg(request, false, "Errore Database: " + e.getMessage());
            forwardByRole(request, response, ruolo);
        }
    }

    // metodo per lo studente
    // metodo aggiungi prenotazione
    private void aggiungi(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException, SQLException {

            String matStr = (String) request.getSession().getAttribute("matricola");
            String idAppello = request.getParameter("idAppello");

            if (matStr == null || idAppello == null || idAppello.trim().isEmpty()) {
                setMsg(request, false, "Dati mancanti per la prenotazione");
                forwardByRole(request, response, ruolo);
                return;
            }

            int mat = Integer.parseInt(matStr);
            int idApp = Integer.parseInt(idAppello);

            try (Connection con = Connessione.getCon()) {

                // Controllo appello
                PreparedStatement checkApp = con.prepareStatement(
                        "SELECT Data FROM appello WHERE idAppello=?"
                );
                checkApp.setInt(1, idApp);
                ResultSet rsApp = checkApp.executeQuery();

                if (!rsApp.next()) {
                    setMsg(request, false, "Appello inesistente");
                    forwardByRole(request, response, ruolo);
                    return;
                }

                // Controllo data
                java.sql.Date dataAppello = rsApp.getDate("Data");
                if (dataAppello.before(new java.util.Date())) {
                    setMsg(request, false, "Impossibile prenotarsi: appello già passato");
                    forwardByRole(request, response, ruolo);
                    return;
                }

                // Controllo doppia prenotazione
                PreparedStatement checkDup = con.prepareStatement(
                        "SELECT 1 FROM prenotazione WHERE stud_prenotato=? AND app_prenotato=?"
                );
                checkDup.setInt(1, mat);
                checkDup.setInt(2, idApp);

                if (checkDup.executeQuery().next()) {
                    setMsg(request, false, "Sei già iscritto a questo appello");
                    forwardByRole(request, response, ruolo);
                    return;
                }

                // Inserimento
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO prenotazione (stud_prenotato, app_prenotato) VALUES (?, ?)"
                );
                ps.setInt(1, mat);
                ps.setInt(2, idApp);
                ps.executeUpdate();

                setMsg(request, true, "Prenotazione effettuata con successo");

            } catch (SQLException e) {
                setMsg(request, false, "Errore durante l'operazione: " + e.getMessage());
            }

            visualizzaMie(request, response);
        }


        // metodo per lo studente
    // metodo cancella prenotazione
    private void cancella(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException, SQLException {

        String idPren = request.getParameter("idPrenotazione");
        if (idPren == null) {
            setMsg(request, false, "ID prenotazione mancante");
            forwardByRole(request, response, ruolo);
            return;
        }

        // Recupero matricola dello studente
        Object obj = request.getSession().getAttribute("matricola");
        int matricola = Integer.parseInt(obj.toString());

        try (Connection con = Connessione.getCon()) {

            // Controllo che la prenotazione appartenga allo studente
            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM prenotazione WHERE idpren=? AND stud_prenotato=?"
            );
            check.setInt(1, Integer.parseInt(idPren));
            check.setInt(2, matricola);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                setMsg(request, false, "Non puoi cancellare prenotazioni che non ti appartengono");
                forwardByRole(request, response, ruolo);
                return;
            }

            // Cancellazione sicura
            PreparedStatement ps = con.prepareStatement("DELETE FROM prenotazione WHERE idpren=?");
            ps.setInt(1, Integer.parseInt(idPren));
            ps.executeUpdate();

            setMsg(request, true, "Prenotazione cancellata correttamente");
        }

        visualizzaMie(request, response);
    }

    private void visualizzaMie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Matricola può essere String o Integer → soluzione universale
            Object obj = request.getSession().getAttribute("matricola");
            if (obj == null) {
                setMsg(request, false, "Sessione scaduta. Effettua di nuovo il login.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

            int mat = Integer.parseInt(obj.toString());
            System.out.println("DEBUG matricola = " + mat);

            List<Map<String, Object>> lista = new ArrayList<>();

            try (Connection con = Connessione.getCon()) {

                PreparedStatement ps = con.prepareStatement(
                        "SELECT p.idpren AS idPrenotazione, a.Data AS dataAppello, c.Materia AS materia " +
                                "FROM prenotazione p " +
                                "JOIN appello a ON p.app_prenotato = a.idAppello " +
                                "JOIN corso c ON a.Materia = c.idcorso " +
                                "WHERE p.stud_prenotato = ?"
                );

                ps.setInt(1, mat);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idPrenotazione", rs.getInt("idPrenotazione"));
                    m.put("data", rs.getDate("dataAppello"));
                    m.put("materia", rs.getString("materia"));
                    lista.add(m);
                }
            }

            request.setAttribute("elenco_prenotazioni", lista);

            if (lista.isEmpty()) {
                setMsg(request, true, "Non hai prenotazioni attive");
            }

            request.getRequestDispatcher("studentePrenotazione.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            setMsg(request, false, "Errore interno durante il caricamento delle prenotazioni");
            request.getRequestDispatcher("studentePrenotazione.jsp").forward(request, response);
        }
    }

    // metodo per il professore che può vedere le prenotazione ai suoi appelli
    private void visualizzaPerProfessore(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        Integer idProf = (Integer) session.getAttribute("idProfessore");

        try (Connection con = Connessione.getCon()) {
            // Carico appelli del prof
            PreparedStatement psAppelli = con.prepareStatement(
                    "SELECT a.idAppello, a.Data, c.materia FROM appello a " +
                            "JOIN corso c ON a.Materia = c.idcorso WHERE c.Cattedra = ? ORDER BY a.Data");
            psAppelli.setInt(1, idProf);
            ResultSet rsAppelli = psAppelli.executeQuery();

            List<Map<String,Object>> appelli = new ArrayList<>();
            while (rsAppelli.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("idAppello", rsAppelli.getInt("idAppello"));
                m.put("data", rsAppelli.getDate("Data"));
                m.put("materia", rsAppelli.getString("materia"));
                appelli.add(m);
            }
            request.setAttribute("elenco_appelli", appelli);

            // Se scelto un appello, carico studenti
            String idAppello = request.getParameter("idAppello");
            if (idAppello != null && !idAppello.isEmpty()) {
                PreparedStatement psStud = con.prepareStatement(
                        "SELECT p.idpren AS idPrenotazione, s.nome, s.cognome, s.Matricola " +
                                "FROM prenotazione p JOIN studente s ON p.stud_prenotato = s.Matricola " +
                                "WHERE p.app_prenotato = ?");
                psStud.setInt(1, Integer.parseInt(idAppello));
                ResultSet rsStud = psStud.executeQuery();

                List<Map<String,Object>> studenti = new ArrayList<>();
                while (rsStud.next()) {
                    Map<String,Object> m = new HashMap<>();
                    m.put("idPrenotazione", rsStud.getInt("idPrenotazione"));
                    m.put("nome", rsStud.getString("nome"));
                    m.put("cognome", rsStud.getString("cognome"));
                    m.put("matricola", rsStud.getInt("Matricola"));
                    studenti.add(m);
                }
                request.setAttribute("elenco_prenotazioni", studenti);
            }
        }
        request.getRequestDispatcher("professorePrenotazione.jsp").forward(request, response);
    }

    // metodo per la segreteria che può vedere tutte le prenotazioni
    private void visualizzaTutte(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException, SQLException {

        try (Connection con = Connessione.getCon()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT p.idpren AS idPrenotazione, s.nome, s.cognome, a.Data, c.Materia " +
                            "FROM prenotazione p JOIN studente s ON p.stud_prenotato=s.Matricola " +
                            "JOIN appello a ON p.app_prenotato=a.idAppello " +
                            "JOIN corso c ON a.Materia = c.idcorso");

            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> lista = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("idPrenotazione", rs.getInt("idPrenotazione"));
                m.put("nome", rs.getString("nome"));
                m.put("cognome", rs.getString("cognome"));
                m.put("data", rs.getDate("Data"));
                m.put("materia", rs.getString("Materia"));
                lista.add(m);
            }
            request.setAttribute("elenco_prenotazioni", lista);
            setMsg(request, true, "Tutte le prenotazioni caricate");
        }
        forwardByRole(request, response, ruolo);
    }

    // metodi di supporto
    private void setMsg(HttpServletRequest req, boolean ok, String msg) {
        req.setAttribute("success", ok);
        req.setAttribute("message", msg);
    }

    private void forwardByRole(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException {
        switch (ruolo) {
            case "studente": request.getRequestDispatcher("studentePrenotazione.jsp").forward(request, response); break;
            case "professore": request.getRequestDispatcher("professorePrenotazione.jsp").forward(request, response); break;
            case "segreteria": request.getRequestDispatcher("segreteriaPrenotazione.jsp").forward(request, response); break;
            default: response.sendRedirect("index.jsp");
        }
    }

}