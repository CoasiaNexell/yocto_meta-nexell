DESCRIPTION = "Nexell NxVideoPlayer Application on Qt5"
HOMEPAGE = "http://www.nexell.co.kr"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit qmake5

PV = "1.0"
PR = "r0"

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/apps/QT/NxVideoPlayer"

SRC_URI = "file://${SRC_PATH}"

TARGET_CC_ARCH += "${LDFLAGS}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

do_myp() {
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

# Add Playler Tool Library
DEPENDS += "nx-drm-allocator nx-scaler nx-video-api libdrm"

DEPENDS += "qtbase-native qtmultimedia ffmpeg nx-mediaplayer"

do_compile() {
    export SDKTARGETSYSROOT=${SYSROOT_DESTDIR}
    oe_runmake CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC}" CFLAGS="${CFLAGS}"
}

do_install() {
    install -d ${D}${bindir}

    cd ${S}
    install -m 0755 ${B}/NxVideoPlayer/NxVideoPlayer ${D}${bindir}/
}

FILES_${PN} = "${bindir}"
