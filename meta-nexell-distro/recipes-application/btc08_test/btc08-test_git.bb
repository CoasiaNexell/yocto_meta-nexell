SUMMARY = "btc08 test application"
LICENSE = "CLOSED"

SRCREV = "${AUTOREV}"

PV = "1.0+EXTERNAL_SRC"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_TOP_PATH}/apps/btc08_test"

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
	install -d ${D}${sysconfdir}/test_scripts
	
    install -m 0755 ${S}/adc_test/adc_test ${D}${bindir}/
    install -m 0755 ${S}/btc08_test/btc08_test ${D}${bindir}/

    install -m 0755 ${S}/test_scripts/gpio.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/reset_autoaddr.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/bist.sh ${D}${sysconfdir}/test_scripts/
	install -m 0755 ${S}/test_scripts/work.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/workloop_1.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/workloop_2.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/be2le.sh ${D}${sysconfdir}/test_scripts/
    install -m 0755 ${S}/test_scripts/midstate.py ${D}${sysconfdir}/test_scripts/
}

FILES_${PN} = "${bindir} ${sysconfdir}/test_scripts"
INSANE_SKIP_${PN} = "ldflags file-rdeps"
