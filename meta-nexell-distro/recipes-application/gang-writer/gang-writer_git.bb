DESCRIPTION = "CoAsiaNexell Gang-Writer Application for Vapor IC"
HOMEPAGE = "http://www.coasianexell.com"
SECTION = "application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# qtdeclarative for qmake declarative macros( eg.OE_QMAKE_QMAKE )
DEPENDS = "qtbase-native qtdeclarative libusb "

require recipes-qt/qt5/qt5.inc

PV = "0.1"
PR = "r0"

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${TOPDIR}/../../../apps/GangWriter"
SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

do_myp() {
    rm -rf ${S}
    cp -a ${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_configure() {
	mkdir -p ${S}/build
	${OE_QMAKE_QMAKE} -makefile -o ${S}/build/Makefile ${S}
}

do_compile() {
    cd ${S}/build
	oe_runmake
}

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}
	install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
	install -m 0755 ${S}/lib/* ${D}/${libdir}
	install -m 0755 ${S}/bin/* ${D}/${bindir}
	install -m 0755 ${S}/script/start_gangwriter.sh ${D}/${bindir}

	# install to /lib/systemd/system/
	# symlink service to mlti-user.target.wants directory
	install -m 0644 ${S}/script/vapor_gangwriter.service ${D}${systemd_system_unitdir}/
	cd ${D}${systemd_system_unitdir}/multi-user.target.wants
	ln -sf ../vapor_gangwriter.service vapor_gangwriter.service
}

FILES_${PN} = "${bindir} ${libdir} ${sysconfdir} ${systemd_system_unitdir}"

INSANE_SKIP_${PN} = "already-stripped"
INSANE_SKIP_${PN}-dev = "dev-elf"
