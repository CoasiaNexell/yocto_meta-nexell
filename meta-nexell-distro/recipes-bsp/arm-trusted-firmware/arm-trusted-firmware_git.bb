DESCRIPTION = "ATF for s5p6818"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://license.md;md5=829bdeb34c1d9044f393d5a16c068371"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/firmware/arm-trusted-firmware"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

TARGET_CPPFLAGS = ""
TARGET_CFLAGS = ""
TARGET_CXXFLAGS = ""
TARGET_LDFLAGS = ""

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
