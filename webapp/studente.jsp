<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Area Studente</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    if (!"studente".equalsIgnoreCase((String) session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }

    String nome = (String) session.getAttribute("nome");
    String cognome = (String) session.getAttribute("cognome");
%>

<div class="dashboard">

    <!--SIDEBAR-->
    <div class="sidebar">
        <h2>Studente</h2>

        <a href="studente.jsp" class="active">Home</a>
        <a href="studentePrenotazione.jsp">Prenotazioni</a>
        <a href="studenteVoti.jsp">I miei voti</a>

        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Benvenuto <%= nome %> <%= cognome %></h1>

        <div class="grid">

            <!-- CARD: PRENOTAZIONI -->
            <div class="card">
                <h3>Prenotazione Esami</h3>
                <p>Visualizza corsi, appelli disponibili e gestisci le tue prenotazioni.</p>
                <a href="studentePrenotazione.jsp" class="button">Vai</a>
            </div>

            <!-- CARD: VOTI -->
            <div class="card">
                <h3>I miei voti</h3>
                <p>Controlla i voti, accetta o rifiuta e visualizza la tua media.</p>
                <a href="studenteVoti.jsp" class="button">Vai</a>
            </div>

        </div>

    </div>

</div>

</body>
</html>
