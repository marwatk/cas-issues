<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" %>


</div> <!-- END #content -->
<div id="securite">
    <spring:message code="screen.logout.security"/> 
<br>
    <spring:message code="screen.welcome.security.l1"/>
</div>
<footer>
  <div id="copyright"><spring:message code="screen.welcome.copyright"/></div>
</footer>

</div> <!-- END #container -->

<!--script src="https://cdnjs.cloudflare.com/ajax/libs/headjs/1.0.3/head.min.js"></script-->
<spring:theme code="cas.javascript.file" var="casJavascriptFile" text="" />
<script type="text/javascript" src="<c:url value="${casJavascriptFile}" />"></script>

</body>
</html>

