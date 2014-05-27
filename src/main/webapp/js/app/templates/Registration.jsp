<div class="container">
	<h1>Registration</h1>
	<form role="form" ng-submit="createUser()">
		<div class="form-control" style="height: auto;">
			<label for="userEmail">Email:</label> 		
			<input class="form-control" id="userEmail" type="email" ng-model="user.email" /><br />
		</div>
   		<input type="submit" class="btn btn-default" name="Submit"/>
	</form>	
</div>