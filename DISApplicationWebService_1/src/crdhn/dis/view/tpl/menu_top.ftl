<header class="main-header">
    <!-- Logo -->
    <a href="/home" class="logo">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini"><b>DiS</b></span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg"><b>Digital Storage Platform</b></span>
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <div class="navbar-custom-menu">
          <ul class="nav navbar-nav">
            <li class="${show_user!}">
                    <a title="${viewerFullName!}" href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                        <i class="fa fa-user fa-fw"></i> ${viewerName!}  <span class="caret"></span>
                    </a>
                <ul class="dropdown-menu">
                    <li><a href="/profile"><i class="fa fa-user fa-fw"></i> Your Profile</a>
                    </li>
                    <li><a id="change_password" href="javascript:void(0);"><i class="fa fa-gear fa-fw"></i> Change Password</a>
                    </li>
                    <li class="divider"></li>
                    <li><a  href="/logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                    </li>
                </ul>
            </li>
                <!-- /.dropdown-user -->
           </ul>
      </div>
    </nav>
  </header>

