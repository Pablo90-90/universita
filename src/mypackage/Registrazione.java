package mypackage;

import java.io.IOException;
//libreria per eccezioni IO(input output)
//libreria per query e operazioni col db
import java.sql.*;
//libreria per le servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
//libreria BCrypt per hashare le password , cosi da evitare furti di password
import org.mindrot.jbcrypt.BCrypt;

//dichiariamo che l'indirizzo della servlet
@WebServlet("/registrazione")

//classe registrazione che estende HttpServlet
public class Registrazione extends HttpServlet {

//Overridiamo il metodo doPost di HttpServlet
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        //passaggio di parametro dalla form di registrazione.jsp
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nome     = request.getParameter("nome");
        String cognome  = request.getParameter("cognome");
        String scelta   = request.getParameter("scelta"); // Studente / Professore

        // controlli sui parametri dalla form
        if (username == null || password == null || nome == null || cognome == null || scelta == null ||
                username.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty() || scelta.isEmpty()) {

            request.setAttribute("success", false);
            request.setAttribute("message", "Non hai inserito tutti i dati");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }

        // hashiamo la password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = Connessione.getCon()) {

            //controllo su entrambe le table studente e professore
            PreparedStatement checkGlobal = conn.prepareStatement(
                    "SELECT username FROM studente WHERE username=? " +
                            "UNION " +
                            "SELECT username FROM professore WHERE username=?"
            );
            checkGlobal.setString(1, username);
            checkGlobal.setString(2, username);
            ResultSet rsGlobal = checkGlobal.executeQuery();

            if (rsGlobal.next()) {
                request.setAttribute("success", false);
                request.setAttribute("message", "Utente già esistente nel portale");
                request.getRequestDispatcher("registrazione.jsp").forward(request, response);
                return;
            }

            // switchiamo in base al ruolo selezionato
            switch (scelta) {

                case "Studente":
                    registraStudente(request, response, conn, username, hashedPassword, nome, cognome);
                    return;

                case "Professore":
                    registraProfessore(request, response, conn, username, hashedPassword, nome, cognome);
                    return;

                default:
                    request.setAttribute("success", false);
                    request.setAttribute("message", "Ruolo non valido");
                    request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("success", false);
            request.setAttribute("message", "Errore durante l'operazione");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
        }
    }

    //metodo registrazione studente
    private void registraStudente(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Connection conn,
                                  String username,
                                  String hashedPassword,
                                  String nome,
                                  String cognome)
            throws SQLException, ServletException, IOException {

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO studente (username, password, nome, cognome, stato) VALUES (?, ?, ?, ?, 'ATTESA')");
        ps.setString(1, username);
        ps.setString(2, hashedPassword);
        ps.setString(3, nome);
        ps.setString(4, cognome);
        ps.executeUpdate();

        request.setAttribute("success", true);
        request.setAttribute("message", "Registrazione avvenuta. Attendere conferma dalla segreteria.");
        request.getRequestDispatcher("registrazione.jsp").forward(request, response);
    }

    //metodo registrazione studente
    private void registraProfessore(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Connection conn,
                                    String username,
                                    String hashedPassword,
                                    String nome,
                                    String cognome)
            throws SQLException, ServletException, IOException {

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO professore (username, password, nome, cognome, stato) VALUES (?, ?, ?, ?, 'ATTESA')");
        ps.setString(1, username);
        ps.setString(2, hashedPassword);
        ps.setString(3, nome);
        ps.setString(4, cognome);
        ps.executeUpdate();

        request.setAttribute("success", true);
        request.setAttribute("message", "Registrazione avvenuta. Attendere conferma dalla segreteria.");
        request.getRequestDispatcher("registrazione.jsp").forward(request, response);
    }

}