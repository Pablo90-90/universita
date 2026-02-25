<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Login</title>
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

<!-- ============================
     LOGIN CENTRATO
============================= -->

<div class="dashboard" style="max-width:450px; min-height:auto; padding:40px;">

    <div class="login-card" style="width:100%;">

        <h1 style="text-align:center; margin-bottom:10px; font-size:24px; color:white;">
            Benvenuto nel Portale Universitario
        </h1>

        <h3 style="margin-top:0; color:#1db954; text-align:center;">
            Accesso Portale Universitario
        </h3>

        <% if (message != null) { %>
            <div class="message <%= (success != null && success) ? "success" : "error" %>">
                <%= message %>
            </div>
        <% } %>

        <!-- FORM LOGIN -->
        <form action="login" method="post">

            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>

            <select name="scelta" required>
                <option value="">-- Seleziona Ruolo --</option>
                <option value="Studente">Studente</option>
                <option value="Professore">Professore</option>
                <option value="Segreteria">Segreteria</option>
            </select>

            <input type="submit" value="Accedi">
        </form>

        <!-- LINK REGISTRAZIONE.JSP -->
        <div style="margin-top: 15px; text-align:center;">
            <a href="registrazione.jsp" class="button" style="padding:8px 15px; display:inline-block;">
                Registrati
            </a>
        </div>

    </div>
</div>

</body>
</html>