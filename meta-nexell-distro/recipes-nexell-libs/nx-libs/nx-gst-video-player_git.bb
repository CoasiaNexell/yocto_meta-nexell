DESCRIPTION = "Nexell Gstreamer Movie Player"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=1c29f522bb1f7e5686af40aff4032b50"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/library/nx-gst-video-player"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad glib-2.0 gdk-pixbuf nx-renderer nx-video-api"

inherit autotools pkgconfig

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

#Nexell code : for /usr/include search QA issue: compile-host-path QA issue avoid
EXTRA_OECONF = " \
     '--prefix=${STAGING_DIR_HOST}' \
     "

EXTRA_OEMAKE = " \
    'libnxgstvplayer_la_CPPFLAGS = \
        $(WARN_CFLAGS) \
        $(GST_CFLAGS) \
        -I./include \
        -I${STAGING_INCDIR} \
        -I${STAGING_INCDIR}/gdk-pixbuf-2.0' \
    'libnxgstvplayer_la_LDFLAGS = -L${STAGING_LIBDIR} -version-number 0:9:0' \
    'libnxgstvplayer_la_LDFLAGS += \
        $(GST_LIBS) \
        -lgstmpegts-1.0 \
        -lgdk_pixbuf-2.0' \
    "

do_configure() {
     cd ${S}
     NOCONFIGURE=true ./autogen.sh
     oe_runconf
}

do_compile() {
     cd ${S}
     oe_runmake clean
     oe_runmake
}

do_install() {
     install -d ${D}${libdir}
     install -d ${D}${includedir}

     cd ${S}
     # libraries & header files
     oe_runmake install DESTDIR=${D}
     install -m 0644 ${S}/include/NX_GstIface.h ${D}${includedir}/NX_GstIface.h
     install -m 0644 ${S}/include/NX_GstTypes.h ${D}${includedir}/NX_GstTypes.h
}

INSANE_SKIP_${PN} = "dev-so invalid-packageconfig"
FILES_${PN} = "${libdir} ${includedir}"
FILES_${PN}-dev = "${includedir}"
FILES_SOLIBSDEV = ""
