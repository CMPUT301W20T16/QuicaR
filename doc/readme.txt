
README

Database Part

BUGS

1. notification goto intent is always MainActivity for now

2. The check notification method is not very accurate as the rider can have multiple requests where we need to decide whether or not notify the rider or just the currently active request. There is a problem that rider can have multiple active requests but it is now not supported by the app.

3. not able to return to the previous activity if the user quit the app and open it again.

4. there is an extra attribute that is not used


TO BE IMPROVED

1. store every user’s current location, only updated when using the app, and can be retrieved both as a rider or driver to achieve real-time locating of rider and driver in a request

2. add time attribute to request so that a request can be set for future ride

3. open request not accepted after a certain duration from the specified request time will be canceled automatically

4. driver/user details on request cannot be updated yet.

5.functionality of rating for drivers still hasn’t implemented.

6.  After picked rider up, it will jump back to driver register activity.

Login page 
Bugs:
When multiple users login/sign up at the same time, users may wait for database responses for a long time.
When the user uses email as a username to log in/sign up, it might result in an error when login.
Sometimes maybe login timeout
To be improved:
	1. Improve the design of the login/register page
	2. Let the user chooses to remember the password so that the user doesn't need to fill all the information again.
	3. (If the time available) sign in by using the fingerprint.

Profile&Wallet
Bugs:
In the Wallet transfer page, after confirmed, maybe some problem with activity swap(For all users)
Driver need to re-login to see update balance
Sometimes the new user may have the null wallet to crash program

Improved:
1.update profile and wallet still need to be better limited for user input. 
2. Unit Tests and Activities Tests need to be more specific
