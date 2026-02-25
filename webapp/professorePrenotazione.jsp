<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Studenti Iscritti</title>
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

    List<Map<String,Object>> appelli = (List<Map<String,Object>>) request.getAttribute("elenco_appelli");
    List<Map<String,Object>> studenti = (List<Map<String,Object>>) request.getAttribute("elenco_prenotazioni");
%>

<!-- FORM INVISIBILI PER NAVIGAZIONE -->
<form id="formAppelli" action="GestioneAppello" method="post" style="display:none;">
    <input type="hidden" name="action" value="visualizzaProf">
</form>

<form id="formStudenti" action="GestionePrenotazione" method="post" style="display:none;">
    <input type="hidden" name="action" value="visualizzaPerProfessore">
</form>

<form id="formVoti" action="professoreVoti.jsp" method="get" style="display:none;"></form>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Professore</h2>

        <a href="professore.jsp">Home</a>
        <a href="#" onclick="document.getElementById('formAppelli').submit();">Gestisci Appelli</a>
        <a href="#" onclick="document.getElementById('formStudenti').submit();">Visualizza Studenti</a>
        <a href="#" onclick="document.getElementById('formVoti').submit();">Visualizza Voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Studenti Iscritti agli Appelli</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="grid">

            <!--TABELLA APPELLI-->
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

            <!--FORM SELEZIONE APPELLO-->
            <div class="card">
                <h2>Seleziona Appello</h2>

                <form action="GestionePrenotazione" method="post">
                    <input type="hidden" name="action" value="visualizzaPerProfessore">
                    <input type="number" name="idAppello" placeholder="ID Appello" required>
                    <input type="submit" class="button" value="Mostra Studenti">
                </form>
            </div>

        </div>

        <!--TABELLA STUDENTI-->
        <% if (studenti != null) { %>
        <div class="card">
            <h2>Studenti Prenotati</h2>

            <table>
                <thead>
                    <tr>
                        <th>ID Prenotazione</th>
                        <th>Nome</th>
                        <th>Cognome</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Map<String,Object> s : studenti) { %>
                    <tr>
                        <td><%= s.get("idPrenotazione") %></td>
                        <td><%= s.get("nome") %></td>
                        <td><%= s.get("cognome") %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>

    </div>

</div>

</body>
</html>