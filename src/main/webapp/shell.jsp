<%@page contentType="text/html" pageEncoding="UTF-8"%>  
<html id="ng-app" ng-app="${applicationName}">
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
Hello!!! ${ applicationName }
	<div ng-view>
	</div>
	
	
	<script type='text/ng-template' id='templates/LandingPage.html'><%@include file="js/app/templates/LandingPage.html" %></script>
	<script type='text/ng-template' id='templates/Login.jsp'><%@include file="js/app/templates/Login.jsp" %></script>
	<script type='text/ng-template' id='templates/Registration.jsp'><%@include file="js/app/templates/Registration.jsp" %></script>
	<script type='text/ng-template' id='templates/RegistrationFlow.jsp'><%@include file="js/app/templates/RegistrationFlow.jsp" %></script>
	<script type='text/ng-template' id='templates/RegistrationApproval.jsp'><%@include file="js/app/templates/RegistrationApproval.jsp" %></script>

	<script type='text/ng-template' id='templates/Admin/Dashboard.html'><%@include file="js/app/templates/Admin/Dashboard.html" %></script>
	<script type='text/ng-template' id='templates/User/Dashboard.html'><%@include file="js/app/templates/User/Dashboard.html" %></script>
	
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
	<script src="js/app/services/Events.js"></script>
	<script src="js/app/controllers/UserLoginController.js"></script>
	<script src="js/app/controllers/UserListController.js"></script>
	<script src="js/app/controllers/UserRegistrationController.js"></script>
	<script src="js/app/controllers/UserRegistrationFlowController.js"></script>
	<script src="js/app/controllers/UserRegistrationApprovalController.js"></script>
	<script src="js/app/controllers/DashboardController.js"></script>
	<script src="js/app/translations.js"></script>
</body>
</html>

