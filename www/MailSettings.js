var MailSettings = (function () {
    function MailSettings(mailSettings) {
        if (mailSettings === void 0) { mailSettings = undefined; }
        if (mailSettings != undefined) {
            this.emailFrom = mailSettings.emailFrom;
            this.emailTo = mailSettings.emailTo;
            this.smtp = mailSettings.smtp;
            this.port = mailSettings.port;
            this.sport = mailSettings.sport;
            this.auth = mailSettings.auth;
            this.ssl = mailSettings.ssl;
            this.smtpUserName = mailSettings.smtpUserName;
            this.smtpPassword = mailSettings.smtpPassword;
            this.attachments = mailSettings.attachments;
            this.subject = mailSettings.subject;
            this.textBody = mailSettings.textBody;
        }
    }
    return MailSettings;
})();