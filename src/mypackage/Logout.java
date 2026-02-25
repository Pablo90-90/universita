package mypackage;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class Logout extends HttpServlet {
    private static final long serialVersionUID = 1L;

    //metodo doGet per invalidare la sessione e reindirizzare all'index
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //controlla se esiste una sessione
        HttpSession session = request.getSession(false); // false = non crea sessione nuova
        if (session != null) {
            session.invalidate(); // invalida la sessione
        }

        // reindirizza alla home o alla pagina di login
        response.sendRedirect("index.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // riporta il POST al GET
    }

}          