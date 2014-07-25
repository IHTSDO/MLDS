'use strict';

mldsApp.constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        staff: 'ROLE_STAFF',
        staffOrAdmin: ['ROLE_STAFF', 'ROLE_ADMIN'],
        user: 'ROLE_USER'
    });