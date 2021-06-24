# Functional description

SSO Notifications enable Identity Providers / Institutes to reduce the number of steps users have to take during the 
login process and increase user-friendliness by providing the possibility to skip the WAYF (Where Are You From) / IdP 
selection screen. This is done by using a cookie, which is set after the user has visited a certain environment (for 
example an intranet page or the homepage of an Electronic Learning Environment).

The cookie only contains information regarding the Identity Provider to pre-select the correct Identity Provider during 
an authentication.

To show the advantage of SSO notification follow the two scenarios beneath. 

**Example without a SSO Notification:**

- The student/teacher logs in on the ELE / Identity Provider
- The student/teacher clicks on a link to learning material ('To service') 
- The student/teacher is forwarded to the WAYF (Where Are You From) screen. There's no information available about the 
Institute/IdP the user is coming from
- Search and select Identity Provider
- Log in step
- The user is now logged in and proceeds to learning material

**Example with a SSO Notification:**

- The student/teacher logs in on the ELE / Identity Provider, which sets a SSO Notification after login
- The student/teacher clicks on a link to learning material ('To service')
- In this scenario the WAYF screen will not be shown. The system reads the cookie that is used for SSO notification and 
automatically selects the correct Institute/IdP.
- Log in step
- The user is now logged in and proceeds to learning material

When an Identity Provider places a SSO notification cookie via an AJAX call or an iframe, this is considered to be a 
third party cookie. Third party cookies are placed by domains other than the domain of the website you are visiting 
(the Identity Provider). Some organizations use third party cookies to track users when they visit different websites. 
Since this type of third party cookies (also called tracking cookies) can infringe the privacy of users, their use is 
increasingly discouraged by browsers and often blocked.

This may prevent the SSO notification cookie from being placed. In such a case, the user can still log in, but will have 
to select his or her school on the WAYF screen. Since browsers have indicated to block third party cookies completely in 
the long term, placing an SSO notification is best done by means of a redirect. 

Placing a SSO Notification using the SSO Notification service therefore requires the originating party to visit the 
service. By providing a redirect url, the SSO Notification service will place the cookie in the browser of the user and 
redirects the browser to the provided redirect url. For security reasons, urls require to be whitelisted. 
