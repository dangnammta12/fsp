<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html lang="en">
    <head>
        <title>Dis Information</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        
        <link rel="stylesheet" type="text/css" href="${static_url}/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="${static_url}/bootstrap/css/bootstrap-dialog.min.css">
        <link rel="stylesheet" type="text/css" href="${static_url}/custom/css/agent.css">

    </head>
    <body>
        <div style="display:none;" class="loading">Loading&#8230;</div>
        <div class="wrapper" style="height: auto;">
            <div class="content-wrapper" style="min-height: 946px; margin-left: 0px;">
                <section class="content">
                    <div class="row">
                        <div class="col-md-10 col-md-offset-1">
                            <div class="box box-primary" style="margin-bottom:0px;">
                                <div class="box-header">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <h1 class="page-header">DIS Info</h1>
                                        </div>
                                        <!-- /.col-md-12 -->
                                    </div>
                                </div>
                                <div class="box-body">
                                    <form class="form-horizontal" id="updateURLForm">
                                         <div class="form-group">
                                            <label for="URLInfo" class="control-label">URL Info: </label>
                                            <div class="row">
                                            <div class="col-sm-7">
                                            <input type="text" class="form-control" id="urlInputForm" name="urlInputForm" placeholder="http://www.example.com">
                                            </div>
                                            <div class="col-sm-5">
                                            <button id="btn_update_url" type="button" class="btn btn-primary">Update</button>
                                            </div>
                                            </div>
                                        </div>
                                    </form> 

                                    <div class="row">
                                        <button class="btn btn-primary text-right" id="addIPAddressBtn" style="margin-bottom: 10px;"><span class="glyphicon glyphicon-plus-sign"></span> Add accepted IP Addresses</button>
                                        <div id="agentIpLst" class="box-body">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div> 
                    </div>
                </section>
            </div>
        </div> 

        
         <!-- Create Agent modal -->
        <div class="modal fade" tabindex="-1" role="dialog" id="addIpAddresses">
            <!-- /.modal-dialog -->
        </div>
        
        <script type="text/javascript" src="${static_url}/jquery/jquery.min.js"></script>
        <script type="text/javascript" src="${static_url}/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="${static_url}/bootstrap/js/bootstrap-dialog.min.js"></script>  
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.core.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.query.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.pagination.js"></script>
        <script type="text/javascript" src="${static_url}/custom/js/gonrin.grid.js"></script> 
        <script type="text/javascript" src="${static_url}/custom/js/dis.js"></script>
    </body>
</html>
