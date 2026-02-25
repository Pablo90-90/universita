package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/GestioneAppello")
public class GestioneAppello extends HttpServlet {

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
        String action = request.getParameter("action");

        if (action == null) {
            setMsg(request, false, "Azione non valida");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (action.equals("visualizzaProf")) {
            visualizzaProf(request, response);
            return;
        }

        switch (action) {

            case "aggiungi":
                if (!ruolo.equals("professore") && !ruolo.equals("segreteria")) {
                    response.sendRedirect("index.jsp");
                    return;
                }
                aggiungiAppello(request, response, ruolo);
                break;

            case "cancella":
                if (!ruolo.equals("professore") && !ruolo.equals("segreteria")) {
                    response.sendRedirect("index.jsp");
                    return;
                }
                cancellaAppello(request, response, ruolo);
                break;

            case "visualizzaPerCorso":
                visualizzaPerCorso(request, response, ruolo);
                break;

            case "visualizzaTutti":
                if (!ruolo.equals("segreteria")) {
                    response.sendRedirect("index.jsp");
                    return;
                }
                visualizzaTutti(request, response, ruolo);
                break;

            case "visualizzaAppelliStudente":
                if (!ruolo.equals("studente")) {
                    response.sendRedirect("index.jsp");
                    return;
                }
                break;

            default:
                setMsg(request, false, "Azione non riconosciuta");
                forwardByRole(request, response, ruolo);
        }
    }

    // metodo aggiungiAppello per prof e segreteria
    private void aggiungiAppello(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String ruolo)
            throws ServletException, IOException {

        //prendiamo parametri e li castiamo per essere coerenti a quelli del DB
        String data = request.getParameter("data");
        String idCorso = request.getParameter("idcorso");

        if (data == null || idCorso == null || data.trim().isEmpty() || idCorso.trim().isEmpty()) {
            setMsg(request, false, "Dati mancanti");
            forwardByRole(request, response, ruolo);
            return;
        }

        java.sql.Date dataSql;
        try {
            dataSql = java.sql.Date.valueOf(data);
        } catch (Exception e) {
            setMsg(request, false, "Formato data non valido (usa YYYY-MM-DD)");
            forwardByRole(request, response, ruolo);
            return;
        }

        // 🔥 CONTROLLO DATA: NON PASSATA
        java.time.LocalDate oggi = java.time.LocalDate.now();
        java.time.LocalDate dataAppello = dataSql.toLocalDate();

        if (dataAppello.isBefore(oggi)) {
            setMsg(request, false, "La data dell'appello non può essere nel passato");
            forwardByRole(request, response, ruolo);
            return;
        }

        // 🔥 CONTROLLO DATA: NON OLTRE 2 ANNI
        java.time.LocalDate limiteFuturo = oggi.plusYears(2);

        if (dataAppello.isAfter(limiteFuturo)) {
            setMsg(request, false, "La data dell'appello non può superare i 2 anni dalla data attuale");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idCorsoInt;
        try {
            idCorsoInt = Integer.parseInt(idCorso);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID corso non valido");
            forwardByRole(request, response, ruolo);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // 🔍 CONTROLLO SE IL CORSO ESISTE
            PreparedStatement checkCorso = con.prepareStatement(
                    "SELECT Cattedra FROM corso WHERE idcorso=?"
            );
            checkCorso.setInt(1, idCorsoInt);
            ResultSet rsCorso = checkCorso.executeQuery();

            if (!rsCorso.next()) {
                setMsg(request, false, "Errore: corso inesistente");
                forwardByRole(request, response, ruolo);
                return;
            }

            // 🔍 CONTROLLO CHE IL PROFESSORE SIA TITOLARE DEL CORSO
            if ("professore".equals(ruolo)) {

                Integer idProfLoggato = (Integer) request.getSession().getAttribute("idProfessore");

                if (idProfLoggato == null) {
                    setMsg(request, false, "Errore: impossibile verificare il docente loggato");
                    forwardByRole(request, response, ruolo);
                    return;
                }

                int idProfTitolare = rsCorso.getInt("Cattedra");

                if (idProfTitolare != idProfLoggato) {
                    setMsg(request, false, "Non puoi aggiungere appelli: non sei il docente titolare del corso");
                    forwardByRole(request, response, ruolo);
                    return;
                }
            }


            // 🔍 CONTROLLO DUPLICATO (stessa data stesso corso)
            PreparedStatement checkDup = con.prepareStatement(
                    "SELECT COUNT(*) FROM appello WHERE Materia=? AND Data=?"
            );
            checkDup.setInt(1, idCorsoInt);
            checkDup.setDate(2, dataSql);
            ResultSet rsDup = checkDup.executeQuery();
            rsDup.next();

            if (rsDup.getInt(1) > 0) {
                setMsg(request, false, "Esiste già un appello in questa data per il corso selezionato");
                forwardByRole(request, response, ruolo);
                return;
            }

            // ✔️ INSERIMENTO
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO appello (Data, Materia) VALUES (?, ?)"
            );

            ps.setDate(1, dataSql);
            ps.setInt(2, idCorsoInt);

            ps.executeUpdate();

            setMsg(request, true, "Appello aggiunto correttamente");

        } catch (Exception e) {
            setMsg(request, false, "Errore: " + e.getMessage());
        }

        forwardByRole(request, response, ruolo);
    }


