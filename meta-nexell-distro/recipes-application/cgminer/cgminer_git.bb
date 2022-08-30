SUMMARY = "cgminer"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "ncurses curl libusb jansson"

inherit autotools pkgconfig

PV = "1.0+EXTERNAL_SRC"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_TOP_PATH}/apps/cgminer"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OECONF = " \
	     --prefix=${STAGING_DIR_HOST} \
	     --with-system-jansson \
	     --enable-btc08 \
	     "

CFLAGS_prepend = "-I . -I ${S} -I ${S}/compat/jansson-2.9/src `pkg-config libusb-1.0 --cflags`"

do_myp() {
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_configure() {
	cd ${S}
	NOCONFIGURE=true ./autogen.sh
	oe_runconf ${EXTRA_OECONF}
}

do_compile() {
	cd ${S}
	oe_runmake clean
	oe_runmake
}

do_compile_append() {
	${CC} api-example.c -o api-example
}

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}/config

	install -m 0755 ${S}/cgminer ${D}${bindir}
	install -m 0755 ${S}/api-example ${D}${bindir}/cgminer-api
	install -m 0755 ${S}/cgminer.conf ${D}${sysconfdir}/config/

	install -m 0755 ${S}/cgminer_vtk.conf ${D}${sysconfdir}/config/
	install -m 0755 ${S}/cgminer_hash.conf ${D}${sysconfdir}/config/
	install -m 0755 ${S}/cgminer_vtk.sh ${D}${bindir}
	install -m 0755 ${S}/cgminer_hash.sh ${D}${bindir}
}

FILES_${PN} = "${bindir} ${sysconfdir}/config"
INSANE_SKIP_${PN} = "ldflags"
RDEPENDS_${PN} = "libgcc bash"
