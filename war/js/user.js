var Users = function() {
    return {
        add: function() {

            var success = function(data, textStatus) {
                $.modal.close();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.responseText);
            };
            var scope = $("#addUser");
            var postData = {
                "username" : $("input[name='username']", scope).val(),
                "password" : $("input[name='password']", scope).val(),
                "email" : $("input[name='email']", scope).val(),
                "medicalProfessional" : $("input[name='medicalProfessional']", scope).val(),
                "roles" : "user"
            };
            doAjax("POST", "/saveuser.do", postData, success, error);
        },

        showAddUserForm : function () {
            a = []
            a.push('<div id="addUser">');
            a.push('<b>Register</b>');
            a.push('<form name="addUserForm">');
            a.push('<table>')
            a.push('<tr><td>Username:<input type="text" name="username" size="30" maxlength="30"/></td></tr>');
            a.push('<tr><td>Password:<input type="password" name="password" size="30" maxlength="30"/></td></tr>');
            a.push('<tr><td>Email:<input type="text" name="email" size="30" maxlength="30"/></td></tr>');
            a.push('<tr><td>Medical Professional:<input type="checkbox" name="medicalProfessional" value="true"/></td></tr>');
            a.push('<tr><td><a href="javascript:Users.add()">Submit</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" class="simplemodal-close">Close</a></td></tr>');
            a.push('</table></form>');
            a.push('</div>')
            $.modal(a.join(''), {minWidth: 400, minHeight: 150});
        },

        showLoginForm : function() {
            a = []
            a.push('<div id="loginForm">');
            a.push('<b>Register</b>');
            a.push('<form name="loginForm">');
            a.push('<table>')
            a.push('<tr><td>Username:<input type="text" name="username" size="20" maxlength="30"/></td></tr>');
            a.push('<tr><td>Password:<input type="password" name="password" size="20" maxlength="30"/></td></tr>');
            a.push('<tr><td><a href="javascript:login()">Submit</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" class="simplemodal-close">Close</a></td></tr>');
            a.push('</table></form>');
            a.push('</div>')
            $.modal(a.join(''), {minWidth: 350, minHeight: 100});
        },

        showSendMailForm : function(id) {
            a = []
            a.push('<div id="sendMail">');
            a.push('<b>Send Email</b>');
            a.push('<form name="sendEmailForm">');
            a.push('<table>')
            a.push('<tr><td><input type="hidden" name="id" value="'+ id + '"/></td></tr>');
            a.push('<tr><td>Subject:<input type="text" name="subject" size="30" maxlength="30"/></td></tr>');
            a.push('<tr><td><textarea name="body" rows="5" cols="30"></textarea></td></tr>');
            a.push('<tr><td><a href="javascript:Users.sendEmail()">Send email</a>&nbsp;<a href="#" class="simplemodal-close">Close</a></td></tr>');
            a.push('</table></form>');
            a.push('</div>')
            $.modal(a.join(''), {minWidth: 400, minHeight: 200});
        },

        sendEmail : function() {
           var success = function(data, textStatus) {
                $.modal.close();
            };
            var error = function(XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.responseText);
            };
            var scope = $("#sendMail");
            var postData = {
                "id" : $("input[name='id']", scope).val(),
                "subject" : $("input[name='subject']", scope).val(),
                "fromEmail" : $("input[name='fromEmail']", scope).val(),
                "body" : $("textarea[name='body']", scope).val()
            };
            doAjax("POST", "/email.do", postData, success, error);
        }
    };
}();