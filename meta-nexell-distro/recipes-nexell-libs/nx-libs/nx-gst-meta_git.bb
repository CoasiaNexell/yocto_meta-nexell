DESCRIPTION = "nx-gst-meta"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=1c29f522bb1f7e5686af40aff4032b50"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://review.gerrithub.io/NexellCorp/linux_library_nx-gst-meta;protocol=https;branch=nexell"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base glib-2.0"

inherit autotools pkgconfig

#Nexell code : for /usr/include search QA issue: compile-host-path QA issue avoid
EXTRA_OEMAKE += " \
     'libnxgstmeta_la_CFLAGS=$(GST_CFLAGS) -I${STAGING_INCDIR}' \
"

do_configure() {
    cd ${S}
    NOCONFIGURE=true ./autogen.sh
	oe_runconf --enable-static
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
	oe_runmake install DESTDIR=${D}
	cp -apr ${D}/* ${BASE_WORKDIR}/extra-rootfs-support/
}

INSANE_SKIP_${PN} = "invalid-packageconfig"
FILES_${PN} = "${libdir} ${includedir}"
FILES_${PN}-dev = "${includedir}"
FILES_SOLIBSDEV = ""
