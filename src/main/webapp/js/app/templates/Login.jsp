<div class="row">
	<div class="container-fluid">
		<div class="login-masthead">
			<img class="center-block"src="images/ihtsdo-logo-lg.gif" alt="ihtsdo-logo" width="516" height="102">
		</div>
	</div>
</div>
<div class="row">
	<div class="container">
      <form class="form-signin"  ng-submit="login()">
        <h2 class="form-signin-heading">Please sign in</h2>
	      <div class="form-group">
	        <input type="email" ng-model="loginForm.username" class="form-control" placeholder="Email address" required autofocus>
	      </div>
	      <div class="form-group">
	        <input type="password" ng-model="loginForm.password" class="form-control" placeholder="Password" required>
	      </div>
	        <label class="checkbox">
	          <input type="checkbox" value="remember-me"> Remember me
	        </label>
		  <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
		  
		  <div class="form-register">
		  	 <a href="#/registration">Register</a>
		  </div>
     
      </form>

    </div> <!-- /container -->
</div>
</div>
</div>
<!-- /container -->