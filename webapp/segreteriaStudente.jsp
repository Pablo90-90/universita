<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Gestione Studenti</title>
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

    List<Map<String,Object>> studenti = (List<Map<String,Object>>) request.getAttribute("elenco_studenti");
%>

<div class="dashboard">

    <!--SIDEBAR-->
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

        <h1 style="margin-top:0;">Gestione Studenti</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">
            <h2>Studenti</h2>

            <!-- AGGIUNTA -->
            <form action="GestioneStudente" method="post" class="action-form">
                <input type="hidden" name="action" value="aggiungi">
                <input type="text" name="user" placeholder="Username" required>
                <input type="password" name="password" placeholder="Password" required>
                <input type="text" name="nome" placeholder="Nome" required>
                <input type="text" name="cognome" placeholder="Cognome" required>
                <input type="submit" value="Aggiungi">
            </form>

            <!-- CANCELLA -->
            <form action="GestioneStudente" method="post" class="action-form">
                <input type="hidden" name="action" value="cancella">
                <input type="number" name="matricola" placeholder="Matricola" required>
                <input type="submit" value="Cancella">
            </form>

            <!-- MOSTRA -->
            <form action="GestioneStudente" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizza">
                <input type="submit" value="Mostra Elenco">
            </form>

            <% if (studenti != null) { %>
            <table>
                <thead>
                    <tr>
                        <th>Matricola</th>
                        <th>Nome</th>
                        <th>Cognome</th>
                        <th>Stato</th>
                        <th>Azioni</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Map<String,Object> s : studenti) { %>
                    <tr>
                        <td><%= s.get("matricola") %></td>
                        <td><%= s.get("nome") %></td>
                        <td><%= s.get("cognome") %></td>
                        <td><%= s.get("stato") %></td>

                        <td>
                            <form action="GestioneRichiesta" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="Approva">
                                <input type="hidden" name="ruolo" value="Studente">
                                <input type="hidden" name="id" value="<%= s.get("matricola") %>">
                                <button class="btn-approve">&#10004;</button>
                            </form>

                            <form action="GestioneRichiesta" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="Rifiuta">
                                <input type="hidden" name="ruolo" value="Studente">
                                <input type="hidden" name="id" value="<%= s.get("matricola") %>">
                                <button class="btn-reject">&#10006;</button>
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