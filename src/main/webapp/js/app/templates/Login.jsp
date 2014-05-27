<div class="container">
	<h1>Login Page</h1>
	
	<form ng-submit="login()">
		<input type="text" ng-model="loginForm.username"/>
		<input type="password" ng-model="loginForm.password"/>
		<input type="submit"/>
	</form>
	
	<a href="#/registration">Register</a>
	
	
	
	<div class="container" ng-controller="UserListController">	
		<h2>List of Users</h2>
		<table class="table">
			<tr><th>Email</th></tr>
			<tr ng-repeat="user in users">
				<td>{{user.email}}</td>
			</tr>
		</table>
	</div>
</div>