DESCRIPTION = "CoAsiaNexell Gang-Writer Application for Vapor IC"
HOMEPAGE = "http://www.coasianexell.com"
SECTION = "application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"


DEPENDS = "qtbase-native libusb "

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.6', 'nexell-sdk-qt5.6.x-env', \
			bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.8', 'nexell-sdk-qt5.8.x-env', \
			bb.utils.contains('DISTRO_FEATURES', 'nexell-qt5.9', 'nexell-sdk-qt5.9.x-env', '', d), d), d) }


PV = "1.0"
PR = "r0"

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "/home/ray/work2/vapor-gang/apps/GangWriter"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_myp() {
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack


TARGET_CC_ARCH += "${LDFLAGS}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${bindir}
	install -m 0755 ${S}/lib/* ${D}/${libdir}
	install -m 0755 ${S}/bin/* ${D}/${bindir}
}

FILES_${PN} = "${bindir} ${libdir} etc"

INSANE_SKIP_${PN} = "already-stripped"
INSANE_SKIP_${PN}-dev = "dev-elf"
