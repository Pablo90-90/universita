<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Gestione Corsi</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    //  CONTROLLO ACCESSO
    if (!"segreteria".equals(session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    //  MESSAGGI
    Boolean success = (Boolean) request.getAttribute("success");
    String message = (String) request.getAttribute("message");

    if (message == null) {
        message = (String) session.getAttribute("message");
        success = (Boolean) session.getAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("success");
    }

    List<Map<String,Object>> corsi = (List<Map<String,Object>>) request.getAttribute("corsi");
%>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Segreteria</h2>

        <a href="segreteria.jsp">Home</a>
        <a href="segreteriaStudente.jsp">Studenti</a>
        <a href="segreteriaProfessore.jsp">Professori</a>
        <a href="segreteriaCorsi.jsp" class="active">Corsi</a>
        <a href="segreteriaAppelli.jsp">Appelli</a>
        <a href="segreteriaVoti.jsp">Voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Gestione Corsi</h1>

        <% if (message != null) { %>
            <div class="message <%= (success != null && success) ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">

            <h2>Corsi</h2>

            <!-- AGGIUNTA -->
            <form action="GestioneCorsi" method="post" class="action-form">
                <input type="hidden" name="action" value="aggiungi">
                <input type="text" name="materia" placeholder="Materia" required>
                <input type="number" name="idProf" placeholder="ID Professore" required>
                <input type="submit" value="Aggiungi Corso">
            </form>

            <!-- CANCELLA -->
            <form action="GestioneCorsi" method="post" class="action-form">
                <input type="hidden" name="action" value="cancella">
                <input type="number" name="idcorso" placeholder="ID Corso" required>
                <input type="submit" value="Cancella Corso">
            </form>

            <!-- MOSTRA -->
            <form action="GestioneCorsi" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizza">
                <input type="submit" value="Mostra Corsi">
            </form>

            <% if (corsi != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
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

        </div>

    </div>

</div>

</body>
</html>