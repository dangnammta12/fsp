/* global BootstrapDialog */
function getTimeString(timeInMiliseconds) {
    var date = new Date(timeInMiliseconds);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    return day + "/" + month + "/" + year;
}
function getTemplateContentHome() {
    return '<div class="row">' +
            '<div class="col-md-12">' +
            '<div class="box box-primary" style="margin-bottom:0px; min-height: 946px;">' +
            '<div class="box-header hidden">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<h1 class="page-header">My Storage</h1>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div id="body_home" class="box-body">' +
            '</div>' +
            '<div id="contextMenu" class="dropdown clearfix">' +
            '<ul id="ul_menu_right" class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu" style="display:block;margin-bottom:5px;">' +
            '</ul>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';
}
function renderPage(FOLDERID, typePage) {
//    console.log("renderPage FOLDERID=" + FOLDERID);
    var sourceData = new Array();
    var url = "";
//    var checkFirst = false;
    if (FOLDERID !== undefined && FOLDERID > 0) {
        url = fsp.url + "/folder/get?folderId=" + FOLDERID;
    } else if (typePage === "shared") {
        $("#shared_with_me").addClass("active");
        $("#mystorage").removeClass("active");
        $("#btn_new").hide();
        url = fsp.url + "/folder/shared/get";
    } else {
        $("#mystorage").addClass("active");
        $("#shared_with_me").removeClass("active");
        $("#btn_new").show();
        url = fsp.url + "/folder/owner/get";
//        checkFirst = true;
    }
    $(".content-header").removeClass("hidden");
    $(".content").html(getTemplateContentHome());
    $(".loading").show();
    $.getJSON(url, function (data) {
        $(".loading").hide();
        if (data !== null && data.error_code === 0) {
            fsp.currentFolderId = data.data.folderId;
//            console.log("currentFolderId=" + fsp.currentFolderId)
            var path = data.data.path.split('/');
            if (typePage !== "shared" && path.length >= 1 && $.isNumeric(path[0])) {
                fsp.currentRootOwnerId = parseInt(path[0]);
            }
//            if (checkFirst === true) {
//                fsp.currentRootOwnerId = data.data.folderId;
//            }
            var arrFolder = data.data.subFolders;
            var arrFile = data.data.files;
            renderMenuVertical(FOLDERID, data.data.subFolders);
            renderMenuHorizontal(data.data.pathFull);

            for (var i = 0; i < arrFolder.length; i++) {
                var folderInfo = arrFolder[i];
                var itemFolder = {};
                itemFolder.type = 1;//folder
                itemFolder.name = folderInfo.folderName;
                itemFolder.id = "folder_" + folderInfo.folderId;
                itemFolder.ownerName = folderInfo.ownerName;
                itemFolder.path = folderInfo.path;
                itemFolder.status = 0;//status only support for FileInfo
                itemFolder.size = 0;//size only support for FileInfo
                itemFolder.createTime = getTimeString(folderInfo.createTime);
                sourceData.push(itemFolder);
            }
            for (var i = 0; i < arrFile.length; i++) {
                var fileInfo = arrFile[i];
                var itemFile = {};
                itemFile.type = 0;//file
                itemFile.name = fileInfo.fileName;
                itemFile.id = "file_" + fileInfo.fileId;
                if (fileInfo.ownerName === undefined || fileInfo.ownerName === "") {
                    itemFile.ownerName = fsp.viewerId;
                } else {
                    itemFile.ownerName = fileInfo.ownerName;
                }

                itemFile.path = data.data.path;
                itemFile.status = fileInfo.fileStatus;
                itemFile.size = fileInfo.fileSize;
                itemFile.percentUpload = fileInfo.percentUpload;
                if (fileInfo.endUploadingTime === undefined || fileInfo.endUploadingTime <= 0) {
                    var d = new Date();
                    itemFile.createTime = getTimeString(d.getTime());
                } else {
                    itemFile.createTime = getTimeString(fileInfo.endUploadingTime);
                }

                sourceData.push(itemFile);
            }
        } else if (data.error_code === 1002) {
            location.href = "/login";
        } else {
            $("#body_home").html("<p style=\"margin:20px;\">Error " + data.error_message + "</p>");
            return false;
        }
        $("#body_home").empty();
        $("#body_home").grid({
            refresh: true,
            selectionMode: "multiple", // "multiple", "single", false
            showSortingIndicator: true,
            onValidateError: function () {
                alert("error");
            },
            fields: [
                {field: "id", label: "ID", visible: false, width: "70px"},
                {field: "name", label: "Name", visible: true, width: "350px", "sortable": {order: "desc"}, template: function (rowData) {
                        var columnName = "";
                        var isIconImage = false;
                        if (rowData.type === 1) { //isFolder
                            columnName = '<i class="fa fa-folder icon-folder"></i>';
                        } else {
                            var itemName = rowData.name.toLowerCase();
                            if (itemName.endsWith(".zip")
                                    || itemName.endsWith(".rar")
                                    || itemName.endsWith("tar.gz")) {
                                columnName = '<i class="icon-archive icon-file"></i>';
                                isIconImage = true;
                            } else if (itemName.endsWith(".doc")
                                    || itemName.endsWith(".docx")) {
                                columnName = '<i class="icon-word icon-file"></i>';
                                isIconImage = true;
                            } else if (itemName.endsWith(".xls")
                                    || itemName.endsWith(".xlsx")) {
                                columnName = '<i class="icon-excel icon-file"></i>';
                                isIconImage = true;
                            } else if (itemName.endsWith(".pdf")) {
                                columnName = '<i class="icon-pdf icon-file"></i>';
                                isIconImage = true;
                            } else if (itemName.endsWith(".png")
                                    || itemName.endsWith(".jpg")
                                    || itemName.endsWith(".jpeg")
                                    || itemName.endsWith(".gif")) {
                                columnName = '<i class="fa fa-file-image-o icon-file"></i>';

                            } else if (itemName.endsWith(".ppt")
                                    || itemName.endsWith(".pptx")) {
                                columnName = '<i class="icon-powerpoint icon-file"></i>';
                                isIconImage = true;
                            } else if (itemName.endsWith(".txt")) {
                                columnName = '<i class="fa fa-file-text-o icon-file"></i>';
                            } else {
                                columnName = '<i class="fa  fa-file-o icon-folder"></i>';
                            }

                        }
                        if (isIconImage === true) {
                            columnName += '<span class="column-name" style="padding-left:37px;line-height:2.071;">' + rowData.name + '</span>';
                        } else {
                            columnName += '<span class="column-name" >' + rowData.name + '</span>';
                        }
                        if (rowData.ownerName === "Me" || rowData.ownerName === fsp.viewerId || fsp.viewerId === "") {

                        } else {
                            columnName += ' <i class="fa fa-users"></i>';
                        }
                        return columnName;
                    }},
                {field: "status", label: "Status", visible: false},
                {field: "path", label: "Path", visible: false},
                {field: "percentUpload", label: "percentUpload", visible: false},
                {field: "type", label: "type", visible: false},
                {field: "ownerName", label: "Owner", visible: true, width: "150px"},
                {field: "createTime", label: "Last modified", "sortable": {order: "desc"}, width: "150px", visible: true},
                {field: "fileSize", label: "File Size", visible: true, width: "150px", template: function (rowData) {
                        if (rowData.type === 1) {
                            return "—";
                        } else if (rowData.status === 1) {
                            var resp = '<div id="filestatus_' + rowData.id + '" class="progress progress-sm active border-solid">' +
                                    '    <div id="processing_' + rowData.id + '" class="progress-bar progress-bar-success progress-bar-striped" role="progressbar" style="width: ' + rowData.percentUpload + '%">' +
                                    ' </div>' +
                                    '</div>';
                            setTimeout(function (rowData) {
                                getFileInfo(rowData.id);
                            }, 10, rowData);
                            return resp;
                        } else if (rowData.status === 2) {
                            return sizeOf(rowData.size);
                        } else {
                            return "<div id=\"filestatus_" + rowData.id + "\"><label class=\"label label-" + rowData.status + "\">" + rowData.status + "</label></div>";
                        }


                    }},
            ],
            dataSource: sourceData,
            primaryField: "id",
            selectedTrClass: "active-row",
            datatableClass: "table",
            paginationMode: false
                    /*filters: {
                     id: {$gt:3} if(id=1){return success;
                     },
                     pagination: {
                     page: 1,
                     pageSize: 100
                     },*/

        });
        if (sourceData.length <= 0) {
            if (fsp.typePage === "owner") {
//                $("#body_home").html('<div onclick="showUploadForm()" class="col-xs-offset-4 col-xs-4" id="filedrag">Click to upload file</div>');

                var html_form_upload = '<form id="form_upload_drapdrop" method="POST" action="/upload/browser" enctype="multipart/form-data" role="form" style="padding:20px 100px;" autocomplete="off">' +
                        '                   <fieldset>' +
                        '                       <div  class="col-xs-offset-4 col-xs-4" id="filedrag">Click to upload file</div>' +
                        '                           <input type="hidden" id="parentId_browser" name="parentId_browser">' +
                        '                       <input id="file_upload" class="hidden" type="file"  name="file">' +
                        '                   </fieldset>' +
                        '               </form>';
                $("#body_home").html(html_form_upload);

                initFiledrag();
                $("#filedrag").click(function (e) {
                    e.stopPropagation();
                    e.preventDefault();
                    $("#file_upload").click();
                    $("#file_upload").on('change', function () {
//                        $('#form_upload_drapdrop #parentId_browser').val(fsp.currentFolderId);
                         $("#form_upload_drapdrop").submit();
                    });
                });
                $("#form_upload_drapdrop").submit(function (event) {
                    var files = $('#file_upload')[0].files;
                    event.preventDefault();
                    sendRequestUpload(fsp.currentFolderId, files[0]);
                });
            } else {
                $("#body_home").html('<div style="margin:10px;" ><label>Not found shared item</label></div>');
            }


            return false;
        }
        var listRow = $(".grid_row");
        for (var i = 0; i < listRow.length; i++) {
            var row = $(listRow[i]);
            row.dblclick(function (event) {
                event.stopPropagation();
                var parent = $(this);
                var rowData = parent.data("row_data");
                if (rowData.type === 1) { //folder
                    var folderId = rowData.id.replace("folder_", "");
                    renderPage(folderId, fsp.typePage);
                }
                return false;
            });
            row.contextmenu(function (event) {
                event.stopPropagation();
                var parent = $(this);
//                var rowData = parent.data("row_data");
                parent.find("td").click();
                $("#contextMenu").css({
                    top: event.pageY - 145 + "px",
                    left: event.pageX - 245 + "px"
                });
                showContextMenu(this);
                return false;
            });
        }

        $(document).on("click", ".grid_row td", function (event) {
            event.stopPropagation();
            event.preventDefault();
            var parent = $(this).closest("tr");
            var rowData = parent.data("row_data");
            if (event.ctrlKey) {
                $("#body_home").data('gonrin').selectedRows("add_id", rowData);
                $("#body_home").data('gonrin').selectedRows("mark_selected", rowData);
            } else {
                $("#body_home").data('gonrin').selectedRows("clear_all_ids");
                $("#body_home").data('gonrin').selectedRows("mark_page_deselected");
                $("#body_home").data('gonrin').selectedRows("add_id", rowData);
                $("#body_home").data('gonrin').selectedRows("mark_selected", rowData);
            }

        });
        //make sure menu closes on any click
        $('body').click(function () {
            $("#contextMenu").hide();
        });
        return false;
    });

}

