<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<link href="css/birthdefects.css" title="escape" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="js/jquery-1.2.6.js"></script>
<script type="text/javascript">
    function deleteUser(id) {
        $("input[name='userIdToDelete']").val(id);
        $("form[name='deleteUserForm']").submit();
    }
</script>
<h4>All Users</h4>

<table border="1">
    <tr>
        <thead>
        <th>Name</th>
        <th>Roles</th>
        <th>Medical Professional</th>
        <security:authorize ifAllGranted="admin">
            <th>Delete</th>
        </security:authorize>
        </thead>
    </tr>
    <c:forEach var="user" items="${users}">
        <tr>
            <td>${user.username}</td>
            <td>
                <c:forEach var="role" items="${user.roles}">
                    ${role}
                </c:forEach>
            </td>
            <td><c:if test="${user.medicalProfessional}">Yes</c:if></td>
            <security:authorize ifAllGranted="admin">
                <td>
                    <a href="javascript:deleteUser('${user.id}')">Delete</a>
                </td>
            </security:authorize>
        </tr>
    </c:forEach>
</table>
<hr>
<security:authorize ifAllGranted="admin">
<h4>Add User</h4>
<spring:hasBindErrors name="user">
    <div id="error_wrapper">
        <ul id="errors">
            <c:forEach items="${errors.allErrors}" var="error">
                <c:if test="${!empty error.code}">
                    <li>
                        <spring:message code="${error.code}" arguments="${error.arguments}"/>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>

<form:form name="addUserForm" modelAttribute="user" action="saveuser.do">
    <table>
        <tr>
            <th>
                Username:
                <form:input path="username" size="30" maxlength="30"/>
            </th>
        </tr>
        <tr>
            <th>
                Password:
                <form:input path="password" size="30" maxlength="30"/>
            </th>
        </tr>
        <tr>
            <th>
                Medical Professional:
                <form:checkbox path="medicalProfessional"/>
            </th>
        </tr>
        <tr>
            <th>
                Roles:
                User: <form:checkbox path="roles" value="user"/> &nbsp;
                Admin: <form:checkbox path="roles" value="admin"/>
            </th>
        </tr>
        <tr>
            <td>
                <p class="submit"><input type="submit" value="Add User"/></p>
            </td>
        </tr>
    </table>
</form:form>
</security:authorize>
<form name="deleteUserForm" action="deleteuser.do" method="POST">
    <input type="hidden" name="userIdToDelete" value=""/>
</form>