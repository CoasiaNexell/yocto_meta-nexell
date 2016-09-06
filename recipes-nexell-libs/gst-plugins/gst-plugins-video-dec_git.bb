DESCRIPTION = "gst-plugins-video-dec"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=1c29f522bb1f7e5686af40aff4032b50"

SRCREV = "2b906437f05880dcdc1f00356a94540b586af0ea"
SRC_URI = "git://git.nexell.co.kr/nexell/linux/library/gst-plugins-video-dec;protocol=git;branch=nexell"

S = "${WORKDIR}/git"

PV = "NEXELL"
PR = "0.1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "nx-gst-meta nx-video-api"
RDEPENDS_${PN} = "libdrm"

inherit autotools pkgconfig

EXTRA_OECONF = " \
     '--prefix=${STAGING_DIR_HOST}' \
     "
    
EXTRA_OEMAKE = " \
     'libgstnxvideodec_la_CFLAGS = $(GST_CFLAGS) -I${STAGING_INCDIR}' \     
     'libgstnxvideodec_la_LDFLAGS = $(GST_PLUGIN_LDFLAGS) -L${STAGING_LIBDIR}' \
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
    cd ${S}
    install -d ${D}${libdir}
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP_${PN} = "compile-host-path dev-so debug-files"
FILES_${PN} = "${libdir} ${includedir}"
ALLOW_EMPTY_${PN} = "1"