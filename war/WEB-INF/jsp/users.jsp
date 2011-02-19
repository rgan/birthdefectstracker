<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        <th>Delete (only if admin role)</th>
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
            <td>
                <a href="javascript:deleteUser('${user.id}')">Delete</a>
            </td>
        </tr>
    </c:forEach>
</table>
<hr>
<h4>Add User</h4>

<p>${saveErrors}</p>

<form name="addUserForm" action="saveuser.do" method="POST">
    <table>
        <tr>
            <th>
                Username:
                <input type="text" name="username" size="30" maxlength="30" value="${user.username}"/>
            </th>
        </tr>
        <tr>
            <th>
                Password:
                <input type="password" name="password" size="30" maxlength="30" value="${user.password}"/>
            </th>
        </tr>
        <tr>
            <th>
                Medical Professional:
                <input type="checkbox" name="medicalProfessional" value="true"
                       <c:if test="${user.medicalProfessional}">checked</c:if> />
            </th>
        </tr>
        <tr>
            <th>
                Roles:
                User: <input type="checkbox" name="roles" value="user" <c:if test="${user.inUserRole}">checked</c:if> /> &nbsp;
                Admin: <input type="checkbox" name="roles" value="admin" <c:if test="${user.inAdminRole}">checked</c:if> />
            </th>
        </tr>
        <tr>
            <td>
                <p class="submit"><input type="submit" value="Add User"/></p>
            </td>
        </tr>
    </table>
</form>

<form name="deleteUserForm" action="deleteuser.do" method="POST">
    <input type="hidden" name="userIdToDelete" value=""/>
</form>