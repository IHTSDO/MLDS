mldsApp.factory('ReleaseFileDownloadCountService', ['$http', function ($http) {
    return {
        findReleaseFileDownloadCounts: function (params) {
            return $http.post('/api/audits/getAllAuditEvents', params)
                .then(function (response) {
                    return response.data;
                });
        }
    };
}]);
