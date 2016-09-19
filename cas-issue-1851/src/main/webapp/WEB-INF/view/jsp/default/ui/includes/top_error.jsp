<!DOCTYPE html>

<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
<head>
  <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <!-- START - NE PAS INDEXER -->
     <title>ERREUR - Service d'authentification (<spring:eval expression="@casProperties.getProperty('host.name')" />)</title>
     <!-- STOP - NE PAS INDEXER -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>


    <spring:theme code="standard.custom.css.file" var="customCssFile" />
    <link rel="stylesheet" href="<c:url value="${customCssFile}" />" />
    <link rel="icon" href="<c:url value="/themes/lille1/favicon.ico" />" type="image/x-icon" />

</head>
<body id="cas">

<div id="conteneur_global">
                        <div id="bandeau_haut">
                          <div id="logo">
                            <img src="themes/lille1/images/bandeau_haut02-H.jpg" alt="univ" width="246" height="98"/>
                          </div>
                          <div id="logo-connexion">
                            <h1><spring:message code="screen.welcome.label.connexion" /></h1>
                          </div>
                        </div>
                        <div id="contenu_principal">
			  <div id="bienvenue">
			    <h1><spring:message code="screen.welcome.label.welcome" /></h1>
			    <p><spring:message code="screen.welcome.left" /></p>
			</div>
                        
