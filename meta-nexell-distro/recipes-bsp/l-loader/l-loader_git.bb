DESCRIPTION = "l-loader for s5p6818"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8c1458438ead3c34974bc0be3a03ed6"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/secure/l-loader"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

DEFAULT_PREFERENCE = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_compile() {
    :
}

#addtask mypatch after do_unpack before do_patch
