DESCRIPTION = "Nexell firmware update service"
HOMEPAGE = "http://www.nexell.co.kr"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PV = "1.0"
PR = "r0"

SRCREV = "${AUTOREV}"

SRC_URI = " \
	file://nx_update \
"

S = "${WORKDIR}/nx_update"

CFLAGS_remove_arm = "-g"

do_install() {
	install -d ${D}${sysconfdir}
	install -d ${D}${bindir}

	install -m 0755 ${S}${sysconfdir}/* ${D}${sysconfdir}/

	install -m 0755 ${S}/nx_update ${D}${bindir}/
}

FILES_${PN} = "${sysconfdir} ${bindir} ${systemd_system_unitdir} ${systemd_system_unitdir}/multi-user.target.wants"
RDEPENDS_${PN} = "bash psplash-nexell"
