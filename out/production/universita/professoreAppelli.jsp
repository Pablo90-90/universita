<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Gestione Appelli</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    if (!"professore".equalsIgnoreCase((String) session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    Boolean success = (Boolean) request.getAttribute("success");
    String message = (String) request.getAttribute("message");

    List<Map<String,Object>> corsi   = (List<Map<String,Object>>) request.getAttribute("elenco_corsi");
    List<Map<String,Object>> appelli = (List<Map<String,Object>>) request.getAttribute("elenco_appelli");
%>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Professore</h2>

        <a href="professore.jsp">Home</a>
        <a href="professoreAppelli.jsp" class="active">Appelli</a>
        <a href="professorePrenotazione.jsp">Studenti iscritti</a>
        <a href="professoreVoti.jsp">Voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!-- MAIN CONTENT-->
    <div class="main-content">

        <h1>Gestione Appelli</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="grid-2">

            <!--ELENCO CORSI-->
            <div class="card">
                <h2>I tuoi Corsi</h2>

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
                            <td><%= c.get("idCorso") %></td>
                            <td><%= c.get("materia") %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
                <% } %>
            </div>

            <!--ELENCO APPELLI-->
            <div class="card">
                <h2>I tuoi Appelli</h2>

                <% if (appelli != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Appello</th>
                            <th>Data</th>
                            <th>Materia</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> a : appelli) { %>
                        <tr>
                            <td><%= a.get("idAppello") %></td>
                            <td><%= a.get("data") %></td>
                            <td><%= a.get("materia") %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
                <% } %>
            </div>

        </div>

        <!--FORM AZIONI-->
        <div class="card">
            <h2>Aggiungi Appello</h2>

            <form action="GestioneAppello" method="post">
                <input type="hidden" name="action" value="aggiungi">
                <input type="number" name="idcorso" placeholder="ID Corso" required>
                <input type="date" name="data" required>
                <input type="submit" class="button" value="Aggiungi">
            </form>

            <h2>Cancella Appello</h2>

            <form action="GestioneAppello" method="post">
                <input type="hidden" name="action" value="cancella">
                <input type="number" name="idAppello" placeholder="ID Appello" required>
                <input type="submit" class="button red" value="Cancella">
            </form>
        </div>

    </div>

</div>

</body>
</html>