<!DOCTYPE html>
<html lang="en">
   <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
      <meta name="description" content="">
      <meta name="author" content="">
      <!--<link rel="icon" href="../../favicon.ico">-->
      <title>Digital Storage</title>
      <!-- Bootstrap core CSS -->
      <link href="${static_url}/css/font-awesome.min.css" rel="stylesheet">
      <link href="${static_url}/css/bootstrap.min.css" rel="stylesheet">
      <link href="${static_url}/css/bootstrap-dialog.min.css" rel="stylesheet">
      <link href="${static_url}/css/style_box.css" rel="stylesheet">
      <link rel="stylesheet" href="${static_url}/css/formValidation.css" />
      <!-- Include Bootstrap Datepicker -->
      <link rel="stylesheet" href="${static_url}/css/datepicker.css" />
      <link href="${static_url}/css/fsp.css" rel="stylesheet">

    </head>
    <body class="skin-blue sidebar-mini">
        <div style="display:none;" class="loading">Loading&#8230;</div>
        <div class="wrapper" style="height: auto;">
            ${MENU_TOP}
            <div class="content-wrapper" style="min-height: 946px; margin-left: 0px;">
            <section class="content">
                <div class="row">
                    <div class="col-md-10 col-md-offset-1">
                        <div class="box box-primary" style="margin-bottom:0px;">
                            <div class="box-header">
                                <div class="row">
                                   <div class="col-md-12">
                                      <h1 class="page-header">View profile</h1>
                                   </div>
                                   <!-- /.col-md-12 -->
                                </div>
                            </div>
                            <div class="box-body">
                                <div class="row">
                                    <!-- left column -->
                                    <div class="col-md-3">
                                      <div class="text-center">
                                        <img src="${static_url}/image/avatar_2x.png" class="avatar img-circle" alt="avatar">

                                      </div>
                                    </div>

                                    <!-- edit form column -->
                                    <div class="col-md-9">
                                      <div id="alert_message" style="display:none;" class="alert alert-info alert-dismissable">
                                        <a class="panel-close close" data-dismiss="alert">×</a> 
                                        <i class="fa fa-coffee"></i>
                                            <div id="error_message" This is an <strong>.alert</strong>. Use this to show important messages to the user.</div>
                                      </div>
                                      <h3>${personal_info!}</h3>

                                      <form id="eventForm" class="form-horizontal" role="form" autocomplete="off">
                                        <div class="form-group required">
                                          <label class="col-lg-3 control-label">FullName</label>
                                          <div class="col-lg-8">
                                            <input id="val_fullname" name="fullname" class="form-control" value="${fullname!}" type="text">
                                          </div>
                                        </div>
                                        <div class="form-group required">
                                          <label class="col-lg-3 control-label">Email</label>
                                          <div class="col-lg-8">
                                            <input disabled="true" id="val_email" class="form-control" name="email" value="${email!}" type="text">
                                          </div>
                                        </div>
                                        <!--<div class="form-group required">
                                          <label class="col-lg-3 control-label">Password</label>
                                          <div class="col-lg-8">
                                            <input id="val_password" name="password" data-rule-password="true" required  class="form-control" value="" type="password">
                                          </div>
                                        </div>
                                        <div class="form-group required">
                                          <label class="col-lg-3 control-label">Password Confirm</label>
                                          <div class="col-lg-8">
                                            <input id="val_confirm_password" name="confirm_password" data-rule-password="true" required class="form-control" value="" type="password">
                                          </div>
                                        </div>
                                        <div class="form-group">
                                            <label class="col-lg-3 control-label">Type Accout</label>
                                          <div class="col-lg-8">
                                            <div class="ui-select">
                                              <select id="user_type" class="form-control">
                                                <option  value="0">User</option>
                                                <option value="1">Admin</option>
                                              </select>
                                            </div>
                                          </div>
                                        </div--!>
                                        <div class="form-group required">
                                            <label class="col-xs-3 control-label">Gender</label>
                                          <div class="col-xs-5">
                                            <div class="ui-select">
                                              <select name="gender" id="val_gender" class="form-control">
                                                <option ${selected_male!} value="0">Male</option>
                                                <option ${selected_female!} value="1">Female</option>
                                              </select>
                                            </div>
                                          </div>
                                        </div>
                                        <div class="form-group required" id="sandbox-container">
                                            <label class="col-xs-3 control-label">Birthday</label>
                                            <div class="col-xs-5 date">
                                                <div class="input-daterange input-group input-append date" id="datepicker">
                                                    <input value="${birthday!}" id="val_birthday" name="birthday" type="text" class="input-sm form-control" placeholder="DD/MM/YYYY" autocomplete="off"/>
                                                    <span  onclick="focusInputDate();" class="input-group-addon add-on"><span class="fa fa-calendar"></span></span>
                                                </div>
                                            </div>
                                        </div> 
                                        <div class="form-group">
                                          <label class="col-lg-3 control-label">Department</label>
                                          <div class="col-lg-8">
                                            <input id="val_department" name="department" class="form-control" value="${department!}" type="text">
                                          </div>
                                        </div>
                                        <div class="form-group">
                                          <label class="col-md-3 control-label">Phone number</label>
                                          <div class="col-md-8">
                                                <input id="val_phone" name="phone" class="form-control" value="${numberPhone!}" type="text">
                                          </div>
                                        </div>
                                        <div class="form-group">
                                          <label class="col-md-3 control-label">Address</label>
                                          <div class="col-md-8">
                                                <textarea id="val_address" name="address" rows="3" class="form-control">${address!}</textarea>
                                          </div>
                                        </div>
                                        <div class="form-group">
                                          <label class="col-md-3 control-label"></label>
                                          <div class="col-md-8">
                                            <input onclick="updateProfile();" class="btn btn-primary" value="Save" type="button">
                                          </div>
                                        </div>
                                      </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div><!-- /.end col-lg-12 -->
                </div>
            </section>
        </div>
    </div> 
        ${FOOTER!}
        <!-- Bootstrap core JavaScript
        ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->
        <script src="${static_url}/js/jquery.min.js"></script>
        <script src="${static_url}/js/bootstrap.min.js"></script>

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script type="text/javascript" src="${static_url}/js/html5shiv.min.js"></script>
            <script type="text/javascript" src="${static_url}/js/respond.min.js"></script>
        <![endif]-->
        <script type="text/javascript" src="${static_url}/js/bootstrap-dialog.min.js"></script>
        
        <script type="text/javascript" src="${static_url}/js/jquery.validate.min.js"></script>
        <script type="text/javascript" src="${static_url}/js/framework_bootstrap.min.js"></script>
        <script type="text/javascript" src="${static_url}/js/moment-with-locales.min.js"></script>
        <script type="text/javascript" src="${static_url}/js/datepicker.min.js"></script>
        <script type="text/javascript" src="${static_url}/js/update_profile.js"></script>
        <script type="text/javascript" src="${static_url}/js/home.js"></script>
<script>

</script>
    </body>
</html>