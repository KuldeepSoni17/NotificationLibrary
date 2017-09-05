# NotificationLibrary

This library is for receiving notification data without setting up FCM and Node.js server.

Disadvantage of this Library :- It generates a persistent notification which is not swipable so only by stopping the service notification disappear.

INCLUDE THIS IN MANIFEST :-
 <service
            android:name=".NotiService"
            android:enabled="true"
            android:exported="true">
</service>
