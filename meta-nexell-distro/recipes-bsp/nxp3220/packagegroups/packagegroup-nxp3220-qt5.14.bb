#
# Copyright (C) 2007 OpenedHand Ltd.
#
# derived from oe-core: meta/recipes-core/packagegroups/packagegroup-core-boot.bb
# add to image:
# IMAGE_INSTALL_append = "packagegroup-<name>"

SUMMARY = "QT 5.14 requirements"
DESCRIPTION = "The Qt 5.14 packages set"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGE_QT5_14_ESSENTIAL = " \
    qtscript \
    liberation-fonts \
    cinematicexperience \
    qtdeclarative \
    qtdeclarative-qmlplugins \
    qtdeclarative-tools \
    qtlocation-plugins \
    qtlocation-qmlplugins \
    qtmultimedia-plugins \
    qtmultimedia-qmlplugins \
    qt3d-qmlplugins \
    qtquickcontrols-qmlplugins \
    qtquickcontrols2-qmlplugins \
    qtgraphicaleffects-qmlplugins \
"

PACKAGE_QT5_14_EXAMPLES = " \
    qtbase-examples \
    qtdeclarative-examples \
"

RDEPENDS_${PN} = " \
    ${PACKAGE_QT5_14_ESSENTIAL}  \
    ${@bb.utils.contains('DISTRO_FEATURES', 'qt-examples', '${PACKAGE_QT5_14_EXAMPLES}', '', d)} \
"
