<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Gestione Voti Professore</title>
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

    if (message == null) {
        message = (String) session.getAttribute("message");
        success = (Boolean) session.getAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("success");
    }

    List<Map<String,Object>> votiAppello   = (List<Map<String,Object>>) request.getAttribute("votiAppello");
    List<Map<String,Object>> votiCorsoProf = (List<Map<String,Object>>) request.getAttribute("votiCorsoProf");
%>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Professore</h2>

        <a href="professore.jsp">Home</a>
        <a href="professoreAppelli.jsp">Appelli</a>
        <a href="professorePrenotazione.jsp">Studenti iscritti</a>
        <a href="professoreVoti.jsp" class="active">Voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Gestione Voti</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">
            <h2>Operazioni sui Voti</h2>

            <!-- AGGIUNGI VOTO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="aggiungi">
                <input type="number" name="idStud" placeholder="Matricola Studente" required>
                <input type="number" name="idApp" placeholder="ID Appello" required>
                <input type="number" name="voto" placeholder="Voto (18-30)" required>
                <input type="submit" value="Aggiungi Voto">
            </form>

            <!-- MODIFICA VOTO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="modifica">
                <input type="number" name="idVoto" placeholder="ID Voto" required>
                <input type="number" name="voto" placeholder="Nuovo Voto (18-30)" required>
                <input type="submit" value="Modifica Voto">
            </form>

            <!-- ELIMINA VOTO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="elimina">
                <input type="number" name="idVoto" placeholder="ID Voto" required>
                <input type="submit" value="Elimina Voto">
            </form>

            <!-- VISUALIZZA PER APPELLO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerAppello">
                <input type="number" name="idApp" placeholder="ID Appello" required>
                <input type="submit" value="Mostra Voti Appello">
            </form>

            <!-- VISUALIZZA PER CORSO (solo corsi del professore) -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerCorsoProf">
                <input type="number" name="idCorso" placeholder="ID Corso" required>
                <input type="submit" value="Mostra Voti Corso">
            </form>

            <!--TABELLA VOTI PER APPELLO-->
            <% if (votiAppello != null) { %>
                <h3>Risultati Appello</h3>
                <table>
                    <thead>
                        <tr>
                            <th>ID Voto</th>
                            <th>Matricola</th>
                            <th>ID Corso</th>
                            <th>Voto</th>
                            <th>Data Registrazione</th>
                            <th>Stato</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> v : votiAppello) { %>
                        <tr>
                            <td><%= v.get("idVoto") %></td>
                            <td><%= v.get("matricola") %></td>
                            <td><%= v.get("idcorso") %></td>
                            <td><%= v.get("voto") %></td>
                            <td><%= v.get("data") %></td>
                            <td><%= v.get("stato") %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>

            <!--TABELLA VOTI PER CORSO (PROF)-->
            <% if (votiCorsoProf != null) { %>
                <h3>Risultati Corso</h3>
                <table>
                    <thead>
                        <tr>
                            <th>ID Voto</th>
                            <th>Matricola</th>
                            <th>ID Corso</th>
                            <th>Voto</th>
                            <th>Data Registrazione</th>
                            <th>Stato</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> v : votiCorsoProf) { %>
                        <tr>
                            <td><%= v.get("idVoto") %></td>
                            <td><%= v.get("matricola") %></td>
                            <td><%= v.get("idcorso") %></td>
                            <td><%= v.get("voto") %></td>
                            <td><%= v.get("data") %></td>
                            <td><%= v.get("stato") %></td>
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