    // metodo cancellaAppello
    private void cancellaAppello(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String ruolo)
            throws ServletException, IOException {

        String idAppello = request.getParameter("idAppello");

        if (idAppello == null || idAppello.trim().isEmpty()) {
            setMsg(request, false, "ID appello mancante");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idApp;
        try {
            idApp = Integer.parseInt(idAppello);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID appello non valido");
            forwardByRole(request, response, ruolo);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // conytollo se appello esiste
            PreparedStatement checkExist = con.prepareStatement(
                    "SELECT Materia FROM appello WHERE idAppello=?"
            );
            checkExist.setInt(1, idApp);
            ResultSet rsExist = checkExist.executeQuery();

            if (!rsExist.next()) {
                setMsg(request, false, "Appello inesistente");
                forwardByRole(request, response, ruolo);
                return;
            }

            int idCorso = rsExist.getInt("Materia");

            // 🔍 CONTROLLO CHE IL PROFESSORE SIA TITOLARE DEL CORSO
            // (solo se ruolo = professore)
            if ("P".equals(ruolo)) {
                int idProfLoggato = (int) request.getSession().getAttribute("id");

                PreparedStatement checkProf = con.prepareStatement(
                        "SELECT COUNT(*) FROM corso WHERE idcorso=? AND Cattedra=?"
                );
                checkProf.setInt(1, idCorso);
                checkProf.setInt(2, idProfLoggato);

                ResultSet rsProf = checkProf.executeQuery();
                rsProf.next();

                if (rsProf.getInt(1) == 0) {
                    setMsg(request, false, "Non puoi cancellare questo appello: non sei il docente titolare del corso");
                    forwardByRole(request, response, ruolo);
                    return;
                }
            }

            // 🔍 CONTROLLO PRENOTAZIONI
            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM prenotazione WHERE app_prenotato=?"
            );
            check.setInt(1, idApp);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                setMsg(request, false, "Impossibile cancellare: esistono prenotazioni");
                forwardByRole(request, response, ruolo);
                return;
            }

            // ✔️ CANCELLAZIONE
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM appello WHERE idAppello=?"
            );
            ps.setInt(1, idApp);
            ps.executeUpdate();

