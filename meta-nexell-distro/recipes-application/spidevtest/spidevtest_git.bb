SUMMARY = "Nexell userland spidev test application"
LICENSE = "CLOSED"

SRCREV = "${AUTOREV}"

PV = "1.0+EXTERNAL_SRC"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_TOP_PATH}/apps/spidevtest"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools pkgconfig

EXTRA_OECONF = "'--prefix=${STAGING_DIR_HOST}'"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
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
