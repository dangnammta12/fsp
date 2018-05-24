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
      <link href="${static_url}/css/fsp.css" rel="stylesheet">

    </head>
    <body class="sidebar-mini skin-blue">
        <div style="display:none;" class="loading">Loading&#8230;</div>
        <div class="wrapper" style="height: auto;">
            ${MENU_TOP}
            ${MENU_LEFT!}
            <div class="content-wrapper" style="min-height: 946px; ${COLSE_MENU_LEFT!}">
            <section class="content-header hidden">
                <div class="row">
                    <div class="col-md-3 text-left">
                            <button id="btn_new" type="button" class="btn btn-info  btn-lg dropdown-toggle" data-toggle="dropdown" aria-expanded="false">New
                              <span class="fa fa-caret-down"></span></button>
                            <ul class="dropdown-menu">
                              <li><a id="btn_createFolder" href="javascript:void(0);">Create Folder</a></li>
                              <li><a id="btn_upload" href="javascript:void(0);">Upload File</a></li>
                            </ul>
                    </div>
                    <div class="col-md-9">
                        <ol id="menu_horizontal" class="breadcrumb text-right" style="background-color:#ecf0f5;">
                        </ol>
                    </div>
                </div>
            </section>
            <section class="content">
                <div class="error-page">
                    <div class="row">
                        <div class="col-md-5 text-right">
                            <h2 class="headline text-yellow"> <img src="${static_url}/image/download_256.png"></h2>
                        </div>
                        <div class="col-md-7" style="padding:45px 0px;">
                            <div class="row">
                            <h3><i class="fa fa-warning text-yellow"></i> FSP download file.</h3>
                            <p>
                              ${FILENAME} (${FILESIZE})<br>
                              ${FileStatus}
                            </p>
                            <form class="search-form">
                                <div class="input-group" style="width:400px">
                                  <input type="text" id="pathDest" class="form-control" placeholder="Enter Path Destination" required autofocus >

                                </div>
                                <!-- /.input-group -->
                              </form>
                              <br>
                            <button id="btn_download" type="button" ${show_download}  class="btn btn-primary">Download</button>
                        </div>
                    </div>
                  
                </div>
                <!-- /.error-page -->
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
        <script type="text/javascript" src="${static_url}/js/gonrin.core.js"></script>
        <script type="text/javascript" src="${static_url}/js/gonrin.query.js"></script>
        <script type="text/javascript" src="${static_url}/js/gonrin.pagination.js"></script>
        <script type="text/javascript" src="${static_url}/js/gonrin.grid.js"></script>
        <script type="text/javascript" src="${static_url}/js/home.js"></script>
        <script>
            $(document).ready(function () {
               fsp.viewerId = '${viewerId}';
                $("#btn_download").click(function() {
                    //var input = $("#chooseFolder"); 
                    //input.trigger('click');
                    var pathDest = $("#pathDest").val();
                    showDownloadForm('${FILENAME}','${FILEID}', pathDest, 0);
                    return false;
                });
            });
        </script>
    </body>
</html>