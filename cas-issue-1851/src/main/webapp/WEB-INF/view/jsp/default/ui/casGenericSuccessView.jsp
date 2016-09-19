<jsp:directive.include file="includes/top.jsp" />
<%@ page pageEncoding="UTF-8" %>
<div id="colonne_droite">
  <div id="zone_formulaire">  

    <div class="errors" id="status">
      <h2><spring:message code="screen.success.header" /></h2>
      <p><spring:message code="screen.success.success" arguments="${principal.id}"/></p>
      <p><spring:message code="screen.success.security" /></p>
    </div> 
  </div>
<div id="zone_probleme">
<h2> Vous avez un problème ?</h2>
<p>
Cliquez sur
<a href="<spring:eval expression="@casProperties.getProperty('sesame.base.url')" />/index.html">SESAME</a>
pour le résoudre.
</p>
<h2>Services numériques :</h2>
<p>
<a href="http://cri.univ-lille1.fr/Services-proposes/Etat-des-services">Calendrier</a>
des interventions et état des services
</p>
</div>  
<jsp:directive.include file="includes/bottom.jsp" />

