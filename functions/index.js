const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

let admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendTeacherNotification = functions.database.ref('/NotificationTeacher/{userId}/{notificationId}').onWrite(event => {

    console.log("--- Getting Started ---");
    const valueObject = event.after.val();

    //get the from ID 
    const fromID = valueObject.fromID;
    console.log("fromId: ", fromID);

    //get the from accountType 
    const fromAccountType = valueObject.fromAccountType;
    console.log("fromAccountType: ", fromAccountType);

    //get the to ID 
    const toId = valueObject.toID;
    console.log("toId: ", toId);

    //get the to accountType 
    const toAccountType = valueObject.toAccountType;
    console.log("toAccountType: ", toAccountType);

    //get notificationType 
    const notificationType = valueObject.notificationType;
    console.log("notificationType: ", notificationType);

    //get objectName 
    var objectName = valueObject.objectName;
    if (objectName === null || objectName == undefined) {
        objectName = "";
    }
    console.log("objectName: ", objectName);

    //get object
    var object = valueObject.object;
    if (object === null || object == undefined) {
        object = "";
    }
    console.log("object: ", object);

    //get activityID 
    var activityID = valueObject.activityID;
    if (activityID === null || activityID == undefined) {
        activityID = "";
    }
    console.log("activityID: ", activityID);

    //get notificationImageURL 
    var notificationImageURL = valueObject.notificationImageURL;
    if (notificationImageURL === null || notificationImageURL == undefined) {
        notificationImageURL = "";
    }
    console.log("notificationImageURL: ", notificationImageURL);

    //get from name
    if (fromAccountType === "School") {
        return admin.database().ref("/School/" + fromID).once('value').then(snap => {
            const fromName = snap.child("schoolName").val();
            console.log("fromName: ", fromName);
            const fromProfilePhoto = snap.child("profilePhotoUrl").val();
            console.log("fromProfilePhoto: ", fromProfilePhoto);
            var payload;



            //get the token of the user receiving the message
            return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                const token = snap.child("token").val();
                console.log("toID: ", toId);
                console.log("token: ", token);

                //Build the message payload
                console.log("Construct the notification message")
                if (notificationType === "ClassPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Like") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " liked your class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Comment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " commented on your class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Event") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new school event for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Newsletter") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " published a new school newsletter for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "EClassroom") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - classroom for " + objectName + ". Click this notification to access the classroom before it expires.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ELibraryAssignment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - library assignment for " + objectName + ". Click this notification to study the material and take on the assignment.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ConnectionRequest") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "ConnectionRequestDeclined") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + objectName + "'s account has been declined by " + fromName,
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + fromName + " has been declined by them",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Disconnection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Connection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "NewResultPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new academic results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewBehaviouralPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new behavioural results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewAttendancePost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted a new attendance record for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                }

                console.log("Send message to device")
                return admin.messaging().sendToDevice(token, payload)
                    .then(function(response) {
                        console.log("token__ : ", token);
                        console.log("Successfully sent message:", JSON.stringify(response));
                        console.log(response.result[0].error);
                        return response.successCount;
                    })
                    .catch(function(error) {
                        console.log("Error sending message:", error);
                    });
            });
        });
    } else {
        return admin.database().ref("/Teacher/" + fromID).once('value').then(snap => {
            const fromName = snap.child("firstName").val() + " " + snap.child("lastName").val();
            console.log("fromName: ", fromName);
            const fromProfilePhoto = snap.child("profilePicURL").val();
            console.log("fromProfilePhoto: ", fromProfilePhoto);
            var payload;

            //get the token of the user receiving the message
            return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                const token = snap.child("token").val();
                console.log("token: ", token);

                //Build the message payload
                console.log("Construct the notification message")
                if (notificationType === "ClassPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Like") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " liked your class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Comment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " commented on your class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Event") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new school event for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Newsletter") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " published a new school newsletter for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "EClassroom") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - classroom for " + objectName + ". Click this notification to access the classroom before it expires.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ELibraryAssignment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - library assignment for " + objectName + ". Click this notification to study the material and take on the assignment.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ConnectionRequest") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "ConnectionRequestDeclined") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + objectName + "'s account has been declined by " + fromName,
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + fromName + " has been declined by them",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Disconnection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Connection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "NewResultPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new academic results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewBehaviouralPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new behavioural results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewAttendancePost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted a new attendance record for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                }

                console.log("Send message to device")
                return admin.messaging().sendToDevice(token, payload)
                    .then(function(response) {
                        console.log("token__ : ", token);
                        console.log("Successfully sent message:", JSON.stringify(response));
                        console.log(response.result[0].error);
                        return response.successCount;
                    })
                    .catch(function(error) {
                        console.log("Error sending message:", error);
                    });

            });
        });
    }
});

