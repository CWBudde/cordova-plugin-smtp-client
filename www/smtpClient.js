/*global cordova, module*/

module.exports = {
    sendMail: function (mailSettings, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SMTPClient", "cordovaSendMail", [ JSON.stringify(mailSettings) ]);
    }
};
