' use strict';
/**
 * Event registry.
 */
angular.module('MLDS')
    .value('Events', {
        newUser: 'ihtsdo.mlds.newUser',
        registrationError: 'ihtsdo.mlds.registrationError'
    });
