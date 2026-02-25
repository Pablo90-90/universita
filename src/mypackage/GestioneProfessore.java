package mypackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/GestioneProfessore")
public class GestioneProfessore extends HttpServlet {

    //si ripete la stessa metodologia usata in studente
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"segreteria".equals(session.getAttribute("ruolo"))) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Nessuna azione specificata");
            request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "aggiungi": aggiungiProfessore(request, response); break;
            case "cancella": cancellaProfessore(request, response); break;
            case "visualizza": visualizzaProfessori(request, response); break;
            default:
                request.setAttribute("success", false);
                request.setAttribute("message", "Azione non riconosciuta");
                request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
        }
    }

    // metodo aggiungiProfessore
    private void aggiungiProfessore(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");

        if (user == null || password == null || nome == null || cognome == null ||
                user.trim().isEmpty() || password.trim().isEmpty() ||
                nome.trim().isEmpty() || cognome.trim().isEmpty()) {

            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM professore WHERE username=?"
            );
            check.setString(1, user);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Errore: username già esistente");
                request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
                return;
            }

            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO professore (username, password, nome, cognome) VALUES (?, ?, ?, ?)"
            );

            stmt.setString(1, user);
            stmt.setString(2, password);
            stmt.setString(3, nome);
            stmt.setString(4, cognome);

            int righe = stmt.executeUpdate();

            if (righe > 0) {
                request.setAttribute("success", true);
                request.setAttribute("message", "Professore inserito correttamente");
            } else {
                request.setAttribute("success", false);
                request.setAttribute("message", "Errore: nessun professore inserito");
            }

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
        }

        request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
    }

    //  metodo cancellaProfessore
    private void cancellaProfessore(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("idProf");

        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Dati inseriti non correttamente");
            request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
            return;
        }

        int idProf;

        try {
            idProf = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "L'ID professore deve essere un numero intero");
            request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
            return;
        }

        try (Connection con = Connessione.getCon()) {

            PreparedStatement check = con.prepareStatement(
                    "SELECT COUNT(*) FROM professore WHERE idProfessore=?"
            );
            check.setInt(1, idProf);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Nessun professore trovato con questo ID");
                request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
                return;
            }

            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM professore WHERE idProfessore=?"
            );
            stmt.setInt(1, idProf);
            stmt.executeUpdate();

            request.setAttribute("success", true);
            request.setAttribute("message", "Professore cancellato correttamente");

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore Database: " + e.getMessage());
        }

        request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
    }

    //  metodo per visualizzare tutti i professori
    private void visualizzaProfessori(HttpServletRequest request,
                                      HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection con = Connessione.getCon();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT idProfessore, nome, cognome, stato FROM professore"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> professori = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> p = new HashMap<>();
                p.put("idProfessore", rs.getInt("idProfessore"));
                p.put("nome", rs.getString("nome"));
                p.put("cognome", rs.getString("cognome"));
                p.put("stato", rs.getString("stato"));
                professori.add(p);
            }

            request.setAttribute("elenco_professori", professori);
            request.setAttribute("success", true);
            request.setAttribute("message", "Professori caricati correttamente");

        } catch (SQLException e) {
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
        }

        request.getRequestDispatcher("segreteriaProfessore.jsp").forward(request, response);
    }

}
