'use strict';

mldsApp.controller('CountryController', ['$scope', 'resolvedCountry', 'Countries', '$modal', '$log',
    function ($scope, resolvedCountry, Countries, $modal, $log) {

        $scope.countries = resolvedCountry;
        
        var reloadCountries = function reloadCountries() {
			$scope.countries = Countries.query();
        };
        
        // FIXME MLDS-234 MB merge this with CountriesService?  Notify dirty?

        var editModalController = ['$scope', '$modalInstance',
            function($scope,$modalInstance) {
        	
        	$scope.create = function () {
        		Countries.save($scope.country,
        				function () {
        			$modalInstance.close();
        			reloadCountries();
        			$scope.clear();
        		});
        	};
        }];

        $scope.openCreate = function openCreate() {
        	$scope.clear();
    		$modal.open({
    			templateUrl: 'views/countriesEditModal.html',
    			controller: editModalController,
    			scope: $scope
    		});
        }
        $scope.update = function (isoCode2) {
            $scope.country = Countries.get({isoCode2: isoCode2});
    		$modal.open({
    			templateUrl: 'views/countriesEditModal.html',
    			controller: editModalController,
    			scope: $scope,
    			resolve: {
    				countryPromise: $scope.country.$promise
    		      }

    		});
        };

        $scope['delete'] = function (isoCode2) {
            Countries['delete']({isoCode2: isoCode2}, reloadCountries);
        };

        $scope.clear = function () {
            $scope.country = {isoCode2: null};
        };
    }]);
