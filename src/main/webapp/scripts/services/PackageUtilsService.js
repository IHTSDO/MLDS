'use strict';

angular.module('MLDS').factory('PackageUtilsService',
		[ '$rootScope', '$resource', '$q', '$log', '$location', '$modal', 'Session', 'UserRegistrationService', 'MemberService', 'UserAffiliateService', '$filter',
		  function($rootScope, $resource, $q, $log, $location, $modal, Session, UserRegistrationService, MemberService, UserAffiliateService, $filter) {

			let service = {};

			service.isReleasePackageInactive = function isReleasePackageInactive(packageEntity) {
				return (packageEntity.inactiveAt) ? true : false;
			};

			service.getMemberOrder = function getMemberOrder(packageEntity) {
				if (packageEntity.member.key === 'IHTSDO') {
					return 0;
				} else {
					let member = MemberService.membersByKey[packageEntity.member.key];
					if (member && member.promotePackages) {
						return 1;
					} else {
						return -1;
					}
				}
			};

			service.releasePackageOrder = [
			                              service.getMemberOrder,
			              			     'priority',
			              			   service.getLatestPublishedDate
			              			];

			service.releasePackageSort = function releasePackageSort(releasePackages) {
				return $filter('orderBy')(releasePackages, service.releasePackageOrder, true);
			};




	        service.isPackagePublished = function isPackagePublished(packageEntity) {
            	        	for(const element of packageEntity.releaseVersions) {
            	        		if (element.releaseType === "online" && !element.archive) {
            	        			return true;
            	        		};
            	        	}
            	        	return false;
            };




              service.isPackageOffline = function isPackageOffline(packageEntity) {
            				let offlineCount =0 ;
            				let alphabetaCount = 0;
                                        for (const releaseVersion of packageEntity.releaseVersions) {
                                            if (releaseVersion.releaseType === "offline" && !releaseVersion.archive) {
                                                offlineCount++;
                                            } else if (releaseVersion.releaseType === "alpha/beta" && !releaseVersion.archive) {
                                                alphabetaCount++;
                                            }
                                        }
            				return alphabetaCount <= 0;
                        };


               service.isPackageFullyArchived = function isPackageFullyArchived(packageEntity){
               let archivePackageCount = 0;
                  for (const releaseVersion of packageEntity.releaseVersions) {
                      if (releaseVersion.archive) {
                          archivePackageCount++;
                      }
                  }
                   return archivePackageCount === packageEntity.releaseVersions.length && packageEntity.releaseVersions.length !== 0;
               }





           service.isPackageEmpty = function isPackageEmpty(packageEntity) {
               return packageEntity.releaseVersions.length === 0;
           };




            service.isPackageNotPublished = function isPackageNotPublished(packageEntity) {
                for (const releaseVersion of packageEntity.releaseVersions) {
                    if (releaseVersion.releaseType === "alpha/beta" && !releaseVersion.archive) {
                        return true;
                    }
                }
                return false;
            };


	        service.getLatestPublishedDate = function getLatestPublishedDate(packageEntity) {
                let latestPublishDate = null;

                for (const releaseVersion of packageEntity.releaseVersions) {
                    const publishDate = new Date(releaseVersion.publishedAt);
                    if (!latestPublishDate || publishDate > new Date(latestPublishDate)) {
                        latestPublishDate = releaseVersion.publishedAt;
                    }
                }

                return latestPublishDate || new Date();
            };



          service.isVersionOnline = function isVersionOnline(releaseVersion) {
              return releaseVersion.releaseType === "online" && !releaseVersion.archive;
          };


            service.isVersionAlphaBeta = function isVersionAlphaBeta(releaseVersion) {
                return releaseVersion.releaseType === "alpha/beta" && !releaseVersion.archive;
            };


            service.isVersionOffline = function isVersionOffline(releaseVersion) {
                return releaseVersion.releaseType === "offline" && !releaseVersion.archive;
            }




	        service.isLatestVersion = function isLatestVersion(version, versions) {
	        	if (!version.publishedAt) {
	        		return false;
	        	}
	        	let versionPublishedDate = new Date(version.publishedAt);
	        	for(const element of versions) {
	        		if (element.publishedAt &&
	    				(versionPublishedDate < new Date(element.publishedAt) )) {
	    				return false;
	    			};
	        	};
	        	return true;
	        };


       service.updateVersionsLists = function updateVersionsLists(packgeEntity) {
           const results = { online: [], alphabeta: [], offline: [], archive: [] };

           const categorizedVersions = service.categorizeReleaseVersions(packgeEntity.releaseVersions);
           service.sortVersions(categorizedVersions);
           service.concatenateSortedArrays(categorizedVersions, results);

           results.archive = categorizedVersions.archive;
           results.online = categorizedVersions.online;

           return results;
       };

       service.categorizeReleaseVersions = function categorizeReleaseVersions(releaseVersions) {
           const categorizedVersions = {
               archive: [],
               publishedOffline: [],
               nonPublishedOffline: [],
               publishedAlphaBeta: [],
               nonPublishedAlphaBeta: [],
               online: []
           };

           releaseVersions.forEach(releaseVersion => {
               if (releaseVersion.archive) {
                   categorizedVersions.archive.push(releaseVersion);
               } else {
                   service.categorizeByReleaseType(releaseVersion, categorizedVersions);
               }
           });

           return categorizedVersions;
       };

       service.categorizeByReleaseType = function categorizeByReleaseType(releaseVersion, categorizedVersions) {
           const { publishedAt, releaseType } = releaseVersion;

           const categorizationMap = {
               "online": () => categorizedVersions.online.push(releaseVersion),
               "offline": () => {
                   if (publishedAt) {
                       categorizedVersions.publishedOffline.push(releaseVersion);
                   } else {
                       categorizedVersions.nonPublishedOffline.push(releaseVersion);
                   }
               },
               "alpha/beta": () => {
                   if (publishedAt) {
                       categorizedVersions.publishedAlphaBeta.push(releaseVersion);
                   } else {
                       categorizedVersions.nonPublishedAlphaBeta.push(releaseVersion);
                   }
               }
           };

           if (categorizationMap[releaseType]) {
               categorizationMap[releaseType]();
           }
       };


	   service.sortByPublishedDateDesc = function(a, b) {
           return new Date(b.publishedAt || b.createdAt) - new Date(a.publishedAt || a.createdAt);
       }

       service.sortVersions = function(categorizedVersions) {
           Object.values(categorizedVersions).forEach(list => list.sort(service.sortByPublishedDateDesc));
       }

        service.concatenateSortedArrays = function(categorizedVersions, results)  {
           results.offline = categorizedVersions.publishedOffline.concat(categorizedVersions.nonPublishedOffline);
           results.alphabeta = categorizedVersions.publishedAlphaBeta.concat(categorizedVersions.nonPublishedAlphaBeta);
        }




	    	service.isEditableReleasePackage = function isEditableReleasePackage(releasePackage) {
	    		let userMember = Session.member;
	    		let memberMatches = angular.equals(userMember, releasePackage.member);

	    		return Session.isAdmin() || memberMatches;
	    	};

	    	service.isRemovableReleasePackage = function isRemovableReleasePackage(releasePackage) {
	    		return !service.isPackagePublished(releasePackage);
	    	};

	    	service.isReleasePackageMatchingMember = function isReleasePackageMatchingMember(releasePackage) {
	    		let userMember = Session.member;
	    		let memberMatches = angular.equals(userMember, releasePackage.member);
	    		return memberMatches;
	    	};

	    	service.isEditableReleasePackage = function isEditableReleasePackage(releasePackage) {
	    		return this.isReleasePackageMatchingMember(releasePackage) || Session.isAdmin();
	    	};

	    	service.openExtensionApplication = function openExtensionApplication(releasePackage) {

	    		let modalInstance = $modal.open({
	    		      templateUrl: 'views/user/startExtensionApplicationModal.html',
	    		      controller: 'StartExtensionApplicationModalController',
	    		      size: 'sm',
	    		      backdrop: 'static',
	    		    	  resolve: {
								releasePackage: function() {
									return releasePackage;
								}
							}
	    		    });

				modalInstance.result
					.then(function(result) {
						UserAffiliateService.refreshAffiliate();
		    			if(result.data?.applicationId) {
		    				$location.path('/extensionApplication/'+ result.data.applicationId);
		    			}
		    	});
	    	};

			return service;
		} ]);
