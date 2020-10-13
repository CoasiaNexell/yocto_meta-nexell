SUMMARY = "Nexell userland spidev test application"
LICENSE = "CLOSED"

SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/linux_apps_spidevtest;protocol=https;branch=master"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools pkgconfig

EXTRA_OECONF = "'--prefix=${STAGING_DIR_HOST}'"

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

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/spidevtest ${D}${bindir}
	install -m 0755 ${S}/runbist1ch.sh ${D}${bindir}
	install -m 0755 ${S}/runjob1ch.sh ${D}${bindir}
	install -m 0755 ${S}/runbist2ch.sh ${D}${bindir}
	install -m 0755 ${S}/runjob2ch.sh ${D}${bindir}
	install -m 0755 ${S}/runbist4ch.sh ${D}${bindir}
	install -m 0755 ${S}/runjob4ch.sh ${D}${bindir}
	install -m 0755 ${S}/runspireset.sh ${D}${bindir}
}

FILES_${PN} = "${bindir}"
INSANE_SKIP_${PN} = "ldflags"
