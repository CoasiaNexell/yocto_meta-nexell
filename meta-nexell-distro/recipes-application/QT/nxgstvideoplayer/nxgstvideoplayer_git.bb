DESCRIPTION = "Nexell NxGstVideoPlayer Application on Qt5"
HOMEPAGE = "http://www.nexell.co.kr"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit qmake5

PV = "1.0"
PR = "r0"

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/apps/QT/NxGstVideoPlayer"

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
DEPENDS += "qtbase-native qtmultimedia"
DEPENDS += "nx-gst-meta fdfdfdnx-renderer nx-video-api nx-drm-allocator"

do_compile() {
    export SDKTARGETSYSROOT=${SYSROOT_DESTDIR}
    oe_runmake CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC}" CFLAGS="${CFLAGS}" clean
    oe_runmake \
        CROSS_COMPILE=${TARGET_PREFIX} \
        INCLUDES="-I${STAGING_INCDIR}" \
        LDFLAGS="-L{STAGING_LIBDIR}" CC="$CC"
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${includedir}

    cd ${S}
    # header files
    install -m 0644 ${S}/libnxgstvplayer/include/NX_GstIface.h ${D}${includedir}/NX_GstIface.h
    install -m 0644 ${S}/libnxgstvplayer/include/NX_GstTypes.h ${D}${includedir}/NX_GstTypes.h
    install -m 0644 ${S}/libnxgstvplayer/include/NX_Log.h ${D}${includedir}/NX_Log.h

    # libraries
    install -m 0755 ${S}/libnxgstvplayer/lib/32bit/libnxgstvplayer.so.1.0.0 ${D}${libdir}/

    # app binary
    cd ${B}
    install -m 0755 ${B}/NxGstVideoPlayer/NxGstVideoPlayer ${D}${bindir}/

    cd ${D}${libdir}
    ln -sf libnxgstvplayer.so.1.0.0 libnxgstvplayer.so.1
    ln -sf libnxgstvplayer.so.1 libnxgstvplayer.so
}

INSANE_SKIP_${PN} = "installed-vs-shipped"
FILES_${PN} = "${libdir} ${includedir} {bindir}"
