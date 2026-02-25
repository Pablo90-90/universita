<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Gestione Voti</title>
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

    List<Map<String,Object>> votiAppello = (List<Map<String,Object>>) request.getAttribute("votiAppello");
    List<Map<String,Object>> votiCorso   = (List<Map<String,Object>>) request.getAttribute("votiCorso");
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

        <h1 style="margin-top:0;">Gestione Voti</h1>

        <% if (message != null) { %>
            <div class="message <%= success != null && success ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <div class="card">
            <h2>Visualizza Voti</h2>

            <!-- VISUALIZZA PER APPELLO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerAppello">
                <input type="number" name="idApp" placeholder="ID Appello" required>
                <input type="submit" value="Mostra per Appello">
            </form>

            <!-- VISUALIZZA PER CORSO -->
            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaPerCorso">
                <input type="number" name="idCorso" placeholder="ID Corso" required>
                <input type="submit" value="Mostra per Corso">
            </form>

            <!--TABELLA VOTI PER APPELLO-->
            <% if (votiAppello != null) { %>
                <h3>Risultati per Appello</h3>
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

            <!--TABELLA VOTI PER CORSO-->
            <% if (votiCorso != null) { %>
                <h3>Risultati per Corso</h3>
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
                    <% for (Map<String,Object> v : votiCorso) { %>
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