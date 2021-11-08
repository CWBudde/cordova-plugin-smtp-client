/*global cordova, module*/

var smtpClient = {
    sendMail: function (mailSettings, successCB, errorCB) {
        cordova.exec(successCB, function (err) { errorCB(err); }, "SMTPClient", "cordovaSendMail", [mailSettings]);
    }
};

module.exports = smtpClient;