function renderMenuHorizontal(args) {
//    var content = "";
    var objMenu = $("#menu_horizontal");
    objMenu.html("");
    var li = $("<li>");
    objMenu.append(li);
    var tag_a = $("<a>").attr({"href": "javascript:void(0);"});
    li.append(tag_a);
    if (fsp.typePage === "shared") {
        tag_a.html('<i class="fa fa-dashboard"></i> Shared with me');
        tag_a.click(function (e) {
            e.stopPropagation();
            renderPage(-1, fsp.typePage);
        });
//        content = '<li><a href="javascript:void(0);"><i class="fa fa-dashboard"></i> Shared with me</a></li>';
    } else {
        tag_a.html('<i class="fa fa-dashboard"></i> My Storage');
        tag_a.click(function (e) {
            e.stopPropagation();
            renderPage(-1, fsp.typePage);
        });
//        content = '<li><a href="javascript:void(0);"><i class="fa fa-dashboard"></i> My Storage</a></li>';
    }
    if (args !== undefined && args.length > 0) {
        for (var i = 1; i < args.length; i++) {
            if (i === args.length - 1) {
                var li_active = $("<li>").attr("class", "active").html(args[i].name);
                objMenu.append(li_active);
            } else {
                var li_child = $("<li>").html('<a href="javascript:void(0);">' + args[i].name + '</a>');
                objMenu.append(li_child);
                li_child.click({objData: args[i]}, function (e) {
                    e.stopPropagation();
                    renderPage(e.data.objData.id, fsp.typePage);
                });

            }
        }
    }

}

function renderMenuVertical(folderId, subfolderInfos) {
    var parent;
    if (folderId <= 0 || folderId === fsp.currentRootOwnerId) {
        if (fsp.typePage === "owner") {
            parent = $("#mystorage");
        } else {
            parent = $("#shared_with_me");
        }
        $(".sidebar-menu").children().removeClass("active");
    } else {
        parent = $("#menu_left_" + folderId);
        var parent_ul = parent.closest("ul");
        parent_ul.children().removeClass("active");
    }
    parent.addClass("active");
    if (fsp.typePage !== "owner") {
        return false;
    }
    var objMenu = parent.find("ul:first");
    objMenu.show();
    objMenu.html("");
    for (var i = 0; i < subfolderInfos.length; i++) {
        var itemFolder = subfolderInfos[i];
        var datafolder = {};
        datafolder.id = "folder_" + itemFolder.folderId;
        datafolder.name = itemFolder.folderName;
        datafolder.type = 1;
        datafolder.path = itemFolder.path;
        var li_folder = $("<li>").attr("id", "menu_left_" + itemFolder.folderId).html('<a href="javascript:void(0);"><i class="fa fa-circle-o"></i> ' + itemFolder.folderName + '</a><ul class="treeview-menu"></ul>');
        objMenu.append(li_folder);
        li_folder.data("row_data", datafolder);
        li_folder.click({obj: itemFolder}, function (e) {
            e.stopPropagation();
            var data = e.data.obj;
            renderPage(data.folderId, fsp.typePage);
            return false;
        });

        li_folder.contextmenu(function (event) {
            event.stopPropagation();
//                var parent = $(this);
//                var rowData = parent.data("row_data");
//                parent.find("td").click();
            $("#contextMenu").css({
                top: event.pageY - 145 + "px",
                left: event.pageX - 245 + "px"
            });
            showContextMenu(this);
            return false;
        });
    }
    return false;
}

