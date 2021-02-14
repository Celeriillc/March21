package com.celerii.celerii.helperClasses;

public class FirebaseErrorMessages {
    public static String getErrorMessage(int errorCode) {
        String message = "We're sorry, an unknown error has occurred, please try again";
        if (errorCode == -2) {
            message = "We're sorry, a server indicated that this operation could not be completed successfully, please try again later";
        } else if (errorCode == -3) {
            message = "We're sorry, you do not have the permission to continue this operation, please try again when you've made the appropriate child/school connection";
        } else if (errorCode == -4) {
            message = "We're sorry, this operation could not be completed successfully because your network disconnected abruptly, please check your connection and try again";
        } else if (errorCode == -6) {
            message = "We're sorry, your authentication token has expired, please logout and re-authenticate to continue";
        } else if (errorCode == -7) {
            message = "We're sorry, your authentication token is invalid, please logout and re-authenticate to continue";
        } else if (errorCode == -8) {
            message = "We're sorry, the server has detected too many tries for this request, please wait a while then try again";
        } else if (errorCode == -9) {
            message = "We're sorry, this request was overridden by a subsequent one, please try again";
        } else if (errorCode == -10) {
            message = "We're sorry, this service is currently unavailable, please try again";
        } else if (errorCode == -11) {
            message = "We're sorry, an exception occurred in the user code, please try again";
        } else if (errorCode == -24) {
            message = "We're sorry, this operation could not be completed due to a network error, please try again";
        } else if (errorCode == -25) {
            message = "We're sorry, this operation was cancelled by your device, if you wish to continue, please try again";
        } else if (errorCode == -999) {
            message = "We're sorry, an unknown error has occurred, please try again";
        }
        return message;
    }
}
