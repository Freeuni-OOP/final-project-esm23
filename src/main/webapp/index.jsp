<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Homepage now lives in HomeServlet + WEB-INF/jsp/home.jsp;
     this file just forwards so existing redirects to "index.jsp" (login/logout) keep working. --%>
<% request.getRequestDispatcher("/HomeServlet").forward(request, response); %>