function deleteFile(args) {
    var url_delete = location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/deletefile";
    $(".loading").show();
    var fId = args.rowData.fileId;
    $.post(url_delete,
            {
                "action": "deleteFile",
                "fileid": fId
            }, {
        "dataType": "json"
    }).done(function (data) {
        $(".loading").hide();
        if (data === null || data === undefined) {
            BootstrapDialog.show({
                title: "Information",
                message: "Error delete file: No response",
                buttons: [{
                        label: "Close",
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }
                    }]
            });
        } else if (data.error_code === 0) {
            $("#body_home").data('gonrin').deleteRow(args.el);
            var popup = BootstrapDialog.show({
                title: "Information",
                message: "Delete file success!",
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
                title: "Information",
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
        return false;
    });
}
function getFileInfo(fileId) {
    var url_getfile = location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/file/get";
    var intervalID = window.setInterval(function (rowData) {
        $.getJSON(url_getfile + "?fileid=" + fileId, function (dataFile) {
            if (dataFile !== null && dataFile.error_code === 0) {
                var percent = (dataFile.data.numberChunkSuccess / dataFile.data.numberOfChunks) * 100;
                if (percent === 100 || dataFile.data.fileStatus === 2) {
                    $("#filestatus_" + fileId).removeClass('progress progress-sm active border-solid');
                    $("#filestatus_" + fileId).html(sizeOf(dataFile.data.fileSize));
                    clearInterval(intervalID);
                } else if (dataFile.data.status_upload === "false") {
                    $("#filestatus_" + fileId).removeClass('progress progress-sm active border-solid');
                    $("#filestatus_" + fileId).html('<label class="label label-FS_UPLOAD_FAIL">UPLOAD FAILED</label>');
                    var tag_a = $('<a>').attr("href", "javascript:void(0);").html('<label style="cursor:pointer" class="label label-FS_EMPTY">RETRY</label>');
                    $("#filestatus_" + fileId).append(tag_a);
                    tag_a.click({objData: dataFile.data}, function (e) {
                        e.stopPropagation();
                        var url_retry = location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/file/retry";
                        $.getJSON(url_retry + "?fileid=" + e.data.objData.fileId, function (respone) {
                            if (respone !== null && respone.error_code === 0) {
                                var id_tag = "file_" + e.data.objData.fileId;
                                var percentUpload = (e.data.objData.numberChunkSuccess / e.data.objData.numberOfChunks) * 100;
                                $("#filestatus_" + id_tag).addClass('progress progress-sm active border-solid');
                                $("#filestatus_" + id_tag).html('    <div id="processing_' + e.data.objData.fileId + '" class="progress-bar progress-bar-success progress-bar-striped" role="progressbar" style="width: ' + percentUpload + '%"></div>');
                                getFileInfo(id_tag);
                            } else {
                                var popup = BootstrapDialog.alert('Can not upload your file, please reupload your file!');
                                setTimeout(function () {
                                    popup.close();
                                }, 2000);
                            }
                        });
                    });
//                    $("#processing_" + fileId).removeClass('progress-bar progress-bar-success progress-bar-striped');
//                    $("#processing_" + fileId).addClass('progress-bar progress-bar-danger progress-bar-striped');
                    clearInterval(intervalID);
                } else {
                    var width = 'width:' + percent + '%';
                    $("#processing_" + fileId).attr('style', width);
                }
            } else {
                clearInterval(intervalID);
            }
        }).fail(function () {
            clearInterval(intervalID);
            $("#processing_" + fileId).removeClass('progress-bar progress-bar-success progress-bar-striped');
            $("#processing_" + fileId).addClass('progress-bar progress-bar-danger progress-bar-striped');
        });

    }, 1000, fileId);
}
function searchFile() {
    var value = $("#textSearch").val();
    $('#body_home').data('gonrin').filter({
        fileName: {$likeI: value}
    });
    var checkContent = $("#body_home .grid-data").html();
    if (checkContent !== undefined && checkContent.trim().length < 10) {
        $("#body_home").html("<p style=\"margin:20px;\">Not found files</p>");
    }
}

function showDownloadForm(itemName, itemId, pathDest, itemType) {
    var message_notify = "";
    var path_submit;
    if (itemType === 0) {
        path_submit = "/download/file";
        message_notify = "System is processing your file download, please waitting!";
    } else {
        path_submit = "/download/folder";
        message_notify = "System is processing folder and zipping for your download, please waitting!";
    }
    BootstrapDialog.show({
        title: "Preparing Download",
        draggable: true,
        cssClass: 'login-dialog',
        message: '<div class="form-group">' +
                '  <label>' + itemName + '</label>' +
                '  <label>' + message_notify + '</label>' +
//                '   <div class="progress">' +
//                '       <div id="prepare_download_' + itemId + '" class="progress-bar progress-bar-success progress-bar-striped" role="progressbar" aria-valuenow="1" aria-valuemin="0" aria-valuemax="100" style="width: 1%">' +
//                '       </div>' +
//                '   </div>' +
                '</div>',
        onshown: function (dialogItself) {
            var url_download = fsp.url + path_submit + "?itemid=" + itemId + "&itemname=" + itemName + "&path=" + pathDest;
            $.getJSON(url_download, function (resp) {
                if (resp !== null && resp.error_code === 0) {
                    dialogItself.close();
                    location.href = fsp.url + path_submit + "/" + itemId + "?itemname=" + itemName;
                } else {
                    dialogItself.close();
                    var popup = BootstrapDialog.show({
                        cssClass: "resizeBootstrapDialog",
                        title: "Error",
                        message: resp.error_code + ":" + resp.error_message,
                        buttons: [{
                                label: "Close", cssClass: 'btn-primary',
                                action: function (dialogItself) {
                                    dialogItself.close();
                                }
                            }]
                    });
                    return false;
                }
            });

        },
        buttons: [{
                label: "Cancel",
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}

function sendRequestCheckDownload(itemId, itemType, pathDest, dialogItself) {
    var url_getfile = fsp.url + "/file/agent/get";
    $.getJSON(url_getfile + "?itemid=" + itemId + "&type=" + itemType, function (dataFile) {
        console.log(dataFile);
        if (dataFile !== null && dataFile.error_code === 0) {
            if (dataFile.data.numberChunkSucess > 0 && dataFile.data.numberChunkSucess === dataFile.data.numberOfChunks) {
                dialogItself.close();
                if (pathDest !== "") {
                    var popup = BootstrapDialog.show({
                        cssClass: "resizeBootstrapDialog",
                        title: "Information",
                        message: "Download successfully, please check your file in path:" + pathDest,
                        buttons: [{
                                label: "Close", cssClass: 'btn-primary',
                                action: function (dialogItself) {
                                    dialogItself.close();
                                }
                            }]
                    });
                    setTimeout(function () {
                        popup.close();
                    }, 3000);
                } else {
                    console.log("send request download file");
                    location.href = fsp.url + "/download/agent/" + dataFile.data.sha + "?itemid=" + itemId + "&itemname=" + dataFile.data.itemName + "&type=" + itemType;
                    return false;
                }
                return false;
            } else {
                sendRequestCheckDownload(itemId, itemType, pathDest, dialogItself);
            }
        } else {
            dialogItself.close();
            var popup = BootstrapDialog.show({
                cssClass: "resizeBootstrapDialog",
                title: "Error",
                message: "Download stopped, please try again later!",
                buttons: [{
                        label: "Close", cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }
                    }]
            });
            setTimeout(function () {
                popup.close();
            }, 3000);
        }
    }).fail(function () {
        dialogItself.close();
        var popup = BootstrapDialog.show({
            cssClass: "resizeBootstrapDialog",
            title: "Error",
            message: "Download stopped, please try again later!",
            buttons: [{
                    label: "Close", cssClass: 'btn-primary',
                    action: function (dialogItself) {
                        dialogItself.close();
                    }
                }]
        });
        setTimeout(function () {
            popup.close();
        }, 3000);
    });
}

function showCreateFolder() {
    BootstrapDialog.show({
        cssClass: "resizeBootstrapDialog",
        title: "New Folder",
        message: '<div class="form-group">' +
                '    <input type="text" id="name_folder" class="form-control">' +
                '</div>',
        onshown: function () {
            $("#name_folder").attr("value", "Untitled folder");
            $("#name_folder").focus();
            $("#name_folder").select();
        },
        buttons: [{
                label: "Cancel",
                cssClass: 'btn-default',
                action: function (dialogItself) {
                    dialogItself.close();
                }
            },
            {
                label: "Create",
                cssClass: 'btn-primary',
                hotkey: 13, // Enter.
                action: function (dialogItself) {
                    $(".loading").show();
                    var folderName = $("#name_folder").val();					
					if(!checkSpecialCharacters(folderName))
                    {
                        $(".loading").hide();
                        BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: "Error",
                                message: "The folder name must cannot contain any special character",
                                buttons: [{
                                        label: "Close", cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }
                                    }]
                            });
                            return false;
                    }else{
                    $.post(fsp.url + "/folder/create",
                            {
                                "parentId": fsp.currentFolderId,
                                "folderName": folderName
                            }, {
                        "dataType": "json"
                    }).done(function (data) {
                        $(".loading").hide();
                        dialogItself.close();
                        if (data === null || data.error_code !== 0) {
                            BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: "Error",
                                message: data.error_code + ":" + data.error_message,
                                buttons: [{
                                        label: "Close", cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }
                                    }]
                            });
                            return false;
                        } else {
                            renderPage(fsp.currentFolderId, "owner");
                        }
                    });

                }}
            }]
    });
}

function checkSpecialCharacters(str){
	if(str.trim().length===0)
		return false;	
	var iChars = "~`!#$%^&*+=[]\\\\.\';,/{}|\":<>?";//
	for(var i = 0; i < str.length; i++)
	{
		if(iChars.indexOf(str.charAt(i))!== -1){
			return false;
		}
	}
	return true;
}

