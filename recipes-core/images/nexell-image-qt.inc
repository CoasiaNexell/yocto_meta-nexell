DESCRIPTION = "Extend core-image by adding Qt5"
LICENSE = "GPLv2"

inherit core-image populate_sdk_qt5
require recipes-graphics/images/core-image-weston.bb

SPLASH = "psplash-nexell"

IMAGE_FEATURES += "package-management ssh-server-dropbear splash"

IMAGE_INSTALL += " \
        kernel-modules \
        "

WAYLAND_IMAGE_INSTALL = "${@base_contains('DISTRO_FEATURES', 'wayland', \
    'qtwayland \
     qtwayland-plugins', '', d)}"

QT_IMAGE_INSTALL_common = " \
     qtbase \
     qtbase-plugins \
     qtbase-fonts \
     qtbase-examples \
     qtdeclarative \
     qtdeclarative-plugins \
     qtdeclarative-qmlplugins \
     qtdeclarative-tools \
     qtmultimedia \
     qtmultimedia-plugins \
     qtmultimedia-qmlplugins \
     qtquick1 \
     qtquickcontrols \
     qtquickcontrols2 \
     qtgraphicaleffects \
     ${WAYLAND_IMAGE_INSTALL} \
     glmark2 \
     "

QT_IMAGE_INSTALL_qtwebkit = " \
     qtwebkit \
     qtwebkit-qmlplugins \
     qtsvg \
     qtsvg-plugins \
     "

QT_IMAGE_INSTALL_qtwebengine = " \
     qtwebengine \
     "
     
QT_IMAGE_INSTALL_qt3d = "qt3d qt3d-qmlplugins qt3d-examples cinematicexperience"

EXAMPLE_IMAGE_INSTALL = " \
     touchpoints \
     qkeytest \
     qcolorcheck \
     "
TOUCH_IMAGE_INSTALL = " \
     tslib \
     tslib-conf \
     tslib-tests \
     tslib-calibrate \
     tslib-nexell \
     "

IMAGE_INSTALL += " \
     ${QT_IMAGE_INSTALL_common} \
     ${QT_IMAGE_INSTALL_qt3d} \
     ${QT_IMAGE_INSTALL_qtwebkit} \
	 ${WAYLAND_IMAGE_INSTALL} \
     ${QT_IMAGE_INSTALL_qtwebengine} \
     ${EXAMPLE_IMAGE_INSTALL} \
     ${TOUCH_IMAGE_INSTALL} \
     "