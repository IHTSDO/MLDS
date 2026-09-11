window.MLDSAuthPlugin = function () {

    return {

        wrapComponents: {

            authorizeBtn: function (Original) {

                return function (props) {

                    const originalAuthorize =
                        props.onClick;

                    function loginWithIMS() {

                        const width = 600;
                        const height = 700;

                        const left =
                            (window.screen.width - width) / 2;

                        const top =
                            (window.screen.height - height) / 2;

                        window.open(
                            "/swagger-login",
                            "mlds-ims-login",
                            [
                                "width=" + width,
                                "height=" + height,
                                "left=" + left,
                                "top=" + top,
                                "resizable=yes",
                                "scrollbars=yes"
                            ].join(",")
                        );
                    }

                    return React.createElement(
                        "button",
                        {
                            className:
                                "btn authorize unlocked",
                            onClick: loginWithIMS
                        },
                        "Authorize"
                    );
                };
            }
        }
    };
};