function showUploadForm() {
    BootstrapDialog.show({
        title: "File Upload",
        message: '<form id="form_upload_browser" method="POST" action="/upload/browser" enctype="multipart/form-data" role="form" style="padding:20px 100px;" autocomplete="off">' +
                '                       <label id="result" class="text-danger"></label>' +
                '                   <fieldset>' +
                '                       <div class="form-group">' +
                '                           <label>Choose File:</label>' +
                '                           <input style="max-width:450px;" type="file" id="file" name="file">' +
                '                           <input type="hidden" id="parentId_browser" name="parentId_browser">' +
                '                           <p class="help-block">Choose file for upload as HTTP protocol.</p>' +
                '                       </div>' +
                '                       <hr>' +
                '                       <button id="upload_browser_submit" name="upload" id="upload" class="btn btn-primary" type="submit" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Upload</button>' +
                '                   </fieldset>' +
                '               </form>',
        onshown: function (dialogItself) {
            $('#form_upload_browser #parentId_browser').val(fsp.currentFolderId);
//            $('#upload_browser_submit').on('click', function () {
//                var $this = $(this);
//                $this.button('loading');
//            });
            $("#upload_browser_submit").click(function (event) {
                function failValidation(msg) {
                    var alert_show = BootstrapDialog.alert(msg);
                    setTimeout(function () {
                        alert_show.close();
                        $('#upload_browser_submit').button("reset");
                    }, 3000);
                    return false;
                }

                var file = $('#file')[0].files[0];
                if (!file.val()) {
                    return failValidation('No choose file');
                }
                //stop submit the form, we will post it manually.
                event.preventDefault();

                // Get form
                var form = $('#form_upload_browser')[0];

                // Create an FormData object
                var data = new FormData(form);

                // If you want to add an extra field for the FormData
                data.append("parentId_browser", fsp.currentFolderId);

                // disabled the submit button
                var $this = $(this);
                $this.button('loading');

                $.ajax({
                    type: "POST",
                    enctype: 'multipart/form-data',
                    url: "/upload/browser",
                    data: data,
                    processData: false,
                    contentType: false,
                    cache: false,
//                    timeout: 600000,
                    success: function (data) {
                        console.log("upload result : ", data);
                        if (data.error_code === 0) {
                            dialogItself.close();
                            renderPage(fsp.currentFolderId, "owner");
                        } else {
                            $("#result").text(data.error_message);
                            $this.button('reset');
                        }

                    },
                    error: function (e) {
                        console.log("upload ERROR : ", e);
                        $("#result").text(e.responseText);
                        $this.button('reset');
                    }
                });

            });
        }

    });
}
function showShareForm(objData) {
    BootstrapDialog.show({
        closable: false,
        title: "Share with others",
        message: '<div class="row text-right">' +
                '<button id="get_link_public" class="btn btn-default" style="margin-right:10px" title="Get the link and turn sharing on">Get sharable link</button>' +
                '</div>' +
                '<form id="form_share_link" role="form" style="padding:0px 50px; display:none;" autocomplete="off">' +
                '<fieldset>' +
                '<div class="form-group">' +
                '<label>Link sharing <span id ="status_link" >ON</span></label>' +
                '<select id="permission_link" class="form-control select2 select2-hidden-accessible" style="width: 100%;" tabindex="-1" aria-hidden="true">' +
                '<option value="10001" >Anyone with the link <b>can view</b></option>' +
                '<option value="10003">OFF - Only specific people can access</option>' +
                '<option value="10002">Anyone with the link <b>can Edit</b></option>' +
                '</select>' +
                '</div>' +
                '<div class="form-group">' +
                '<input type="text" readonly="true" class="form-control" id="link_public" value="http://link_public.com.vn/jksdfhjklsdfhsdjklfhsdkljfhsdkljh">' +
                '</div>' +
                '</fieldset>' +
                '<hr>' +
                '</form>' +
                '<form id="share_peoples" role="form" style="padding:0px 50px;" autocomplete="off">' +
                '<fieldset>' +
                '<div class="row">' +
                '   <div id="share-advanced" class="col-xs-12">' +
                '       <label>Who has access:</label>' +
                '       <div class="permission-list table-responsive">' +
                '           <table class="table">' +
                '               <tbody class="body-list-permission">' +
                '               </tbody>' +
                '           </table>' +
                '       </div>' +
                '   </div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-md-9" style="padding-right: 0px;">' +
                '<label>Share with peoples:</label>' +
//                '<input type="text" id="listuser_autocomplete" class="form-control">' +
                '<div class="list-share">' +
                '   <span class="list-user-share">' +
                '   </span>' +
                '   <textarea placeholder="Enter email address... " id="listuser_autocomplete"></textarea>' +
                '</div>' +
//                '<input type="text" id="" class="form-control">' +
                '<p class="help-block hidden">Input path to upload direct local file.</p>' +
                '</div>' +
                '<div class="col-md-3" style="margin: 26px 0px;">' +
                '<button id="btn_change_access" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Can Edit ' +
                '<span class="fa fa-caret-down"></span>' +
                '</button>' +
                '<ul class="dropdown-menu pull-right">' +
                '<li><a id="btn_change_edit" href="javascript:void(0);">Can organize, add, & edit</a></li>' +
                '<li><a id="btn_change_view" href="javascript:void(0);">Can view only</a></li>' +
                '</ul>' +
                '</div>' +
                '</div>' +
                '<button id="btn_share_done" style="margin-right: 10px;" data-dismiss="modal" class="btn btn-primary" type="button" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Done</button>' +
                '<button id="btn_share_send" style="display:none" data-dismiss="modal" class="btn btn-primary" type="button" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Send</button>' +
                '<button id="btn_share_cancel" style="margin-left: 10px;display:none" data-dismiss="modal" class="btn btn-primary" type="button" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Cancel</button>' +
                '<a href="javascript:void(0);" id="btn_share_advanced" style="float:right;" >Advanced</a>' +
                '</fieldset>' +
                '</form>',
        onshown: function () {
            resizeListShare();
            $("#btn_change_access").data("permisson", 1);
            $("#btn_change_edit").click(function (e) {
                $("#btn_change_access").html($(this).text() + '<span class="fa fa-caret-down"></span>');
                $("#btn_change_access").data("permisson", 1);
            });
            $("#btn_change_view").click(function (e) {
                $("#btn_change_access").html($(this).text() + '<span class="fa fa-caret-down"></span>');
                $("#btn_change_access").data("permisson", 2);
            });
            $("#listuser_autocomplete").focus();
            var itemId = objData.id.split('_')[1];
            var urlGetFolder = fsp.url + "/share/public/get?itemId=" + itemId + "&type=" + objData.type;
            getLinkSharePublic(urlGetFolder);
            $('#get_link_public').on('click', function () {
                var link_form = $("#form_share_link");
                if (link_form.is(':hidden')) {
                    var urlGetFolder = fsp.url + "/share/public/change?itemId=" + itemId + "&type=" + objData.type + "&permission=10001";
                    getLinkSharePublic(urlGetFolder);
                    link_form.show();
                }


            });
            $('body').on('keydown', '#listuser_autocomplete', function (e) {
                if (e.which === 13 || e.which === 9 || e.which === 32) {
                    e.preventDefault();
                    autoComplete();
                    return false;
                }
            });
            $('body').on('keydown', '#listuser_autocomplete', function (e) {
                if ($(this).val() !== "" || $(this).val() !== null || $(this).val() !== undefined) {
                    $("#btn_share_done").hide();
                    $("#btn_share_cancel").show();
                    $("#btn_share_send").show();
                }
                if (e.which === 8) {
                    if ($(this).val().length <= 1 && $(".list-user-share").width() < 10) {
                        $("#btn_share_done").show();
                        $("#btn_share_cancel").hide();
                        $("#btn_share_send").hide();
                    }
                }

            });
            $("#permission_link").on('change', function () {
                var permission = this.value;
                var urlGetFolder = fsp.url + "/share/public/change?itemId=" + itemId + "&type=" + objData.type + "&permission=" + permission;
                getLinkSharePublic(urlGetFolder);
            });
            $("#btn_share_send").click(function (e) {
                var email = $("#listuser_autocomplete").val();
                if (email !== null && email !== undefined && email !== '') {
                    autoComplete();
                }
                var listEmail = '';
                $('.list-user-share').children('.user-share').children('.share-user-content').each(function () {
                    listEmail += $(this).text();
                    if (!$(this).parent().is(":last-child")) {
                        listEmail += ',';
                    }
                });
                if (listEmail === '' || listEmail === null || listEmail === undefined) {
                    alert('enter email share');
                    return false;
                } else {
                    var link_public = $("#link_public").val();
                    var permisson = $("#btn_change_access").data("permisson");
                    var urlShare = fsp.url + "/file/share?fileId=" + objData.id.split("_")[1] + "&permissions=" + permisson + "&emails=" + listEmail;
                    if (objData.type > 0) {
                        urlShare = fsp.url + "/folder/share?folderId=" + objData.id.split("_")[1] + "&permissions=" + permisson + "&emails=" + listEmail;
                    }
                    urlShare += "&link=" + link_public + "&viewerId=" + fsp.viewerId + "&itemName=" + objData.name;
                    $(".loading").show();
                    $.getJSON(urlShare, function (data) {
                        $(".loading").hide();
                        if (data !== null && data.error_code === 0) {
                            BootstrapDialog.show({
                                title: "Information",
                                message: "Shared successfully",
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        } else if (data.error_code === 1002) {
                            location.href = "/login";
                        } else {
                            BootstrapDialog.show({
                                title: "Information",
                                message: "get folder faile",
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        }
                    });
                }
            });
            $("#btn_share_advanced").click(function (e) {
                $(".body-list-permission").html('');
                var urlShare = fsp.url + "/item/user/get?itemId=" + objData.id.split("_")[1] + "&type=" + objData.type;
                $(".loading").show();
                $.getJSON(urlShare, function (data) {
                    $(".loading").hide();
                    $("#btn_share_advanced").hide();
                    if (data !== null && data.error_code === 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            var item = data.data[i];
                            var trUser = $("<tr>").attr("class", "tr_item");
                            var itemId;
                            if (item.fileId !== undefined && item.fileId > 0) {
                                itemId = item.fileId;
                            } else {
                                itemId = item.folderId;
                            }
                            trUser.data("itemId", itemId);
                            var tdUserName = $("<td>").css("padding-left", "0px").css("vertical-align", "middle").css("border-top", "1px solid #e7e7e7;").html(item.accountName);

                            var tdAction = $("<td>").attr("class", "text-right").css("position", "relative").css("border-top", "1px solid #e7e7e7;");
                            var btnAction = $("<button>").attr("class", "btn btn-default dropdown-toggle").attr("data-toggle", "dropdown").attr("aria-expanded", "false");
                            btnAction.html('Can edit <span class="fa fa-caret-down"></span>');
                            if (item.permission === 2 || item.permission === '2') {
                                btnAction.html('Can view <span class="fa fa-caret-down"></span>');
                            }
                            var ulAction = $("<ul>").attr("class", "dropdown-menu pull-right");
                            var liActionEdit = $("<li>").html('<a>Can edit</a>').css("cursor", "pointer");
                            var liActionView = $("<li>").html('<a>Can view</a>').css("cursor", "pointer");

                            var tdIconRemove = $("<td>").attr("class", "text-right remove-share-advanced").css("vertical-align", "middle").css("border-top", "1px solid #e7e7e7;");
                            var sIconRemove = $("<span>").attr("class", "fa fa-times").css("cursor", "pointer");
                            trUser.append(tdUserName);
                            trUser.append(tdAction);
                            if (item.isOwner) {
                                tdAction.html('isOwner');
                            } else {
                                tdIconRemove.append(sIconRemove);
                                tdAction.append(btnAction);
                            }
                            tdAction.append(ulAction);
                            ulAction.append(liActionEdit);
                            ulAction.append(liActionView);
                            trUser.append(tdIconRemove);
//                            tdIconRemove.append(sIconRemove);
                            $(".body-list-permission").append(trUser);
                            console.log(item);
                            liActionEdit.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                updatePermisson(objData.id.split("_")[1], objData.type, itemCurrent.accountName, itemCurrent.appKey, 1, this);
                            });
                            liActionView.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                updatePermisson(objData.id.split("_")[1], objData.type, itemCurrent.accountName, itemCurrent.appKey, 2, this);
                            });
                            sIconRemove.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                var urlShare = fsp.url + "/item/user/permission/remove?itemId=" + objData.id.split("_")[1] + "&type=" + objData.type + "&accountShared=" + itemCurrent.accountName + "&appKeyShared=" + itemCurrent.appKey;
                                $(".loading").show();
                                $.getJSON(urlShare, function (data) {
                                    $(".loading").hide();
                                    if (data !== null && data.error_code === 0) {
                                        $(this).parent().parent().hide();
                                        BootstrapDialog.show({
                                            title: "Information",
                                            message: "remove permisson success",
                                            buttons: [{
                                                    label: "Close",
                                                    cssClass: 'btn-primary',
                                                    action: function (dialogItselfRemovePermisson) {
                                                        dialogItselfRemovePermisson.close();
                                                    }}]
                                        });
                                        return false;
                                    } else if (data.error_code === 1002) {
                                        location.href = "/login";
                                    } else {
                                        BootstrapDialog.show({
                                            title: "Information",
                                            message: "remove permisson false \n" + data.error_message,
                                            buttons: [{
                                                    label: "close",
                                                    cssClass: 'btn-primary',
                                                    action: function (dialogItselfRemovePermisson) {
                                                        dialogItselfRemovePermisson.close();
                                                    }}]
                                        });
                                        return false;
                                    }
                                });

                            });
                        }
                        $("#share-advanced").show();
                        return false;
                    } else if (data.error_code === 1002) {
                        location.href = "/login";
                    } else {
                        BootstrapDialog.show({
                            title: "Information",
                            message: "get user accept faile",
                            buttons: [{
                                    label: "close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }}]
                        });
                        return false;
                    }
                });
            });
        }

    });
}

