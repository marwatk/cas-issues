<jsp:directive.include file="includes/top.jsp" />
<%@ page pageEncoding="UTF-8" %>
<div id="colonne_droite">
  <div id="zone_formulaire">
  <form id="credentials" action="" method="post">
          <div id="zone_ok"><spring:message code="screen.logout.success" /></div>
  </form>
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
</div>

<jsp:directive.include file="includes/bottom.jsp" />