
#User Flows

## Pages

##### Landing page
- Content: orienting copy about IHTSDO, SNOMED, licensing, etc.
- Primary action: 
    - New User --> [New User Registration page](#new-user-registration-page)
- Secondary actions:
    - User login --> [User Dashboard](#user-dashboard) or [Login page](#login-page)

##### New User Registration page
- Primary Action: Create user by form submit  --> [Email verification page](#email-verification-page)
- User fields
    - Name (or First and Last?)
    - Email
    - password
    - password confirm
    - *Accept* Website Terms 

##### Email verification page
- Content: Thank the user, and direct them to check their email.
- Behaviour: Send email containing verification link
- Offline: User *clicks* link to vaildate Email, *Returns* to [Affiliate Registration Page] via Login Page

##### Affiliate Registration Page
- Long form with sub-sections
    - User Infomation
    - Organization Information
    - *Accept* SNOWMED License
    - **Submit Registration** --> [Pending Registration Page](#pending-registration-page)

##### Pending Registration Page
- Content: Thank user, explain and outline timeframe of approval process.
- Terminal page.

##### Login Page
   
##### User Dashboard
- Behaviour: If the user is not yet attached to an approved affiliate, we redirect to [Pending Registration Page](#pending-registration-page)
- Actions:
    - Enter usage information (if agent?)
    - Download standard
    - Edit Profile

##### Admin Dashboard
- Actions: review and approve registration applications

###Misc Pages
##### Forgot username/password
##### FAQ???  About? Copies of the licenses, T&C?

-------

##Email Templates
* Validate account
* Forgot username & password
* Registration Accepted - Welcome 
* Registration denied

-------

## Flows
###First Time User
User will land on a welcome page
* User registers for new account
* Form is submitted for approval
* Email sent to user to validate email 
* User clicks activation link in email 

*  **Login page**
Returns to site as logged in user

* **Registration**
* Collect remaining user info 
* Collect Organization Organization affiliate registrations  

### Returning User

* [Login page](#login-page)
* Registration Dashboard
* Collect remaining user info 
* Collect sublicense usage information
* Organization Organization affiliate registrations  
* Present website terms and conditions
* **user accepts**
* **user does not accept**
* Present SNOMED CT License terms & conditions
*  **Usage Reporting**
* User does not have information to continue
* User continues with usage report

## Applicant Approval




