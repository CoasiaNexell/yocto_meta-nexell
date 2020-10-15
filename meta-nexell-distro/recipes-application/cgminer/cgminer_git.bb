SUMMARY = "cgminer"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "ncurses curl libusb jansson"

inherit autotools pkgconfig

SRC_URI = "git://github.com/CoasiaNexell/linux_apps_cgminer;protocol=https;branch=btc08"

S = "${WORKDIR}/git"

SRCREV = "${AUTOREV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

EXTRA_OECONF = " \
	     --prefix=${STAGING_DIR_HOST} \
	     --with-system-jansson \
	     --enable-btc08 \
	     "

CFLAGS_prepend = "-I . -I ${S} -I ${S}/compat/jansson-2.9/src `pkg-config libusb-1.0 --cflags`"

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
	install -m 0755 ${S}/cgminer ${D}${bindir}
	install -m 0755 ${S}/api-example ${D}${bindir}/cgminer-api
}

FILES_${PN} = "${bindir}"
INSANE_SKIP_${PN} = "ldflags"