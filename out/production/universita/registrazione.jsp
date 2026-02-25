<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Registrazione</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    Boolean success = (Boolean) request.getAttribute("success");
    String message = (String) request.getAttribute("message");

    if (message == null) {
        message = (String) session.getAttribute("message");
        success = (Boolean) session.getAttribute("success");
        session.removeAttribute("message");
        session.removeAttribute("success");
    }
%>

<!--CARD REGISTRAZIONE CENTRATA-->
<div class="dashboard" style="max-width:450px; min-height:auto; padding:40px;">

    <div class="login-card" style="width:100%;">

        <!-- TITOLO PRINCIPALE -->
        <h1 style="text-align:center; margin-bottom:10px; font-size:24px; color:white;">
            Benvenuto nella pagina di Registrazione
        </h1>

        <h3 style="margin-top:0; color:#1db954; text-align:center;">
            Registrazione Portale Universitario
        </h3>

        <!-- MESSAGGI -->
        <% if (message != null) { %>
            <div class="message <%= (success != null && success) ? "success" : "error" %>">
                <%= message %>
            </div>

            <% if ("Utente già esistente nel portale".equals(message)) { %>
                <div style="margin-top: 10px; text-align:center;">
                    <a href="index.jsp" class="button" style="padding:6px 12px; display:inline-block;">
                        Hai già un account? Accedi
                    </a>
                </div>
            <% } %>
        <% } %>

        <!-- FORM REGISTRAZIONE -->
        <form action="registrazione" method="post">

            <input type="text" name="nome" placeholder="Nome" required>
            <input type="text" name="cognome" placeholder="Cognome" required>

            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>

            <select name="scelta" required>
                <option value="">-- Seleziona Ruolo --</option>
                <option value="Studente">Studente</option>
                <option value="Professore">Professore</option>
            </select>

            <input type="submit" value="Registrati">
        </form>

        <!-- LINK TORNA AL LOGIN -->
        <div style="margin-top: 15px; text-align:center;">
            <a href="index.jsp" class="button" style="padding:8px 15px; display:inline-block;">
                Torna al Login
            </a>
        </div>

    </div>
</div>

</body>
</html>
