<%@ page import="org.jasig.cas.web.support.WebUtils"%>
<jsp:directive.include file="default/ui/includes/top_error.jsp" />
  <div id="colonne_droite">
    <div id="zone_formulaire">
        <div id="status" class="errors">
    <h2>${pageContext.errorData.statusCode} - <spring:message code="screen.blocked.header" /></h2>

    <%
        Object casAcessDeniedKey = request.getAttribute(WebUtils.CAS_ACCESS_DENIED_REASON);
        request.setAttribute("casAcessDeniedKey", casAcessDeniedKey);

    %>

    <c:choose>
        <c:when test="${not empty casAcessDeniedKey}">
            <p><spring:message code="${casAcessDeniedKey}" /></p>
        </c:when>
    </c:choose>
    <p><%=request.getAttribute("javax.servlet.error.message")%></p>
    <p><spring:message code="AbstractAccessDecisionManager.accessDenied"/></p>
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
