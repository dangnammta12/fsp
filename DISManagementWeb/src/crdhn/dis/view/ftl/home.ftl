<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Agent Management</title>
        <meta charset="utf-8">
        <link rel="stylesheet" type="text/css" href="${static_url}/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="${static_url}/bootstrap/css/bootstrap-dialog.min.css">
        <link rel="stylesheet" type="text/css" href="${static_url}/bootstrap/css/bootstrap-multiselect.css">
        <style>
            .margin-btn-grid{ margin-left: 5px;}
            
            .icon-circle:before {
                content: "\25cf";
              /*  font-size: 1.5em;*/
                #color: #ef4c4c
            }			
            /* iPads (landscape) ----------- */
            @media only screen 
            and (min-device-width : 768px) 
            and (max-device-width : 1024px) 
            and (orientation : landscape) {
                /* Styles */
                .scale-dialog .modal-dialog{ width: 700px;}
            }

            /* iPads (portrait) ----------- */
            @media only screen 
            and (min-device-width : 768px) 
            and (max-device-width : 1024px) 
            and (orientation : portrait) {
                /* Styles */
                .scale-dialog .modal-dialog{ width: 700px;}
            }

            /* Desktops and laptops ----------- */
            @media only screen 
            and (min-width : 1224px) {
                /* Styles */
                .scale-dialog .modal-dialog{ width: 900px;}
            }

            /* Large screens ----------- */
            @media only screen 
            and (min-width : 1824px) {
                /* Styles */
                .scale-dialog .modal-dialog{ width: 900px;}
            }
        </style>
    </head>
    <body>
        <!--Agent DataTable-->
        <div class="container">
            <div style="display:none;" class="loading">Loading&#8230;</div>
            <ul class="nav nav-tabs" role="tablist" id = "menuTab">
                <li class="nav-item">
                    <a class="nav-link" id="tab_agent" data-toggle="tab" href="#agent" role="tab" aria-controls="agent">Agent</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="tab_app" data-toggle="tab" href="#app" role="tab" aria-controls="app">Application</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="tab_config" data-toggle="tab" href="#config" role="tab" aria-controls="agent">Configuration</a>
                </li>
            </ul>

            <div class="tab-content">
                <div id="agent" class="tab-pane fade">
                    <div class="col-md-12">
                        <center><h1 class="page-heading">Agent</h1></center>
                        <div class="removeMessages"></div>                    
                        <button  class="btn btn-primary pull pull-right" data-toggle="modal" data-target="#addNewAgent" id="addAgentModalBtn">
                            <span class="glyphicon glyphicon-plus-sign"></span> Add New Agent
                        </button>
                        <br /><br /><br />
                        <div id="agentManagement" class="box-body">
                        </div>                    
                    </div>
                </div>                
                <div class="tab-pane fade" id="app" role="tabpanel">
                    <div class="col-md-12">
                        <center><h1 class="page-heading">Application</h1></center>
                        <button  class="btn btn-primary pull pull-right" data-toggle="modal" data-target="#addNewApp" id="btnAddApp">
                            <span class="glyphicon glyphicon-plus-sign"></span> Add New Application
                        </button>
                        <br /><br /><br />
                        <div id="appManagement" class="box-body">
                        </div>                    
                    </div>
                </div>
                <div class="tab-pane fade" id="config" role="tabpanel">
                    <div class="col-md-12">
                        <center><h1 class="page-heading">Configuration</h1></center>
                        <div class="box-body">
                            <form class="form-horizontal" id="updateURLForm">
                                 <div class="form-group">
                                    <label for="URLInfo" class="control-label">URL Info: </label>
                                    <input type="text" class="form-control" id="urlInputForm" name="urlInputForm" placeholder="http://www.example.com">
                                    <button id="btn_update_url" type="button" style="margin-top:5px;" class="btn btn-primary">Update</button>
                                </div>
                            </form> 
                            <hr>
                            <div class="row">
                                <div class="row form-group">
                                    <label class="col-sm-8 control-label">List accepted IP</label>
                                    <button class="col-sm-4 text-right btn btn-primary " id="addIPAddressBtn" style="margin-bottom: 10px;"><span class="glyphicon glyphicon-plus-sign"></span> Add accepted IP</button>
                                </div>
                                <div id="agentIpLst" class="box-body">
                                </div>
                            </div>
                        </div>                    
                    </div>
                </div> 
            </div>
        </div>
        <!--/Agent DataTable-->

        <script type="text/javascript" src="${static_url}/jquery/jquery.min.js"></script>
        <script type="text/javascript" src="${static_url}/bootstrap/js/bootstrap.min.js"></script>  
        <script type="text/javascript" src="${static_url}/bootstrap/js/bootstrap-dialog.min.js"></script>  
        <script type="text/javascript" src="${static_url}/bootstrap/js/bootstrap-multiselect.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.core.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.query.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.pagination.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.grid.js"></script> 
        <!--script type="text/javascript" src="${static_url}/custom/js/validator.min.js"></script--!>
        <!--script type="text/javascript" src="${static_url}/custom/js/renderHtml.js"></script--!>
        <!--script type="text/javascript" src="${static_url}/custom/js/app.js"></script--!>
        <script type="text/javascript" src="${static_url}/custom/js/home.js"></script>

</body>
</html>
