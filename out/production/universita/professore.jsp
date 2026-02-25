<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Area Professore</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    if (!"professore".equalsIgnoreCase((String) session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    String nome = (String) session.getAttribute("nome");
    String cognome = (String) session.getAttribute("cognome");
%>

<!-- FORM INVISIBILI -->
<form id="formAppelli" action="GestioneAppello" method="post" style="display:none;">
    <input type="hidden" name="action" value="visualizzaProf">
</form>

<form id="formStudenti" action="GestionePrenotazione" method="post" style="display:none;">
    <input type="hidden" name="action" value="visualizzaPerProfessore">
</form>

<div class="dashboard">

    <div class="sidebar">
        <h2>Professore</h2>

        <a href="professore.jsp">Home</a>
        <a href="#" onclick="document.getElementById('formAppelli').submit();">Gestisci Appelli</a>
        <a href="#" onclick="document.getElementById('formStudenti').submit();">Visualizza Studenti</a>
        <!-- QUI: vai alla JSP, NON al servlet -->
        <a href="professoreVoti.jsp">Visualizza Voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <div class="main-content">

        <h1>Benvenuto Prof. <%= nome %> <%= cognome %></h1>

        <div class="grid">

            <div class="card">
                <h2>Gestisci Appelli</h2>
                <p>Aggiungi, visualizza e cancella i tuoi appelli.</p>
                <a href="#" class="button" onclick="document.getElementById('formAppelli').submit();">Vai</a>
            </div>

            <div class="card">
                <h2>Visualizza Studenti</h2>
                <p>Controlla gli studenti iscritti ai tuoi appelli.</p>
                <a href="#" class="button" onclick="document.getElementById('formStudenti').submit();">Vai</a>
            </div>

            <!-- CARD: VOTI -->
            <div class="card">
                <h2>Gestione Voti</h2>
                <p>Visualizza e gestisci i voti dei tuoi corsi.</p>
                <!-- QUI: link diretto alla JSP corretta -->
                <a href="professoreVoti.jsp" class="button">Vai</a>
            </div>

        </div>

    </div>

</div>

</body>
</html>
