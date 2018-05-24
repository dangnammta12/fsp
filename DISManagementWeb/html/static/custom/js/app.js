/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var numberOfAgent;
$(document).ready(function () {
    $("#app").html(renderAppManagement());
    renderApplicationTable();
    addNewApp();
});

function renderApplicationTable() {
    $.ajax({
        url: hostPort + "/apps/getapps",
        method: 'GET',
        dataType: 'JSON',
        success: function (response) {
            if (response.error_code === 0) {
                var sourceData = new Array();
                var arrData = response.data;
                for (var i = 0; i < arrData.length; i++) {
                    var arr = {};
                    arr.appId = arrData[i].appId;
                    arr.appName = arrData[i].appName;
                    arr.appDesc = arrData[i].appDesc;
                    arr.appIp = arrData[i].appIp;
                    arr.appPort = arrData[i].appPort;
                    arr.agentIds = arrData[i].agentIds;
                    sourceData.push(arr);
                }
            }

            $("#appManagement").empty();
            $("#appManagement").grid({
                refresh: true,
                fields: [
                    {field: "appId", label: "ID", visible: false, width: "10px"},
                    {field: "appName", label: "Application Name", visible: true, width: "150px"},
                    {field: "appDesc", label: "Application Description", visible: true, width: "230px"},
                    {field: "appId", label: "Host:Port", visible: true, width: "30px", template: function (rowData) {
                            return rowData.appIp + " : " + rowData.appPort;
                        }},
                    {field: "agentIds", label: "Number of Agent", visible: true, width: "10px", template: function (rowData) {
                            numberOfAgent = rowData.agentIds;

                            return numberOfAgent.length.toString();
                        }},

                    {field: "appId", label: "Action", visible: true, width: "5px", template: function (rowData) {
                            var actions = '<button type="submit" data-toggle="modal" data-target="#agentInfoModal" class="btn btn-primary" onclick="agentDetail(' + "'" + rowData.appId + "'" + ')"><span class="glyphicon glyphicon glyphicon glyphicon-pencil"></span>  Edit</button>' +
                                    '&nbsp;&nbsp;<button type="submit" data-toggle="modal" data-target="#displayDisInfoModal" class="btn btn-danger" onclick="getDisInfo(' + "'" + rowData.appId + "'" + ')"><span class="glyphicon glyphicon glyphicon-trash"></span>  Delete</button>';

                            return actions;
                        }}
                ],
                dataSource: sourceData,
                datatableClass: "table",
                primaryField: "agentAcceptedIp"
            });
        }
    });
}

function addNewApp() {
    $("#addAppModalBtn").on('click', function () {
        //alert("Hello");
        var url = hostPort + "/gets";
        $.getJSON(url, function (response) {
            $.each(response.data, function (i, field) {
                var option = $('<option value = "' + field.agentId + '">' + field.agentName + '</option>');
                $("#multi-select").append(option);
            });
            $('#multi-select').multiselect();
        });

        $("#addNewAppModal").html(renderAddNewApp());

        //reset form
        $("#createApplicationForm")[0].reset();

        //remove error message
        $(".form-group").removeClass('has-error').removeClass('has-success');
        $(".text-danger").remove();

        //empty the message div
        $(".messages").html("");

        $("#createApplicationForm").unbind('submit').bind('submit', function () {
            $(".form-group").removeClass('has-error').removeClass('has-success');
            $(".text-danger").remove();
            var form = $(this);
            var appName = $("#appName").val();
            var appDesc = $("#appDesc").val();
            var appIp = $("#appIp").val();
            var appPort = $("#appPort").val();
            var selectednumbers = [];
            if (appName === "") {
                $("#appName").closest('.form-group').addClass('has-error');
                $("#appName").after('<p class="text-danger">The Application Name field is required</p>');
            }

            if (appIp === "") {
                $("#appIp").closest('.form-group').addClass('has-error');
                $("#appIp").after('<p class="text-danger" style=" width: max-content;">The IP Address field is required</p>');
            }

            if (!validator.isIP(appIp)) {
                $(".text-danger").remove();
                $("#appIp").closest('.form-group').removeClass('has-error').addClass('has-error');
                $("#appIp").after('<p class="text-danger" style=" width: max-content;">The IP Address is invalid format.</p>');
            }

            if (appPort === "") {
                $("#appPort").closest('.form-group').addClass('has-error');
                $("#appPort").after('<p class="text-danger" style=" width: max-content;">The Port field is required</p>');
            }

            if (!validator.isPort(appPort)) {
                $(".text-danger").remove();
                $("#appPort").closest('.form-group').removeClass('has-error').addClass('has-error');
                $("#appPort").after('<p class="text-danger" style=" width: max-content;">The Port is invalid</p>');
            }

            if ($('#multi-select :selected').length === 0) {
                $("#multi-select").closest('.form-group').addClass('has-error');
                $("#select-error-msg").text('At least an agent is choosen. ').css("color", "#a94442");
            } else {
                $("#select-error-msg").text('').css("color", "#FFFFFF");
                $('#multi-select :selected').each(function (i, selected) {
                    selectednumbers[i] = $(selected).val();

                });
            }

            if (appName && appIp && appPort && $('#multi-select :selected').length > 0) {
                console.log($('#multi-select :selected').length);
                $.ajax({
                    url: hostPort + "/apps/newapp",
                    method: 'POST',
                    data: {appName: appName, appDesc: appDesc, appIp: appIp, appPort: appPort, agentIds: selectednumbers.toString()},
                    dataType: 'JSON',
                    success: function (response) {
                        $(".form-group").removeClass('has-error').removeClass('has-success');
                        if (response.error_code === 0) {
                            $(".messages").html('<div class="alert alert-success alert-dismissible" role="alert">' +
                                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                    '<strong> <span class="glyphicon glyphicon-ok-sign"></span> </strong>' + response.error_message +
                                    '</div>');
                            $("#createApplicationForm")[0].reset();
                            //       console.log(response.error_message);
                            renderApplicationTable();
                            renderAgentTable();
                        } else {
                            $(".messages").html('<div class="alert alert-warning alert-dismissible" role="alert">' +
                                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                    '<strong> <span class="glyphicon glyphicon-exclamation-sign"></span> </strong>' + response.error_message +
                                    '</div>');
                        }
                    }
                });
            }

            return false;

        });

    });


}