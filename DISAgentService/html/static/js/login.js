/* global BootstrapDialog, zmTemplate */
function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
function login() {
    var url = location.protocol + "//" + window.location.hostname + ":"+window.location.port+"/login";
    $(".loading").show();
    var email = $("#email").val();
    var password = $("#password").val();
    if (email === undefined || email.trim().length <= 0 ||
            password === undefined || password.trim().length <= 0) {
        $("#error_message").html("Enter Email and password!");
        $("#error_message").show();
    }else if (!validateEmail(email)){
        $("#error_message").html("Invalid address email!");
        $("#error_message").show();
    }
//    store.remove('sessionKey');
//    if ($.ajaxSettings.headers !== null) {
//        delete $.ajaxSettings.headers['Authorization'];
//    }
//    document.cookie = "sessionkey=\"\";expires=-1;path=\/";
    $.post(url,
            {
                "action": "login",
                "email": email,
                "password": password

            }, {
        "dataType": "json"
    }).done(function (data) {
        $(".loading").hide();
        console.log(data);
        if (data.error_code === 0) {
//            store.set('sessionKey', data.sessionKey);
//
//            $.ajaxSetup({
//                headers: {
//                    'Authorization': data.sessionKey
//                }
//            });
//            document.cookie = "sessionkey=" + data.sessionKey + ";expires=" + data.expire_sessionkey + ";path=\/";
//            var popup = BootstrapDialog.show({
//                cssClass: "resizeBootstrapDialog",
//                title: "Notify",
//                message: data.error_message,
//                buttons: [{
//                        label: "Close",
//                        cssClass: 'btn-primary',
//                        action: function (dialogItself) {
//                            dialogItself.close();
//                        }
//                    }]
//            });
//            setTimeout(function () {
//                popup.close();
                location.href = "/home";
//            }, 100);
        } else {
            $("#error_message").html(data.error_message);
            $("#error_message").show();
        }

    });
}
function resetpass() {
    BootstrapDialog.show({
        title: "Reset password",
        closable: true,
        closeByBackdrop: false,
        closeByKeyboard: false,
        message: "                      <div class=\"form-group\">" +
                "                          <input id=\"email\" type=\"email\" class=\"form-control\" value=\"\" placeholder=\"" + mappingLanguage.label_email + "\" >" +
                "                      </div>",
        buttons: [{
                label: "Change",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var url = location.protocol + "//" + window.location.hostname + "/ajx/dip/user";
                    $(".loading").show();
                    var email = $("#email").val();
                    if (email === undefined || email.trim().length <= 0) {
                        BootstrapDialog.show({
                            cssClass: "resizeBootstrapDialog",
                            title: "Infomation",
                            message: mappingLanguage.title_error_message + mappingLanguage.message_alert_missing_param,
                            buttons: [{
                                    label: "Close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }
                                }]
                        });
                        return false;
                    }
                    $.post(url,
                            {
                                "action": "resetpass",
                                "email": email.trim()

                            }, {
                        "dataType": "json"
                    }).done(function (data) {
                        $(".loading").hide();
                        dialogItself.close();
                        if (data.error_code === 0) {
                            var popup = BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: "Notify",
                                message: data.error_message,
                                buttons: [{
                                        label: "Close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }
                                    }]
                            });
                            setTimeout(function () {
                                popup.close();
                            }, fisConfig.timeoutClose);
                        } else {
                            BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: mappingLanguage.title_error,
                                message: mappingLanguage.title_error_code + data.error_code + mappingLanguage.title_error_message + data.error_message,
                                buttons: [{
                                        label: "Close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }
                                    }]
                            });
                            return false;
                        }

                    });
                }
            }, {
                label: mappingLanguage.btn_cancel,
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}

function focusInputDate() {
    $("#val_birthday").focus();
}
$(document).ready(function () {
    $('#btnlogin').click(function () {
        login();
    });
    $('#register').click(function () {
        location.href = "/register";
    });
});
