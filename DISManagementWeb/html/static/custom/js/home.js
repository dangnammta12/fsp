/* global BootstrapDialog */

var hostPort = location.protocol + "//" + window.location.hostname + ":" + window.location.port;
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
function getCookie(name) {
    var match = document.cookie.match(RegExp('(?:^|;\\s*)' + name + '=([^;]*)'));
    return match ? match[1] : null;
}
$(document).ready(function () {
//    $('#menuTab a:first').tab('show');
//    $(".loading").show();
    var x = getCookie("p");
    if (x === "config") {
        $("#tab_config").tab('show');
    } else if (x === "app") {
        $("#tab_app").tab('show');
        $("#btnAddApp").unbind("click").bind("click", function () {
            addApp();
        });
        renderApps();
    } else {
        $("#tab_agent").tab('show');
        $("#addAgentModalBtn").unbind("click").bind("click", function () {
            addAgent();
        });
        renderAgentTable("agentManagement", false, true);
    }
    getDisInfo();
    $("#tab_agent").unbind("click").bind("click", function () {
        document.cookie = "p=agent";
        $("#addAgentModalBtn").unbind("click").bind("click", function () {
            addAgent();
        });
        renderAgentTable("agentManagement", false, true);
    });

    $("#tab_app").unbind("click").bind("click", function () {
        document.cookie = "p=app";
        $("#btnAddApp").unbind("click").bind("click", function () {
            addApp();
        });
        renderApps();
    });
    $("#tab_config").unbind("click").bind("click", function () {
        document.cookie = "p=config";
    });

});
function renderAgentTable(elemenId, isSelect, isChoosenColumn) {

    $.ajax({
        url: hostPort + "/agent/gets",
        method: 'GET',
        dataType: 'JSON',
        success: function (response) {
            if (response.error_code === 0) {
                $(".loading").hide();
                var sourceData = response.data;
                renderGridAgent(elemenId, isSelect, isChoosenColumn, sourceData);
//                if (isChoosenColumn) {
//                    var parent_data = $("#" + elemenId + " .grid-data");
//                    for (var i = 0; i < sourceData.length; i++) {
//                        var rowData = sourceData[i];
//                        var element = parent_data.find("#change_status_agent_" + rowData.agentId);
//                        $(element).unbind("click").bind('click', {obj: rowData}, function (e) {
//                            setAgentStatus(e.data.obj);
//                        });
//                    }
//                }

            }
        }
    });
    return false;
}

