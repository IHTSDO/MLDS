(function () {

    window.MLDS_SWAGGER_AUTHENTICATED = false;

    window.addEventListener("message", function (event) {

        if (event.origin !== window.location.origin) {
            return;
        }

        if (
            event.data &&
            event.data.type === "MLDS_AUTHENTICATED"
        ) {
            window.MLDS_SWAGGER_AUTHENTICATED = true;

            if (window.ui) {
                window.ui.preauthorizeApiKey(
                    "mldsSession",
                    "authenticated"
                );
            }
        }
    });

})();
