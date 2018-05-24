<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <!--<link rel="icon" href="../../favicon.ico">--!>

        <title>Digital Storage</title>

        <!-- Bootstrap core CSS -->
        <link href="${static_url}/css/font-awesome.min.css" rel="stylesheet">
        <link href="${static_url}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${static_url}/css/bootstrap-dialog.min.css" rel="stylesheet">
        <link href="${static_url}/css/fsp.css" rel="stylesheet">
        <link href="${static_url}/css/login.css" rel="stylesheet">
    </head>

    <body>
    <div style="display:none;" class="loading">Loading&#8230;</div>
        <div id="wrapper">
             <div class="container align0">
                <div class="card card-container">
                    <img id="profile-img" class="profile-img-card" src="${static_url}/image/avatar_2x.png" />
                    <div style="display:none; word-wrap: break-word;" id="error_message" class="alert alert-danger">
                        The user name or password you entered isn't correct. Try entering it again.
                    </div> 
                    <p id="profile-name" class="profile-name-card"></p>
                    <form class="form-signin" action="/login" method="POST">
                        <span id="reauth-email" class="reauth-email"></span>
                        <input type="text" id="email" class="form-control" placeholder="Enter Email" required autofocus name="email">
                        <input type="password" id="password" class="form-control" placeholder="Enter Password" required name="password">
                        <div class="row">
                            <div id="remember" class="col-lg-5 checkbox">
                                <label>
                                    <input type="checkbox" value="remember-me" name="remember"> Remember Me
                                </label>
                            </div>
                            <a href="javascript:void(0);" class="col-lg-7 text-right forgot-password"  style="margin-top:10px;">
                                Forgot your Password?
                            </a>
                        </div>
                        <a id="btnlogin" class="btn btn-lg btn-primary btn-block btn-signin" href="javascript:void(0);" >Login</a>
                    </form>
                    <br>
                    <div class="row form-inline">
                        <span class="text-muted">Not registered?</label>
                        <a id="register" href="/register" class="forgot-password">
                            Register a new membership
                        </a>
                    </div>
                    <br>
                </div><!-- /card-container -->
            </div><!-- /container -->
            
        </div>

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
        <script type="text/javascript" src="${static_url}/js/login.js"></script>
    
<script> 
    $(document).ready(function() {
    document.getElementById('email').onkeypress = function(e) {
        var event = e || window.event;
        var charCode = event.which || event.keyCode;
        if ( charCode == '13' ) {
          login();
          return false;
        }
      }
    document.getElementById('password').onkeypress = function(e) {
        var event = e || window.event;
        var charCode = event.which || event.keyCode;
        if ( charCode == '13' ) {
          login();
          return false;
        }
      }
      $(".forgot-password").bind("click",function(){
        resetpass();
        return false;
        });
    });
</script>
    </body>
</html>