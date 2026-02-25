package mypackage;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/GestioneVoti")
public class GestioneVoti extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(GestioneVoti.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String ruolo = (String) request.getSession().getAttribute("ruolo");

        if (ruolo == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            if (action == null) {
                setMsg(request, false, "Azione non specificata.");
                forwardByRole(request, response, ruolo);
                return;
            }

            switch (action) {

                // operazioni per i voi
                case "aggiungi": aggiungiVoto(request, response, ruolo); break;
                case "modifica": modificaVoto(request, response, ruolo); break;
                case "elimina": eliminaVoto(request, response, ruolo); break;

                // metodi per visualizzare voti
                case "visualizzaPerAppello": visualizzaPerAppello(request, response, ruolo); break;
                case "visualizzaPerCorso": visualizzaPerCorso(request, response, ruolo); break;
                case "visualizzaPerCorsoProf": visualizzaPerCorsoProf(request, response, ruolo); break;
                case "visualizzaMieiVoti": visualizzaMieiVoti(request, response, ruolo); break;

                // metodi studente
                case "accettaVoto": accettaVoto(request, response, ruolo); break;
                case "rifiutaVoto": rifiutaVoto(request, response, ruolo); break;

                // metodi per le medie
                case "mediaStudente": mediaStudente(request, response, ruolo); break;
                case "mediaPerCorso": mediaPerCorso(request, response, ruolo); break;

                default:
                    setMsg(request, false, "Azione non riconosciuta: " + action);
                    forwardByRole(request, response, ruolo);
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Errore GestioneVoti", ex);
            setMsg(request, false, "Errore server: " + ex.getMessage());
            forwardByRole(request, response, ruolo);
        }
    }

    // metodo aggiungiVoto , di default il voto ha stato in attesa
    private void aggiungiVoto(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idStudStr = request.getParameter("idStud");
        String idAppStr = request.getParameter("idApp");
        String votoStr = request.getParameter("voto");

        if (isBlank(idStudStr) || isBlank(idAppStr) || isBlank(votoStr)) {
            setMsg(request, false, "Parametri mancanti.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idStud, idApp, voto;
        try {
            idStud = Integer.parseInt(idStudStr);
            idApp = Integer.parseInt(idAppStr);
            voto = Integer.parseInt(votoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "Parametri non numerici.");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (!appelloExists(idApp)) {
            setMsg(request, false, "Appello inesistente.");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (voto < 0 || voto > 30) {
            setMsg(request, false, "Voto non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        try (Connection con = Connessione.getCon()) {
            if (esisteVoto(con, idStud, idApp)) {
                setMsg(request, false, "Esiste già un voto per questo studente in questo appello.");
                forwardByRole(request, response, ruolo);
                return;
            }
        }

        try (Connection con = Connessione.getCon()) {
            if (!esistePrenotazione(con, idStud, idApp)) {
                setMsg(request, false, "Lo studente non è prenotato a questo appello.");
                forwardByRole(request, response, ruolo);
                return;
            }
        }

        String sql = "INSERT INTO esiti (voto, dataRegistrazione, stato, idStud, idApp) "
                + "VALUES (?, CURRENT_DATE, 'attesa', ?, ?)";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voto);
            ps.setInt(2, idStud);
            ps.setInt(3, idApp);

            if (ps.executeUpdate() > 0)
                setMsg(request, true, "Voto inserito.");
            else
                setMsg(request, false, "Errore inserimento voto.");
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo modifica voti per i docenti che dopo la modifica fa tornare il voto in attesa
    private void modificaVoto(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idVotoStr = request.getParameter("idVoto");
        String votoStr = request.getParameter("voto");

        if (isBlank(idVotoStr) || isBlank(votoStr)) {
            setMsg(request, false, "Parametri mancanti.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idVoto, voto;
        try {
            idVoto = Integer.parseInt(idVotoStr);
            voto = Integer.parseInt(votoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "Parametri non numerici.");
            forwardByRole(request, response, ruolo);
            return;
        }

        // 🔥 Aggiorniamo sia il voto che lo stato
        String sql = "UPDATE esiti SET voto=?, stato='attesa' WHERE idVoto=?";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voto);
            ps.setInt(2, idVoto);

            if (ps.executeUpdate() > 0)
                setMsg(request, true, "Voto modificato e impostato in attesa.");
            else
                setMsg(request, false, "ID voto inesistente.");
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo eliminaVoto per i professori
    private void eliminaVoto(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idVotoStr = request.getParameter("idVoto");

        if (isBlank(idVotoStr)) {
            setMsg(request, false, "ID voto mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idVoto;
        try {
            idVoto = Integer.parseInt(idVotoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID voto non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        String sql = "DELETE FROM esiti WHERE idVoto=?";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVoto);

            if (ps.executeUpdate() > 0)
                setMsg(request, true, "Voto eliminato.");
            else
                setMsg(request, false, "ID voto inesistente.");
        }

        forwardByRole(request, response, ruolo);
    }


    //metodo per visualizzare i voti per appello
    private void visualizzaPerAppello(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idAppStr = request.getParameter("idApp");

        if (isBlank(idAppStr)) {
            setMsg(request, false, "ID appello mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idApp;
        try {
            idApp = Integer.parseInt(idAppStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID appello non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (!appelloExists(idApp)) {
            setMsg(request, false, "Appello inesistente.");
            forwardByRole(request, response, ruolo);
            return;
        }

        String baseSql =
                "SELECT e.idVoto, s.Matricola, c.idcorso, e.voto, e.dataRegistrazione, e.stato "
                        + "FROM esiti e "
                        + "JOIN appello a ON e.idApp = a.idAppello "
                        + "JOIN corso c ON a.Materia = c.idcorso "
                        + "JOIN studente s ON e.idStud = s.Matricola "
                        + "WHERE e.idApp = ?";

        if (ruolo.equalsIgnoreCase("professore")) {

            Integer idProf = (Integer) request.getSession().getAttribute("idProfessore");
            if (idProf == null) {
                setMsg(request, false, "Sessione professore non valida.");
                forwardByRole(request, response, ruolo);
                return;
            }

            try (Connection con = Connessione.getCon()) {
                if (!isProfTitolare(con, idApp, idProf)) {
                    setMsg(request, false, "Accesso negato: appello non tuo.");
                    forwardByRole(request, response, ruolo);
                    return;
                }
            }

            String sql = baseSql + " AND c.Cattedra = ?";
            request.setAttribute("votiAppello", fetchVoti(sql, idApp, idProf));

        } else {
            request.setAttribute("votiAppello", fetchVoti(baseSql, idApp));
        }

        setMsg(request, true, "Voti caricati.");
        forwardByRole(request, response, ruolo);
    }

    //metodo per visualizzare i voti per corso
    private void visualizzaPerCorso(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idCorsoStr = request.getParameter("idCorso");

        if (isBlank(idCorsoStr)) {
            setMsg(request, false, "ID corso mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idCorso;
        try {
            idCorso = Integer.parseInt(idCorsoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID corso non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (!corsoExists(idCorso)) {
            setMsg(request, false, "Corso inesistente.");
            forwardByRole(request, response, ruolo);
            return;
        }

        String sql =
                "SELECT e.idVoto, s.Matricola, c.idcorso, e.voto, e.dataRegistrazione, e.stato "
                        + "FROM esiti e "
                        + "JOIN appello a ON e.idApp = a.idAppello "
                        + "JOIN corso c ON a.Materia = c.idcorso "
                        + "JOIN studente s ON e.idStud = s.Matricola "
                        + "WHERE c.idcorso = ?";

        request.setAttribute("votiCorso", fetchVoti(sql, idCorso));
        setMsg(request, true, "Voti caricati.");
        forwardByRole(request, response, ruolo);
    }

    //metodo per visualizzare i voti di un corso per i prof
    private void visualizzaPerCorsoProf(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        String idCorsoStr = request.getParameter("idCorso");

        if (isBlank(idCorsoStr)) {
            setMsg(request, false, "ID corso mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idCorso;
        try {
            idCorso = Integer.parseInt(idCorsoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID corso non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        if (!corsoExists(idCorso)) {
            setMsg(request, false, "Corso inesistente.");
            forwardByRole(request, response, ruolo);
            return;
        }

        Integer idProf = (Integer) request.getSession().getAttribute("idProfessore");
        if (idProf == null) {
            setMsg(request, false, "Sessione professore non valida.");
            forwardByRole(request, response, ruolo);
            return;
        }

        try (Connection con = Connessione.getCon()) {
            if (!isCorsoTuo(con, idCorso, idProf)) {
                setMsg(request, false, "Accesso negato: corso non tuo.");
                forwardByRole(request, response, ruolo);
                return;
            }
        }

        String sql =
                "SELECT e.idVoto, s.Matricola, c.idcorso, e.voto, e.dataRegistrazione, e.stato "
                        + "FROM esiti e "
                        + "JOIN appello a ON e.idApp = a.idAppello "
                        + "JOIN corso c ON a.Materia = c.idcorso "
                        + "JOIN studente s ON e.idStud = s.Matricola "
                        + "WHERE c.idcorso = ?";

        request.setAttribute("votiCorsoProf", fetchVoti(sql, idCorso));
        setMsg(request, true, "Voti caricati.");
        forwardByRole(request, response, ruolo);
    }

    //metodo per far visualizzare agli studenti i propri voti
    private void visualizzaMieiVoti(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        Integer matricola = getMatricolaFromSession(request);

        if (matricola == null) {
            setMsg(request, false, "Sessione scaduta o matricola non valida.");
            response.sendRedirect("index.jsp");
            return;
        }

        String sql =
                "SELECT e.idVoto, s.Matricola, c.idcorso, e.voto, e.dataRegistrazione, e.stato "
                        + "FROM esiti e "
                        + "JOIN appello a ON e.idApp = a.idAppello "
                        + "JOIN corso c ON a.Materia = c.idcorso "
                        + "JOIN studente s ON e.idStud = s.Matricola "
                        + "WHERE e.idStud = ?";

        request.setAttribute("listaVoti", fetchVoti(sql, matricola));
        setMsg(request, true, "Voti caricati.");
        forwardByRole(request, response, ruolo);
    }

    //metodo per far accettare agli studenti i voti ricevuti dai prof
    private void accettaVoto(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        Integer matricola = getMatricolaFromSession(request);
        if (matricola == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String idVotoStr = request.getParameter("idVoto");
        if (isBlank(idVotoStr)) {
            setMsg(request, false, "ID voto mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idVoto;
        try {
            idVoto = Integer.parseInt(idVotoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID voto non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        String sql = "UPDATE esiti SET stato='accettato' WHERE idVoto=? AND idStud=?";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVoto);
            ps.setInt(2, matricola);

            if (ps.executeUpdate() > 0)
                setMsg(request, true, "Voto accettato.");
            else
                setMsg(request, false, "Impossibile accettare il voto.");
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo per far rifiutare agli studenti i voti
    private void rifiutaVoto(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        Integer matricola = getMatricolaFromSession(request);
        if (matricola == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String idVotoStr = request.getParameter("idVoto");
        if (isBlank(idVotoStr)) {
            setMsg(request, false, "ID voto mancante.");
            forwardByRole(request, response, ruolo);
            return;
        }

        int idVoto;
        try {
            idVoto = Integer.parseInt(idVotoStr);
        } catch (NumberFormatException e) {
            setMsg(request, false, "ID voto non valido.");
            forwardByRole(request, response, ruolo);
            return;
        }

        String sql = "UPDATE esiti SET stato='rifiutato' WHERE idVoto=? AND idStud=?";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVoto);
            ps.setInt(2, matricola);

            if (ps.executeUpdate() > 0)
                setMsg(request, true, "Voto rifiutato.");
            else
                setMsg(request, false, "Impossibile rifiutare il voto.");
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo per calcolare la media di tutti i voti di uno studente
    private void mediaStudente(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        Integer matricola = getMatricolaFromSession(request);
        if (matricola == null) {
            setMsg(request, false, "Sessione scaduta o matricola non valida.");
            response.sendRedirect("index.jsp");
            return;
        }

        String sql = "SELECT AVG(voto) AS media FROM esiti WHERE idStud = ? AND stato = 'accettato'";

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricola);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double media = rs.getDouble("media");
                    if (rs.wasNull()) {
                        request.setAttribute("mediaGenerale", null);
                        setMsg(request, false, "Nessun voto accettato per calcolare la media.");
                    } else {
                        request.setAttribute("mediaGenerale", media);
                        setMsg(request, true, "Media calcolata correttamente.");
                    }
                }
            }
        }

        forwardByRole(request, response, ruolo);
    }

    //metodo per calcolare la media di uno studente in un determinato corso
    private void mediaPerCorso(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws Exception {

        Integer matricola = getMatricolaFromSession(request);
        if (matricola == null) {
            setMsg(request, false, "Sessione scaduta o matricola non valida.");
            response.sendRedirect("index.jsp");
            return;
        }

        String sql =
                "SELECT c.idcorso, c.Materia, AVG(e.voto) AS media " +
                        "FROM esiti e " +
                        "JOIN appello a ON e.idApp = a.idAppello " +
                        "JOIN corso c ON a.Materia = c.idcorso " +
                        "WHERE e.idStud = ? AND e.stato = 'accettato' " +
                        "GROUP BY c.idcorso, c.Materia";

        List<Map<String, Object>> medie = new ArrayList<>();

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matricola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idcorso", rs.getInt("idcorso"));
                    m.put("materia", rs.getString("Materia"));
                    m.put("media", rs.getDouble("media"));
                    medie.add(m);
                }
            }
        }

        if (medie.isEmpty()) {
            setMsg(request, false, "Nessun voto accettato per calcolare le medie per corso.");
        } else {
            setMsg(request, true, "Medie per corso calcolate correttamente.");
        }

        request.setAttribute("medieCorsi", medie);
        forwardByRole(request, response, ruolo);
    }

    //  FETCH VOTI GENERICO (usa alias coerenti con le JSP) , per stampare i dati delle visualizzazioni
    private List<Map<String, Object>> fetchVoti(String sql, int... params) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idVoto", rs.getInt("idVoto"));
                    m.put("matricola", rs.getInt("Matricola"));
                    m.put("idcorso", rs.getInt("idcorso"));
                    m.put("voto", rs.getInt("voto"));
                    m.put("data", rs.getDate("dataRegistrazione"));
                    m.put("stato", rs.getString("stato"));
                    list.add(m);
                }
            }
        }

        return list;
    }

    // rispetto alle altre gestioni , qui si è preferito creare metodi appositi per tutti i check
    //dato che vengono ripetuti spesso

    //controlla se un corso esiste
    private boolean corsoExists(int idCorso) throws SQLException {
        String sql = "SELECT 1 FROM corso WHERE idcorso = ?";
        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCorso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Controlla se esiste già un voto per quello studente in quell'appello
    private boolean esisteVoto(Connection con, int idStud, int idApp) throws SQLException {
        String sql = "SELECT COUNT(*) FROM esiti WHERE idStud = ? AND idApp = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idStud);
            ps.setInt(2, idApp);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Controlla se lo studente è prenotato a quell'appello
    private boolean esistePrenotazione(Connection con, int idStud, int idApp) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prenotazione WHERE stud_prenotato = ? AND app_prenotato = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idStud);
            ps.setInt(2, idApp);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private boolean appelloExists(int idApp) throws SQLException {
        String sql = "SELECT 1 FROM appello WHERE idAppello = ?";
        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idApp);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // verifica se il professore è titolare dell'appello (tramite corso)
    private boolean isProfTitolare(Connection con, int idApp, int idProf) throws SQLException {
        String sql =
                "SELECT 1 " +
                        "FROM appello a " +
                        "JOIN corso c ON a.Materia = c.idcorso " +
                        "WHERE a.idAppello = ? AND c.Cattedra = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idApp);
            ps.setInt(2, idProf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // verifica se il corso appartiene al professore
    private boolean isCorsoTuo(Connection con, int idCorso, int idProf) throws SQLException {
        String sql = "SELECT 1 FROM corso WHERE idcorso = ? AND Cattedra = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCorso);
            ps.setInt(2, idProf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //metodi di supporto , oltre ai due già usati nelle altri
    // si è anche implementato un metodo per verificare se una string è vuoto o null
    // e un metodo per ricavare la matricola della sessione dato che c'erano stati problemi a ricavarla in questa servlet
    //sai questi che i check erano cose gia usate nelle altre servlet

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Integer getMatricolaFromSession(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute("matricola");

        if (obj == null) return null;

        if (obj instanceof Integer) return (Integer) obj;

        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private void setMsg(HttpServletRequest req, boolean ok, String msg) {
        req.setAttribute("success", ok);
        req.setAttribute("message", msg);
    }

    private void forwardByRole(HttpServletRequest request, HttpServletResponse response, String ruolo)
            throws ServletException, IOException {

        if (ruolo == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (ruolo.toLowerCase()) {
            case "studente":
                request.getRequestDispatcher("studenteVoti.jsp").forward(request, response);
                break;
            case "professore":
                request.getRequestDispatcher("professoreVoti.jsp").forward(request, response);
                break;
            case "segreteria":
                request.getRequestDispatcher("segreteriaVoti.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }

}