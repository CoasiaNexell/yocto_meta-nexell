inherit linux-nexell-base

DESCRIPTION = "Nexell nx_3d_svm_daemon process"
HOMEPAGE = "http://www.nexell.co.kr"
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PV = "1.0"
PR = "r0"

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/apps/svm_daemon"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_compile() {
	:
}

do_install() {
	install -d ${D}${base_sbindir}

	if [ -f ${S}/bin/nx_3d_avm_daemon ]; then
		install -m 0755 ${S}/bin/nx_3d_avm_daemon ${D}${base_sbindir}/nx_3d_avm_daemon
	fi
	if [ -f ${S}/bin/nx_3d_avm_rawimage ]; then
		install -m 0755 ${S}/bin/nx_3d_avm_rawimage ${D}${base_sbindir}/nx_3d_avm_rawimage
	fi
#	if [ -f ${S}/bin/test_capture ]; then
#		install -m 0755 ${S}/bin/test_capture ${D}${base_sbindir}/test_capture
#	fi
}

FILES_${PN} = "${base_sbindir}"

INSANE_SKIP_${PN} = "already-stripped"
