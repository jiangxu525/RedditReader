<%-- 
    Document   : UploadImage
    Created on : Jul 2, 2020, 9:37:29 AM
    Author     : jiang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Upload Image</title>
    </head>
    <body>

        <%
            String basePath = request.getScheme()
                    + "://"
                    + request.getServerName()
                    + ":"
                    + request.getServerPort()
                    + request.getContextPath()
                    + "/";

            pageContext.setAttribute("basePath", basePath);
        %>
    <base href = "<%=basePath%>">

    <div style="text-align: center;">
        <div style="display: inline-block; text-align: left;">
            <form method="post" action="ImageUploadView" enctype="multipart/form-data">
                <input type="file" name="photo" >
                    <p>
                        Title: <br>
                        <input type="text" name="title"><br>
                    </p>
                <input type="submit" name="add" value="Add">
            </form>

            <p color=red>
                <font color=red size=4px>
                ${requestScope.result} ${requestScope.errorMessage}
                </font>
            </p>
            </body>
            </html>