function share_otherApp(objData){
    BootstrapDialog.show({
        closable: false,
        title: "Share with others",
        message: '<form id="share_peoples" role="form" style="padding:0px 50px;" autocomplete="off">' +
                '<fieldset>' +
                '<div class="row">' +
                '   <div id="share-advanced" class="col-xs-12">' +
                '       <label>Who has access:</label>' +
                '       <div class="permission-list table-responsive">' +
                '           <table class="table">' +
                '               <tbody class="body-list-permission">' +
                '               </tbody>' +
                '           </table>' +
                '       </div>' +
                '   </div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-md-9" style="padding-right: 0px;">' +
                '<div class="form-group">' +
                '   <input type="text" id="accountNameShared" placeholder="Enter accountName">' +
                '   <input type="text" id="appKeyShared" placeholder="Enter AppKey">' +
                '   <button id="btn_add_account" class="btn btn-primary" type="button" >Add</button>' +
                '</div>' +
                '<label>Share with peoples:</label>' +
                '<div class="list-share">' +
                '   <span class="list-user-share">' +
                '   </span>' +
                '</div>' +
                '</div>' +
                '<div class="col-md-3" style="margin: 26px 0px;">' +
                '<button id="btn_change_access" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Can Edit ' +
                '<span class="fa fa-caret-down"></span>' +
                '</button>' +
                '<ul class="dropdown-menu pull-right">' +
                '<li><a id="btn_change_edit" href="javascript:void(0);">Can organize, add, & edit</a></li>' +
                '<li><a id="btn_change_view" href="javascript:void(0);">Can view only</a></li>' +
                '</ul>' +
                '</div>' +
                '</div>' +
                '<button id="btn_share_done" style="margin-right: 10px;" data-dismiss="modal" class="btn btn-primary" type="button" >Done</button>' +
                '<button id="btn_share_send" style="display:none" data-dismiss="modal" class="btn btn-primary" type="button" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Submit</button>' +
                '<button id="btn_share_cancel" style="margin-left: 10px;display:none" data-dismiss="modal" class="btn btn-primary" type="button" >Cancel</button>' +
                '<a href="javascript:void(0);" id="btn_share_advanced" style="float:right;" >Advanced</a>' +
                '</fieldset>' +
                '</form>',
        onshown: function () {
            resizeListShare();
            $("#btn_change_access").data("permisson", 1);
            $("#btn_change_edit").click(function (e) {
                $("#btn_change_access").html($(this).text() + '<span class="fa fa-caret-down"></span>');
                $("#btn_change_access").data("permisson", 1);
            });
            $("#btn_change_view").click(function (e) {
                $("#btn_change_access").html($(this).text() + '<span class="fa fa-caret-down"></span>');
                $("#btn_change_access").data("permisson", 2);
            });
            $("#btn_share_send").click(function (e) {
                var listEmail = '';
                $('.list-user-share').children('.user-share').children('.share-user-content').each(function () {
                    listEmail += $(this).text();
                    if (!$(this).parent().is(":last-child")) {
                        listEmail += ',';
                    }
                });
                if (listEmail === '' || listEmail === null || listEmail === undefined) {
                    alert('No choosen account');
                    return false;
                } else {
                    var link_public = $("#link_public").val();
                    var permisson = $("#btn_change_access").data("permisson");
                    var urlShare = fsp.url + "/file/share?fileId=" + objData.id.split("_")[1] + "&permissions=" + permisson + "&emails=" + listEmail;
                    if (objData.type > 0) {
                        urlShare = fsp.url + "/folder/share?folderId=" + objData.id.split("_")[1] + "&permissions=" + permisson + "&emails=" + listEmail;
                    }
                    urlShare += "&link=" + link_public + "&viewerId=" + fsp.viewerId + "&itemName=" + objData.name;
                    $(".loading").show();
                    $.getJSON(urlShare, function (data) {
                        $(".loading").hide();
                        if (data !== null && data.error_code === 0) {
                            BootstrapDialog.show({
                                title: "Information",
                                message: "Shared successfully",
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        } else if (data.error_code === 1002) {
                            location.href = "/login";
                        } else {
                            BootstrapDialog.show({
                                title: "Information",
                                message: "get folder faile",
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        }
                    });
                }
            });
            $("#btn_share_advanced").click(function (e) {
                $(".body-list-permission").html('');
                var urlShare = fsp.url + "/item/user/get?itemId=" + objData.id.split("_")[1] + "&type=" + objData.type;
                $(".loading").show();
                $.getJSON(urlShare, function (data) {
                    $(".loading").hide();
                    $("#btn_share_advanced").hide();
                    if (data !== null && data.error_code === 0) {
                        for (var i = 0; i < data.data.length; i++) {
                            var item = data.data[i];
                            var trUser = $("<tr>").attr("class", "tr_item");
                            var itemId;
                            if (item.fileId !== undefined && item.fileId > 0) {
                                itemId = item.fileId;
                            } else {
                                itemId = item.folderId;
                            }
                            trUser.data("itemId", itemId);
                            var tdUserName = $("<td>").css("padding-left", "0px").css("vertical-align", "middle").css("border-top", "1px solid #e7e7e7;").html(item.accountName);

                            var tdAction = $("<td>").attr("class", "text-right").css("position", "relative").css("border-top", "1px solid #e7e7e7;");
                            var btnAction = $("<button>").attr("class", "btn btn-default dropdown-toggle").attr("data-toggle", "dropdown").attr("aria-expanded", "false");
                            btnAction.html('Can edit <span class="fa fa-caret-down"></span>');
                            if (item.permission === 2 || item.permission === '2') {
                                btnAction.html('Can view <span class="fa fa-caret-down"></span>');
                            }
                            var ulAction = $("<ul>").attr("class", "dropdown-menu pull-right");
                            var liActionEdit = $("<li>").html('<a>Can edit</a>').css("cursor", "pointer");
                            var liActionView = $("<li>").html('<a>Can view</a>').css("cursor", "pointer");

                            var tdIconRemove = $("<td>").attr("class", "text-right remove-share-advanced").css("vertical-align", "middle").css("border-top", "1px solid #e7e7e7;");
                            var sIconRemove = $("<span>").attr("class", "fa fa-times").css("cursor", "pointer");
                            trUser.append(tdUserName);
                            trUser.append(tdAction);
                            if (item.isOwner) {
                                tdAction.html('isOwner');
                            } else {
                                tdIconRemove.append(sIconRemove);
                                tdAction.append(btnAction);
                            }
                            tdAction.append(ulAction);
                            ulAction.append(liActionEdit);
                            ulAction.append(liActionView);
                            trUser.append(tdIconRemove);
//                            tdIconRemove.append(sIconRemove);
                            $(".body-list-permission").append(trUser);
                            console.log(item);
                            liActionEdit.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                updatePermisson(objData.id.split("_")[1], objData.type, itemCurrent.accountName, itemCurrent.appKey, 1, this);
                            });
                            liActionView.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                updatePermisson(objData.id.split("_")[1], objData.type, itemCurrent.accountName, itemCurrent.appKey, 2, this);
                            });
                            sIconRemove.click({obj: item}, function (e) {
                                var itemCurrent = e.data.obj;
                                var urlShare = fsp.url + "/item/user/permission/remove?itemId=" + objData.id.split("_")[1] + "&type=" + objData.type + "&accountShared=" + itemCurrent.accountName + "&appKeyShared=" + itemCurrent.appKey;
                                $(".loading").show();
                                $.getJSON(urlShare, function (data) {
                                    $(".loading").hide();
                                    if (data !== null && data.error_code === 0) {
                                        $(this).parent().parent().hide();
                                        BootstrapDialog.show({
                                            title: "Information",
                                            message: "remove permisson success",
                                            buttons: [{
                                                    label: "close",
                                                    cssClass: 'btn-primary',
                                                    action: function (dialogItselfRemovePermisson) {
                                                        dialogItselfRemovePermisson.close();
                                                    }}]
                                        });
                                        return false;
                                    } else if (data.error_code === 1002) {
                                        location.href = "/login";
                                    } else {
                                        BootstrapDialog.show({
                                            title: "Information",
                                            message: "remove permisson false \n" + data.error_message,
                                            buttons: [{
                                                    label: "close",
                                                    cssClass: 'btn-primary',
                                                    action: function (dialogItselfRemovePermisson) {
                                                        dialogItselfRemovePermisson.close();
                                                    }}]
                                        });
                                        return false;
                                    }
                                });

                            });
                        }
                        $("#share-advanced").show();
                        return false;
                    } else if (data.error_code === 1002) {
                        location.href = "/login";
                    } else {
                        BootstrapDialog.show({
                            title: "Information",
                            message: "get user accept faile",
                            buttons: [{
                                    label: "close",
                                    cssClass: 'btn-primary',
                                    action: function (dialogItself) {
                                        dialogItself.close();
                                    }}]
                        });
                        return false;
                    }
                });
            });
        }

    });
}

