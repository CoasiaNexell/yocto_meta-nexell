#
# Copyright (C) 2007 OpenedHand Ltd.
#
# derived from oe-core: meta/recipes-core/packagegroups/packagegroup-core-boot.bb

SUMMARY = "QT requirements"
DESCRIPTION = "The qt set of packages required by NEXELL"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

GSTREAMER10 = " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
"

QT_WAYLAND = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'qtwayland qtwayland-plugins', '', d)} \
"

QT5_ESSENTIAL_IMAGES = " \
    qtbase \
    qtbase-plugins \
    qtlocation \
    qtlocation-plugins \
    qtlocation-qmlplugins \
    qtdeclarative-qmlplugins \
    qtmultimedia-plugins \
    qtmultimedia-qmlplugins \
    qt3d-qmlplugins \
    qtgraphicaleffects-qmlplugins \
"

QT5_14_ESSENTIAL_IMAGES = " \
    qtscript \
    liberation-fonts \
    qtbase-examples \
    cinematicexperience \
       qtdeclarative \
       qtdeclarative-qmlplugins \
       qtdeclarative-tools \
    qtlocation-plugins \
    qtlocation-qmlplugins \
    qtmultimedia-plugins \
    qtmultimedia-qmlplugins \
    qt3d-qmlplugins \
    qtwebkit-qmlplugins \
    qtquickcontrols-qmlplugins \
    qtquickcontrols2-qmlplugins \
    qtgraphicaleffects-qmlplugins \
"


QT_ALSA_LIB_IMAGE_INSTALL = " \
"

QT_ALSA_UTILS_IMAGE_INSTALL = " \
    alsa-utils \
"

QT_V4L_UTILS_IMAGE_INSTALL = " \
    v4l-utils \
"

QT_WEBKIT = " \
    qtwebkit \
    qtwebkit-qmlplugins \
    qtsvg \
    qtsvg-plugins \
"

MULTIMEDIA_IMAGE_INSTALL = " \
    ffmpeg \
    fswebcam \
"

NEXELL_LIBS = " \
    gst-plugins-camera \
    gst-plugins-renderer \
    gst-plugins-scaler \
    gst-plugins-video-dec \
    gst-plugins-video-sink \
    libdrm \
    nx-drm-allocator \
    nx-gst-meta \
    nx-renderer \
    nx-scaler \
    nx-v4l2 \
    nx-video-api \
    nx-uds \
    nx-config \
    nx-gl-tools \
"

NEXELL_APPS = "\
    nx-video-api-test \
"

UTILS_INSTALL = " \
    glibc-utils \
    localedef \
    glibc-localedata-i18n \
    glibc-gconv-ibm850 \
    glibc-gconv-cp1252 \
    glibc-gconv-iso8859-1 \
    glibc-gconv-iso8859-15 \
    glibc-gconv-euc-kr \
    lsof \
"

RDEPENDS_${PN} = " \
    nexell-drm-mali-qt \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nexell-daudio-ref', '', 'weston-conf', d)} \
    ${GSTREAMER10} \
    ${QT5_ESSENTIAL_IMAGES} \
    ${QT_WAYLAND} \
    ${QT5_14_ESSENTIAL_IMAGES} \
    ${NEXELL_LIBS} \
    ${NEXELL_APPS} \
    ${UTILS_INSTALL} \
    ${QT_ALSA_UTILS_IMAGE_INSTALL} \
    ${QT_V4L_UTILS_IMAGE_INSTALL} \
"
