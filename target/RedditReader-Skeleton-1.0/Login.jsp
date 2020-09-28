<%-- 
    Document   : Login
    Created on : Jul 3, 2020, 10:02:26 PM
    Author     : jiang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <div class ="errorMsg">
            <span class="errorMsg">
                ${empty requestScope.msg?"Please Login":requestScope.msg}
            </span>
            <br>
        </div>

        <div class="form">
            <form action="LoginServlet" method="post">
                <label>USER NAME: </label>
                <input type="text" name="username" value="${requestScope.username}">
                <br><br>
                <label>PASSWORD:   </label>
                <input type="password" name="password">
                <br>                <br>
                <input type="submit" value="login">
            </form>
        </div>
    </body>
</html>
