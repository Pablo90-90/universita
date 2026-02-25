<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="ISO-8859-1">
    <title>Area Segreteria</title>
    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<%
    if (!"segreteria".equals(session.getAttribute("ruolo"))) {
        response.sendRedirect("index.jsp");
        return;
    }
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
        <a href="segreteriaVoti.jsp">Voti</a>


        <a href="logout.jsp" class="red">Logout</a>
    </div>

    <!--MAIN CONTENT-->
    <div class="main-content">

        <h1 style="margin-top:0;">Area Segreteria</h1>

        <div class="grid">

            <!-- CARD: STUDENTI -->
            <div class="card">
                <h3>Gestione Studenti</h3>
                <p>Accedi alla gestione completa degli studenti.</p>
                <a href="segreteriaStudente.jsp" class="button">Vai</a>
            </div>

            <!-- CARD: PROFESSORI -->
            <div class="card">
                <h3>Gestione Professori</h3>
                <p>Visualizza, aggiungi o modifica i professori.</p>
                <a href="segreteriaProfessore.jsp" class="button">Vai</a>
            </div>

            <!-- CARD: CORSI -->
            <div class="card">
                <h3>Gestione Corsi</h3>
                <p>Gestisci elenco dei corsi disponibili.</p>
                <a href="segreteriaCorsi.jsp" class="button">Vai</a>
            </div>

            <!-- CARD: APPELLI -->
            <div class="card">
                <h3>Gestione Appelli</h3>
                <p>Organizza e modifica gli appelli degli esami.</p>
                <a href="segreteriaAppelli.jsp" class="button">Vai</a>
            </div>

            <!-- CARD: VOTI -->
            <div class="card">
                  <h3>Gestione Voti</h3>
                  <p>Visualizza i voti.</p>
                  <a href="segreteriaVoti.jsp" class="button">Vai</a>
            </div>

        </div>

    </div>

</div>

</body>
</html>