function renderGridAgent(elemenId, isSelect, isShowFull, sourceData) {
    $("#" + elemenId).empty();
    $("#" + elemenId).grid({
        refresh: true,
        selectionMode: false,
        showSortingIndicator: false,
        onValidateError: function () {
            alert("error");
        },
        fields: [
            {field: "select", label: "Select", visible: isSelect, width: "auto", template: function (rowData) {
                    return '<input type="checkbox" name=' + rowData.agentId + ' id="checkbox_' + rowData.agentId + '">';
                }},
            {field: "lastPingTime", label: "LastTimePing", visible: isShowFull, width: "auto", template: function (rowData) {
                    var textClass = "text-success";
                    if(rowData.lastPingTimeNumber === 0){
                        textClass = "text-default";
                    } else if (rowData.lastPingTimeNumber > 1 * 60 * 1000) {
                        textClass = "text-danger";
                    }
                    return '<span id="lastPingTime_' + rowData.agentId + '"><i aria-hidden="true" class="icon-circle ' + textClass + '"></i> ' + rowData.lastPingTime + '</span>';
                }},
            {field: "agentId", label: "AgentId", visible: false, width: "auto"},
            {field: "agentName", label: "Name", visible: true, width: "auto", template: function (rowData) {
                    return '<span id="agentName_' + rowData.agentId + '">' + rowData.agentName + '</span>';
                }},
            {field: "agentDesc", label: "Description", visible: false, width: "auto", template: function (rowData) {
                    return '<span id="agentDesc_' + rowData.agentId + '">' + rowData.agentDesc + '</span>';
                }},
//                        {field: "appIds", label: "Number Apps", visible: true, width: "auto", template: function (rowData) {
//                                return '<span id="appIds_' + rowData.agentId + '">' + rowData.appIds.length + '</span>';
//                            }},
            {field: "agentIp", label: "IP", visible: isShowFull, width: "auto", template: function (rowData) {
                    return '<span id="agentIp_' + rowData.agentId + '">' + rowData.agentIp + '</span>';
                }},
            {field: "agentUrl", label: "AgentUrl", visible: true, width: "auto", template: function (rowData) {
                    return '<span id="agentUrl_' + rowData.agentId + '">' + rowData.agentUrl + '</span>';
                }},
            {field: "status", label: "State", visible: true, width: "auto", template: function (rowData) {
                    var tpl = '';
                    if (rowData.status !== true) {
                        tpl = '<span class="label label-default" id="state_agent_' + rowData.agentId + '">Disabled</span>';
//                                    tpl = '<button id="change_status_agent_' + rowData.agentId + '" type="button" class="btn btn-success" >Active</button>';
                    } else {
                        tpl = '<span class="label label-success" id="state_agent_' + rowData.agentId + '">Enabled</span>';
//                                    tpl = '<button id="change_status_agent_' + rowData.agentId + '" type="button" class="btn btn-default" >Inactive</button>';
                    }
                    return tpl;
                }},
            {field: "command", label: "Action", visible: isShowFull, width: "auto",
                command: [
                    {
                        "label": "Setup",
                        "action": function (params, args) {
                            showSetUpAgent(params.rowData.agentId);
                        },
                        "class": "btn-primary btn-sm margin-btn-grid",
                    }, {
                        "label": "Detail",
                        "action": function (params, args) {
                            showAgentDetail(params.rowData);
                        },
                        "class": "btn-primary btn-sm margin-btn-grid", // margin-btn-grid
                    }, {
                        "label": "Enable",
                        "action": function (params, args) {
                            setAgentStatus(params.rowData.agentId, true);
                        },
                        "class": function (params, args) {
                            if (params.status === false) {
                                return "btn-primary btn-sm margin-btn-grid enable-status";
                            } else {
                                return "btn-primary btn-sm margin-btn-grid enable-status hidden";
                            }
                        }
                    }, {
                        "label": "Disable",
                        "action": function (params, args) {
                            setAgentStatus(params.rowData.agentId, false);
                        },
                        "class": function (params) {
                            if (params.status === true) {
                                return "btn-primary btn-sm margin-btn-grid disable-status";
                            } else {
                                return "btn-primary btn-sm margin-btn-grid disable-status hidden";
                            }
                        }
                    }, {
                        "label": "Delete",
                        "action": function (params, args) {
                            if (params.rowData.status === true) {
                                BootstrapDialog.show({
                                    title: "Error",
                                    message: "Can not delete Agent activing",
                                    buttons: [{
                                            label: "Close",
                                            cssClass: 'btn-primary',
                                            action: function (dialogItself) {
                                                dialogItself.close();
                                            }
                                        }]
                                });
                                return;
                            } else {
                                $.ajax({
                                    url: hostPort + "/agent/delete",
                                    method: 'POST',
                                    data: {agentId: params.rowData.agentId},
                                    dataType: 'JSON',
                                    success: function (response) {
                                        if (response.error_code === 0) {
                                            $(params.el).empty();
                                            $(params.el).hide();
                                            var popup = BootstrapDialog.show({
                                                title: "Information",
                                                message: "Delete Agent success!",
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

                        },
                        "class": "btn-danger btn-sm margin-btn-grid",
                    }
                ],
                commandButtonClass: ["btn-sm"]
            }
        ],
        events: {
            "rowclick": function (e) {
//                console.log(e);
            },
        },
        dataSource: sourceData,
        datatableClass: "table",
        primaryField: "agentId"
    });
}
function showAgentDetail(data) {
    BootstrapDialog.show({
        title: "Agent Detail",
        cssClass: "scale-dialog",
        message: '<form class="form-vertical" id="updateAgentForm">' +
                '<div class="form-group">' +
                '<label class="control-label">AgentId</label>' +
                '<input disabled type="text" class="form-control" id="agentId">' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">Name</label>' +
                '<input type="text" class="form-control" id="agentName" name="agentName" placeholder="Name">' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">Description</label>' +
                '<input type="text" class="form-control" id="agentDesc" placeholder="Description">' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">IP</label>' +
                '<input disabled type="text" class="form-control" id="agentIp"  placeholder="10.10.10.10">' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">Agent URL</label>' +
                '<input type="text" class="form-control" id="agentUrl" placeholder="Agent Url">' +
                '</div><hr>' +
                '<h4 class="page-heading">Application Info</h4>' +
                '<div id="appInfos_agent">' +
                '</div>' +
                '</form>',
        onshown: function () {
            $("#agentId").attr("value", data.agentId);
            $("#agentName").attr("value", data.agentName);
            $("#agentDesc").attr("value", data.agentDesc);
            $("#agentIp").attr("value", data.agentIp);
            $("#agentUrl").attr("value", data.agentUrl);
            renderAppInfoInAgent(data.agentId, "agent");
        },
        buttons: [{
                label: "Update",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var agentName = $("#agentName").val();
                    var agentDesc = $("#agentDesc").val();
//                    var agentIp = $("#agentIp").val();
                    var agentUrl = $("#agentUrl").val();
                    if (agentName === undefined || agentName.trim().length === 0 ||
                            agentUrl === "") {
                        BootstrapDialog.show({
                            title: "Error",
                            message: "Agent name not empty",
                            buttons: [{
                                    label: "Close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }
                                }]
                        });
                        return;
                    }
                    $.ajax({
                        url: hostPort + '/agent/update',
                        method: 'POST',
                        data: {agentId: data.agentId, agentName: agentName,
                            agentDesc: agentDesc, agentUrl: agentUrl},
                        dataType: 'JSON',
                        success: function (response) {
                            if (response.error_code === 0) {
//                                $("#agentName_" + data.agentId).html(agentName);
//                                $("#agentDesc_" + data.agentId).html(agentDesc);
//                                $("#agentIp_" + data.agentId).html(agentIp);
//                                $("#agentUrl_" + data.agentId).html(agentUrl);
                                renderAgentTable("agentManagement", false, true);
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
                    dialogItself.close();
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

function addAgent() {
    BootstrapDialog.show({
        title: "Create new Agent",
        message: '<form class="form-vertical" id="createAgentForm">' +
                '<div class="messages"></div>' +
                '<div class="form-group">' +
                ' <label for="agentName" class="control-label">Name</label>' +
                '<input type="text" class="form-control" id="agentName" name="agentName" placeholder="Name">' +
                '</div>' +
                '<div class="form-group">' +
                ' <label for="agentDesc" class="control-label">Description</label>' +
                '<input type="text" class="form-control" id="agentDesc" name="agentDesc" placeholder="Description">' +
                '</div>' +
//                '<div class="form-group">' +
//                ' <label for="agentIp" class="control-label">IP</label>' +
//                '<input type="text" class="form-control" id="agentIp" name="agentIp" placeholder="10.10.10.10">' +
//                '</div>' +
                '<div class="form-group">' +
                ' <label class="control-label">Agent Url</label>' +
                '<input type="text" class="form-control" id="agentUrl" placeholder="http://host:port">' +
                '</div>' +
                '</form>',
        buttons: [{
                label: "Add New",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var agentName = $("#agentName").val();
                    var agentDesc = $("#agentDesc").val();
//                    var agentIp = $("#agentIp").val();
                    var agentUrl = $("#agentUrl").val();

                    if (agentName === "" || agentName.trim() === "") {
                        $("#agentName").closest('.form-group').addClass('has-error');
                        $("#agentName").after('<p class="text-danger">The Agent Name field is required</p>');
                    }

//                    if (!isIPAddress(agentIp)) {
//                        $("#agentIp").closest('.form-group').addClass('has-error');
//                        $("#agentIp").after('<p class="text-danger">Invalid IP Address</p>');
//                    }

                    if (agentUrl.startsWith("http") === false) {
                        $("#agentUrl").closest('.form-group').addClass('has-error');
                        $("#agentUrl").after('<p class="text-danger">Invalid URL</p>');
                    }
                    $(".messages").html("");
                    $.ajax({
                        url: hostPort + "/agent/add",
                        method: 'POST',
                        data: {agentName: agentName, agentDesc: agentDesc, agentUrl: agentUrl},
                        dataType: 'JSON',
                        success: function (response) {

                            // remove the error 
                            if (response.error_code === 0) {
                                dialogItself.close();
                                renderAgentTable("agentManagement", false, true);
                                showSetUpAgent(response.data.agentId);
                            } else {
                                $(".messages").html('<div class="alert alert-warning alert-dismissible" role="alert">' +
                                        '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                        '<strong> <span class="glyphicon glyphicon-exclamation-sign"></span> </strong>' + response.error_message +
                                        '</div>');
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

function setAgentStatus(agentId, status) {
    $.ajax({
        url: hostPort + "/agent/changestatus",
        method: 'POST',
        dataType: 'JSON',
        data: {agentId: agentId, status: status},
        success: function (response) {
            if (response.error_code === 0) {
                var ele = $("#state_agent_" + agentId);
                if (status === true) {
                    ele.removeClass("label-default").addClass("label-success").html("Enabled");
                    $(".disable-status").removeClass("hidden");
                    $(".enable-status").addClass("hidden");
                } else {
                    $(".disable-status").addClass("hidden");
                    $(".enable-status").removeClass("hidden");
                    ele.removeClass("label-success").addClass("label-default").html("Disabled");
                }

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

function renderAppInfoInAgent(id, pageName) {
    if (id) {
        $.ajax({
            url: hostPort + "/app/gets",
            method: 'GET',
            data: {agentId: id},
            dataType: 'JSON',
            success: function (response) {
                if (response.error_code === 0) {
                    $(".loading").hide();
                    var sourceData = response.data;
                    $("#appInfos_" + pageName).empty();
                    $("#appInfos_" + pageName).grid({
                        refresh: true,
                        fields: [
                            {field: "appId", label: "ID", visible: false, width: "15px"},
                            {field: "appName", label: "Name", visible: true, width: "auto"},
                            {field: "appDesc", label: "Description", visible: true, width: "auto"},
//                            {field: "appIp", label: "Address", visible: true, width: "170px", template: function (rowData) {
//                                    var hostPort = rowData.appIp + " : " + rowData.appPort;
//                                    return hostPort;
//                                }}

                        ],
                        events: {
                            "rowclick": function (e) {
//                                console.log(e);
                            },
                        },
                        dataSource: sourceData,
                        datatableClass: "table",
                        primaryField: "appId"
                    });
                }
            }
        });
        return false;
    }
}
var disInfo = {
}

function getDisInfo() {
    $.ajax({
        url: hostPort + "/getdis",
        type: 'GET',
        dataType: 'JSON',
        success: function (response) {
            if (response.error_code === 0) {
                disInfo = response.data;
                $("#urlInputForm").val(response.data.disConnectionInfo);
                $("#btn_update_url").unbind("click").bind('click', function () {
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
                            url: hostPort + "/updateURL",
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
                $("#addIPAddressBtn").unbind("click").bind('click', function () {
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
//    $.ajax({
//        url: hostPort + "/getdis",
//        type: 'GET',
//        dataType: 'JSON',
//        success: function (response) {
//            if (response.error_code === 0) {
//                disInfo = response.data;
//            } else {
//                console.log("getDisInfo error=" + response);
//            }
//        },
//        error: function (msg) {
//            console.log(msg);
//        }
//
//    });
}

function showSetUpAgent(agentId) {
    BootstrapDialog.show({
        title: "Agent Installation Instructions",
        cssClass: "scale-dialog",
        message: '<form class="form-vertical">' +
                '<div class="form-group">' +
                ' <label class="control-label">1. Open the config file conf/production.config.ini and enter your AgentKey as shown below.</label>' +
                '<input class="form-control" id="agentKey" readonly>' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">2. Open the file conf/puDISKey.pem and enter your DIS Public key as shown below.</label>' +
                '<textarea class="form-control" rows="5" id="disPublicKey" readonly></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">3. Open the file conf/prAgentKey.pem and enter your Agent Private key as shown below.</label>' +
                '<textarea class="form-control" rows="5" id="agentPrivateKey" readonly></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                ' <label class="control-label">4. Open the config file conf/production.config.ini and enter your DIS URL as shown below.</label>' +
                '<input class="form-control" id="baseurl" readonly>' +
                '</div>' +
                '<div class="form-group">' +
                ' <label class="control-label">5. Start the agent in command line</label>' +
                '<textarea class="form-control" rows="3" id="startAgent" readonly>./runserver start</textarea>' +
                '</div>' +
                '</form>',
        onshown: function () {
            $("#agentKey").val(agentId).click(function () {
                $(this).select();
            });
            
            $("#disPublicKey").val(disInfo.disPublicKey).click(function () {
                $(this).select();
            });
            ;
            $("#agentPrivateKey").val(disInfo.agentPrivateKey).click(function () {
                $(this).select();
            });
            $("#baseurl").val("url_dis = " + disInfo.disConnectionInfo).click(function () {
                $(this).select();
            });
            $("#startAgent").click(function () {
                $(this).select();
            });
        },
        buttons: [{
                label: "Done",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}

function addApp() {
    BootstrapDialog.show({
        title: "Create new Application",
        message: '<form class="form-vertical">' +
                '<div class="messages"></div>' +
                '<div class="form-group">' +
                ' <label class="control-label">Name</label>' +
                '<input type="text" class="form-control" id="appName" placeholder="Application Name">' +
                '</div>' +
                '<div class="form-group">' +
                ' <label class="control-label">Description</label>' +
                '<input type="text" class="form-control" id="appDesc" placeholder="App Description">' +
                '</div>' +
                '<div id="listAgent">' +
                '</div>' +
                '</form>',
        onshown: function () {
            renderAgentTable("listAgent", true, false);
        },
        buttons: [{
                label: "Add New",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var appName = $("#appName").val();
                    var appDesc = $("#appDesc").val();

                    if (appName === "" || appName.trim() === "") {
                        $("#appName").closest('.form-group').addClass('has-error');
                        $("#appName").after('<p class="text-danger">The Application Name field is required</p>');
                        return;
                    }
                    var listAgentId = "";
//                    var listAgent = $("#listAgent").data("gonrin").selectedRows("get_ids");
//                    if (listAgent !== undefined && listAgent.length > 0) {
//                        for (var i = 0; i < listAgent.length; i++) {
//                            if (i === listAgent.length - 1) {
//                                listAgentId += listAgent[i].agentId;
//                            } else {
//                                listAgentId += listAgent[i].agentId + ",";
//                            }
//                        }
//
//                    }
                    $("#listAgent .grid-data tr input:checked").each(function () {
                        listAgentId += this.name + ",";
                    });
                    if (listAgentId.endsWith(",")) {
                        listAgentId = listAgentId.substr(0, listAgentId.length - 1);
                    }
                    if (listAgentId === "") {
                        $(".messages").html('<div class="alert alert-warning alert-dismissible" role="alert">' +
                                '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                '<strong> <span class="glyphicon glyphicon-exclamation-sign"></span> </strong>' + "Please choose Agent" +
                                '</div>');
                        return;
                    }
                    $(".messages").html("");
                    $.ajax({
                        url: hostPort + "/app/add",
                        method: 'POST',
                        data: {appName: appName, appDesc: appDesc, agentIds: listAgentId},
                        dataType: 'JSON',
                        success: function (response) {
                            dialogItself.close();
                            if (response.error_code === 0) {
                                renderApps();
                            } else {
                                $(".messages").html('<div class="alert alert-warning alert-dismissible" role="alert">' +
                                        '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                        '<strong> <span class="glyphicon glyphicon-exclamation-sign"></span> </strong>' + response.error_message +
                                        '</div>');
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

function renderApps() {
    $.ajax({
        url: hostPort + "/app/gets",
        method: 'GET',
        dataType: 'JSON',
        success: function (response) {
            if (response.error_code === 0) {
                $(".loading").hide();
                var sourceData = response.data;
                $("#appManagement").empty();
                $("#appManagement").grid({
                    refresh: true,
                    selectionMode: false,
                    showSortingIndicator: false,
                    onValidateError: function () {
                        alert("error");
                    },
                    fields: [
                        {field: "appId", label: "ID", visible: false, width: "10px"},
                        {field: "appName", label: "Name", visible: true, width: "150px", template: function (rowData) {
                                return '<span id="appName_' + rowData.appId + '">' + rowData.appName + '</span>';
                            }},
                        {field: "appDesc", label: "Description", width: "230px", template: function (rowData) {
                                return '<span id="appDesc_' + rowData.appId + '">' + rowData.appDesc + '</span>';
                            }},
                        {field: "agents", label: "Agents", width: "auto", template: function (rowData) {
                                var content_agent = "";
                                if (rowData.agents !== null && rowData.agents.length > 0) {
                                    for (var i = 0; i < rowData.agents.length; i++) {
                                        content_agent += rowData.agents[i].agentName + "( " + rowData.agents[i].agentUrl + " )<br>";
                                    }
                                }
                                return content_agent;
                            }},
                        {field: "command", label: "Action", width: "auto",
                            command: [
                                {
                                    "label": "Setup",
                                    "action": function (params, args) {
                                        showSetUpApp(params.rowData);
                                    },
                                    "class": "btn-primary btn-sm margin-btn-grid",
                                }, {
                                    "label": "Detail",
                                    "action": function (params, args) {
                                        showAppDetail(params.rowData);
                                    },
                                    "class": "btn-primary btn-sm margin-btn-grid", // margin-btn-grid
                                }, {
                                    "label": "Delete",
                                    "action": function (params, args) {
                                        $.ajax({
                                            url: hostPort + "/app/delete",
                                            method: 'POST',
                                            data: {appId: params.rowData.appId},
                                            dataType: 'JSON',
                                            success: function (response) {
                                                if (response.error_code === 0) {
                                                    $(params.el).empty();
                                                    $(params.el).hide();
                                                    var popup = BootstrapDialog.show({
                                                        title: "Information",
                                                        message: "Delete Application success!",
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

                                    },
                                    "class": "btn-danger btn-sm margin-btn-grid",
                                }
                            ],
                            commandButtonClass: ["btn-sm"]
                        }
                    ],
                    events: {
                        "rowclick": function (e) {
//                            console.log(e);
                        },
                    },
                    dataSource: sourceData,
                    datatableClass: "table",
                    primaryField: "appId"
                });

            }
        }
    });
    return false;
}

function showAppDetail(data) {
    BootstrapDialog.show({
        title: "Application Detail",
        cssClass: "scale-dialog",
        message: '<form class="form-vertical">' +
                '<div class="form-group">' +
                '<label class="control-label">ID</label>' +
                '<input type="text" class="form-control" id="appId" readonly>' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">Name</label>' +
                '<input type="text" class="form-control" id="appName" placeholder="App Name">' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">Description</label>' +
                '<input type="text" class="form-control" id="appDesc"  placeholder="App Description">' +
                '</div>' +
                '<h4 class="page-heading">Agent Info</h4>' +
                '<div id="agent_Infos">' +
                '</div>' +
                '</form>',
        onshown: function () {
            $("#appId").attr("value", data.appId);
            $("#appName").attr("value", data.appName);
            $("#appDesc").attr("value", data.appDesc);
            renderGridAgent("agent_Infos", false, false, data.agents);
//            $("#agent_Infos").empty();
//            $("#agent_Infos").grid({
//                refresh: true,
//                selectionMode: false,
//                showSortingIndicator: false,
//                onValidateError: function () {
//                    alert("error");
//                },
//                fields: [
//                    {field: "agentId", label: "ID", visible: false, width: "10px"},
//                    {field: "agentName", label: "Name", visible: true, width: "150px", template: function (rowData) {
//                            return '<span id="agentName_' + rowData.agentId + '">' + rowData.agentName + '</span>';
//                        }},
//                    {field: "agentDesc", label: "Description", width: "230px", template: function (rowData) {
//                            return '<span id="agentDesc_' + rowData.agentId + '">' + rowData.agentDesc + '</span>';
//                        }},
//                    {field: "agentIp", label: "IP", width: "auto", template: function (rowData) {
//                            return '<span id="agentIp_' + rowData.agentId + '">' + rowData.agentIp + '</span>';
//                        }},
//                    {field: "agentUrl", label: "AgentUrl", visible: true, width: "auto", template: function (rowData) {
//                            return '<span id="agentUrl_' + rowData.agentId + '">' + rowData.agentUrl + '</span>';
//                        }},
//                    {field: "status", label: "Status", visible: true, width: "auto", template: function (rowData) {
//                            var tpl = '';
//                            if (rowData.status !== true) {
//                                //onclick="setStatus(' + "'" + rowData.agentId + "'" + "," + false + ')"
//                                tpl = '<button id="change_status_agent_' + rowData.agentId + '" type="button" class="btn btn-success" >Active</button>';
//                            } else {
//                                tpl = '<button id="change_status_agent_' + rowData.agentId + '" type="button" class="btn btn-default" >Inactive</button>';
//                            }
//                            return tpl;
//                        }},
//                ],
//                events: {
//                    "rowclick": function (e) {
//                        console.log(e);
//                    },
//                },
//                dataSource: data.agents,
//                datatableClass: "table",
//                primaryField: "agentId"
//            });
        },
        buttons: [{
                label: "Update",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var appName = $("#appName").val();
                    var appDesc = $("#appDesc").val();
                    if (appName === undefined || appName.trim().length === 0) {
                        BootstrapDialog.show({
                            title: "Error",
                            message: "App name not empty",
                            buttons: [{
                                    label: "Close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }
                                }]
                        });
                        return;
                    }
                    $.ajax({
                        url: hostPort + '/app/update',
                        method: 'POST',
                        data: {appId: data.appId, appName: appName,
                            appDesc: appDesc},
                        dataType: 'JSON',
                        success: function (response) {
                            if (response.error_code === 0) {
                                renderApps();
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
                    dialogItself.close();
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

function showSetUpApp(data) {
    BootstrapDialog.show({
        title: "Application Installation Instructions",
        cssClass: "scale-dialog",
        message: '<form class="form-vertical">' +
                '<div class="form-group">' +
                '<label class="control-label">1. Save your appkey as shown below for authentication every your request to DIS system.</label>' +
                '<textarea class="form-control" rows="2" id="appId" readonly></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">2. Save one in the Urls as shown below for connect to DIS system.</label>' +
                '<textarea class="form-control" rows="5" id="baseUrl" readonly></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                '<label class="control-label">3. Save your AgentKey as shown below for encryption/Decrytion data in your request to DIS system.</label>' +
                '<textarea class="form-control" rows="5" id="puAgentKey" readonly></textarea>' +
                '</div>' +
                '<div class="form-group">' +
                ' <label class="control-label">4. Note: Each request send to the DIS system must include appKey, accountName and data parameters.</label>' +
                '</div>' +
                '</form>',
        onshown: function () {

            $("#appId").val(data.appId).click(function () {
                $(this).select();
            });
            var baseUrls = "";
            for (var i = 0; i < data.agents.length; i++) {
                if (i === data.agents.length - 1) {
                    baseUrls += data.agents[i].agentUrl;
                } else {
                    baseUrls += data.agents[i].agentUrl + "\n or \n";
                }

            }
            $("#baseUrl").val(baseUrls).click(function () {
                $(this).select();
            });
            $("#puAgentKey").val(disInfo.agentPublicKey).click(function () {
                $(this).select();
            });
        },
        buttons: [{
                label: "Done",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}

function addAcceptedIp() {
    BootstrapDialog.show({
        title: "Add IP",
        message: '<form class="form-vertical">' +
                '<div class="form-group">' +
                ' <label class="control-label">Ip Address:</label>' +
                '<input type="text" class="form-control" id="ipAddress" placeholder="10.10.10.10">' +
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
                        url: hostPort + "/addIp",
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
                                url: hostPort + "/removeIp",
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

