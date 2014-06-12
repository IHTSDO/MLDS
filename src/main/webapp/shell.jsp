<%@page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>  
<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="java.io.Reader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.util.List"%>
<%@page import="ca.intelliware.ihtsdo.mlds.web.TemplateCollector"%>
<%@page import="java.nio.file.FileVisitor"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.nio.file.Files"%>
<!doctype html>
<html id="ng-app" ng-app="${applicationName}" xmlns:ng="http://angularjs.org">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title ng-bind="'TITLE' | translate">IHTSDO - MLDS</title>
<!--[if lt IE 9]>
      <script src="bower_components/html5shiv/dist/html5shiv.js"></script>
      <script src="bower_components/respond/respond.min.js"></script>
      <script src="bower_components/es5-shim/es5-shim.js"></script>
      <script src="bower_components/json3/lib/json3.min.js"></script>
    <![endif]-->
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/main.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
</head>
<body>
	<div ng-view>
	</div>
	
	
	<%
		TemplateCollector collector =  (TemplateCollector)request.getAttribute("templateCollector");
		List<String> templateFiles = collector.getRelativeFiles("/js/app/templates");
		for(String templateFile: templateFiles) {
			Reader templateReader = new InputStreamReader(config.getServletContext().getResourceAsStream("/js/app/templates/" +templateFile),"UTF-8");			
			String url = templateFile.replace("\\", "/");			
			%>
			<script type='text/ng-template' id='templates/<%= url %>'><%
				IOUtils.copy(templateReader, out);
				templateReader.close();
			%></script>
			<%
		}
	%>
	<script type="text/javascript">
		var mlds = mlds || {};
		mlds.userInfo = <%= new ObjectMapper().writer().writeValueAsString(request.getAttribute("userInfo")) %>;
	</script>
	
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
	<script src="js/app/translations.js"></script>
	<script src="js/app/services/UserRegistrationService.js"></script>
	<script src="js/app/services/UserSession.js"></script>
	<script src="js/app/services/DomainBlacklistService.js"></script>
	<script src="js/app/services/Events.js"></script>
	<script src="js/app/controllers/registration/NewUserRegistrationController.js"></script>
	<script src="js/app/controllers/registration/AffiliateRegistrationController.js"></script>
	<script src="js/app/controllers/LogoutController.js"></script>
	<script src="js/app/controllers/UserListController.js"></script>
	<script src="js/app/controllers/UserDashboardController.js"></script>
	<script src="js/app/controllers/AdminDashboardController.js"></script>
	<script src="js/app/directives/bsValidation.js"></script>
	<script type="text/javascript" src="https://jira.ihtsdotools.org/s/d41d8cd98f00b204e9800998ecf8427e/en_US-v99clk-1988229788/6262/3/1.4.7/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?collectorId=89d47347"></script>

Cheers,
</body>
</html>

