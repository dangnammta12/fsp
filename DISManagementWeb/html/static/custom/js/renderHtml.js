/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function renderFormURL() {
    return '<div class="modal-body">' +
            '<div class="messages"></div>' +
            '<div class="form-group"> <!--/here teh addclass has-error will appear -->' +
            '<label for="URLInfo" class="col-sm-2 control-label">URL Info: </label>' +
            '<div class="col-sm-10 input-group"> ' +
            '<input type="text" class="form-control" id="urlInputForm" name="urlInputForm" placeholder="http://IP_ADDRESS:PORT/DisInfo/" style="width: 550px;margin-right:20px;">' +
            '<button type="submit" class="btn btn-primary go inline">Update</button>' +
            '</br><span id="url-error-msg" class="error-msg" style="float: left;color: rgb(138, 109, 59);margin-top: 5px;"></span>'+
            '</div>' +
            '</div>' +
            '</div>';
}

function renderAddIpForm() {
    return  '<div class="modal-dialog" role="document">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"><span class="glyphicon glyphicon-plus-sign"></span>  Accepted IP</h4>' +
            '</div>' +
            '<form class="form-horizontal" id="createIPForm">' +
            '<div class="modal-body">'+
            '<div class="messages"></div>' +
            '<div class="form-group">' +
            '<label for="agentName" class="col-sm-2 control-label">IP Addresses:</label>' +
            '<div class="col-sm-10">' +
            '<textarea class="form-control" id="ip_area" rows="5"></textarea>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
            '<button type="submit" class="btn btn-primary">Add</button>' +
            '</div>' +
            '</form>' +
            '</div>' +
            '</div>';
}

function renderCreateAgentForm() {
    return '<div class="modal-dialog" role="document">'+
            '<div class="modal-content">'+
            '<div class="modal-header">'+
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'+
            '<h4 class="modal-title"><span class="glyphicon glyphicon-plus-sign"></span>  Add Member</h4>'+
            '</div>'+
            '<form class="form-horizontal" id="createAgentForm">'+
            '<div class="modal-body">'+
            '<div class="messages"></div>'+
            '<div class="form-group">'+
            ' <label for="agentName" class="col-sm-2 control-label">Agent Name</label>'+
            '<div class="col-sm-10">'+
            '<input type="text" class="form-control" id="agentName" name="agentName" placeholder="Agent Name">'+
            '</div>'+
            '</div>'+
            
            '<div class="form-group">'+
            ' <label for="agentDesc" class="col-sm-2 control-label">Agent Description</label>'+
            '<div class="col-sm-10">'+
            '<input type="text" class="form-control" id="agentDesc" name="agentDesc" placeholder="Agent Description">'+
            '</div>'+
            '</div>'+
            
            '<div class="form-group">'+
            ' <label for="agentIp" class="col-sm-2 control-label">IP Address</label>'+
            '<div class="col-sm-10">'+
            '<input type="text" class="form-control" id="agentIp" name="agentIp" placeholder="IP Address">'+
            '</div>'+
            '</div>'+            
            '</div>'+
            '<div class="modal-footer">'+
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>'+
            '<button type="submit" class="btn btn-primary">Add Agent</button>'+
            '</div>'+
            '</form>'+
            '</div>'+
            '</div>';
}

function renderAgentInfoForm() {
    return '<div class="modal-dialog" role="document">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"><span class="glyphicon glyphicon-edit"></span>  Edit Agent and Display Application Info</h4>' +
            '</div>' +
            '<form class="form-horizontal" id="updateAgentForm">' +
            '<div class="modal-body">' +
            '<div class="edit-messages"></div>' +
            '<div class="form-group">' +
            '<label for="editAgentName" class="col-sm-2 control-label">Agent Name</label>' +
            '<div class="col-sm-10">' +
            '<input type="text" class="form-control" id="editAgentName" name="editAgentName" placeholder="Agent Name">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label for="editAgentDesc" class="col-sm-2 control-label">Agent Description</label>' +
            '<div class="col-sm-10">' +
            '<input type="text" class="form-control" id="editAgentDesc" name="editAgentDesc" placeholder="Agent Description">' +
            '</div>' +
            '</div>' +
            '<br /><br /><br />' +
            '<center><h4 class="page-heading">Application Info</h4>' +
            '<div id="applicationInfos">' +
            '</div>' +
            '</center>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
            '<button type="submit" class="btn btn-primary">Update Agent</button>' +
            '</div>' +
            '</form>' +
            '</div>' +
            '</div>';
}