exports.sendParentNotification = functions.database.ref('/NotificationParent/{userId}/{notificationId}').onWrite(event => {

    console.log("--- Getting Started ---");
    const valueObject = event.after.val();

    //get the from ID 
    const fromID = valueObject.fromID;
    console.log("fromId: ", fromID);

    //get the from accountType 
    const fromAccountType = valueObject.fromAccountType;
    console.log("fromAccountType: ", fromAccountType);

    //get the to ID 
    const toId = valueObject.toID;
    console.log("toId: ", toId);

    //get the to accountType 
    const toAccountType = valueObject.toAccountType;
    console.log("toAccountType: ", toAccountType);

    //get notificationType 
    const notificationType = valueObject.notificationType;
    console.log("notificationType: ", notificationType);

    //get objectName 
    var objectName = valueObject.objectName;
    if (objectName === null || objectName == undefined) {
        objectName = "";
    }
    console.log("objectName: ", objectName);

    //get object
    var object = valueObject.object;
    if (object === null || object == undefined) {
        object = "";
    }
    console.log("object: ", object);

    //get activityID 
    var activityID = valueObject.activityID;
    if (activityID === null || activityID == undefined) {
        activityID = "";
    }
    console.log("activityID: ", activityID);

    //get notificationImageURL 
    var notificationImageURL = valueObject.notificationImageURL;
    if (notificationImageURL === null || notificationImageURL == undefined) {
        notificationImageURL = "";
    }

    console.log("notificationImageURL: ", notificationImageURL);

    //get from name
    if (fromAccountType === "School") {
        return admin.database().ref("/School/" + fromID).once('value').then(snap => {
            const fromName = snap.child("schoolName").val();
            console.log("fromName: ", fromName);
            const fromProfilePhoto = snap.child("profilePhotoUrl").val();
            console.log("fromProfilePhoto: ", fromProfilePhoto);
            var payload;

            //get the token of the user receiving the message
            return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                const token = snap.child("token").val();
                console.log("toID: ", toId);
                console.log("token: ", token);

                //Build the message payload
                console.log("Construct the notification message")
                if (notificationType === "ClassPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Like") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " liked your class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Comment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " commented on your class post for " + object,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Event") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new school event for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Newsletter") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " published a new school newsletter for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "EClassroom") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - classroom for " + objectName + ". Click this notification to access the classroom before it expires.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ELibraryAssignment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - library assignment for " + objectName + ". Click this notification to study the material and take on the assignment.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ConnectionRequest") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "ConnectionRequestDeclined") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + objectName + "'s account has been declined by " + fromName,
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + fromName + " has been declined by them",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Disconnection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Connection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "NewResultPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new academic results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewBehaviouralPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new behavioural results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewAttendancePost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted a new attendance record for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                }

                console.log("Send message to device")
                return admin.messaging().sendToDevice(token, payload)
                    .then(function(response) {
                        console.log("token__ : ", token);
                        console.log("Successfully sent message:", JSON.stringify(response));
                        console.log(response.result[0].error);
                        return response.successCount;
                    })
                    .catch(function(error) {
                        console.log("Error sending message:", error);
                    });
            });
        });
    } else {
        return admin.database().ref("/Teacher/" + fromID).once('value').then(snap => {
            const fromName = snap.child("firstName").val() + " " + snap.child("lastName").val();
            console.log("fromName: ", fromName);
            const fromProfilePhoto = snap.child("profilePicURL").val();
            console.log("fromProfilePhoto: ", fromProfilePhoto);
            var payload;

            //get the token of the user receiving the message
            return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                const token = snap.child("token").val();
                console.log("token: ", token);

                //Build the message payload
                console.log("Construct the notification message")
                if (notificationType === "ClassPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Like") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " liked your class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Comment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " commented on your class post for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Event") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " created a new school event for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "Newsletter") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " published a new school newsletter for you",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "EClassroom") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - classroom for " + objectName + ". Click this notification to access the classroom before it expires.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ELibraryAssignment") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has created a new e - library assignment for " + objectName + ". Click this notification to study the material and take on the assignment.",
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "ConnectionRequest") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has requested to connect to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "ConnectionRequestDeclined") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + objectName + "'s account has been declined by " + fromName,
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: "Your request to connect to " + fromName + " has been declined by them",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Disconnection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has disconnected from your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "Connection") {
                    if (toAccountType === "Parent") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to " + objectName + "'s account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    } else if (toAccountType === "Teacher") {
                        payload = {
                            data: {
                                data_type: notificationType,
                                account_type: toAccountType,
                                fromAccountType: fromAccountType,
                                fromID: fromID,
                                fromName: fromName,
                                object: object,
                                objectName: objectName,
                                notificationImageURL: notificationImageURL,
                                message: fromName + " has connected to your account",
                                activityID: activityID,
                            },
                        };
                        console.log("payload: ", payload)
                    }
                } else if (notificationType === "NewResultPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new academic results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewBehaviouralPost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted new behavioural results for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                } else if (notificationType === "NewAttendancePost") {
                    payload = {
                        data: {
                            data_type: notificationType,
                            account_type: toAccountType,
                            fromAccountType: fromAccountType,
                            fromID: fromID,
                            fromName: fromName,
                            object: object,
                            objectName: objectName,
                            notificationImageURL: notificationImageURL,
                            message: fromName + " has posted a new attendance record for " + objectName,
                            activityID: activityID,
                        },
                    };
                    console.log("payload: ", payload)
                }

                console.log("Send message to device")
                return admin.messaging().sendToDevice(token, payload)
                    .then(function(response) {
                        console.log("token__ : ", token);
                        console.log("Successfully sent message:", JSON.stringify(response));
                        console.log(response.result[0].error);
                        return response.successCount;
                    })
                    .catch(function(error) {
                        console.log("Error sending message:", error);
                    });

            });
        });
    }
});

