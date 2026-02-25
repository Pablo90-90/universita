<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>I miei voti</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    if (!"studente".equalsIgnoreCase((String) session.getAttribute("ruolo"))) {
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

    List<Map<String,Object>> voti       = (List<Map<String,Object>>) request.getAttribute("listaVoti");
    Double mediaStud                    = (Double) request.getAttribute("mediaGenerale");
    List<Map<String,Object>> medieCorsi = (List<Map<String,Object>>) request.getAttribute("medieCorsi");
%>

<div class="dashboard">

    <!--SIDEBAR -->
    <div class="sidebar">
        <h2>Studente</h2>

        <a href="studente.jsp">Home</a>
        <a href="studentePrenotazione.jsp">Prenotazioni</a>
        <a href="studenteVoti.jsp" class="active">I miei voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1>I miei voti</h1>

        <% if (message != null) { %>
            <div class="message <%= (success != null && success) ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <!--LISTA VOTI-->
        <div class="card">
            <h2>Voti Registrati</h2>

            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="visualizzaMieiVoti">
                <input type="submit" value="Aggiorna Lista Voti">
            </form>

            <% if (voti != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Voto</th>
                            <th>Matricola</th>
                            <th>ID Corso</th>
                            <th>Voto</th>
                            <th>Data</th>
                            <th>Stato</th>
                            <th>Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> v : voti) { %>
                        <tr>
                            <td><%= v.get("idVoto") %></td>
                            <td><%= v.get("matricola") %></td>
                            <td><%= v.get("idcorso") %></td>
                            <td><%= v.get("voto") %></td>
                            <td><%= v.get("data") %></td>
                            <td><%= v.get("stato") %></td>

                            <td>
                                <% if ("attesa".equals(v.get("stato"))) { %>

                                    <!-- ACCETTA -->
                                    <form action="GestioneVoti" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="accettaVoto">
                                        <input type="hidden" name="idVoto" value="<%= v.get("idVoto") %>">
                                        <input type="submit" value="Accetta">
                                    </form>

                                    <!-- RIFIUTA -->
                                    <form action="GestioneVoti" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="rifiutaVoto">
                                        <input type="hidden" name="idVoto" value="<%= v.get("idVoto") %>">
                                        <input type="submit" value="Rifiuta" class="red">
                                    </form>

                                <% } else { %>
                                    <span class="disabled">Nessuna azione</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>

        </div>

        <!--MEDIA GENERALE-->
        <div class="card">
            <h2>Media Generale</h2>

            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="mediaStudente">
                <input type="submit" value="Calcola Media">
            </form>

            <% if (mediaStud != null) { %>
                <p><strong>Media:</strong> <%= mediaStud %></p>
            <% } %>
        </div>

        <!--MEDIA PER CORSO-->
        <div class="card">
            <h2>Media per Corso</h2>

            <form action="GestioneVoti" method="post" class="action-form">
                <input type="hidden" name="action" value="mediaPerCorso">
                <input type="submit" value="Mostra Medie">
            </form>

            <% if (medieCorsi != null) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID Corso</th>
                            <th>Materia</th>
                            <th>Media</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Map<String,Object> m : medieCorsi) { %>
                        <tr>
                            <td><%= m.get("idcorso") %></td>
                            <td><%= m.get("materia") %></td>
                            <td><%= m.get("media") %></td>
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