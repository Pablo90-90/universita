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
    if (!"segreteria".equals(session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    Boolean success = (Boolean) request.getAttribute("success");
    String message = (String) request.getAttribute("message");

    if (message == null) {
        message = (String) session.getAttribute("message");
        success = (Boolean) session.getAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("success");
    }

    List<Map<String,Object>> appelli = (List<Map<String,Object>>) request.getAttribute("elenco_appelli");
%>

<div class="dashboard">

    <!-- SIDEBAR-->
    <div class="sidebar">
        <h2>Segreteria</h2>

        <a href="segreteria.jsp">Home</a>
        <a href="segreteriaStudente.jsp">Studenti</a>
        <a href="segreteriaProfessore.jsp">Professori</a>
        <a href="segreteriaCorsi.jsp">Corsi</a>
        <a href="segreteriaAppelli.jsp">Appelli</a>
        <a href="segreteriaVoti.jsp" class="active">Voti</a>


        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Gestione Appelli</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">
            <h2>Appelli</h2>

            <!-- MOSTRA APPELLI PER CORSO -->
            <form action="GestioneAppello" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerCorso">
                <input type="number" name="idcorso" placeholder="ID Corso" required>
                <input type="submit" value="Mostra Appelli per Corso">
            </form>

            <!-- MOSTRA TUTTI GLI APPELLI -->
            <form action="GestioneAppello" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaTutti">
                <input type="submit" value="Mostra Tutti gli Appelli">
            </form>

            <% if (appelli != null) { %>
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

        </div>

    </div>

</div>

</body>
</html>