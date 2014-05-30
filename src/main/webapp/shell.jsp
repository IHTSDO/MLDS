<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="java.io.Reader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>  
<%@page import="java.util.List"%>
<%@page import="ca.intelliware.ihtsdo.mlds.web.TemplateCollector"%>
<%@page import="java.nio.file.FileVisitor"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.nio.file.Files"%>

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
	<div ng-view>
	</div>
	
	
	<%
		TemplateCollector collector =  (TemplateCollector)request.getAttribute("templateCollector");
		List<String> templateFiles = collector.getRelativeFiles("/js/app/templates");
		for(String templateFile: templateFiles) {
			Reader templateReader = new InputStreamReader(config.getServletContext().getResourceAsStream("/js/app/templates/" +templateFile),"UTF-8");
			%>
			<script type='text/ng-template' id='templates/<%= templateFile %>'><%
				IOUtils.copy(templateReader, out);
				templateReader.close();
			%></script>
			<%
		}
	%>
	
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
	<script src="js/app/controllers/registration/UserRegistrationController.js"></script>
	<script src="js/app/controllers/LandingPageController.js"></script>
	<script src="js/app/controllers/UserListController.js"></script>
	<script src="js/app/controllers/UserRegistrationFlowController.js"></script>
	<script src="js/app/controllers/UserRegistrationApprovalController.js"></script>
	<script src="js/app/controllers/DashboardController.js"></script>
	<script src="js/app/controllers/AdminDashboardController.js"></script>
	<script src="js/app/translations.js"></script>
</body>
</html>

