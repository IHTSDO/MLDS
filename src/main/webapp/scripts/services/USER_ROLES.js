'use strict';

mldsApp.constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        staff: 'ROLE_STAFF',
        member: 'ROLE_MEMBER',
        staffOrAdmin: ['ROLE_STAFF', 'ROLE_ADMIN'],
        memberOrStaffOrAdmin: ['ROLE_MEMBER', 'ROLE_STAFF', 'ROLE_ADMIN'],
        user: 'ROLE_USER'
    });