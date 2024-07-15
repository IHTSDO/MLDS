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
            let results = { online: [], alphabeta: [], offline: [], archive: [] };

            function sortByPublishedDateDesc(a, b) {
                return new Date(b.publishedAt || b.createdAt) - new Date(a.publishedAt || a.createdAt);
            }

            function categorizeArchive(releaseVersion, categorizedVersions) {
                if (releaseVersion.archive) {
                    categorizedVersions.archive.push(releaseVersion);
                    return true;
                }
                return false;
            }

            function categorizeByReleaseType(releaseVersion, categorizedVersions) {
                switch (releaseVersion.releaseType) {
                    case "online":
                        categorizedVersions.online.push(releaseVersion);
                        break;
                    case "offline":
                        if (releaseVersion.publishedAt) {
                            categorizedVersions.publishedOffline.push(releaseVersion);
                        } else {
                            categorizedVersions.nonPublishedOffline.push(releaseVersion);
                        }
                        break;
                    case "alpha/beta":
                        if (releaseVersion.publishedAt) {
                            categorizedVersions.publishedAlphaBeta.push(releaseVersion);
                        } else {
                            categorizedVersions.nonPublishedAlphaBeta.push(releaseVersion);
                        }
                        break;
                    default:
                        // Handle unexpected release types if necessary
                        break;
                }
            }

            function categorizeReleaseVersions(releaseVersions) {
                let categorizedVersions = {
                    archive: [],
                    publishedOffline: [],
                    nonPublishedOffline: [],
                    publishedAlphaBeta: [],
                    nonPublishedAlphaBeta: [],
                    online: []
                };

                for (const releaseVersion of releaseVersions) {
                    if (!categorizeArchive(releaseVersion, categorizedVersions)) {
                        categorizeByReleaseType(releaseVersion, categorizedVersions);
                    }
                }

                return categorizedVersions;
            }

            function sortVersions(categorizedVersions) {
                categorizedVersions.online.sort(sortByPublishedDateDesc);
                categorizedVersions.publishedOffline.sort(sortByPublishedDateDesc);
                categorizedVersions.nonPublishedOffline.sort(sortByPublishedDateDesc);
                categorizedVersions.publishedAlphaBeta.sort(sortByPublishedDateDesc);
                categorizedVersions.nonPublishedAlphaBeta.sort(sortByPublishedDateDesc);
            }

            function concatenateSortedArrays(categorizedVersions) {
                results.offline = categorizedVersions.publishedOffline.concat(categorizedVersions.nonPublishedOffline);
                results.alphabeta = categorizedVersions.publishedAlphaBeta.concat(categorizedVersions.nonPublishedAlphaBeta);
            }

            const categorizedVersions = categorizeReleaseVersions(packgeEntity.releaseVersions);
            sortVersions(categorizedVersions);
            concatenateSortedArrays(categorizedVersions);
            results.archive = categorizedVersions.archive;
            results.online = categorizedVersions.online;

            return results;
        };




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
