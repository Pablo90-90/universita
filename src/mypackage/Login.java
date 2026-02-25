package mypackage;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class Login extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException {

		//parametri dalla form index.jsp
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		//ci serve per capire su quale table operare e per evitare di effettuare la ricerca su più table
		String scelta   = request.getParameter("scelta");

		//check di controllo sui parametri
		if (username == null || password == null || scelta == null ||
				username.isEmpty() || password.isEmpty() || scelta.isEmpty()) {

			request.setAttribute("success", false);
			request.setAttribute("message", "Non hai inserito tutti i dati");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		try (Connection conn = Connessione.getCon()) {

			//switch di indirizzamento verso il metodo giusto
			switch (scelta) {
				case "Studente":
					loginStudente(request, response, conn, username, password);
					return;

				case "Professore":
					loginProfessore(request, response, conn, username, password);
					return;

				case "Segreteria":
					loginSegreteria(request, response, conn, username, password);
					return;

				default:
					request.setAttribute("success", false);
					request.setAttribute("message", "Ruolo non valido");
					request.getRequestDispatcher("index.jsp").forward(request, response);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("success", false);
			request.setAttribute("message", "Errore durante l'operazione");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
	}

	// login studente

	private void loginStudente(HttpServletRequest request,
							   HttpServletResponse response,
							   Connection conn,
							   String username,
							   String password)
			throws SQLException, ServletException, IOException {

		//query di ricerca dove controlliamo se l'utente esiste realmente
		PreparedStatement ps = conn.prepareStatement(
				"SELECT Matricola, nome, cognome, stato, password FROM studente WHERE username=?"
		);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Credenziali studente non valide");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		//con BCrypt controlliamo se la password è corretta
		String hashNelDB = rs.getString("password");

		if (!BCrypt.checkpw(password, hashNelDB)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Password errata");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		//per studente e professore verifichiamo se lo stato dell'account è verificato
		//la segreteria può accettare o rifiutare i nuovi utenti
		//al momento della registrazione lo stato è in ATTESA

		String stato = rs.getString("stato");

		if ("ATTESA".equalsIgnoreCase(stato)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Account in attesa di approvazione dalla segreteria.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		if ("RIFIUTATO".equalsIgnoreCase(stato)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Registrazione rifiutata dalla segreteria.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		//creiamo la sessione e passiamo i parametri per settarli attributi di quest'ultima
		HttpSession session = request.getSession(true);
		session.setAttribute("ruolo", "studente");
		session.setAttribute("matricola", rs.getString("Matricola"));
		session.setAttribute("nome", rs.getString("nome"));
		session.setAttribute("cognome", rs.getString("cognome"));

		response.sendRedirect("studente.jsp");
	}

	//  login professore

	private void loginProfessore(HttpServletRequest request,
								 HttpServletResponse response,
								 Connection conn,
								 String username,
								 String password)
			throws SQLException, ServletException, IOException {

		PreparedStatement ps = conn.prepareStatement(
				"SELECT idProfessore, nome, cognome, stato, password FROM professore WHERE username=?"
		);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Credenziali professore non valide");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		String hashNelDB = rs.getString("password");

		if (!BCrypt.checkpw(password, hashNelDB)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Password errata");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		String stato = rs.getString("stato");

		if ("ATTESA".equalsIgnoreCase(stato)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Account in attesa di approvazione dalla segreteria.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		if ("RIFIUTATO".equalsIgnoreCase(stato)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Registrazione rifiutata dalla segreteria.");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("ruolo", "professore");
		session.setAttribute("idProfessore", rs.getInt("idProfessore"));
		session.setAttribute("nome", rs.getString("nome"));
		session.setAttribute("cognome", rs.getString("cognome"));

		response.sendRedirect("professore.jsp");
	}

	// login segreteria

	private void loginSegreteria(HttpServletRequest request,
								 HttpServletResponse response,
								 Connection conn,
								 String username,
								 String password)
			throws SQLException, ServletException, IOException {

		PreparedStatement ps = conn.prepareStatement(
				"SELECT id_seg, pass FROM segreteria WHERE user=?"
		);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Credenziali segreteria non valide");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		String hashNelDB = rs.getString("pass");

		if (!BCrypt.checkpw(password, hashNelDB)) {
			request.setAttribute("success", false);
			request.setAttribute("message", "Password errata");
			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("ruolo", "segreteria");
		session.setAttribute("id_seg", rs.getInt("id_seg"));

		response.sendRedirect("segreteria.jsp");
	}

}