function getLinkSharePublic(urlRequest) {
    $.getJSON(urlRequest, function (data) {
        if (data !== null && data.error_code === 0) {
            var key = data.data.key;
            var permission = data.data.permission;
//          10001 - publicview, 10002 - public edit, 10003 - only share people
            if (permission === 10003) {
                $("#status_link").html("OFF");
            } else {
                $("#status_link").html("ON");
                $("#form_share_link").show();
            }
            $("#permission_link").val(permission);
            $('#permission_link option[value="' + permission + '"').attr("selected", "selected");
            $("#link_public").val(fsp.url + "/share/public/get/" + key + "");
            $("#link_public").click(function () {
                $("#link_public").select();
            });

        } else {
            var popup = BootstrapDialog.alert("Error: " + data.error_message + " (" + data.error_code + ")");
            setTimeout(function () {
                popup.close();
            }, 3000);
        }
    });
}
function sizeOf(bytes) {
    if (bytes === 0) {
        return "0.00 bytes";
    }
    var e = Math.floor(Math.log(bytes) / Math.log(1000));
    return (bytes / Math.pow(1000, e)).toFixed(2) + ' ' + ' KMGTP'.charAt(e) + 'B';
}
var fsp = {
    viewerId: "",
    currentFolderId: -1,
    currentRootOwnerId: -1,
    typePage: "owner",
    url: location.protocol + "//" + window.location.hostname + ":" + window.location.port
};

