<html id="ng-app" ng-app="MLDS">
<head>
<title ng-bind="'TITLE' | translate">IHTSDO - MLDS</title>
<!--[if lt IE 9]>
      <script src="bower_components/html5shiv/dist/html5shiv.js"></script>
      <script src="bower_components/respond/respond.min.js"></script>
      <script src="bower_components/es5-shim/es5-shim.js"></script>
      <script src="bower_components/json3/lib/json3.min.js"></script>
    <![endif]-->
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
</head>
<body class="body-dark">
	
	<%@include file="registration.jsp" %>
	
	<div class="debug container" ng-controller="UserListController">
		<form role="form" ng-submit="createUser()">
			<div class="form-control" style="height: auto;">
				<label for="userEmail">Email:</label> 		
				<input class="form-control" id="userEmail" type="email" ng-model="user.email" /><br />
			</div>
    		<input type="submit" class="btn btn-default" name="Submit"/>
		</form>	
		<h2>List of Users</h2>
		
		<table class="table">
			<tr><th>Email</th></tr>
			<tr ng-repeat="user in users">
				<td>{{user.email}}</td>
			</tr>
		</table>
	</div>
	
	<script src="bower_components/jquery/jquery.js"></script>
	<script src="bower_components/angular/angular.js"></script>
	<script src="bower_components/angular-translate/angular-translate.js"></script>
	<script src="bower_components/angular-cookies/angular-cookies.js"></script>
	<script src="bower_components/angular-resource/angular-resource.js"></script>
	<script src="bower_components/angular-route/angular-route.js"></script>
	<script src="bower_components/underscore/underscore.js"></script>
	<script src="bower_components/js-base64/base64.js"></script>

	<script src="bower_components/bootstrap/dist/js/bootstrap.js"></script>

	<script src="js/app/app.js"></script>
	<script src="js/app/services/UserRegistrationService.js"></script>
	<script src="js/app/controllers/UserListController.js"></script>
	<script src="js/app/translations.js"></script>
</body>
</html>

