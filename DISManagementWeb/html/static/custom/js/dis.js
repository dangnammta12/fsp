/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var url = location.protocol + "//" + window.location.hostname + ":" + window.location.port;

function isUrlValid(userInput) {
    var res = userInput.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z‌​]{2,6}\b([-a-zA-Z0-9‌​@:%_\+.~#?&=]*)/g);
    if (res === null || res === undefined)
        return false;
    else
        return true;
}

function isIPAddress(inputText)
{
    var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if (inputText.match(ipformat)) {
        return true;
    } else {
        return false;
    }
}
var arrIP = [];

$(document).ready(function () {
    $.ajax({
        url: url + "/getdis",
        type: 'GET',
        dataType: 'JSON',
        success: function (response) {
            if (response.error_code === 0) {
                $("#urlInputForm").val(response.data.disConnectionInfo);
                $("#btn_update_url").click(function () {
                    var url_value = $("#urlInputForm").val();
//                    if (isUrlValid(url_value) === false) {
                    if (url_value.startsWith("http") === false) {
                        BootstrapDialog.show({
                            title: "Error",
                            message: "invalid url, please check again!",
                            buttons: [{
                                    label: "Close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }
                                }]
                        });
                    } else {
                        $.ajax({
                            url: url + "/updateURL",
                            type: 'post',
                            dataType: 'json',
                            data: {disConnectionInfo: url_value},
                            success: function (response) {
                                if (response.error_code === 0) {
                                    var popup = BootstrapDialog.show({
                                        title: "Information",
                                        message: "Update success!",
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
                                    }, 3000);
                                } else {
                                    BootstrapDialog.show({
                                        title: "Error",
                                        message: response.error_message,
                                        buttons: [{
                                                label: "Close",
                                                cssClass: 'btn-primary',
                                                action: function (dialogItself) {
                                                    dialogItself.close();
                                                }
                                            }]
                                    });
                                }
                            }
                        });
                    }
                });
                $("#addIPAddressBtn").click(function () {
                    addAcceptedIp();
                });
                arrIP = response.data.agentAcceptedIp;
                renderAcceptIpTable(response.data.agentAcceptedIp);
            }
        },
        erorr: function () {
            BootstrapDialog.show({
                title: "Error",
                message: "Can not connect to server, please check  your connection",
                buttons: [{
                        label: "Close",
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }
                    }]
            });
        }

    });
});

function addAcceptedIp() {
    BootstrapDialog.show({
        title: "Add new application",
        message: '<form class="form-vertical">' +
                '<div class="form-group">' +
                ' <label class="control-label">Ip Address:</label>' +
                '<input type="text" class="form-control" id="ipAddress" placeholder="enter address IP">' +
                '</div>' +
                '</form>',
        buttons: [{
                label: "Add New",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var addressIp = $("#ipAddress").val();
                    if (isIPAddress(addressIp) === false) {
                        $("#ipAddress").closest('.form-group').addClass('has-error');
                        $("#ipAddress").next('p').remove();
                        $("#ipAddress").after('<p class="text-danger">Invalid IP Address</p>');
                        return;
                    }
                    if (arrIP.indexOf(addressIp) >= 0) {
                        $("#ipAddress").closest('.form-group').addClass('has-error');
                        $("#ipAddress").next('p').remove();
                        $("#ipAddress").after('<p class="text-danger">IP Address existed!</p>');
                        return;
                    }
                    $.ajax({
                        url: url + "/addIp",
                        type: 'POST',
                        data: {agentAcceptedIp: addressIp},
                        dataType: 'JSON',
                        success: function (data) {
                            if (data.error_code === 0) {
                                dialogItself.close();
                                var popup = BootstrapDialog.show({
                                    title: "Information",
                                    message: "Add IP success!",
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
                                }, 3000);
                                arrIP.push(addressIp);
                                renderAcceptIpTable(arrIP);
                            } else {
                                BootstrapDialog.show({
                                    title: "Error",
                                    message: data.error_message,
                                    buttons: [{
                                            label: "Close",
                                            cssClass: 'btn-primary',
                                            action: function (dialogItself) {
                                                dialogItself.close();
                                            }
                                        }]
                                });
                            }

                        }
                    });
                }
            }, {
                label: "Close",
                cssClass: 'btn-default',
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}

function renderAcceptIpTable(listIP) {
    var sourceData = new Array();
    for (var i = 0; i < listIP.length; i++) {
        var ipLst = {};
        ipLst.id = (i + 1);
        ipLst.agentAcceptedIp = listIP[i];
        sourceData.push(ipLst);
    }
    $("#agentIpLst").empty();
    $("#agentIpLst").grid({
        refresh: true,
        fields: [
            {field: "id", label: "ID", visible: true, width: "150px"},
            {field: "agentAcceptedIp", label: "Accepted Ip", width: "auto", visible: true},
            {field: "command", label: "Action", width: "auto",
                command: [
                    {
                        "label": "Delete",
                        "action": function (params, args) {
                            $.ajax({
                                url: url + "/removeIp",
                                type: 'POST',
                                data: {agentAcceptedIp: params.rowData.agentAcceptedIp},
                                dataType: 'JSON',
                                success: function (data) {
                                    if (data.error_code === 0) {
                                        arrIP.splice(arrIP.indexOf(params.rowData.agentAcceptedIp), 1);
                                        renderAcceptIpTable(arrIP);
                                        var popup = BootstrapDialog.show({
                                            title: "Information",
                                            message: "remove IP success!",
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
                                        }, 3000);


                                    } else {
                                        BootstrapDialog.show({
                                            title: "Error",
                                            message: data.error_message,
                                            buttons: [{
                                                    label: "Close",
                                                    cssClass: 'btn-primary',
                                                    action: function (dialogItself) {
                                                        dialogItself.close();
                                                    }
                                                }]
                                        });
                                    }

                                }
                            });
                        },
                        "class": "btn-danger btn-sm margin-btn-grid",
                    }
                ],
                commandButtonClass: ["btn-sm"]}
        ],
        dataSource: sourceData,
        datatableClass: "table",
        primaryField: "agentAcceptedIp"
    });

    return false;
}




