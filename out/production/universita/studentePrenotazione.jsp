<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Prenotazione Esami</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    // ============================
    //  CONTROLLO ACCESSO STUDENTE
    // ============================
    if (!"studente".equalsIgnoreCase((String) session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    // ============================
    //  MESSAGGI
    // ============================
    Boolean success = (Boolean) request.getAttribute("success");
    String message = (String) request.getAttribute("message");

    // ============================
    //  DATI DA SERVLET
    // ============================
    List<Map<String,Object>> corsi        = (List<Map<String,Object>>) request.getAttribute("corsi");
    List<Map<String,Object>> appelli      = (List<Map<String,Object>>) request.getAttribute("elenco_appelli");
%>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Studente</h2>

        <a href="studente.jsp">Home</a>
        <a href="studentePrenotazione.jsp" class="active">Prenotazioni</a>
        <a href="studenteVoti.jsp">I miei voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1>Prenotazione Esami</h1>

        <% if (message != null) { %>
            <div class="message <%= (success != null && success) ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">

            <!-- MOSTRA SEMPRE I CORSI-->
            <h2>Corsi Disponibili</h2>

            <form action="GestioneCorsi" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizza">
                <input type="submit" value="Aggiorna Corsi">
            </form>

            <% if (corsi != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Corso</th>
                            <th>Materia</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> c : corsi) { %>
                        <tr>
                            <td><%= c.get("idcorso") %></td>
                            <td><%= c.get("materia") %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>

            <!--MOSTRA APPELLI DEL CORSO -->
            <h2>Visualizza Appelli del Corso</h2>

            <form action="GestioneAppello" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerCorso">
                <input type="number" name="idcorso" placeholder="ID Corso" required>
                <input type="submit" value="Mostra Appelli">
            </form>

            <% if (appelli != null) { %>
                <h3>Appelli Disponibili</h3>
                <table>
                    <thead>
                        <tr>
                            <th>ID Appello</th>
                            <th>Data</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> a : appelli) { %>
                        <tr>
                            <td><%= a.get("idAppello") %></td>
                            <td><%= a.get("data") %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>

            <!--PRENOTAZIONE APPELLO-->
            <h2>Prenotati a un Appello</h2>

            <form action="GestionePrenotazione" method="post" class="action-form">
                <input type="hidden" name="action" value="aggiungi">
                <input type="number" name="idAppello" placeholder="ID Appello" required>
                <input type="submit" value="Prenotati">
            </form>

            <!--LE TUE PRENOTAZIONI-->
            <h2>Le tue prenotazioni</h2>

            <form action="GestionePrenotazione" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaMie">
                <input type="submit" value="Aggiorna Prenotazioni">
            </form>

            <%
                List<Map<String,Object>> prenotazioni =
                    (List<Map<String,Object>>) request.getAttribute("elenco_prenotazioni");
            %>

            <% if (prenotazioni != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Prenotazione</th>
                            <th>Data Appello</th>
                            <th>Materia</th>
                            <th>Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> p : prenotazioni) { %>
                        <tr>
                            <td><%= p.get("idPrenotazione") %></td>
                            <td><%= p.get("data") %></td>
                            <td><%= p.get("materia") %></td>
                            <td>
                                <form action="GestionePrenotazione" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="cancella">
                                    <input type="hidden" name="idPrenotazione" value="<%= p.get("idPrenotazione") %>">
                                    <input type="submit" value="Cancella" class="button red">
                                </form>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>

        </div>

    </div>

</div>

</body>
</html>