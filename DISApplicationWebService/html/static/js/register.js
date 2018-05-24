/* global BootstrapDialog */

$(document).ready(function () {
    $('#datepicker')
            .datepicker({
                format: 'dd/mm/yyyy'
            }).on('changeDate', function (e) {
        // Revalidate the date field
        $('#eventForm').formValidation('revalidateField', 'birthday');
    });
    $('#eventForm').formValidation({
        framework: 'bootstrap',
        icon: {
            valid: 'fa fa-ok',
            /// invalid: 'fa fa-remove',
            validating: 'fa fa-refresh'
        },
        fields: {
            email: {
                validators: {
                    notEmpty: {
                        message: 'Email empty'
                    },
                    regexp: {
                        regexp: '^[^@\\s]+@([^@\\s]+\\.)+[^@\\s]+$',
                        message: 'Invalid email'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: 'password empty'
                    }
                }
            },
            fullname: {
                validators: {
                    notEmpty: {
                        message: 'Fullname empty'
                    }
                }
            },
  //          gender: {
  //              validators: {
  //                  notEmpty: {
  //                      message: 'Gender empty'
  //                  }
  //              }
  //          },
//            department: {
//                validators: {
//                    notEmpty: {
//                        message: '{{validate_department}}'
//                    }
//                }
//            },
            birthday: {
                validators: {
//                    notEmpty: {
//                        message: 'Birthday empty'
//                    },
                    date: {
                        format: 'DD/MM/YYYY',
                        message: 'invalid format birthday'
                    }
                }
            },
            address: {
                validators: {
//                    notEmpty: {
//                        message: 'Address empty'
//                    }
                }
            },
            confirm_password: {
                validators: {
                    notEmpty: {
                        message: 'Password confirm empty'
                    },
                    identical: {
                        field: 'password',
                        message: 'Password not match'
                    }
                }
            },
            phone: {
                validators: {
//                    notEmpty: {
//                        message: 'Phone empty'
//                    },
                    regexp: {
                       // regexp: /^[0-9]+$/,					   
					   regexp: /^[\+]?[(]?\d+[)]?[-\s\.]?[\.\d+]+$/,
                        message: 'Invalid phonenumber'
                    }
                }
            },
        }
    }).on('err.validator.fv', function (e, data) {
        // $(e.target)    --> The field element
        // data.fv        --> The FormValidation instance
        // data.field     --> The field name
        // data.element   --> The field element
        // data.validator --> The current validator name

        data.element
                .data('fv.messages')
                // Hide all the messages
                .find('.help-block[data-fv-for="' + data.field + '"]').hide()
                // Show only message associated with current validator
                .filter('[data-fv-validator="' + data.validator + '"]').show();
    });
});
function createUser() {
    var url = location.protocol + "//" + window.location.hostname + ":"+window.location.port+"/register";
    if ($('#eventForm').data('formValidation').validate().getInvalidFields().length === 0) {
//        var username = $("#val_username").val();
        var password = $("#val_password").val();
        var pass_confirm = $("#val_confirm_password").val();
        var fullname = $("#val_fullname").val();
        var department = $("#val_department").val();
        var email = $("#val_email").val();
        var address = $("#val_address").val();
        var birthday = $("#val_birthday").val();
        var gender = $("#val_gender").val(); //0-male,1-female
        var phone = $("#val_phone").val();
        if (pass_confirm !== password) {
            $("#error_message").html("Password not match!");
            $("#alert_message").show();
            return false;
        }
        if (fullname === undefined || fullname === "" ||
                password === undefined || password === "" ||
//                department === undefined || department === "" ||
                email === undefined || email === ""
//              ||  address === undefined || address === "" 
           //   ||  gender === undefined || gender === ""
           //   ||  birthday === undefined || birthday === ""
				) {
            BootstrapDialog.show({
                cssClass: "resizeBootstrapDialog",
                title: "Error",
                message: "Missing parameter!",
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
                    "action": "register",
                    "password": password,
                    "fullname": fullname,
                    "email": email,
                    "department": department,
                    "address": address,
                    "gender": gender,
                    "birthday": birthday,
                    "phone": phone,
                    "type": 1//member

                }, {
            "dataType": "json"
        }).done(function (data) {
            $(".loading").hide();
            console.log(data);
            if (data.error_code === 0) {
                var popup = BootstrapDialog.show({
                    cssClass: "resizeBootstrapDialog",
                    title: "Notify",
                    message: "Register success!",
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
                    window.open(location.protocol + "//" + window.location.hostname + ":"+window.location.port+"/login", "_self");
                }, 1000);
                return false;
            } else {
                var message = data.error_message;
                console.log(message);
                BootstrapDialog.show({
                    cssClass: "resizeBootstrapDialog",
                    title: "Error",
                    message: message,
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
}