            setMsg(request, true, "Appello cancellato correttamente");

        } catch (Exception e) {
            setMsg(request, false, "Errore: " + e.getMessage());
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo per visualizzare gli appelli di tutti i corsi di un prof
    private void visualizzaProf(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer idProf = (Integer) session.getAttribute("idProfessore");

        if (idProf == null) {
            setMsg(request, false, "Sessione scaduta");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            //partiamo recuperando i corsi
            PreparedStatement psCorsi = con.prepareStatement(
                    "SELECT idcorso, materia FROM corso WHERE Cattedra=?"
            );
            psCorsi.setInt(1, idProf);
            ResultSet rsCorsi = psCorsi.executeQuery();

            List<Map<String,Object>> corsi = new ArrayList<>();
            while (rsCorsi.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("idCorso", rsCorsi.getInt("idcorso"));
                m.put("materia", rsCorsi.getString("materia"));
                corsi.add(m);
            }

            request.setAttribute("elenco_corsi", corsi);

            //recuperiamo gli appelli
            PreparedStatement psAppelli = con.prepareStatement(
                    "SELECT a.idAppello, a.Data, c.materia\n" +
                            "FROM appello a\n" +
                            "JOIN corso c ON a.Materia = c.idcorso\n" +
                            "WHERE c.Cattedra = ?\n" +
                            "ORDER BY a.Data\n"
            );
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

            // Messaggio opzionale
            if (corsi.isEmpty() && appelli.isEmpty()) {
                setMsg(request, true, "Nessun corso o appello trovato");
            } else {
                setMsg(request, true, "Dati caricati correttamente");
            }

        } catch (Exception e) {
            setMsg(request, false, "Errore: " + e.getMessage());
        }

        // reindirizzamento alla jsp del professore
        request.getRequestDispatcher("professoreAppelli.jsp").forward(request, response);
    }

    // metodo per visualizzare gli appelli di un corso specifico
    private void visualizzaPerCorso(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String ruolo)
            throws ServletException, IOException {

        String idCorsoParam = request.getParameter("idcorso");

        //check e casting del parametro
        if (idCorsoParam == null || idCorsoParam.trim().isEmpty()) {
            setMsg(request, false, "ID corso non valido");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idCorso;
        try {
            idCorso = Integer.parseInt(idCorsoParam);
        } catch (NumberFormatException e) {
            setMsg(request, false, "L'ID corso deve essere numerico");
            forwardByRole(request, response, ruolo);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            // CONTROLLO ESISTENZA CORSO
            PreparedStatement check = con.prepareStatement(
                    "SELECT Materia FROM corso WHERE idcorso = ?"
            );
            check.setInt(1, idCorso);
            ResultSet rsCheck = check.executeQuery();

            if (!rsCheck.next()) {
                setMsg(request, false, "Corso inesistente");
                forwardByRole(request, response, ruolo);
                return;
            }

            String nomeMateria = rsCheck.getString("Materia");
            request.setAttribute("nomeMateria", nomeMateria);

            PreparedStatement ps = con.prepareStatement(
                    "SELECT idAppello, Data FROM appello WHERE Materia = ? ORDER BY Data"
            );
            ps.setInt(1, idCorso);

            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> appelli = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> a = new HashMap<>();
                a.put("idAppello", rs.getInt("idAppello"));
                a.put("data", rs.getDate("Data"));
                appelli.add(a);
            }

            request.setAttribute("elenco_appelli", appelli);

            if (appelli.isEmpty()) {
                setMsg(request, false, "Nessun appello trovato per questo corso");
            } else {
                setMsg(request, true, "Appelli caricati correttamente");
            }

        } catch (SQLException e) {
            setMsg(request, false, "Errore durante l'operazione: " + e.getMessage());
        }

        //reindirizzamento automatico
        forwardByRole(request, response, ruolo);
    }


    // vmetodo per visualizzare tutti gli appelli
    private void visualizzaTutti(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String ruolo)
            throws ServletException, IOException {

        try (Connection con = Connessione.getCon()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT idAppello, Data, Materia FROM appello"
            );

            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> lista = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("idAppello", rs.getInt("idAppello"));
                m.put("data", rs.getDate("Data"));
                m.put("materia", rs.getInt("Materia"));
                lista.add(m);
            }

            request.setAttribute("elenco_appelli", lista);
            setMsg(request, true, "Tutti gli appelli caricati");

        } catch (Exception e) {
            setMsg(request, false, "Errore: " + e.getMessage());
        }

        forwardByRole(request, response, ruolo);
    }

    // metodi di supporto
    private void setMsg(HttpServletRequest req, boolean ok, String msg) {
        req.setAttribute("success", ok);
        req.setAttribute("message", msg);
    }

    private void forwardByRole(HttpServletRequest request,
                               HttpServletResponse response,
                               String ruolo)
            throws ServletException, IOException {

        switch (ruolo) {
            case "studente":
                request.getRequestDispatcher("studentePrenotazione.jsp").forward(request, response);
                break;
            case "professore":
                request.getRequestDispatcher("professoreAppelli.jsp").forward(request, response);
                break;
            default:
                request.getRequestDispatcher("segreteriaAppelli.jsp").forward(request, response);
        }
    }

}