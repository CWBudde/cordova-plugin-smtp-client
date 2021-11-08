/*global cordova, module*/

var smtpClient = {
    sendMail: function (mailSettings, successCB, errorCB) {
        cordova.exec(successCB, function (err) { errorCB(err); }, "SMTPClient", "cordovaSendMail", [JSON.stringify(mailSettings)]);
    }
};

module.exports = smtpClient;