function renderRemoveAgentModal() {
    return '<div class="modal-dialog" role="document">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"><span class="glyphicon glyphicon-trash"></span> Delete Member</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<p>Do you really want to delete this agent ?</p>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>' +
            '<button type="button" class="btn btn-primary" id="deleteAgentBtn">Delete</button>' +
            '</div>' +
            '</div>' +
            '</div>';
}

function renderDisInfoModal() {
    return  '<div class="modal-dialog" role="document">'+
            '<div class="modal-content">'+
            '<div class="modal-header">'+
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'+
            '<h4 class="modal-title"><span class="glyphicon glyphicon-plus-info-sign"></span>  Dis Information</h4>'+
            '</div>'+
            '<form class="form-horizontal">'+
            '<div class="modal-body">'+
            
            '<div class="form-group">'+
            ' <label for="agentName" class="col-sm-2 control-label">Dis Public Key</label>'+
            '<div class="col-sm-10">'+
            '<textarea class="form-control" rows="5" id="disPublicKey" readonly></textarea>'+
            '</div>'+
            '</div>'+
            
            '<div class="form-group">'+
            ' <label for="agentDesc" class="col-sm-2 control-label">Agent Private Key</label>'+
            '<div class="col-sm-10">'+
            '<textarea class="form-control" rows="5" id="agentPrivateKey" readonly></textarea>'+
            '</div>'+
            '</div>'+
            
                     
            '</div>'+
            '<div class="modal-footer">'+
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>'+
           
            '</div>'+
            '</form>'+
            '</div>'+
            '</div>';
} 

function renderAppManagement() {
    return '<div class="col-md-12">' +
            '<center><h1 class="page-heading">Application Management</h1></center>' +
            '<div class="removeMessages"></div>' +
            '<button  class="btn btn-primary pull pull-right" data-toggle="modal" data-target="#addNewAppModal" id="addAppModalBtn">' +
            '<span class="glyphicon glyphicon-plus-sign"></span> Add New Application' +
            '</button>' +
            '<br /><br /><br />' +
            '<div id="appManagement" class="box-body">' +
            '</div>' +
            '</div>';
}

function renderAddNewApp() {
    return  '<div class="modal-dialog" role="document">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"><span class="glyphicon glyphicon-plus-sign"></span>  Add Application</h4>' +
            '</div>' +
            '<form class="form-horizontal" id="createApplicationForm">' +
            '<div class="modal-body">' +
            '<div class="messages"></div>' +
            '<div class="form-group">' +
            '<label for="appName" class="col-sm-2 control-label">Application Name</label>' +
            '<div class="col-sm-10">' +
            '<input type="text" class="form-control" id="appName" name="appName" placeholder="Application Name">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label for="appDesc" class="col-sm-2 control-label">Application Description</label>' +
            '<div class="col-sm-10">' +
            '<input type="text" class="form-control" id="appDesc" name="appDesc" placeholder="Application Description">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label for="appIp" class="col-sm-2 control-label">IP Address</label>' +
            '<div class="col-sm-4">' +
            '<input type="text" class="form-control" id="appIp" name="appIp" placeholder="IP ADDRESS">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label for="appPort" class="col-sm-2 control-label">Port</label>' +
            '<div class="col-sm-2">' +
            '<input type="text" class="form-control" id="appPort" name="appPort" placeholder="Port">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label for="agent" class="col-sm-2 control-label">Agent</label>' +
            '<div class="col-sm-10">' +
            '<select id="multi-select" multiple="multiple"></select>' +
            '</br><span id="select-error-msg" class="error-msg" style="float: left;color: rgb(138, 109, 59);margin-top: 5px;"></span>'+
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
            '<button type="submit" class="btn btn-primary">Add</button>' +
            '</div>' +
            '</form>' +
            '</div>' +              
            '</div>';
}