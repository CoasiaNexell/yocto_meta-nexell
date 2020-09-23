DESCRIPTION = "nx-mediaplayer"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=2aed54cfb5560da61d68f65c345001cb"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/library/nx-mediaplayer"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "libdrm-nx ffmpeg nx-video-api nx-scaler nx-v4l2 jpeg"

inherit autotools pkgconfig

#Nexell code : for /usr/include search QA issue: compile-host-path QA issue avoid
EXTRA_OECONF = " \
    '--prefix=${STAGING_DIR_HOST}' \
    "

EXTRA_OEMAKE = " \
    'libnxfilter_la_CPPFLAGS = \
        -I${STAGING_INCDIR} \
        -I../../include \
        -I${STAGING_INCDIR}/drm \
        -I./BaseFilter \
        -I./BaseInterface \
        -I./BaseEngine' \
    'libnxfilter_la_LDFLAGS = -L${STAGING_LIBDIR} -version-info 1:0:0' \
    'libnxfilter_la_LDFLAGS += \
        -lavcodec \
        -lavformat \
        -lavutil \
        -lswresample \
        -ljpeg \
        -lasound \
        -ldrm \
        -lnx_video_api \
        -lnx_scaler \
        -lpthread'\
    'libnxfilterhelper_la_CPPFLAGS = \
        -I${STAGING_INCDIR} \
        -I../libnxfilter \
        -I${STAGING_INCDIR}/drm \
        -I../libnxfilter/BaseFilter \
        -I../libnxfilter/BaseInterface \
        -I../libnxfilter/BaseEngine' \
    'libnxfilterhelper_la_LDFLAGS = -L${STAGING_LIBDIR}  -version-info 1:0:0' \
    'libnxmpmanager_la_CPPFLAGS = \
        -I${STAGING_INCDIR} \
        -I../../include -I../libnxfilter \
        -I${STAGING_INCDIR}/drm \
        -I../libnxfilter/BaseFilter \
        -I../libnxfilter/BaseInterface \
        -I../libnxfilter/BaseEngine \
        -I../libnxfilterhelper' \
    'libnxmpmanager_la_LDFLAGS = -L${STAGING_LIBDIR} -version-info 1:0:0' \
    'libnxmpmanager_la_LDFLAGS += \
        '\
    'NxPlayerConsole_CFLAGS ='\
    'NxPlayerConsole_CPPFLAGS = \
        $(WARN_CFLAGS)	\
        -I.				\
        -I../../include	\
        -I${STAGING_INCDIR}	\
        -I${STAGING_INCDIR}/libdrm' \
    'NxPlayerConsole_LDFLAGS = -L${STAGING_LIBDIR}'\
    'NxPlayerConsole_LDFLAGS += \
        -lavcodec \
        -lavformat \
        -lavutil \
        -lswresample \
        -ldrm \
        -lnx_video_api \
        -lnx_scaler \
        -lpthread'\
    "

do_myp() {
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

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
    cd ${S}
    install -d ${D}${libdir}
    install -d ${D}${includedir}

    # header files
    install -m 0644 ${S}/include/NX_IRgbRender.h ${D}${includedir}/NX_IRgbRender.h
    install -m 0644 ${S}/include/NX_MoviePlay.h ${D}${includedir}/NX_MoviePlay.h

    install -m 0755 ${S}/src/libnxfilter/.libs/libnxfilter.so.1.0.0 ${D}${libdir}/
    install -m 0755 ${S}/src/libnxfilterhelper/.libs/libnxfilterhelper.so.1.0.0 ${D}${libdir}/
    install -m 0755 ${S}/src/libnxmpmanager/.libs/libnxmpmanager.so.1.0.0 ${D}${libdir}/

    # libraries
    cd ${D}${libdir}
    ln -sf libnxfilter.so.1.0.0 libnxfilter.so.1
    ln -sf libnxfilter.so.1 libnxfilter.so

    ln -sf libnxfilterhelper.so.1.0.0 libnxfilterhelper.so.1
    ln -sf libnxfilterhelper.so.1 libnxfilterhelper.so
	
    ln -sf libnxmpmanager.so.1.0.0 libnxmpmanager.so.1
    ln -sf libnxmpmanager.so.1 libnxmpmanager.so
}

INSANE_SKIP_${PN} = "dev-so invalid-packageconfig"
FILES_${PN} = "${libdir} ${includedir}"
FILES_${PN}-dev = "${includedir}"
FILES_SOLIBSDEV = ""