$(document).ready(function () {

    $("#shared_with_me").bind("click", function () {
        fsp.typePage = "shared";
        renderPage(-1, fsp.typePage);
    });
    $("#mystorage").bind("click", function () {
        fsp.typePage = "owner";
        renderPage(-1, fsp.typePage);
    });

    $("#btnSearch").bind("click", function () {
        searchFile();
        return false;
    });
    $("#btn_file_upload").bind('click', function () {
        showUploadForm();
        return false;
    });
    $("#btn_upload_width_path").bind('click', function () {
        showUploadWithPath();
        return false;
    });
    $("#btn_createFolder").bind('click', function () {
        showCreateFolder();
        return false;
    });

    $("#change_password").click(function (e) {
        var messagess = '<form id="eventForm" class="form-horizontal fv-form fv-form-bootstrap" role="form" autocomplete="off" novalidate="novalidate">' +
                '<div class="form-group required">' +
                '                      <label class="col-lg-4 control-label">Current Password</label>' +
                '                      <div class="col-lg-7">' +
                '                        <input id="val_current_password" name="password" data-rule-password="true" required="" class="form-control" value="" data-fv-field="password" type="password">' +
                '                      <small style="display: none;" class="help-block" data-fv-validator="notEmpty" data-fv-for="password" data-fv-result="NOT_VALIDATED">password empty</small></div>' +
                '                    </div>' +
                '<div class="form-group required">' +
                '                      <label class="col-lg-4 control-label">New Password</label>' +
                '                      <div class="col-lg-7">' +
                '                        <input id="val_new_password" name="password" data-rule-password="true" required="" class="form-control" value="" data-fv-field="password" type="password">' +
                '                      <small style="display: none;" class="help-block" data-fv-validator="notEmpty" data-fv-for="password" data-fv-result="NOT_VALIDATED">password empty</small></div>' +
                '                    </div>' +
                '<div class="form-group required">' +
                '                      <label class="col-lg-4 control-label">Confirm Password</label>' +
                '                      <div class="col-lg-7">' +
                '                        <input id="val_confirm_password" name="password" data-rule-password="true" required="" class="form-control" value="" data-fv-field="password" type="password">' +
                '                      <small style="display: none;" class="help-block" data-fv-validator="notEmpty" data-fv-for="password" data-fv-result="NOT_VALIDATED">password empty</small></div>' +
                '                    </div></form>';
        BootstrapDialog.show({
            title: "Change password",
            message: messagess,
            buttons: [{
                    label: "Change",
                    cssClass: 'btn-primary',
                    action: function (dialogItself) {
                        var currentPass = $("#val_current_password").val();
                        var newPass = $("#val_new_password").val();
                        var confirmNewPass = $("#val_confirm_password").val();
                        var urlChangPass = fsp.url + "/changePassword";
                        if (currentPass === null || currentPass === '' || currentPass === undefined ||
                                newPass === null || newPass === '' || newPass === undefined ||
                                confirmNewPass === null || confirmNewPass === '' || confirmNewPass === undefined) {
                            BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: "Error",
                                message: "enter current password and new pasword",
                                buttons: [{
                                        label: "Close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself1) {
                                            dialogItself1.close();
                                        }
                                    }]
                            });
                            return false;
                        }
                        if (newPass !== confirmNewPass) {
                            BootstrapDialog.show({
                                cssClass: "resizeBootstrapDialog",
                                title: "Error",
                                message: "confirm password khong trung new password",
                                buttons: [{
                                        label: "Close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself1) {
                                            dialogItself1.close();
                                        }
                                    }]
                            });
                            return false;
                        }
                        $(".loading").show();
                        $.post(urlChangPass,
                                {
                                    "action": "changepassword",
                                    "password": currentPass,
                                    "newPassword": newPass
                                }, {
                            "dataType": "json"
                        }).done(function (data) {
                            $(".loading").hide();
                            if (data !== null && data.error_code === 0) {
                                dialogItself.close();
                                BootstrapDialog.show({
                                    title: "Information",
                                    message: "Change password success",
                                    buttons: [{
                                            label: "close",
                                            cssClass: 'btn-primary',
                                            action: function (dialogItself) {
                                                dialogItself.close();
                                            }}]
                                });
                            } else if (data.error_code === 1002) {
                                location.href = "/login";
                            } else {
                                BootstrapDialog.show({
                                    title: "Information",
                                    message: data.error_message + "[" + data.error_code + "]",
                                    buttons: [{
                                            label: "close",
                                            cssClass: 'btn-primary',
                                            action: function (dialogItself) {
                                                dialogItself.close();
                                            }}]
                                });
                                return false;
                            }
                        });
                    }
                }, {
                    label: "Close",
                    cssClass: 'btn-primary',
                    action: function (dialogItself) {
                        dialogItself.close();
                    }
                }]
        });
    });
});
//anbq start
function showContextMenu(element) {
    var row_data = $(element).data("row_data");

    var objMenu = $("#contextMenu").find("ul");
    objMenu.html("");
    var li_share = $("<li>").attr("id", "menu_context_share").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-user-plus" aria-hidden="true"></i>Share</a>');
    objMenu.append(li_share);
    li_share.click({obj: row_data}, function (e) {
        e.stopPropagation();
        $("#contextMenu").hide();
        showShareForm(row_data);
    });
//    var li_share = $("<li>").attr("id", "menu_context_share_other").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-user-plus" aria-hidden="true"></i>Share with other App</a>');
//    objMenu.append(li_share);
//    li_share.click({obj: row_data}, function (e) {
//        e.stopPropagation();
//        $("#contextMenu").hide();
//        share_otherApp(row_data);
//    });
    if (fsp.typePage === "owner") {
        if (row_data.type > 0) {
            var li_rename = $("<li>").attr("id", "menu_context_rename").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-pencil-square-o" aria-hidden="true"></i>Rename...</a>');
            objMenu.append(li_rename);
            li_rename.click({obj: row_data}, function (e) {
                e.stopPropagation();
                $("#contextMenu").hide();
                rename(row_data);
            });
        }

        var li_moveto = $("<li>").attr("id", "menu_context_moveto").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-share" aria-hidden="true"></i>Move to...</a>');
        objMenu.append(li_moveto);
        li_moveto.click({obj: row_data}, function (e) {
            e.stopPropagation();
            $("#contextMenu").hide();
            moveTo(row_data);
        });
    }


    var li_download = $("<li>").attr("id", "menu_context_download").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-download" aria-hidden="true"></i>Download</a>');
    objMenu.append(li_download);
    li_download.click({obj: row_data}, function (e) {
        e.stopPropagation();
        $("#contextMenu").hide();
        showDownloadForm(row_data.name, row_data.id.split("_")[1], "", row_data.type);
    });

    var li_remove = $("<li>").attr("id", "menu_context_remove").html('<a href="javascript:void(0);"><i style="min-width: 16px;" class="fa fa-trash" aria-hidden="true"></i>Remove</a>');
    objMenu.append(li_remove);
    li_remove.click({obj: row_data}, function (e) {
        e.stopPropagation();
        $("#contextMenu").hide();
        remove(row_data);
    });
    $("#contextMenu").show();
}
function rename(jObject) {
    var messageRenameSuccess = "Rename file success!";
    var messageDialog = '<div class="form-group"><input id="input_name" class="typeahead form-control" value="' + jObject.name + '" placeholder="Please enter new name"></div>';
    if (jObject.type > 0) {
        messageRenameSuccess = "Rename folder success!";
    }
    BootstrapDialog.show({
        title: "Rename",
        message: messageDialog,
        onshown: function () {
            $("#input_name").attr("value", jObject.name);
            $("#input_name").focus();
            $("#input_name").select();
        },
        buttons: [{
                label: "Ok",
                hotkey: 13, // Enter.
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var name = $("#input_name").val();
                    var urlRename = fsp.url + "/folder/changename";
                    $(".loading").show();
                    $.post(urlRename,
                            {
                                "action": "changeNameFolder",
                                "folderId": jObject.id.split("_")[1],
                                "folderName": name
                            }, {
                        "dataType": "json"
                    }).done(function (data) {
                        $(".loading").hide();
                        dialogItself.close();
                        if (data !== null && data.error_code === 0) {
                            jObject.name = name;
                            if (jObject.type > 0) { //is folder
                                $("#menu_left_" + jObject.id.split("_")[1] + " a").html('<i class="fa fa-circle-o"></i> ' + name);
                            }

                            $("#tbl_body_home_tr_" + jObject.id + " .column-name").text(name);
                            var popup = BootstrapDialog.show({
                                title: "Information",
                                message: messageRenameSuccess,
                                buttons: [{
                                        label: "close",
                                        hotkey: 13, // Enter.
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            setTimeout(function () {
                                popup.close();
                            }, 3000);
                        } else if (data.error_code === 1002) {
                            location.href = "/login";
                        } else {
                            BootstrapDialog.show({
                                title: "Error",
                                message: data.error_message,
                                buttons: [{
                                        label: "close",
                                        hotkey: 13, // Enter.
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        }
                    });

                }
            }, {
                label: "Cancel",
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}
function moveTo(row_data) {
    var path = row_data.path.split('/');
    var CurrentId = row_data.id.split('_')[1];
    var folderGetId = path[path.length - 1];
    if (row_data.type > 0) {
        folderGetId = path[path.length - 2];
    }
    var urlGetFolder = fsp.url + "/folder/get?folderId=" + folderGetId;
    $.getJSON(urlGetFolder, function (data) {
        if (data !== null && data.error_code === 0) {
            console.log(data);
            var messTitle = $("<div>");
            var iconBackTitle = $("<a>").attr("class", "icon-back-move fa fa-arrow-left").attr("title", "Back").attr("id", "backMoveTo").attr({"href": "javascript:void(0);"});
            var spanPath = $("<span>").attr("id", "pathMoveTo").text(data.data.folderName);
            messTitle.append(iconBackTitle);
            messTitle.append(spanPath);
            $(iconBackTitle).data("path", data.data.path);
            iconBackTitle.click(function (e) {
                e.stopPropagation();
                var path = $(this).data("path");
                var dataPath = path.split('/');
                if (dataPath.length > 1) {
                    genMoveTo(dataPath[dataPath.length - 2], CurrentId);
                } else {
                    genMoveTo(dataPath[dataPath.length - 1], CurrentId);
                }
                var newPath = "";
                for (var i = 0; i < dataPath.length - 1; i++) {
                    newPath += dataPath[i];
                    if (i < dataPath.length - 2) {
                        newPath += "/";
                    }
                }
                $(this).data("path", newPath);
            });
            if (data.data.path.split("/").length > 1) {
                iconBackTitle.show();
            } else {
                iconBackTitle.hide();
                $("#pathMoveTo").css("margin-left", "5px");
                $("#pathMoveTo").text("Move to...");
            }
            var messContent = $("<div>");
            var divMoveto = $("<div>").attr("id", "contentMoveTo");
            messContent.append(divMoveto);
            BootstrapDialog.show({
                title: messTitle,
                message: messContent,
                onshown: function () {
                    genMoveTo(folderGetId, CurrentId);
                },
                buttons: [{
                        label: "Move",
                        hotkey: 13, // Enter.
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            var newFolderId = $("#pathMoveTo").data("newFolderId");
                            var pathObj = row_data.path.split("/");
                            var urlGetFolder = fsp.url + "/file/move?fileId=" + CurrentId + "&newFolderId=" + newFolderId + "&parentId=" + pathObj[pathObj.length - 1];
                            if (row_data.type > 0) {
                                urlGetFolder = fsp.url + "/folder/move?folderId=" + CurrentId + "&newFolderId=" + newFolderId;
                            }
							var mes;
							if(urlGetFolder.match("fileId"))
								mes = "Move file success";
							else mes = "Move folder success";
                            $.getJSON(urlGetFolder, function (data) {
                                if (data !== null && data.error_code === 0) {
                                    if (row_data.type > 0) {
                                        if (newFolderId !== pathObj[pathObj.length - 2]) {
                                            $("#menu_left_" + CurrentId).empty();
                                            $("#menu_left_" + CurrentId).hide();
                                            $("#tbl_body_home_tr_folder_" + CurrentId).hide();
                                        }
                                    } else {
                                        if (newFolderId !== pathObj[pathObj.length - 1]) {
                                            $("#tbl_body_home_tr_file_" + CurrentId).hide();
                                        }
                                    }
                                    var popup = BootstrapDialog.show({
                                        title: "Information",
                                        message: mes,
                                        buttons: [{
                                                label: "close",
                                                cssClass: 'btn-primary',
                                                action: function (dialogItself) {
                                                    dialogItself.close();
                                                }}]
                                    });
                                    setTimeout(function () {
                                        popup.close();
                                    }, 3000);
                                } else if (data.error_code === 1002) {
                                    location.href = "/login";
                                } else {
                                    BootstrapDialog.show({
                                        title: "Error",
                                        message: "move folder failed! \n" + data.error_message,
                                        buttons: [{
                                                label: "close",
                                                cssClass: 'btn-primary',
                                                action: function (dialogItself) {
                                                    dialogItself.close();
                                                }}]
                                    });
                                    return false;
                                }
                            });
                            dialogItself.close();
                        }
                    }, {
                        label: "Cancel",
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }}
                ]
            });
        } else if (data.error_code === 1002) {
            location.href = "/login";
        } else {
            var popup = BootstrapDialog.show({
                title: "Information",
                message: "get folder failed",
                buttons: [{
                        label: "close",
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }}]
            });
            return false;
        }
    });
}
function genMoveTo(folderGetId, folderCurrentId) {
    $("#contentMoveTo").html("");
    $("#pathMoveTo").data("newFolderId", folderGetId);
    var urlGetFolder = fsp.url + "/folder/get?folderId=" + folderGetId;
    $.getJSON(urlGetFolder, function (data) {
        if (data !== null && data.error_code === 0) {
            var messContent = $("<table>").attr("class", "table").css("margin-bottom", "0px");            			
            var tBody = $("<tbody>");
            if (data.data.path.split("/").length > 1) {
				$("#pathMoveTo").text(data.data.folderName);
                $("#pathMoveTo").css("margin-left", "30px");
                $("#backMoveTo").show();
            } else {
                $("#backMoveTo").hide();
                $("#pathMoveTo").css("margin-left", "5px");
                $("#pathMoveTo").text("Move to...");
            }
            var arrFolder = data.data.subFolders;		
			var tempPath = data.data.pathFull;			
			var folderPath = "My Storage";
			if(tempPath.length > 0)
			{
				for(var j = 1; j < tempPath.length; j++)									
					folderPath = folderPath + "/" + tempPath[j].name;				
			}											
			var messPath = $("<span>").attr("class","path-directory-label-footer").text(folderPath);			
            if (arrFolder.length > 0) {
                if (arrFolder.length === 1 && arrFolder[0].folderId === parseInt(folderCurrentId)) {
                  //  $("#contentMoveTo").html('<h1><small>Folder empty</small></h1>');
				  $("#contentMoveTo").append(messPath);
                } else {
                    for (var i = 0; i < arrFolder.length; i++) {
                        var folder = arrFolder[i];
                        //if not folder move
                        var folderID = folder.folderId;
                        if (folderID !== parseInt(folderCurrentId)) {
                            var td = $("<td>").html(folder.folderName);
                            var tr = $("<tr>").attr("folder_data", folder).attr("class", "trMoveto").append(td);
                            td.dblclick({obj: folder}, function (e) {
                                e.stopPropagation();
                                e.preventDefault();
                                var data = e.data.obj;
                                genMoveTo(data.folderId, folderCurrentId);
                                $("#backMoveTo").data("path", data.path);
                            });
                            tr.click({obj: folder}, function (e) {
                                e.stopPropagation();
                                e.preventDefault();
                                var data = e.data.obj;
                                $(".trMoveto").removeClass("active-row");
                                $(this).addClass("active-row");
                                $("#pathMoveTo").text(data.folderName);
                                $("#pathMoveTo").data("newFolderId", data.folderId);
                            });
                            tBody.append(tr);							
                        }
                    }
                    messContent.append(tBody);										
                    $("#contentMoveTo").append(messContent);
					$("#contentMoveTo").append(messPath);
                }
            } else {
              //  $("#contentMoveTo").html('<h1><small>Folder empty</small></h1>');
			  $("#contentMoveTo").append(messPath);
            }

        } else if (data.error_code === 1002) {
            location.href = "/login";
        } else {
            BootstrapDialog.show({
                title: "Information",
                message: "get folder failed! \n" + data.error_message,
                buttons: [{
                        label: "close",
                        cssClass: 'btn-primary',
                        action: function (dialogItself) {
                            dialogItself.close();
                        }}]
            });
            return false;
        }
    });
}
function remove(jObject) {
    var preAction = "Remove";
    if (fsp.typePage === "shared") {
        preAction = "Remove permission access";
    }
    var messageDeleteQuesion = "Are you sure " + preAction.toLocaleLowerCase() + " this file?";
    var messageDeleteSuccess = preAction + " file success!";
    if (jObject.type > 0) {
        messageDeleteQuesion = "Are you sure " + preAction.toLocaleLowerCase() + " this folder?";
        messageDeleteSuccess = preAction + " folder success!";
    }
    BootstrapDialog.show({
        title: "Information",
        message: messageDeleteQuesion,
        buttons: [{
                label: "Yes",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    var parentId = jObject.path.split("/")[jObject.path.split("/").length - 1];
                    var urlDelete = fsp.url + "/file/delete?fileId=" + jObject.id.split("_")[1] + "&parentId=" + parentId;
                    if (jObject.type > 0) {
                        urlDelete = fsp.url + "/folder/delete?folderId=" + jObject.id.split("_")[1];
                    }
                    $(".loading").show();
                    $.getJSON(urlDelete, function (data) {
                        $(".loading").hide();
                        if (data !== null && data.error_code === 0) {
                            if (jObject.type > 0) { //is folder
                                $("#menu_left_" + jObject.id.split("_")[1]).empty();
                                $("#menu_left_" + jObject.id.split("_")[1]).hide();
                            }
                            $("#tbl_body_home_tr_" + jObject.id).hide();
                            var popup = BootstrapDialog.show({
                                title: "Information",
                                message: messageDeleteSuccess,
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            setTimeout(function () {
                                popup.close();
                            }, 3000);
                        } else if (data.error_code === 1002) {
                            location.href = "/login";
                        } else {
                            BootstrapDialog.show({
                                title: "Information",
                                message: data.error_message,
                                buttons: [{
                                        label: "close",
                                        cssClass: 'btn-primary',
                                        action: function (dialogItself) {
                                            dialogItself.close();
                                        }}]
                            });
                            return false;
                        }
                    });
                    dialogItself.close();
                }
            }, {
                label: "No",
                action: function (dialogItself) {
                    dialogItself.close();
                }
            }]
    });
}
function resizeListShare() {
    var widthListShare = $(".list-share").width();
    var widthUser = $(".list-user-share").children(".user-share:last-child").width();
    if (widthListShare / 2 < widthUser) {
        $("#listuser_autocomplete").width("93%");
    } else {
        $("#listuser_autocomplete").width(widthListShare - widthUser - 30 + "px");
    }
    $("#listuser_autocomplete").focus();
}
function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
function autoComplete() {
    var element = $("#listuser_autocomplete");
    var username = $.trim($(element).val());
    if (validateEmail(username)) {
        var sUsershare = $("<span>").attr("class", "user-share");
        var dShareUserContent = $("<div>").attr("class", "share-user-content").html(username);
        var dIconclose = $("<div>").attr("class", "div-icon-closed");
        var sIconclose = $("<span>").attr("class", "icon-delete-share fa fa-times");
        sUsershare.append(dShareUserContent);
        sUsershare.append(dIconclose);
        dIconclose.append(sIconclose);
        dIconclose.click(function (e) {
            $(this).parent().remove();
            if ($("#listuser_autocomplete").val().length <= 1 && $(".list-user-share").width() < 10) {
                $("#btn_share_done").show();
                $("#btn_share_cancel").hide();
                $("#btn_share_send").hide();
            }
            resizeListShare();
        });
        $(".list-user-share").append(sUsershare);
        $(element).val("");
        resizeListShare();
    } else if (username !== null && username !== undefined && username !== '') {
        BootstrapDialog.show({
            title: "Information",
            message: 'Enter email',
            buttons: [{
                    label: "close",
                    cssClass: 'btn-primary',
                    action: function (dialogItself) {
                        dialogItself.close();
                        $("#listuser_autocomplete").focus();
                    }}]
        });
    }
    return false;
}
function updatePermisson(id, type, accountShared, appKeyShared, permisson, element) {
    var urlShare = fsp.url + "/item/user/permission/update?itemId=" + id + "&type=" + type + "&accountShared=" + accountShared + "&appKeyShared=" + appKeyShared + "&permissions=" + permisson;
    $(".loading").show();
    $.getJSON(urlShare, function (data) {
        $(".loading").hide();
        if (data !== null && data.error_code === 0) {
            if (permisson === 1 || permisson === '1') {
                $(element).parent().parent().children('button').html('Can edit <span class="fa fa-caret-down"></span>');
            } else {
                $(element).parent().parent().children('button').html('Can view <span class="fa fa-caret-down"></span>');
            }
            var popup = BootstrapDialog.show({
                title: "Information",
                message: "update permisson success",
                buttons: [{
                        label: "close",
                        cssClass: 'btn-primary',
                        action: function (dialogItselfRemovePermisson) {
                            dialogItselfRemovePermisson.close();
                        }}]
            });
            setTimeout(function () {
                popup.close();
            }, 3000);
            return false;
        } else if (data.error_code === 1002) {
            location.href = "/login";
        } else {
            BootstrapDialog.show({
                title: "Information",
                message: "update permisson failed \n" + data.error_message,
                buttons: [{
                        label: "close",
                        cssClass: 'btn-primary',
                        action: function (dialogItselfRemovePermisson) {
                            dialogItselfRemovePermisson.close();
                        }}]
            });
            return false;
        }
    });
    $(this).parent().parent().hide();
}
function initFiledrag() {
    $('#form_upload_drapdrop #parentId_browser').val(fsp.currentFolderId);
    var filedrag = document.getElementById("filedrag");
    filedrag.addEventListener("dragover", FileDragHover, false);
    filedrag.addEventListener("dragleave", FileDragHover, false);
    filedrag.addEventListener("drop", FileSelectHandler, false);
//    filedrag.style.display = "block";
}
function FileDragHover(e) {
    e.stopPropagation();
    e.preventDefault();
    e.target.className = (e.type === "dragover" ? "hover col-xs-offset-4 col-xs-4" : "col-xs-offset-4 col-xs-4");
}
function FileSelectHandler(e) {
    // cancel event and hover styling
    FileDragHover(e);
    var parentId = $('#form_upload_drapdrop #parentId_browser').val();
    // fetch FileList object
    var files = e.target.files || e.dataTransfer.files;
//    $("#file_drag").val("");
    // process all File objects
    sendRequestUpload(parentId, files[0]);

}
function sendRequestUpload(parentId, file) {
    var formData = new FormData();
    formData.append('parentId_browser', parentId);
    formData.append('filename', file.name);
    formData.append('file', file);
    var dialog = BootstrapDialog.show({
        title: "Information",
        message: '<label id="notify_upload" >File is uploading</label>',
        buttons: [{
                label: "Cancel",
                cssClass: 'btn-primary',
                action: function (dialogItself) {
                    dialogItself.close();
                }}]
    });
    $.ajax({
        url: fsp.url + "/upload/browser",
        data: formData,
        type: 'POST',
        contentType: false, // NEEDED, DON'T OMIT THIS (requires jQuery 1.6+)
        processData: false, // NEEDED, DON'T OMIT THIS
        success: function (data) {
            console.log("Upload drap/drop success data="+data);
            if (data.error_code === 0) {
                $("#notify_upload").html("File upload success!");
                renderPage(fsp.currentFolderId, fsp.typePage);
                dialog.close();
            } else {
                $("#notify_upload").html("File upload failed \n Error:" + data.error_message);
            }

            return false;
        },
        error: function (e) {
            console.log("Upload drap/drop failed, e=" + e);
            BootstrapDialog.show({
                title: "Information",
                message: "Upload drap/drop failed, error=" + e.responseText,
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
}
function showUploadWithPath() {
    BootstrapDialog.show({
        title: "File Upload With Path Direct",
        message: '               <form id="form_upload_path"  method="POST" action="/upload/path" role="form" style="padding:10px 100px;" autocomplete="off">' +
                '                   <fieldset>' +
                '                       <div class="form-group">' +
                '                           <label>Direct Path File:</label>' +
                '                           <input type="text" id="path" name="path" class="form-control">' +
                '                           <p class="help-block">Input path to upload direct local file.</p>' +
                '                           <input type="hidden" id="parentId_path" name="parentId_path">' +
                '                       </div>' +
                '                       <hr>' +
                '                       <button id="upload_path_submit" name="upload_path" class="btn btn-primary" type="submit" data-loading-text="<i class=\'fa fa-spinner fa-spin \'></i> Processing Submit">Upload</button>' +
                '                   </fieldset>' +
                '               </form>',
        onshown: function (dialogItself) {

            $("#form_upload_path").click(function (event) {
                function failValidation(msg) {
                    var alert_show = BootstrapDialog.alert(msg);
                    setTimeout(function () {
                        alert_show.close();
                        $('#upload_path_submit').button("reset");
                    }, 3000);
                    return false;
                }

                var path = $('#path');
                if (!path.val() || path.trim() === "") {
                    return failValidation('Please enter your file path');
                }
                //stop submit the form, we will post it manually.
                event.preventDefault();

                // Get form
                var form = $('#form_upload_path')[0];

                // Create an FormData object
                var data = new FormData(form);

                // If you want to add an extra field for the FormData
                data.append("parentId_path", fsp.currentFolderId);

                // disabled the submit button
                var $this = $('#upload_path_submit');
                $this.button('loading');

                $.ajax({
                    type: "POST",
//                    enctype: 'multipart/form-data',
                    url: "/upload/path",
                    data: data,
                    processData: false,
                    contentType: false,
                    cache: false,
                    timeout: 600000,
                    success: function (data) {
                        console.log("upload path result : ", data);
                        if (data.error_code === 0) {
                            dialogItself.close();
                            renderPage(fsp.currentFolderId, "owner");
                        } else {
                            $("#result").text(data.error_message);
                            $this.button('reset');
                        }

                    },
                    error: function (e) {
                        console.log("upload path ERROR : ", e);
                        $("#result").text(e.responseText);
                        $this.button('reset');
                    }
                });

            });
        }

    });
}
//anbq end
