/*global cordova, module*/

var smtpClient = {
    sendMail: function (mailSettings, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SMTPClient", "cordovaSendMail", [ JSON.stringify(mailSettings) ]);
    }
};

module.exports = smtpClient;
