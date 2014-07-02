Tips to Debugging IE8 Issues Errors
================================

Do remember to load EcmaScript 5 (es5-shim)

Follow words are reserved keywords in IE8 and will throw a parse error 
- delete
- catch

Example:
_this.$http.delete("")

Needs to look like:
_this.$http.["delete"]("")

In the example of using $http another way to fix the issue would be to use $http() directly for example:
$http({
      method: 'DELETE', 
      url: ''
  }); 