exports.sendMessageNotification = functions.database.ref('/Messages Recent/{userId}/{otherPartyId}').onWrite(event => {

    console.log("--- Getting Started ---");
    const valueObject = event.after.val();

    //get the isMine
    const isMine = valueObject.mine;
    console.log("mine: ", isMine);

    if (isMine === false) {
        //get the from ID 
        const fromID = valueObject.senderID;
        console.log("fromId: ", fromID);

        //get the to ID 
        const toId = valueObject.receiverID;
        console.log("toId: ", toId);

        //get notificationType 
        const message = valueObject.message;
        console.log("message: ", message);

        //get notificationType 
        const notificationType = "Message";
        console.log("notificationType: ", notificationType);

        //get notificationImageURL 
        var notificationImageURL = valueObject.fileURL;
        if (notificationImageURL === null || notificationImageURL == undefined) {
            notificationImageURL = "";
        }
        console.log("notificationImageURL: ", notificationImageURL);

        return admin.database().ref("/School/" + fromID).once('value').then(snap => {
            if (snap.exists()) {
                const fromName = snap.child("schoolName").val();
                console.log("fromName: ", fromName);
                var fromProfilePhoto = snap.child("profilePhotoUrl").val();
                if (fromProfilePhoto === null) {
                    fromProfilePhoto = "";
                }
                console.log("fromProfilePhoto: ", fromProfilePhoto);
                var payload;

                //get the token of the user receiving the message
                return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                    const token = snap.child("token").val();
                    console.log("toID: ", toId);
                    console.log("token: ", token);

                    //Build the message payload
                    console.log("Construct the notification message")
                    payload = {
                        data: {
                            data_type: notificationType,
                            fromID: fromID,
                            fromName: fromName,
                            notificationImageURL: fromProfilePhoto,
                            message: fromName + ": " + message,
                        },
                    };
                    console.log("payload: ", payload)

                    console.log("Send message to device")
                    return admin.messaging().sendToDevice(token, payload)
                        .then(function(response) {
                            console.log("token__ : ", token);
                            console.log("Successfully sent message:", JSON.stringify(response));
                            console.log(response.result[0].error);
                            return response.successCount;
                        })
                        .catch(function(error) {
                            console.log("Error sending message:", error);
                        });
                });
            } else {
                return admin.database().ref("/Teacher/" + fromID).once('value').then(snap => {
                    if (snap.exists()) {
                        const fromName = snap.child("firstName").val() + " " + snap.child("lastName").val();
                        console.log("fromName: ", fromName);
                        var fromProfilePhoto = snap.child("profilePicURL").val();
                        if (fromProfilePhoto === null) {
                            fromProfilePhoto = "";
                        }
                        console.log("fromProfilePhoto: ", fromProfilePhoto);
                        var payload;

                        //get the token of the user receiving the message
                        return admin.database().ref("/UserRoles/" + toId).once('value').then(snap => {

                            const token = snap.child("token").val();
                            console.log("token: ", token);

                            //Build the message payload
                            console.log("Construct the notification message")
                            payload = {
                                data: {
                                    data_type: notificationType,
                                    fromID: fromID,
                                    fromName: fromName,
                                    notificationImageURL: fromProfilePhoto,
                                    message: fromName + ": " + message,
                                },
                            };
                            console.log("payload: ", payload)

                            console.log("Send message to device")
                            return admin.messaging().sendToDevice(token, payload)
                                .then(function(response) {
                                    console.log("token__ : ", token);
                                    console.log("Successfully sent message:", JSON.stringify(response));
                                    console.log(response.result[0].error);
                                    return response.successCount;
                                })
                                .catch(function(error) {
                                    console.log("Error sending message:", error);
                                });

                        });
                    }
                });
            }
        });
    }
});