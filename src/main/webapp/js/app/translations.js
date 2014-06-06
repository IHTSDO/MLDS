'use strict';

angular.module('MLDS').config(
      ['$translateProvider', function($translateProvider) {

        $translateProvider.translations('en', {

          'TITLE': 'IHTSDO - MLDS',
          'FOOTER_COPY': 'All rights reserved.',

          'HOME': 'Home',
          'DASHBOARD': 'Dashboard',
          'ABOUT': 'About',
          'CONTACT': 'Contact',
          'USERS': 'Users',
          'SELF_TEST': 'Self test',
          'ADMIN': 'Admin',
          'CONFIGURATION': 'Configuration',
          'LOG': 'Log',
          'SIGN_OUT': 'Sign out',
          'SIGN_IN': 'Sign in',

          'SIGN_IN_USERNAME_PLACEHOLDER': 'Username',
          'SIGN_IN_PASSWORD_PLACEHOLDER': 'Password',

          'WELCOME_HEADING': 'Light/Speed',
          'WELCOME_SUBHEADING': 'Kessel Run. Twelve parsecs. Just sayin\'.',
          'WELCOME_CONTENT': 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce et lectus nibh. Suspendisse ut interdum nulla. Integer augue enim, accumsan id dignissim sed, venenatis et nisl.',
          'WELCOME_LEARN_MORE': 'Learn more',
          'WELCOME_RESPONSIVE_HEADING': 'Deliver Faster',
          'WELCOME_MOBILE_HEADING': 'Mobile',
          'WELCOME_PROVEN_HEADING': 'Robust',
          'WELCOME_VIEW_DETAILS_BTN': 'View Details',

          'DASHBOARD_COMPANIES_TAB': 'Companies',
          'DASHBOARD_COUNTRIES_TAB': 'Countries',
          'DASHBOARD_USERS_TAB': 'Users',
          'DASHBOARD_ROLES_TAB': 'Roles',

          'COMPANY_LIST_NUMBER_OF_CUSTOMERS_LABEL': 'Number of Customers',

          'COMPANY_DETAIL_CONTACT_LABEL': 'Contact',
          'COMPANY_DETAIL_EMAIL_LABEL': 'Email',
          'COMPANY_DETAIL_SECURITY_CODE_LABEL': 'Security Code',
          'COMPANY_DETAIL_EDIT_BTN': 'Edit',
          'COMPANY_DETAIL_CUSTOMER_LIST_BTN': 'Customer List',
          'COMPANY_DETAIL_SEND_EMAIL_BTN': 'Send Email',

          'ABOUT_HEADING': 'About',
          'CONTACT_HEADING': 'Contact',
          
          'USER_NAME_LABEL': 'User Name',
          'FULL_NAME_LABEL': 'Full Name',
          'EMAIL_LABEL': 'Email',
          'ENABLED_LABEL': 'Enabled',
          'PASSWORD_LABEL': 'Password',
          'CONFIRM_PASSWORD_LABEL': 'Confirm Password',

          'PASSWORD_EXPIRY_IN_DAYS': 'Maximum password age (in days)',
          
          'SAVE_BUTTON_TEXT': 'Save',
          'CANCEL_BUTTON_TEXT': 'Cancel',
          
          'ERROR_MESSAGE_REQUIRED': 'This is a mandatory field. Please provide a value.',
          'ERROR_MESSAGE_EMAIL_FORMAT': 'This is not a valid email address.',
          'ERROR_MESSAGE_PASSWORD_CONFIRM': 'The two password fields do not match. Please try again.'
        });

        $translateProvider.translations('fr', {

          'TITLE': 'IHTSDO - MLDS en français',
          'FOOTER_COPY': 'FR_All rights reserved.',

          'HOME': 'FR_Home',
          'DASHBOARD': 'FR_Dashboard',
          'ABOUT': 'FR_About',
          'CONTACT': 'FR_Contact',
          'USERS': 'FR_Users',
          'SELF_TEST': 'FR_Self_test',
          'ADMIN': 'FR_Admin',
          'LOG': 'FR_Log',
          'CONFIGURATION': 'FR_Configuration',
          'SIGN_OUT': 'FR_Sign out',
          'SIGN_IN': 'FR_Sign in',

          'SIGN_IN_USERNAME_PLACEHOLDER': 'FR_Username',
          'SIGN_IN_PASSWORD_PLACEHOLDER': 'FR_Password',

          'WELCOME_HEADING': 'Light/Speed',
          'WELCOME_SUBHEADING': 'FR_Better Apps Faster',
          'WELCOME_CONTENT': 'FR_Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce et lectus nibh. Suspendisse ut interdum nulla. Integer augue enim, accumsan id dignissim sed, venenatis et nisl.',
          'WELCOME_LEARN_MORE': 'FR_Learn more',
          'WELCOME_RESPONSIVE_HEADING': 'FR_Deliver Faster',
          'WELCOME_MOBILE_HEADING': 'FR_Mobile',
          'WELCOME_PROVEN_HEADING': 'FR_Robust',
          'WELCOME_VIEW_DETAILS_BTN': 'FR_View Details',

          'DASHBOARD_COMPANIES_TAB': 'FR_Companies',
          'DASHBOARD_COUNTRIES_TAB': 'FR_Countries',
          'DASHBOARD_USERS_TAB': 'FR_Users',
          'DASHBOARD_ROLES_TAB': 'FR_Roles',

          'COMPANY_LIST_NUMBER_OF_CUSTOMERS_LABEL': 'FR_Number of Customers',

          'COMPANY_DETAIL_CONTACT_LABEL': 'FR_Contact',
          'COMPANY_DETAIL_EMAIL_LABEL': 'FR_Email',
          'COMPANY_DETAIL_SECURITY_CODE_LABEL': 'FR_Security Code',
          'COMPANY_DETAIL_EDIT_BTN': 'FR_Edit',
          'COMPANY_DETAIL_CUSTOMER_LIST_BTN': 'FR_Customer List',
          'COMPANY_DETAIL_SEND_EMAIL_BTN': 'FR_Send Email',

          'ABOUT_HEADING': 'FR_About',
          'CONTACT_HEADING': 'FR_Contact',
          
          'USER_NAME_LABEL': 'FR_User Name',
          'FULL_NAME_LABEL': 'FR_Full Name',
          'EMAIL_LABEL': 'FR_Email',
          'ENABLED_LABEL': 'FR_Enabled',
          'PASSWORD_LABEL': 'FR_Password',
          'CONFIRM_PASSWORD_LABEL': 'FR_Confirm Password',

          'PASSWORD_EXPIRY_IN_DAYS': 'Le maximum password age (in days)',
          
          'SAVE_BUTTON_TEXT': 'FR_Save',
          'CANCEL_BUTTON_TEXT': 'FR_Cancel',
          
          'ERROR_MESSAGE_REQUIRED': 'FR_This is a mandatory field. Please provide a value.',
          'ERROR_MESSAGE_EMAIL_FORMAT': 'FR_This is not a valid email address.',
          'ERROR_MESSAGE_PASSWORD_CONFIRM': 'FR_The two password fields do not match. Please try again.'

        });

        $translateProvider.preferredLanguage('en');

      }]
    ).config(
      ['$provide', '$localeProvider', function($provide, $localeProvider) {

        //RV: the backend services require this language to specified this way.
        //It seems this doesn't take into account the user's locale.
        var requestLang = 'ENGLISH',
            langCode = new RegExp(/^([a-z]{2})[-]([a-z]){2}|([a-z]{2})/i).exec($localeProvider.$get().id);

        if(langCode && langCode[1] === 'fr') {
          requestLang = 'FRENCH';
        }

        $provide.value('requestLang', requestLang);
        $provide.value('langCode', langCode[1]);

      }]
    ).run(
      ['$translate','langCode', function($translate, langCode) {

        $translate.use('fr');

      }]
    );