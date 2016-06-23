<jsp:directive.include file="default/ui/includes/top_error.jsp" />
  <c:url var="url" value="/login">
    <c:param name="service" value="${param.service}" />
    <c:param name="renew" value="true" />
  </c:url>
  <div id="colonne_droite">
    <div id="zone_formulaire">
        <div id="status" class="errors">
    <h2><spring:message code="screen.unavailable.heading" /></h2>
    <p><spring:message code="screen.unavailable.message" /></p>
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
  </div>
<jsp:directive.include file="default/ui/includes/bottom.jsp" />
