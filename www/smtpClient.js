/*global cordova, module*/

var smtpClient = {
    sendMail: function (mailSettings, successCB, errorCB) {
        cordova.exec(successCB, function (err) { errorCB(err); }, "SMTPClient", "cordovaSendMail", [JSON.stringify(mailSettings)]);
    },
    testConnection: function (smtpSettings, successCB, errorCB) {
        cordova.exec(successCB, function (err) { errorCB(err); }, "SMTPClient", "cordovaTestConnection", [JSON.stringify(smtpSettings)]);
    }
};

module.exports = smtpClient;
