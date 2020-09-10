DESCRIPTION = "nexell unix domain socket wrapper"

LICENSE = "CLOSED"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/library/nx-uds"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools

# Nexell code : for /usr/include search QA issue: compile-host-path QA issue avoid
EXTRA_OECONF = " \
	'--prefix=${STAGING_DIR_HOST}' \
"

EXTRA_OEMAKE += " \
	'AM_CFLAGS=$(WARN_CFLAGS) -I./include -I${STAGING_INCDIR}' \
	'libnx_uds_la_LDFLAGS = -L${STAGING_LIBDIR}' \
"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
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
	install -d ${D}${libdir}
	install -d ${D}${includedir}

	cd ${S}
	oe_runmake install DESTDIR=${D}
	cp -apr ${D}/* ${BASE_WORKDIR}/extra-rootfs-support/
}

INSANE_SKIP_${PN} = "dev-so invalid-packageconfig"
FILES_${PN} = "${libdir} ${includedir}"
FILES_${PN}-dev = "${includedir}"
